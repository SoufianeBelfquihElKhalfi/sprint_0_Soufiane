
<?php
declare(strict_types=1);

// Realiza una petición al servidor.
function peticion(string $ruta, ?array $datos = null): array
{
    $curl = curl_init('http://localhost:8000' . $ruta);

    curl_setopt_array($curl, [
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_CONNECTTIMEOUT => 5,
        CURLOPT_TIMEOUT => 10,
    ]);

    if ($datos !== null) {
        curl_setopt_array($curl, [
            CURLOPT_POST => true,
            CURLOPT_HTTPHEADER => ['Content-Type: application/json'],
            CURLOPT_POSTFIELDS => json_encode(
                $datos,
                JSON_THROW_ON_ERROR
            ),
        ]);
    }

    $respuesta = curl_exec($curl);

    if ($respuesta === false) {
        $error = curl_error($curl);
        curl_close($curl);
        throw new RuntimeException($error);
    }

    $estado = curl_getinfo($curl, CURLINFO_HTTP_CODE);
    curl_close($curl);

    return [
        $estado,
        json_decode($respuesta, true, 512, JSON_THROW_ON_ERROR),
    ];
}

// Comprueba que se cumpla una condición.
function verificar(bool $condicion, string $mensaje): void
{
    if (!$condicion) {
        throw new RuntimeException('Prueba fallida: ' . $mensaje);
    }
}

// Datos de prueba.
// Incluye campos adicionales para comprobar
// que el servidor los ignora.
$medicion = [
    'fecha' => '2026-09-29 10:00:00',
    'tipo' => 'temperatura',
    'valor' => 23.5,
    'major' => 2821,
    'minor' => 1234,
];

// Guarda una medición.
[$estado, $guardada] = peticion('/Guardamedicion', $medicion);

verificar($estado === 201, 'POST debe devolver 201.');
verificar(isset($guardada['id']), 'POST debe devolver un ID.');

// Recupera la última medición.
[$estado, $recuperada] = peticion('/recuperamedicion');

verificar($estado === 200, 'GET debe devolver 200.');

// Comprueba los campos necesarios.
foreach (['id', 'fecha', 'tipo', 'valor'] as $campo) {
    verificar(isset($recuperada[$campo]), "Falta el campo $campo.");
}

// Comprueba que los campos adicionales no se devuelven.
verificar(
    !array_key_exists('major', $recuperada),
    'Se ha devuelto un campo que debía ignorarse.'
);

verificar(
    !array_key_exists('minor', $recuperada),
    'Se ha devuelto un campo que debía ignorarse.'
);

verificar(
    (string) $recuperada['id'] === (string) $guardada['id'],
    'Debe recuperar la última medición insertada.'
);

verificar(
    $recuperada['fecha'] === $medicion['fecha'],
    'La fecha debe coincidir.'
);

verificar(
    $recuperada['tipo'] === $medicion['tipo'],
    'El tipo debe coincidir.'
);

verificar(
    abs((float) $recuperada['valor'] - $medicion['valor']) < 0.000001,
    'El valor debe coincidir.'
);

echo "Prueba correcta: medición guardada y recuperada.\n";
