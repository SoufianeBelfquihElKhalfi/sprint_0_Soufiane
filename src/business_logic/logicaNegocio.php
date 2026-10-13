<?php
declare(strict_types=1);

class LogicaNegocio
{
    private PDO $conexion;

    // Recibe la conexión preparada fuera de esta clase.
    public function __construct(PDO $conexion)
    {
        $this->conexion = $conexion;
        $this->conexion->setAttribute(
            PDO::ATTR_ERRMODE,
            PDO::ERRMODE_EXCEPTION
        );
    }

    // Guarda la medición y devuelve el ID generado.
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

    // Recupera la última medición.
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
