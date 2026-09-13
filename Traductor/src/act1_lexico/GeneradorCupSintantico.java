package act1_lexico;

import java.io.IOException;
import java_cup.runtime.*;


public class GeneradorCupSintantico {
    public static void main(String[] args) {
        String[] opcionesCup = {
            "-destdir", "src/act1_lexico",
            "-parser", "ParserJava",
            "-symbols", "sym",
            "src/act1_lexico/java_sintactico.cup"
        };
        try {
            java_cup.Main.main(opcionesCup);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
