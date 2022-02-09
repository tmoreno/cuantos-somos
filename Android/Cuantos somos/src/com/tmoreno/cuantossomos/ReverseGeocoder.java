package com.tmoreno.cuantossomos;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;

/**
 * Clase que hace una petición al servidor de reverse geocoding
 */
public class ReverseGeocoder extends AsyncTask<Double, Void, JSONObject> {

	private static final int TIMEOUT_CONNECTION = 5000;
	private static final String url = "http://nominatim.openstreetmap.org/reverse?format=json&accept-language=en&zoom=18&addressdetails=1";
	
	@Override
	protected JSONObject doInBackground(Double... params) {
		JSONObject resultado = null;
		
		try {
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_CONNECTION);

			HttpClient httpClient = new DefaultHttpClient(httpParams);
			
			HttpGet peticion = new HttpGet(url + "&lat=" + params[0] + "&lon=" + params[1]);
			
			HttpResponse respuesta = httpClient.execute(peticion);
			
			JSONObject respJson = new JSONObject(EntityUtils.toString(respuesta.getEntity()));
			
			// Formamos el JSON según la API de nuestro servidor
			resultado = new JSONObject(respJson.getJSONObject("address").toString());
			resultado.put("latitud", respJson.opt("lat"));
			resultado.put("longitud", respJson.opt("lon"));
			resultado.put("osm_id", respJson.opt("osm_id"));
			resultado.put("place_id", respJson.opt("place_id"));
			
			// Si la calle es peatonal la clave se llama 'pedestrian' en vez de 'road'
			if(resultado.opt("road") == null && resultado.opt("pedestrian") != null){
				resultado.put("road", resultado.opt("pedestrian"));
	        }
		}
		catch(Throwable e) {
			cancel(true);
		}
		
		return resultado;
	}
}
