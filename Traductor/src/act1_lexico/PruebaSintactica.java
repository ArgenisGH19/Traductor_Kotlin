package act1_lexico;

import java.io.*;
import java_cup.runtime.Symbol;

public class PruebaSintactica {
    public static void main(String[] args) {
        System.out.println("=== INTEGRANTES DEL EQUIPO ===");
        System.out.println("1. Becerra Bedoy, Pablo");
        System.out.println("2. Gutierrez Hurtado, Argenis Adan");
        System.out.println("3. Rubio Calderon, Gerardo Josue");
        System.out.println("=========================================================\n");

        // Cambia el nombre del archivo .txt aquí según la prueba que quieras hacer
        String rutaArchivo = "src/act1_lexico/semantica_valida_act9.txt"; 
        System.out.println("--- INICIANDO ANALISIS SINTACTICO Y SEMANTICO ---");
        System.out.println("Archivo a analizar: " + rutaArchivo + "\n");

        // Declaramos el parser afuera del try para poder acceder a sus variables en el catch si hay error fatal
        parser sintactico = null;

        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) {
                System.err.println("¡ERROR! No se encontro el archivo de prueba en: " + archivo.getAbsolutePath());
                return;
            }

            // Lectura limpia omitiendo BOM si existe
            InputStream inputStream = new FileInputStream(archivo);
            PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream, 3);
            byte[] bom = new byte[3];
            int n = pushbackInputStream.read(bom, 0, bom.length);
            
            // Si no tiene BOM UTF-8 (0xEF, 0xBB, 0xBF), los devolvemos al stream
            if (!(n == 3 && (bom[0] & 0xFF) == 0xEF && (bom[1] & 0xFF) == 0xBB && (bom[2] & 0xFF) == 0xBF)) {
                if (n > 0) pushbackInputStream.unread(bom, 0, n);
            }

            Reader reader = new InputStreamReader(pushbackInputStream, "UTF-8");
            LexerJava lexer = new LexerJava(reader);
            sintactico = new parser(lexer);

            // Analizamos el archivo y llenamos la tabla de símbolos
            sintactico.parse();

            // --- IMPRESIÓN DE DIAGNÓSTICO FINAL (Situación 1 y 2) ---
            System.out.println("\n========================================================");
            if (sintactico.totalErrores == 0) {
                System.out.println("ANALISIS FINALIZADO CORRECTAMENTE");
                System.out.println("Errores sintacticos encontrados: 0");
                System.out.println("Estado: Codigo sintacticamente valido.");
            } else if (!sintactico.errorFatal) {
                System.out.println("ANALISIS COMPLETADO CON ERRORES");
                System.out.println("Errores sintacticos encontrados: " + sintactico.totalErrores);
                System.out.println("Estado: Se completo el recorrido del archivo, \npero el codigo contiene errores sintacticos.");
            }
            System.out.println("========================================================\n");

            // Imprimimos la tabla de símbolos al final
            sintactico.tablaSimbolos.imprimirTabla();

        } catch (Exception e) {
            // --- IMPRESIÓN DE DIAGNÓSTICO FINAL (Situación 3: Error Fatal) ---
            System.err.println("\n========================================================");
            System.err.println("ANALISIS INTERRUMPIDO");
            System.err.println("Estado: Se encontro un error sintactico no recuperable.");
            
            if (sintactico != null && sintactico.ultimaLinea != -1) {
                System.err.println("\nUltima ubicacion analizada: linea " + sintactico.ultimaLinea + ", columna " + sintactico.ultimaColumna);
            }
            System.err.println("========================================================\n");
        }
    }
}