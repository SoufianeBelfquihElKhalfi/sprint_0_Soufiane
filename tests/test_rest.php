
<?php
declare(strict_types=1);

// --------------------------------------------------------------
// Archivo: test_rest.php
//
// Descripción:
// Comprueba las respuestas HTTP de la API REST.
// No inserta ni modifica registros de MySQL.
// --------------------------------------------------------------

header('Content-Type: text/html; charset=utf-8');

$correctas = 0;
$fallidas = 0;

function comprobar(
    string $nombre,
    int $obtenido,
    int $esperado
): void {
    global $correctas, $fallidas;

    if ($obtenido === $esperado) {
        $correctas++;
        echo '<p style="color:green;">CORRECTA: '
            . htmlspecialchars($nombre)
            . '</p>';
    } else {
        $fallidas++;
        echo '<p style="color:red;">FALLIDA: '
            . htmlspecialchars($nombre)
            . ' (esperado: ' . $esperado
            . ', recibido: ' . $obtenido
            . ')</p>';
    }
}

function peticion(
    string $ruta,
    string $metodo = 'GET'
): int {
    $url = 'https://sbelelk.upv.edu.es'
        . '/communication/servidor.php?ruta='
        . urlencode($ruta);

    $curl = curl_init($url);

    curl_setopt_array($curl, [
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_TIMEOUT => 10,
        CURLOPT_CUSTOMREQUEST => $metodo,
    ]);

    $respuesta = curl_exec($curl);

    if ($respuesta === false) {
        curl_close($curl);
        return 0;
    }

    $codigo = curl_getinfo(
        $curl,
        CURLINFO_HTTP_CODE
    );

    curl_close($curl);

    return (int) $codigo;
}

echo '<h1>Pruebas API REST</h1>';

// PRUEBA 1: Recuperar la última medición.
comprobar(
    'GET recuperar medición',
    peticion('/recuperamedicion'),
    200
);

// PRUEBA 2: POST sin Content-Type JSON.
comprobar(
    'POST sin Content-Type JSON',
    peticion('/Guardamedicion', 'POST'),
    415
);

// PRUEBA 3: Método HTTP incorrecto.
comprobar(
    'GET en una ruta que requiere POST',
    peticion('/Guardamedicion'),
    405
);

// PRUEBA 4: Ruta inexistente.
comprobar(
    'Ruta inexistente',
    peticion('/ruta-inexistente'),
    404
);

echo '<hr>';
echo '<h2>Resumen</h2>';
echo '<p>Correctas: ' . $correctas . '</p>';
echo '<p>Fallidas: ' . $fallidas . '</p>';
