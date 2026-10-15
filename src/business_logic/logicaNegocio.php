
<?php
declare(strict_types=1);

// --------------------------------------------------------------
// Archivo: logicaNegocio.php
//
// Descripción:
// Gestiona el almacenamiento y la recuperación de mediciones
// mediante una conexión a la base de datos.
//
// Autor: Soufiane Belfquih El Khalfi
// Proyecto: Biometría y Medio Ambiente - Sprint 0
// Aportación: Implementación de la lógica de negocio.
// 
// 
// Fecha: 30/09/2026
// Copyright: soufiane
// --------------------------------------------------------------

class LogicaNegocio
{
    private PDO $conexion;

    // --------------------------------------------------------------
    // Constructor: LogicaNegocio()
    //
    // Diseño lógico:
    // conexion: ConexionBD --> LogicaNegocio()
    //
    // Recibe una conexión a la base de datos, la almacena
    // y configura el tratamiento de errores.
    // --------------------------------------------------------------
    public function __construct(PDO $conexion)
    {
        $this->conexion = $conexion;
        $this->conexion->setAttribute(
            PDO::ATTR_ERRMODE,
            PDO::ERRMODE_EXCEPTION
        );
    }

    // --------------------------------------------------------------
    // Función: guardarMedicion()
    //
    // Diseño lógico:
    // fecha: Text, tipo: Text, valor: R
    //     --> guardarMedicion() --> Text
    //
    // Comprueba que el valor sea finito, guarda la medición
    // en la base de datos y devuelve el ID generado.
    // --------------------------------------------------------------
    public function guardarMedicion(
        DateTimeInterface $fecha,
        string $tipo,
        float $valor
    ): string {
        if (!is_finite($valor)) {
            throw new InvalidArgumentException(
                'valor debe ser un número finito.'
            );
        }

        $consulta = $this->conexion->prepare(
            'INSERT INTO Medicion (fecha, tipo, valor)
             VALUES (:fecha, :tipo, :valor)'
        );

        $consulta->execute([
            'fecha' => $fecha->format('Y-m-d H:i:s'),
            'tipo'  => $tipo,
            'valor' => $valor,
        ]);

        return $this->conexion->lastInsertId();
    }

    // --------------------------------------------------------------
    // Función: recuperarMedicion()
    //
    // Diseño lógico:
    // recuperarMedicion() --> Medicion ∪ {null}
    //
    // Consulta las mediciones ordenadas por ID descendente
    // y devuelve la última o null si no existen registros.
    // --------------------------------------------------------------
    public function recuperarMedicion(): ?array
    {
        $consulta = $this->conexion->query(
            'SELECT id, fecha, tipo, valor
             FROM Medicion
             ORDER BY id DESC
             LIMIT 1'
        );

        $medicion = $consulta->fetch(PDO::FETCH_ASSOC);

        return $medicion === false ? null : $medicion;
    }
}
