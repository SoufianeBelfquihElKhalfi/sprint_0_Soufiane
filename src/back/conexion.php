<?php
declare(strict_types=1);

require_once __DIR__ . '/logicaNegocio.php';


$host = 'localhost';
$baseDatos = 'biometria_medio_ambiente';
$usuario = 'root_sprint0';
$password = 'soufiane123';

// Prepara la conexión y la entrega a la lógica de negocio.
$conexion = new PDO(
    "mysql:host=$host;dbname=$baseDatos",
    $usuario,
    $password,
    [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_EMULATE_PREPARES => false,
    ]
);

return new LogicaNegocio($conexion);