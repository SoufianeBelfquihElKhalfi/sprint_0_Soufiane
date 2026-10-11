package com.examplo.sbelelk.appbiometria;

import java.util.Arrays;

/** Lee un anuncio BLE completo; solo interpreta los bloques iBeacon válidos. */
public class TramaIBeacon {
    private byte[] prefijo = new byte[0];
    private byte[] uuid = new byte[0];
    private byte[] major = new byte[0];
    private byte[] minor = new byte[0];
    private byte txPower;
    private final byte[] losBytes;
    private byte[] advFlags = new byte[0];
    private byte[] advHeader = new byte[0];
    private byte[] companyID = new byte[0];
    private byte iBeaconType;
    private byte iBeaconLength;
    private boolean valido;

    public TramaIBeacon(byte[] bytes) {
        losBytes = bytes == null ? new byte[0] : bytes.clone();
        for (int indice = 0; indice < losBytes.length;) {
            int longitud = losBytes[indice] & 0xff;
            if (longitud == 0) break;
            // La longitud incluye el tipo, pero NO el propio byte de longitud.
            int fin = indice + 1 + longitud;
            if (fin > losBytes.length) break;
            int tipo = losBytes[indice + 1] & 0xff;
            if (tipo == 0x01 && longitud == 2) {
                advFlags = Arrays.copyOfRange(losBytes, indice, fin);
            }
            int datos = indice + 2;
            if (!valido && tipo == 0xff && longitud == 26
                    && (losBytes[datos] & 0xff) == 0x4c
                    && losBytes[datos + 1] == 0x00
                    && losBytes[datos + 2] == 0x02
                    && losBytes[datos + 3] == 0x15) {
                advHeader = Arrays.copyOfRange(losBytes, indice, datos);
                companyID = Arrays.copyOfRange(losBytes, datos, datos + 2);
                iBeaconType = losBytes[datos + 2];
                iBeaconLength = losBytes[datos + 3];
                int inicioUuid = datos + 4;
                prefijo = Arrays.copyOfRange(losBytes, 0, inicioUuid);
                uuid = Arrays.copyOfRange(losBytes, inicioUuid, inicioUuid + 16);
                major = Arrays.copyOfRange(losBytes, inicioUuid + 16, inicioUuid + 18);
                minor = Arrays.copyOfRange(losBytes, inicioUuid + 18, inicioUuid + 20);
                txPower = losBytes[inicioUuid + 20];
                valido = true;
            }
            indice = fin;
        }
    }

    public boolean esIBeacon() { return valido; }
    public byte[] getPrefijo() { return prefijo.clone(); }
    public byte[] getUUID() { return uuid.clone(); }
    public byte[] getMajor() { return major.clone(); }
    public byte[] getMinor() { return minor.clone(); }
    public byte getTxPower() { return txPower; }
    public byte[] getLosBytes() { return losBytes.clone(); }
    public byte[] getAdvFlags() { return advFlags.clone(); }
    public byte[] getAdvHeader() { return advHeader.clone(); }
    public byte[] getCompanyID() { return companyID.clone(); }
    public byte getiBeaconType() { return iBeaconType; }
    public byte getiBeaconLength() { return iBeaconLength; }
}
