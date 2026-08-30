package act1_lexico;

import java.io.IOException;



public class GeneradorCupSintantico {
    public static void main(String[] args) {
        String[] opcionesCup = {
            "-destdir", "src/act1_lexico",
            "-parser", "ParserJava",
            "-symbols", "sym",
            "src/act1_lexico/java_lexico.cup"
        };
        try {
            java_cup.Main.main(opcionesCup);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
