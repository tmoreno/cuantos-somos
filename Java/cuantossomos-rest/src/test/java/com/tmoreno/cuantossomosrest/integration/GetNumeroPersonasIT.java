package com.tmoreno.cuantossomosrest.integration;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.ws.rs.core.MediaType;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.tmoreno.cuantossomosrest.getnumeropersonas.GetNumeroPersonasParametros;
import com.tmoreno.cuantossomosrest.insertarposicionusuario.InsertarPosicionUsuarioParametros;

public class GetNumeroPersonasIT {

	private static final String URL = "http://localhost:8080/cuantossomos-rest";
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private static Gson gson;
	private static WebResource numeroPersonasWebResource;
	private static WebResource insertarPosicionWebResource;
	private static InsertarPosicionUsuarioParametros doctorFleming;
	
	@BeforeClass
	public static void init(){
		gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		
		numeroPersonasWebResource = Client.create().resource(URL).path("/getnumeropersonas");
		insertarPosicionWebResource = Client.create().resource(URL).path("/insertarposicionusuario");
			
        doctorFleming = new InsertarPosicionUsuarioParametros();
		doctorFleming.setCountry("Spain");
		doctorFleming.setState("Murcia");
		doctorFleming.setCounty("Murcia");
		doctorFleming.setCity("Murcia");
		doctorFleming.setSuburb("La Alberca");
		doctorFleming.setRoad("Doctor Fleming");
		doctorFleming.setLatitud(37.938585);
		doctorFleming.setLongitud(-1.144429);
	}
	
	@Test
    public void peticionNulaTest() {
		try{
			numeroPersonasWebResource.type(MediaType.APPLICATION_JSON).get(String.class);
		}
		catch (UniformInterfaceException e) {
			assertEquals(Status.PRECONDITION_FAILED, e.getResponse().getClientResponseStatus());
		}
    }

	@Test
    public void peticionVaciaTest() {
		try{
			numeroPersonasWebResource.queryParam("p", "").type(MediaType.APPLICATION_JSON).get(String.class);
		}
		catch (UniformInterfaceException e) {
			assertEquals(Status.PRECONDITION_FAILED, e.getResponse().getClientResponseStatus());
		}
    }
	
	@Test
    public void posicionJsonVacioTest() {
		try{
			numeroPersonasWebResource.queryParam("p", "{}").type(MediaType.APPLICATION_JSON).get(String.class);
		}
		catch (UniformInterfaceException e) {
			assertEquals(Status.PRECONDITION_FAILED, e.getResponse().getClientResponseStatus());
		}
    }
	
	@Test
    public void posicionJsonMalFormadoTest() {
		try{
			numeroPersonasWebResource.queryParam("p", "{sdffd:sdffg}").type(MediaType.APPLICATION_JSON).get(String.class);
		}
		catch (UniformInterfaceException e) {
			assertEquals(Status.PRECONDITION_FAILED, e.getResponse().getClientResponseStatus());
		}
    }
	
	@Test
    public void posicionJsonCampoNoExisteTest() {
		try{
			numeroPersonasWebResource.queryParam("p", "{\"dfgdfg\":sfgdfg}").type(MediaType.APPLICATION_JSON).get(String.class);
		}
		catch (UniformInterfaceException e) {
			assertEquals(Status.PRECONDITION_FAILED, e.getResponse().getClientResponseStatus());
		}
    }
	
	@Test
    public void faltaParametroFechaTest() {
		GetNumeroPersonasParametros parametros = new GetNumeroPersonasParametros();
		parametros.setIdPosicion(3);
        
        try{
        	numeroPersonasWebResource.queryParam("p", gson.toJson(parametros, GetNumeroPersonasParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		}
		catch (UniformInterfaceException e) {
			assertEquals(Status.PRECONDITION_FAILED, e.getResponse().getClientResponseStatus());
		}
    }
	
	@Test
    public void faltaParametroIdPosicionTest() throws ParseException {
		GetNumeroPersonasParametros parametros = new GetNumeroPersonasParametros();
		parametros.setFecha(new Timestamp(sdf.parse("02/01/2012 12:04:59").getTime()));
        
        try{
        	numeroPersonasWebResource.queryParam("p", gson.toJson(parametros, GetNumeroPersonasParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		}
		catch (UniformInterfaceException e) {
			assertEquals(Status.PRECONDITION_FAILED, e.getResponse().getClientResponseStatus());
		}
    }
	
	@Test
    public void noExisteIdPosicionTest() throws ParseException {
		GetNumeroPersonasParametros parametros = new GetNumeroPersonasParametros();
		parametros.setIdPosicion(999);
		parametros.setFecha(new Timestamp(sdf.parse("01/01/2012 11:00:00").getTime()));
		
		String resultado = numeroPersonasWebResource.queryParam("p", gson.toJson(parametros, GetNumeroPersonasParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"numPersonas\": 0}", resultado);
    }
	
	@Test
    public void numeroPersonasTest() throws ParseException {
		doctorFleming.setIdUsuario("@usuario1");
        doctorFleming.setFecha(new Timestamp(sdf.parse("01/01/2012 11:00:00").getTime()));
        insertarPosicionWebResource.queryParam("p", gson.toJson(doctorFleming, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		
		GetNumeroPersonasParametros parametros = new GetNumeroPersonasParametros();
		parametros.setIdPosicion(1);
		parametros.setFecha(new Timestamp(sdf.parse("01/01/2012 11:00:00").getTime()));
		String resultado = numeroPersonasWebResource.queryParam("p", gson.toJson(parametros, GetNumeroPersonasParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"numPersonas\": 1}", resultado);
    		
		parametros = new GetNumeroPersonasParametros();
		parametros.setIdPosicion(1);
		parametros.setFecha(new Timestamp(sdf.parse("01/01/2012 11:00:01").getTime()));
		resultado = numeroPersonasWebResource.queryParam("p", gson.toJson(parametros, GetNumeroPersonasParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"numPersonas\": 1}", resultado);
		
		parametros = new GetNumeroPersonasParametros();
		parametros.setIdPosicion(1);
		parametros.setFecha(new Timestamp(sdf.parse("01/01/2012 11:59:59").getTime()));
		resultado = numeroPersonasWebResource.queryParam("p", gson.toJson(parametros, GetNumeroPersonasParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"numPersonas\": 1}", resultado);
		
		parametros = new GetNumeroPersonasParametros();
		parametros.setIdPosicion(1);
		parametros.setFecha(new Timestamp(sdf.parse("01/01/2012 12:00:00").getTime()));
		resultado = numeroPersonasWebResource.queryParam("p", gson.toJson(parametros, GetNumeroPersonasParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"numPersonas\": 1}", resultado);
		
		parametros = new GetNumeroPersonasParametros();
		parametros.setIdPosicion(1);
		parametros.setFecha(new Timestamp(sdf.parse("01/01/2012 12:00:01").getTime()));
		resultado = numeroPersonasWebResource.queryParam("p", gson.toJson(parametros, GetNumeroPersonasParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"numPersonas\": 0}", resultado);
    }
}
