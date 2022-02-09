package com.tmoreno.cuantossomosrest.exceptions;

public class DBConnectionException extends Exception {
	private static final long serialVersionUID = 1L;

	public DBConnectionException(String message) {
		super(message);
	}
	
	public DBConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
