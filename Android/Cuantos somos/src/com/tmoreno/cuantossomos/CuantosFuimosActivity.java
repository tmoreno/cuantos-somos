package com.tmoreno.cuantossomos;

import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class CuantosFuimosActivity extends Activity {

	public static final String ID_POSICION = "idPosicion";
	public static final String CALLE = "calle";
	public static final String NUM_PERSONAS = "numPersonas";
	public static final String FECHA = "fecha";
	
	private static final int TIMEOUT_CONNECTION = 10000;
	
	private int idPosicion;
	private String calle;
	private int numPersonas;
	private String fecha;
	
	private TextView numPersonasTextView;
	private DatePicker datePicker;
	private ProgressDialog progressDailog;
	private BuscadorTask buscadorTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cuantosfuimos);
        
        idPosicion = getIntent().getExtras().getInt(ID_POSICION);
        calle = getIntent().getExtras().getString(CALLE);
        numPersonas = getIntent().getExtras().getInt(NUM_PERSONAS);
        fecha = getIntent().getExtras().getString(FECHA);
        
        progressDailog = new ProgressDialog(getParent());
		progressDailog.setMessage(getString(R.string.espere));
        
        TextView calleTextView = (TextView) findViewById(R.id.calle);
        calleTextView.setText(calle);
        
        numPersonasTextView = (TextView) findViewById(R.id.numpersonas);
        numPersonasTextView.setText(numPersonasText(numPersonas));
        
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        datePicker.init(getYear(fecha), getMonth(fecha) - 1, getDay(fecha), null);
        
        Button buscarButton = (Button) findViewById(R.id.buscarBoton);
        buscarButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					JSONObject parametro = new JSONObject();
					parametro.put("idPosicion", idPosicion);
					parametro.put("fecha", getFechaDatePicker());
					
					buscadorTask = new BuscadorTask();
					buscadorTask.execute(parametro);
				}
				catch (Throwable e) {
					getParent().showDialog(LugaresActivityGroup.CONEXION_ERROR_DIALOG);
				}
			}

			private String getFechaDatePicker() {
				StringBuilder fecha = new StringBuilder();
				
				fecha.append(String.format("%02d", datePicker.getDayOfMonth()));
				fecha.append("/");
				fecha.append(String.format("%02d", datePicker.getMonth() + 1));
				fecha.append("/");
				fecha.append(datePicker.getYear());
				fecha.append(" 00:00:00");
				
				return fecha.toString();
			}
		});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(buscadorTask != null){
			buscadorTask.cancel(true);
		}
	}
	
	/**
	 * Obtiene el texto a mostrar según el número de personas
	 * @param numPersonas
	 * @return
	 */
	private String numPersonasText(int numPersonas) {
		String numPersonasText = "";
		
		if (numPersonas == 1){
			numPersonasText = getString(R.string.unaPersona);
        }
        else{
        	numPersonasText = getString(R.string.nPersonas, numPersonas);
        }
		
		return numPersonasText;
	}
	
	private class BuscadorTask extends AsyncTask<JSONObject, Integer, JSONObject> {
		@Override
    	protected void onPreExecute() {
    		progressDailog.show();
    	}
		
		@Override
		protected JSONObject doInBackground(JSONObject... params) {
			JSONObject resultado = null;
			
			try {
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_CONNECTION);

				HttpClient httpClient = new DefaultHttpClient(httpParams);
				
				HttpGet peticion = new HttpGet(ConstantesURL.BUSCADOR_URL + URLEncoder.encode(params[0].toString()));
				peticion.setHeader("content-type", "application/json");
				
				HttpResponse respuesta = httpClient.execute(peticion);
				String respStr = EntityUtils.toString(respuesta.getEntity());
				
				resultado = new JSONObject(respStr);
			}
			catch(Throwable e) {
				resultado = null;
			}
			
			return resultado;
		}
    	
    	@Override
    	protected void onPostExecute(JSONObject resultado) {
    		progressDailog.dismiss();
    		
    		if(resultado != null){
    			try {
					numPersonas = resultado.getInt("numPersonas");
					numPersonasTextView.setText(numPersonasText(numPersonas));
				} 
    			catch (JSONException e) {
    				getParent().showDialog(LugaresActivityGroup.CONEXION_ERROR_DIALOG);
				}
    		}
    		else{
    			getParent().showDialog(LugaresActivityGroup.CONEXION_ERROR_DIALOG);
    		}
    	}
    }
	
	private int getYear(String fecha){
		return Integer.parseInt(fecha.substring(fecha.lastIndexOf("/") + 1));
	}
	
	private int getMonth(String fecha){
		return Integer.parseInt(fecha.substring(fecha.indexOf("/") + 1, fecha.lastIndexOf("/")));
	}
	
	private int getDay(String fecha){
		return Integer.parseInt(fecha.substring(0, fecha.indexOf("/")));
	}
}
