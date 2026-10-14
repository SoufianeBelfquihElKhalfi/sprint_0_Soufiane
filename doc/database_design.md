# Diseño del Componente

## Base de Datos

El componente `database` almacena las mediciones recogidas por el sistema de Biometría y Medio Ambiente.

La base de datos utilizada es `biometria_medio_ambiente`.

Su función es conservar las mediciones enviadas desde la aplicación Android para que puedan recuperarse posteriormente.

## Tipos de Datos

Se utilizan los tipos abstractos definidos en la especificación de ingeniería del software:

- `N`: números naturales.
- `R`: números reales.
- `Text`: cadenas de texto.

### Medicion

Medicion = (
    id: N,
    fecha: Text,
    tipo: Text,
    valor: R
)

- `id`: identificador único de la medición.
- `fecha`: fecha y hora de la medición.
- `tipo`: tipo de medición realizada.
- `valor`: valor numérico de la medición.

## Diseño de la Tabla

### TABLE: Medicion

**DESCRIPTION:**

Almacena las mediciones del sistema, incluyendo su identificador, fecha, tipo y valor.

**COLUMNS:**

```text
+ id | BIGINT UNSIGNED | NOT NULL | AUTO_INCREMENT
+ fecha | DATETIME | NOT NULL
+ tipo | TEXT | NOT NULL
+ valor | DOUBLE | NOT NULL
```

**PRIMARY KEY:**

```text
id
```

**FOREIGN KEYS:**

Ninguna.

**CONSTRAINTS:**

- `id` es único y se genera automáticamente.
- Todos los campos son obligatorios.
- No existen relaciones con otras tablas.

## Operaciones de Datos

Las operaciones sobre las mediciones son gestionadas por el componente `business_logic`.

### guardarMedicion()

**Firma lógica:**

fecha: Text, tipo: Text, valor: R --> guardarMedicion() --> Text

**Diseño lógico:**

1. Recibir la fecha, el tipo y el valor de una medición.
2. Insertar una nueva medición en la tabla `Medicion`.
3. Generar automáticamente el identificador de la medición.
4. Devolver el identificador generado.

### recuperarMedicion()

**Firma lógica:**

recuperarMedicion() --> Medicion ∪ {null}

**Diseño lógico:**

1. Consultar las mediciones almacenadas.
2. Ordenarlas por identificador de mayor a menor.
3. Obtener la primera medición.
4. Si no existen registros, devolver `null`.
5. En caso contrario, devolver la medición recuperada.

# Aclaraciones del Diseño

- La base de datos utiliza MySQL.
- La tabla `Medicion` contiene únicamente los campos `id`, `fecha`, `tipo` y `valor`.
- El identificador es la clave primaria.
- El identificador se genera automáticamente al insertar una medición.
- La fecha se almacena mediante el tipo `DATETIME`.
- El valor de la medición se almacena como número real.
- No se almacenan los campos `major` ni `minor` de las tramas Bluetooth.
- La última medición se identifica mediante el valor más alto del campo `id`.
- El acceso a los datos se realiza desde el componente `business_logic`.

# Reglas Generales

- **Sistema de gestión:** MySQL.
- **Diseño lógico:** utilizar tipos abstractos independientes del lenguaje de programación.
- **Separación de responsabilidades:** la base de datos se utiliza desde la lógica de negocio, evitando consultas SQL directamente desde los clientes.
- **Legibilidad:** utilizar nombres claros para las tablas y columnas.
- **Coherencia:** la estructura implementada debe coincidir con el diseño documentado.
- **Pruebas automatizadas:** verificar que se pueden insertar mediciones y recuperar la última medición almacenada.