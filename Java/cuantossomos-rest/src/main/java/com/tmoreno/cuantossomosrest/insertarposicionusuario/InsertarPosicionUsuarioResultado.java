package com.tmoreno.cuantossomosrest.insertarposicionusuario;

import com.tmoreno.cuantossomosrest.Resultado;

public class InsertarPosicionUsuarioResultado implements Resultado {

	private int idPosicion;
	private int numPersonas;
	
	public int getIdPosicion() {
		return idPosicion;
	}

	public void setIdPosicion(int idPosicion) {
		this.idPosicion = idPosicion;
	}

	public int getNumPersonas() {
		return numPersonas;
	}
	
	public void setNumPersonas(int numPersonas) {
		this.numPersonas = numPersonas;
	}
	
	@Override
	public String toJson(){
		return "{\"idPosicion\": " + idPosicion + ",\"numPersonas\": " + numPersonas + "}";
	}
}
