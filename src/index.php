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
            background: white;
            color: black;
            font-family: Arial, sans-serif;
        }

        /* Franja rosa como en la imagen de referencia. */
        header {
            height: 80px;
            background: #ffcccc;
        }

        main {
            width: min(100%, 760px);
            padding: 76px 24px 40px;
            margin-left: clamp(0px, 11vw, 140px);
        }

        h1 {
            margin: 0 0 60px;
            font-size: clamp(32px, 5vw, 48px);
            font-weight: normal;
        }

        .datos {
            margin-left: clamp(0px, 7vw, 88px);
            font-size: 24px;
        }

        .datos p {
            margin: 0 0 30px;
            overflow-wrap: anywhere;
        }

        button {
            display: block;
            max-width: 100%;
            margin: 66px 0 0 clamp(0px, 2.5vw, 32px);
            padding: 16px 34px;
            border: 1px solid black;
            border-radius: 16px;
            background: #70eff5;
            color: black;
            font: inherit;
            font-size: 24px;
            cursor: pointer;
        }

        button:disabled {
            opacity: 0.6;
            cursor: wait;
        }

        button:focus-visible {
            outline: 3px solid #176b70;
            outline-offset: 4px;
        }

        #mensaje {
            font-size: 16px;
        }
    </style>
</head>
<body>
    <header aria-label="Biometría y Medio Ambiente"></header>

    <main>
        <h1>Última medición:</h1>

        <!-- La interfaz muestra únicamente estos tres campos. -->
        <div class="datos" aria-live="polite">
            <p>ID : <span id="id">—</span></p>
            <p>Tipo : <span id="tipo">—</span></p>
            <p>Valor : <span id="valor">—</span></p>
        </div>

        <button id="obtener" type="button">
            Obtener última medición
        </button>

        <!-- Solo se muestra cuando ocurre un error. -->
        <p id="mensaje" role="status" hidden></p>
    </main>

    <script src="./LogicaNegocioFakeWEB.js"></script>
    <script>
        const logica = new LogicaNegocioFakeWEB();
        const boton = document.getElementById('obtener');
        const mensaje = document.getElementById('mensaje');

        // La interfaz delega la comunicación en la lógica fake.
        async function actualizarMedicion() {
            boton.disabled = true;
            mensaje.hidden = true;

            try {
                const medicion = await logica.recuperarMedicion();

                for (const campo of ['id', 'tipo', 'valor']) {
                    document.getElementById(campo).textContent = medicion[campo];
                }
            } catch (error) {
                mensaje.textContent = error.message;
                mensaje.hidden = false;
            } finally {
                boton.disabled = false;
            }
        }

        boton.addEventListener('click', actualizarMedicion);

        // Obtiene la última medición al abrir la página.
        actualizarMedicion();
    </script>
</body>
</html>