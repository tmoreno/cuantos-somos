package com.tmoreno.cuantossomos.modelo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumPersonasPosicion {
	
	public static final String CREATE_TABLE_NUMPERSONASPOSICION = "CREATE TABLE numpersonasposicion (id INTEGER PRIMARY KEY, idposicion INTEGER, numpersonas INTEGER, fecha TIMESTAMP, hora TIMESTAMP)";
	private static final String SELECT_NUMPERSONASPOSICION_FECHA = "SELECT * FROM numpersonasposicion WHERE idposicion = ? AND fecha = ?";
	private static final String SELECT_NUMPERSONASPOSICION = "SELECT * FROM numpersonasposicion WHERE idposicion = ? ORDER BY fecha DESC, hora DESC";
	private static final SimpleDateFormat diaFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss");
	
	private int id;
	private int idPosicion;
	private int numPersonas;
	private Date fecha;
	private Date hora;
	
	/**
	 * Obtiene el número de personas uqe habían en una posición
	 * @param db
	 * @param idPosicion
	 * @return
	 */
	public static List<NumPersonasPosicion> getNumPersonasPosicion(SQLiteDatabase db, int idPosicion) {
		NumPersonasPosicion numPersonasPosicion = null;
		List<NumPersonasPosicion> numPersonas = new ArrayList<NumPersonasPosicion>();
		
		String[] args = new String[] {String.valueOf(idPosicion)};
		Cursor c = db.rawQuery(SELECT_NUMPERSONASPOSICION, args);
		
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			numPersonasPosicion = new NumPersonasPosicion();
			numPersonasPosicion.setId(c.getInt(0));
			numPersonasPosicion.setIdPosicion(c.getInt(1));
			numPersonasPosicion.setNumPersonas(c.getInt(2));
			numPersonasPosicion.setFecha(new Date(c.getLong(3)));
			numPersonasPosicion.setHora(new Date(c.getLong(4)));
			
			numPersonas.add(numPersonasPosicion);
		}
		
		return numPersonas;
	}
	
	/**
	 * Obtiene el número de personas que había en una posición y fecha
	 * @param db
	 * @param idPosicion
	 * @param fecha
	 * @return
	 * @throws ParseException
	 */
	public static NumPersonasPosicion getNumPersonasPosicion(SQLiteDatabase db, int idPosicion, String fecha) throws ParseException{
		NumPersonasPosicion numPersonasPosicion = null;
		
		String dia = fecha.substring(0, fecha.indexOf(" "));
		
		String[] args = new String[] {String.valueOf(idPosicion), String.valueOf(diaFormat.parse(dia).getTime())};
		Cursor c = db.rawQuery(SELECT_NUMPERSONASPOSICION_FECHA, args);
		
		if (c.moveToFirst()) {
			numPersonasPosicion = new NumPersonasPosicion();
			numPersonasPosicion.setId(c.getInt(0));
			numPersonasPosicion.setIdPosicion(c.getInt(1));
			numPersonasPosicion.setNumPersonas(c.getInt(2));
			numPersonasPosicion.setFecha(new Date(c.getLong(3)));
			numPersonasPosicion.setHora(new Date(c.getLong(4)));
		}
		
		return numPersonasPosicion;
	}
	
	/**
	 * Inserta el número de personas que hay en una posición y fecha
	 * @param db
	 * @param idPosicion
	 * @param numPersonas
	 * @param fecha
	 * @throws ParseException
	 */
	public static void insert(SQLiteDatabase db, int idPosicion, int numPersonas, String fecha) throws ParseException {
		String dia = fecha.substring(0, fecha.indexOf(" "));
		String hora = fecha.substring(fecha.indexOf(" ") + 1);
		
		ContentValues nuevoRegistro = new ContentValues();
		nuevoRegistro.put("idPosicion", idPosicion);
		nuevoRegistro.put("numPersonas", numPersonas);
		nuevoRegistro.put("fecha", diaFormat.parse(dia).getTime());
		nuevoRegistro.put("hora", horaFormat.parse(hora).getTime());
		
		db.insert("numpersonasposicion", null, nuevoRegistro);
	}
	
	/**
	 * Actualiza el número de personas que hay en una fecha
	 * @param db
	 * @param id
	 * @param numPersonas
	 * @param fecha
	 * @throws ParseException
	 */
	public static void update(SQLiteDatabase db, int id, int numPersonas, String fecha) throws ParseException {
		String hora = fecha.substring(fecha.indexOf(" ") + 1);
		
		ContentValues nuevoRegistro = new ContentValues();
		nuevoRegistro.put("numPersonas", numPersonas);
		nuevoRegistro.put("hora", horaFormat.parse(hora).getTime());
		
		String[] args = new String[] {String.valueOf(id)};
		
		db.update("numpersonasposicion", nuevoRegistro, "id=?", args);
	}

	/**
	 * Borra el número de personas dado
	 * @param db
	 * @param id
	 */
	public static void borrar(SQLiteDatabase db, int id) {
		String[] args = new String[] {String.valueOf(id)};
		
		db.delete("numpersonasposicion", "id=?", args);
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
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
	
	public Date getFecha() {
		return fecha;
	}
	
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	public Date getHora() {
		return hora;
	}
	
	public void setHora(Date hora) {
		this.hora = hora;
	}
}