package com.tmoreno.cuantossomosrest.getnumeropersonas;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.tmoreno.cuantossomosrest.Operacion;

public class GetNumeroPersonasOperacion extends Operacion<GetNumeroPersonasParametros, GetNumeroPersonasResultado> {

	public GetNumeroPersonasOperacion(GetNumeroPersonasParametros parametros) {
		super(parametros);
	}

	@Override
	public List<GetNumeroPersonasResultado> ejecutar() throws Throwable {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List<GetNumeroPersonasResultado> resultado = new ArrayList<GetNumeroPersonasResultado>();
		
		String query = "SELECT count(distinct idUsuario) " +
					   "  FROM posiciones_usuarios " +
					   " WHERE idPosicion = ? " +
					   "   AND fecha >= DATE_SUB(?, INTERVAL 1 HOUR) " +
					   "   AND fecha <= ? ";
		
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, parametros.getIdPosicion());
			ps.setTimestamp(2, parametros.getFecha());
			ps.setTimestamp(3, parametros.getFecha());
			
			rs = ps.executeQuery();
			
			if(rs.next()){
				GetNumeroPersonasResultado numPersonas = new GetNumeroPersonasResultado();
				numPersonas.setNumPersonas(rs.getInt(1));
				resultado.add(numPersonas);
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
		
		return resultado;
	}
}
