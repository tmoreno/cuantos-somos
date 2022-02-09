package com.tmoreno.cuantossomos.modelo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CuantosSomosSQLiteHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "cuantosSomosDB";
	private static final int VERSION = 1;
	
	public CuantosSomosSQLiteHelper(Context context, CursorFactory factory) {
		super(context, DATABASE_NAME, factory, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Posicion.CREATE_TABLE_POSICION);
		db.execSQL(NumPersonasPosicion.CREATE_TABLE_NUMPERSONASPOSICION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}
