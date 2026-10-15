
package com.examplo.sbelelk.biometria2;

// --------------------------------------------------------------
// Archivo: Medicion.java
//
// Descripción:
// Representa una medición del proyecto de Biometría
// y Medio Ambiente.
//
// Almacena el identificador, la fecha, el tipo y el valor
// de una medición.
//
// Autor: Soufiane Belfquih El Khalfi
// Proyecto: Biometría y Medio Ambiente - Sprint 0
// Aportación: Representación de las mediciones en Android.
//
// Fecha: 03/10/2026
// Copyright (c) 2026 Soufiane Belfquih El Khalfi
// --------------------------------------------------------------

// --------------------------------------------------------------
// Clase: Medicion
//
// Diseño lógico:
//
// Medicion = (
//     id: Text,
//     fecha: Text,
//     tipo: Text,
//     valor: R
// )
//
// Representa los datos de una medición.
// El identificador es asignado por la base de datos.
// --------------------------------------------------------------

// Representa una medición del proyecto de Biometría
// y Medio Ambiente.
public class Medicion {

    private String id;
    private String fecha;
    private String tipo;
    private double valor;

    // --------------------------------------------------------------
    // Constructor: Medicion()
    //
    // Diseño lógico:
    // fecha: Text, tipo: Text, valor: R --> Medicion()
    //
    // Crea una nueva medición con su fecha, tipo y valor.
    // El identificador se asignará al almacenarla en la
    // base de datos.
    // --------------------------------------------------------------
    // Constructor para crear una nueva medición.
    // El ID lo asigna la base de datos.
    public Medicion(String fecha, String tipo, double valor) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.valor = valor;
    }

    // --------------------------------------------------------------
    // Constructor: Medicion()
    //
    // Diseño lógico:
    // id: Text, fecha: Text, tipo: Text, valor: R
    //     --> Medicion()
    //
    // Crea un objeto con los datos de una medición
    // recuperada desde el servidor.
    // --------------------------------------------------------------
    // Constructor para recuperar una medición del servidor.
    public Medicion(String id, String fecha, String tipo, double valor) {
        this.id = id;
        this.fecha = fecha;
        this.tipo = tipo;
        this.valor = valor;
    }

    // --------------------------------------------------------------
    // Función: getId()
    //
    // Diseño lógico:
    // getId() --> Text
    //
    // Devuelve el identificador de la medición.
    // --------------------------------------------------------------
    public String getId() {
        return id;
    }

    // --------------------------------------------------------------
    // Función: getFecha()
    //
    // Diseño lógico:
    // getFecha() --> Text
    //
    // Devuelve la fecha y hora de la medición.
    // --------------------------------------------------------------
    public String getFecha() {
        return fecha;
    }

    // --------------------------------------------------------------
    // Función: getTipo()
    //
    // Diseño lógico:
    // getTipo() --> Text
    //
    // Devuelve el tipo de medición realizada.
    // --------------------------------------------------------------
    public String getTipo() {
        return tipo;
    }

    // --------------------------------------------------------------
    // Función: getValor()
    //
    // Diseño lógico:
    // getValor() --> R
    //
    // Devuelve el valor numérico de la medición.
    // --------------------------------------------------------------
    public double getValor() {
        return valor;
    }
}
