package com.tmoreno.cuantossomosrest;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.tmoreno.cuantossomosrest.exceptions.DBConnectionException;

public class Util {

	private static final String JNDI_NOMBRE = "java:comp/env/jdbc/cuantossomos";

	/**
	 * Método que recupera una conexión de base de datos
	 * @return
	 * @throws DBConnectionException
	 */
	public static Connection getDBConnection() throws DBConnectionException {
		Connection connection = null;
		
		try {
			Context initialContext = new InitialContext();
			DataSource datasource = (DataSource) initialContext.lookup(JNDI_NOMBRE);
			
			if (datasource != null) {
				connection = datasource.getConnection();
				connection.setAutoCommit(false);
			} 
			else {
				throw new DBConnectionException("No se ha podido crear el origen de datos");
			}
		} 
		catch (NamingException e) {
			throw new DBConnectionException("No se ha podido crear el origen de datos", e);
		} 
		catch (SQLException e) {
			throw new DBConnectionException("No se ha podido crear el origen de datos", e);
		}
		
		return connection;
	}
}
