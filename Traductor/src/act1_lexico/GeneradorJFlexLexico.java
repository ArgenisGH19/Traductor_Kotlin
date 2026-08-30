package act1_lexico;

/*
Equipo EM02 KOTLIN
Gutiérrez Hurtado Argenis Adan
Becerra Bedoy Pablo
Rubio Calderon Gerardo Josue
*/

import java.io.File;

public class GeneradorJFlexLexico {
    public static void main(String[] args) {
        // Obtenemos la ruta raíz del proyecto en ejecución
        String rutaProyecto = System.getProperty("user.dir");
        
        // Construimos la ruta absoluta hacia tu archivo .jflex
        String rutaJFlex = rutaProyecto + File.separator + "src" + File.separator + "act1_lexico" + File.separator + "java_lexico.jflex";
        
        File archivoJFlex = new File(rutaJFlex);
        
        System.out.println("Buscando archivo en: " + archivoJFlex.getAbsolutePath());
        
        if (!archivoJFlex.exists()) {
            System.err.println("¡ERROR! El archivo java_lexico.jflex no existe en esa ruta.");
            return;
        }

        try {
            // Se pasa la ruta en un arreglo String[] para evitar la incompatibilidad de tipos
            String[] parametros = { archivoJFlex.getAbsolutePath() };
            jflex.Main.generate(parametros);
            
            System.out.println("¡ÉXITO! LexerJava.java generado correctamente.");
        } catch (Exception e) {
            System.err.println("Ocurrió un error al generar con JFlex:");
            e.printStackTrace();
        }
    }
}