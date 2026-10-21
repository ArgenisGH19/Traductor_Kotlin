/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package traductor;

import act1_lexico.LexerJava;
import act1_lexico.parser; // Importamos el parser que acaba de generar CUP
import act1_lexico.ManejoErrores; // Importamos nuestro manejador de errores
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
            // IMPORTANTE: Pon el nombre de uno de tus archivos .txt (ej. "semantica_invalida_act9.txt")
            String rutaArchivo = "src/act1_lexico/prueba10.txt"; 
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

        } catch (Exception e) {
            System.err.println("Error durante la ejecucion del parser: " + e.getMessage());
            e.printStackTrace();
        }
    }
}