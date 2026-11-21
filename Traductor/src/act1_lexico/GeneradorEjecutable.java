package act1_lexico;

import java.io.*;
import java.util.*;

public class GeneradorEjecutable {
    private TablaSimbolos tablaSimbolos;
    private List<String> codigoMaquinaDirecciones;
    private Map<String, String> mapaEtiquetas;
    private String rutaEjecutable;

    public GeneradorEjecutable(TablaSimbolos tablaSimbolos, List<String> codigoMaquinaDirecciones, Map<String, String> mapaEtiquetas, String rutaEjecutable) {
        this.tablaSimbolos = tablaSimbolos;
        this.codigoMaquinaDirecciones = codigoMaquinaDirecciones;
        this.mapaEtiquetas = mapaEtiquetas;
        this.rutaEjecutable = rutaEjecutable;
    }

    private String obtenerDireccionInicio() {
        // Buscar el método principal (void sin parámetros, ej. proceso o main)
        for (Simbolo s : tablaSimbolos.getTabla()) {
            if (s.getCategoria().equals("METODO") && s.getTiposParametros().isEmpty() && !s.getEtiquetaC3D().isEmpty()) {
                String dir = mapaEtiquetas.get(s.getEtiquetaC3D());
                if (dir != null) return dir;
            }
        }
        return "0x1000";
    }

    // --- PUNTO 3.1: CREACIÓN DEL ARCHIVO EJECUTABLE ---
    public File crearArchivoEjecutable() throws IOException {
        File archivo = new File(rutaEjecutable);
        String entryPoint = obtenerDireccionInicio();

        int tamData = 0;
        for (Simbolo s : tablaSimbolos.getTabla()) {
            if (!s.getDireccionMemoria().equals("N/A")) tamData += 4;
        }
        int tamText = codigoMaquinaDirecciones.size() * 4;

        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            pw.println("MAGIC: 0x7F454C46");
            pw.println("ARCH: CPU-32BIT-KOTLIN-TARGET");
            pw.println("ENTRY_POINT: " + entryPoint);
            pw.println("DATA_SIZE: " + tamData + " bytes");
            pw.println("TEXT_SIZE: " + tamText + " bytes");
            pw.println("----------------------------------------");

            pw.println(".DATA");
            for (Simbolo s : tablaSimbolos.getTabla()) {
                if (!s.getDireccionMemoria().equals("N/A")) {
                    String valInicial = s.getValor().equals("null") || s.getValor().startsWith("N/A") || s.getValor().equals("Asignado") ? "0" : s.getValor();
                    pw.println(s.getDireccionMemoria() + ": " + valInicial + " # " + s.getId());
                }
            }
            pw.println("----------------------------------------");

            pw.println(".TEXT");
            for (String inst : codigoMaquinaDirecciones) {
                pw.println(inst);
            }
        }

        System.out.println("\n========================================================");
        System.out.println("   4. ARCHIVO EJECUTABLE GENERADO");
        System.out.println("========================================================");
        System.out.println("Archivo creado en: " + archivo.getAbsolutePath());
        System.out.println("Tamano .DATA: " + tamData + " bytes | Tamano .TEXT: " + tamText + " bytes | Entry Point: " + entryPoint);
        return archivo;
    }

    // --- PUNTO 3.2: CONFIGURACIÓN DEL ENTORNO Y EJECUCIÓN ---
    public void configurarYEjecutar(File ejecutable) {
        System.out.println("\n========================================================");
        System.out.println("   5. CONFIGURACION DE ENTORNO Y EJECUCION");
        System.out.println("========================================================");

        if (!ejecutable.exists()) {
            System.err.println("Error: No se encuentra el archivo ejecutable.");
            return;
        }
        boolean permisoOk = ejecutable.setExecutable(true);
        System.out.println("[Entorno] Permisos de ejecucion configurados: " + (permisoOk ? "OK (+x)" : "Solo lectura"));
        System.out.println("[Entorno] Cargando segmentos .DATA y .TEXT en memoria...");

        Map<String, Double> memoria = new HashMap<>();
        Map<Integer, String> instrucciones = new TreeMap<>();

        for (Simbolo s : tablaSimbolos.getTabla()) {
            if (!s.getDireccionMemoria().equals("N/A")) {
                double valorNum = 0.0;
                try {
                    valorNum = Double.parseDouble(s.getValor());
                } catch (Exception ignored) {}
                memoria.put(s.getDireccionMemoria(), valorNum);
            }
        }

        for (String linea : codigoMaquinaDirecciones) {
            String[] partes = linea.split(":\\s*", 2);
            int dir = Integer.parseInt(partes[0].replace("0x", ""), 16);
            instrucciones.put(dir, partes[1].trim());
        }

        String entryPointHex = obtenerDireccionInicio();
        int pc = Integer.parseInt(entryPointHex.replace("0x", ""), 16);

        double R1 = 0.0;
        String R1Texto = null;
        Stack<Double> pilaArgs = new Stack<>();
        Stack<Integer> pilaRetorno = new Stack<>();
        int flagCmp = 0;
        int maxCiclos = 500;
        int ciclos = 0;

        System.out.println("[CPU] Iniciando ejecucion desde Entry Point " + entryPointHex + "...\n");

        while (instrucciones.containsKey(pc) && ciclos < maxCiclos) {
            ciclos++;
            String inst = instrucciones.get(pc);
            if (inst.equals("HALT")) break;

            boolean saltoRealizado = false;

            if (inst.startsWith("LOAD R1, \"")) {
                R1Texto = inst.substring(inst.indexOf('"') + 1, inst.lastIndexOf('"'));
            } else {
                String[] tokens = inst.split("[\\s,]+");
                String op = tokens[0];

                switch (op) {
                    case "LOAD":
                        R1Texto = null;
                        if (tokens[2].equals("return_value")) {
                            // R1 conserva el valor retornado por la función llamada
                        } else {
                            R1 = obtenerValorOperando(tokens[2], memoria);
                            pilaArgs.push(R1);
                        }
                        break;
                    case "STORE":
                        memoria.put(tokens[1], R1);
                        pilaArgs.clear();
                        break;
                    case "ADD":
                        R1 += obtenerValorOperando(tokens[2], memoria);
                        break;
                    case "SUB":
                        R1 -= obtenerValorOperando(tokens[2], memoria);
                        break;
                    case "MUL":
                        R1 *= obtenerValorOperando(tokens[2], memoria);
                        break;
                    case "DIV":
                        double div = obtenerValorOperando(tokens[2], memoria);
                        if (div != 0) R1 /= div;
                        break;
                    case "CMP":
                        double valCmp = obtenerValorOperando(tokens[2], memoria);
                        flagCmp = Double.compare(R1, valCmp);
                        break;
                    case "SETE":
                        R1 = (flagCmp == 0) ? 1.0 : 0.0;
                        break;
                    case "SETNE":
                        R1 = (flagCmp != 0) ? 1.0 : 0.0;
                        break;
                    case "SETL":
                        R1 = (flagCmp < 0) ? 1.0 : 0.0;
                        break;
                    case "SETLE":
                        R1 = (flagCmp <= 0) ? 1.0 : 0.0;
                        break;
                    case "SETG":
                        R1 = (flagCmp > 0) ? 1.0 : 0.0;
                        break;
                    case "SETGE":
                        R1 = (flagCmp >= 0) ? 1.0 : 0.0;
                        break;
                    case "JMP":
                        pc = Integer.parseInt(tokens[1].replace("0x", ""), 16);
                        saltoRealizado = true;
                        break;
                    case "JE":
                        if (flagCmp == 0) { pc = Integer.parseInt(tokens[1].replace("0x", ""), 16); saltoRealizado = true; }
                        break;
                    case "JNE":
                        if (flagCmp != 0) { pc = Integer.parseInt(tokens[1].replace("0x", ""), 16); saltoRealizado = true; }
                        break;
                    case "JL":
                        if (flagCmp < 0) { pc = Integer.parseInt(tokens[1].replace("0x", ""), 16); saltoRealizado = true; }
                        break;
                    case "JLE":
                        if (flagCmp <= 0) { pc = Integer.parseInt(tokens[1].replace("0x", ""), 16); saltoRealizado = true; }
                        break;
                    case "JG":
                        if (flagCmp > 0) { pc = Integer.parseInt(tokens[1].replace("0x", ""), 16); saltoRealizado = true; }
                        break;
                    case "JGE":
                        if (flagCmp >= 0) { pc = Integer.parseInt(tokens[1].replace("0x", ""), 16); saltoRealizado = true; }
                        break;
                    case "CALL":
                        pilaRetorno.push(pc + 4);
                        int dirDestino = Integer.parseInt(tokens[1].replace("0x", ""), 16);
                        cargarParametrosEnMemoria(dirDestino, pilaArgs, memoria);
                        pc = dirDestino;
                        saltoRealizado = true;
                        break;
                    case "RET":
                        if (!pilaRetorno.isEmpty()) {
                            pc = pilaRetorno.pop();
                            saltoRealizado = true;
                        }
                        break;
                    case "OUT":
                        if (R1Texto != null) {
                            System.out.println("  >> [SALIDA DEL PROGRAMA]: " + R1Texto);
                        } else {
                            System.out.println("  >> [SALIDA DEL PROGRAMA]: " + R1);
                        }
                        break;
                    case "IN":
                        R1 = 1.0;
                        break;
                }
            }

            if (!saltoRealizado) {
                pc += 4;
            }
        }

        System.out.println("\n[CPU] Ejecucion finalizada (" + ciclos + " instrucciones ejecutadas).");
        System.out.println("[CPU] Estado final de memoria (.DATA):");
        for (Simbolo s : tablaSimbolos.getTabla()) {
            if (!s.getDireccionMemoria().equals("N/A")) {
                System.out.println("  " + s.getDireccionMemoria() + " (" + s.getId() + ") = " + memoria.get(s.getDireccionMemoria()));
            }
        }
    }

    private void cargarParametrosEnMemoria(int dirMetodo, Stack<Double> pilaArgs, Map<String, Double> memoria) {
        String etiqMetodo = "";
        for (Map.Entry<String, String> entry : mapaEtiquetas.entrySet()) {
            if (Integer.parseInt(entry.getValue().replace("0x", ""), 16) == dirMetodo) {
                etiqMetodo = entry.getKey();
                break;
            }
        }
        String nombreMetodo = "";
        for (Simbolo s : tablaSimbolos.getTabla()) {
            if (s.getCategoria().equals("METODO") && s.getEtiquetaC3D().equals(etiqMetodo)) {
                nombreMetodo = s.getId();
                break;
            }
        }
        List<Simbolo> params = new ArrayList<>();
        for (Simbolo s : tablaSimbolos.getTabla()) {
            if (s.getCategoria().equals("VARIABLE") && s.getAmbito().equals(nombreMetodo)) {
                params.add(s);
            }
        }
        for (int i = params.size() - 1; i >= 0; i--) {
            if (!pilaArgs.isEmpty()) {
                memoria.put(params.get(i).getDireccionMemoria(), pilaArgs.pop());
            }
        }
        pilaArgs.clear();
    }

    private double obtenerValorOperando(String operando, Map<String, Double> memoria) {
        if (memoria.containsKey(operando)) {
            return memoria.get(operando);
        }
        try {
            return Double.parseDouble(operando);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}