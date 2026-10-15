# Diseño del Componente

## Lógica de Negocio

El componente `business_logic` gestiona las mediciones del sistema de Biometría y Medio Ambiente.

Permite guardar nuevas mediciones y recuperar la última medición almacenada en la base de datos.

La lógica de negocio es independiente de la comunicación REST. No gestiona peticiones HTTP ni códigos de respuesta.

## Tipos de Datos

Se utilizan los tipos abstractos definidos en la especificación de ingeniería del software:

- `N`: números naturales.
- `R`: números reales.
- `Text`: cadenas de texto.

### Medicion

```text
Medicion = (
    id: N,
    fecha: Text,
    tipo: Text,
    valor: R
)
```

- `id`: identificador único generado por la base de datos.
- `fecha`: fecha y hora de la medición.
- `tipo`: tipo de medición realizada.
- `valor`: valor numérico de la medición.

## Clase LogicaNegocio

La clase `LogicaNegocio` utiliza una conexión a la base de datos proporcionada desde el exterior.

### Atributos privados

```text
conexion: ConexionBD
```

`ConexionBD` representa de forma abstracta la conexión a la base de datos.

### Operaciones públicas

```text
                  -------- LogicaNegocio --------
                  |
                  | conexion: ConexionBD
                  |
conexion: ConexionBD --> LogicaNegocio() -->
                  |
                  |
fecha: Text        |
tipo: Text         |
valor: R       --> | guardarMedicion() -->
id: Text       <-- |
                  |
                  |
                  | 
                  | recuperarMedicion() <--
medicion: Medicion | 
             <--  |
                  |
                  --------------------------------
```

La operación `recuperarMedicion()` puede devolver una medición o `null` si no existe ningún registro.

## Operaciones

### LogicaNegocio()

**Firma lógica:**

```text
conexion: ConexionBD --> LogicaNegocio()
```

**Diseño lógico:**

1. Recibir una conexión a la base de datos.
2. Guardar la conexión como parte del estado del componente.
3. Configurar el tratamiento de errores de la conexión.

### guardarMedicion()

**Firma lógica:**

```text
fecha: Text, tipo: Text, valor: R
    --> guardarMedicion() --> Text
```

**Diseño lógico:**

1. Recibir la fecha, el tipo y el valor de una medición.
2. Comprobar que el valor es un número finito.
3. Preparar la inserción de los datos en la tabla `Medicion`.
4. Insertar los campos `fecha`, `tipo` y `valor`.
5. Obtener el identificador generado automáticamente por la base de datos.
6. Devolver el identificador como texto.

### recuperarMedicion()

**Firma lógica:**

```text
recuperarMedicion() --> Medicion ∪ {null}
```

**Diseño lógico:**

1. Consultar las mediciones almacenadas en la tabla `Medicion`.
2. Ordenar los registros según su identificador de mayor a menor.
3. Seleccionar únicamente el primer registro.
4. Si no existe ninguna medición, devolver `null`.
5. En caso contrario, devolver la medición encontrada.

## Relación con la Base de Datos

El componente utiliza la base de datos `biometria_medio_ambiente`, definida en `database_design.md`.

La tabla utilizada es `Medicion`, que contiene los campos:

- `id`: identificador único.
- `fecha`: fecha y hora de la medición.
- `tipo`: tipo de medición.
- `valor`: valor numérico.

El componente `business_logic` realiza las operaciones de almacenamiento y recuperación.

El componente `communication` invoca sus operaciones públicas sin acceder directamente a las consultas SQL.

# Aclaraciones del Diseño

- La lógica de negocio está implementada mediante la clase `LogicaNegocio`.
- La conexión a la base de datos se proporciona desde el exterior del componente.
- El identificador se genera automáticamente al insertar una medición.
- La operación `guardarMedicion()` no recibe el identificador como parámetro.
- La operación `guardarMedicion()` devuelve el identificador generado como texto.
- La operación `recuperarMedicion()` no necesita parámetros.
- La última medición se identifica mediante el valor más alto del campo `id`.
- Si la tabla está vacía, `recuperarMedicion()` devuelve `null`.
- El componente no contiene rutas REST ni gestiona códigos HTTP.
- Las operaciones utilizan los mismos datos definidos en `database_design.md`.

# Reglas Generales

- **Lenguaje de programación:** PHP.
- **Encabezados de funciones y métodos:** incluir el diseño lógico de cada método dentro de un bloque de comentarios delimitado por líneas discontinuas (`--------------------`).
- **Legibilidad del código:** utilizar nombres claros y mantener el código sencillo y autoexplicativo.
- **Separación de responsabilidades:** la lógica de negocio no debe contener rutas REST, códigos HTTP ni cabeceras HTTP.
- **Acceso a datos:** las operaciones con la base de datos deben estar encapsuladas en la lógica de negocio.
- **Pruebas automatizadas:** crear pruebas unitarias o de integración para `guardarMedicion()` y `recuperarMedicion()`.
- **Coherencia:** la implementación debe respetar las operaciones y los tipos establecidos en este documento.