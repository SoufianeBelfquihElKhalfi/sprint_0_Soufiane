<?php
declare(strict_types=1);

// --------------------------------------------------------------
// Archivo: test_validaciones.php
//
// Descripción:
// Comprueba que la API REST rechaza mediciones incorrectas.
//
// Todas las peticiones son inválidas intencionadamente.
// No deben generar registros en la base de datos.
// --------------------------------------------------------------

header('Content-Type: text/html; charset=utf-8');

$correctas = 0;
$fallidas = 0;

// --------------------------------------------------------------
// Función: comprobar()
//
// Compara el código HTTP obtenido con el esperado.
// --------------------------------------------------------------
function comprobar(
    string $nombre,
    int $obtenido,
    int $esperado
): void {

    global $correctas, $fallidas;

    $nombreSeguro = htmlspecialchars(
        $nombre,
        ENT_QUOTES,
        'UTF-8'
    );

    if ($obtenido === $esperado) {

        $correctas++;

        echo "<p style='color:green;'>";
        echo "CORRECTA: $nombreSeguro";
        echo "</p>";

    } else {

        $fallidas++;

        echo "<p style='color:red;'>";
        echo "FALLIDA: $nombreSeguro";
        echo " (esperado: $esperado, recibido: $obtenido)";
        echo "</p>";
    }
}

// --------------------------------------------------------------
// Función: enviarDatos()
//
// Envía JSON mediante POST al servidor REST
// y devuelve el código HTTP recibido.
// --------------------------------------------------------------
function enviarDatos(string $json): int {

    $url = 'https://sbelelk.upv.edu.es'
        . '/communication/servidor.php?ruta=/Guardamedicion';

    $curl = curl_init($url);

    if ($curl === false) {
        return 0;
    }

    curl_setopt_array($curl, [
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_POST => true,
        CURLOPT_HTTPHEADER => [
            'Content-Type: application/json'
        ],
        CURLOPT_POSTFIELDS => $json,
        CURLOPT_CONNECTTIMEOUT => 5,
        CURLOPT_TIMEOUT => 10,
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

// --------------------------------------------------------------
// Ejecutar las pruebas.
// --------------------------------------------------------------
echo '<h1>Pruebas de validación REST</h1>';

// PRUEBA 1: Falta fecha.
comprobar(
    'Falta la fecha',
    enviarDatos(json_encode([
        'tipo' => 'temperatura',
        'valor' => 4321
    ])),
    400
);

// PRUEBA 2: Falta tipo.
comprobar(
    'Falta el tipo',
    enviarDatos(json_encode([
        'fecha' => '2026-10-15 10:00:00',
        'valor' => 4321
    ])),
    400
);

// PRUEBA 3: Falta valor.
comprobar(
    'Falta el valor',
    enviarDatos(json_encode([
        'fecha' => '2026-10-15 10:00:00',
        'tipo' => 'temperatura'
    ])),
    400
);

// PRUEBA 4: Fecha inválida.
comprobar(
    'Fecha con formato incorrecto',
    enviarDatos(json_encode([
        'fecha' => '15/10/2026',
        'tipo' => 'temperatura',
        'valor' => 4321
    ])),
    400
);

// PRUEBA 5: Valor de tipo texto.
comprobar(
    'Valor no numérico',
    enviarDatos(json_encode([
        'fecha' => '2026-10-15 10:00:00',
        'tipo' => 'temperatura',
        'valor' => 'incorrecto'
    ])),
    400
);

// PRUEBA 6: JSON mal formado.
comprobar(
    'JSON mal formado',
    enviarDatos('{"fecha":'),
    400
);

// PRUEBA 7: JSON que no es un objeto.
comprobar(
    'JSON sin estructura de objeto',
    enviarDatos('[1,2,3]'),
    400
);

// --------------------------------------------------------------
// Resumen final.
// --------------------------------------------------------------
echo '<hr>';
echo '<h2>Resumen de resultados</h2>';

echo '<p>Correctas: ' . $correctas . '</p>';
echo '<p>Fallidas: ' . $fallidas . '</p>';

if ($fallidas === 0) {
    echo '<h3 style="color:green;">';
    echo 'Todas las validaciones funcionan correctamente.';
    echo '</h3>';
} else {
    echo '<h3 style="color:red;">';
    echo 'Hay validaciones que necesitan revisión.';
    echo '</h3>';
}
