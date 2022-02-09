package com.tmoreno.cuantossomos;

import java.util.ArrayList;
import java.util.List;

import com.tmoreno.cuantossomos.adaptador.AdaptadorLugares;
import com.tmoreno.cuantossomos.modelo.CuantosSomosSQLiteHelper;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class LugaresActivity extends ListActivity {
	
	public static final String ID_POSICION = "idPosicion";

	private int numPosicionesAnterior;
	
	private CuantosSomosSQLiteHelper cuantosSomosDataBaseHelper;
	private AdaptadorLugares adaptador;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista);
        
        numPosicionesAnterior = 0;
        cuantosSomosDataBaseHelper = new CuantosSomosSQLiteHelper(this, null);
        
        getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int idPosicion = adaptador.getItem(position).getIdPosicion();
				
				Intent intent = new Intent(getParent(), NumPersonasLugarActivity.class);
				intent.putExtra(ID_POSICION, idPosicion);
				
				LugaresActivityGroup parentActivity = (LugaresActivityGroup)getParent();
	            parentActivity.startChildActivity(NumPersonasLugarActivity.class.getSimpleName(), intent);
			}
		});
        
        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final Posicion p = adaptador.getItem(position);
				final int posicion = position;
				
				AlertDialog.Builder adb = new AlertDialog.Builder(getParent());
				adb.setTitle(getString(R.string.borrarPosicion, p.getCalle()));
				adb.setNegativeButton(R.string.cancelar, null);
				adb.setPositiveButton(R.string.aceptar, new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						SQLiteDatabase db = cuantosSomosDataBaseHelper.getWritableDatabase();
						Posicion.borrar(db, p.getIdPosicion());
						db.close();
						
						if(borrarSeccion(posicion)){
							adaptador.remove(adaptador.getItem(posicion - 1));
						}
						adaptador.remove(p);
						
						// Si se introduce una nueva posición desde la pantalla
						// principal, se podrá refrescar la pantalla porque el
						// número de posiciones es distinto, si no se hace el
						// decremento, sería el mismo
						numPosicionesAnterior--;
						
						adaptador.notifyDataSetChanged();
					}
				});
				adb.show();
				return true;
			}

			private boolean borrarSeccion(int position) {
				boolean borrarSeccion = false;
				
				if(adaptador.getItem(position - 1).getSeccion() != null){
					if(position == adaptador.getCount() - 1){
						borrarSeccion = true;
					}
					else if(adaptador.getItem(position + 1).getSeccion() != null){
						borrarSeccion = true;
					}
				}
				
				return borrarSeccion;
			}
		});
    }
	
	@Override
	public void onResume(){
		super.onResume();

		SQLiteDatabase db = cuantosSomosDataBaseHelper.getWritableDatabase();
		List<Posicion> posicionesEnBd = Posicion.getAllPosiciones(db);
		db.close();
		
		if(numPosicionesAnterior != posicionesEnBd.size()){
			numPosicionesAnterior = posicionesEnBd.size();
			
			posicionesEnBd = ponerSecciones(posicionesEnBd);
	        
			adaptador = new AdaptadorLugares(this, posicionesEnBd);
	        getListView().setAdapter(adaptador);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.buscar).setIcon(android.R.drawable.ic_menu_search);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		getParent().onSearchRequested();
	    return true;
	}

	/**
	 * Añada las secciones a la lista de posiciones para luego ser mostradas en el ListView
	 * @param posiciones
	 * @return
	 */
	private List<Posicion> ponerSecciones(List<Posicion> posiciones) {
		String inicialActual = null;
		String inicialAnterior = null;
		Posicion posicionConInicial = null;
		List<Posicion> posicionesConSecciones = new ArrayList<Posicion>();
		
		for(Posicion p : posiciones){
			inicialActual = p.getCalle().toUpperCase().charAt(0)+"";
			
			if(inicialAnterior == null || !inicialActual.equals(inicialAnterior)){
				inicialAnterior = inicialActual;
				
				posicionConInicial = new Posicion();
				posicionConInicial.setSeccion(inicialActual);
				
				posicionesConSecciones.add(posicionConInicial);
			}
			
			posicionesConSecciones.add(p);
		}
		
		return posicionesConSecciones;
	}
}