package com.tmoreno.cuantossomos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.tmoreno.cuantossomos.adaptador.AdaptadorNumPersonasLugar;
import com.tmoreno.cuantossomos.modelo.CuantosSomosSQLiteHelper;
import com.tmoreno.cuantossomos.modelo.NumPersonasPosicion;
import com.tmoreno.cuantossomos.modelo.Posicion;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

public class NumPersonasLugarActivity extends ListActivity {
	
	private static final SimpleDateFormat FECHA_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	private CuantosSomosSQLiteHelper cuantosSomosDataBaseHelper;
	private AdaptadorNumPersonasLugar adaptador;

	private Posicion posicion;
	private List<NumPersonasPosicion> numPersonas;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista);
        
        int idPosicion = getIntent().getExtras().getInt(LugaresActivity.ID_POSICION);
        cuantosSomosDataBaseHelper = new CuantosSomosSQLiteHelper(this, null);
        
        SQLiteDatabase db = cuantosSomosDataBaseHelper.getWritableDatabase();
		numPersonas = NumPersonasPosicion.getNumPersonasPosicion(db, idPosicion);
		posicion = Posicion.getPosicion(db, idPosicion);
		db.close();
		
		adaptador = new AdaptadorNumPersonasLugar(this, numPersonas);
        getListView().setAdapter(adaptador);
        
        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final NumPersonasPosicion n = adaptador.getItem(position);
				String fecha = FECHA_FORMAT.format(n.getFecha());
				
				AlertDialog.Builder adb = new AlertDialog.Builder(getParent());
				adb.setTitle(getString(R.string.borrarNumPersonasPosicion, fecha));
				adb.setNegativeButton(R.string.cancelar, null);
				adb.setPositiveButton(R.string.aceptar, new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						SQLiteDatabase db = cuantosSomosDataBaseHelper.getWritableDatabase();
						NumPersonasPosicion.borrar(db, n.getId());
						db.close();
						
						adaptador.remove(n);
						adaptador.notifyDataSetChanged();
					}
				});
				adb.show();
				return true;
			}
        });
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.cuantossomosfuimos).setIcon(android.R.drawable.ic_menu_search);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(getParent(), CuantosFuimosActivity.class);
		intent.putExtra(CuantosFuimosActivity.ID_POSICION, posicion.getIdPosicion());
		intent.putExtra(CuantosFuimosActivity.CALLE, posicion.getCalle());
		
		if(numPersonas.size() == 0){
			intent.putExtra(CuantosFuimosActivity.NUM_PERSONAS, 0);
			intent.putExtra(CuantosFuimosActivity.FECHA, FECHA_FORMAT.format(new Date()));
		}
		else{
			NumPersonasPosicion n = numPersonas.get(0);
			intent.putExtra(CuantosFuimosActivity.NUM_PERSONAS, n.getNumPersonas());
			intent.putExtra(CuantosFuimosActivity.FECHA, FECHA_FORMAT.format(n.getFecha()));
		}
		
		LugaresActivityGroup parentActivity = (LugaresActivityGroup)getParent();
        parentActivity.startChildActivity(CuantosFuimosActivity.class.getSimpleName(), intent);
        
	    return true;
	}
}