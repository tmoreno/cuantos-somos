package com.tmoreno.cuantossomosrest;

import java.sql.Connection;
import java.util.List;

/**
 * Clase raíz de la jerarquía de operaciones
 */
public abstract class Operacion<P extends Parametros, R extends Resultado> {
	
	protected P parametros;
	
	protected Connection connection;
	
	public Operacion(P parametros){
		this.parametros = parametros;
	}
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Método que ejecuta la operación
	 * @return 
	 * @throws Throwable 
	 */
	public abstract List<R> ejecutar() throws Throwable;
}
