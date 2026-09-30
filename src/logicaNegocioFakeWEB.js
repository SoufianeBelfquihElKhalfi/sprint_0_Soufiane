class LogicaNegocioFakeWEB {
    // Consulta el servidor REST y devuelve la medición recibida en JSON.
    async recuperarMedicion() {
        const respuesta = await fetch('/recuperamedicion', {
            headers: { Accept: 'application/json' },
            cache: 'no-store'
        });

        const datos = await respuesta.json();

        if (!respuesta.ok) {
            throw new Error(datos.error || 'No se pudo recuperar la medición.');
        }

        return datos;
    }
}