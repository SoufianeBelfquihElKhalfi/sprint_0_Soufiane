
 // -*-c++-*-

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
// Variables globales
// --------------------------------------------------------------
namespace Globales {

  LED elLED(7);

  PuertoSerie elPuerto(115200);

};

#include "EmisoraBLE.h"
#include "Publicador.h"
#include "Medidor.h"

namespace Globales {

  Publicador elPublicador;

  Medidor elMedidor;

};

// --------------------------------------------------------------
// Intervalo entre rondas de publicación: 30 segundos.
// --------------------------------------------------------------
const unsigned long INTERVALO_PUBLICACION_MS = 30000;

// --------------------------------------------------------------
// Inicializar placa
// --------------------------------------------------------------
void inicializarPlaquita() {

  // De momento nada.

}

// --------------------------------------------------------------
// setup()
// --------------------------------------------------------------
void setup() {

  inicializarPlaquita();

  Globales::elPublicador.encenderEmisora();

  Globales::elMedidor.iniciarMedidor();

  esperar(1000);

  Globales::elPuerto.escribir("---- setup(): fin ---- \n");

}

// --------------------------------------------------------------
// LED de funcionamiento
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
// Contador
// --------------------------------------------------------------
namespace Loop {

  uint8_t cont = 0;

};

// --------------------------------------------------------------
// loop()
// Publica las mediciones cada 30 segundos indefinidamente.
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
