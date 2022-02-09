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
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.tmoreno.cuantossomosrest.insertarposicionusuario.InsertarPosicionUsuarioParametros;

public class InsertarPosicionUsuarioIT {
	
	private static final String URL = "http://localhost:8080/cuantossomos-rest";
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private static Gson gson;
	private static WebResource webResource;
	private static InsertarPosicionUsuarioParametros doctorFleming;
	private static InsertarPosicionUsuarioParametros mercedesIllan;
	
	@BeforeClass
	public static void init(){
		gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		
        webResource = Client.create().resource(URL).path("/insertarposicionusuario");
        
        doctorFleming = new InsertarPosicionUsuarioParametros();
		doctorFleming.setCountry("Spain");
		doctorFleming.setState("Murcia");
		doctorFleming.setCounty("Murcia");
		doctorFleming.setCity("Murcia");
		doctorFleming.setSuburb("La Alberca");
		doctorFleming.setRoad("Doctor Fleming");
		doctorFleming.setLatitud(37.938585);
		doctorFleming.setLongitud(-1.144429);
		
		mercedesIllan = new InsertarPosicionUsuarioParametros();
		mercedesIllan.setCountry("Spain");
		mercedesIllan.setState("Murcia");
		mercedesIllan.setCounty("Murcia");
		mercedesIllan.setCity("Murcia");
		mercedesIllan.setSuburb("La Alberca");
		mercedesIllan.setRoad("Mercedes Illan");
		mercedesIllan.setLatitud(37.9373948);
		mercedesIllan.setLongitud(-1.145219);
	}
	
	@Test
    public void peticionNulaTest() {
		try{
			webResource.type(MediaType.APPLICATION_JSON).get(String.class);
		}
		catch (UniformInterfaceException e) {
			assertEquals(Status.PRECONDITION_FAILED, e.getResponse().getClientResponseStatus());
		}
    }

	@Test
    public void peticionVaciaTest() {
		try{
			webResource.queryParam("p", "").type(MediaType.APPLICATION_JSON).get(String.class);
		}
		catch (UniformInterfaceException e) {
			assertEquals(Status.PRECONDITION_FAILED, e.getResponse().getClientResponseStatus());
		}
    }
	
	@Test
    public void posicionJsonVacioTest() {
		try{
			webResource.queryParam("p", "{}").type(MediaType.APPLICATION_JSON).get(String.class);
		}
		catch (UniformInterfaceException e) {
			assertEquals(Status.PRECONDITION_FAILED, e.getResponse().getClientResponseStatus());
		}
    }
	
	@Test
    public void posicionJsonMalFormadoTest() {
		try{
			webResource.queryParam("p", "{sdffd:sdffg}").type(MediaType.APPLICATION_JSON).get(String.class);
		}
		catch (UniformInterfaceException e) {
			assertEquals(Status.PRECONDITION_FAILED, e.getResponse().getClientResponseStatus());
		}
    }
	
	@Test
    public void posicionJsonCampoNoExisteTest() {
		try{
			webResource.queryParam("p", "{\"dfgdfg\":sfgdfg}").type(MediaType.APPLICATION_JSON).get(String.class);
		}
		catch (UniformInterfaceException e) {
			assertEquals(Status.PRECONDITION_FAILED, e.getResponse().getClientResponseStatus());
		}
    }
	
	@Test
    public void posicionIncompletaTest() {
		InsertarPosicionUsuarioParametros posicion = new InsertarPosicionUsuarioParametros();
        posicion.setCountry("Spain");
        posicion.setState("Murcia");
        posicion.setCounty("Murcia");
        
        try{
        	webResource.queryParam("p", gson.toJson(posicion, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		}
		catch (UniformInterfaceException e) {
			assertEquals(Status.PRECONDITION_FAILED, e.getResponse().getClientResponseStatus());
		}
    }
	
	@Test
    public void idPosicionTest() throws ParseException {
        doctorFleming.setIdUsuario("@usuario1");
        doctorFleming.setFecha(new Timestamp(sdf.parse("01/01/2012 11:00:00").getTime()));
		String resultado = webResource.queryParam("p", gson.toJson(doctorFleming, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"idPosicion\": 1,\"numPersonas\": 1}", resultado);
		
        doctorFleming.setFecha(new Timestamp(sdf.parse("01/01/2012 11:10:00").getTime()));
		resultado = webResource.queryParam("p", gson.toJson(doctorFleming, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"idPosicion\": 1,\"numPersonas\": 1}", resultado);
		
        doctorFleming.setIdUsuario("@usuario2");
        doctorFleming.setFecha(new Timestamp(sdf.parse("01/01/2012 11:05:00").getTime()));
		resultado = webResource.queryParam("p", gson.toJson(doctorFleming, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"idPosicion\": 1,\"numPersonas\": 2}", resultado);
		
        mercedesIllan.setIdUsuario("@usuario3");
        mercedesIllan.setFecha(new Timestamp(sdf.parse("01/01/2012 13:00:00").getTime()));
		resultado = webResource.queryParam("p", gson.toJson(mercedesIllan, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"idPosicion\": 2,\"numPersonas\": 1}", resultado);
		
        mercedesIllan.setIdUsuario("@usuario2");
        mercedesIllan.setFecha(new Timestamp(sdf.parse("01/01/2012 13:05:00").getTime()));
		resultado = webResource.queryParam("p", gson.toJson(mercedesIllan, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"idPosicion\": 2,\"numPersonas\": 2}", resultado);
    }
	
	@Test
    public void unUsuarioMercedesIllanTest() throws ParseException {
        mercedesIllan.setIdUsuario("@usuario1");
        mercedesIllan.setFecha(new Timestamp(sdf.parse("02/01/2012 10:00:00").getTime()));
		String resultado = webResource.queryParam("p", gson.toJson(mercedesIllan, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"idPosicion\": 2,\"numPersonas\": 1}", resultado);
		
		mercedesIllan.setFecha(new Timestamp(sdf.parse("02/01/2012 10:05:00").getTime()));
		resultado = webResource.queryParam("p", gson.toJson(mercedesIllan, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"idPosicion\": 2,\"numPersonas\": 1}", resultado);
		
		mercedesIllan.setFecha(new Timestamp(sdf.parse("02/01/2012 11:04:59").getTime()));
		resultado = webResource.queryParam("p", gson.toJson(mercedesIllan, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"idPosicion\": 2,\"numPersonas\": 1}", resultado);
		
		mercedesIllan.setFecha(new Timestamp(sdf.parse("02/01/2012 12:04:59").getTime()));
		resultado = webResource.queryParam("p", gson.toJson(mercedesIllan, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"idPosicion\": 2,\"numPersonas\": 1}", resultado);
		
		mercedesIllan.setFecha(new Timestamp(sdf.parse("02/01/2012 13:05:00").getTime()));
		resultado = webResource.queryParam("p", gson.toJson(mercedesIllan, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"idPosicion\": 2,\"numPersonas\": 1}", resultado);
    }
	
	@Test
    public void dosUsuariosMismaHoraMercedesIllanTest() throws ParseException {
        mercedesIllan.setIdUsuario("@usuario1");
        mercedesIllan.setFecha(new Timestamp(sdf.parse("03/01/2012 10:00:00").getTime()));
		String resultado = webResource.queryParam("p", gson.toJson(mercedesIllan, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"idPosicion\": 2,\"numPersonas\": 1}", resultado);
		
		mercedesIllan.setIdUsuario("@usuario2");
        mercedesIllan.setFecha(new Timestamp(sdf.parse("03/01/2012 10:00:00").getTime()));
		resultado = webResource.queryParam("p", gson.toJson(mercedesIllan, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"idPosicion\": 2,\"numPersonas\": 2}", resultado);
    }
	
	@Test
    public void dosUsuariosCasiUnaHoraMercedesIllanTest() throws ParseException {
        mercedesIllan.setIdUsuario("@usuario1");
        mercedesIllan.setFecha(new Timestamp(sdf.parse("04/01/2012 10:00:00").getTime()));
		String resultado = webResource.queryParam("p", gson.toJson(mercedesIllan, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"idPosicion\": 2,\"numPersonas\": 1}", resultado);
		
		mercedesIllan.setIdUsuario("@usuario2");
        mercedesIllan.setFecha(new Timestamp(sdf.parse("04/01/2012 10:59:59").getTime()));
		resultado = webResource.queryParam("p", gson.toJson(mercedesIllan, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"idPosicion\": 2,\"numPersonas\": 2}", resultado);
    }
	
	@Test
    public void dosUsuariosUnaHoraMercedesIllanTest() throws ParseException {
        mercedesIllan.setIdUsuario("@usuario1");
        mercedesIllan.setFecha(new Timestamp(sdf.parse("05/01/2012 10:00:00").getTime()));
		String resultado = webResource.queryParam("p", gson.toJson(mercedesIllan, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"idPosicion\": 2,\"numPersonas\": 1}", resultado);
		
		mercedesIllan.setIdUsuario("@usuario2");
        mercedesIllan.setFecha(new Timestamp(sdf.parse("05/01/2012 11:00:00").getTime()));
		resultado = webResource.queryParam("p", gson.toJson(mercedesIllan, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"idPosicion\": 2,\"numPersonas\": 2}", resultado);
    }
	
	@Test
    public void dosUsuariosDespuesHoraMercedesIllanTest() throws ParseException {
        mercedesIllan.setIdUsuario("@usuario1");
        mercedesIllan.setFecha(new Timestamp(sdf.parse("06/01/2012 10:00:00").getTime()));
		String resultado = webResource.queryParam("p", gson.toJson(mercedesIllan, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"idPosicion\": 2,\"numPersonas\": 1}", resultado);
		
		mercedesIllan.setIdUsuario("@usuario2");
        mercedesIllan.setFecha(new Timestamp(sdf.parse("06/01/2012 11:00:01").getTime()));
		resultado = webResource.queryParam("p", gson.toJson(mercedesIllan, InsertarPosicionUsuarioParametros.class)).type(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"idPosicion\": 2,\"numPersonas\": 1}", resultado);
    }
}
