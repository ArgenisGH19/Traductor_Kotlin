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

public class ErrorLexico {
    private String tipo;
    private String lexema;
    private int linea;
    private int columna;
    private String descripcion;

    public ErrorLexico(String tipo, String lexema, int linea, int columna, String descripcion) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.linea = linea;
        this.columna = columna;
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return String.format("[%s] Error en Linea %d, Columna %d: '%s' -> %s", 
                tipo, linea, columna, lexema, descripcion);
    }
}