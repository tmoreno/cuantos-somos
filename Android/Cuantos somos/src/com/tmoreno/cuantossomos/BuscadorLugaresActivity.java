package com.tmoreno.cuantossomos;

import java.util.List;

import com.tmoreno.cuantossomos.adaptador.AdaptadorLugares;
import com.tmoreno.cuantossomos.modelo.CuantosSomosSQLiteHelper;
import com.tmoreno.cuantossomos.modelo.Posicion;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class BuscadorLugaresActivity extends ListActivity{

	private AdaptadorLugares adaptador;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista);
		
		String nombreCalle = getIntent().getStringExtra(SearchManager.QUERY);
		
		CuantosSomosSQLiteHelper cuantosSomosDataBaseHelper = new CuantosSomosSQLiteHelper(this, null);
		SQLiteDatabase db = cuantosSomosDataBaseHelper.getWritableDatabase();
		List<Posicion> posicionesBuscadas = Posicion.getAllPosicionesPorCalle(db, nombreCalle);
		db.close();
		
		adaptador = new AdaptadorLugares(this, posicionesBuscadas);
        setListAdapter(adaptador);
        
        getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int idPosicion = adaptador.getItem(position).getIdPosicion();
				
				Intent intent = new Intent(BuscadorLugaresActivity.this, BuscadorNumPersonasLugarActivity.class);
				intent.putExtra(LugaresActivity.ID_POSICION, idPosicion);
				startActivity(intent);
			}
		});
	}
}
