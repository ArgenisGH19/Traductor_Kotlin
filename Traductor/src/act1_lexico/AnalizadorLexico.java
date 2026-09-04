package act1_lexico;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java_cup.runtime.Symbol;

public class AnalizadorLexico {
    public static void main(String[] args) {
        
        try {
            // 1. Limpieza de errores de ejecuciones anteriores
            ManejoErrores.limpiar();
            
            Reader lector = new BufferedReader(new FileReader("src/act1_lexico/tokens_java.txt"));
            LexerJava lexer = new LexerJava(lector);

            System.out.println("=== INTEGRANTES DEL EQUIPO ===");
            System.out.println("1. Becerra Bedoy, Pablo");  
            System.out.println("2. Argenis Adan Gutierrez Hurtado");
            System.out.println("3. Gerardo Josue Rubio Calderon");
            System.out.println("=========================================================\n");
            
            System.out.println("--- INICIANDO ANÁLISIS LÉXICO ---");
            
            while (true) {
                // Obtenemos el siguiente token generado por el Lexer
                Symbol token = lexer.next_token();
                
                // Si llegamos al final del archivo, rompemos el ciclo
                if (token.sym == sym.EOF) {
                    System.out.println("--- FIN DEL ARCHIVO ---");
                    break;
                }
                
                // Si el token capturado NO es un error léxico, mostramos el token válido
                if (token.sym != sym.ERROR) {
                    System.out.println("Token valido -> ID (sym): " + token.sym + " | Valor: " + token.value);
                }
            }
            
            System.out.println("\n--- RESULTADOS DEL MANEJO DE ERRORES ---");
            // 2. Muestra la lista final de todos los errores recopilados
            ManejoErrores.mostrarErrores();
            
        } catch (FileNotFoundException ex) {
            System.err.println("¡Error! No se encontro el archivo de texto. Verifica la ruta: " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("¡Error de lectura!: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("¡Error general en el analizador!: " + ex.getMessage());
        }

        System.out.println("\n=========================================================");
        System.out.println("=== FIN DE EJECUCIÓN - INTEGRANTES DEL EQUIPO ===");
        System.out.println("1. Becerra Bedoy, Pablo");
        System.out.println("2. Argenis Adan Gutierrez Hurtado");
        System.out.println("3. Gerardo Josue Rubio Calderon");
        System.out.println("=========================================================");
    }
}