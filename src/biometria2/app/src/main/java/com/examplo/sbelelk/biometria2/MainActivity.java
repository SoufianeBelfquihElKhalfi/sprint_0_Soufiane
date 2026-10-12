
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

public class MainActivity extends AppCompatActivity {

    private static final String ETIQUETA_LOG = ">>>>";
    private static final int CODIGO_PERMISO_UBICACION = 100;
    private static final int CODIGO_PERMISOS_BLUETOOTH = 101;
    private static final int CODIGO_ACTIVAR_BLUETOOTH = 102;

    // Nombre del dispositivo que queremos buscar.
    private static final String NOMBRE_DISPOSITIVO = "GTI-Soufiane";

    private BluetoothAdapter adaptadorBluetooth;
    private BluetoothLeScanner elEscanner;
    private ScanCallback callbackDelEscaneo;

    // null: buscar todos los dispositivos.
    // Otro valor: buscar solamente ese nombre.
    private String nombreBuscado = null;

    private boolean escaneando = false;

    // --------------------------------------------------------------
    // Se ejecuta al abrir la aplicación.
    // --------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(ETIQUETA_LOG, "Aplicación iniciada");

        inicializarBlueTooth();
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
            }
        }

        Log.d(ETIQUETA_LOG,
                "------------------------------------");
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
}
