package com.tmoreno.cuantossomos.adaptador;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tmoreno.cuantossomos.R;
import com.tmoreno.cuantossomos.modelo.Posicion;

public class AdaptadorLugares extends ArrayAdapter<Posicion> {
	
	private List<Posicion> posiciones;
	private Activity context;
	
	public AdaptadorLugares(Activity context, List<Posicion> posiciones) {
		super(context, R.layout.lugarlistitem, posiciones);
		this.context = context;
		this.posiciones = posiciones;
	}
	
	@Override
	public boolean isEnabled(int position){
		return (posiciones.get(position).getSeccion() == null);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View item = convertView;
		ViewHolder holder;
		
		Posicion p = posiciones.get(position);
		
		if(p.getSeccion() == null){
			if(item == null || !(item instanceof LinearLayout)){
    			LayoutInflater inflater = context.getLayoutInflater();
    			item = inflater.inflate(R.layout.lugarlistitem, null);
    			
    			holder = new ViewHolder();
    			holder.calle = (TextView)item.findViewById(R.id.calle);
    			holder.ciudad = (TextView)item.findViewById(R.id.ciudad);
    			
    			item.setTag(holder);
    		}
    		else{
    			holder = (ViewHolder)item.getTag();
    		}
			
			holder.calle.setText(posiciones.get(position).getCalle());
			holder.ciudad.setText(posiciones.get(position).getCiudad());
		}
		else{
			if(item == null || !(item instanceof TextView)){
    			LayoutInflater inflater = context.getLayoutInflater();
    			item = inflater.inflate(R.layout.lugarlistsection, null);
    			
    			holder = new ViewHolder();
    			holder.seccion = (TextView)item.findViewById(R.id.seccion);
    			
    			item.setTag(holder);
    		}
    		else{
    			holder = (ViewHolder)item.getTag();
    		}
			
			holder.seccion.setText(posiciones.get(position).getSeccion());
		}
		
		return(item);
	}
	
	static class ViewHolder {
		TextView seccion;
		TextView calle;
		TextView ciudad;
	}
}