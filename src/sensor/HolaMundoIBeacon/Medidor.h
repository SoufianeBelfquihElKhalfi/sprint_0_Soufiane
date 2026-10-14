
 // -*- mode: c++ -*-

// --------------------------------------------------------------
// Archivo: Medidor.h
//
// Descripción:
// Implementa el componente encargado de obtener las mediciones
// del proyecto de Biometría y Medio Ambiente.
//
// En el Sprint 0, las mediciones son ficticias.
// No se realiza ninguna lectura de sensores físicos.
//
// Autor: pendiente de verificar en el código original.
// Integración: Soufiane Belfquih El Khalfi
// Proyecto: Biometría y Medio Ambiente - Sprint 0
// Aportación: Integración del medidor de valores ficticios.
//
// Fecha: 28/09/2026
// Copyright: Soufiane
// --------------------------------------------------------------

#ifndef MEDIDOR_H_INCLUIDO
#define MEDIDOR_H_INCLUIDO

// --------------------------------------------------------------
// Clase: Medidor
//
// Diseño lógico:
//
// Medidor = ( )
//
// Proporciona operaciones para inicializar el medidor
// y obtener valores numéricos de CO2 y temperatura.
//
// Actualmente devuelve valores constantes de prueba.
// --------------------------------------------------------------

// ------------------------------------------------------
// ------------------------------------------------------
class Medidor {

  // .....................................................
  // .....................................................
private:

public:


  // --------------------------------------------------------------
  // Constructor: Medidor()
  //
  // Diseño lógico:
  // Medidor()
  //
  // Construye un objeto Medidor.
  // Actualmente no realiza ninguna inicialización.
  // --------------------------------------------------------------
  // .....................................................
  // constructor
  // .....................................................
  Medidor(  ) {
  } // ()

  // --------------------------------------------------------------
  // Función: iniciarMedidor()
  //
  // Diseño lógico:
  // iniciarMedidor()
  //
  // Permite realizar la inicialización del medidor
  // después de construir el objeto.
  //
  // Actualmente no realiza ninguna operación.
  // --------------------------------------------------------------
  // .....................................................
  // .....................................................
  void iniciarMedidor() {
	// las cosas que no se puedan hacer en el constructor, if any
  } // ()

  // --------------------------------------------------------------
  // Función: medirCO2()
  //
  // Diseño lógico:
  // medirCO2() --> Z
  //
  // Devuelve un valor ficticio de CO2.
  //
  // En este Sprint 0 devuelve siempre 1234.
  // --------------------------------------------------------------
  // .....................................................
  // .....................................................
  int medirCO2() {
	return 1234;
  } // ()

  // --------------------------------------------------------------
  // Función: medirTemperatura()
  //
  // Diseño lógico:
  // medirTemperatura() --> Z
  //
  // Devuelve un valor ficticio de temperatura.
  //
  // En este Sprint 0 devuelve siempre -1234.
  // --------------------------------------------------------------
  // .....................................................
  // .....................................................
  int medirTemperatura() {
	return -1234; // qué frío !
  } // ()
	
}; // class

// ------------------------------------------------------
// ------------------------------------------------------
// ------------------------------------------------------
// ------------------------------------------------------
#endif
