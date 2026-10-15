
/*
--------------------------------------------------------------
Archivo: logicaNegocioFakeWEB.js

Descripción:
Implementa la lógica de negocio fake de la página web.

Permite recuperar la última medición almacenada mediante
una petición HTTP al servidor REST.

Autor: Soufiane Belfquih El Khalfi
Proyecto: Biometría y Medio Ambiente - Sprint 0
Aportación: Implementación de la lógica fake del cliente web.

Fecha: 3/10/2026
Copyright: Soufiane.
--------------------------------------------------------------
*/

class LogicaNegocioFakeWEB {

    // --------------------------------------------------------------
    // Función: recuperarMedicion()
    //
    // Diseño lógico:
    // recuperarMedicion() --> Medicion ∪ {null}
    //
    // Solicita la última medición almacenada mediante una
    // petición HTTP GET al servidor REST.
    //
    // Convierte la respuesta recibida a datos JSON.
    // Si la petición falla, comunica el error mediante
    // una excepción.
    //
    // Si la petición es correcta, devuelve los datos
    // de la medición a la interfaz gráfica.
    // --------------------------------------------------------------
    async recuperarMedicion() {
        const respuesta = await fetch('/communication/servidor.php?ruta=/recuperamedicion', {
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
