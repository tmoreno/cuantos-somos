package com.tmoreno.cuantossomosrest.getnumeropersonas;

import com.tmoreno.cuantossomosrest.Resultado;

public class GetNumeroPersonasResultado implements Resultado {

	private int numPersonas;
	
	public int getNumPersonas() {
		return numPersonas;
	}
	
	public void setNumPersonas(int numPersonas) {
		this.numPersonas = numPersonas;
	}
	
	@Override
	public String toJson(){
		return "{\"numPersonas\": " + numPersonas + "}";
	}
}
