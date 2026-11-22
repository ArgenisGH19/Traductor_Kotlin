package act1_lexico;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Equipo 2: Kotlin - Fase de Optimizacion de Codigo Intermedio (C3D)
INTEGRANTES:
1. Becerra Bedoy, Pablo
2. Gutierrez Hurtado, Argenis Adan
3. Rubio Calderon, Gerardo Josue
*/

public class OptimizadorC3D {

    private List<String> bitacora = new ArrayList<>();

    public List<String> getBitacora() {
        return bitacora;
    }

    public List<String> optimizar(List<String> codigoOriginal) {
        bitacora.clear();
        List<String> codigo = new ArrayList<>(codigoOriginal);

        // 1. Eliminar instrucciones inalcanzables tras goto o return
        codigo = eliminarCodigoInalcanzable(codigo);

        // Ejecutamos un ciclo de punto fijo para que las pasadas se retroalimenten
        for (int iter = 0; iter < 3; iter++) {
            // 2. Eliminar calculos repetidos (Subexpresiones comunes)
            codigo = eliminarCalculosRepetidos(codigo);

            // 3. Eliminar asignaciones sobrescritas inmediatamente
            codigo = eliminarAsignacionesSobrescritas(codigo);

            // 4. Simplificacion algebraica (ej. x + 5 + 10 -> x + 15) y propagacion de copias
            codigo = simplificarYPropagarCopias(codigo);

            // 5. Eliminar variables y temporales que nunca se utilizan
            codigo = eliminarVariablesNoUtilizadas(codigo);
        }

        return codigo;
    }

    // --- PASADA 1: INSTRUCCIONES INALCANZABLES ---
    private List<String> eliminarCodigoInalcanzable(List<String> codigo) {
        List<String> resultado = new ArrayList<>();
        boolean inalcanzable = false;

        for (String linea : codigo) {
            String limpia = linea.trim();
            if (limpia.isEmpty()) continue;

            // Si encontramos una etiqueta (ej. L2:) o inicio de metodo/clase, el flujo vuelve a ser alcanzable
            if (limpia.endsWith(":") || limpia.startsWith("#")) {
                inalcanzable = false;
                resultado.add(linea);
                continue;
            }

            if (inalcanzable) {
                bitacora.add("[Codigo Inalcanzable] Se elimino '" + limpia + "' porque esta despues de un salto incondicional.");
                continue;
            }

            resultado.add(linea);

            // Si es un salto incondicional (goto Lx) o un return, lo que siga hasta la proxima etiqueta es codigo muerto
            if (limpia.startsWith("goto ") || limpia.equals("return") || limpia.startsWith("return ")) {
                inalcanzable = true;
            }
        }
        return resultado;
    }

    // --- PASADA 2: CÁLCULOS REPETIDOS (CSE) ---
    private List<String> eliminarCalculosRepetidos(List<String> codigo) {
        List<String> resultado = new ArrayList<>();
        Map<String, String> expresionesActivas = new HashMap<>(); // "a + b" -> "t1"
        Map<String, String> reemplazos = new HashMap<>();         // "t2" -> "t1"

        for (String linea : codigo) {
            String limpia = aplicarReemplazos(linea.trim(), reemplazos);

            // Al cambiar de bloque basico (etiquetas, saltos, llamadas), limpiamos el mapa local
            if (limpia.endsWith(":") || limpia.startsWith("goto ") || limpia.startsWith("if ") || limpia.startsWith("call ") || limpia.startsWith("#")) {
                expresionesActivas.clear();
                resultado.add(limpia);
                continue;
            }

            // Si una variable se lee desde teclado, su valor cambia: invalidar expresiones que la usen
            if (limpia.startsWith("read ")) {
                String varLeida = limpia.substring(5).trim();
                expresionesActivas.entrySet().removeIf(e ->
                    e.getValue().equals(varLeida) || contieneVariable(e.getKey(), varLeida)
                );
                reemplazos.remove(varLeida);
                resultado.add(limpia);
                continue;
            }

            if (esAsignacion(limpia)) {
                String[] partes = limpia.split("=", 2);
                String izq = partes[0].trim();
                String der = partes[1].trim();

                // Invalidar expresiones previas si la variable izquierda fue modificada
                expresionesActivas.entrySet().removeIf(e -> 
                    e.getValue().equals(izq) || contieneVariable(e.getKey(), izq)
                );
                reemplazos.remove(izq);

                // Si el lado derecho es una operacion binaria (ej. a + b)
                if (esOperacionBinaria(der)) {
                    if (expresionesActivas.containsKey(der)) {
                        String tempPrevio = expresionesActivas.get(der);
                        reemplazos.put(izq, tempPrevio);
                        bitacora.add("[Calculo Redundante] Se elimino '" + limpia + "' porque '" + der + "' ya fue calculado en '" + tempPrevio + "'.");
                        continue;
                    } else {
                        expresionesActivas.put(der, izq);
                    }
                }
            }
            resultado.add(limpia);
        }
        return resultado;
    }

    // --- PASADA 3: ASIGNACIONES SOBRESCRITAS INMEDIATAMENTE ---
    private List<String> eliminarAsignacionesSobrescritas(List<String> codigo) {
        List<String> resultado = new ArrayList<>(codigo);
        boolean[] eliminar = new boolean[resultado.size()];

        for (int i = 0; i < resultado.size(); i++) {
            String linActual = resultado.get(i).trim();
            if (!esAsignacion(linActual)) continue;

            String varIzq = linActual.split("=", 2)[0].trim();

            for (int j = i + 1; j < resultado.size(); j++) {
                String linSig = resultado.get(j).trim();

                // Si salimos del bloque basico, dejamos de buscar sobrescritura directa
                if (linSig.endsWith(":") || linSig.startsWith("goto ") || linSig.startsWith("if ") || linSig.startsWith("call ") || linSig.startsWith("#")) {
                    break;
                }

                // Si se sobrescribe mediante lectura desde teclado sin haberla usado antes
                if (linSig.startsWith("read ")) {
                    String varLeida = linSig.substring(5).trim();
                    if (varLeida.equals(varIzq)) {
                        eliminar[i] = true;
                        bitacora.add("[Asignacion Innecesaria] Se elimino '" + linActual + "' porque '" + varIzq + "' se sobrescribe con '" + linSig + "' sin haberse usado.");
                        break;
                    }
                    continue;
                }

                // Si la variable se lee en el lado derecho o en print/println/return antes de reasignarse, si fue util
                if (seUsaVariableEnInstruccion(linSig, varIzq)) {
                    break;
                }

                // Si se vuelve a asignar la misma variable sin haberse leido antes
                if (esAsignacion(linSig)) {
                    String sigIzq = linSig.split("=", 2)[0].trim();
                    if (sigIzq.equals(varIzq)) {
                        eliminar[i] = true;
                        bitacora.add("[Asignacion Innecesaria] Se elimino '" + linActual + "' porque '" + varIzq + "' se sobrescribe en '" + linSig + "' sin haberse usado.");
                        break;
                    }
                }
            }
        }

        List<String> depurado = new ArrayList<>();
        for (int i = 0; i < resultado.size(); i++) {
            if (!eliminar[i]) depurado.add(resultado.get(i));
        }
        return depurado;
    }

    // --- PASADA 4: SIMPLIFICACIÓN ALGEBRAICA Y PROPAGACIÓN DE COPIAS ---
    private List<String> simplificarYPropagarCopias(List<String> codigo) {
        List<String> resultado = new ArrayList<>(codigo);

        // 4A. Simplificacion algebraica en cadena: v1 = x + C1 seguido de v2 = v1 + C2 --> v2 = x + (C1+C2)
        Pattern patronSumaConst = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)\\s*([+\\-])\\s*(\\d+)$");
        Map<String, String[]> sumasConstantes = new HashMap<>();

        for (int i = 0; i < resultado.size(); i++) {
            String linea = resultado.get(i).trim();
            if (linea.endsWith(":") || linea.startsWith("goto ") || linea.startsWith("if ") || linea.startsWith("#")) {
                sumasConstantes.clear();
                continue;
            }
            if (linea.startsWith("read ")) {
                String varLeida = linea.substring(5).trim();
                sumasConstantes.entrySet().removeIf(e -> e.getKey().equals(varLeida) || e.getValue()[0].equals(varLeida));
                continue;
            }
            if (esAsignacion(linea)) {
                String[] partes = linea.split("=", 2);
                String izq = partes[0].trim();
                String der = partes[1].trim();

                // Si alguien modifica una variable base, invalidar
                sumasConstantes.entrySet().removeIf(e -> e.getKey().equals(izq) || e.getValue()[0].equals(izq));

                Matcher m = patronSumaConst.matcher(der);
                if (m.matches()) {
                    String operando = m.group(1);
                    String op = m.group(2);
                    int valor = Integer.parseInt(m.group(3));
                    int valorConSigno = op.equals("-") ? -valor : valor;

                    if (sumasConstantes.containsKey(operando)) {
                        String[] prev = sumasConstantes.get(operando);
                        String baseOriginal = prev[0];
                        int sumaTotal = Integer.parseInt(prev[1]) + valorConSigno;
                        String nuevaDer = (sumaTotal >= 0) ? (baseOriginal + " + " + sumaTotal) : (baseOriginal + " - " + Math.abs(sumaTotal));
                        String nuevaLinea = izq + " = " + nuevaDer;
                        bitacora.add("[Simplificacion Algebraica] Se simplifico '" + linea + "' (usando '" + operando + "') a '" + nuevaLinea + "'.");
                        resultado.set(i, nuevaLinea);
                        sumasConstantes.put(izq, new String[]{baseOriginal, String.valueOf(sumaTotal)});
                    } else {
                        sumasConstantes.put(izq, new String[]{operando, String.valueOf(valorConSigno)});
                    }
                }
            }
        }

        // 4B. Propagacion de copias: si v1 = expr_simple y luego v2 = v1 (y v1 no se usa mas), hacer v2 = expr_simple
        for (int i = 0; i < resultado.size() - 1; i++) {
            String lin1 = resultado.get(i).trim();
            String lin2 = resultado.get(i + 1).trim();

            if (esAsignacion(lin1) && esAsignacion(lin2)) {
                String[] p1 = lin1.split("=", 2);
                String izq1 = p1[0].trim();
                String der1 = p1[1].trim();

                String[] p2 = lin2.split("=", 2);
                String izq2 = p2[0].trim();
                String der2 = p2[1].trim();

                // Si lin2 es "izq2 = izq1" y izq1 no vuelve a usarse en todo el metodo
                if (der2.equals(izq1) && !der1.startsWith("call ") && !der1.startsWith("create ") && !seUsaDespues(resultado, izq1, i + 1)) {
                    String fusion = izq2 + " = " + der1;
                    bitacora.add("[Propagacion de Copia] Se fusiono '" + lin1 + "' y '" + lin2 + "' en '" + fusion + "'.");
                    resultado.set(i + 1, fusion);
                    resultado.remove(i);
                    i--;
                }
            }
        }

        return resultado;
    }

    // --- PASADA 5: ELIMINACIÓN DE VARIABLES NO UTILIZADAS ---
    private List<String> eliminarVariablesNoUtilizadas(List<String> codigo) {
        Set<String> variablesLeidas = new HashSet<>();

        // Si el programa tiene salidas reales (println, print, return), no necesitamos proteger nombres fijos
        boolean tieneSalida = false;
        for (String l : codigo) {
            String t = l.trim();
            if (t.startsWith("println ") || t.startsWith("print ") || t.startsWith("return ")) {
                tieneSalida = true;
                break;
            }
        }

        Set<String> variablesProtegidas = tieneSalida
            ? Collections.emptySet()
            : new HashSet<>(Arrays.asList("result", "resultado", "total", "z", "edad", "resultTexto", "sum", "count", "obj"));

        for (String linea : codigo) {
            String limpia = linea.trim();
            if (limpia.isEmpty() || limpia.startsWith("#") || limpia.endsWith(":") || limpia.startsWith("read ")) continue;

            if (esAsignacion(limpia)) {
                String der = limpia.split("=", 2)[1].trim();
                extraerIdentificadores(der, variablesLeidas);
            } else {
                // Instrucciones como if, print, println, return, param, call
                extraerIdentificadores(limpia, variablesLeidas);
            }
        }

        List<String> resultado = new ArrayList<>();
        for (String linea : codigo) {
            String limpia = linea.trim();
            if (esAsignacion(limpia)) {
                String[] partes = limpia.split("=", 2);
                String izq = partes[0].trim();
                String der = partes[1].trim();

                // No eliminamos llamadas a metodos ni creacion de objetos porque tienen efectos secundarios
                boolean tieneEfectoSecundario = der.startsWith("call ") || der.startsWith("create ") || der.equals("return_value");

                if (!variablesLeidas.contains(izq) && !variablesProtegidas.contains(izq) && !tieneEfectoSecundario) {
                    bitacora.add("[Variable No Utilizada] Se elimino '" + limpia + "' porque '" + izq + "' nunca se utiliza posteriormente.");
                    continue;
                }
            }
            resultado.add(linea);
        }
        return resultado;
    }

    // --- MÉTODOS AUXILIARES ---
    private boolean esAsignacion(String linea) {
        if (linea.startsWith("#") || linea.startsWith("if ") || linea.startsWith("goto ") ||
            linea.startsWith("call ") || linea.startsWith("param ") || linea.startsWith("print ") ||
            linea.startsWith("println ") || linea.startsWith("read ") || linea.startsWith("return") ||
            linea.endsWith(":")) {
            return false;
        }
        return linea.matches("^[a-zA-Z_][a-zA-Z0-9_]*\\s*=[^=].*$");
    }

    private boolean esOperacionBinaria(String expr) {
        return expr.matches("^[a-zA-Z0-9_\"']+\\s*(\\+|\\-|\\*|/|%|==|!=|<|>|<=|>=|&&|\\|\\|)\\s*[a-zA-Z0-9_\"']+$");
    }

    private String quitarCadenas(String texto) {
        return texto.replaceAll("\"([^\"\\\\]|\\\\.)*\"", "");
    }

    private boolean contieneVariable(String expr, String var) {
        String sinCadenas = quitarCadenas(expr);
        return Pattern.compile("\\b" + Pattern.quote(var) + "\\b").matcher(sinCadenas).find();
    }

    private boolean seUsaVariableEnInstruccion(String linea, String var) {
        if (linea.startsWith("read ")) return false;
        if (esAsignacion(linea)) {
            String der = linea.split("=", 2)[1].trim();
            return contieneVariable(der, var);
        }
        return contieneVariable(linea, var);
    }

    private boolean seUsaDespues(List<String> codigo, String var, int indiceInicio) {
        for (int k = indiceInicio + 1; k < codigo.size(); k++) {
            String lin = codigo.get(k).trim();
            if (lin.startsWith("# Definicion del metodo")) break;
            if (seUsaVariableEnInstruccion(lin, var)) return true;
            // Si estamos dentro de un ciclo (hay un goto hacia atras), por seguridad conservamos variables de usuario
            if (lin.startsWith("goto L") && !var.startsWith("t")) return true;
        }
        return false;
    }

    private String aplicarReemplazos(String linea, Map<String, String> reemplazos) {
        if (reemplazos.isEmpty()) return linea;
        // Nunca modificar el contenido de impresiones con texto literal
        if (linea.startsWith("print \"") || linea.startsWith("println \"") || linea.startsWith("read ")) {
            return linea;
        }
        if (esAsignacion(linea)) {
            String[] partes = linea.split("=", 2);
            String izq = partes[0].trim();
            String der = partes[1].trim();
            if (!der.startsWith("\"")) {
                for (Map.Entry<String, String> entry : reemplazos.entrySet()) {
                    der = der.replaceAll("\\b" + Pattern.quote(entry.getKey()) + "\\b", entry.getValue());
                }
            }
            return izq + " = " + der;
        } else {
            String mod = linea;
            for (Map.Entry<String, String> entry : reemplazos.entrySet()) {
                mod = mod.replaceAll("\\b" + Pattern.quote(entry.getKey()) + "\\b", entry.getValue());
            }
            return mod;
        }
    }

    private void extraerIdentificadores(String texto, Set<String> conjunto) {
        String sinCadenas = quitarCadenas(texto);
        Matcher m = Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b").matcher(sinCadenas);
        Set<String> reservadas = new HashSet<>(Arrays.asList(
            "if", "not", "goto", "then", "error", "call", "param", "print", "println", "read", "return", "create", "true", "false", "null", "type"
        ));
        while (m.find()) {
            String token = m.group();
            if (!reservadas.contains(token) && !token.matches("^L\\d+$")) {
                conjunto.add(token);
            }
        }
    }
}