<?php
declare(strict_types=1);

class logicaNegocio
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

    // Guarda la medición y devuelve el ID generado automáticamente.
    public function guardarMedicion(
        DateTimeInterface $fecha,
        string $tipo,
        float $valor,
        int $major,
        int $minor
    ): string {
        // Los números naturales no pueden ser negativos.
        if ($major < 0 || $minor < 0) {
            throw new InvalidArgumentException(
                'major y minor deben ser enteros no negativos.'
            );
        }

        if (!is_finite($valor)) {
            throw new InvalidArgumentException(
                'valor debe ser un número finito.'
            );
        }

        // No se incluye id: lo genera la base de datos.
        // La consulta preparada mantiene los datos separados del SQL.
        $consulta = $this->conexion->prepare(
            'INSERT INTO Medicion (fecha, tipo, valor, major, minor)
             VALUES (:fecha, :tipo, :valor, :major, :minor)'
        );

        $consulta->execute([
            'fecha' => $fecha->format('Y-m-d H:i:s'),
            'tipo'  => $tipo,
            'valor' => $valor,
            'major' => $major,
            'minor' => $minor,
        ]);

        // PDO devuelve el identificador como texto.
        return $this->conexion->lastInsertId();
    }

    // Recupera todos los campos de la medición con el mayor ID.
    public function recuperarMedicion(): ?array
    {
        $consulta = $this->conexion->query(
            'SELECT id, fecha, tipo, valor, major, minor
             FROM Medicion
             ORDER BY id DESC
             LIMIT 1'
        );

        $medicion = $consulta->fetch(PDO::FETCH_ASSOC);

        // Si la tabla está vacía, devuelve null.
        return $medicion === false ? null : $medicion;
    }
}