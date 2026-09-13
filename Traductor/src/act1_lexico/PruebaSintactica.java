package act1_lexico;

import java.io.*;
import java_cup.runtime.Symbol;

public class PruebaSintactica {
    public static void main(String[] args) {
        System.out.println("=== INTEGRANTES DEL EQUIPO ===");
        System.out.println("1. Becerra Bedoy, Pablo");
        System.out.println("2. Gutiérrez Hurtado, Argenis Adán");
        System.out.println("3. Rubio Calderón, Gerardo Josué");
        System.out.println("=========================================================\n");

        String rutaArchivo = "src/act1_lexico/prueba valida.txt";
        System.out.println("--- INICIANDO ANALISIS SINTACTICO ---");
        System.out.println("Archivo a analizar: " + rutaArchivo + "\n");

        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) {
                System.err.println("¡ERROR! No se encontró el archivo de prueba en: " + archivo.getAbsolutePath());
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
            parser sintactico = new parser(lexer);

            sintactico.parse();
            System.out.println("\nAnalisis sintactico finalizado con exito.");

        } catch (Exception e) {
            System.err.println("\nEl analisis no puede continuar. Abortando...");
            e.printStackTrace();
        }
    }
}