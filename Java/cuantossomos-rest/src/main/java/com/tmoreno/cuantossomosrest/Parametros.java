package com.tmoreno.cuantossomosrest;

import com.tmoreno.cuantossomosrest.exceptions.ParametrosNoValidosException;

public interface Parametros {

	public void validar() throws ParametrosNoValidosException;
}
