package act1_lexico;

import java.io.File;

public class GeneradorCompleto {
    public static void main(String[] args) {
        try {
            String rutaProyecto = System.getProperty("user.dir");
            String carpetaDestino = rutaProyecto + File.separator + "src" + File.separator + "act1_lexico";

            // Se asigna la ruta apuntando directamente a java_lexico.cup
            String rutaCup = carpetaDestino + File.separator + "java_lexico.cup";
            String rutaFlex = carpetaDestino + File.separator + "java_lexico.jflex";

            File archivoCup = new File(rutaCup);

            System.out.println("=== 1. GENERANDO JAVACUP (sym.java y parser.java) ===");
            if (!archivoCup.exists()) {
                System.err.println("¡ERROR FATAL! No se encontró el archivo: " + rutaCup);
                return;
            }

            System.out.println("Archivo CUP detectado en: " + archivoCup.getAbsolutePath());

            String[] opcionesCup = {
                "-destdir", carpetaDestino,
                "-parser", "parser",
                "-symbols", "sym",
                archivoCup.getAbsolutePath()
            };
            
            java_cup.Main.main(opcionesCup);
            System.out.println("--> JavaCUP ejecutó correctamente.");

            // Espera de 1 segundo para sincronización con el sistema de archivos
            Thread.sleep(1000);

            System.out.println("\n=== 2. GENERANDO JFLEX (LexerJava.java) ===");
            File archivoFlex = new File(rutaFlex);
            
            if (!archivoFlex.exists()) {
                System.err.println("¡ERROR FATAL! No se encontró el archivo .jflex en: " + rutaFlex);
                return;
            }

            String[] opcionesFlex = { archivoFlex.getAbsolutePath() };
            jflex.Main.generate(opcionesFlex);
            System.out.println("--> JFlex ejecutó correctamente.");

            System.out.println("\n==========================================");
            System.out.println("¡PROCESO COMPLETADO CON ÉXITO!");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("Ocurrió un error durante la generación:");
            e.printStackTrace();
        }
    }
}