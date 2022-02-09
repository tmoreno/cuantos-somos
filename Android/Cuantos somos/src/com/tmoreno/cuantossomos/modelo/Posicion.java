package com.tmoreno.cuantossomos.modelo;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Posicion {
	
	public static final String CREATE_TABLE_POSICION = "CREATE TABLE posicion (idposicion INTEGER PRIMARY KEY, calle VARCHAR, ciudad VARCHAR)";
	private static final String SELECT_POSICION = "SELECT * FROM posicion WHERE idposicion = ?";
	private static final String SELECT_ALL_POSICION = "SELECT * FROM posicion ORDER BY calle";
	private static final String SELECT_ALL_POSICION_NOMBRECALLE = "SELECT * FROM posicion WHERE calle like ? ORDER BY calle";
	
	private int idPosicion;
	private String seccion;
	private String calle;
	private String ciudad;
	
	/**
	 * Obtiene todas las posiciones
	 * @param db
	 * @return
	 */
	public static List<Posicion> getAllPosiciones(SQLiteDatabase db){
		Posicion posicion = null;
		List<Posicion> posiciones = new ArrayList<Posicion>();
		
		Cursor c = db.rawQuery(SELECT_ALL_POSICION, new String [0]);
		
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			posicion = new Posicion();
			posicion.setIdPosicion(c.getInt(0));
			posicion.setCalle(c.getString(1));
			posicion.setCiudad(c.getString(2));
			
			posiciones.add(posicion);
		}
		
		return posiciones;
	}
	
	/**
	 * Obtiene todas las posiciones según el nombre de la calle
	 * @param db
	 * @param nombreCalle
	 * @return
	 */
	public static List<Posicion> getAllPosicionesPorCalle(SQLiteDatabase db, String nombreCalle) {
		Posicion posicion = null;
		List<Posicion> posiciones = new ArrayList<Posicion>();
		
		Cursor c = db.rawQuery(SELECT_ALL_POSICION_NOMBRECALLE, new String [] {"%"+nombreCalle+"%"});
		
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			posicion = new Posicion();
			posicion.setIdPosicion(c.getInt(0));
			posicion.setCalle(c.getString(1));
			posicion.setCiudad(c.getString(2));
			
			posiciones.add(posicion);
		}
		
		return posiciones;
	}
	
	/**
	 * Obtiene una posición
	 * @param db
	 * @param idPosicion
	 * @return
	 */
	public static Posicion getPosicion(SQLiteDatabase db, int idPosicion){
		Posicion posicion = null;
		
		String[] args = new String[] {String.valueOf(idPosicion)};
		Cursor c = db.rawQuery(SELECT_POSICION, args);
		
		if (c.moveToFirst()) {
			posicion = new Posicion();
			posicion.setIdPosicion(c.getInt(0));
			posicion.setCalle(c.getString(1));
			posicion.setCiudad(c.getString(2));
		}
		
		return posicion;
	}
	
	/**
	 * Inserta una nueva posición
	 * @param db
	 * @param idPosicion
	 * @param calle
	 * @param ciudad
	 */
	public static void insert(SQLiteDatabase db, int idPosicion, String calle, String ciudad) {
		ContentValues nuevoRegistro = new ContentValues();
		nuevoRegistro.put("idPosicion", idPosicion);
		nuevoRegistro.put("calle", calle);
		nuevoRegistro.put("ciudad", ciudad);
		
		db.insert("posicion", null, nuevoRegistro);
	}
	
	/**
	 * Borra una posición
	 * @param db
	 * @param idPosicion
	 */
	public static void borrar(SQLiteDatabase db, int idPosicion) {
		String[] args = new String[] {String.valueOf(idPosicion)};
		
		db.delete("posicion", "idPosicion=?", args);
		db.delete("numpersonasposicion", "idPosicion=?", args);
	}
	
	public int getIdPosicion() {
		return idPosicion;
	}
	
	public void setIdPosicion(int idPosicion) {
		this.idPosicion = idPosicion;
	}
	
	public String getSeccion() {
		return seccion;
	}

	public void setSeccion(String seccion) {
		this.seccion = seccion;
	}

	public String getCalle() {
		return calle;
	}
	
	public void setCalle(String calle) {
		this.calle = calle;
	}
	
	public String getCiudad() {
		return ciudad;
	}
	
	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}
}