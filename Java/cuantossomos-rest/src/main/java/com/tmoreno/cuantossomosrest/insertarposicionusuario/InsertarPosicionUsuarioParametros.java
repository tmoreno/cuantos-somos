package com.tmoreno.cuantossomosrest.insertarposicionusuario;

import java.sql.Timestamp;

import com.tmoreno.cuantossomosrest.Parametros;
import com.tmoreno.cuantossomosrest.exceptions.ParametrosNoValidosException;

public class InsertarPosicionUsuarioParametros implements Parametros {

	private String country;
	private String state;
	private String county;
	private String city;
	private String suburb;
	private String road;
	private Double latitud;
	private Double longitud;
	private String plataforma;
	private Long osm_id;
	private Long place_id;
	private String idUsuario;
	private Timestamp fecha;
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCounty() {
		// Si no se ha especificado el condado cogemos el estado
		if(county == null || "".equals(county)){
			return state;
		}
		
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getSuburb() {
		// Si no se ha especificado el suburbio cogemos la ciudad
		if(suburb == null || "".equals(suburb)){
			return city;
		}
		
		return suburb;
	}

	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}

	public String getRoad() {
		return road;
	}

	public void setRoad(String road) {
		this.road = road;
	}	
	
	public Double getLatitud() {
		return latitud;
	}

	public void setLatitud(Double latitud) {
		this.latitud = latitud;
	}

	public Double getLongitud() {
		return longitud;
	}

	public void setLongitud(Double longitud) {
		this.longitud = longitud;
	}
	
	public String getPlataforma() {
		return plataforma;
	}

	public void setPlataforma(String plataforma) {
		this.plataforma = plataforma;
	}

	public Long getOsm_id() {
		return osm_id;
	}

	public void setOsm_id(Long osm_id) {
		this.osm_id = osm_id;
	}

	public Long getPlace_id() {
		return place_id;
	}

	public void setPlace_id(Long place_id) {
		this.place_id = place_id;
	}

	public String getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Timestamp getFecha() {
		return fecha;
	}

	public void setFecha(Timestamp fecha) {
		this.fecha = fecha;
	}
	
	@Override
	public void validar() throws ParametrosNoValidosException {
		if(country == null || "".equals(country) 
		   || state == null || "".equals(state) 
		   || getCounty() == null || "".equals(getCounty())
		   || city == null || "".equals(city)
		   || getSuburb() == null || "".equals(getSuburb())
		   || road == null || "".equals(road)
		   || latitud == null || longitud == null
		   || idUsuario == null || "".equals(idUsuario) 
		   || fecha == null){
			throw new ParametrosNoValidosException();
		}
	}
}
