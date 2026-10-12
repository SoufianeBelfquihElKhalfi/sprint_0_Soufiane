package com.examplo.sbelelk.biometria2;




// Representa una medición del proyecto de Biometría
// y Medio Ambiente.
public class Medicion {

    private String id;
    private String fecha;
    private String tipo;
    private double valor;

    // Constructor para crear una nueva medición.
    // El ID lo asigna la base de datos.
    public Medicion(String fecha, String tipo, double valor) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.valor = valor;
    }

    // Constructor para recuperar una medición del servidor.
    public Medicion(String id, String fecha, String tipo, double valor) {
        this.id = id;
        this.fecha = fecha;
        this.tipo = tipo;
        this.valor = valor;
    }

    public String getId() {
        return id;
    }

    public String getFecha() {
        return fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }
}
