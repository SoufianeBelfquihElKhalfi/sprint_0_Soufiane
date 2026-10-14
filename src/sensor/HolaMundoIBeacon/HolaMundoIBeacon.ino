
 // -*-c++-*-

// --------------------------------------------------------------
// Archivo: HolaMundoIBeacon.ino
//
// Descripción:
// Programa principal de la placa Arduino para el proyecto
// de Biometría y Medio Ambiente.
//
// Inicializa los componentes y publica una medición ficticia
// mediante anuncios Bluetooth LE con formato iBeacon.
//
// La publicación se realiza cada 30 segundos.
//
// Autor original: Jordi Bataller i Mascarell
// Fecha original: 2019-07-07
//
// Adaptación: Soufiane Belfquih El Khalfi
// Proyecto: Biometría y Medio Ambiente - Sprint 0
// Aportación: Publicación periódica de una medición ficticia.
//
// Copyright: pendiente de verificar.
// --------------------------------------------------------------
// Jordi Bataller i Mascarell
// 2019-07-07
// --------------------------------------------------------------

#include <bluefruit.h>

#undef min
#undef max

#include "LED.h"
#include "PuertoSerie.h"

// --------------------------------------------------------------
// Variables globales.
//
// Se crean los objetos utilizados para controlar el LED
// y la comunicación mediante el puerto serie.
// --------------------------------------------------------------
namespace Globales {

  LED elLED(7);

  PuertoSerie elPuerto(115200);

};

#include "EmisoraBLE.h"
#include "Publicador.h"
#include "Medidor.h"

// --------------------------------------------------------------
// Objetos globales para la publicación BLE
// y la obtención de mediciones.
// --------------------------------------------------------------
namespace Globales {

  Publicador elPublicador;

  Medidor elMedidor;

};

// --------------------------------------------------------------
// Intervalo entre rondas de publicación: 30 segundos.
// --------------------------------------------------------------
const unsigned long INTERVALO_PUBLICACION_MS = 30000;

// --------------------------------------------------------------
// Función: inicializarPlaquita()
//
// Diseño lógico:
// inicializarPlaquita()
//
// Función reservada para la inicialización específica
// de la placa. Actualmente no realiza ninguna operación.
// --------------------------------------------------------------
void inicializarPlaquita() {

  // De momento nada.

}

// --------------------------------------------------------------
// Función: setup()
//
// Diseño lógico:
// setup()
//
// Inicializa la placa, enciende la emisora Bluetooth LE
// e inicializa el medidor.
//
// Realiza una espera inicial y comunica mediante
// el puerto serie que ha terminado la configuración.
// --------------------------------------------------------------
void setup() {

  inicializarPlaquita();

  Globales::elPublicador.encenderEmisora();

  Globales::elMedidor.iniciarMedidor();

  esperar(1000);

  Globales::elPuerto.escribir("---- setup(): fin ---- \n");

}

// --------------------------------------------------------------
// Función: lucecitas()
//
// Diseño lógico:
// lucecitas()
//
// Controla una secuencia de iluminación del LED
// para indicar el funcionamiento de la placa.
//
// La secuencia contiene varias activaciones del LED
// y pausas entre ellas.
// --------------------------------------------------------------
inline void lucecitas() {

  using namespace Globales;

  elLED.brillar(100);
  esperar(400);

  elLED.brillar(100);
  esperar(400);

  elLED.brillar(100);
  esperar(400);

  elLED.brillar(1000);
  esperar(1000);

}

// --------------------------------------------------------------
// Contador de rondas de publicación.
//
// Se incrementa en cada ejecución de loop().
// El contador también se utiliza para identificar
// las sucesivas publicaciones Bluetooth.
// --------------------------------------------------------------
namespace Loop {

  uint8_t cont = 0;

};

// --------------------------------------------------------------
// Función: loop()
//
// Diseño lógico:
// loop()
//
// Ejecuta periódicamente una ronda de publicación.
//
// 1. Registra el instante inicial de la ronda.
// 2. Incrementa el contador de publicaciones.
// 3. Ejecuta la secuencia de iluminación del LED.
// 4. Publica una medición ficticia de temperatura.
// 5. Detiene el anuncio Bluetooth.
// 6. Registra el final de la ronda por el puerto serie.
// 7. Espera el tiempo restante hasta completar 30 segundos.
//
// La función se ejecuta indefinidamente.
// --------------------------------------------------------------
void loop() {

  using namespace Loop;
  using namespace Globales;

  // Guardamos cuándo empieza esta ronda.
  unsigned long inicio = millis();

  cont++;

  elPuerto.escribir("\n---- loop(): empieza ");
  elPuerto.escribir(cont);
  elPuerto.escribir("\n");

  // Calculamos el major de temperatura (tipo 12).
uint16_t major = (12 << 8) | cont;


  // LED de funcionamiento.
  lucecitas();

  // ------------------------------------------------------------
  // Publicar CO2
  // ------------------------------------------------------------
 /* int valorCO2 = elMedidor.medirCO2();

  elPublicador.publicarCO2(
      valorCO2,
      cont,
      1000
  );
*/
  // ------------------------------------------------------------
  // Publicar temperatura (valor de prueba: 1234)
  // ------------------------------------------------------------
  int valorTemperatura = 4321;

  elPublicador.publicarTemperatura(
      valorTemperatura,
      cont,
      1000
  );

  // Detenemos el anuncio al terminar la publicación.
  elPublicador.laEmisora.detenerAnuncio();

  elPuerto.escribir("---- loop(): acaba ");
  elPuerto.escribir(cont);
  elPuerto.escribir("\n");

  // ------------------------------------------------------------
  // Esperar hasta completar los 30 segundos.
  // ------------------------------------------------------------
  unsigned long tiempoTranscurrido = millis() - inicio;

  if (tiempoTranscurrido < INTERVALO_PUBLICACION_MS) {

    esperar(INTERVALO_PUBLICACION_MS - tiempoTranscurrido);

  }

}
