# Diseño del Componente

## Lógica de Negocio

El componente `business_logic` se encarga de gestionar las mediciones del sistema de Biometría y Medio Ambiente.

Permite guardar nuevas mediciones en la base de datos y recuperar la última medición almacenada.

La lógica de negocio es independiente de la capa de comunicación REST. No gestiona peticiones HTTP ni códigos de respuesta.

## Tipos de Datos

### Medicion

Medicion = (
    id: N,
    fecha: Text,
    tipo: Text,
    valor: R
)

- `id`: identificador único de la medición, generado automáticamente por la base de datos.
- `fecha`: fecha y hora de la medición, en formato YYYY-MM-DD HH:MM:SS.
- `tipo`: tipo de medición realizada.
- `valor`: valor numérico de la medición.

## Operaciones

### guardarMedicion()

**Firma lógica:**

guardarMedicion(fecha: Text, tipo: Text, valor: R) → Text

**Diseño lógico:**

1. Recibir la fecha, el tipo y el valor de una medición.
2. Comprobar que el valor es un número finito.
3. Preparar la inserción de los datos en la tabla `Medicion`.
4. Insertar los campos `fecha`, `tipo` y `valor`.
5. Obtener el identificador generado automáticamente por la base de datos.
6. Devolver el identificador como texto.

### recuperarMedicion()

**Firma lógica:**

recuperarMedicion() → Medicion ∪ {null}

**Diseño lógico:**

1. Consultar la tabla `Medicion`.
2. Ordenar los registros por el campo `id` de mayor a menor.
3. Seleccionar únicamente el primer registro.
4. Si no existe ninguna medición, devolver `null`.
5. En caso contrario, devolver la medición encontrada.

## Relación con la Base de Datos

El componente utiliza la base de datos `biometria_medio_ambiente`, definida en `database_design.md`.

Tabla utilizada: `Medicion`.

Campos:

- `id`: clave primaria autoincremental.
- `fecha`: fecha y hora de la medición.
- `tipo`: tipo de medición.
- `valor`: valor de la medición.

La lógica de negocio no depende de la capa de comunicación.

# Aclaraciones del Diseño

- La base de datos genera automáticamente el identificador de cada medición.
- El método `guardarMedicion()` no recibe el identificador como parámetro.
- El método `recuperarMedicion()` devuelve la última medición según el identificador más alto.
- Si la tabla está vacía, `recuperarMedicion()` devuelve `null`.
- La conexión a la base de datos se proporciona desde el exterior del componente.

# Reglas Generales

- **Lenguaje de programación:** PHP.
- **Encabezados de funciones y métodos:** incluir el diseño lógico de cada método dentro de un bloque de comentarios delimitado por líneas discontinuas (`--------------------`).
- **Legibilidad del código:** utilizar nombres claros y mantener el código sencillo y autoexplicativo.
- **Separación de responsabilidades:** la lógica de negocio no debe contener rutas REST, códigos HTTP ni cabeceras HTTP.
- **Pruebas automatizadas:** crear pruebas unitarias o de integración para `guardarMedicion()` y `recuperarMedicion()`.