package com.tmoreno.cuantossomosrest.insertarposicionusuario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.tmoreno.cuantossomosrest.Operacion;
import com.tmoreno.cuantossomosrest.exceptions.PosicionNotFoundException;
import com.tmoreno.cuantossomosrest.getnumeropersonas.GetNumeroPersonasOperacion;
import com.tmoreno.cuantossomosrest.getnumeropersonas.GetNumeroPersonasParametros;

public class InsertarPosicionUsuarioOperacion extends Operacion<InsertarPosicionUsuarioParametros, InsertarPosicionUsuarioResultado> {

	public InsertarPosicionUsuarioOperacion(InsertarPosicionUsuarioParametros parametros) {
		super(parametros);
	}

	@Override
	public List<InsertarPosicionUsuarioResultado> ejecutar() throws Throwable {
		int idPosicion;
		List<InsertarPosicionUsuarioResultado> resultado = new ArrayList<InsertarPosicionUsuarioResultado>();
		
		try {
			idPosicion = getIdPosicion();
		}
		catch (PosicionNotFoundException e) {
			insertarPosicion();
			idPosicion = getIdPosicion();
		}
		
		insertarPosicionUsuario(idPosicion);
		
		GetNumeroPersonasParametros numPersonasParams = new GetNumeroPersonasParametros();
		numPersonasParams.setFecha(parametros.getFecha());
		numPersonasParams.setIdPosicion(idPosicion);
		GetNumeroPersonasOperacion numPersonasOp = new GetNumeroPersonasOperacion(numPersonasParams);
		numPersonasOp.setConnection(connection);
		
		InsertarPosicionUsuarioResultado item = new InsertarPosicionUsuarioResultado();
		item.setIdPosicion(idPosicion);
		item.setNumPersonas(numPersonasOp.ejecutar().get(0).getNumPersonas());
		resultado.add(item);
		
		return resultado;
	}
	
	private int getIdPosicion() throws SQLException, PosicionNotFoundException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		int idPosicion;
		
		String query = "SELECT id " +
					   "  FROM posiciones " +
					   " WHERE pais = ? " +
					   "   AND areaAdministrativa = ? " +
					   "   AND subAreaAdministrativa = ? " +
					   "   AND localidad = ? " +
					   "   AND subLocalidad = ? " +
					   "   AND calle = ?";
		
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, parametros.getCountry());
			ps.setString(2, parametros.getState());
			ps.setString(3, parametros.getCounty());
			ps.setString(4, parametros.getCity());
			ps.setString(5, parametros.getSuburb());
			ps.setString(6, parametros.getRoad());
			
			rs = ps.executeQuery();
			
			if(rs.next()){
				idPosicion = rs.getInt(1);
			}
			else {
				throw new PosicionNotFoundException();
			}
		}
		finally {
			if(rs != null){
				rs.close();
			}
			
			if(ps != null){
				ps.close();
			}
		}
		
		return idPosicion;
	}
	
	private void insertarPosicion() throws SQLException {
		PreparedStatement ps = null;
		
		String query = "INSERT INTO posiciones (pais, areaAdministrativa, " +
					   "                        subAreaAdministrativa, localidad, " +
					   "                        subLocalidad, calle, " +
					   "                        osm_id, place_id) " +
					   "VALUES (?,?,?,?,?,?,?,?)"; 
		
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, parametros.getCountry());
			ps.setString(2, parametros.getState());
			ps.setString(3, parametros.getCounty());
			ps.setString(4, parametros.getCity());
			ps.setString(5, parametros.getSuburb());
			ps.setString(6, parametros.getRoad());
			ps.setLong(7, parametros.getOsm_id());
			ps.setLong(8, parametros.getPlace_id());
			
			ps.executeUpdate();
		}
		finally {
			if(ps != null){
				ps.close();
			}
		}
	}
	
	private void insertarPosicionUsuario(int idPosicion) throws SQLException {
		PreparedStatement ps = null;
		
		String query = "INSERT INTO posiciones_usuarios " +
					   "            (idPosicion, idUsuario, fecha, " +
					   "             latitud, longitud, plataforma) " +
					   "VALUES (?,?,?,?,?,?)"; 
		
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, idPosicion);
			ps.setString(2, parametros.getIdUsuario());
			ps.setTimestamp(3, parametros.getFecha());
			ps.setDouble(4, parametros.getLatitud());
			ps.setDouble(5, parametros.getLongitud());
			ps.setString(6, parametros.getPlataforma());
			
			ps.executeUpdate();
		}
		finally {
			if(ps != null){
				ps.close();
			}
		}
	}
}
