<?php


// Envía una respuesta JSON y finaliza la petición.
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

// Obtiene la ruta y el método de la petición.
$ruta = $_GET['ruta'] ?? parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
$metodo = $_SERVER['REQUEST_METHOD'];

$rutas = [
    '/' => 'GET',
    '/index.php' => 'GET',
    '/LogicaNegocioFakeWEB.js' => 'GET',
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

// Muestra la página web.
if ($ruta === '/' || $ruta === '/index.php') {
    header('Content-Type: text/html; charset=utf-8');
    require __DIR__ . '/../index.php';
    exit;
}

// Entrega el archivo JavaScript.
if ($ruta === '/LogicaNegocioFakeWEB.js') {
    header('Content-Type: application/javascript; charset=utf-8');
    readfile(__DIR__ . '/../LogicaNegocioFakeWEB.js');
    exit;
}

try {

    // POST: guarda una nueva medición.
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
            responder(400, [
                'error' => 'El JSON no es válido.',
            ]);
        }

        if (!$datos instanceof stdClass) {
            responder(400, [
                'error' => 'Debes enviar un objeto JSON.',
            ]);
        }

        // Solo son obligatorios estos tres campos.
        foreach (['fecha', 'tipo', 'valor'] as $campo) {
            if (!isset($datos->$campo)) {
                responder(400, [
                    'error' => "Falta el campo obligatorio: $campo.",
                ]);
            }
        }

        // Comprueba los tipos de los datos necesarios.
        if (
            !is_string($datos->fecha) ||
            !is_string($datos->tipo) ||
            !(is_int($datos->valor) || is_float($datos->valor))
        ) {
            responder(400, [
                'error' => 'Los tipos de los campos no son válidos.',
            ]);
        }

        // Convierte y valida la fecha.
        $fecha = DateTimeImmutable::createFromFormat(
            '!Y-m-d H:i:s',
            $datos->fecha
        );

        if (!$fecha || $fecha->format('Y-m-d H:i:s') !== $datos->fecha) {
            responder(400, [
                'error' => 'fecha debe tener formato YYYY-MM-DD HH:MM:SS.',
            ]);
        }

        // Conecta con la lógica de negocio.
        $logica = require __DIR__ . '/conexion.php';

        // Guarda únicamente los datos necesarios.
        $id = $logica->guardarMedicion(
            $fecha,
            $datos->tipo,
            (float) $datos->valor
        );

        responder(201, ['id' => $id]);
    }

    // GET: recupera la última medición.
    $logica = require __DIR__ . '/conexion.php';

    $medicion = $logica->recuperarMedicion();

    if ($medicion === null) {
        responder(404, [
            'error' => 'No hay mediciones almacenadas.',
        ]);
    }

    responder(200, $medicion);

} catch (InvalidArgumentException $error) {

    responder(422, [
        'error' => $error->getMessage(),
    ]);

} catch (Throwable $error) {

    error_log((string) $error);

    responder(500, [
        'error' => 'No se pudo completar la operación.',
    ]);
}
