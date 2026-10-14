
 // -*- mode: c++ -*-

// --------------------------------------------------------------
// Archivo: PuertoSerie.h
//
// Descripción:
// Gestiona la comunicación mediante el puerto serie
// de la placa Arduino.
//
// Permite inicializar la comunicación serie con una
// velocidad determinada, esperar a que esté disponible
// y escribir mensajes para consultar el funcionamiento.
//
// Autor original: Jordi Bataller i Mascarell
// Fecha original: 2019-07-07
//
// Integración: Soufiane Belfquih El Khalfi
// Proyecto: Biometría y Medio Ambiente - Sprint 0
// Aportación: Integración de mensajes de depuración
// y seguimiento de las publicaciones Bluetooth.
//
// Copyright: Soufiane
// --------------------------------------------------------------

// ----------------------------------------------------------
// Jordi Bataller i Mascarell
// 2019-07-07
// ----------------------------------------------------------

#ifndef PUERTO_SERIE_H_INCLUIDO
#define PUERTO_SERIE_H_INCLUIDO

// --------------------------------------------------------------
// Clase: PuertoSerie
//
// Diseño lógico:
//
// PuertoSerie = ( )
//
// Gestiona las operaciones de comunicación por puerto serie.
//
// Permite establecer la velocidad de comunicación,
// comprobar la disponibilidad del puerto y escribir
// diferentes tipos de datos.
//
// Utiliza el objeto Serial de Arduino.
// --------------------------------------------------------------

// ----------------------------------------------------------
// ----------------------------------------------------------
class PuertoSerie  {

public:

  // --------------------------------------------------------------
  // Constructor: PuertoSerie()
  //
  // Diseño lógico:
  // baudios: Z --> PuertoSerie()
  //
  // Inicializa la comunicación serie de Arduino
  // utilizando la velocidad recibida en baudios.
  //
  // No espera a que el puerto esté disponible.
  // --------------------------------------------------------------
  // .........................................................
  // .........................................................
  PuertoSerie (long baudios) {
	Serial.begin( baudios );
	// mejor no poner esto aquí: while ( !Serial ) delay(10);   
  } // ()

  // --------------------------------------------------------------
  // Función: esperarDisponible()
  //
  // Diseño lógico:
  // esperarDisponible()
  //
  // Comprueba si la comunicación serie está disponible.
  //
  // Mientras el puerto no esté preparado, realiza
  // esperas de 10 milisegundos.
  //
  // Finaliza cuando Serial está disponible.
  // --------------------------------------------------------------
  // .........................................................
  // .........................................................
  void esperarDisponible() {

	while ( !Serial ) {
	  delay(10);   
	}

  } // ()

  // --------------------------------------------------------------
  // Función: escribir()
  //
  // Diseño lógico:
  // mensaje: T --> escribir()
  //
  // Recibe un dato de tipo genérico T y lo escribe
  // mediante la comunicación serie de Arduino.
  //
  // Puede utilizarse con distintos tipos de datos,
  // como cadenas de texto y valores numéricos.
  //
  // No añade automáticamente un salto de línea.
  // --------------------------------------------------------------
  // .........................................................
  // .........................................................
  template<typename T>
  void escribir (T mensaje) {
	Serial.print( mensaje );
  } // ()
  
}; // class PuertoSerie

// ----------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------
#endif
