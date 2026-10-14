
package com.examplo.sbelelk.biometria2;

import java.util.Arrays;

// --------------------------------------------------------------
// Archivo: TramaIBeacon.java
//
// Descripción:
// Representa una trama iBeacon recibida mediante Bluetooth LE.
//
// Permite separar los bytes de la trama para obtener el UUID,
// Major, Minor, TxPower y los campos de identificación.
//
// Autor original: Jordi Bataller i Mascarell
// Adaptación al proyecto: Soufiane Belfquih El Khalfi
// Proyecto: Biometría y Medio Ambiente - Sprint 0
// Aportación: Integración de la lectura de tramas iBeacon.
//
// Fecha original: 03/10/2026
// Copyright: pendiente de verificar.
// --------------------------------------------------------------

// --------------------------------------------------------------
// Clase: TramaIBeacon
//
// Diseño lógico:
//
// TramaIBeacon = (
//     prefijo: [N]_9,
//     uuid: [N]_16,
//     major: [N]_2,
//     minor: [N]_2,
//     txPower: Z,
//     losBytes: [N]_30,
//     advFlags: [N]_3,
//     advHeader: [N]_2,
//     companyID: [N]_2,
//     iBeaconType: N,
//     iBeaconLength: N
// )
//
// Los elementos de los arrays representan bytes.
// La clase extrae los distintos campos de una trama iBeacon.
// --------------------------------------------------------------

// -----------------------------------------------------------------------------------
// @author: Jordi Bataller i Mascarell
// -----------------------------------------------------------------------------------
public class TramaIBeacon {
    private byte[] prefijo = null; // 9 bytes
    private byte[] uuid = null; // 16 bytes
    private byte[] major = null; // 2 bytes
    private byte[] minor = null; // 2 bytes
    private byte txPower = 0; // 1 byte

    private byte[] losBytes;

    private byte[] advFlags = null; // 3 bytes
    private byte[] advHeader = null; // 2 bytes
    private byte[] companyID = new byte[2]; // 2 bytes
    private byte iBeaconType = 0 ; // 1 byte
    private byte iBeaconLength = 0 ; // 1 byte

    // -------------------------------------------------------------------------------
    // Función: getPrefijo()
    //
    // Diseño lógico:
    // getPrefijo() --> [N]_9
    //
    // Devuelve los nueve bytes del prefijo iBeacon.
    // -------------------------------------------------------------------------------
    public byte[] getPrefijo() {
        return prefijo;
    }

    // -------------------------------------------------------------------------------
    // Función: getUUID()
    //
    // Diseño lógico:
    // getUUID() --> [N]_16
    //
    // Devuelve los dieciséis bytes del UUID.
    // -------------------------------------------------------------------------------
    public byte[] getUUID() {
        return uuid;
    }

    // -------------------------------------------------------------------------------
    // Función: getMajor()
    //
    // Diseño lógico:
    // getMajor() --> [N]_2
    //
    // Devuelve los dos bytes del campo Major.
    // -------------------------------------------------------------------------------
    public byte[] getMajor() {
        return major;
    }

    // -------------------------------------------------------------------------------
    // Función: getMinor()
    //
    // Diseño lógico:
    // getMinor() --> [N]_2
    //
    // Devuelve los dos bytes del campo Minor.
    // -------------------------------------------------------------------------------
    public byte[] getMinor() {
        return minor;
    }

    // -------------------------------------------------------------------------------
    // Función: getTxPower()
    //
    // Diseño lógico:
    // getTxPower() --> Z
    //
    // Devuelve el valor de potencia de transmisión.
    // -------------------------------------------------------------------------------
    public byte getTxPower() {
        return txPower;
    }

    // -------------------------------------------------------------------------------
    // Función: getLosBytes()
    //
    // Diseño lógico:
    // getLosBytes() --> [N]_30
    //
    // Devuelve los bytes originales de la trama.
    // -------------------------------------------------------------------------------
    public byte[] getLosBytes() {
        return losBytes;
    }

    // -------------------------------------------------------------------------------
    // Función: getAdvFlags()
    //
    // Diseño lógico:
    // getAdvFlags() --> [N]_3
    //
    // Devuelve los tres bytes de las banderas BLE.
    // -------------------------------------------------------------------------------
    public byte[] getAdvFlags() {
        return advFlags;
    }

    // -------------------------------------------------------------------------------
    // Función: getAdvHeader()
    //
    // Diseño lógico:
    // getAdvHeader() --> [N]_2
    //
    // Devuelve los dos bytes de la cabecera del anuncio.
    // -------------------------------------------------------------------------------
    public byte[] getAdvHeader() {
        return advHeader;
    }

    // -------------------------------------------------------------------------------
    // Función: getCompanyID()
    //
    // Diseño lógico:
    // getCompanyID() --> [N]_2
    //
    // Devuelve los dos bytes identificadores del fabricante.
    // -------------------------------------------------------------------------------
    public byte[] getCompanyID() {
        return companyID;
    }

    // -------------------------------------------------------------------------------
    // Función: getiBeaconType()
    //
    // Diseño lógico:
    // getiBeaconType() --> N
    //
    // Devuelve el tipo de trama iBeacon.
    // -------------------------------------------------------------------------------
    public byte getiBeaconType() {
        return iBeaconType;
    }

    // -------------------------------------------------------------------------------
    // Función: getiBeaconLength()
    //
    // Diseño lógico:
    // getiBeaconLength() --> N
    //
    // Devuelve la longitud indicada en la cabecera iBeacon.
    // -------------------------------------------------------------------------------
    public byte getiBeaconLength() {
        return iBeaconLength;
    }

    // -------------------------------------------------------------------------------
    // Constructor: TramaIBeacon()
    //
    // Diseño lógico:
    // bytes: [N]_30 --> TramaIBeacon()
    //
    // Recibe los 30 bytes de una trama iBeacon.
    //
    // Separa el prefijo, UUID, Major, Minor y TxPower.
    // Después obtiene los campos internos del prefijo:
    // banderas, cabecera, fabricante, tipo y longitud.
    // -------------------------------------------------------------------------------
    public TramaIBeacon(byte[] bytes ) {
        this.losBytes = bytes;

        prefijo = Arrays.copyOfRange(losBytes, 0, 8+1 ); // 9 bytes
        uuid = Arrays.copyOfRange(losBytes, 9, 24+1 ); // 16 bytes
        major = Arrays.copyOfRange(losBytes, 25, 26+1 ); // 2 bytes
        minor = Arrays.copyOfRange(losBytes, 27, 28+1 ); // 2 bytes
        txPower = losBytes[ 29 ]; // 1 byte

        advFlags = Arrays.copyOfRange( prefijo, 0, 2+1 ); // 3 bytes
        advHeader = Arrays.copyOfRange( prefijo, 3, 4+1 ); // 2 bytes
        companyID = Arrays.copyOfRange( prefijo, 5, 6+1 ); // 2 bytes
        iBeaconType = prefijo[ 7 ]; // 1 byte
        iBeaconLength = prefijo[ 8 ]; // 1 byte

    } // ()
} // class
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
