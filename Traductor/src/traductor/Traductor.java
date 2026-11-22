package traductor;

import act1_lexico.LexerJava;
import act1_lexico.parser;
import act1_lexico.OptimizadorC3D;
import act1_lexico.GeneradorBytecodeJasmin;
import java.io.*;
import java.util.List;

/*
Equipo 2: Kotlin
INTEGRANTES: Argenis Adán Gutiérrez Hurtado, Gerardo Josue Rubio Calderon, Pablo Becerra Bedoy
*/

public class Traductor {
    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("=== INTEGRANTES DEL EQUIPO ===");
        System.out.println("1. Becerra Bedoy, Pablo");
        System.out.println("2. Gutierrez Hurtado, Argenis Adan");
        System.out.println("3. Rubio Calderon, Gerardo Josue");
        System.out.println("=========================================================\n");

        String rutaArchivo = "src/pruebas/prueba_final.txt";
        String carpetaSalida = "salida_bytecode";
        System.out.println("--- INICIANDO COMPILACION COMPLETA (KOTLIN -> JVM .CLASS) ---");
        System.out.println("Archivo fuente: " + rutaArchivo + "\n");

        parser analizador = null;

        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) {
                System.err.println("ERROR: No se encontro el archivo de prueba en: " + archivo.getAbsolutePath());
                return;
            }

            // Lectura limpia omitiendo BOM
            InputStream inputStream = new FileInputStream(archivo);
            PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream, 3);
            byte[] bom = new byte[3];
            int n = pushbackInputStream.read(bom, 0, bom.length);
            if (!(n == 3 && (bom[0] & 0xFF) == 0xEF && (bom[1] & 0xFF) == 0xBB && (bom[2] & 0xFF) == 0xBF)) {
                if (n > 0) pushbackInputStream.unread(bom, 0, n);
            }

            Reader reader = new InputStreamReader(pushbackInputStream, "UTF-8");
            LexerJava lexer = new LexerJava(reader);
            analizador = new parser(lexer);

            // 1. Analisis lexico, sintactico, semantico y C3D
            analizador.parse();

            System.out.println("\n========================================================");
            if (lexer.totalErroresLexicos == 0 && analizador.totalErrores == 0 && analizador.totalErroresSemanticos == 0) {
                System.out.println("ANALISIS FINALIZADO CORRECTAMENTE");
                System.out.println("Errores lexicos: 0 | Errores sintacticos: 0 | Errores semanticos: 0");
            } else {
                System.out.println("ANALISIS COMPLETADO CON ERRORES");
                System.out.println("Errores lexicos: " + lexer.totalErroresLexicos);
                System.out.println("Errores sintacticos: " + analizador.totalErrores);
                System.out.println("Errores semanticos: " + analizador.totalErroresSemanticos);
                System.out.println("Generacion de bytecode cancelada por errores en el fuente.");
                System.out.println("========================================================\n");
                analizador.tablaSimbolos.imprimirTabla();
                return;
            }
            System.out.println("========================================================\n");

            // 2. Tabla de Simbolos
            analizador.tablaSimbolos.imprimirTabla();

            // 3. C3D Original y Optimizado
            OptimizadorC3D optimizador = new OptimizadorC3D();
            List<String> c3dOptimizado = optimizador.optimizar(analizador.codigoIntermedio);

            System.out.println("\n========================================================");
            System.out.println("          CODIGO DE 3 DIRECCIONES (OPTIMIZADO)");
            System.out.println("========================================================");
            for (String inst : c3dOptimizado) {
                System.out.println(inst);
            }

            // 4. Generacion de Bytecode Jasmin (.j)
            GeneradorBytecodeJasmin genBytecode = new GeneradorBytecodeJasmin();
            String codigoJasmin = genBytecode.generar(c3dOptimizado, analizador.tablaSimbolos);
            File archivoJ = genBytecode.guardarArchivoJasmin(codigoJasmin, carpetaSalida);
            String nombreClase = genBytecode.getNombreClase();

            System.out.println("\n========================================================");
            System.out.println("          BYTECODE JASMIN GENERADO (" + archivoJ.getName() + ")");
            System.out.println("========================================================");
            System.out.println(codigoJasmin);

            // Localizar exactamente el java y javap del JDK activo de NetBeans
            boolean esWindows = System.getProperty("os.name").toLowerCase().contains("win");
            String ext = esWindows ? ".exe" : "";
            File binDir = new File(System.getProperty("java.home"), "bin");
            File javaExe = new File(binDir, "java" + ext);
            File javapExe = new File(binDir, "javap" + ext);

            String javaBin = javaExe.exists() ? javaExe.getAbsolutePath() : "java";
            String javapBin = javapExe.exists() ? javapExe.getAbsolutePath() : "javap";

            // 5. Ensamblar .j -> .class con jasmin.jar
            File jasminJar = new File("jasmin.jar").exists() ? new File("jasmin.jar") : new File("lib/jasmin.jar");
            if (!jasminJar.exists()) {
                System.err.println("\nAVISO: Coloca 'jasmin.jar' en la raiz del proyecto para ensamblar automaticamente.");
                System.err.println("Tambien puedes ejecutar manualmente:");
                System.err.println("  java -jar jasmin.jar -d " + carpetaSalida + " " + archivoJ.getPath());
                System.err.println("  javap -c -cp " + carpetaSalida + " " + nombreClase);
                System.err.println("  java -cp " + carpetaSalida + " " + nombreClase);
                return;
            }

            System.out.println("========================================================");
            System.out.println("        ENSAMBLANDO .CLASS CON JASMIN");
            System.out.println("========================================================");
            int exitJasmin = ejecutarComando(new String[]{
                javaBin, "-jar", jasminJar.getPath(), "-d", carpetaSalida, archivoJ.getPath()
            });

            if (exitJasmin != 0) {
                System.err.println("Error al ensamblar el archivo .class con Jasmin (codigo " + exitJasmin + ").");
                return;
            }
            System.out.println("Archivo ejecutable generado: " + carpetaSalida + File.separator + nombreClase + ".class\n");

            // 6. Inspeccionar bytecode real con javap -c
            System.out.println("========================================================");
            System.out.println("        INSPECCION DE BYTECODE REAL (javap -c)");
            System.out.println("========================================================");
            ejecutarComando(new String[]{javapBin, "-c", "-cp", carpetaSalida, nombreClase});

            // 7. Ejecutar el archivo .class real en la JVM
            System.out.println("\n========================================================");
            System.out.println("        EJECUCION DEL PROGRAMA COMPILADO EN JVM");
            System.out.println("========================================================");
            int exitRun = ejecutarComando(new String[]{javaBin, "-cp", carpetaSalida, nombreClase});
            if (exitRun != 0) {
                System.err.println("La ejecucion del programa compilado termino con codigo: " + exitRun);
            }
            System.out.println("========================================================\n");

        } catch (Exception e) {
            System.err.println("\nError durante la compilacion o ejecucion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int ejecutarComando(String[] comando) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(comando);
        pb.inheritIO(); // Conecta consola y teclado directamente para println y readln
        Process proceso = pb.start();
        return proceso.waitFor();
    }
}