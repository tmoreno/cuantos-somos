package com.tmoreno.cuantossomos.adaptador;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tmoreno.cuantossomos.R;
import com.tmoreno.cuantossomos.modelo.NumPersonasPosicion;

public class AdaptadorNumPersonasLugar extends ArrayAdapter<NumPersonasPosicion> {
	
	private static final SimpleDateFormat FECHA_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat HORA_FORMAT = new SimpleDateFormat("HH:mm");
	
	private List<NumPersonasPosicion> numPersonas;
	private Activity context;
	
	public AdaptadorNumPersonasLugar(Activity context, List<NumPersonasPosicion> numPersonas) {
		super(context, R.layout.numpersonaslistitem, numPersonas);
		this.context = context;
		this.numPersonas = numPersonas;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View item = convertView;
		ViewHolder holder;
		
		if(item == null || !(item instanceof LinearLayout)){
			LayoutInflater inflater = context.getLayoutInflater();
			item = inflater.inflate(R.layout.numpersonaslistitem, null);
			
			holder = new ViewHolder();
			holder.fecha = (TextView)item.findViewById(R.id.fecha);
			holder.numPersonas = (TextView)item.findViewById(R.id.numPersonas);
			
			item.setTag(holder);
		}
		else{
			holder = (ViewHolder)item.getTag();
		}
		
		NumPersonasPosicion n = numPersonas.get(position);
		
		String fecha = FECHA_FORMAT.format(n.getFecha()) + " " + HORA_FORMAT.format(n.getHora());
		holder.fecha.setText(fecha);
		
		String numeroPersonas = context.getString(R.string.nPersonas, n.getNumPersonas());
		if(n.getNumPersonas() == 1){
			numeroPersonas = context.getString(R.string.unaPersona);
		}
		holder.numPersonas.setText(numeroPersonas);
		
		return(item);
	}
	
	static class ViewHolder {
		TextView fecha;
		TextView numPersonas;
	}
}