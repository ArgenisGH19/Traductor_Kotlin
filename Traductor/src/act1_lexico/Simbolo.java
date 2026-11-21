package act1_lexico;

import java.util.ArrayList;
import java.util.List;

/*
Equipo 2: Kotlin
INTEGRANTES: Argenis Adán Gutiérrez Hurtado, Gerardo Josue Rubio Calderon, Pablo Becerra Bedoy
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
    private List<String> tiposParametros;
    
    // Etiqueta C3D asociada al método (ej. L6, L7)
    private String etiquetaC3D;

    // Dirección de memoria asignada (ej. 0x2000, 0x2004)
    private String direccionMemoria;

    public Simbolo(String id, String categoria, String tipoDato, String valor, String ambito, int linea, int columna, String tokenOriginador) {
        this.id = id;
        this.categoria = categoria;
        this.tipoDato = tipoDato;
        this.valor = (valor == null || valor.isEmpty()) ? "null" : valor;
        this.ambito = ambito;
        this.linea = linea;
        this.columna = columna;
        this.tokenOriginador = tokenOriginador;
        this.tiposParametros = new ArrayList<>();
        this.etiquetaC3D = "";
        this.direccionMemoria = "N/A";
    }

    public String getId() { return id; }
    public String getCategoria() { return categoria; }
    public String getTipoDato() { return tipoDato; }
    public String getValor() { return valor; }
    public String getAmbito() { return ambito; }
    public int getLinea() { return linea; }
    public int getColumna() { return columna; }
    public String getTokenOriginador() { return tokenOriginador; }

    public void setTipoDato(String tipoDato) { 
        this.tipoDato = tipoDato; 
    }

    public void agregarTipoParametro(String tipo) {
        this.tiposParametros.add(tipo);
    }

    public List<String> getTiposParametros() {
        return tiposParametros;
    }

    public String getEtiquetaC3D() {
        return etiquetaC3D;
    }

    public void setEtiquetaC3D(String etiquetaC3D) {
        this.etiquetaC3D = etiquetaC3D;
    }

    public String getDireccionMemoria() {
        return direccionMemoria;
    }

    public void setDireccionMemoria(String direccionMemoria) {
        this.direccionMemoria = direccionMemoria;
    }
}