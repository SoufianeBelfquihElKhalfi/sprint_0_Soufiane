
 // -*- mode: c++ -*-

// --------------------------------------------------------------
// Archivo: Publicador.h
//
// Descripción:
// Gestiona la publicación de mediciones mediante Bluetooth LE
// utilizando anuncios con formato iBeacon.
//
// Permite publicar mediciones de CO2 y temperatura.
// Utiliza el campo Major para identificar el tipo de medición
// y el contador, y el campo Minor para transmitir el valor.
//
// Autor original: Jordi Bataller i Mascarell
// Adaptación: Soufiane Belfquih El Khalfi
// Proyecto: Biometría y Medio Ambiente - Sprint 0
// Aportación: Publicación de mediciones ficticias por BLE.
//
// Fecha: 28/09/2026
// Copyright: Soufiane
// --------------------------------------------------------------

// --------------------------------------------------------------
// Jordi Bataller i Mascarell
// --------------------------------------------------------------

#ifndef PUBLICADOR_H_INCLUIDO
#define PUBLICADOR_H_INCLUIDO

// --------------------------------------------------------------
// Clase: Publicador
//
// Diseño lógico:
//
// MedicionesID = { CO2, TEMPERATURA, RUIDO }
//
// Tipos de medición:
// CO2 = 11
// TEMPERATURA = 12
// RUIDO = 13
//
// Atributos:
// beaconUUID: [N]_16
// laEmisora: EmisoraBLE
// RSSI: Z
//
// Gestiona una emisora Bluetooth LE para publicar
// mediciones utilizando anuncios con formato iBeacon.
// --------------------------------------------------------------

// --------------------------------------------------------------
// --------------------------------------------------------------
class Publicador {

  // ............................................................
  // ............................................................
private:

  uint8_t beaconUUID[16] = { 
	'E', 'P', 'S', 'G', '-', 'G', 'T', 'I', 
	'-', 'P', 'R', 'O', 'Y', '-', '3', 'A'
	};

  // ............................................................
  // ............................................................
public:
  EmisoraBLE laEmisora {
	"GTI-Soufiane", //  nombre emisora
	  0x004c, // fabricanteID (Apple)
	  4 // txPower
	  };
  
  const int RSSI = -53; // por poner algo, de momento no lo uso

  // ............................................................
  // ............................................................
public:

  // --------------------------------------------------------------
  // Enumeración: MedicionesID
  //
  // Diseño lógico:
  // MedicionesID = { CO2, TEMPERATURA, RUIDO }
  //
  // Identifica los tipos de medición que puede publicar
  // el sistema mediante Bluetooth LE.
  // --------------------------------------------------------------
  // ............................................................
  // ............................................................
  enum MedicionesID  {
	CO2 = 11,
	TEMPERATURA = 12,
	RUIDO = 13
  };

  // --------------------------------------------------------------
  // Constructor: Publicador()
  //
  // Diseño lógico:
  // Publicador()
  //
  // Construye el objeto publicador.
  // La emisora Bluetooth no se enciende en el constructor,
  // sino posteriormente mediante encenderEmisora().
  // --------------------------------------------------------------
  // ............................................................
  // ............................................................
  Publicador( ) {
	// ATENCION: no hacerlo aquí. (*this).laEmisora.encenderEmisora();
	// Pondremos un método para llamarlo desde el setup() más tarde
  } // ()

  // --------------------------------------------------------------
  // Función: encenderEmisora()
  //
  // Diseño lógico:
  // encenderEmisora()
  //
  // Inicializa la emisora Bluetooth LE utilizada
  // para publicar las mediciones iBeacon.
  // --------------------------------------------------------------
  // ............................................................
  // ............................................................
  void encenderEmisora() {
	(*this).laEmisora.encenderEmisora();
  } // ()

  // --------------------------------------------------------------
  // Función: publicarCO2()
  //
  // Diseño lógico:
  // valorCO2: Z, contador: N, tiempoEspera: N
  //     --> publicarCO2()
  //
  // Construye el campo Major utilizando el identificador
  // de CO2 y el contador de publicaciones.
  //
  // Utiliza el valor de CO2 como campo Minor.
  // Emite el anuncio, espera el tiempo indicado
  // y finalmente detiene la emisión Bluetooth.
  // --------------------------------------------------------------
  // ............................................................
  // ............................................................
  void publicarCO2( int16_t valorCO2, uint8_t contador,
					long tiempoEspera ) {

	//
	// 1. empezamos anuncio
	//
	uint16_t major = (MedicionesID::CO2 << 8) + contador;
	(*this).laEmisora.emitirAnuncioIBeacon( (*this).beaconUUID, 
											major,
											valorCO2, // minor
											(*this).RSSI // rssi
									);

	/*
	Globales::elPuerto.escribir( "   publicarCO2(): valor=" );
	Globales::elPuerto.escribir( valorCO2 );
	Globales::elPuerto.escribir( "   contador=" );
	Globales::elPuerto.escribir( contador );
	Globales::elPuerto.escribir( "   todo="  );
	Globales::elPuerto.escribir( major );
	Globales::elPuerto.escribir( "\n" );
	*/

	//
	// 2. esperamos el tiempo que nos digan
	//
	esperar( tiempoEspera );

	//
	// 3. paramos anuncio
	//
	(*this).laEmisora.detenerAnuncio();
	Bluefruit.Advertising.clearData();
Bluefruit.ScanResponse.clearData();
  } // ()

  // --------------------------------------------------------------
  // Función: publicarTemperatura()
  //
  // Diseño lógico:
  // valorTemperatura: Z, contador: N, tiempoEspera: N
  //     --> publicarTemperatura()
  //
  // Construye el Major combinando el identificador
  // de temperatura y el contador de publicaciones.
  //
  // Utiliza el valor de temperatura como Minor.
  // Emite el anuncio iBeacon durante el tiempo indicado.
  //
  // Finalmente detiene la emisión y limpia
  // los datos de publicidad Bluetooth.
  // --------------------------------------------------------------
  // ............................................................
  // ............................................................
  
void publicarTemperatura(int16_t valorTemperatura,
                        uint8_t contador,
                        long tiempoEspera) {

  // Construimos el major:
  // 8 bits superiores = tipo de medición (12).
  // 8 bits inferiores = contador.
  uint16_t major =
      (static_cast<uint16_t>(MedicionesID::TEMPERATURA) << 8)
      | contador;

  // Mostramos los valores que vamos a emitir.
  Globales::elPuerto.escribir("DEBUG PUBLICADOR - Contador: ");
  Globales::elPuerto.escribir((int) contador);
  Globales::elPuerto.escribir("\n");

  Globales::elPuerto.escribir("DEBUG PUBLICADOR - Major: ");
  Globales::elPuerto.escribir((int) major);
  Globales::elPuerto.escribir("\n");

  Globales::elPuerto.escribir("DEBUG PUBLICADOR - Minor: ");
  Globales::elPuerto.escribir((int) valorTemperatura);
  Globales::elPuerto.escribir("\n");

  // Emitimos el iBeacon con los valores actualizados.
  (*this).laEmisora.emitirAnuncioIBeacon(
      (*this).beaconUUID,
      major,
      valorTemperatura,
      (*this).RSSI
  );

  // Mantenemos el anuncio durante el tiempo indicado.
  esperar(tiempoEspera);

  // Detenemos la emisión y limpiamos los datos.
  (*this).laEmisora.detenerAnuncio();

  Bluefruit.Advertising.clearData();
  Bluefruit.ScanResponse.clearData();
}
 // ()
	
}; // class

// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
#endif
