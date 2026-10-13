package act1_lexico;

import java.util.LinkedList;
import java.util.Stack;

/*
Actividad 9: Analisis semantico gestion de la tabla de simbolos y tipos de datos
Gerardo Josue Rubio Calderon
Pablo Becerra Bedoy
Argenis Adan Gutierez Hurtado
*/

public class TablaSimbolos {
    // Estructura dinámica para guardar todos los símbolos
    private LinkedList<Simbolo> tabla;
    // Pila para manejar en qué bloque o método estamos actualmente
    private Stack<String> pilaAmbitos;

    public TablaSimbolos() {
        tabla = new LinkedList<>();
        pilaAmbitos = new Stack<>();
        pilaAmbitos.push("GLOBAL"); // El ámbito por defecto al iniciar el archivo
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
        // Si el tamaño de la pila es 2 (GLOBAL -> NombreDeClase), es un Atributo
        if (pilaAmbitos.size() <= 2) {
            return "ATRIBUTO";
        }
        // Si es mayor a 2, estamos dentro de un método, es Variable Local
        return "VARIABLE";
    }

    // --- VALIDACIONES DE NOMENCLATURA (Reglas de la Actividad) ---
    public boolean esCamelCase(String id) {
        return id.matches("^[a-z]+([A-Z][a-z0-9]*)*$");
    }

    public boolean esPascalCase(String id) {
        return id.matches("^[A-Z][a-z0-9]*([A-Z][a-z0-9]*)*$");
    }

    public boolean esSnakeCaseOConstante(String id) {
        // Acepta mayúsculas con guiones bajos (MAX_VALOR) o camelCase para constantes
        return id.matches("^[A-Z]+(_[A-Z]+)*$") || esCamelCase(id);
    }

    // --- LÓGICA CENTRAL: AGREGAR Y VALIDAR ---
    public boolean agregarSimbolo(String id, String categoria, String tipoDato, String valor, int linea, int columna) {
        String ambitoActual = getAmbitoActual();

        // 1. Validar si ya existe en el MISMO ámbito (Variable duplicada)
        for (Simbolo s : tabla) {
            if (s.getId().equals(id) && s.getAmbito().equals(ambitoActual)) {
                System.err.println("Error semantico en linea " + linea + ", columna " + columna + 
                                   ": El identificador '" + id + "' ya ha sido declarado en el ambito '" + ambitoActual + "'.");
                return false;
            }
        }

        // 2. Validar Nomenclatura (Casing) según la categoría
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

        // 3. Crear e insertar en la estructura dinámica
        Simbolo nuevoSimbolo = new Simbolo(id, categoria, tipoDato, valor, ambitoActual, linea, columna, "ID");
        tabla.add(nuevoSimbolo);
        return true;
    }

    // --- BÚSQUEDA (Para saber si una variable existe antes de usarla) ---
    public boolean existeSimbolo(String id) {
        // Busca desde el ámbito actual hacia afuera
        for (Simbolo s : tabla) {
            if (s.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    // --- IMPRESIÓN DEL REPORTE FINAL ---
    public void imprimirTabla() {
        System.out.println("\n========================================================================================================================");
        System.out.println("                                                  TABLA DE SIMBOLOS");
        System.out.println("========================================================================================================================");
        System.out.printf("%-18s %-13s %-11s %-16s %-17s %-7s %-9s %-15s\n", 
                          "ID", "Categoria", "Tipo Dato", "Valor", "Ambito", "Linea", "Columna", "Token Originador");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        
        for (Simbolo s : tabla) {
            System.out.printf("%-18s %-13s %-11s %-16s %-17s %-7d %-9d %-15s\n",
                    s.getId(), s.getCategoria(), s.getTipoDato(), s.getValor(), 
                    s.getAmbito(), s.getLinea(), s.getColumna(), s.getTokenOriginador());
        }
        System.out.println("========================================================================================================================");
        System.out.println("Total de simbolos registrados: " + tabla.size());
    }
}