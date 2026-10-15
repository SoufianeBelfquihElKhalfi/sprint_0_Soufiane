
package com.examplo.sbelelk.biometria2;

import org.junit.Test;

import static org.junit.Assert.*;

public class MedicionTest {

    // --------------------------------------------------------------
    // PRUEBA 1: Crear una medición nueva sin ID.
    // --------------------------------------------------------------
    @Test
    public void testCrearMedicionSinId() {

        Medicion medicion = new Medicion(
                "2026-10-15 10:00:00",
                "temperatura",
                4321.0
        );

        assertNull(medicion.getId());
    }

    // --------------------------------------------------------------
    // PRUEBA 2: Comprobar la fecha.
    // --------------------------------------------------------------
    @Test
    public void testFecha() {

        Medicion medicion = new Medicion(
                "2026-10-15 10:00:00",
                "temperatura",
                4321.0
        );

        assertEquals(
                "2026-10-15 10:00:00",
                medicion.getFecha()
        );
    }

    // --------------------------------------------------------------
    // PRUEBA 3: Comprobar el tipo.
    // --------------------------------------------------------------
    @Test
    public void testTipo() {

        Medicion medicion = new Medicion(
                "2026-10-15 10:00:00",
                "temperatura",
                4321.0
        );

        assertEquals(
                "temperatura",
                medicion.getTipo()
        );
    }

    // --------------------------------------------------------------
    // PRUEBA 4: Comprobar el valor.
    // --------------------------------------------------------------
    @Test
    public void testValor() {

        Medicion medicion = new Medicion(
                "2026-10-15 10:00:00",
                "temperatura",
                4321.0
        );

        assertEquals(
                4321.0,
                medicion.getValor(),
                0.001
        );
    }

    // --------------------------------------------------------------
    // PRUEBA 5: Crear una medición recuperada con ID.
    // --------------------------------------------------------------
    @Test
    public void testCrearMedicionConId() {

        Medicion medicion = new Medicion(
                "25",
                "2026-10-15 10:00:00",
                "O3",
                1234.0
        );

        assertEquals("25", medicion.getId());
        assertEquals(
                "2026-10-15 10:00:00",
                medicion.getFecha()
        );
        assertEquals("O3", medicion.getTipo());
        assertEquals(
                1234.0,
                medicion.getValor(),
                0.001
        );
    }

    // --------------------------------------------------------------
    // PRUEBA 6: Comprobar un valor decimal.
    // --------------------------------------------------------------
    @Test
    public void testValorDecimal() {

        Medicion medicion = new Medicion(
                "2026-10-15 10:00:00",
                "temperatura",
                23.75
        );

        assertEquals(
                23.75,
                medicion.getValor(),
                0.001
        );
    }

    // --------------------------------------------------------------
    // PRUEBA 7: Comprobar un valor cero.
    // --------------------------------------------------------------
    @Test
    public void testValorCero() {

        Medicion medicion = new Medicion(
                "2026-10-15 10:00:00",
                "temperatura",
                0.0
        );

        assertEquals(
                0.0,
                medicion.getValor(),
                0.001
        );
    }
}
