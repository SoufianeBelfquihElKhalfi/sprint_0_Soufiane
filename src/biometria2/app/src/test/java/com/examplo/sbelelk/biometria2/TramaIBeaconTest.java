package com.examplo.sbelelk.biometria2;




import org.junit.Test;

import static org.junit.Assert.*;

public class TramaIBeaconTest {

    // Crea una trama de 30 bytes para las pruebas.
    private byte[] crearTrama() {

        byte[] bytes = new byte[30];

        // Prefijo de 9 bytes.
        bytes[0] = 0x02;
        bytes[1] = 0x01;
        bytes[2] = 0x06;
        bytes[3] = 0x1A;
        bytes[4] = (byte) 0xFF;
        bytes[5] = 0x4C;
        bytes[6] = 0x00;
        bytes[7] = 0x02;
        bytes[8] = 0x15;

        // UUID: 16 bytes.
        for (int i = 0; i < 16; i++) {
            bytes[9 + i] = (byte) (i + 1);
        }

        // Major = 1.
        bytes[25] = 0x00;
        bytes[26] = 0x01;

        // Minor = 4321 (0x10E1).
        bytes[27] = 0x10;
        bytes[28] = (byte) 0xE1;

        // TxPower.
        bytes[29] = (byte) 0xC5;

        return bytes;
    }

    @Test
    public void testLongitudTrama() {

        TramaIBeacon trama =
                new TramaIBeacon(crearTrama());

        assertEquals(30, trama.getLosBytes().length);
    }

    @Test
    public void testLongitudUUID() {

        TramaIBeacon trama =
                new TramaIBeacon(crearTrama());

        assertEquals(16, trama.getUUID().length);
    }

    @Test
    public void testMajor() {

        TramaIBeacon trama =
                new TramaIBeacon(crearTrama());

        assertArrayEquals(
                new byte[]{0x00, 0x01},
                trama.getMajor()
        );
    }

    @Test
    public void testMinor4321() {

        TramaIBeacon trama =
                new TramaIBeacon(crearTrama());

        assertArrayEquals(
                new byte[]{0x10, (byte) 0xE1},
                trama.getMinor()
        );

        byte[] minor = trama.getMinor();

        int valor = ((minor[0] & 0xFF) << 8)
                | (minor[1] & 0xFF);

        assertEquals(4321, valor);
    }

    @Test
    public void testTxPower() {

        TramaIBeacon trama =
                new TramaIBeacon(crearTrama());

        assertEquals(
                (byte) 0xC5,
                trama.getTxPower()
        );
    }

    @Test
    public void testIdentificadorFabricante() {

        TramaIBeacon trama =
                new TramaIBeacon(crearTrama());

        assertArrayEquals(
                new byte[]{0x4C, 0x00},
                trama.getCompanyID()
        );
    }

    @Test
    public void testTipoYLongitudIBeacon() {

        TramaIBeacon trama =
                new TramaIBeacon(crearTrama());

        assertEquals(0x02, trama.getiBeaconType());
        assertEquals(0x15, trama.getiBeaconLength());
    }
}
