
package com.examplo.sbelelk.biometria2;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String ETIQUETA_LOG = ">>>>";
    private static final int CODIGO_PERMISO_UBICACION = 100;
    private static final int CODIGO_PERMISOS_BLUETOOTH = 101;
    private static final int CODIGO_ACTIVAR_BLUETOOTH = 102;
    private TextView textoIdMedicion;
    private TextView textoFechaMedicion;
    private TextView textoTipoMedicion;
    private TextView textoValorMedicion;

    private long ultimoEnvioMs = 0;

    private static final long INTERVALO_MINIMO_ENVIO_MS = 20000;

    // Nombre del dispositivo que queremos buscar.
    private static final String NOMBRE_DISPOSITIVO = "GTI-Soufiane";

    private BluetoothAdapter adaptadorBluetooth;
    private BluetoothLeScanner elEscanner;
    private ScanCallback callbackDelEscaneo;

    // null: buscar todos los dispositivos.
    // Otro valor: buscar solamente ese nombre.
    private String nombreBuscado = null;

    private boolean escaneando = false;

    // Lógica fake para comunicarnos con el servidor REST.
    private LogicaNegocioFake logicaNegocioFake;

    // --------------------------------------------------------------
    // Se ejecuta al abrir la aplicación.
    // --------------------------------------------------------------





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(ETIQUETA_LOG, "Aplicación iniciada");

        inicializarBlueTooth();

        // Inicializamos la lógica fake.
        logicaNegocioFake = new LogicaNegocioFake();

        // Referencias a los textos de la pantalla.
        textoIdMedicion = findViewById(
                R.id.textoIdMedicion
        );

        textoFechaMedicion = findViewById(
                R.id.textoFechaMedicion
        );

        textoTipoMedicion = findViewById(
                R.id.textoTipoMedicion
        );

        textoValorMedicion = findViewById(
                R.id.textoValorMedicion
        );
    }



    // --------------------------------------------------------------
    // Inicializa Bluetooth sin utilizar permisos incompatibles.
    // --------------------------------------------------------------
    private void inicializarBlueTooth() {

        adaptadorBluetooth = BluetoothAdapter.getDefaultAdapter();

        if (adaptadorBluetooth == null) {
            Log.e(ETIQUETA_LOG,
                    "Este dispositivo no tiene Bluetooth");
            return;
        }

        Log.d(ETIQUETA_LOG,
                "Bluetooth disponible");

        if (adaptadorBluetooth.isEnabled()) {
            Log.d(ETIQUETA_LOG,
                    "Bluetooth activado");
        } else {
            Log.d(ETIQUETA_LOG,
                    "Bluetooth desactivado");
        }
    }

    // --------------------------------------------------------------
    // Comprueba los permisos necesarios para buscar dispositivos.
    // --------------------------------------------------------------
    private boolean comprobarPermisos() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            // Android 12 o superior.
            List<String> permisos = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED) {

                permisos.add(Manifest.permission.BLUETOOTH_SCAN);
            }

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED) {

                permisos.add(Manifest.permission.BLUETOOTH_CONNECT);
            }

            if (!permisos.isEmpty()) {

                ActivityCompat.requestPermissions(
                        this,
                        permisos.toArray(new String[0]),
                        CODIGO_PERMISOS_BLUETOOTH
                );

                return false;
            }

        } else {

            // Android 7.1.2, 8, 9, 10 y 11.
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {

                Log.d(ETIQUETA_LOG,
                        "Solicitando permiso de ubicación");

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        CODIGO_PERMISO_UBICACION
                );

                return false;
            }
        }

        return true;
    }

    // --------------------------------------------------------------
    // Prepara el escaneo y comprueba Bluetooth y ubicación.
    // --------------------------------------------------------------
    private void prepararEscaneo(String nombre) {

        nombreBuscado = nombre;

        if (!comprobarPermisos()) {
            return;
        }

        if (adaptadorBluetooth == null) {
            Log.e(ETIQUETA_LOG,
                    "Bluetooth no disponible");
            return;
        }

        if (!adaptadorBluetooth.isEnabled()) {

            Log.d(ETIQUETA_LOG,
                    "Solicitando activar Bluetooth");

            Intent intent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE
            );

            startActivityForResult(
                    intent,
                    CODIGO_ACTIVAR_BLUETOOTH
            );

            return;
        }

        // En Android 7 es necesario tener la ubicación activada.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {

            android.location.LocationManager ubicacion =
                    (android.location.LocationManager)
                            getSystemService(LOCATION_SERVICE);

            boolean ubicacionActivada =
                    ubicacion != null &&
                            (ubicacion.isProviderEnabled(
                                    android.location.LocationManager.GPS_PROVIDER)
                                    || ubicacion.isProviderEnabled(
                                    android.location.LocationManager.NETWORK_PROVIDER));

            if (!ubicacionActivada) {

                Log.e(ETIQUETA_LOG,
                        "Debes activar la ubicación del teléfono");

                new AlertDialog.Builder(this)
                        .setTitle("Ubicación desactivada")
                        .setMessage(
                                "Activa la ubicación para buscar dispositivos BLE."
                        )
                        .setPositiveButton(
                                "Abrir ajustes",
                                (dialog, which) -> startActivity(
                                        new Intent(
                                                Settings.ACTION_LOCATION_SOURCE_SETTINGS
                                        )
                                )
                        )
                        .setNegativeButton("Cancelar", null)
                        .show();

                return;
            }
        }

        iniciarEscaneo();
    }

    // --------------------------------------------------------------
    // Inicia el escaneo BLE.
    // --------------------------------------------------------------
    private void iniciarEscaneo() {

        // Detiene una búsqueda anterior.
        detenerBusquedaDispositivosBTLE();

        elEscanner = adaptadorBluetooth.getBluetoothLeScanner();

        if (elEscanner == null) {

            Log.e(ETIQUETA_LOG,
                    "No se ha podido obtener el escáner BLE");

            return;
        }

        callbackDelEscaneo = new ScanCallback() {

            @Override
            public void onScanResult(
                    int callbackType,
                    ScanResult resultado
            ) {

                Log.d(ETIQUETA_LOG,
                        "Dispositivo BLE detectado");

                mostrarInformacionDispositivoBTLE(resultado);
            }

            @Override
            public void onBatchScanResults(
                    List<ScanResult> resultados
            ) {

                for (ScanResult resultado : resultados) {
                    mostrarInformacionDispositivoBTLE(resultado);
                }
            }

            @Override
            public void onScanFailed(int errorCode) {

                Log.e(ETIQUETA_LOG,
                        "Error de escaneo BLE: " + errorCode);

                escaneando = false;
                callbackDelEscaneo = null;
            }
        };

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(
                        ScanSettings.SCAN_MODE_LOW_LATENCY
                )
                .build();

        List<ScanFilter> filtros = new ArrayList<>();

        if (nombreBuscado != null) {

            filtros.add(
                    new ScanFilter.Builder()
                            .setDeviceName(nombreBuscado)
                            .build()
            );

            Log.d(ETIQUETA_LOG,
                    "Buscando dispositivo: " + nombreBuscado);

        } else {

            Log.d(ETIQUETA_LOG,
                    "Buscando todos los dispositivos BLE");
        }

        try {

            elEscanner.startScan(
                    filtros,
                    settings,
                    callbackDelEscaneo
            );

            escaneando = true;

            Log.d(ETIQUETA_LOG,
                    "Escaneo BLE solicitado correctamente");

        } catch (SecurityException e) {

            Log.e(ETIQUETA_LOG,
                    "Error de permisos Bluetooth", e);

            escaneando = false;
            callbackDelEscaneo = null;

        } catch (IllegalStateException e) {

            Log.e(ETIQUETA_LOG,
                    "Bluetooth no está preparado", e);

            escaneando = false;
            callbackDelEscaneo = null;
        }
    }

    // --------------------------------------------------------------
    // Muestra los datos del dispositivo detectado.
    // --------------------------------------------------------------
    private void mostrarInformacionDispositivoBTLE(
            ScanResult resultado
    ) {

        if (resultado == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }

        BluetoothDevice dispositivo = resultado.getDevice();

        Log.d(ETIQUETA_LOG,
                "------------------------------------");

        Log.d(ETIQUETA_LOG,
                "DISPOSITIVO BLE DETECTADO");

        if (dispositivo != null) {

            try {

                Log.d(ETIQUETA_LOG,
                        "Nombre: " + dispositivo.getName());

                Log.d(ETIQUETA_LOG,
                        "MAC: " + dispositivo.getAddress());

            } catch (SecurityException e) {

                Log.e(ETIQUETA_LOG,
                        "Sin permiso para leer datos Bluetooth", e);
            }
        }

        Log.d(ETIQUETA_LOG,
                "RSSI: " + resultado.getRssi());

        if (resultado.getScanRecord() != null) {

            byte[] bytes =
                    resultado.getScanRecord().getBytes();

            if (bytes != null) {

                Log.d(ETIQUETA_LOG,
                        "Trama BLE: " +
                                Utilidades.bytesToHexString(bytes));

                // NUEVO: interpretamos la trama iBeacon.
                mostrarDatosIBeacon(bytes);
            }
        }

        Log.d(ETIQUETA_LOG,
                "------------------------------------");
    }

    // --------------------------------------------------------------
    // NUEVO: busca una trama iBeacon y muestra sus datos.
    // --------------------------------------------------------------

    private void mostrarDatosIBeacon(byte[] bytes) {

        if (bytes == null) {
            return;
        }

        // Recorremos los bloques del anuncio BLE.
        int posicion = 0;

        while (posicion < bytes.length) {

            int longitud = bytes[posicion] & 0xFF;

            if (longitud == 0) {
                break;
            }

            int fin = posicion + longitud + 1;

            // Evitamos leer fuera del array.
            if (fin > bytes.length) {
                break;
            }

            int tipo = bytes[posicion + 1] & 0xFF;

            // 0xFF indica datos del fabricante.
            if (tipo == 0xFF && longitud >= 26) {

                int inicio = posicion + 2;

                // Comprobamos que sea una trama iBeacon.
                boolean esIBeacon =
                        (bytes[inicio] & 0xFF) == 0x4C &&
                                (bytes[inicio + 1] & 0xFF) == 0x00 &&
                                (bytes[inicio + 2] & 0xFF) == 0x02 &&
                                (bytes[inicio + 3] & 0xFF) == 0x15;

                if (esIBeacon) {

                    // Construimos los 30 bytes que necesita
                    // nuestra clase TramaIBeacon.
                    byte[] trama = new byte[30];

                    trama[0] = 0x02;
                    trama[1] = 0x01;
                    trama[2] = 0x06;
                    trama[3] = 0x1A;
                    trama[4] = (byte) 0xFF;

                    System.arraycopy(
                            bytes,
                            inicio,
                            trama,
                            5,
                            25
                    );

                    TramaIBeacon ibeacon =
                            new TramaIBeacon(trama);

                    // Obtenemos el UUID.
                    String uuid = new String(
                            ibeacon.getUUID(),
                            java.nio.charset.StandardCharsets.US_ASCII
                    );

                    // Solo aceptamos los iBeacon de nuestro proyecto.
                    if (!uuid.equals("EPSG-GTI-PROY-3A")) {

                        Log.d(ETIQUETA_LOG,
                                "iBeacon de otro proyecto. No se envía.");

                        return;
                    }

                    // Obtenemos major y minor.
                    byte[] bytesMajor = ibeacon.getMajor();
                    byte[] bytesMinor = ibeacon.getMinor();

                    int major =
                            ((bytesMajor[0] & 0xFF) << 8) |
                                    (bytesMajor[1] & 0xFF);

                    int minor =
                            ((bytesMinor[0] & 0xFF) << 8) |
                                    (bytesMinor[1] & 0xFF);

                    Log.d(ETIQUETA_LOG,
                            "===== DATOS IBEACON =====");

                    Log.d(ETIQUETA_LOG, "UUID: " + uuid);
                    Log.d(ETIQUETA_LOG, "Major: " + major);
                    Log.d(ETIQUETA_LOG, "Minor: " + minor);
                    Log.d(ETIQUETA_LOG,
                            "TxPower: " + ibeacon.getTxPower());

                    // --------------------------------------------------
                    // Controlamos el tiempo entre envíos.
                    // --------------------------------------------------
                    long ahora =
                            android.os.SystemClock.elapsedRealtime();

                    if (ultimoEnvioMs != 0 &&
                            ahora - ultimoEnvioMs <
                                    INTERVALO_MINIMO_ENVIO_MS) {

                        Log.d(ETIQUETA_LOG,
                                "Esperando siguiente envío BLE");

                        return;
                    }

                    // Guardamos el momento del envío.
                    ultimoEnvioMs = ahora;

                    // --------------------------------------------------
                    // Creamos la medición con el minor recibido.
                    // --------------------------------------------------
                    java.text.SimpleDateFormat formato =
                            new java.text.SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm:ss",
                                    java.util.Locale.getDefault()
                            );

                    String fecha =
                            formato.format(new java.util.Date());

                    Medicion medicion = new Medicion(
                            fecha,
                            "O3",
                            minor
                    );

                    // --------------------------------------------------
                    // Enviamos la medición al servidor REST.
                    // --------------------------------------------------
                    logicaNegocioFake.guardarMedicion(medicion);

                    Log.d(ETIQUETA_LOG,
                            "MEDICIÓN ENVIADA AL SERVIDOR");

                    Log.d(ETIQUETA_LOG,
                            "Fecha: " + fecha);

                    Log.d(ETIQUETA_LOG,
                            "Tipo: O3");

                    Log.d(ETIQUETA_LOG,
                            "Valor enviado: " + minor);

                    return;
                }
            }

            posicion = fin;
        }

        Log.d(ETIQUETA_LOG,
                "No se encontró una trama iBeacon completa");
    }


    // --------------------------------------------------------------
    // Botón 1: buscar todos los dispositivos BLE.
    // --------------------------------------------------------------
    public void botonBuscarDispositivosBTLEPulsado(View v) {

        Log.d(ETIQUETA_LOG,
                "Botón BUSCAR pulsado");

        prepararEscaneo(null);
    }

    // --------------------------------------------------------------
    // Botón 2: detener el escaneo BLE.
    // --------------------------------------------------------------
    public void botonDetenerBusquedaDispositivosBTLEPulsado(
            View v
    ) {

        Log.d(ETIQUETA_LOG,
                "Botón DETENER pulsado");

        detenerBusquedaDispositivosBTLE();
    }

    // --------------------------------------------------------------
    // Botón 3: buscar nuestro dispositivo BLE.
    // --------------------------------------------------------------
    public void botonBuscarNuestroDispositivoBTLEPulsado(
            View v
    ) {

        Log.d(ETIQUETA_LOG,
                "Botón BUSCAR NUESTRO DISPOSITIVO pulsado");

        prepararEscaneo(NOMBRE_DISPOSITIVO);
    }

    // --------------------------------------------------------------
    // Detiene la búsqueda de dispositivos BLE.
    // --------------------------------------------------------------
    private void detenerBusquedaDispositivosBTLE() {

        if (elEscanner == null ||
                callbackDelEscaneo == null) {

            return;
        }

        try {

            elEscanner.stopScan(callbackDelEscaneo);

            Log.d(ETIQUETA_LOG,
                    "Escaneo BLE detenido");

        } catch (SecurityException e) {

            Log.e(ETIQUETA_LOG,
                    "No se pudo detener el escaneo", e);

        } finally {

            escaneando = false;
            callbackDelEscaneo = null;
        }
    }

    // --------------------------------------------------------------
    // Recibe los resultados de los permisos solicitados.
    // --------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == CODIGO_PERMISO_UBICACION ||
                requestCode == CODIGO_PERMISOS_BLUETOOTH) {

            boolean concedidos = grantResults.length > 0;

            for (int resultado : grantResults) {

                if (resultado != PackageManager.PERMISSION_GRANTED) {
                    concedidos = false;
                }
            }

            if (concedidos) {

                Log.d(ETIQUETA_LOG,
                        "Permisos concedidos");

                prepararEscaneo(nombreBuscado);

            } else {

                Log.e(ETIQUETA_LOG,
                        "Permisos denegados");
            }
        }
    }

    // --------------------------------------------------------------
    // Resultado de la solicitud para activar Bluetooth.
    // --------------------------------------------------------------
    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode == CODIGO_ACTIVAR_BLUETOOTH) {

            if (resultCode == RESULT_OK) {

                Log.d(ETIQUETA_LOG,
                        "Bluetooth activado");

                prepararEscaneo(nombreBuscado);

            } else {

                Log.e(ETIQUETA_LOG,
                        "El usuario no activó Bluetooth");
            }
        }
    }

    // --------------------------------------------------------------
    // Detiene el escaneo al cerrar la actividad.
    // --------------------------------------------------------------
    @Override
    protected void onDestroy() {

        detenerBusquedaDispositivosBTLE();

        super.onDestroy();
    }


    // --------------------------------------------------------------
// Botón 4: recuperar la última medición del servidor.
// --------------------------------------------------------------
    public void botonActualizarMedicionPulsado(View v) {

        Log.d(ETIQUETA_LOG,
                "Botón ACTUALIZAR MEDICIÓN pulsado");

        textoValorMedicion.setText("Consultando servidor...");

        logicaNegocioFake.recuperarMedicion(
                (medicion, error) -> {

                    // Actualizamos la pantalla desde el hilo principal.
                    runOnUiThread(() -> {

                        if (error != null) {

                            textoValorMedicion.setText(
                                    "No se pudo recuperar la medición"
                            );

                            Log.e(ETIQUETA_LOG, error);
                            return;
                        }

                        if (medicion == null) {
                            return;
                        }

                        textoIdMedicion.setText(
                                "ID: " + medicion.getId()
                        );

                        textoFechaMedicion.setText(
                                "Fecha: " + medicion.getFecha()
                        );

                        textoTipoMedicion.setText(
                                "Tipo: " + medicion.getTipo()
                        );

                        textoValorMedicion.setText(
                                "Valor: " + medicion.getValor()
                        );

                        Log.d(ETIQUETA_LOG,
                                "Medición mostrada en pantalla");
                    });
                }
        );
    }

}
