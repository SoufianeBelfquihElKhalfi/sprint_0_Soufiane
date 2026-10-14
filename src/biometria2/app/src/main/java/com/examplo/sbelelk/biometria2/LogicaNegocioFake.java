
package com.examplo.sbelelk.biometria2;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

// --------------------------------------------------------------
// Archivo: LogicaNegocioFake.java
//
// Descripción:
// Implementa la lógica de negocio fake de la aplicación Android.
// Gestiona el envío y la recuperación de mediciones mediante
// peticiones HTTP a la API REST del servidor.
//
// Autor: Soufiane Belfquih El Khalfi
// Proyecto: Biometría y Medio Ambiente - Sprint 0
// Aportación: Comunicación entre Android y el servidor REST.
//
// Fecha: 03/10/2026
// Copyright (c) 2026 Soufiane Belfquih El Khalfi
// --------------------------------------------------------------

// --------------------------------------------------------------
// Clase: LogicaNegocioFake
//
// Encapsula las operaciones de comunicación con el servidor.
// Permite guardar mediciones mediante POST y recuperarlas
// mediante GET, sin realizar estas peticiones directamente
// desde la interfaz gráfica.
// --------------------------------------------------------------
public class LogicaNegocioFake {

    private static final String ETIQUETA_LOG = ">>>>";

    private static final String URL_SERVIDOR =
            "https://sbelelk.upv.edu.es/communication/servidor.php?ruta=";

    // --------------------------------------------------------------
    // Interfaz: ResultadoMedicion
    //
    // Diseño lógico:
    // medicion: Medicion, error: Text --> alRecuperar()
    //
    // Permite comunicar el resultado de una consulta REST
    // a la actividad principal de Android.
    // --------------------------------------------------------------
    public interface ResultadoMedicion {

        void alRecuperar(Medicion medicion, String error);

    }

    // --------------------------------------------------------------
    // Función: guardarMedicion()
    //
    // Diseño lógico:
    // medicion: Medicion --> guardarMedicion()
    //
    // Recibe una medición y la convierte a formato JSON.
    // Realiza una petición HTTP POST al servidor REST.
    // Registra la respuesta recibida o los posibles errores.
    //
    // La operación se ejecuta en un hilo secundario para
    // no bloquear la interfaz de Android.
    // --------------------------------------------------------------
    public void guardarMedicion(Medicion medicion) {

        new Thread(() -> {

            HttpURLConnection conexion = null;

            try {

                // Construimos el JSON de la medición.
                JSONObject json = new JSONObject();

                json.put("fecha", medicion.getFecha());
                json.put("tipo", medicion.getTipo());
                json.put("valor", medicion.getValor());
                Log.d(ETIQUETA_LOG,
                        "DEBUG POST - JSON enviado: " + json.toString());

                URL url = new URL(
                        URL_SERVIDOR + "/Guardamedicion"
                );

                conexion = (HttpURLConnection)
                        url.openConnection();

                conexion.setRequestMethod("POST");

                conexion.setRequestProperty(
                        "Content-Type",
                        "application/json; charset=UTF-8"
                );

                conexion.setConnectTimeout(5000);
                conexion.setReadTimeout(10000);
                conexion.setDoOutput(true);

                // Convertimos el JSON en bytes.
                byte[] datos = json.toString().getBytes(
                        StandardCharsets.UTF_8
                );

                // Enviamos los datos al servidor.
                try (OutputStream salida =
                             conexion.getOutputStream()) {

                    salida.write(datos);
                }

                // Recibimos la respuesta del servidor.
                int codigo = conexion.getResponseCode();

                String respuesta =
                        leerRespuesta(conexion, codigo);

                Log.d(ETIQUETA_LOG,
                        "POST /Guardamedicion - HTTP " + codigo);

                Log.d(ETIQUETA_LOG,
                        "Respuesta del servidor: " + respuesta);

            } catch (Exception e) {

                Log.e(ETIQUETA_LOG,
                        "Error al guardar la medición", e);

            } finally {

                if (conexion != null) {
                    conexion.disconnect();
                }
            }

        }).start();
    }

    // --------------------------------------------------------------
    // Función: recuperarMedicion()
    //
    // Diseño lógico:
    // recuperarMedicion()
    //
    // Solicita la recuperación de la última medición
    // sin proporcionar una función que reciba el resultado.
    // --------------------------------------------------------------
    public void recuperarMedicion() {

        recuperarMedicion(null);

    }

    // --------------------------------------------------------------
    // Función: recuperarMedicion()
    //
    // Diseño lógico:
    // recuperarMedicion() --> Medicion ∪ {null}
    //
    // Realiza una petición HTTP GET para recuperar
    // la última medición almacenada en el servidor.
    //
    // Convierte la respuesta JSON en un objeto Medicion.
    // Comunica el resultado o el error mediante el callback.
    //
    // La operación se ejecuta en un hilo secundario.
    // --------------------------------------------------------------
    public void recuperarMedicion(ResultadoMedicion callback) {

        new Thread(() -> {

            HttpURLConnection conexion = null;

            Medicion medicion = null;
            String error = null;

            try {

                URL url = new URL(
                        URL_SERVIDOR + "/recuperamedicion"
                );

                conexion = (HttpURLConnection)
                        url.openConnection();

                conexion.setRequestMethod("GET");

                conexion.setConnectTimeout(5000);
                conexion.setReadTimeout(10000);

                // Recibimos la respuesta del servidor.
                int codigo = conexion.getResponseCode();

                String respuesta =
                        leerRespuesta(conexion, codigo);

                Log.d(ETIQUETA_LOG,
                        "GET /recuperamedicion - HTTP " + codigo);

                Log.d(ETIQUETA_LOG,
                        "Respuesta del servidor: " + respuesta);

                // Si la petición ha funcionado,
                // convertimos el JSON en una Medicion.
                if (codigo == 200) {

                    JSONObject json = new JSONObject(respuesta);

                    medicion = new Medicion(
                            json.getString("id"),
                            json.getString("fecha"),
                            json.getString("tipo"),
                            json.getDouble("valor")
                    );

                    Log.d(ETIQUETA_LOG,
                            "Medición recuperada:");

                    Log.d(ETIQUETA_LOG,
                            "ID: " + medicion.getId());

                    Log.d(ETIQUETA_LOG,
                            "Fecha: " + medicion.getFecha());

                    Log.d(ETIQUETA_LOG,
                            "Tipo: " + medicion.getTipo());

                    Log.d(ETIQUETA_LOG,
                            "Valor: " + medicion.getValor());

                } else {

                    error = "Error HTTP " + codigo +
                            ": " + respuesta;

                    Log.e(ETIQUETA_LOG, error);
                }

            } catch (Exception e) {

                error = "Error al recuperar la medición: " +
                        e.getMessage();

                Log.e(ETIQUETA_LOG,
                        "Error al recuperar la medición", e);

            } finally {

                if (conexion != null) {
                    conexion.disconnect();
                }
            }

            // Devolvemos los datos a MainActivity.
            // El callback se ejecuta en un hilo secundario.
            // MainActivity debe usar runOnUiThread()
            // para actualizar los TextView.
            if (callback != null) {

                callback.alRecuperar(medicion, error);

            }

        }).start();
    }

    // --------------------------------------------------------------
    // Función: leerRespuesta()
    //
    // Diseño lógico:
    // conexion: ConexionHTTP, codigo: N
    //     --> leerRespuesta() --> Text
    //
    // Lee el contenido de la respuesta HTTP recibida.
    // Selecciona el flujo de entrada o de error según
    // el código de estado y devuelve el contenido como texto.
    // --------------------------------------------------------------
    private String leerRespuesta(
            HttpURLConnection conexion,
            int codigo
    ) throws Exception {

        InputStream entrada;

        if (codigo >= 200 && codigo < 300) {

            entrada = conexion.getInputStream();

        } else {

            entrada = conexion.getErrorStream();

        }

        if (entrada == null) {
            return "";
        }

        StringBuilder respuesta = new StringBuilder();

        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(
                        entrada,
                        StandardCharsets.UTF_8
                ))) {

            String linea;

            while ((linea = lector.readLine()) != null) {

                respuesta.append(linea);

            }
        }

        return respuesta.toString();
    }
}
