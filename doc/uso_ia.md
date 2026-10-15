# Uso de Inteligencia Artificial — Sprint 0

## 1. Introducción

Durante el desarrollo del Sprint 0 del proyecto **Biometría y Medio Ambiente** se ha utilizado ChatGPT como herramienta de apoyo para generar, comprender, revisar y documentar diferentes componentes del sistema.

Inicialmente se prepararon una serie de prompts para definir la estructura y el funcionamiento de cada componente. Estos prompts estaban acompañados de diagramas que representaban la arquitectura prevista.

A lo largo del desarrollo, **los prompts iniciales se fueron modificando y ampliando** para adaptar las respuestas de la inteligencia artificial a las necesidades reales del proyecto, resolver problemas encontrados y ajustar la implementación a los requisitos de la asignatura.

Por tanto, los prompts originales representan el punto de partida del desarrollo, pero no necesariamente la versión definitiva de cada componente.

## 2. Herramienta utilizada

- **Herramienta:** ChatGPT (OpenAI).
- **Finalidad:** asistencia en programación, diseño, documentación y revisión.
- **Lenguajes:** C++, Java, PHP, JavaScript y SQL.
- **Entornos:** Arduino IDE, Android Studio, GitHub y Plesk.

La inteligencia artificial se ha utilizado como herramienta de apoyo. Las decisiones finales de implementación se han ido ajustando durante el desarrollo del Sprint 0.

## 3. Prompts iniciales del proyecto

Los prompts originales están recogidos en el documento *Tarea 2 Biometria*, que incluye seis apartados y sus respectivos diseños.

### 3.1. Base de datos

**Objetivo inicial:** generar la base de datos para almacenar las mediciones del sistema.

El primer prompt definía una tabla `Medicion` con los siguientes campos:

```text
Medicion(
    id: N,
    fecha: DateTime,
    tipo: Text,
    valor: R,
    major: N,
    minor: N
)
```

También establecía que el identificador debía ser una clave primaria autogenerada y que los campos no podían admitir valores nulos.

**Modificaciones posteriores:**

Durante el desarrollo se simplificó la estructura de la medición, eliminando `major` y `minor` de la base de datos.

La estructura utilizada actualmente es:

```text
Medicion(
    id: N,
    fecha: Text,
    tipo: Text,
    valor: R
)
```

El servidor recibe los datos necesarios para guardar la medición, mientras que la aplicación Android se encarga de interpretar previamente los campos de la trama iBeacon.

### 3.2. Lógica de negocio

**Objetivo inicial:** generar una clase `LogicaNegocio` que separase el acceso a la base de datos de la comunicación REST.

El prompt establecía dos operaciones principales:

```text
guardarMedicion()
recuperarMedicion()
```

La primera debía almacenar una medición y la segunda recuperar la última registrada.

**Modificaciones posteriores:**

Se adaptaron las funciones a la estructura definitiva de la base de datos, utilizando únicamente fecha, tipo y valor para guardar una medición.

La implementación actual utiliza PHP y PDO para interactuar con MySQL.

También se preparó documentación del diseño lógico de las operaciones.

### 3.3. Servidor REST

**Objetivo inicial:** generar un servidor que recibiese las peticiones HTTP y delegase las operaciones en la clase `LogicaNegocio`.

Se definieron dos rutas:

```text
POST /Guardamedicion
GET /recuperamedicion
```

El servidor debía encargarse de recibir las peticiones y devolver las respuestas correspondientes, sin implementar directamente las consultas SQL.

**Modificaciones posteriores:**

Se adaptó el código para trabajar con peticiones y respuestas JSON.

También se incorporaron comprobaciones de los campos obligatorios, validación de datos y tratamiento de errores HTTP.

La implementación utiliza el archivo `servidor.php` como punto de entrada de la API.

### 3.4. Lógica fake de Android

**Objetivo inicial:** generar en Java una clase de lógica fake para comunicar la aplicación Android con el servidor REST.

El prompt original proponía el nombre `LogicaNegocioFakeApp` y dos operaciones:

```text
guardarMedicion(medicion)
recuperarMedicion()
```

**Modificaciones posteriores:**

La clase definitiva se denomina `LogicaNegocioFake`.

Se adaptaron sus operaciones para realizar peticiones HTTP desde hilos secundarios y evitar bloquear la interfaz de Android.

La aplicación también incorpora la recepción de anuncios Bluetooth LE, la interpretación de tramas iBeacon y la creación de mediciones a partir de los datos recibidos.

Se añadieron mecanismos para comprobar permisos, iniciar y detener búsquedas Bluetooth y mostrar las mediciones recuperadas.

### 3.5. Lógica fake del cliente web

**Objetivo inicial:** crear una clase JavaScript llamada `LogicaNegocioFakeWEB`.

El prompt definía la operación:

```text
recuperarMedicion()
```

Su responsabilidad era solicitar al servidor la última medición y devolver los datos preparados para mostrarlos en la página web.

**Modificaciones posteriores:**

Se adaptó la implementación para utilizar `fetch()` y gestionar respuestas JSON.

También se incorporó el tratamiento de los errores HTTP.

Se mantuvo la separación entre la interfaz visual y las operaciones de comunicación.

### 3.6. Diseño de la interfaz web

**Objetivo inicial:** generar una interfaz muy sencilla para consultar la última medición almacenada.

El prompt incluía una imagen de referencia y establecía estos elementos:

- Una cabecera.
- El título «Última medición:».
- El identificador de la medición.
- El tipo de medición.
- El valor.
- Un botón «Obtener última medición».

También indicaba que la medición debía recuperarse automáticamente al cargar la página.

**Modificaciones posteriores:**

Se implementó la interfaz mediante PHP, HTML, CSS y JavaScript.

Se adaptaron los estilos para reproducir el diseño solicitado y se conectó la página con `LogicaNegocioFakeWEB`.

La página permite consultar la última medición tanto al abrirse como al pulsar el botón.

## 4. Evolución de los prompts

El desarrollo no se realizó mediante una única petición a la inteligencia artificial.

Los prompts iniciales sirvieron para obtener las primeras propuestas de implementación, que posteriormente se fueron revisando mediante nuevas instrucciones.

Las modificaciones se centraron principalmente en:

1. Adaptar la estructura de las mediciones a los requisitos definitivos.
2. Mantener separadas las responsabilidades de los componentes.
3. Configurar correctamente la comunicación entre Android y el servidor REST.
4. Integrar la recepción Bluetooth LE mediante iBeacon.
5. Ajustar la interfaz web al diseño solicitado.
6. Añadir validaciones y tratamiento de errores.
7. Incorporar comentarios descriptivos y diseños lógicos.
8. Revisar la estructura del repositorio según las indicaciones de la asignatura.

Esta evolución permitió adaptar progresivamente los resultados generados con IA al funcionamiento del proyecto.

## 5. Uso de IA para documentación y revisión

Una vez implementados los principales componentes, también se utilizó ChatGPT para mejorar la documentación del código.

Se proporcionaron los archivos existentes y se solicitó que se añadieran comentarios sin modificar las instrucciones del programa.

Un ejemplo de instrucción utilizada fue:

> "nono pasamelo todo pero sin tocar el codigo solo comentarios."

Este procedimiento se aplicó a archivos PHP, Java, JavaScript y C++.

También se utilizó ChatGPT para revisar la rama `develop` de GitHub e identificar elementos pendientes de la evaluación.

## 6. Revisión y validación de resultados

Las respuestas generadas mediante inteligencia artificial se han utilizado como propuestas de trabajo.

Durante el desarrollo se ha procurado comprobar que las modificaciones fueran coherentes con la arquitectura y los requisitos del Sprint 0.

Especialmente durante la fase de documentación, se estableció la condición de mantener intacto el código ejecutable.

La revisión automática completa del repositorio y los resultados definitivos de las pruebas se documentarán en la fase final.

## 7. Evidencias del uso de IA

Como evidencia inicial se dispone del documento **Tarea 2 Biometria**, que contiene:

- Los seis prompts iniciales.
- Los diagramas de diseño utilizados como referencia.
- Los requisitos solicitados a la inteligencia artificial para cada componente.

Este documento permite comprobar cómo se planteó inicialmente el desarrollo.

La implementación actual del repositorio y su historial de modificaciones reflejan la evolución posterior del proyecto.

## 8. Conclusión

La inteligencia artificial se ha utilizado durante distintas fases del Sprint 0, desde los primeros diseños hasta la documentación del código.

Los prompts originales no se mantuvieron sin cambios, sino que se fueron modificando y ampliando para responder a las necesidades que aparecieron durante el desarrollo.

El resultado actual es una implementación que integra Bluetooth LE, Android, una API REST, lógica de negocio, una base de datos y una interfaz web.

ChatGPT ha servido como herramienta de asistencia, mientras que la comprobación definitiva del funcionamiento y el cumplimiento de la rúbrica requieren pruebas y validaciones específicas.