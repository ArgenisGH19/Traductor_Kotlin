package traductor;

import act1_lexico.LexerJava;
import act1_lexico.parser; 
import act1_lexico.ManejoErrores; 
import java.io.FileReader;

/*
Equipo 2: Kotlin
 INTEGRANTES: Argenis Adán Gutiérrez Hurtado, Gerardo Josue Rubio Calderon, Pablo Becerra Bedoy
*/

public class Traductor {
    public static void main(String[] args) {
        try {
            // 1. Limpiamos cualquier error de ejecuciones anteriores
            ManejoErrores.limpiar();

            // 2. Ruta de tu archivo de prueba. 
            String rutaArchivo = "src/act1_lexico/prueba_act12.txt"; 
            FileReader reader = new FileReader(rutaArchivo); 
            
            LexerJava lexer = new LexerJava(reader);
            parser analizador = new parser(lexer);
            
            System.out.println("--- Iniciando analisis sintactico y semantico ---");
            
            // 3. Inicia el análisis
            analizador.parse();
            
            System.out.println("\n--- Analisis completado ---");

            // 4. Imprimimos la Tabla de Símbolos final
            analizador.tablaSimbolos.imprimirTabla();

            // 5. Imprimimos el reporte de todos los errores (Léxicos y Semánticos)
            System.out.println();
            ManejoErrores.mostrarErrores();

            // 6. --- NUEVO: IMPRIMIR CÓDIGO DE 3 DIRECCIONES ---
            // Solo lo imprimimos si no hubo errores semánticos graves que detuvieran todo
            System.out.println("\n=======================================================");
            System.out.println("             CODIGO DE 3 DIRECCIONES (C3D)");
            System.out.println("=======================================================");
            if (analizador.codigoIntermedio != null && !analizador.codigoIntermedio.isEmpty()) {
                for (String instruccion : analizador.codigoIntermedio) {
                    System.out.println(instruccion);
                }
            } else {
                System.out.println("No se genero codigo intermedio.");
            }
            System.out.println("=======================================================");

        } catch (Exception e) {
            System.err.println("Error durante la ejecucion del parser: " + e.getMessage());
            e.printStackTrace();
        }
    }
}