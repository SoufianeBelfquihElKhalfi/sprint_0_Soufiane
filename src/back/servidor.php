<?php
declare(strict_types=1);

// Envía una respuesta JSON y termina la petición.
function responder(int $estado, array $datos): void
{
    http_response_code($estado);
    header('Content-Type: application/json; charset=utf-8');

    echo json_encode(
        $datos,
        JSON_UNESCAPED_UNICODE | JSON_INVALID_UTF8_SUBSTITUTE
    );

    exit;
}

$ruta = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
$metodo = $_SERVER['REQUEST_METHOD'];

// Rutas permitidas: no se exponen los archivos internos de back.
$rutas = [
    '/' => 'GET',
    '/index.php' => 'GET',
    '/logicaNegocioFakeWEB.js' => 'GET',
    '/Guardamedicion' => 'POST',
    '/recuperamedicion' => 'GET',
];

if (!is_string($ruta) || !isset($rutas[$ruta])) {
    responder(404, ['error' => 'Ruta no encontrada.']);
}

if ($metodo !== $rutas[$ruta]) {
    header('Allow: ' . $rutas[$ruta]);
    responder(405, ['error' => 'Método no permitido.']);
}

// Muestra el frontend en el mismo servidor que la API.
if ($ruta === '/' || $ruta === '/index.php') {
    header('Content-Type: text/html; charset=utf-8');
    require __DIR__ . '/../index.php';
    exit;
}

// Entrega la clase JavaScript utilizada por el frontend.
if ($ruta === '/logicaNegocioFakeWEB.js') {
    header('Content-Type: application/javascript; charset=utf-8');
    readfile(__DIR__ . '/../logicaNegocioFakeWEB.js');
    exit;
}

try {
    /*
     * POST /Guardamedicion
     * Recibe fecha, tipo, valor, major y minor mediante JSON.
     * El servidor comprueba su formato y llama a guardarMedicion().
     * El ID se genera automáticamente; no debe enviarse.
     */
    if ($ruta === '/Guardamedicion') {
        $tipoContenido = strtolower(trim(explode(
            ';',
            $_SERVER['CONTENT_TYPE'] ?? ''
        )[0]));

        if ($tipoContenido !== 'application/json') {
            responder(415, [
                'error' => 'Debes enviar Content-Type: application/json.',
            ]);
        }

        try {
            $datos = json_decode(
                file_get_contents('php://input'),
                false,
                512,
                JSON_THROW_ON_ERROR
            );
        } catch (JsonException $error) {
            responder(400, ['error' => 'El JSON no es válido.']);
        }

        if (!$datos instanceof stdClass) {
            responder(400, [
                'error' => 'Debes enviar un objeto JSON.',
            ]);
        }

        foreach (['fecha', 'tipo', 'valor', 'major', 'minor'] as $campo) {
            if (!isset($datos->$campo)) {
                responder(400, [
                    'error' => "Falta el campo obligatorio: $campo.",
                ]);
            }
        }

        if (
            !is_string($datos->fecha) ||
            !is_string($datos->tipo) ||
            !(is_int($datos->valor) || is_float($datos->valor)) ||
            !is_int($datos->major) ||
            !is_int($datos->minor)
        ) {
            responder(400, [
                'error' => 'Los tipos de los campos no son válidos.',
            ]);
        }

        // Convierte la fecha al tipo que espera LogicaNegocio.
        $fecha = DateTimeImmutable::createFromFormat(
            '!Y-m-d H:i:s',
            $datos->fecha
        );

        if (!$fecha || $fecha->format('Y-m-d H:i:s') !== $datos->fecha) {
            responder(400, [
                'error' => 'fecha debe ser válida y tener formato YYYY-MM-DD HH:MM:SS.',
            ]);
        }

        $logica = require __DIR__ . '/conexion.php';

        $id = $logica->guardarMedicion(
            $fecha,
            $datos->tipo,
            (float) $datos->valor,
            $datos->major,
            $datos->minor
        );

        responder(201, ['id' => $id]);
    }

    /*
     * GET /recuperamedicion
     * Pide la última medición a la lógica de negocio.
     * Devuelve sus seis campos, o un error 404 si la tabla está vacía.
     * El frontend decide mostrar únicamente id, tipo y valor.
     */
    $logica = require __DIR__ . '/conexion.php';
    $medicion = $logica->recuperarMedicion();

    if ($medicion === null) {
        responder(404, [
            'error' => 'No hay mediciones almacenadas.',
        ]);
    }

    responder(200, $medicion);
} catch (InvalidArgumentException $error) {
    // La lógica de negocio ha rechazado los valores recibidos.
    responder(422, ['error' => $error->getMessage()]);
} catch (Throwable $error) {
    // Los detalles internos quedan en el registro del servidor.
    error_log((string) $error);

    responder(500, [
        'error' => 'No se pudo completar la operación.',
    ]);
}