package com.tmoreno.cuantossomosrest.servicios;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonSyntaxException;
import com.tmoreno.cuantossomosrest.ParametrosBuilder;
import com.tmoreno.cuantossomosrest.Util;
import com.tmoreno.cuantossomosrest.buscador.BuscadorOperacion;
import com.tmoreno.cuantossomosrest.exceptions.ParametrosNoValidosException;
import com.tmoreno.cuantossomosrest.getnumeropersonas.GetNumeroPersonasParametros;
import com.tmoreno.cuantossomosrest.getnumeropersonas.GetNumeroPersonasResultado;

@Path("/buscador")
public class Buscador {
	
	private final Logger logger = LoggerFactory.getLogger(Buscador.class);

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String buscador(@QueryParam("p") String json) {
		Connection con = null;
		List<GetNumeroPersonasResultado> resultado = null;
		
		try {			
			GetNumeroPersonasParametros parametros = (GetNumeroPersonasParametros) 
				ParametrosBuilder.build(json, GetNumeroPersonasParametros.class);
			
			BuscadorOperacion operacion = new BuscadorOperacion(parametros);
			
			con = Util.getDBConnection();
			
			operacion.setConnection(con);
			
			resultado = operacion.ejecutar();
			
			con.commit();
		}
		catch (JsonSyntaxException e) {
			logger.error("Error del parser JSON", e);
			throw new WebApplicationException(Status.PRECONDITION_FAILED);
		}
		catch (ParametrosNoValidosException e) {
			logger.error("Error en la validación de parámetros", e);
			throw new WebApplicationException(Status.PRECONDITION_FAILED);
		}
		catch (Throwable t) {
			if(con != null){
				try {
					con.rollback();
				} 
				catch (SQLException e) { 
					logger.error("Error al hacer rollback");
				}
			}
			
			logger.error("Error", t);
			throw new WebApplicationException();
		}
		finally {
			if(con != null){
				try {
					con.close();
				}
				catch (SQLException e) {
					logger.error("Error al cerrar la conexión de base de datos");
				}
			}
		}
		
		return resultado.get(0).toJson();
	}
}
