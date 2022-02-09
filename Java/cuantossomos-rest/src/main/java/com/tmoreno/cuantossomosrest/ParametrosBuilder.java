package com.tmoreno.cuantossomosrest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.tmoreno.cuantossomosrest.exceptions.ParametrosNoValidosException;

public class ParametrosBuilder {

	/**
	 * Método que transforma el JSON en el tipo de parámetro indicado, además valida
	 * que los parámetros sean válidos
	 * @param json
	 * @param tipo
	 * @return
	 * @throws ParametrosNoValidosException
	 */
	public static Parametros build(String json, Class<?> tipo) throws ParametrosNoValidosException {
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		
		if(json == null || "".equals(json)){
			throw new ParametrosNoValidosException();
		}
		
		Parametros parametros = (Parametros) gson.fromJson(json, tipo);
		parametros.validar();
		
		return parametros;
	}
}
