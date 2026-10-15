
package com.examplo.sbelelk.biometria2;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LogicaNegocioFakeTest {

    // --------------------------------------------------------------
    // PRUEBA 1: Recuperar una medición del servidor.
    //
    // No modifica ningún registro de MySQL.
    // --------------------------------------------------------------
    @Test
    public void testRecuperarMedicion() throws Exception {

        LogicaNegocioFake logica =
                new LogicaNegocioFake();

        CountDownLatch espera = new CountDownLatch(1);

        AtomicReference<Medicion> resultado =
                new AtomicReference<>();

        AtomicReference<String> error =
                new AtomicReference<>();

        logica.recuperarMedicion((medicion, mensajeError) -> {

            resultado.set(medicion);
            error.set(mensajeError);

            espera.countDown();
        });

        // Esperamos hasta 20 segundos al servidor.
        boolean terminado = espera.await(
                20,
                TimeUnit.SECONDS
        );

        assertTrue(
                "El servidor no respondió a tiempo",
                terminado
        );

        assertNull(
                "La recuperación devolvió un error",
                error.get()
        );

        assertNotNull(
                "No se recibió ninguna medición",
                resultado.get()
        );
    }

    // --------------------------------------------------------------
    // PRUEBA 2: Comprobar los campos recuperados.
    //
    // No modifica ningún registro de MySQL.
    // --------------------------------------------------------------
    @Test
    public void testCamposMedicionRecuperada() throws Exception {

        LogicaNegocioFake logica =
                new LogicaNegocioFake();

        CountDownLatch espera = new CountDownLatch(1);

        AtomicReference<Medicion> resultado =
                new AtomicReference<>();

        logica.recuperarMedicion((medicion, error) -> {
            resultado.set(medicion);
            espera.countDown();
        });

        assertTrue(
                "Tiempo de espera agotado",
                espera.await(20, TimeUnit.SECONDS)
        );

        Medicion medicion = resultado.get();

        assertNotNull(medicion);
        assertNotNull(medicion.getId());
        assertNotNull(medicion.getFecha());
        assertNotNull(medicion.getTipo());
        assertFalse(medicion.getTipo().trim().isEmpty());
        assertTrue(Double.isFinite(medicion.getValor()));
    }
}
