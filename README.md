
# Sprint 0 — Biometría y Medio Ambiente

## 1. Descripción del proyecto

Este repositorio contiene el desarrollo del Sprint 0 del proyecto **Biometría y Medio Ambiente**.

El objetivo es implementar una primera versión del sistema de comunicación entre una placa emisora Bluetooth Low Energy (BLE), una aplicación Android, un servidor REST, una base de datos y una interfaz web.

Durante este sprint se utiliza una **medición ficticia** para comprobar la comunicación entre los componentes, sin necesidad de obtener todavía datos de un sensor físico.

## 2. Objetivo del Sprint 0

Comprobar el funcionamiento del siguiente recorrido:

1. La placa emite una medición ficticia mediante Bluetooth LE utilizando una trama iBeacon.
2. La aplicación Android busca dispositivos BLE y recibe la trama.
3. Android identifica las tramas correspondientes al proyecto y obtiene el valor de la medición.
4. La aplicación envía la medición al servidor mediante HTTP POST.
5. El servidor utiliza la lógica de negocio para almacenar la medición en MySQL.
6. La aplicación Android y la interfaz web pueden consultar la última medición mediante HTTP GET.

## 3. Tecnologías utilizadas

| Componente | Tecnología |
|---|---|
| Placa emisora | Arduino / C++ / Bluefruit |
| Comunicación inalámbrica | Bluetooth Low Energy (BLE) / iBeacon |
| Aplicación móvil | Android Studio / Java |
| Servidor REST | PHP |
| Lógica de negocio | PHP |
| Base de datos desplegada | MySQL |
| Interfaz web | HTML / CSS / JavaScript |
| Servidor de despliegue | Plesk |
| Control de versiones | Git / GitHub |

## 4. Arquitectura del sistema

El proyecto está dividido en diferentes componentes con responsabilidades separadas.

**Sensor:** genera y publica una medición ficticia mediante BLE.

**Android:** busca dispositivos BLE, recibe las tramas iBeacon y utiliza una lógica de negocio fake para comunicarse con el servidor.

**Comunicación:** recibe las peticiones HTTP, valida sus datos y ofrece las operaciones de la API REST.

**Lógica de negocio:** gestiona el almacenamiento y la recuperación de las mediciones.

**Base de datos:** almacena las mediciones.

**Lógica fake web:** realiza las peticiones HTTP necesarias para consultar la última medición.

**Interfaz web:** muestra los datos recibidos y permite actualizar la información.

### Flujo de comunicación

```text
Placa Arduino
     |
     | Bluetooth LE / iBeacon
     v
Aplicación Android
     |
     | HTTP POST / GET
     v
Servidor REST (PHP)
     |
     v
Lógica de negocio (PHP)
     |
     v
Base de datos MySQL
     ^
     |
Servidor REST
     ^
     |
Lógica fake web (JavaScript)
     ^
     |
Interfaz web
```

## 5. Estructura del repositorio

Los componentes principales están organizados de la siguiente manera:

```text
Sprint 0
|
|-- author.md
|-- database.sqlite
|-- README.md
|
|-- doc/
|   |-- business_logic_design.md
|   |-- communication_design.md
|   |-- database_design.md
|   |-- frontend_business_logic_design.md
|   |-- prompts_iniciales.pdf
|   |-- uso_ia.md
|
|-- tests/
|   |-- index.php
|   |-- test_rest.php
|   |-- test_validaciones.php
|   |-- test_integracion.php
|
|-- src/
    |
    |-- sensor/
    |   |-- HolaMundoIBeacon/
    |       |-- HolaMundoIBeacon.ino
    |       |-- EmisoraBLE.h
    |       |-- Publicador.h
    |       |-- Medidor.h
    |       |-- LED.h
    |       |-- PuertoSerie.h
    |       |-- ServicioEnEmisora.h
    |
    |-- biometria2/
    |   |-- app/
    |   |   |-- src/
    |   |       |-- main/
    |   |       |-- test/
    |   |       |-- androidTest/
    |   |-- build.gradle
    |   |-- settings.gradle
    |
    |-- communication/
    |   |-- servidor.php
    |
    |-- business_logic/
    |   |-- logicaNegocio.php
    |   |-- prueba.php
    |
    |-- database/
    |   |-- conexion.php
    |
    |-- frontend_business_logic/
    |   |-- logicaNegocioFakeWEB.js
    |
    |-- WEB/
        |-- index.php
```

El archivo `database.sqlite` forma parte del repositorio, pero el despliegue PHP actual utiliza MySQL.

## 6. Funcionamiento de Arduino

El programa principal se encuentra en:

`src/sensor/HolaMundoIBeacon/HolaMundoIBeacon.ino`

La placa utiliza la biblioteca Bluefruit para publicar anuncios Bluetooth LE con formato iBeacon.

### Configuración de la publicación

- **Nombre de la emisora:** `GTI-Soufiane`
- **UUID:** `EPSG-GTI-PROY-3A`
- **Tipo de medición publicado:** temperatura
- **Valor ficticio:** `4321`
- **Intervalo entre rondas de publicación:** 30 segundos
- **Duración indicada para cada publicación:** 1000 milisegundos

La información se codifica utilizando los campos Major y Minor de iBeacon.

El campo Major contiene el identificador del tipo de medición y un contador de publicaciones. El campo Minor contiene el valor ficticio.

### Preparación de la placa

1. Abrir el proyecto de Arduino en el entorno compatible con la placa utilizada.
2. Instalar y configurar la biblioteca Bluefruit correspondiente.
3. Seleccionar la placa y el puerto correctos.
4. Compilar y cargar el programa.
5. Comprobar los mensajes de depuración mediante el monitor serie.

## 7. Aplicación Android

La aplicación está desarrollada en Java mediante Android Studio.

El proyecto se encuentra en:

`src/biometria2/`

### Funcionalidades

- Buscar todos los dispositivos Bluetooth LE disponibles.
- Detener el escaneo BLE.
- Buscar el dispositivo `GTI-Soufiane`.
- Identificar las tramas iBeacon correspondientes al proyecto.
- Extraer los campos UUID, Major y Minor.
- Crear una medición a partir del valor recibido.
- Enviar mediciones al servidor.
- Recuperar y mostrar la última medición almacenada.

La aplicación utiliza `LogicaNegocioFake.java` para realizar las peticiones HTTP.

### Ejecución

1. Abrir Android Studio.
2. Seleccionar la carpeta `src/biometria2` como proyecto.
3. Esperar a que finalice la sincronización de Gradle.
4. Conectar un teléfono Android compatible con Bluetooth LE.
5. Compilar e instalar la aplicación.
6. Conceder los permisos de Bluetooth y ubicación que solicite el sistema.
7. Iniciar el escaneo mediante los botones de la aplicación.

Para comprobar la recepción de tramas es necesario disponer de una emisora BLE compatible.

## 8. API REST

El componente de comunicación está implementado en PHP mediante `servidor.php`.

Proporciona dos operaciones principales.

### Guardar una medición

**Método:** POST

**Ruta lógica:** `/Guardamedicion`

**Datos de entrada:**

```json
{
  "fecha": "2026-10-14 10:00:00",
  "tipo": "temperatura",
  "valor": 4321
}
```

Si la operación se realiza correctamente, el servidor responde con HTTP `201 Created` e incluye el identificador generado.

### Recuperar la última medición

**Método:** GET

**Ruta lógica:** `/recuperamedicion`

Si existen mediciones, el servidor devuelve HTTP `200 OK` y un objeto JSON con los siguientes campos:

```json
{
  "id": "1",
  "fecha": "2026-10-14 10:00:00",
  "tipo": "temperatura",
  "valor": 4321
}
```

Los ejemplos muestran la estructura de las respuestas. Los valores reales dependen de las mediciones almacenadas.

### Dirección del servidor

La API desplegada utiliza como punto de entrada:

`https://sbelelk.upv.edu.es/communication/servidor.php?ruta=`

Las operaciones se indican mediante el parámetro `ruta`.

## 9. Base de datos

El sistema utiliza una base de datos MySQL denominada:

`biometria_medio_ambiente`

La tabla principal es `Medicion`.

| Campo | Tipo | Descripción |
|---|---|---|
| id | BIGINT UNSIGNED | Identificador de la medición |
| fecha | DATETIME | Fecha y hora |
| tipo | TEXT | Tipo de medición |
| valor | DOUBLE | Valor numérico |

El identificador se genera automáticamente.

La lógica de negocio permite insertar nuevas mediciones y recuperar la que tiene el identificador más alto.

## 10. Interfaz web

La interfaz está implementada mediante HTML, CSS y JavaScript.

Permite visualizar:

- El identificador de la última medición.
- El tipo de medición.
- El valor de la medición.

Incluye un botón para volver a consultar la última medición almacenada.

La interfaz delega la comunicación HTTP en `LogicaNegocioFakeWEB`.

### Dirección web

https://sbelelk.upv.edu.es/

## 11. Despliegue en Plesk

El servidor del proyecto está desplegado mediante Plesk.

La estructura de archivos del despliegue no coincide exactamente con la organización utilizada en GitHub.

En el despliegue, `index.php` se encuentra en la raíz del directorio público, mientras que los componentes PHP y JavaScript se organizan en sus carpetas correspondientes.

### Componentes del despliegue

```text
httpdocs/
|
|-- index.php
|
|-- communication/
|   |-- servidor.php
|
|-- business_logic/
|   |-- logicaNegocio.php
|
|-- database/
|   |-- conexion.php
|
|-- frontend_business_logic/
    |-- logicaNegocioFakeWEB.js
```

Para desplegar el sistema es necesario configurar una base de datos MySQL y adaptar la configuración de conexión al entorno de destino.

Las credenciales de producción no deben publicarse en el repositorio.

## 12. Pruebas

El proyecto incorpora pruebas unitarias de Java, pruebas instrumentadas de Android, pruebas de lógica de negocio en PHP y pruebas de la API REST. También se han realizado comprobaciones manuales de la placa BLE y del funcionamiento integrado del sistema.

### 12.1. Pruebas PHP y REST

| Archivo | Qué comprueba |
|---|---|
| `tests/index.php` | PHP, conexión a MySQL, recuperación y estructura de mediciones; operaciones de guardar y recuperar mediante una base de datos SQLite temporal en memoria. |
| `tests/test_rest.php` | Códigos HTTP y métodos admitidos en las rutas REST. |
| `tests/test_validaciones.php` | Rechazo de JSON inválido, campos ausentes, fechas incorrectas y valores no numéricos. |
| `tests/test_integracion.php` | Recorrido POST → MySQL → GET, identificador y valores guardados y recuperados. |
| `src/business_logic/prueba.php` | Prueba HTTP previa de guardar y recuperar, preparada para un servidor local de desarrollo. |

Para ejecutar las pruebas desde Plesk se colocan los archivos de `tests/` en `httpdocs/tests/`. Las pruebas de lectura pueden consultarse en `/tests/`, `/tests/test_rest.php` y `/tests/test_validaciones.php` cuando el servidor dispone de PHP, cURL y los controladores PDO necesarios.

`tests/index.php` consulta la base de datos MySQL configurada y realiza pruebas de escritura adicionales sobre SQLite en memoria, sin modificar MySQL en esas comprobaciones.

**Precaución:** `test_integracion.php` contiene la constante `EJECUTAR_PRUEBA`, desactivada inicialmente. Activarla implica insertar una medición real en MySQL. Debe ejecutarse solo de forma controlada y desactivarse al terminar. Por seguridad, los scripts de prueba no deberían permanecer accesibles públicamente en el servidor de producción.

### 12.2. Pruebas unitarias de Android (Java)

Se encuentran en `src/biometria2/app/src/test/java/com/examplo/sbelelk/biometria2/`:

- `TramaIBeaconTest.java`: siete pruebas de extracción de la trama, UUID, Major, Minor `4321`, potencia, fabricante y estructura iBeacon.
- `MedicionTest.java`: siete pruebas sobre los constructores, identificador, fecha, tipo y valores numéricos de las mediciones.

Se ejecutan en Android Studio mediante **Run** sobre cada clase de test; no necesitan un teléfono ni una conexión BLE. Comprueban el procesamiento de datos con entradas controladas, no la recepción Bluetooth real.

### 12.3. Pruebas instrumentadas de Android

En `src/biometria2/app/src/androidTest/java/com/examplo/sbelelk/biometria2/LogicaNegocioFakeTest.java` se encuentran dos pruebas:

- `testRecuperarMedicion`: verifica que Android recibe una medición del servidor sin errores.
- `testCamposMedicionRecuperada`: verifica la presencia del ID, fecha, tipo y valor numérico.

Requieren un dispositivo Android o emulador con Internet y un servidor que tenga mediciones disponibles. En la ejecución observada se obtuvo el resultado **2 passed**.

### 12.4. Pruebas manuales de la SparkFun y el sistema completo

Se realizaron las siguientes comprobaciones:

1. **SparkFun y Monitor serie:** las publicaciones muestran un contador creciente, valores Major consecutivos y `Minor = 4321`.
2. **Recepción y envío:** la aplicación Android se utiliza como intermediaria entre los anuncios iBeacon y el servidor REST.
3. **Persistencia:** phpMyAdmin muestra mediciones consecutivas con valor `4321`, con intervalos observados de 30 segundos.
4. **Consulta web:** la interfaz mostró la última medición con ID `313`, tipo `O3` y valor `4321`.

Estas evidencias son coherentes con el recorrido **SparkFun → Android → API REST → MySQL → web**. Los mensajes del Monitor serie por sí solos no certifican la recepción BLE, y esta comprobación manual no sustituye una batería automatizada de pruebas de hardware.

### 12.5. Alcance y resultados

Se han verificado directamente la ejecución de PHP y la conexión MySQL en Plesk, dos pruebas instrumentadas de recuperación en Android y la visualización de mediciones en la base de datos y la web.

Los archivos de pruebas adicionales están incluidos en el repositorio, pero es necesario comprobar y conservar sus resultados de ejecución para acreditar cada prueba.

La batería existente valida los casos principales del Sprint 0, aunque no garantiza cobertura exhaustiva ni sustituye la revisión del agente corrector de la asignatura.

## 13. Documentación de diseño

La carpeta `doc/` contiene los diseños de los componentes principales:

- `database_design.md`: diseño de la base de datos.
- `business_logic_design.md`: diseño de la lógica de negocio.
- `communication_design.md`: diseño del componente REST.
- `frontend_business_logic_design.md`: diseño de la lógica fake web.

Estos documentos incluyen las operaciones, firmas lógicas, tipos de datos y responsabilidades de cada componente.

## 14. Estado del Sprint 0

En el repositorio están implementados los componentes necesarios para el flujo de comunicación entre la placa BLE, la aplicación Android, el servidor REST, la base de datos MySQL y la interfaz web.

Las pruebas y comprobaciones realizadas aportan evidencias del funcionamiento de los componentes principales y de su integración.

La validación final del Sprint 0 requiere comprobar el cumplimiento de la rúbrica y ejecutar el agente corrector de la asignatura.
