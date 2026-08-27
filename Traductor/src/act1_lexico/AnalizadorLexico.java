/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
            
            Reader lector = new BufferedReader(new FileReader("src/act1_lexico/tokens_java.txt"));
            LexerJava lexer = new LexerJava(lector);
            
            System.out.println("--- INICIANDO ANALISIS LEXICO AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA ---");
            
            while (true) {
                // Obtenemos el siguiente token
                Symbol token = lexer.next_token();
                
                // Si llegamos al final del archivo, rompemos el ciclo
                if (token.sym == sym.EOF) {
                    System.out.println("--- FIN DEL ARCHIVO ---");
                    break;
                }
                
                // Imprimimos la información del token detectado
                System.out.println("Token detectado -> ID (sym): " + token.sym + " | Valor: " + token.value);
            }
            
        } catch (FileNotFoundException ex) {
            // Si el archivo no existe o la ruta está mal, ahora SÍ te va a avisar
            System.err.println("¡Error! No se encontro el archivo de texto. Verifica la ruta: " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("¡Error de lectura!: " + ex.getMessage());
        } catch (Error ex) {
            // Este catch atrapa el error de "Illegal character" que lanza JFlex si pones un carácter no válido (como una coma)
            System.err.println("¡Error Lexico! Encontraste un carácter que no está en las reglas: " + ex.getMessage());
        }
        
    }
}