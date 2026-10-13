// -*- mode: c++ -*-

// --------------------------------------------------------------
// Jordi Bataller i Mascarell
// --------------------------------------------------------------

#ifndef PUBLICADOR_H_INCLUIDO
#define PUBLICADOR_H_INCLUIDO

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

  // ............................................................
  // ............................................................
  enum MedicionesID  {
	CO2 = 11,
	TEMPERATURA = 12,
	RUIDO = 13
  };

  // ............................................................
  // ............................................................
  Publicador( ) {
	// ATENCION: no hacerlo aquí. (*this).laEmisora.encenderEmisora();
	// Pondremos un método para llamarlo desde el setup() más tarde
  } // ()

  // ............................................................
  // ............................................................
  void encenderEmisora() {
	(*this).laEmisora.encenderEmisora();
  } // ()

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
