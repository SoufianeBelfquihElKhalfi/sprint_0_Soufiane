
 // -*- mode: c++ -*-

// --------------------------------------------------------------
// Archivo: LED.h
//
// Descripción:
// Gestiona el funcionamiento de un LED conectado
// a la placa Arduino.
//
// Permite encender, apagar, alternar el estado del LED
// y mantenerlo encendido durante un tiempo determinado.
//
// También proporciona una función auxiliar para realizar
// esperas utilizando la función delay() de Arduino.
//
// Autor original: Jordi Bataller i Mascarell
// Fecha original: 2019-07-07
//
// Integración: Soufiane Belfquih El Khalfi
// Proyecto: Biometría y Medio Ambiente - Sprint 0
// Aportación: Integración del control del LED de funcionamiento.
//
// Copyright: Soufiane
// --------------------------------------------------------------

#ifndef LED_H_INCLUIDO
#define LED_H_INCLUIDO

// ----------------------------------------------------------
// Jordi Bataller i Mascarell
// 2019-07-07
// ----------------------------------------------------------

// --------------------------------------------------------------
// Función: esperar()
//
// Diseño lógico:
// tiempo: Z --> esperar()
//
// Realiza una pausa durante el tiempo indicado
// en milisegundos utilizando delay() de Arduino.
//
// Durante esta espera se bloquea la ejecución
// del programa.
// --------------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------
void esperar (long tiempo) {
  delay (tiempo);
}

// --------------------------------------------------------------
// Clase: LED
//
// Diseño lógico:
//
// LED = (
//     numeroLED: Z,
//     encendido: B
// )
//
// Representa un LED conectado a un pin de la placa.
//
// Almacena el número del pin utilizado y un valor
// booleano que indica si el LED está encendido.
//
// Proporciona operaciones para controlar su estado.
// --------------------------------------------------------------

// ----------------------------------------------------------
// ----------------------------------------------------------
class LED {
private:
  int numeroLED;
  bool encendido;
public:

  // --------------------------------------------------------------
  // Constructor: LED()
  //
  // Diseño lógico:
  // numero: Z --> LED()
  //
  // Recibe el número del pin al que está conectado el LED.
  //
  // Configura el pin como salida y establece
  // inicialmente el estado del LED como apagado.
  // --------------------------------------------------------------
  // .........................................................
  // .........................................................
  LED (int numero)
	: numeroLED (numero), encendido(false)
  {
	pinMode(numeroLED, OUTPUT);
	apagar ();
  }

  // --------------------------------------------------------------
  // Función: encender()
  //
  // Diseño lógico:
  // encender()
  //
  // Establece el pin del LED en estado HIGH
  // y actualiza el estado interno a true.
  // --------------------------------------------------------------
  // .........................................................
  // .........................................................
  void encender () {
	digitalWrite(numeroLED, HIGH); 
	encendido = true;
  }

  // --------------------------------------------------------------
  // Función: apagar()
  //
  // Diseño lógico:
  // apagar()
  //
  // Establece el pin del LED en estado LOW
  // y actualiza el estado interno a false.
  // --------------------------------------------------------------
  // .........................................................
  // .........................................................
  void apagar () {
	  digitalWrite(numeroLED, LOW);
	  encendido = false;
  }

  // --------------------------------------------------------------
  // Función: alternar()
  //
  // Diseño lógico:
  // alternar()
  //
  // Comprueba el estado actual del LED.
  //
  // Si está encendido, lo apaga.
  // Si está apagado, lo enciende.
  // --------------------------------------------------------------
  // .........................................................
  // .........................................................
  void alternar () {
	if (encendido) {
	  apagar();
	} else {
	  encender ();
	}
  } // ()

  // --------------------------------------------------------------
  // Función: brillar()
  //
  // Diseño lógico:
  // tiempo: Z --> brillar()
  //
  // Enciende el LED y lo mantiene encendido
  // durante el número de milisegundos indicado.
  //
  // Una vez transcurrido ese tiempo, apaga el LED.
  // --------------------------------------------------------------
  // .........................................................
  // .........................................................
  void brillar (long tiempo) {
	encender ();
	esperar(tiempo); 
	apagar ();
  }
}; // class

// ----------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------
#endif
