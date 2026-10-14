# Diseño del Componente

## Comunicación

El componente `communication` permite comunicar los clientes Android y web con la lógica de negocio del sistema de Biometría y Medio Ambiente.

Ofrece una API REST para guardar nuevas mediciones y recuperar la última medición almacenada.

El componente recibe las peticiones externas, comprueba los datos y delega las operaciones en el componente `business_logic`.

## Tipos de Datos

Se utilizan los tipos abstractos definidos en la especificación de ingeniería del software:

- `N`: números naturales.
- `R`: números reales.
- `Text`: cadenas de texto.
- `B`: valores booleanos.

### MedicionEntrada

```text
MedicionEntrada = (
    fecha: Text,
    tipo: Text,
    valor: R
)
```

Representa los datos necesarios para guardar una medición.

### Medicion

```text
Medicion = (
    id: N,
    fecha: Text,
    tipo: Text,
    valor: R
)
```

Representa una medición almacenada en la base de datos.

### Error

```text
Error = (
    mensaje: Text
)
```

Representa la descripción de un error ocurrido al procesar una operación.

## Operaciones

### guardarMedicion()

**Firma lógica:**

```text
fecha: Text, tipo: Text, valor: R
    --> guardarMedicion() --> Text
```

**Diseño lógico:**

1. Recibir los datos de una medición mediante una petición externa.
2. Comprobar que los campos `fecha`, `tipo` y `valor` están presentes.
3. Comprobar que los datos tienen los tipos esperados.
4. Validar que la fecha tenga el formato `YYYY-MM-DD HH:MM:SS`.
5. Invocar la operación `guardarMedicion()` del componente `business_logic`.
6. Recibir el identificador de la medición almacenada.
7. Devolver el identificador al cliente.

### recuperarMedicion()

**Firma lógica:**

```text
recuperarMedicion() --> Medicion ∪ {null}
```

**Diseño lógico:**

1. Recibir una solicitud para recuperar la última medición.
2. Invocar la operación `recuperarMedicion()` del componente `business_logic`.
3. Recibir la última medición almacenada.
4. Si no existe ninguna medición, devolver un resultado vacío.
5. En caso contrario, devolver la medición obtenida.

## Interfaz REST

### Guardar una medición

```text
Método: POST
Ruta: /Guardamedicion
```

**Entrada:**

```text
{
    "fecha": "2026-10-14 10:00:00",
    "tipo": "temperatura",
    "valor": 4321
}
```

**Salida correcta:**

```text
{
    "id": "1"
}
```

Código HTTP: `201 Created`.

### Recuperar la última medición

```text
Método: GET
Ruta: /recuperamedicion
```

**Entrada:** no requiere parámetros.

**Salida correcta:**

```text
{
    "id": "1",
    "fecha": "2026-10-14 10:00:00",
    "tipo": "temperatura",
    "valor": 4321
}
```

Código HTTP: `200 OK`.

## Relación con Otros Componentes

```text
Cliente Android / Cliente Web
             |
             v
       communication
             |
             v
       business_logic
             |
             v
          database
```

El componente `communication` utiliza las operaciones públicas de `business_logic`.

La lógica de negocio no necesita conocer los detalles de las peticiones HTTP.

# Aclaraciones del Diseño

- La API REST utiliza JSON para intercambiar datos.
- La operación de guardado utiliza el método POST.
- La operación de recuperación utiliza el método GET.
- Los datos obligatorios para guardar una medición son `fecha`, `tipo` y `valor`.
- El identificador lo genera automáticamente la base de datos.
- Los campos adicionales recibidos en una medición no se almacenan.
- El componente de comunicación no ejecuta directamente consultas SQL.
- La lógica de negocio es independiente del protocolo HTTP.
- Las rutas `/Guardamedicion` y `/recuperamedicion` representan las funcionalidades disponibles.
- El archivo `servidor.php` también permite servir la página web y los recursos asociados.

# Reglas Generales

- **Lenguaje de programación:** PHP.
- **Encabezados de funciones:** cada función debe incluir su diseño lógico dentro de un bloque de comentarios delimitado por líneas discontinuas (`--------------------`).
- **Legibilidad:** utilizar nombres claros y código sencillo, evitando comentarios innecesarios.
- **Separación de responsabilidades:** la comunicación gestiona peticiones y respuestas HTTP, mientras que la lógica de negocio realiza las operaciones sobre las mediciones.
- **Pruebas automatizadas:** implementar pruebas para los métodos GET y POST, los campos obligatorios, los datos incorrectos y los errores de comunicación.