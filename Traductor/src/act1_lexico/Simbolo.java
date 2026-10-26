package act1_lexico;

import java.util.ArrayList;
import java.util.List;

/*
Actividad 9 y 11: Analisis semantico gestion de la tabla de simbolos, tipos de datos y firmas
Gerardo Josue Rubio Calderon
Pablo Becerra Bedoy
Argenis Adan Gutierez Hurtado
*/
public class Simbolo {
    private String id;
    private String categoria; // CLASE, METODO, VARIABLE, ATRIBUTO, CONSTANTE
    private String tipoDato;
    private String valor;
    private String ambito;
    private int linea;
    private int columna;
    private String tokenOriginador;
    
    // --- ¡NUEVO (Actividad 11)! Memoria para firmas de métodos ---
    private List<String> tiposParametros;

    public Simbolo(String id, String categoria, String tipoDato, String valor, String ambito, int linea, int columna, String tokenOriginador) {
        this.id = id;
        this.categoria = categoria;
        this.tipoDato = tipoDato;
        this.valor = (valor == null || valor.isEmpty()) ? "null" : valor;
        this.ambito = ambito;
        this.linea = linea;
        this.columna = columna;
        this.tokenOriginador = tokenOriginador;
        this.tiposParametros = new ArrayList<>(); // Lo inicializamos vacío
    }

    // Getters originales
    public String getId() { return id; }
    public String getCategoria() { return categoria; }
    public String getTipoDato() { return tipoDato; }
    public String getValor() { return valor; }
    public String getAmbito() { return ambito; }
    public int getLinea() { return linea; }
    public int getColumna() { return columna; }
    public String getTokenOriginador() { return tokenOriginador; }

    // --- ¡NUEVOS METODOS (Actividad 11)! ---
    public void agregarTipoParametro(String tipo) {
        this.tiposParametros.add(tipo);
    }

    public List<String> getTiposParametros() {
        return tiposParametros;
    }
    
    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }
}

    