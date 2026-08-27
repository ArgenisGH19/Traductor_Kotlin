package act1_lexico;

import java.io.IOException;



public class GeneradorCupSintantico {
    public static void main(String[] args) throws IOException, Exception {
        
        String[] parametros = {"-destdir", "src\\act1_lexico",
            "-parser", "ParserJava", 
            "-progress", "src\\act1_lexico\\java_lexico.cup"};
        java_cup.Main.main(parametros);
        
    }
    
   
}
