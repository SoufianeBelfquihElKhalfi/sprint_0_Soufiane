<?php
declare(strict_types=1);

// --------------------------------------------------------------
// Archivo: test_integracion.php
//
// Descripción:
// Prueba el recorrido POST -> MySQL -> GET.
//
// ATENCION:
// Al activar esta prueba, se insertará una medición real
// en la base de datos MySQL del servidor.
// --------------------------------------------------------------

header('Content-Type: text/html; charset=utf-8');

// Por seguridad, la prueba está desactivada inicialmente.
const EJECUTAR_PRUEBA = false;

if (!EJECUTAR_PRUEBA) {
    echo '<h1>Prueba de integración</h1>';
    echo '<p>Prueba desactivada.</p>';
    echo '<p>Para ejecutarla, cambia ';
    echo 'EJECUTAR_PRUEBA a true.</p>';
    exit;
}

// --------------------------------------------------------------
// Función: peticion()
//
// Realiza una petición HTTP y devuelve el código y JSON.
// --------------------------------------------------------------
function peticion(
    string $ruta,
    ?array $datos = null
): array {

    $url = 'https://sbelelk.upv.edu.es'
        . '/communication/servidor.php?ruta='
        . urlencode($ruta);

    $curl = curl_init($url);

    if ($curl === false) {
        throw new RuntimeException(
            'No se pudo iniciar cURL.'
        );
    }

    curl_setopt_array($curl, [
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_CONNECTTIMEOUT => 5,
        CURLOPT_TIMEOUT => 10,
    ]);

    if ($datos !== null) {
        curl_setopt_array($curl, [
            CURLOPT_POST => true,
            CURLOPT_HTTPHEADER => [
                'Content-Type: application/json'
            ],
            CURLOPT_POSTFIELDS => json_encode(
                $datos,
                JSON_THROW_ON_ERROR
            ),
        ]);
    }

    $respuesta = curl_exec($curl);

    if ($respuesta === false) {
        curl_close($curl);

        throw new RuntimeException(
            'Error al realizar la petición HTTP.'
        );
    }

    $codigo = (int) curl_getinfo(
        $curl,
        CURLINFO_HTTP_CODE
    );

    curl_close($curl);

    return [
        $codigo,
        json_decode(
            $respuesta,
            true,
            512,
            JSON_THROW_ON_ERROR
        )
    ];
}

// --------------------------------------------------------------
// Función: comprobar()
//
// Comprueba una condición y muestra el resultado.
// --------------------------------------------------------------
function comprobar(
    string $nombre,
    bool $condicion
): void {

    $color = $condicion ? 'green' : 'red';
    $estado = $condicion ? 'CORRECTA' : 'FALLIDA';

    echo '<p style="color:' . $color . ';">';
    echo $estado . ': ';
    echo htmlspecialchars($nombre, ENT_QUOTES, 'UTF-8');
    echo '</p>';
}

echo '<h1>Prueba de integración POST - MySQL - GET</h1>';

try {

    // ----------------------------------------------------------
    // 1. Crear una medición específica de prueba.
    // ----------------------------------------------------------
    $medicion = [
        'fecha' => date('Y-m-d H:i:s'),
        'tipo' => 'TEST_INTEGRACION',
        'valor' => 4321.0,
    ];

    // ----------------------------------------------------------
    // 2. Guardar la medición mediante POST.
    // ----------------------------------------------------------
    [$codigoPost, $guardada] = peticion(
        '/Guardamedicion',
        $medicion
    );

    comprobar(
        'POST devuelve HTTP 201',
        $codigoPost === 201
    );

    comprobar(
        'POST devuelve un identificador',
        isset($guardada['id'])
    );

    if (
        $codigoPost !== 201 ||
        !isset($guardada['id'])
    ) {
        exit;
    }

    // ----------------------------------------------------------
    // 3. Recuperar la última medición mediante GET.
    // ----------------------------------------------------------
    [$codigoGet, $recuperada] = peticion(
        '/recuperamedicion'
    );

    comprobar(
        'GET devuelve HTTP 200',
        $codigoGet === 200
    );

    // ----------------------------------------------------------
    // 4. Comparar la medición guardada y recuperada.
    // ----------------------------------------------------------
    comprobar(
        'El identificador coincide',
        isset($recuperada['id']) &&
        (string) $recuperada['id'] ===
            (string) $guardada['id']
    );

    comprobar(
        'La fecha coincide',
        isset($recuperada['fecha']) &&
        $recuperada['fecha'] === $medicion['fecha']
    );

    comprobar(
        'El tipo coincide',
        isset($recuperada['tipo']) &&
        $recuperada['tipo'] === $medicion['tipo']
    );

    comprobar(
        'El valor coincide',
        isset($recuperada['valor']) &&
        (float) $recuperada['valor'] ===
            $medicion['valor']
    );

} catch (Throwable $error) {

    echo '<p style="color:red;">';
    echo 'FALLIDA: Error durante la prueba de integración.';
    echo '</p>';
}
