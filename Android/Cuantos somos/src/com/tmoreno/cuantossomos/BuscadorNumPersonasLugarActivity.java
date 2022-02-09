package com.tmoreno.cuantossomos;

import java.util.List;

import com.tmoreno.cuantossomos.adaptador.AdaptadorNumPersonasLugar;
import com.tmoreno.cuantossomos.modelo.CuantosSomosSQLiteHelper;
import com.tmoreno.cuantossomos.modelo.NumPersonasPosicion;

import android.app.ListActivity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class BuscadorNumPersonasLugarActivity extends ListActivity {
	
	private AdaptadorNumPersonasLugar adaptador;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista);
        
        int idPosicion = getIntent().getExtras().getInt(LugaresActivity.ID_POSICION);
        
        CuantosSomosSQLiteHelper cuantosSomosDataBaseHelper = new CuantosSomosSQLiteHelper(this, null);
        SQLiteDatabase db = cuantosSomosDataBaseHelper.getWritableDatabase();
		List<NumPersonasPosicion> numPersonas = NumPersonasPosicion.getNumPersonasPosicion(db, idPosicion);
		db.close();
		
		adaptador = new AdaptadorNumPersonasLugar(this, numPersonas);
		setListAdapter(adaptador);
	}
}
