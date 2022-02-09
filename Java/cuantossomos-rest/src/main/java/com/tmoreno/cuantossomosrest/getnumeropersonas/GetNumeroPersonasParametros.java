package com.tmoreno.cuantossomosrest.getnumeropersonas;

import java.sql.Timestamp;

import com.tmoreno.cuantossomosrest.Parametros;
import com.tmoreno.cuantossomosrest.exceptions.ParametrosNoValidosException;

public class GetNumeroPersonasParametros implements Parametros {

	private int idPosicion;
	private Timestamp fecha;
	
	public int getIdPosicion() {
		return idPosicion;
	}
	
	public void setIdPosicion(int idPosicion) {
		this.idPosicion = idPosicion;
	}
	
	public Timestamp getFecha() {
		return fecha;
	}
	
	public void setFecha(Timestamp fecha) {
		this.fecha = fecha;
	}

	@Override
	public void validar() throws ParametrosNoValidosException {
		if(idPosicion == 0 || fecha == null){
			throw new ParametrosNoValidosException();
		}
	}	
}
