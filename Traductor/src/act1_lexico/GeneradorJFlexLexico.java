/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package act1_lexico;

import jflex.Main;
//import jflex.exceptions.SilentExit;

/**
 *
 * @author Profesor
 */
public class GeneradorJFlexLexico {
    public static void main(String[] args) {
        
        try {
            String rutaflex="src/act1_lexico/java_lexico.jflex";
            
            String datos[]={rutaflex};
            
            Main.generate(datos);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.getLogger(GeneradorJFlexLexico.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
    }
}
