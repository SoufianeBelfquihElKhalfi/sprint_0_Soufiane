package com.examplo.sbelelk.appbiometria;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Utilidades {
    public static byte[] stringToBytes(String texto) {
        return texto.getBytes(StandardCharsets.UTF_8);
    }

    public static UUID stringToUUID(String texto) {
        byte[] bytes = stringToBytes(texto);
        if (bytes.length != 16) throw new IllegalArgumentException("El texto debe ocupar 16 bytes UTF-8");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return new UUID(buffer.getLong(), buffer.getLong());
    }

    public static String uuidToString(UUID uuid) {
        return bytesToString(dosLongToBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));
    }

    public static String uuidToHexString(UUID uuid) {
        return bytesToHexString(dosLongToBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));
    }

    public static String bytesToString(byte[] bytes) {
        return bytes == null ? "" : new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] dosLongToBytes(long mas, long menos) {
        return ByteBuffer.allocate(16).putLong(mas).putLong(menos).array();
    }

    /** Entero sin signo en big endian; major y minor deben estar entre 0 y 65535. */
    public static int bytesToInt(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return 0;
        if (bytes.length > 4) throw new IllegalArgumentException("Máximo 4 bytes");
        int valor = 0;
        for (byte b : bytes) valor = (valor << 8) | (b & 0xff);
        return valor;
    }

    public static int bytesToIntOK(byte[] bytes) { return bytesToInt(bytes); }

    public static long bytesToLong(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return 0;
        if (bytes.length > 8) throw new IllegalArgumentException("Máximo 8 bytes");
        long valor = 0;
        for (byte b : bytes) valor = (valor << 8) | (b & 0xff);
        return valor;
    }

    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) return "";
        char[] hex = "0123456789abcdef".toCharArray();
        StringBuilder texto = new StringBuilder();
        for (byte b : bytes) {
            if (texto.length() > 0) texto.append(':');
            texto.append(hex[(b & 0xff) >>> 4]).append(hex[b & 0x0f]);
        }
        return texto.toString();
    }
}
