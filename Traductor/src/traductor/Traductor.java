/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package traductor;

import act1_lexico.LexerJava;
import act1_lexico.ParserJava;
import java.io.FileReader;

public class Traductor {
    public static void main(String[] args) {
        try {
            // Nombre o ruta de tu archivo de prueba
            FileReader reader = new FileReader("prueba"); 
            
            LexerJava lexer = new LexerJava(reader);
            ParserJava parser = new ParserJava(lexer);
            
            // Inicia el análisis sintáctico
            parser.parse();
            
            System.out.println("\n--- Analisis sintactico completado con exito ---");
        } catch (Exception e) {
            System.err.println("Error durante la ejecucion del parser: " + e.getMessage());
            e.printStackTrace();
        }
    }
}