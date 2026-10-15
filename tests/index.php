<?php
declare(strict_types=1);

// --------------------------------------------------------------
// Archivo: tests/index.php
//
// Descripción:
// Ejecuta pruebas automáticas del Sprint 0.
//
// Comprueba el funcionamiento de PHP, la conexión MySQL,
// la recuperación y el guardado de mediciones.
//
// Las pruebas de escritura utilizan SQLite en memoria
// para no modificar los datos reales del servidor.
//
// Proyecto: Biometría y Medio Ambiente - Sprint 0
// --------------------------------------------------------------

header('Content-Type: text/html; charset=utf-8');

// Contadores de pruebas.
$correctas = 0;
$fallidas = 0;

// --------------------------------------------------------------
// Función: comprobar()
//
// Comprueba una condición y muestra el resultado.
// --------------------------------------------------------------
function comprobar(
    string $nombre,
    bool $condicion
): void {

    global $correctas, $fallidas;

    if ($condicion) {

        $correctas++;

        echo '<p style="color:green;">';
        echo 'CORRECTA: ' . htmlspecialchars($nombre);
        echo '</p>';

    } else {

        $fallidas++;

        echo '<p style="color:red;">';
        echo 'FALLIDA: ' . htmlspecialchars($nombre);
        echo '</p>';
    }
}

// --------------------------------------------------------------
// Función: ejecutarPrueba()
//
// Ejecuta una comprobación y captura sus errores.
// --------------------------------------------------------------
function ejecutarPrueba(
    string $nombre,
    callable $prueba
): void {

    try {
        comprobar($nombre, $prueba() === true);

    } catch (Throwable $error) {
        comprobar($nombre, false);
    }
}

// --------------------------------------------------------------
// Cabecera de resultados.
// --------------------------------------------------------------
echo '<h1>Pruebas del Sprint 0</h1>';

// --------------------------------------------------------------
// PRUEBA 1: PHP funciona.
// --------------------------------------------------------------
ejecutarPrueba('PHP disponible', function (): bool {

    return PHP_VERSION_ID >= 80000;

});

// --------------------------------------------------------------
// PRUEBA 2: Conexión con MySQL.
// --------------------------------------------------------------
$logicaReal = null;

try {

    $logicaReal = require
        __DIR__ . '/../database/conexion.php';

    comprobar(
        'Conexión a MySQL',
        $logicaReal instanceof LogicaNegocio
    );

} catch (Throwable $error) {

    comprobar('Conexión a MySQL', false);
}

// --------------------------------------------------------------
// PRUEBA 3: Recuperar la última medición de MySQL.
// --------------------------------------------------------------
$medicionReal = null;

if ($logicaReal instanceof LogicaNegocio) {

    ejecutarPrueba(
        'Recuperar última medición de MySQL',
        function () use (
            $logicaReal,
            &$medicionReal
        ): bool {

            $medicionReal =
                $logicaReal->recuperarMedicion();

            return is_array($medicionReal);

        }
    );
}

// --------------------------------------------------------------
// PRUEBA 4: Comprobar campos obligatorios.
// --------------------------------------------------------------
if (is_array($medicionReal)) {

    ejecutarPrueba(
        'La medición contiene los cuatro campos',
        function () use ($medicionReal): bool {

            return isset(
                $medicionReal['id'],
                $medicionReal['fecha'],
                $medicionReal['tipo'],
                $medicionReal['valor']
            );

        }
    );

    // ----------------------------------------------------------
    // PRUEBA 5: Comprobar el identificador.
    // ----------------------------------------------------------
    ejecutarPrueba(
        'El identificador es válido',
        function () use ($medicionReal): bool {

            return ctype_digit(
                (string) $medicionReal['id']
            ) && (int) $medicionReal['id'] > 0;

        }
    );

    // ----------------------------------------------------------
    // PRUEBA 6: Comprobar el tipo de medición.
    // ----------------------------------------------------------
    ejecutarPrueba(
        'El tipo de medición es válido',
        function () use ($medicionReal): bool {

            return is_string(
                $medicionReal['tipo']
            ) && trim($medicionReal['tipo']) !== '';

        }
    );

    // ----------------------------------------------------------
    // PRUEBA 7: Comprobar el valor de la medición.
    // ----------------------------------------------------------
    ejecutarPrueba(
        'El valor de la medición es numérico',
        function () use ($medicionReal): bool {

            return is_numeric(
                $medicionReal['valor']
            ) && is_finite(
                (float) $medicionReal['valor']
            );

        }
    );

    // ----------------------------------------------------------
    // PRUEBA 8: Comprobar la fecha.
    // ----------------------------------------------------------
    ejecutarPrueba(
        'La fecha tiene el formato correcto',
        function () use ($medicionReal): bool {

            $fecha = DateTimeImmutable::createFromFormat(
                '!Y-m-d H:i:s',
                (string) $medicionReal['fecha']
            );

            return $fecha !== false &&
                $fecha->format('Y-m-d H:i:s') ===
                    $medicionReal['fecha'];

        }
    );

    // ----------------------------------------------------------
    // PRUEBA 9: Comprobar los campos adicionales.
    // ----------------------------------------------------------
    ejecutarPrueba(
        'No se devuelven major ni minor',
        function () use ($medicionReal): bool {

            return !array_key_exists(
                'major',
                $medicionReal
            ) && !array_key_exists(
                'minor',
                $medicionReal
            );

        }
    );
}

// --------------------------------------------------------------
// PRUEBAS CON SQLITE TEMPORAL.
//
// No se modifica la base de datos de producción.
// --------------------------------------------------------------

echo '<h2>Pruebas de lógica de negocio</h2>';

try {

    // Crear una base de datos temporal.
    $conexion = new PDO('sqlite::memory:');

    // Crear la tabla para los tests.
    $conexion->exec(
        'CREATE TABLE Medicion (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            fecha TEXT NOT NULL,
            tipo TEXT NOT NULL,
            valor REAL NOT NULL
        )'
    );

    // Crear un objeto de nuestra lógica real.
    $logica = new LogicaNegocio($conexion);

    // ----------------------------------------------------------
    // PRUEBA 10: Tabla inicialmente vacía.
    // ----------------------------------------------------------
    ejecutarPrueba(
        'Tabla vacía devuelve null',
        function () use ($logica): bool {

            return $logica->recuperarMedicion() === null;

        }
    );

    // ----------------------------------------------------------
    // PRUEBA 11: Guardar una medición.
    // ----------------------------------------------------------
    $fechaPrueba = new DateTimeImmutable(
        '2026-10-15 10:00:00'
    );

    $idGuardado = null;

    ejecutarPrueba(
        'Guardar una medición correctamente',
        function () use (
            $logica,
            $fechaPrueba,
            &$idGuardado
        ): bool {

            $idGuardado = $logica->guardarMedicion(
                $fechaPrueba,
                'temperatura',
                4321.0
            );

            return $idGuardado === '1';

        }
    );

    // ----------------------------------------------------------
    // PRUEBA 12: Recuperar la medición guardada.
    // ----------------------------------------------------------
    ejecutarPrueba(
        'Recuperar la medición guardada',
        function () use (
            $logica,
            $idGuardado
        ): bool {

            $medicion = $logica->recuperarMedicion();

            return is_array($medicion)
                && (string) $medicion['id'] ===
                    (string) $idGuardado
                && $medicion['fecha'] ===
                    '2026-10-15 10:00:00'
                && $medicion['tipo'] === 'temperatura'
                && (float) $medicion['valor'] === 4321.0;

        }
    );

    // ----------------------------------------------------------
    // PRUEBA 13: Guardar una segunda medición.
    // ----------------------------------------------------------
    ejecutarPrueba(
        'Recuperar siempre la última medición',
        function () use ($logica): bool {

            $nuevoId = $logica->guardarMedicion(
                new DateTimeImmutable(
                    '2026-10-15 11:00:00'
                ),
                'CO2',
                1234.0
            );

            $ultima = $logica->recuperarMedicion();

            return is_array($ultima)
                && (string) $ultima['id'] ===
                    (string) $nuevoId
                && $ultima['tipo'] === 'CO2'
                && (float) $ultima['valor'] === 1234.0;

        }
    );

    // ----------------------------------------------------------
    // PRUEBA 14: Rechazar un valor no válido.
    // ----------------------------------------------------------
    ejecutarPrueba(
        'Rechazar valores infinitos',
        function () use ($logica): bool {

            try {

                $logica->guardarMedicion(
                    new DateTimeImmutable(),
                    'temperatura',
                    INF
                );

                return false;

            } catch (InvalidArgumentException $error) {

                return true;
            }

        }
    );

} catch (Throwable $error) {

    comprobar(
        'Preparación de SQLite temporal',
        false
    );
}

// --------------------------------------------------------------
// RESUMEN FINAL
// --------------------------------------------------------------

echo '<hr>';
echo '<h2>Resumen de resultados</h2>';

echo '<p>Pruebas correctas: ' . $correctas . '</p>';
echo '<p>Pruebas fallidas: ' . $fallidas . '</p>';

if ($fallidas === 0) {

    echo '<h3 style="color:green;">';
    echo 'Todas las pruebas ejecutadas son correctas.';
    echo '</h3>';

} else {

    echo '<h3 style="color:red;">';
    echo 'Hay pruebas que necesitan revisión.';
    echo '</h3>';
}
