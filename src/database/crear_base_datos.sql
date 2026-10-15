
CREATE DATABASE IF NOT EXISTS biometria_medio_ambiente;

USE biometria_medio_ambiente;

CREATE TABLE IF NOT EXISTS Medicion (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME NOT NULL,
    tipo TEXT NOT NULL,
    valor DOUBLE NOT NULL
);

INSERT INTO Medicion (fecha, tipo, valor)
VALUES ('2026-10-15 10:00:00', 'temperatura', 4321);

SELECT * FROM Medicion;
