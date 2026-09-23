package act1_lexico;

import java.io.File;
/*
Equipo EM02 KOTLIN
Actividad 6: Análisis sintáctico de expresiones aritméticas y lógicas.
Gutiérrez Hurtado Argenis Adan
Becerra Bedoy Pablo
Rubio Calderon Gerardo Josue
*/
public class GeneradorCompleto {
    public static void main(String[] args) {
        try {
            String rutaProyecto = System.getProperty("user.dir");
            String carpetaDestino = rutaProyecto + File.separator + "src" + File.separator + "act1_lexico";

            // Definimos las rutas de los 3 archivos
            String rutaCupLexico = carpetaDestino + File.separator + "java_lexico.cup";
            String rutaCupSintactico = carpetaDestino + File.separator + "java_sintactico.cup";
            String rutaFlex = carpetaDestino + File.separator + "java_lexico.jflex";

            File archivoCupLexico = new File(rutaCupLexico);
            File archivoCupSintactico = new File(rutaCupSintactico);
            File archivoFlex = new File(rutaFlex);

            // =========================================================================
            System.out.println("=== 1. GENERANDO JAVACUP SECUNDARIO (java_lexico.cup) ===");
            if (archivoCupLexico.exists()) {
                // Le damos nombres distintos (ParserLexico y symLexico) para que no sobreescriba el principal
                String[] opcionesCupLexico = {
                    "-destdir", carpetaDestino,
                    "-parser", "ParserLexico",
                    "-symbols", "symLexico",
                    archivoCupLexico.getAbsolutePath()
                };
                java_cup.Main.main(opcionesCupLexico);
                System.out.println("--> java_lexico.cup compilado (Gener ParserLexico.java y symLexico.java)");
            } else {
                System.out.println("--> Aviso: No se encontro java_lexico.cup, saltando paso...");
            }

            // Esperamos medio segundo
            Thread.sleep(500);

            // =========================================================================
            System.out.println("\n=== 2. GENERANDO JAVACUP PRINCIPAL (java_sintactico.cup) ===");
            if (archivoCupSintactico.exists()) {
                // Este generará los verdaderos 'parser.java' y 'sym.java' que usa JFlex
                String[] opcionesCupSintactico = {
                    "-destdir", carpetaDestino,
                    "-parser", "parser",
                    "-symbols", "sym",
                    archivoCupSintactico.getAbsolutePath()
                };
                java_cup.Main.main(opcionesCupSintactico);
                System.out.println("--> java_sintactico.cup compilado (Genero parser.java y sym.java)");
            } else {
                System.err.println("¡ERROR FATAL! No se encontro el archivo principal: " + rutaCupSintactico);
                return;
            }

            // Esperamos un segundo completo para sincronizar archivos
            Thread.sleep(1000);

            // =========================================================================
            System.out.println("\n=== 3. GENERANDO JFLEX (LexerJava.java) ===");
            if (!archivoFlex.exists()) {
                System.err.println("¡ERROR FATAL! No se encontro el archivo .jflex en: " + rutaFlex);
                return;
            }

            String[] opcionesFlex = { archivoFlex.getAbsolutePath() };
            jflex.Main.generate(opcionesFlex);
            System.out.println("--> java_lexico.jflex ejecuto correctamente.");

            System.out.println("\n==========================================");
            System.out.println("¡PROCESO TRIPLE COMPLETADO CON EXITO!");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("Ocurrio un error durante la generacion:");
            e.printStackTrace();
        }
    }
}