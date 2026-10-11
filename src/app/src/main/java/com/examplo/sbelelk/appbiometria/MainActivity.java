package com.examplo.sbelelk.appbiometria;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// ------------------------------------------------------------------
// Tres acciones:
// 1. Buscar dispositivos BLE.
// 2. Detener la búsqueda.
// 3. Seleccionar un dispositivo y seguir sus anuncios.
//
// Seleccionar no establece una conexión GATT: filtra los anuncios
// recibidos para mostrar únicamente el dispositivo elegido.
// ------------------------------------------------------------------
public class MainActivity extends AppCompatActivity {

    private static final String ETIQUETA_LOG = ">>>>";
    private static final int CODIGO_PETICION_PERMISOS = 1122;

    private BluetoothLeScanner elEscanner;
    private ScanCallback callbackDelEscaneo;

    private TextView textoResultados;
    private TextView textoEstado;
    private TextView textoSeleccion;

    // Una entrada por dirección MAC: evita dispositivos duplicados.
    private final Map<String, ScanResult> dispositivos =
            new LinkedHashMap<>();

    private final Map<String, String> nombres =
            new LinkedHashMap<>();

    private final Map<String, Long> vistos =
            new LinkedHashMap<>();

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private String macFija;
    private String nombreFijo;

    // null significa mostrar todos los dispositivos.
    private String filtroActual;

    private Runnable accionPendiente;
    private boolean visible;
    private long ultimoInicio = -10000;

    // --------------------------------------------------------------
    // Recibe la respuesta a la solicitud de activar Bluetooth.
    // --------------------------------------------------------------
    private final ActivityResultLauncher<Intent> habilitarBluetooth =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    resultado -> {
                        if (resultado.getResultCode() == RESULT_OK) {
                            continuarAccion();
                        } else {
                            accionPendiente = null;
                            estado(
                                    "Bluetooth desactivado. "
                                            + "Actívalo para buscar."
                            );
                        }
                    }
            );

    // --------------------------------------------------------------
    // Al volver de Ajustes, comprueba de nuevo la ubicación.
    // --------------------------------------------------------------
    private final ActivityResultLauncher<Intent> habilitarUbicacion =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    resultado -> continuarAccion()
            );

    // --------------------------------------------------------------
    // Detiene la búsqueda si se apaga Bluetooth.
    // --------------------------------------------------------------
    private final BroadcastReceiver cambiosBluetooth =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int nuevoEstado = intent.getIntExtra(
                            BluetoothAdapter.EXTRA_STATE,
                            -1
                    );

                    if (nuevoEstado == BluetoothAdapter.STATE_OFF
                            || nuevoEstado
                            == BluetoothAdapter.STATE_TURNING_OFF) {

                        detenerBusquedaDispositivosBTLE();

                        estado(
                                "Bluetooth desactivado. "
                                        + "La búsqueda se ha detenido."
                        );
                    }
                }
            };

    // --------------------------------------------------------------
    // Actualiza la pantalla una vez por segundo durante el escaneo.
    // --------------------------------------------------------------
    private final Runnable refrescar = new Runnable() {
        @Override
        public void run() {
            actualizarResultados();

            if (callbackDelEscaneo != null) {
                handler.postDelayed(this, 1000);
            }
        }
    };

    // --------------------------------------------------------------
    // Bundle -> onCreate -> ()
    // Prepara la pantalla y recupera el dispositivo guardado.
    // --------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textoResultados = findViewById(R.id.textoResultados);
        textoEstado = findViewById(R.id.textoEstado);
        textoSeleccion = findViewById(R.id.textoSeleccion);

        macFija = getPreferences(MODE_PRIVATE)
                .getString("macFija", null);

        nombreFijo = getPreferences(MODE_PRIVATE)
                .getString("nombreFijo", "Sin nombre");

        actualizarSeleccion();

        registerReceiver(
                cambiosBluetooth,
                new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        );

        estado("Pulsa Buscar para descubrir dispositivos BLE cercanos.");
    }

    // --------------------------------------------------------------
    // View -> botonBuscarDispositivosBTLEPulsado -> ()
    // Primer botón: muestra todos los dispositivos BLE detectados.
    // --------------------------------------------------------------
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        prepararBusqueda(() -> iniciarEscaneo(null));
    }

    // --------------------------------------------------------------
    // View -> botonDetenerBusquedaDispositivosBTLEPulsado -> ()
    // Segundo botón: detiene la recepción y conserva los resultados.
    // --------------------------------------------------------------
    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        accionPendiente = null;

        detenerBusquedaDispositivosBTLE();

        estado(
                "Búsqueda detenida. "
                        + "Se conservan los resultados y la selección."
        );
    }

    // --------------------------------------------------------------
    // View -> botonBuscarNuestroDispositivoBTLEPulsado -> ()
    // Tercer botón: permite elegir un dispositivo de la lista.
    // --------------------------------------------------------------
    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {

        // Copia estable: los nuevos anuncios no cambian los índices
        // del diálogo mientras el usuario está eligiendo.
        List<String> direcciones =
                new ArrayList<>(dispositivos.keySet());

        // También permite volver a elegir el dispositivo guardado,
        // aunque todavía no haya anunciado en esta sesión.
        if (macFija != null && !direcciones.contains(macFija)) {
            direcciones.add(0, macFija);
        }

        if (direcciones.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Todavía no hay dispositivos")
                    .setMessage(
                            "Pulsa Buscar y espera a que aparezcan "
                                    + "dispositivos BLE. Después podrás elegir uno."
                    )
                    .setPositiveButton(
                            "Buscar",
                            (dialogo, boton) ->
                                    botonBuscarDispositivosBTLEPulsado(v)
                    )
                    .setNegativeButton("Cancelar", null)
                    .show();

            return;
        }

        String[] opciones = new String[direcciones.size()];

        for (int i = 0; i < opciones.length; i++) {
            String mac = direcciones.get(i);
            String nombre = nombres.get(mac);

            if (nombre == null) {
                nombre = mac.equals(macFija)
                        ? nombreFijo
                        : "Sin nombre";
            }

            opciones[i] = nombre
                    + "\n" + mac
                    + (mac.equals(macFija) ? " · guardado" : "");
        }

        new AlertDialog.Builder(this)
                .setTitle("Selecciona el dispositivo que quieres seguir")
                .setItems(opciones, (dialogo, indice) -> {
                    macFija = direcciones.get(indice);

                    if (nombres.containsKey(macFija)) {
                        nombreFijo = nombres.get(macFija);
                    }

                    getPreferences(MODE_PRIVATE)
                            .edit()
                            .putString("macFija", macFija)
                            .putString("nombreFijo", nombreFijo)
                            .apply();

                    actualizarSeleccion();

                    prepararBusqueda(() -> iniciarEscaneo(macFija));
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // --------------------------------------------------------------
    // Runnable -> prepararBusqueda -> ()
    // Comprueba permisos, Bluetooth y ubicación antes de escanear.
    // --------------------------------------------------------------
    @SuppressLint("MissingPermission")
    private void prepararBusqueda(Runnable accion) {
        if (!visible) {
            return;
        }

        accionPendiente = accion;

        List<String> permisos = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permisos.add(Manifest.permission.BLUETOOTH_SCAN);
            permisos.add(Manifest.permission.BLUETOOTH_CONNECT);
        }

        // Se conserva ubicación para recibir también iBeacon.
        permisos.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permisos.add(Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> faltan = new ArrayList<>();

        for (String permiso : permisos) {
            if (ContextCompat.checkSelfPermission(this, permiso)
                    != PackageManager.PERMISSION_GRANTED) {
                faltan.add(permiso);
            }
        }

        if (!faltan.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    faltan.toArray(new String[0]),
                    CODIGO_PETICION_PERMISOS
            );

            estado("Concede los permisos para buscar dispositivos BLE.");
            return;
        }

        BluetoothManager gestor =
                (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        BluetoothAdapter adaptador =
                gestor == null ? null : gestor.getAdapter();

        if (adaptador == null
                || !getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {

            accionPendiente = null;
            estado("Este dispositivo no dispone de Bluetooth Low Energy.");
            return;
        }

        try {
            if (!adaptador.isEnabled()) {
                habilitarBluetooth.launch(
                        new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                );
                return;
            }

            LocationManager ubicacion =
                    (LocationManager) getSystemService(LOCATION_SERVICE);

            if (ubicacion != null && !ubicacion.isLocationEnabled()) {
                new AlertDialog.Builder(this)
                        .setTitle("Activa la ubicación")
                        .setMessage(
                                "Activa Ubicación para que el teléfono "
                                        + "pueda detectar los anuncios BLE."
                        )
                        .setPositiveButton(
                                "Abrir ajustes",
                                (dialogo, boton) ->
                                        habilitarUbicacion.launch(
                                                new Intent(
                                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS
                                                )
                                        )
                        )
                        .setNegativeButton(
                                "Cancelar",
                                (dialogo, boton) -> {
                                    accionPendiente = null;
                                    estado(
                                            "Búsqueda cancelada: "
                                                    + "ubicación desactivada."
                                    );
                                }
                        )
                        .setOnCancelListener(
                                dialogo -> accionPendiente = null
                        )
                        .show();

                return;
            }

            elEscanner = adaptador.getBluetoothLeScanner();
            accionPendiente = null;

            if (elEscanner == null) {
                estado("No se pudo obtener el escáner. Vuelve a intentarlo.");
            } else {
                accion.run();
            }

        } catch (SecurityException e) {
            accionPendiente = null;

            estado(
                    "Faltan permisos de Bluetooth. "
                            + "Revisa los permisos en Ajustes de la aplicación."
            );
        }
    }

    // --------------------------------------------------------------
    // () -> continuarAccion -> ()
    // Retoma la acción después de conceder permisos o activar Bluetooth.
    // --------------------------------------------------------------
    private void continuarAccion() {
        Runnable pendiente = accionPendiente;
        accionPendiente = null;

        if (pendiente != null) {
            prepararBusqueda(pendiente);
        }
    }

    // --------------------------------------------------------------
    // String (MAC o null) -> iniciarEscaneo -> ()
    // Mantiene una única sesión de escaneo.
    // --------------------------------------------------------------
    @SuppressLint("MissingPermission")
    private void iniciarEscaneo(String mac) {
        filtroActual = mac;

        // Si ya se está buscando, basta con cambiar qué se muestra.
        // No se inicia otro escaneo ni se pierde el callback anterior.
        if (callbackDelEscaneo != null) {
            mostrarModo();
            actualizarResultados();
            return;
        }

        long ahora = SystemClock.elapsedRealtime();

        // Evita iniciar demasiados escaneos en poco tiempo.
        if (ahora - ultimoInicio < 6500) {
            estado(
                    "Espera unos segundos antes de iniciar "
                            + "otra búsqueda (límite de Android)."
            );
            return;
        }

        ScanCallback callback = new ScanCallback() {

            @Override
            public void onScanResult(int tipo, ScanResult resultado) {
                runOnUiThread(() -> {
                    if (callbackDelEscaneo == this) {
                        mostrarInformacionDispositivoBTLE(resultado);
                    }
                });
            }

            @Override
            public void onBatchScanResults(List<ScanResult> resultados) {
                runOnUiThread(() -> {
                    if (callbackDelEscaneo != this) {
                        return;
                    }

                    for (ScanResult resultado : resultados) {
                        mostrarInformacionDispositivoBTLE(resultado);
                    }
                });
            }

            @Override
            public void onScanFailed(int codigo) {
                runOnUiThread(() -> {
                    if (callbackDelEscaneo != this) {
                        return;
                    }

                    detenerBusquedaDispositivosBTLE();

                    estado(
                            "No se pudo iniciar el escaneo (código "
                                    + codigo
                                    + "). Espera unos segundos y pulsa Buscar."
                    );
                });
            }
        };

        callbackDelEscaneo = callback;

        try {
            ultimoInicio = ahora;

            ScanSettings ajustes = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();

            elEscanner.startScan(null, ajustes, callback);

            mostrarModo();
            handler.post(refrescar);

        } catch (SecurityException | IllegalStateException e) {
            detenerBusquedaDispositivosBTLE();

            estado(
                    "No se pudo buscar. "
                            + "Comprueba Bluetooth y los permisos."
            );

            Log.e(ETIQUETA_LOG, "Error al iniciar el escaneo", e);
        }
    }

    // --------------------------------------------------------------
    // ScanResult -> mostrarInformacionDispositivoBTLE -> ()
    // Guarda el último anuncio del dispositivo.
    // --------------------------------------------------------------
    @SuppressLint("MissingPermission")
    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {
        if (resultado == null || resultado.getDevice() == null) {
            return;
        }

        try {
            String mac = resultado.getDevice().getAddress();

            // Cuando hay uno seleccionado, ignora los demás.
            if (filtroActual != null && !filtroActual.equals(mac)) {
                return;
            }

            ScanRecord registro = resultado.getScanRecord();

            String nombre = registro == null
                    ? null
                    : registro.getDeviceName();

            if (nombre == null) {
                nombre = resultado.getDevice().getName();
            }

            if (nombre != null) {
                nombres.put(mac, nombre);
            }

            if (!nombres.containsKey(mac)) {
                nombres.put(mac, "Sin nombre");
            }

            dispositivos.put(mac, resultado);
            vistos.put(mac, SystemClock.elapsedRealtime());

        } catch (SecurityException e) {
            detenerBusquedaDispositivosBTLE();

            estado(
                    "Permisos retirados. "
                            + "Pulsa Buscar para solicitarlos de nuevo."
            );
        }
    }

    // --------------------------------------------------------------
    // () -> actualizarResultados -> ()
    // Muestra una entrada por dispositivo, sin acumular duplicados.
    // --------------------------------------------------------------
    private void actualizarResultados() {
        StringBuilder texto = new StringBuilder();
        long ahora = SystemClock.elapsedRealtime();

        for (Map.Entry<String, ScanResult> entrada
                : dispositivos.entrySet()) {

            String mac = entrada.getKey();

            if (filtroActual != null && !filtroActual.equals(mac)) {
                continue;
            }

            ScanResult resultado = entrada.getValue();

            texto.append(nombres.get(mac))
                    .append("\nMAC: ")
                    .append(mac)
                    .append("\nRSSI: ")
                    .append(resultado.getRssi())
                    .append(" dBm")
                    .append("\nÚltimo anuncio: hace ")
                    .append((ahora - vistos.get(mac)) / 1000)
                    .append(" s");

            ScanRecord registro = resultado.getScanRecord();

            TramaIBeacon trama = new TramaIBeacon(
                    registro == null ? null : registro.getBytes()
            );

            // No todos los dispositivos BLE emiten iBeacon.
            if (trama.esIBeacon()) {
                texto.append("\nUUID: ")
                        .append(
                                Utilidades.bytesToHexString(trama.getUUID())
                        )
                        .append("\nMajor: ")
                        .append(
                                Utilidades.bytesToInt(trama.getMajor())
                        )
                        .append(" | Minor: ")
                        .append(
                                Utilidades.bytesToInt(trama.getMinor())
                        )
                        .append("\nTxPower: ")
                        .append(trama.getTxPower())
                        .append(" dBm");

            } else {
                texto.append("\nAnuncio BLE (no es iBeacon)");
            }

            texto.append("\n\n");
        }

        if (texto.length() > 0) {
            textoResultados.setText(texto.toString());

        } else if (filtroActual == null) {
            textoResultados.setText(
                    "Todavía no se han detectado dispositivos BLE."
            );

        } else {
            textoResultados.setText(
                    "Esperando anuncios del dispositivo seleccionado…"
            );
        }
    }

    // --------------------------------------------------------------
    // () -> detenerBusquedaDispositivosBTLE -> ()
    // Detiene el escaneo e invalida resultados pendientes.
    // --------------------------------------------------------------
    @SuppressLint("MissingPermission")
    private void detenerBusquedaDispositivosBTLE() {
        ScanCallback anterior = callbackDelEscaneo;

        callbackDelEscaneo = null;
        handler.removeCallbacks(refrescar);

        if (anterior != null && elEscanner != null) {
            try {
                elEscanner.stopScan(anterior);

            } catch (SecurityException | IllegalStateException e) {
                Log.w(
                        ETIQUETA_LOG,
                        "Escáner ya no disponible",
                        e
                );
            }
        }

        actualizarResultados();
    }

    // --------------------------------------------------------------
    // () -> actualizarSeleccion -> ()
    // --------------------------------------------------------------
    private void actualizarSeleccion() {
        if (macFija == null) {
            textoSeleccion.setText("Dispositivo fijo: ninguno");

        } else {
            textoSeleccion.setText(
                    "Dispositivo fijo: "
                            + nombreFijo
                            + "\n"
                            + macFija
            );
        }
    }

    // --------------------------------------------------------------
    // () -> mostrarModo -> ()
    // --------------------------------------------------------------
    private void mostrarModo() {
        if (filtroActual == null) {
            estado("Buscando todos los dispositivos BLE…");

        } else {
            estado(
                    "Siguiendo solo "
                            + nombreFijo
                            + ". Buscar vuelve a mostrar todos."
            );
        }
    }

    // --------------------------------------------------------------
    // String -> estado -> ()
    // --------------------------------------------------------------
    private void estado(String mensaje) {
        textoEstado.setText(mensaje);
        Log.d(ETIQUETA_LOG, mensaje);
    }

    // --------------------------------------------------------------
    // Respuesta a la solicitud de permisos.
    // Comprueba todos los permisos solicitados.
    // --------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(
            int codigo,
            String[] permisos,
            int[] resultados
    ) {
        super.onRequestPermissionsResult(codigo, permisos, resultados);

        if (codigo != CODIGO_PETICION_PERMISOS) {
            return;
        }

        boolean concedidos = resultados.length > 0;

        for (int resultado : resultados) {
            concedidos &= resultado == PackageManager.PERMISSION_GRANTED;
        }

        if (concedidos) {
            continuarAccion();

        } else {
            accionPendiente = null;

            estado(
                    "Permisos denegados. Permite dispositivos cercanos "
                            + "y ubicación precisa en "
                            + "Ajustes > Aplicaciones > Permisos."
            );
        }
    }

    // --------------------------------------------------------------
    // () -> onStart -> ()
    // --------------------------------------------------------------
    @Override
    protected void onStart() {
        visible = true;
        super.onStart();
    }

    // --------------------------------------------------------------
    // () -> onStop -> ()
    // No deja búsquedas activas al salir de la pantalla.
    // --------------------------------------------------------------
    @Override
    protected void onStop() {
        visible = false;

        boolean estabaBuscando = callbackDelEscaneo != null;

        detenerBusquedaDispositivosBTLE();

        if (estabaBuscando) {
            estado(
                    "Búsqueda pausada al salir. "
                            + "Pulsa Buscar o Seleccionar para continuar."
            );
        }

        super.onStop();
    }

    // --------------------------------------------------------------
    // () -> onDestroy -> ()
    // --------------------------------------------------------------
    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        unregisterReceiver(cambiosBluetooth);

        super.onDestroy();
    }
}