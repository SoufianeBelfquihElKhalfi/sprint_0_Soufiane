# Diseño del Componente

## Base de Datos

La base de datos `biometria_medio_ambiente` almacena las mediciones recogidas por el sistema de Biometría y Medio Ambiente.

### TABLE: Medicion

**DESCRIPTION:**

Almacena las mediciones, indicando su identificador, la fecha en la que se registraron, el tipo de medición y su valor.

**COLUMNS:**

| Nombre | Tipo | Restricciones |
|---|---|---|
| id | BIGINT UNSIGNED | NOT NULL, AUTO_INCREMENT |
| fecha | DATETIME | NOT NULL |
| tipo | TEXT | NOT NULL |
| valor | DOUBLE | NOT NULL |

**PRIMARY KEY:**

- `id`

**FOREIGN KEYS:**

- Ninguna.

**CONSTRAINTS:**

- `id` es único y se genera automáticamente.
- Todos los campos son obligatorios.
- La tabla no tiene relaciones con otras tablas.

# Aclaraciones del Diseño

- La base de datos utiliza MySQL.
- La tabla `Medicion` contiene únicamente los campos `id`, `fecha`, `tipo` y `valor`.
- El identificador se genera automáticamente cuando se guarda una medición.

