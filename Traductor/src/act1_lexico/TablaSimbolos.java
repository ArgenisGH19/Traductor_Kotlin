package act1_lexico;

import java.util.LinkedList;
import java.util.Stack;

/*
Actividad 9 y 11: Analisis semantico gestion de la tabla de simbolos, tipos de datos y control
Gerardo Josue Rubio Calderon
Pablo Becerra Bedoy
Argenis Adan Gutierez Hurtado
*/

public class TablaSimbolos {
    private LinkedList<Simbolo> tabla;
    private Stack<String> pilaAmbitos;
    
    // Radares de Control
    private int ciclosAnidados = 0;
    private String tipoRetornoActual = "void"; 

    // Contador base de memoria para el segmento .data (avanza de 4 en 4 bytes)
    private int direccionBaseMemoria = 0x2000;

    public TablaSimbolos() {
        tabla = new LinkedList<>();
        pilaAmbitos = new Stack<>();
        pilaAmbitos.push("GLOBAL");
    }

    // --- MANEJO DE ÁMBITOS ---
    public void entrarAmbito(String nuevoAmbito) {
        pilaAmbitos.push(nuevoAmbito);
    }

    public void salirAmbito() {
        if (pilaAmbitos.size() > 1) {
            pilaAmbitos.pop();
        }
    }

    public String getAmbitoActual() {
        return pilaAmbitos.peek();
    }
    
    public String determinarCategoriaVariable() {
        if (pilaAmbitos.size() <= 2) {
            return "ATRIBUTO";
        }
        return "VARIABLE";
    }

    // --- RADAR DE CICLOS Y RETORNOS ---
    public void entrarCiclo() { ciclosAnidados++; }
    
    public void salirCiclo() { if(ciclosAnidados > 0) ciclosAnidados--; }
    
    public boolean estoyEnUnCiclo() { return ciclosAnidados > 0; }
    
    public void setTipoRetornoActual(String tipo) { this.tipoRetornoActual = tipo; }
    
    public String getTipoRetornoActual() { return this.tipoRetornoActual; }

    public void registrarParametroEnMetodoActual(String tipoDato) {
        String nombreMetodo = getAmbitoActual();
        Simbolo metodo = obtenerSimbolo(nombreMetodo);
        if (metodo != null && metodo.getCategoria().equals("METODO")) {
            metodo.agregarTipoParametro(tipoDato);
        }
    }

    // --- VALIDACIONES DE NOMENCLATURA ---
    public boolean esCamelCase(String id) {
        return id.matches("^[a-z]+([A-Z][a-z0-9]*)*$");
    }

    public boolean esPascalCase(String id) {
        return id.matches("^[A-Z][a-z0-9]*([A-Z][a-z0-9]*)*$");
    }

    public boolean esSnakeCaseOConstante(String id) {
        return id.matches("^[A-Z]+(_[A-Z]+)*$") || esCamelCase(id);
    }

    // --- LÓGICA CENTRAL: AGREGAR Y VALIDAR ---
    public boolean agregarSimbolo(String id, String categoria, String tipoDato, String valor, int linea, int columna) {
        String ambitoActual = getAmbitoActual();

        for (Simbolo s : tabla) {
            if (s.getId().equals(id) && s.getAmbito().equals(ambitoActual)) {
                System.err.println("Error semantico en linea " + linea + ", columna " + columna + 
                                   ": El identificador '" + id + "' ya ha sido declarado en el ambito '" + ambitoActual + "'.");
                return false;
            }
        }

        if (categoria.equals("CLASE") && !esPascalCase(id)) {
            System.err.println("Error lexico/semantico en linea " + linea + ", columna " + columna + 
                               ": La clase '" + id + "' debe usar la convencion PascalCase.");
        } else if ((categoria.equals("VARIABLE") || categoria.equals("ATRIBUTO") || categoria.equals("METODO")) && !esCamelCase(id)) {
            System.err.println("Error lexico/semantico en linea " + linea + ", columna " + columna + 
                               ": El identificador '" + id + "' debe utilizar la convencion camelCase.");
        } else if (categoria.equals("CONSTANTE") && !esSnakeCaseOConstante(id)) {
            System.err.println("Error lexico/semantico en linea " + linea + ", columna " + columna + 
                               ": La constante '" + id + "' debe usar mayusculas (SNAKE_CASE) o camelCase.");
        }

        Simbolo nuevoSimbolo = new Simbolo(id, categoria, tipoDato, valor, ambitoActual, linea, columna, "ID");

        // Asignación automática de dirección de memoria para variables, atributos y constantes
        if (categoria.equals("VARIABLE") || categoria.equals("ATRIBUTO") || categoria.equals("CONSTANTE")) {
            nuevoSimbolo.setDireccionMemoria(String.format("0x%04X", direccionBaseMemoria));
            direccionBaseMemoria += 4;
        }

        tabla.add(nuevoSimbolo);
        return true;
    }

    // --- BÚSQUEDA ---
    public boolean existeSimbolo(String id) {
        for (Simbolo s : tabla) {
            if (s.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public Simbolo obtenerSimbolo(String id) {
        for (int i = tabla.size() - 1; i >= 0; i--) {
            Simbolo s = tabla.get(i);
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null; 
    }

    public LinkedList<Simbolo> getTabla() {
        return tabla;
    }

    // --- IMPRESIÓN DEL REPORTE FINAL ---
    public void imprimirTabla() {
        System.out.println("\n=====================================================================================================================================");
        System.out.println("                                                        TABLA DE SIMBOLOS");
        System.out.println("=====================================================================================================================================");
        System.out.printf("%-18s %-13s %-11s %-16s %-17s %-12s %-7s %-9s %-15s\n", 
                          "ID", "Categoria", "Tipo Dato", "Valor", "Ambito", "Direccion", "Linea", "Columna", "Token Originador");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------");
        
        for (Simbolo s : tabla) {
            System.out.printf("%-18s %-13s %-11s %-16s %-17s %-12s %-7d %-9d %-15s\n",
                    s.getId(), s.getCategoria(), s.getTipoDato(), s.getValor(), 
                    s.getAmbito(), s.getDireccionMemoria(), s.getLinea(), s.getColumna(), s.getTokenOriginador());
        }
        System.out.println("=====================================================================================================================================");
        System.out.println("Total de simbolos registrados: " + tabla.size());
    }

    // --- IMPRESIÓN DE TABLA DE DIRECCIONES DE MEMORIA (PUNTO 2.2) ---
    public void imprimirTablaDirecciones() {
        System.out.println("\n====================================================");
        System.out.println("     TABLA DE SIMBOLOS (ASIGNACION DE MEMORIA)");
        System.out.println("====================================================");
        System.out.printf("| %-18s | %-12s | %-12s |\n", "Simbolo", "Categoria", "Direccion");
        System.out.println("|--------------------|--------------|--------------|");
        for (Simbolo s : tabla) {
            if (!s.getDireccionMemoria().equals("N/A")) {
                System.out.printf("| %-18s | %-12s | %-12s |\n",
                        s.getId(), s.getCategoria(), s.getDireccionMemoria());
            }
        }
        System.out.println("====================================================\n");
    }
}