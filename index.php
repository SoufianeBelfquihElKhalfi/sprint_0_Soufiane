<?php
// Este archivo debe definir recuperarMedicion() y devolver un array con id, tipo y valor.
require_once __DIR__ . '/mediciones.php';

// Obtiene la última medición al cargar la página y al pulsar el botón.
$medicion = recuperarMedicion();

function escapar($valor): string
{
    return htmlspecialchars((string) $valor, ENT_QUOTES, 'UTF-8');
}
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Última medición</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            color: #222;
            background: #fff;
        }

        header {
            padding: 22px 24px;
            border-bottom: 1px solid #ddd;
            font-size: 18px;
            font-weight: bold;
        }

        main {
            width: 100%;
            max-width: 700px;
            margin: 60px auto;
            padding: 0 24px;
        }

        h1 {
            margin: 0 0 32px;
            font-size: clamp(32px, 6vw, 44px);
        }

        dl {
            margin: 0 0 32px;
            font-size: 20px;
        }

        dl div {
            display: flex;
            gap: 12px;
            margin-bottom: 18px;
        }

        dt {
            font-weight: bold;
        }

        dd {
            margin: 0;
            overflow-wrap: anywhere;
            min-width: 0;
        }

        button {
            padding: 14px 22px;
            border: none;
            border-radius: 4px;
            background: #287549;
            color: #fff;
            font: inherit;
            cursor: pointer;
        }

        button:hover {
            background: #1e5c38;
        }

        button:focus-visible {
            outline: 3px solid #222;
            outline-offset: 4px;
        }
    </style>
</head>
<body>
    <header>Biometría y Medio Ambiente</header>

    <main>
        <h1>Última medición:</h1>

        <!-- Solo se muestran los tres campos solicitados. -->
        <dl>
            <div>
                <dt>ID:</dt>
                <dd><?= escapar($medicion['id'] ?? '—') ?></dd>
            </div>
            <div>
                <dt>Tipo:</dt>
                <dd><?= escapar($medicion['tipo'] ?? '—') ?></dd>
            </div>
            <div>
                <dt>Valor:</dt>
                <dd><?= escapar($medicion['valor'] ?? '—') ?></dd>
            </div>
        </dl>

        <!-- Recarga la página para consultar de nuevo la medición. -->
        <form method="post">
            <button type="submit">Obtener última medición</button>
        </form>
    </main>
</body>
</html>