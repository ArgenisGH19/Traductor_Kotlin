package act1_lexico;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class PruebaSintactica {
    public static void main(String[] args) {
        
        // Cambiare este nombre por "programa_errores_prueba.txt" o "programa_fatal_prueba.txt" para probar los otros casos.
        // Asegúrate de que los .txt estén en la misma ruta que tu tokens_java.txt
        String rutaArchivo = "src/act1_lexico/programa_fatal_prueba.txt";
        
        try {
            // 1. Limpieza de errores de ejecuciones anteriores (por si acaso el léxico detecta algo raro)
            ManejoErrores.limpiar();
            
            Reader lector = new BufferedReader(new FileReader(rutaArchivo));
            LexerJava lexer = new LexerJava(lector);
            
            // 2. Inicializamos el analizador sintáctico (CUP) pasándole nuestro léxico (JFlex)
            ParserJava sintactico = new ParserJava(lexer);

            System.out.println("=== INTEGRANTES DEL EQUIPO ===");
            System.out.println("1. Becerra Bedoy, Pablo");  
            System.out.println("2. Argenis Adan Gutierrez Hurtado");
            System.out.println("3. Gerardo Josue Rubio Calderon");
            System.out.println("=========================================================\n");
            
            System.out.println("--- INICIANDO ANALISIS SINTACTICO ---");
            System.out.println("Archivo a analizar: " + rutaArchivo + "\n");
            
            // 3. Ejecutamos el análisis sintáctico de toda la estructura
            sintactico.parse();
            
            // 4. Si el analizador léxico atrapó algún error en el proceso, lo mostramos al final
            System.out.println("\n--- RESULTADOS DEL MANEJO DE ERRORES LEXICOS ---");
            ManejoErrores.mostrarErrores();
            
        } catch (FileNotFoundException ex) {
            System.err.println("¡Error! No se encontro el archivo de texto. Verifica la ruta: " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("¡Error de lectura!: " + ex.getMessage());
        } catch (Exception ex) {
            // Este catch atrapará el System.exit(1) o cualquier excepción del report_fatal_error de CUP
            System.err.println("¡Error estructural! El analisis sintáctico finalizo abruptamente.");
        }

        System.out.println("\n=========================================================");
        System.out.println("=== FIN DE EJECUCION - INTEGRANTES DEL EQUIPO ===");
        System.out.println("1. Becerra Bedoy, Pablo");
        System.out.println("2. Argenis Adan Gutierrez Hurtado");
        System.out.println("3. Gerardo Josue Rubio Calderon");
        System.out.println("=========================================================");
    }
}