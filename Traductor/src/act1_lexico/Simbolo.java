package act1_lexico;
/*
Actividad 9: Analisis semantico gestion de la tabla de simbolos y tipos de datos
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

    public Simbolo(String id, String categoria, String tipoDato, String valor, String ambito, int linea, int columna, String tokenOriginador) {
        this.id = id;
        this.categoria = categoria;
        this.tipoDato = tipoDato;
        this.valor = (valor == null || valor.isEmpty()) ? "null" : valor;
        this.ambito = ambito;
        this.linea = linea;
        this.columna = columna;
        this.tokenOriginador = tokenOriginador;
    }

    // Getters para poder imprimir la tabla después
    public String getId() { return id; }
    public String getCategoria() { return categoria; }
    public String getTipoDato() { return tipoDato; }
    public String getValor() { return valor; }
    public String getAmbito() { return ambito; }
    public int getLinea() { return linea; }
    public int getColumna() { return columna; }
    public String getTokenOriginador() { return tokenOriginador; }
}