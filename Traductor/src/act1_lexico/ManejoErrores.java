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
    private static List<ErrorLexico> listaErrores = new ArrayList<>();

    public static void agregar(ErrorLexico error) {
        listaErrores.add(error);
    }

    public static void mostrarErrores() {
        if (listaErrores.isEmpty()) {
            System.out.println("No se encontraron errores lexicos.");
        } else {
            System.out.println("=== REPORTES DE ERRORES LEXICOS ===");
            for (ErrorLexico err : listaErrores) {
                System.out.println(err);
            }
        }
    }

    public static void limpiar() {
        listaErrores.clear();
    }
}
