package act1_lexico;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Equipo 2: Kotlin - Generador de Bytecode JVM (Formato Jasmin)
INTEGRANTES:
1. Becerra Bedoy, Pablo
2. Gutierrez Hurtado, Argenis Adan
3. Rubio Calderon, Gerardo Josue
*/

public class GeneradorBytecodeJasmin {

    private String nombreClase = "ProgramaCompilado";
    private Map<String, String> tiposVariables = new LinkedHashMap<>();
    private Map<String, Integer> slotsLocales = new LinkedHashMap<>();
    private int siguienteSlot = 1; // Slot 0 reservado para String[] args
    private int contadorCmp = 0;
    private boolean usaScanner = false;
    private TablaSimbolos tablaSimbolos;

    private static final Pattern PATRON_BINARIO = Pattern.compile(
        "^([a-zA-Z0-9_.\"-]+)\\s*(\\+|\\-|\\*|/|%|==|!=|<=|>=|<|>|&&|\\|\\|)\\s*([a-zA-Z0-9_.\"-]+)$"
    );

    public String getNombreClase() {
        return nombreClase;
    }

    public String generar(List<String> codigoC3D, TablaSimbolos tablaSimbolos) {
        this.tablaSimbolos = tablaSimbolos;
        this.tiposVariables.clear();
        this.slotsLocales.clear();
        this.contadorCmp = 0;
        this.usaScanner = false;

        // 1. Extraer nombre de la clase y las instrucciones del metodo principal (main o ultimo metodo)
        List<String> cuerpoMain = extraerMetodoPrincipal(codigoC3D);

        // 2. Detectar si se usa entrada por consola (Scanner)
        for (String linea : cuerpoMain) {
            if (linea.trim().startsWith("read ")) {
                usaScanner = true;
                break;
            }
        }

        // Slot 0 = args, Slot 1 = Scanner (si se requiere)
        siguienteSlot = usaScanner ? 2 : 1;

        // 3. Registrar tipos y asignar slots locales
        analizarTiposYAsignarSlots(cuerpoMain);

        // 4. Construir archivo Jasmin (.j)
        StringBuilder j = new StringBuilder();
        j.append(".class public ").append(nombreClase).append("\n");
        j.append(".super java/lang/Object\n\n");

        // Constructor por defecto
        j.append("; Constructor por defecto\n");
        j.append(".method public <init>()V\n");
        j.append("    aload_0\n");
        j.append("    invokespecial java/lang/Object/<init>()V\n");
        j.append("    return\n");
        j.append(".end method\n\n");

        // Metodo main ejecutable en la JVM
        j.append("; Punto de entrada principal\n");
        j.append(".method public static main([Ljava/lang/String;)V\n");
        j.append("    .limit stack 16\n");
        j.append("    .limit locals ").append(siguienteSlot).append("\n\n");

        // Inicializar Scanner en slot 1 si hay instrucciones de lectura
        if (usaScanner) {
            j.append("    ; Inicializacion de java.util.Scanner(System.in)\n");
            j.append("    new java/util/Scanner\n");
            j.append("    dup\n");
            j.append("    getstatic java/lang/System/in Ljava/io/InputStream;\n");
            j.append("    invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V\n");
            j.append("    astore 1\n\n");
        }

        // Pre-inicializar slots locales para garantizar 0 VerifyError en ramas condicionales
        if (!slotsLocales.isEmpty()) {
            j.append("    ; Pre-inicializacion segura de variables locales\n");
            for (Map.Entry<String, Integer> entry : slotsLocales.entrySet()) {
                String var = entry.getKey();
                int slot = entry.getValue();
                String tipo = tiposVariables.getOrDefault(var, "int");
                switch (tipo) {
                    case "float":
                        j.append("    fconst_0\n    fstore ").append(slot).append(" ; ").append(var).append("\n");
                        break;
                    case "double":
                        j.append("    dconst_0\n    dstore ").append(slot).append(" ; ").append(var).append("\n");
                        break;
                    case "String":
                        j.append("    ldc \"\"\n    astore ").append(slot).append(" ; ").append(var).append("\n");
                        break;
                    default: // int, boolean, char
                        j.append("    iconst_0\n    istore ").append(slot).append(" ; ").append(var).append("\n");
                        break;
                }
            }
            j.append("\n");
        }

        // 5. Traducir cada instruccion C3D a instrucciones JVM
        j.append("    ; Codigo traducido desde C3D\n");
        for (String raw : cuerpoMain) {
            String linea = raw.trim();
            if (linea.isEmpty()) continue;
            traducirInstruccion(linea, j);
        }

        j.append("    return\n");
        j.append(".end method\n");

        return j.toString();
    }

    public File guardarArchivoJasmin(String codigoJasmin, String carpetaSalida) throws IOException {
        File dir = new File(carpetaSalida);
        if (!dir.exists()) dir.mkdirs();
        File archivoJ = new File(dir, nombreClase + ".j");
        try (FileWriter fw = new FileWriter(archivoJ)) {
            fw.write(codigoJasmin);
        }
        return archivoJ;
    }

    // --- EXTRACCIÓN DEL MÉTODO PRINCIPAL ---
    private List<String> extraerMetodoPrincipal(List<String> codigoC3D) {
        Map<String, List<String>> metodos = new LinkedHashMap<>();
        String metodoActual = "main";

        for (String linea : codigoC3D) {
            String limpia = linea.trim();
            if (limpia.startsWith("# Definicion de la clase ")) {
                nombreClase = limpia.replace("# Definicion de la clase ", "").trim();
            } else if (limpia.startsWith("# Definicion del metodo ")) {
                metodoActual = limpia.replace("# Definicion del metodo ", "").trim();
                metodos.putIfAbsent(metodoActual, new ArrayList<>());
            } else if (!limpia.isEmpty()) {
                metodos.putIfAbsent(metodoActual, new ArrayList<>());
                metodos.get(metodoActual).add(limpia);
            }
        }

        if (metodos.containsKey("main")) {
            return metodos.get("main");
        }
        // Si no existe metodo llamado main, tomamos el ultimo metodo declarado
        List<String> ultimo = new ArrayList<>();
        for (List<String> cuerpo : metodos.values()) {
            ultimo = cuerpo;
        }
        return ultimo;
    }

    // --- ANÁLISIS DE TIPOS Y SLOTS ---
    private void analizarTiposYAsignarSlots(List<String> cuerpo) {
        for (String linea : cuerpo) {
            if (linea.startsWith("#") || linea.endsWith(":")) continue;

            if (linea.startsWith("read ")) {
                String var = linea.substring(5).trim();
                registrarVariable(var, buscarTipoEnTabla(var, "int"));
                continue;
            }

            if (esAsignacion(linea)) {
                String[] partes = linea.split("=", 2);
                String izq = partes[0].trim();
                String der = partes[1].trim();
                String tipoInferido = inferirTipoExpresion(der);
                String tipoFinal = buscarTipoEnTabla(izq, tipoInferido);
                registrarVariable(izq, tipoFinal);
            }
        }
    }

    private void registrarVariable(String nombre, String tipo) {
        if (!slotsLocales.containsKey(nombre)) {
            tiposVariables.put(nombre, tipo);
            slotsLocales.put(nombre, siguienteSlot);
            siguienteSlot += tipo.equals("double") ? 2 : 1;
        }
    }

    private String buscarTipoEnTabla(String id, String fallback) {
        if (tablaSimbolos != null) {
            Simbolo s = tablaSimbolos.obtenerSimbolo(id);
            if (s != null && !s.getTipoDato().equals("void")) {
                return s.getTipoDato();
            }
        }
        return fallback;
    }

    private String inferirTipoExpresion(String expr) {
        if (expr.startsWith("\"")) return "String";
        if (expr.equals("true") || expr.equals("false") || expr.startsWith("!")) return "boolean";

        Matcher m = PATRON_BINARIO.matcher(expr);
        if (m.matches()) {
            String op1 = m.group(1);
            String op = m.group(2);
            String op2 = m.group(3);
            if (op.matches("==|!=|<|<=|>|>=|&&|\\|\\|")) return "boolean";
            String t1 = obtenerTipoOperando(op1);
            String t2 = obtenerTipoOperando(op2);
            if (t1.equals("double") || t2.equals("double")) return "double";
            if (t1.equals("float") || t2.equals("float")) return "float";
            return "int";
        }
        return obtenerTipoOperando(expr);
    }

    private String obtenerTipoOperando(String op) {
        if (op.startsWith("\"")) return "String";
        if (op.equals("true") || op.equals("false")) return "boolean";
        if (op.matches("^-?\\d+$")) return "int";
        if (op.matches("^-?\\d+\\.\\d+[fF]$")) return "float";
        if (op.matches("^-?\\d+\\.\\d+[dD]?$")) return "double";
        if (tiposVariables.containsKey(op)) return tiposVariables.get(op);
        return buscarTipoEnTabla(op, "int");
    }

    // --- TRADUCCIÓN DE INSTRUCCIONES A JASMIN ---
    private void traducirInstruccion(String linea, StringBuilder j) {
        // Comentarios
        if (linea.startsWith("#")) {
            j.append("    ; ").append(linea.substring(1).trim()).append("\n");
            return;
        }

        // Etiquetas (L1:, L2:, ...)
        if (linea.endsWith(":")) {
            j.append(linea).append("\n");
            return;
        }

        // Salto incondicional
        if (linea.startsWith("goto ")) {
            j.append("    ").append(linea).append("\n");
            return;
        }

        // Return dentro de main
        if (linea.equals("return") || linea.startsWith("return ")) {
            j.append("    return\n");
            return;
        }

        // Guardas de error inyectadas por C3D (ej. division por cero -> la JVM lanza ArithmeticException)
        if (linea.startsWith("if ") && linea.contains(" then error ")) {
            j.append("    ; [Guardia C3D] ").append(linea).append("\n");
            return;
        }

        // Salto condicional: if not t1 goto L2
        if (linea.startsWith("if not ")) {
            String[] p = linea.substring(7).split(" goto ");
            cargarOperando(p[0].trim(), "boolean", j);
            j.append("    ifeq ").append(p[1].trim()).append("\n");
            return;
        }

        // Salto condicional de when/switch: if t4 == 1 goto L6
        if (linea.startsWith("if ") && linea.contains(" == ") && linea.contains(" goto ")) {
            String cuerpoIf = linea.substring(3);
            String[] partesGoto = cuerpoIf.split(" goto ");
            String[] comparacion = partesGoto[0].split(" == ");
            String izq = comparacion[0].trim();
            String der = comparacion[1].trim();
            String etiq = partesGoto[1].trim();
            String tipo = obtenerTipoOperando(izq);

            if (tipo.equals("String")) {
                cargarOperando(izq, "String", j);
                cargarOperando(der, "String", j);
                j.append("    invokevirtual java/lang/String/equals(Ljava/lang/Object;)Z\n");
                j.append("    ifne ").append(etiq).append("\n");
            } else {
                cargarOperando(izq, "int", j);
                cargarOperando(der, "int", j);
                j.append("    if_icmpeq ").append(etiq).append("\n");
            }
            return;
        }

        // Salto condicional simple: if t1 goto L1
        if (linea.startsWith("if ") && linea.contains(" goto ")) {
            String[] p = linea.substring(3).split(" goto ");
            cargarOperando(p[0].trim(), "boolean", j);
            j.append("    ifne ").append(p[1].trim()).append("\n");
            return;
        }

        // Impresion con salto de linea: println <op>
        if (linea.startsWith("println ")) {
            String arg = linea.substring(8).trim();
            emitirImpresion(arg, true, j);
            return;
        }

        // Impresion sin salto de linea: print <op>
        if (linea.startsWith("print ")) {
            String arg = linea.substring(6).trim();
            emitirImpresion(arg, false, j);
            return;
        }

        // Lectura desde teclado: read <var>
        if (linea.startsWith("read ")) {
            String var = linea.substring(5).trim();
            emitirLectura(var, j);
            return;
        }

        // Asignaciones y expresiones
        if (esAsignacion(linea)) {
            String[] partes = linea.split("=", 2);
            String izq = partes[0].trim();
            String der = partes[1].trim();
            String tipoDest = tiposVariables.getOrDefault(izq, "int");
            int slotDest = slotsLocales.get(izq);

            // Negacion logica (!op)
            if (der.startsWith("!")) {
                cargarOperando(der.substring(1).trim(), "boolean", j);
                j.append("    iconst_1\n");
                j.append("    ixor\n");
                almacenarEnSlot(slotDest, "boolean", j);
                return;
            }

            // Operacion binaria (aritmetica, relacional o logica)
            Matcher m = PATRON_BINARIO.matcher(der);
            if (m.matches()) {
                String op1 = m.group(1);
                String operador = m.group(2);
                String op2 = m.group(3);
                emitirOperacionBinaria(op1, operador, op2, tipoDest, slotDest, j);
                return;
            }

            // Asignacion directa (literal o copia de variable)
            cargarOperando(der, tipoDest, j);
            almacenarEnSlot(slotDest, tipoDest, j);
        }
    }

    private void emitirImpresion(String arg, boolean conSalto, StringBuilder j) {
        String tipo = obtenerTipoOperando(arg);
        String metodo = conSalto ? "println" : "print";
        String desc;
        switch (tipo) {
            case "String":  desc = "(Ljava/lang/String;)V"; break;
            case "float":   desc = "(F)V"; break;
            case "double":  desc = "(D)V"; break;
            case "boolean": desc = "(Z)V"; break;
            default:        desc = "(I)V"; break;
        }
        j.append("    getstatic java/lang/System/out Ljava/io/PrintStream;\n");
        cargarOperando(arg, tipo, j);
        j.append("    invokevirtual java/io/PrintStream/").append(metodo).append(desc).append("\n");
    }

    private void emitirLectura(String var, StringBuilder j) {
        String tipo = tiposVariables.getOrDefault(var, "int");
        int slot = slotsLocales.get(var);
        j.append("    ; Leer '").append(var).append("' (").append(tipo).append(") desde consola\n");
        j.append("    aload 1\n");
        j.append("    invokevirtual java/util/Scanner/nextLine()Ljava/lang/String;\n");

        switch (tipo) {
            case "String":
                j.append("    astore ").append(slot).append("\n");
                break;
            case "float":
                j.append("    invokevirtual java/lang/String/trim()Ljava/lang/String;\n");
                j.append("    invokestatic java/lang/Float/parseFloat(Ljava/lang/String;)F\n");
                j.append("    fstore ").append(slot).append("\n");
                break;
            case "double":
                j.append("    invokevirtual java/lang/String/trim()Ljava/lang/String;\n");
                j.append("    invokestatic java/lang/Double/parseDouble(Ljava/lang/String;)D\n");
                j.append("    dstore ").append(slot).append("\n");
                break;
            case "boolean":
                j.append("    invokevirtual java/lang/String/trim()Ljava/lang/String;\n");
                j.append("    invokestatic java/lang/Boolean/parseBoolean(Ljava/lang/String;)Z\n");
                j.append("    istore ").append(slot).append("\n");
                break;
            default: // int
                j.append("    invokevirtual java/lang/String/trim()Ljava/lang/String;\n");
                j.append("    invokestatic java/lang/Integer/parseInt(Ljava/lang/String;)I\n");
                j.append("    istore ").append(slot).append("\n");
                break;
        }
    }

    private void emitirOperacionBinaria(String op1, String operador, String op2, String tipoDest, int slotDest, StringBuilder j) {
        String t1 = obtenerTipoOperando(op1);
        String t2 = obtenerTipoOperando(op2);

        // Operadores logicos
        if (operador.equals("&&") || operador.equals("||")) {
            cargarOperando(op1, "boolean", j);
            cargarOperando(op2, "boolean", j);
            j.append(operador.equals("&&") ? "    iand\n" : "    ior\n");
            almacenarEnSlot(slotDest, "boolean", j);
            return;
        }

        // Operadores relacionales
        if (operador.matches("==|!=|<|<=|>|>=")) {
            String lTrue = "L_CMP_T_" + (++contadorCmp);
            String lEnd = "L_CMP_E_" + contadorCmp;
            String tipoComun = (t1.equals("double") || t2.equals("double")) ? "double"
                             : (t1.equals("float") || t2.equals("float")) ? "float"
                             : (t1.equals("String") || t2.equals("String")) ? "String" : "int";

            cargarOperando(op1, tipoComun, j);
            cargarOperando(op2, tipoComun, j);

            if (tipoComun.equals("String")) {
                j.append("    invokevirtual java/lang/String/equals(Ljava/lang/Object;)Z\n");
                if (operador.equals("!=")) {
                    j.append("    iconst_1\n    ixor\n");
                }
                almacenarEnSlot(slotDest, "boolean", j);
                return;
            }

            if (tipoComun.equals("double") || tipoComun.equals("float")) {
                j.append(tipoComun.equals("double") ? "    dcmpl\n" : "    fcmpl\n");
                String salto = "";
                switch (operador) {
                    case "==": salto = "ifeq"; break;
                    case "!=": salto = "ifne"; break;
                    case "<":  salto = "iflt"; break;
                    case "<=": salto = "ifle"; break;
                    case ">":  salto = "ifgt"; break;
                    case ">=": salto = "ifge"; break;
                }
                j.append("    ").append(salto).append(" ").append(lTrue).append("\n");
            } else {
                String salto = "";
                switch (operador) {
                    case "==": salto = "if_icmpeq"; break;
                    case "!=": salto = "if_icmpne"; break;
                    case "<":  salto = "if_icmplt"; break;
                    case "<=": salto = "if_icmple"; break;
                    case ">":  salto = "if_icmpgt"; break;
                    case ">=": salto = "if_icmpge"; break;
                }
                j.append("    ").append(salto).append(" ").append(lTrue).append("\n");
            }

            j.append("    iconst_0\n");
            j.append("    goto ").append(lEnd).append("\n");
            j.append(lTrue).append(":\n");
            j.append("    iconst_1\n");
            j.append(lEnd).append(":\n");
            almacenarEnSlot(slotDest, "boolean", j);
            return;
        }

        // Operadores aritmeticos (+, -, *, /, %)
        String tipoOp = (t1.equals("double") || t2.equals("double")) ? "double"
                      : (t1.equals("float") || t2.equals("float")) ? "float" : "int";

        cargarOperando(op1, tipoOp, j);
        cargarOperando(op2, tipoOp, j);

        String prefijo = tipoOp.equals("double") ? "d" : (tipoOp.equals("float") ? "f" : "i");
        switch (operador) {
            case "+": j.append("    ").append(prefijo).append("add\n"); break;
            case "-": j.append("    ").append(prefijo).append("sub\n"); break;
            case "*": j.append("    ").append(prefijo).append("mul\n"); break;
            case "/": j.append("    ").append(prefijo).append("div\n"); break;
            case "%": j.append("    ").append(prefijo).append("rem\n"); break;
        }

        // Promocion hacia el tipo destino si hace falta
        if (tipoOp.equals("int") && tipoDest.equals("float")) j.append("    i2f\n");
        else if (tipoOp.equals("int") && tipoDest.equals("double")) j.append("    i2d\n");
        else if (tipoOp.equals("float") && tipoDest.equals("double")) j.append("    f2d\n");

        almacenarEnSlot(slotDest, tipoDest, j);
    }

    private void cargarOperando(String op, String tipoEsperado, StringBuilder j) {
        // Literal String
        if (op.startsWith("\"")) {
            j.append("    ldc ").append(op).append("\n");
            return;
        }
        // Literal booleano
        if (op.equals("true")) {
            j.append("    iconst_1\n");
            return;
        }
        if (op.equals("false")) {
            j.append("    iconst_0\n");
            return;
        }
        // Literal entero
        if (op.matches("^-?\\d+$")) {
            j.append("    ldc ").append(op).append("\n");
            if (tipoEsperado.equals("float")) j.append("    i2f\n");
            else if (tipoEsperado.equals("double")) j.append("    i2d\n");
            return;
        }
        // Literal decimal
        if (op.matches("^-?\\d+\\.\\d+[fFdD]?$")) {
            String limpio = op.replaceAll("[fFdD]$", "");
            if (tipoEsperado.equals("double")) {
                j.append("    ldc2_w ").append(limpio).append("\n");
            } else {
                j.append("    ldc ").append(limpio).append("\n");
            }
            return;
        }

        // Variable local o temporal
        if (!slotsLocales.containsKey(op)) {
            throw new IllegalStateException("Variable o temporal no registrado en slots locales: " + op);
        }
        int slot = slotsLocales.get(op);
        String tipoReal = tiposVariables.getOrDefault(op, "int");
        switch (tipoReal) {
            case "float":
                j.append("    fload ").append(slot).append("\n");
                if (tipoEsperado.equals("double")) j.append("    f2d\n");
                break;
            case "double":
                j.append("    dload ").append(slot).append("\n");
                break;
            case "String":
                j.append("    aload ").append(slot).append("\n");
                break;
            default:
                j.append("    iload ").append(slot).append("\n");
                if (tipoEsperado.equals("float")) j.append("    i2f\n");
                else if (tipoEsperado.equals("double")) j.append("    i2d\n");
                break;
        }
    }

    private void almacenarEnSlot(int slot, String tipo, StringBuilder j) {
        switch (tipo) {
            case "float":  j.append("    fstore ").append(slot).append("\n"); break;
            case "double": j.append("    dstore ").append(slot).append("\n"); break;
            case "String": j.append("    astore ").append(slot).append("\n"); break;
            default:       j.append("    istore ").append(slot).append("\n"); break;
        }
    }

    private boolean esAsignacion(String linea) {
        if (linea.startsWith("#") || linea.startsWith("if ") || linea.startsWith("goto ") ||
            linea.startsWith("call ") || linea.startsWith("param ") || linea.startsWith("print ") ||
            linea.startsWith("println ") || linea.startsWith("read ") || linea.startsWith("return") ||
            linea.endsWith(":")) {
            return false;
        }
        return linea.matches("^[a-zA-Z_][a-zA-Z0-9_]*\\s*=[^=].*$");
    }
}