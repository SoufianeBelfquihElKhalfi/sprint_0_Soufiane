
<?php
declare(strict_types=1);

// --------------------------------------------------------------
// Archivo: conexion.php
//
// Descripción:
// Establece la conexión con la base de datos MySQL
// del proyecto de Biometría y Medio Ambiente.
//
// Crea la conexión y la proporciona a la clase
// LogicaNegocio para gestionar las mediciones.
//
// Autor: Soufiane Belfquih El Khalfi
// Proyecto: Biometría y Medio Ambiente - Sprint 0
// Aportación: Conexión entre la base de datos y
// la lógica de negocio.
//
// Fecha: 30/09/2026
// Copyright: soufiane
// --------------------------------------------------------------

require_once __DIR__ . '/../business_logic/logicaNegocio.php';

// --------------------------------------------------------------
// Configuración de la conexión a la base de datos.
//
// Se definen el servidor, el nombre de la base de datos
// y las credenciales necesarias para establecer la conexión.
// --------------------------------------------------------------
$host = 'localhost';
$baseDatos = 'biometria_medio_ambiente';
$usuario = 'root_sprint0';
$password = 'soufiane123';

// --------------------------------------------------------------
// Diseño lógico:
//
// datosConexion: ConfiguracionBD
//     --> conectarBD() --> ConexionBD
//
// Se establece una conexión con MySQL mediante PDO.
// La conexión utiliza excepciones para gestionar los errores
// y desactiva la emulación de consultas preparadas.
// --------------------------------------------------------------
$conexion = new PDO(
    "mysql:host=$host;dbname=$baseDatos",
    $usuario,
    $password,
    [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_EMULATE_PREPARES => false,
    ]
);

// --------------------------------------------------------------
// Se proporciona la conexión a la clase LogicaNegocio
// y se devuelve el objeto creado al componente que
// ha solicitado la conexión.
// --------------------------------------------------------------
return new LogicaNegocio($conexion);
