# Diseño del Componente

## Lógica de Negocio Fake del Cliente Web

El componente `frontend_business_logic` permite que la interfaz web utilice las operaciones de la lógica de negocio sin acceder directamente al servidor.

Actúa como intermediario entre la interfaz gráfica y el componente `communication`, encapsulando las peticiones HTTP.

La interfaz pública de este componente debe coincidir con las operaciones correspondientes del componente `business_logic`.

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

## Clase LogicaNegocioFakeWEB

El componente está formado por una clase que ofrece a la interfaz gráfica las operaciones necesarias para consultar las mediciones.

### Operaciones públicas

**Firma lógica:**

recuperarMedicion() --> Medicion ∪ {null}

Esta firma coincide con la operación `recuperarMedicion()` definida en `business_logic_design.md`.

### recuperarMedicion()

**Diseño lógico:**

1. Recibir una solicitud de la interfaz gráfica para obtener la última medición.
2. Solicitar la última medición al componente de comunicación.
3. Recibir la respuesta del servidor.
4. Comprobar si la operación se ha realizado correctamente.
5. Si existe un error, comunicarlo a la interfaz gráfica.
6. Si la operación es correcta, obtener los datos de la medición.
7. Devolver la medición obtenida a la interfaz gráfica.

## Relación con Otros Componentes

Interfaz Web
↓
LogicaNegocioFakeWEB
↓
Comunicación REST
↓
Lógica de Negocio
↓
Base de Datos

La interfaz gráfica utiliza únicamente los métodos públicos de `LogicaNegocioFakeWEB`.

La comunicación HTTP se realiza dentro de la lógica fake y no directamente desde la interfaz.

# Aclaraciones del Diseño

- La implementación del componente se realiza mediante la clase `LogicaNegocioFakeWEB`.
- La clase dispone de una única operación pública: `recuperarMedicion()`.
- Esta operación no recibe parámetros.
- La operación devuelve los datos de la última medición almacenada.
- La firma lógica coincide con la operación equivalente del componente `business_logic`.
- La implementación utiliza una petición HTTP GET para obtener los datos del servidor.
- La comunicación se encuentra encapsulada dentro del componente fake.
- Los datos recibidos se interpretan como una medición.
- En la implementación actual, los errores HTTP se comunican mediante una excepción.
- La interfaz gráfica utiliza los datos recibidos para mostrar el identificador, el tipo y el valor de la medición.
- El componente no accede directamente a la base de datos.
- El componente no contiene consultas SQL.

# Reglas Generales

- **Lenguaje de programación:** JavaScript.
- **Encabezados de funciones y métodos:** incluir el diseño lógico de cada método dentro de un bloque de comentarios delimitado por líneas discontinuas (`--------------------`).
- **Legibilidad del código:** utilizar nombres claros y mantener el código sencillo y autoexplicativo, evitando comentarios innecesarios.
- **Separación de responsabilidades:** la interfaz gráfica no debe realizar peticiones HTTP directamente. Estas deben estar encapsuladas en el componente `frontend_business_logic`.
- **Correspondencia de interfaces:** las operaciones públicas deben mantener el mismo nombre, tipos de entrada y tipos de salida que sus equivalentes del componente `business_logic`.
- **Pruebas automatizadas:** crear pruebas para `recuperarMedicion()`, comprobando la recepción correcta de datos y el tratamiento de los errores del servidor.