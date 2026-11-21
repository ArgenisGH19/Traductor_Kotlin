package act1_lexico;

import java.util.*;
import java.util.regex.*;

public class GeneradorCodigoMaquina {
    private TablaSimbolos tablaSimbolos;
    private List<String> codigoIntermedio;
    private List<String> codigoOptimizado = new ArrayList<>();
    private List<String> codigoMaquinaSimbolico = new ArrayList<>();
    private List<String> codigoMaquinaDirecciones = new ArrayList<>();
    private Map<String, String> mapaEtiquetas = new LinkedHashMap<>();

    public GeneradorCodigoMaquina(TablaSimbolos tablaSimbolos, List<String> codigoIntermedio) {
        this.tablaSimbolos = tablaSimbolos;
        this.codigoIntermedio = codigoIntermedio;
    }

    public void compilar() {
        optimizarCodigoIntermedio();
        traducirAInstruccionesMaquina();
        asignarDireccionesMemoria();
    }

    // --- PUNTO 1.2: OPTIMIZACIÓN DE INSTRUCCIONES ---
    private void optimizarCodigoIntermedio() {
        Map<String, String> propagacion = new HashMap<>();

        for (String linea : codigoIntermedio) {
            String inst = linea.trim();
            if (inst.isEmpty() || inst.startsWith("#")) continue;
            if (inst.contains("then error")) continue;

            // 1. Plegado de constantes (Constant Folding): t1 = 4 + 5 -> t1 = 9
            Matcher mArit = Pattern.compile("^(t\\d+)\\s*=\\s*(-?\\d+)\\s*([+\\-*/])\\s*(-?\\d+)$").matcher(inst);
            if (mArit.matches()) {
                String dest = mArit.group(1);
                int a = Integer.parseInt(mArit.group(2));
                String op = mArit.group(3);
                int b = Integer.parseInt(mArit.group(4));
                int res = 0;
                switch (op) {
                    case "+": res = a + b; break;
                    case "-": res = a - b; break;
                    case "*": res = a * b; break;
                    case "/": res = (b != 0) ? (a / b) : 0; break;
                }
                propagacion.put(dest, String.valueOf(res));
                codigoOptimizado.add(dest + " = " + res + "   # [Opt: Plegado de constantes]");
                continue;
            }

            // 2. Eliminación de copias temporales redundantes
            Matcher mCopia = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(t\\d+)$").matcher(inst);
            if (mCopia.matches() && propagacion.containsKey(mCopia.group(2))) {
                String varDest = mCopia.group(1);
                String valReal = propagacion.get(mCopia.group(2));
                codigoOptimizado.add(varDest + " = " + valReal + "   # [Opt: Propagacion de constante]");
                continue;
            }

            codigoOptimizado.add(inst);
        }
    }

    // --- PUNTO 1.1: MAPEO DE CÓDIGO INTERMEDIO A INSTRUCCIONES DE MÁQUINA ---
    private void traducirAInstruccionesMaquina() {
        String registroActual = "";
        Map<String, String> comparacionesTemp = new HashMap<>();

        for (String linea : codigoOptimizado) {
            String inst = linea.split("#")[0].trim();

            // Etiquetas (ej. L1:)
            if (inst.endsWith(":")) {
                codigoMaquinaSimbolico.add(inst);
                registroActual = "";
                continue;
            }

            // Retorno de método: return t1
            if (inst.startsWith("return ")) {
                String val = inst.substring(7).trim();
                if (comparacionesTemp.containsKey(val)) {
                    String[] p = comparacionesTemp.get(val).split("\\s+");
                    codigoMaquinaSimbolico.add("LOAD R1, " + p[0]);
                    codigoMaquinaSimbolico.add("CMP R1, " + p[2]);
                    codigoMaquinaSimbolico.add(obtenerSetCondicional(p[1]) + " R1");
                } else if (!val.equals(registroActual)) {
                    codigoMaquinaSimbolico.add("LOAD R1, " + val);
                }
                codigoMaquinaSimbolico.add("RET");
                registroActual = "";
                continue;
            }

            // Llamada a método: call L1
            if (inst.startsWith("call ")) {
                String etiq = inst.substring(5).trim();
                codigoMaquinaSimbolico.add("CALL " + etiq);
                registroActual = "return_value";
                continue;
            }

            // Salto incondicional: goto L1
            if (inst.startsWith("goto ")) {
                String etiq = inst.substring(5).trim();
                codigoMaquinaSimbolico.add("JMP " + etiq);
                continue;
            }

            // Salto condicional negado: if not cond goto L1
            if (inst.startsWith("if not ")) {
                String resto = inst.substring(7).trim();
                String[] partes = resto.split("\\s+goto\\s+");
                String cond = partes[0].trim();
                String etiq = partes[1].trim();
                if (comparacionesTemp.containsKey(cond)) {
                    traducirCondicion(comparacionesTemp.get(cond), etiq, true);
                } else {
                    traducirCondicion(cond, etiq, true);
                }
                registroActual = "";
                continue;
            }

            // Salto condicional directo: if cond goto L1
            if (inst.startsWith("if ")) {
                String resto = inst.substring(3).trim();
                String[] partes = resto.split("\\s+goto\\s+");
                String cond = partes[0].trim();
                String etiq = partes[1].trim();
                if (comparacionesTemp.containsKey(cond)) {
                    traducirCondicion(comparacionesTemp.get(cond), etiq, false);
                } else {
                    traducirCondicion(cond, etiq, false);
                }
                registroActual = "";
                continue;
            }

            // Entrada / Salida (LEER / ESCRIBIR)
            if (inst.toLowerCase().startsWith("read ") || inst.toLowerCase().startsWith("leer ")) {
                String var = inst.split("\\s+")[1].trim();
                codigoMaquinaSimbolico.add("IN R1");
                codigoMaquinaSimbolico.add("STORE " + var + ", R1");
                registroActual = var;
                continue;
            }
            if (inst.toLowerCase().startsWith("write ") || inst.toLowerCase().startsWith("escribir ") || inst.toLowerCase().startsWith("print ")) {
                String elem = inst.substring(inst.indexOf(' ') + 1).trim();
                if (!elem.equals(registroActual)) {
                    codigoMaquinaSimbolico.add("LOAD R1, " + elem);
                }
                codigoMaquinaSimbolico.add("OUT R1");
                continue;
            }

            // Operación relacional guardada en temporal: t4 = iteracion == 5
            Matcher mRel = Pattern.compile("^(t\\d+)\\s*=\\s*(.+?)\\s*(==|!=|<=|>=|<|>)\\s*(.+)$").matcher(inst);
            if (mRel.matches()) {
                String temp = mRel.group(1).trim();
                String exprRel = mRel.group(2).trim() + " " + mRel.group(3).trim() + " " + mRel.group(4).trim();
                comparacionesTemp.put(temp, exprRel);
                continue;
            }

            // Operación aritmética binaria: dest = op1 (+|-|*|/) op2
            Matcher mBin = Pattern.compile("^([a-zA-Z0-9_]+)\\s*=\\s*([^+\\-*/]+)\\s*([+\\-*/])\\s*(.+)$").matcher(inst);
            if (mBin.matches()) {
                String dest = mBin.group(1).trim();
                String op1 = mBin.group(2).trim();
                String operador = mBin.group(3).trim();
                String op2 = mBin.group(4).trim();

                if (!op1.equals(registroActual)) {
                    codigoMaquinaSimbolico.add("LOAD R1, " + op1);
                }

                String mnemonico = obtenerMnemonico(operador);
                codigoMaquinaSimbolico.add(mnemonico + " R1, " + op2);

                if (!dest.matches("^t\\d+$")) {
                    codigoMaquinaSimbolico.add("STORE " + dest + ", R1");
                }
                registroActual = dest;
                continue;
            }

            // Asignación simple: dest = fuente
            Matcher mAsig = Pattern.compile("^([a-zA-Z0-9_]+)\\s*=\\s*(.+)$").matcher(inst);
            if (mAsig.matches()) {
                String dest = mAsig.group(1).trim();
                String fuente = mAsig.group(2).trim();
                if (fuente.equals("true")) fuente = "1";
                if (fuente.equals("false")) fuente = "0";

                if (!fuente.equals(registroActual) || dest.matches("^t\\d+$")) {
                    codigoMaquinaSimbolico.add("LOAD R1, " + fuente);
                }
                if (!dest.matches("^t\\d+$")) {
                    codigoMaquinaSimbolico.add("STORE " + dest + ", R1");
                }
                registroActual = dest;
            }
        }
        codigoMaquinaSimbolico.add("HALT");
    }

    private void traducirCondicion(String cond, String etiq, boolean negado) {
        Matcher m = Pattern.compile("^(.+?)\\s*(==|!=|<=|>=|<|>)\\s*(.+)$").matcher(cond);
        if (m.matches()) {
            String op1 = m.group(1).trim();
            String rel = m.group(2).trim();
            String op2 = m.group(3).trim();
            codigoMaquinaSimbolico.add("LOAD R1, " + op1);
            codigoMaquinaSimbolico.add("CMP R1, " + op2);
            String salto = negado ? saltoInverso(rel) : saltoDirecto(rel);
            codigoMaquinaSimbolico.add(salto + " " + etiq);
        } else {
            codigoMaquinaSimbolico.add("LOAD R1, " + cond);
            codigoMaquinaSimbolico.add("CMP R1, 0");
            codigoMaquinaSimbolico.add((negado ? "JE " : "JNE ") + etiq);
        }
    }

    private String obtenerMnemonico(String op) {
        switch (op) {
            case "+": return "ADD";
            case "-": return "SUB";
            case "*": return "MUL";
            case "/": return "DIV";
            default:  return "CMP";
        }
    }

    private String obtenerSetCondicional(String rel) {
        switch (rel) {
            case "==": return "SETE";
            case "!=": return "SETNE";
            case "<":  return "SETL";
            case "<=": return "SETLE";
            case ">":  return "SETG";
            case ">=": return "SETGE";
            default:   return "SETG";
        }
    }

    private String saltoDirecto(String rel) {
        switch (rel) {
            case "==": return "JE";
            case "!=": return "JNE";
            case "<":  return "JL";
            case "<=": return "JLE";
            case ">":  return "JG";
            case ">=": return "JGE";
            default:   return "JMP";
        }
    }

    private String saltoInverso(String rel) {
        switch (rel) {
            case "==": return "JNE";
            case "!=": return "JE";
            case "<":  return "JGE";
            case "<=": return "JG";
            case ">":  return "JLE";
            case ">=": return "JL";
            default:   return "JMP";
        }
    }

    // --- PUNTO 2.1: ASIGNACIÓN DE DIRECCIONES DE MEMORIA ---
    private void asignarDireccionesMemoria() {
        int direccionCodigo = 0x1000;

        for (String inst : codigoMaquinaSimbolico) {
            if (inst.endsWith(":")) {
                String nombreEtiq = inst.substring(0, inst.length() - 1);
                mapaEtiquetas.put(nombreEtiq, String.format("0x%04X", direccionCodigo));
            } else {
                direccionCodigo += 4;
            }
        }

        direccionCodigo = 0x1000;
        for (String inst : codigoMaquinaSimbolico) {
            if (inst.endsWith(":")) continue;

            // Si la instrucción contiene una cadena literal "...", no reemplazar nada dentro de las comillas
            if (inst.contains("\"")) {
                codigoMaquinaDirecciones.add(String.format("0x%04X: %s", direccionCodigo, inst));
                direccionCodigo += 4;
                continue;
            }

            String instResuelta = inst;

            for (Map.Entry<String, String> entry : mapaEtiquetas.entrySet()) {
                instResuelta = instResuelta.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
            }

            for (Simbolo s : tablaSimbolos.getTabla()) {
                if (!s.getDireccionMemoria().equals("N/A")) {
                    instResuelta = instResuelta.replaceAll("\\b" + s.getId() + "\\b", s.getDireccionMemoria());
                }
            }

            codigoMaquinaDirecciones.add(String.format("0x%04X: %s", direccionCodigo, instResuelta));
            direccionCodigo += 4;
        }
    }

    public void imprimirResultados() {
        System.out.println("\n========================================================");
        System.out.println("   1. CODIGO INTERMEDIO OPTIMIZADO");
        System.out.println("========================================================");
        for (String s : codigoOptimizado) System.out.println("  " + s);

        System.out.println("\n========================================================");
        System.out.println("   2. CODIGO MAQUINA (ENSAMBLADOR SIMBOLICO)");
        System.out.println("========================================================");
        for (String s : codigoMaquinaSimbolico) System.out.println("  " + s);

        System.out.println("\n========================================================");
        System.out.println("   3. CODIGO MAQUINA CON DIRECCIONES DE MEMORIA");
        System.out.println("========================================================");
        for (String s : codigoMaquinaDirecciones) System.out.println("  " + s);
    }

    public List<String> getCodigoMaquinaDirecciones() {
        return codigoMaquinaDirecciones;
    }

    public Map<String, String> getMapaEtiquetas() {
        return mapaEtiquetas;
    }
}