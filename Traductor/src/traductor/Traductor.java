package traductor;

import act1_lexico.LexerJava;
import act1_lexico.parser;
import java.io.*;

/*
Equipo 2: Kotlin
INTEGRANTES: Argenis Adán Gutiérrez Hurtado, Gerardo Josue Rubio Calderon, Pablo Becerra Bedoy
*/

public class Traductor {
    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("=== INTEGRANTES DEL EQUIPO ===");
        System.out.println("1. Becerra Bedoy, Pablo");
        System.out.println("2. Gutierrez Hurtado, Argenis Adan");
        System.out.println("3. Rubio Calderon, Gerardo Josue");
        System.out.println("=========================================================\n");

        String rutaArchivo = "src/act1_lexico/prueba_act13.txt"; 
        System.out.println("--- INICIANDO ANALISIS Y GENERACION DE CODIGO INTERMEDIO ---");
        System.out.println("Archivo a analizar: " + rutaArchivo + "\n");

        parser analizador = null;

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
            
            if (!(n == 3 && (bom[0] & 0xFF) == 0xEF && (bom[1] & 0xFF) == 0xBB && (bom[2] & 0xFF) == 0xBF)) {
                if (n > 0) pushbackInputStream.unread(bom, 0, n);
            }

            Reader reader = new InputStreamReader(pushbackInputStream, "UTF-8");
            LexerJava lexer = new LexerJava(reader);
            analizador = new parser(lexer);

            // 1. Inicia el análisis sintáctico, semántico y generación C3D
            analizador.parse();

            // 2. Resumen de diagnóstico (Situación 1 y 2)
            System.out.println("\n========================================================");
            if (analizador.totalErrores == 0 && analizador.totalErroresSemanticos == 0) {
                System.out.println("ANALISIS FINALIZADO CORRECTAMENTE");
                System.out.println("Errores sintacticos: 0 | Errores semanticos: 0");
                System.out.println("Estado: Codigo valido y traducido con exito.");
            } else if (!analizador.errorFatal) {
                System.out.println("ANALISIS COMPLETADO CON ERRORES");
                System.out.println("Errores sintacticos encontrados: " + analizador.totalErrores);
                System.out.println("Errores semanticos encontrados: " + analizador.totalErroresSemanticos);
                System.out.println("Estado: Se completo el recorrido bloqueando las instrucciones invalidas.");
            }
            System.out.println("========================================================\n");

            // 3. Imprimimos la Tabla de Símbolos final
            analizador.tablaSimbolos.imprimirTabla();

            // 4. Imprimimos el Código de 3 Direcciones (C3D)
            System.out.println("\n========================================================");
            System.out.println("              CODIGO DE 3 DIRECCIONES (C3D)");
            System.out.println("========================================================");
            if (analizador.codigoIntermedio != null && !analizador.codigoIntermedio.isEmpty()) {
                for (String instruccion : analizador.codigoIntermedio) {
                    System.out.println(instruccion);
                }
            } else {
                System.out.println("No se genero codigo intermedio.");
            }
            System.out.println("========================================================\n");

        } catch (Exception e) {
            System.err.println("\n========================================================");
            System.err.println("ANALISIS INTERRUMPIDO");
            System.err.println("Estado: Se encontro un error sintactico no recuperable.");
            if (analizador != null && analizador.ultimaLinea != -1) {
                System.err.println("Ultima ubicacion analizada: linea " + analizador.ultimaLinea + ", columna " + analizador.ultimaColumna);
            }
            System.err.println("========================================================\n");
        }
    }
}