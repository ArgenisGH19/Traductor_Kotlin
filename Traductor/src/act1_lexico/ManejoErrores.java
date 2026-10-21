/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
Equipo EM02 KOTLIN
Gutiérrez Hurtado Argenis Adan
Becerra Bedoy Pablo
Rubio Calderon Gerardo Josue
*/
package act1_lexico;

import java.util.ArrayList;
import java.util.List;

public class ManejoErrores {
    private static List<ErrorLexico> listaErroresLexicos = new ArrayList<>();
    private static List<String> listaErroresSemanticos = new ArrayList<>();

    public static void agregar(ErrorLexico error) {
        listaErroresLexicos.add(error);
    }

    public static void agregarSemantico(String mensaje, int linea, int columna) {
        String error = "Error semantico en linea " + linea + ", columna " + columna + ":\n" + mensaje;
        listaErroresSemanticos.add(error);
        System.err.println(error);
    }

    public static void mostrarErrores() {
        if (listaErroresLexicos.isEmpty() && listaErroresSemanticos.isEmpty()) {
            System.out.println("No se encontraron errores lexicos ni semanticos.");
        } else {
            if (!listaErroresLexicos.isEmpty()) {
                System.out.println("=== REPORTES DE ERRORES LEXICOS ===");
                for (ErrorLexico err : listaErroresLexicos) {
                    System.out.println(err);
                }
            }
            if (!listaErroresSemanticos.isEmpty()) {
                System.out.println("\n=== REPORTES DE ERRORES SEMANTICOS ===");
                for (String err : listaErroresSemanticos) {
                    System.out.println(err);
                }
            }
        }
    }

    public static void limpiar() {
        listaErroresLexicos.clear();
        listaErroresSemanticos.clear();
    }
}