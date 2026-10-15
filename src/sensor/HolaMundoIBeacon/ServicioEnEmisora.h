
	// -*- mode: c++ -*-

	// --------------------------------------------------------------
	// Archivo: ServicioEnEmisora.h
	//
	// Descripción:
	// Gestiona los servicios y las características Bluetooth LE
	// utilizados por la emisora.
	//
	// Permite configurar identificadores UUID, propiedades,
	// permisos de lectura y escritura, tamaños de datos
	// y funciones de notificación.
	//
	// Autor original: Jordi Bataller i Mascarell
	// Fecha original: 2019-07-17
	//
	// Integración: Soufiane Belfquih El Khalfi
	// Proyecto: Biometría y Medio Ambiente - Sprint 0
	// Aportación: Integración de la gestión de servicios BLE.
	//
	// Copyright: Soufiane
	// --------------------------------------------------------------

	// ----------------------------------------------------------
	// Jordi Bataller i Mascarell
	// 2019-07-17
	// ----------------------------------------------------------
	#ifndef SERVICIO_EMISORA_H_INCLUIDO
	#define SERVICIO_EMISORA_H_INCLUIDO

	// ----------------------------------------------------
	// ----------------------------------------------------
	#include <vector>

	// --------------------------------------------------------------
	// Función: alReves()
	//
	// Diseño lógico:
	// p: [T], n: Z --> alReves() --> [T]
	//
	// Recibe un array y el número de elementos.
	// Invierte el orden de los primeros n elementos
	// dentro del mismo array.
	//
	// Devuelve un puntero al array modificado.
	// --------------------------------------------------------------
	// ----------------------------------------------------
	// alReves() utilidad
	// pone al revés el contenido de una array en el mismo array
	// ----------------------------------------------------
	template< typename T >
	T *  alReves( T * p, int n ) {
		T aux;

		for( int i=0; i < n/2; i++ ) {
		aux = p[i];
		p[i] = p[n-i-1];
		p[n-i-1] = aux;
		}
		return p;
	} // ()

	// --------------------------------------------------------------
	// Función: stringAUint8AlReves()
	//
	// Diseño lógico:
	// pString: Text, pUint: [N], tamMax: Z
	//     --> stringAUint8AlReves() --> [N]
	//
	// Recibe una cadena de texto y un array de bytes.
	//
	// Copia los caracteres al array en orden inverso,
	// utilizando como máximo tamMax posiciones.
	//
	// Devuelve un puntero al array modificado.
	// --------------------------------------------------------------
	// ----------------------------------------------------
	// ----------------------------------------------------
	uint8_t * stringAUint8AlReves( const char * pString, uint8_t * pUint, int tamMax ) {

		int longitudString =  strlen( pString );
		int longitudCopiar = ( longitudString > tamMax ? tamMax : longitudString );
		// copio nombreServicio -> uuidServicio pero al revés
		for( int i=0; i<=longitudCopiar-1; i++ ) {
			pUint[ tamMax-i-1 ] = pString[ i ];
		} // for

		return pUint;
	} // ()

	// --------------------------------------------------------------
	// Clase: ServicioEnEmisora
	//
	// Diseño lógico:
	//
	// ServicioEnEmisora = (
	//     uuidServicio: [N]_16,
	//     elServicio: BLEService,
	//     lasCaracteristicas: [Caracteristica]
	// )
	//
	// Representa un servicio Bluetooth LE.
	//
	// Permite añadir características y activar
	// el servicio utilizando la biblioteca Bluefruit.
	// --------------------------------------------------------------
	// ----------------------------------------------------------
	// ----------------------------------------------------------
	class ServicioEnEmisora {

	public:

		// --------------------------------------------------------
		// --------------------------------------------------------

		// --------------------------------------------------------------
		// Tipo: CallbackCaracteristicaEscrita
		//
		// Diseño lógico:
		// conn_handle: N, chr: BLECharacteristic,
		// data: [N], len: N --> CallbackCaracteristicaEscrita()
		//
		// Define la función que se ejecuta cuando un dispositivo
		// escribe datos en una característica BLE.
		// --------------------------------------------------------------
		// .........................................................
		// .........................................................
		using CallbackCaracteristicaEscrita = void ( uint16_t conn_handle,
													BLECharacteristic * chr,
													uint8_t * data, uint16_t len); 

		// --------------------------------------------------------------
		// Clase: Caracteristica
		//
		// Diseño lógico:
		//
		// Caracteristica = (
		//     uuidCaracteristica: [N]_16,
		//     laCaracteristica: BLECharacteristic
		// )
		//
		// Representa una característica de un servicio BLE.
		//
		// Permite configurar propiedades, permisos y tamaños,
		// escribir datos, enviar notificaciones y registrar callbacks.
		// --------------------------------------------------------------
		// .........................................................
		// .........................................................
		class Caracteristica {
		private:
		uint8_t uuidCaracteristica[16] = { // el uuid se copia aquí (al revés) a partir de un string-c
			// least signficant byte, el primero
			'0', '1', '2', '3', 
			'4', '5', '6', '7', 
			'8', '9', 'A', 'B', 
			'C', 'D', 'E', 'F'
		};

		// 
		// 
		// 
		BLECharacteristic laCaracteristica;

		public:

		// --------------------------------------------------------------
		// Constructor: Caracteristica()
		//
		// Diseño lógico:
		// nombreCaracteristica_: Text --> Caracteristica()
		//
		// Recibe el nombre de una característica y lo convierte
		// en un UUID de 16 bytes.
		// --------------------------------------------------------------
		// .........................................................
		// .........................................................
		Caracteristica( const char * nombreCaracteristica_ )
			:
			laCaracteristica( stringAUint8AlReves( nombreCaracteristica_, &uuidCaracteristica[0], 16 ) )
		{
			
		} // ()

		// --------------------------------------------------------------
		// Constructor: Caracteristica()
		//
		// Diseño lógico:
		// nombreCaracteristica_: Text, props: N,
		// permisoRead: SecureMode_t,
		// permisoWrite: SecureMode_t, tam: N
		//     --> Caracteristica()
		//
		// Crea una característica BLE y configura
		// sus propiedades, permisos y tamaño máximo.
		// --------------------------------------------------------------
		// .........................................................
		// .........................................................
		Caracteristica( const char * nombreCaracteristica_ ,
						uint8_t props,
						SecureMode_t permisoRead,
						SecureMode_t permisoWrite, 
						uint8_t tam ) 
			:
			Caracteristica( nombreCaracteristica_ ) // llamada al otro constructor
		{
			(*this).asignarPropiedadesPermisosYTamanyoDatos( props, permisoRead, permisoWrite, tam );
		} // ()

		private:

		// --------------------------------------------------------------
		// Función: asignarPropiedades()
		//
		// Diseño lógico:
		// props: N --> asignarPropiedades()
		//
		// Configura las propiedades de la característica BLE,
		// como lectura, escritura o notificaciones.
		// --------------------------------------------------------------
		// .........................................................
		// CHR_PROPS_WRITE , CHR_PROPS_READ ,  CHR_PROPS_NOTIFY 
		// .........................................................
		void asignarPropiedades ( uint8_t props ) {
			// no puedo escribir AUN si el constructor llama a esto: Serial.println( " laCaracteristica.setProperties( props ); ");
			(*this).laCaracteristica.setProperties( props );
		} // ()

		// --------------------------------------------------------------
		// Función: asignarPermisos()
		//
		// Diseño lógico:
		// permisoRead: SecureMode_t,
		// permisoWrite: SecureMode_t --> asignarPermisos()
		//
		// Establece los permisos de lectura y escritura
		// de la característica BLE.
		// --------------------------------------------------------------
		// .........................................................
		// SecureMode_t::SECMODE_OPEN  , SecureMode_t::SECMODE_NO_ACCESS
		// .........................................................
		void asignarPermisos( SecureMode_t permisoRead, SecureMode_t permisoWrite ) {
			// no puedo escribir AUN si el constructor llama a esto: Serial.println( "laCaracteristica.setPermission( permisoRead, permisoWrite ); " );
			(*this).laCaracteristica.setPermission( permisoRead, permisoWrite );
		} // ()

		// --------------------------------------------------------------
		// Función: asignarTamanyoDatos()
		//
		// Diseño lógico:
		// tam: N --> asignarTamanyoDatos()
		//
		// Establece la longitud máxima de los datos
		// que puede almacenar la característica.
		// --------------------------------------------------------------
		// .........................................................
		// .........................................................
		void asignarTamanyoDatos( uint8_t tam ) {
			// no puedo escribir AUN si el constructor llama a esto: Serial.print( " (*this).laCaracteristica.setFixedLen( tam = " );
			// no puedo escribir AUN si el constructor llama a esto: Serial.println( tam );
			// (*this).laCaracteristica.setFixedLen( tam );
			(*this).laCaracteristica.setMaxLen( tam );
		} // ()

		public:

		// --------------------------------------------------------------
		// Función: asignarPropiedadesPermisosYTamanyoDatos()
		//
		// Diseño lógico:
		// props: N, permisoRead: SecureMode_t,
		// permisoWrite: SecureMode_t, tam: N
		//     --> asignarPropiedadesPermisosYTamanyoDatos()
		//
		// Configura las propiedades, permisos de lectura
		// y escritura y tamaño máximo de los datos.
		// --------------------------------------------------------------
		// .........................................................
		// .........................................................
		void asignarPropiedadesPermisosYTamanyoDatos( uint8_t props,
													SecureMode_t permisoRead,
													SecureMode_t permisoWrite, 
													uint8_t tam ) {
			asignarPropiedades( props );
			asignarPermisos( permisoRead, permisoWrite );
			asignarTamanyoDatos( tam );
		} // ()
													

		// --------------------------------------------------------------
		// Función: escribirDatos()
		//
		// Diseño lógico:
		// str: Text --> escribirDatos() --> N
		//
		// Escribe una cadena en la característica BLE.
		// Devuelve el número de bytes escritos.
		// --------------------------------------------------------------
		// .........................................................
		// .........................................................
		uint16_t escribirDatos( const char * str ) {
			// Serial.print( " return (*this).laCaracteristica.write( str  = " );
			// Serial.println( str );

			uint16_t r = (*this).laCaracteristica.write( str );

			// Serial.print( ">>>Escritos " ); Serial.print( r ); Serial.println( " bytes con write() " );

			return r;
		} // ()

		// --------------------------------------------------------------
		// Función: notificarDatos()
		//
		// Diseño lógico:
		// str: Text --> notificarDatos() --> N
		//
		// Envía una notificación BLE con los datos recibidos.
		// Devuelve el resultado numérico de la operación.
		// --------------------------------------------------------------
		// .........................................................
		// .........................................................
		uint16_t notificarDatos( const char * str ) {
			
			uint16_t r = laCaracteristica.notify( &str[0] );

			return r;
		} //  ()

		// --------------------------------------------------------------
		// Función: instalarCallbackCaracteristicaEscrita()
		//
		// Diseño lógico:
		// cb: CallbackCaracteristicaEscrita
		//     --> instalarCallbackCaracteristicaEscrita()
		//
		// Registra una función que se ejecutará
		// cuando se escriban datos en la característica.
		// --------------------------------------------------------------
		// .........................................................
		// .........................................................
		void instalarCallbackCaracteristicaEscrita( CallbackCaracteristicaEscrita cb ) {
			(*this).laCaracteristica.setWriteCallback( cb );
		} // ()

		// --------------------------------------------------------------
		// Función: activar()
		//
		// Diseño lógico:
		// activar()
		//
		// Inicializa la característica BLE y registra
		// el resultado de la operación por el puerto serie.
		// --------------------------------------------------------------
		// .........................................................
		// .........................................................
		void activar() {
			err_t error = (*this).laCaracteristica.begin();
			Globales::elPuerto.escribir(  " (*this).laCaracteristica.begin(); error = " );
			Globales::elPuerto.escribir(  error );
		} // ()

		}; // class Caracteristica
		
		// --------------------------------------------------------
		// --------------------------------------------------------
	private:
		
		uint8_t uuidServicio[16] = { // el uuid se copia aquí (al revés) a partir de un string-c
		// least signficant byte, el primero
		'0', '1', '2', '3', 
		'4', '5', '6', '7', 
		'8', '9', 'A', 'B', 
		'C', 'D', 'E', 'F'
		};

		//
		//
		//
		BLEService elServicio;

		//
		//
		//
		std::vector< Caracteristica * > lasCaracteristicas;

	public:

		// --------------------------------------------------------------
		// Constructor: ServicioEnEmisora()
		//
		// Diseño lógico:
		// nombreServicio_: Text --> ServicioEnEmisora()
		//
		// Recibe el nombre del servicio y lo convierte
		// en un UUID de 16 bytes.
		// --------------------------------------------------------------
		// .........................................................
		// .........................................................
		ServicioEnEmisora( const char * nombreServicio_ )
		:
		elServicio( stringAUint8AlReves( nombreServicio_, &uuidServicio[0], 16 ) )
		{
		
		} // ()

		// --------------------------------------------------------------
		// Función: escribeUUID()
		//
		// Diseño lógico:
		// escribeUUID()
		//
		// Muestra el identificador UUID del servicio
		// mediante el puerto serie.
		// --------------------------------------------------------------
		// .........................................................
		// .........................................................
		void escribeUUID() {
		Serial.println ( "**********" );
		for (int i=0; i<= 15; i++) {
			Serial.print( (char) uuidServicio[i] );
		}
		Serial.println ( "\n**********" );
		} // ()

		// --------------------------------------------------------------
		// Función: anyadirCaracteristica()
		//
		// Diseño lógico:
		// car: Caracteristica --> anyadirCaracteristica()
		//
		// Añade una referencia a la característica recibida
		// dentro de la colección de características del servicio.
		// --------------------------------------------------------------
		// .........................................................
		// .........................................................
		void anyadirCaracteristica( Caracteristica & car ) {
		(*this).lasCaracteristicas.push_back( & car );
		} // ()

		// --------------------------------------------------------------
		// Función: activarServicio()
		//
		// Diseño lógico:
		// activarServicio()
		//
		// Inicializa el servicio Bluetooth LE.
		// Recorre sus características y las activa.
		// --------------------------------------------------------------
		// .........................................................
		// .........................................................
		void activarServicio( ) {
		// entiendo que al llegar aquí ya ha sido configurado
		// todo: características y servicio

		err_t error = (*this).elServicio.begin();
		Serial.print( " (*this).elServicio.begin(); error = " );
		Serial.println( error );

		for( auto pCar : (*this).lasCaracteristicas ) {
			(*pCar).activar();
		} // for

		} // ()

		// --------------------------------------------------------------
		// Función: operator BLEService&()
		//
		// Diseño lógico:
		// operator BLEService&() --> BLEService
		//
		// Permite utilizar un objeto ServicioEnEmisora
		// como referencia a un objeto BLEService.
		// --------------------------------------------------------------
		// .........................................................
		// .........................................................
		operator BLEService&() {
		// "conversión de tipo": si pongo esta clase en un sitio donde necesitan un BLEService
		return elServicio;
		} // ()
		
	}; // class

	#endif

	// ----------------------------------------------------------
	// ----------------------------------------------------------
	// ----------------------------------------------------------
	// ----------------------------------------------------------
