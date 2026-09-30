<?php
declare(strict_types=1);

// Requiere la extensión cURL de PHP y el servidor en funcionamiento.
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

function verificar(bool $condicion, string $mensaje): void
{
    if (!$condicion) {
        throw new RuntimeException('Prueba fallida: ' . $mensaje);
    }
}

$medicion = [
    'fecha' => '2026-09-29 10:00:00',
    'tipo' => 'temperatura',
    'valor' => 23.5,
    'major' => 1,
    'minor' => 2,
];

// Comprueba que POST guarda la medición y devuelve su ID.
[$estado, $guardada] = peticion('/Guardamedicion', $medicion);

verificar($estado === 201, 'POST debe devolver 201.');
verificar(isset($guardada['id']), 'POST debe devolver un ID.');

// Comprueba que GET recupera esa misma medición.
[$estado, $recuperada] = peticion('/recuperamedicion');

verificar($estado === 200, 'GET debe devolver 200.');

foreach (['id', 'fecha', 'tipo', 'valor', 'major', 'minor'] as $campo) {
    verificar(isset($recuperada[$campo]), "Falta el campo $campo.");
}

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

verificar(
    (int) $recuperada['major'] === $medicion['major'],
    'major debe coincidir.'
);

verificar(
    (int) $recuperada['minor'] === $medicion['minor'],
    'minor debe coincidir.'
);

echo "Prueba correcta: medición guardada y recuperada.\n";