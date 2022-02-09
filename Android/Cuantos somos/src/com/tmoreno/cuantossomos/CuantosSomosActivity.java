package com.tmoreno.cuantossomos;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import oauth.signpost.OAuth;

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

import com.tmoreno.cuantossomos.modelo.CuantosSomosSQLiteHelper;
import com.tmoreno.cuantossomos.modelo.NumPersonasPosicion;
import com.tmoreno.cuantossomos.modelo.Posicion;
import com.tmoreno.cuantossomos.twitter.PedirAccessTokenActivity;
import com.tmoreno.cuantossomos.twitter.tasks.EnviarTweetTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CuantosSomosActivity extends Activity {
	
	private static final int DESCONECTAR_TWITTER_MENU_OP = 1;
	
	private static final int GPS_DESACTIVADO_DIALOG = 1;
	private static final int OBTENER_POSICION_ERROR_DIALOG = 2;
	private static final int CONEXION_ERROR_DIALOG = 3;
	private static final int CUENTA_TWITTER_NO_CONECTADO_DIALOG = 4;
	private static final int DESCONECTAR_TWITTER_DIALOG = 5;
	private static final int ENVIAR_TWEET_DIALOG = 6;
	private static final int ENVIAR_TWEET_ERROR_DIALOG = 7;
	private static final int TWEET_DUPLICADO_ERROR_DIALOG = 8;
	
	private static final long DISTANCIA_MINIMA_GPS = 10;
	private static final int MAX_TIEMPO_DIF = 120000;
	private static final int MIN_PRECISION = 200;
	private static final int TIMEOUT_CONNECTION = 10000;
	
	private static final long DURACION_MOVIMIENTO_PERSONA = 3000;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	private CuantosSomosSQLiteHelper cuantosSomosDataBaseHelper;

	private int numPersonas;
	private int numPersonasAnterior;
	private int idPosicion;
	private int alturaTabLayout;
	private String deviceId;
	private RelativeLayout fondo;
	private TextView posicionTextView;
	private Button cuantosSomosButton;
	private Button somosButton;
	private ImageButton twitterButton;
	private String textoTweet;
	private ProgressDialog progressDailog;
	
	private RelativeLayout personaLayout;
	private ImageView personaImage;
	private ImageView personaParadaImage;
	private TextView variacionTextView;
	private ImageView indicador;
	
	private JSONObject posicionACompartir;
	private Location ultimaMejorLocation;
	private ReverseGeocoder reverseGeocoder;
	private LocationManager locationManager;
	private LocationListener locListener;
	private InsertarPosicionUsuarioTask insertarPosicionUsuarioTask;
	private GetNumeroPersonasTask getNumeroPersonasTask;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cuantossomos);
		
		deviceId = getDeviceId();
		
		cuantosSomosDataBaseHelper = new CuantosSomosSQLiteHelper(this, null);
		
		// Inicializamos con -1 para saber que todavía no se ha obtenido
		// el alto de la barra de pestañas y así el método get sólo se llame una vez
		alturaTabLayout = -1;
		
		fondo = (RelativeLayout) findViewById(R.id.fondo);
		posicionTextView = (TextView) findViewById(R.id.posicionTextView);
		cuantosSomosButton = (Button) findViewById(R.id.cuantosSomosButton);
		somosButton = (Button) findViewById(R.id.somosButton);
		twitterButton = (ImageButton) findViewById(R.id.twitterButton);
		
		personaLayout = (RelativeLayout) findViewById(R.id.personaLayout);
		personaImage = (ImageView) findViewById(R.id.personaImage);
		personaImage.setBackgroundResource(R.drawable.movimientopersona);
		personaParadaImage = (ImageView) findViewById(R.id.personaParadaImage);
		variacionTextView = (TextView) findViewById(R.id.variacionTextView);
		indicador = (ImageView) findViewById(R.id.indicadorImage);
		
		progressDailog = new ProgressDialog(this);
		progressDailog.setMessage(getString(R.string.espere));
		
		cuantosSomosButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				insertarPosicionUsuarioTask = new InsertarPosicionUsuarioTask();
				insertarPosicionUsuarioTask.execute(posicionACompartir);
			}
		});
		
		somosButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				fondo.removeView(personaLayout);
				
				try {							
					JSONObject parametro = new JSONObject();
					parametro.put("idPosicion", idPosicion);
					parametro.put("fecha", sdf.format(new Date()));
					
					getNumeroPersonasTask = new GetNumeroPersonasTask();
					getNumeroPersonasTask.execute(parametro);
				}
				catch (Throwable e) {
					showDialog(CONEXION_ERROR_DIALOG);
				}
			}
		});
		
		twitterButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(ENVIAR_TWEET_DIALOG);
			}
		});

		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				if(esMejorLocation(location)){

					ultimaMejorLocation = location;

					try {
						reverseGeocoder = new ReverseGeocoder();
						reverseGeocoder.execute(location.getLatitude(), location.getLongitude());
						JSONObject posicionActual = reverseGeocoder.get();
						
						// Las posiciones que no tengan calle, ciudad, estado o país no son válidas
						if (posicionActual.optString("road") == null || "".equals(posicionActual.optString("road")) ||
						    posicionActual.optString("city") == null || "".equals(posicionActual.optString("city")) ||
						    posicionActual.optString("state") == null || "".equals(posicionActual.optString("state")) ||
						    posicionActual.optString("country") == null || "".equals(posicionActual.optString("country"))) {
							
							posicionTextView.setText(getString(R.string.posicionDesconocida));
							somosButton.setVisibility(View.GONE);
							cuantosSomosButton.setVisibility(View.VISIBLE);
							cuantosSomosButton.setEnabled(false);
							
							reiniciarAVistaVacia();
						}
						else if (posicionACompartir == null || 
								!posicionACompartir.optString("road").equals(posicionActual.optString("road"))) {
							
							posicionACompartir = posicionActual;
							posicionTextView.setText(posicionActual.optString("road").toUpperCase());
							somosButton.setVisibility(View.GONE);
							cuantosSomosButton.setVisibility(View.VISIBLE);
							cuantosSomosButton.setEnabled(true);
							
							reiniciarAVistaVacia();
						}
					} 
					catch (Throwable e) {
						showDialog(OBTENER_POSICION_ERROR_DIALOG);
					}
				}
			}

			public void onProviderDisabled(String provider){
				showDialog(GPS_DESACTIVADO_DIALOG);
			}

			public void onProviderEnabled(String provider){

			}

			public void onStatusChanged(String provider, int status, Bundle extras){

			}
		};
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		// En caso de que el GPS no esté activo se le envía a la pantalla de
		// configuración, pero si no lo activa ahí y vuelve a la aplicación,
		// tenemos que volver a mostrar el mensaje para que lo active
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			showDialog(GPS_DESACTIVADO_DIALOG);
		}
		else {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, DISTANCIA_MINIMA_GPS, locListener);
		}
	}

	@Override
	public Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		switch (id) {
			case GPS_DESACTIVADO_DIALOG:
				builder.setMessage(getString(R.string.gpsDesactivado));
				builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				});
				break;
				
			case OBTENER_POSICION_ERROR_DIALOG:
				builder.setMessage(getString(R.string.obtenerPosicionError));
				builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				break;
				
			case CONEXION_ERROR_DIALOG:
				builder.setMessage(getString(R.string.conexionError));
				builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				break;
				
			case CUENTA_TWITTER_NO_CONECTADO_DIALOG:
				builder.setMessage(getString(R.string.noCuentasTwitterError));
				builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(getApplicationContext(), PedirAccessTokenActivity.class);
						startActivity(i);
					}
				});
				builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				break;
				
			case DESCONECTAR_TWITTER_DIALOG:
				builder.setMessage(getString(R.string.desconectarPregunta));
				builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					    
						Editor edit = prefs.edit();
						edit.remove(OAuth.OAUTH_TOKEN);
						edit.remove(OAuth.OAUTH_TOKEN_SECRET);
						edit.commit();
					}
				});
				builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				break;
				
			case ENVIAR_TWEET_DIALOG:
				textoTweet = getString(R.string.enviarTweet, numPersonas, 
									   posicionACompartir.optString("road").replaceAll(" ", ""));
				
				builder.setTitle(getString(R.string.twitear));
				builder.setMessage(textoTweet);
				builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						EnviarTweetTask enviarTweet = null;
						
						try {
							enviarTweet = new EnviarTweetTask(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
							enviarTweet.execute(textoTweet);
							enviarTweet.get();
							Toast.makeText(getApplicationContext(), getString(R.string.tweetEnviado), Toast.LENGTH_SHORT).show();
						} 
						catch (Throwable e) {
							if(enviarTweet.isTweetDuplicado()){
								showDialog(TWEET_DUPLICADO_ERROR_DIALOG);
							}
							else if(enviarTweet.isCuentaTwitterNoRegistrada()){
								showDialog(CUENTA_TWITTER_NO_CONECTADO_DIALOG);
							}
							else{
								showDialog(ENVIAR_TWEET_ERROR_DIALOG);
							}
						}
					}
				});
				builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				
				break;
				
			case ENVIAR_TWEET_ERROR_DIALOG:
				builder.setMessage(getString(R.string.enviarTweetError));
				builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				break;
				
			case TWEET_DUPLICADO_ERROR_DIALOG:
				builder.setMessage(getString(R.string.tweetDuplicadoError));
				builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				break;
				
			default:
				break;
		}
		
		return builder.create();
	}
	
	@Override
	public void onPrepareDialog(int id, Dialog dialog) {
	    super.onPrepareDialog(id, dialog);

	    switch (id) {
	        case ENVIAR_TWEET_DIALOG:
				textoTweet = getString(R.string.enviarTweet, numPersonas, 
						               posicionACompartir.optString("road").replaceAll(" ", ""));
				((AlertDialog) dialog).setMessage(textoTweet);
	            break;
	            
	        default:
				break;
	    }
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		
		if(token != null && !"".equals(token) && secret != null && !"".equals(secret)){
			if(menu.findItem(DESCONECTAR_TWITTER_MENU_OP) == null){
				menu.addSubMenu(Menu.NONE, 
								DESCONECTAR_TWITTER_MENU_OP, 
								Menu.NONE, 
								getString(R.string.desconectar)).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
			}
		}
		else{
			menu.removeItem(DESCONECTAR_TWITTER_MENU_OP);
		}
		        
        return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		showDialog(DESCONECTAR_TWITTER_DIALOG);
		
	    return true;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(locationManager != null && locListener != null){
			locationManager.removeUpdates(locListener);
			locationManager = null;
			locListener = null;
		}
		
		if(insertarPosicionUsuarioTask != null){
			insertarPosicionUsuarioTask.cancel(true);
		}
		
		if(getNumeroPersonasTask != null){
			getNumeroPersonasTask.cancel(true);
		}
	}
	
	/**
	 * Método que obtiene el identificador único de dispositivo, necesario para
	 * enviar la posición al servidor
	 * @return
	 */
	private String getDeviceId() {
		StringBuilder deviceId = new StringBuilder();
		
	    try {
	    	String androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID); 
	        
	        MessageDigest digest = MessageDigest.getInstance("MD5");
	        digest.reset();
	        digest.update(androidId.getBytes());
	        
	        byte messageDigest[] = digest.digest();
	        for (int i = 0; i < messageDigest.length; i++) {
	        	deviceId.append(Integer.toString((messageDigest[i] & 0xff) + 0x100, 16).substring(1));
            }
	    } 
	    catch (NoSuchAlgorithmException e) {
	        Log.e(getClass().getName(), "Error al obtener el id de dispositivo", e);
	    }
	    
	    return deviceId.toString();
	}
	
	/**
	 * Método que mueve a la persona según la variación en el número de personas
	 * en el grupo
	 * @param variacion
	 */
	private void mover(int variacion) {
		TranslateAnimation personaAnimacion = null;
        
        int xPersonaOrigen = fondo.getWidth()/2 - personaImage.getWidth();
        int xPersonaDestino = xPersonaOrigen;
        int yPersonaOrigen = fondo.getHeight();
        int yPersonaDestino = (fondo.getHeight()/2) + getAlturaTabLayout();
        
        if(Math.abs(variacion) <= 100){
        	variacionTextView.setText(Math.abs(variacion)+"");
        }
        else{
        	variacionTextView.setText("+100");
        }
        
        if(variacion >= 0){
        	personaAnimacion = new TranslateAnimation(xPersonaOrigen, xPersonaDestino, yPersonaOrigen, yPersonaDestino);
        	if(variacion == 0){
        		indicador.setBackgroundResource(R.drawable.igual);
        	}
        	else{
        		indicador.setBackgroundResource(R.drawable.sube);
        	}
        }
        else{
        	personaAnimacion = new TranslateAnimation(xPersonaOrigen, xPersonaDestino, yPersonaDestino, fondo.getHeight() - (personaImage.getHeight()/2));
        	indicador.setBackgroundResource(R.drawable.baja);
        }
        
        personaAnimacion.setDuration(DURACION_MOVIMIENTO_PERSONA);
        personaAnimacion.setFillAfter(true);
        personaAnimacion.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
				personaImage.setVisibility(View.VISIBLE);
				personaParadaImage.setVisibility(View.INVISIBLE);
			}
			
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			public void onAnimationEnd(Animation animation) {
				personaImage.setVisibility(View.INVISIBLE);
				personaParadaImage.setVisibility(View.VISIBLE);
			}
		});
        
        ((AnimationDrawable) personaImage.getBackground()).start();
        personaLayout.startAnimation(personaAnimacion);
	}
	
	/**
	 * Método que obtiene la altura de la barra de pestañas desde el fichero de
	 * preferencias
	 * @return
	 */
	private int getAlturaTabLayout() {
		if(alturaTabLayout == -1){
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			alturaTabLayout = prefs.getInt(MainActivity.ALTURA_TAB_LAYOUT, 0);
		}
		
		return alturaTabLayout;
	}

	/**
	 * Método que reinicia la pantalla para permitir compartir la posición del usuario
	 */
	private void reiniciarAVistaVacia(){
		numPersonas = 0;
		numPersonasAnterior = 0;
		
		cambiarFondo(0);
		
		twitterButton.setVisibility(View.GONE);
		fondo.removeView(personaLayout);
	}
	
	/**
	 * Método que cambia el fondo según el número de personas
	 * @param numPersonas
	 */
	private void cambiarFondo(int numPersonas) {		
		if(numPersonas >= 1 && numPersonas <= 100){
	        fondo.setBackgroundResource(R.drawable.fondo_1_100);
	    }
	    else if(numPersonas >= 101 && numPersonas <= 1000){
	    	fondo.setBackgroundResource(R.drawable.fondo_101_1000);
	    }
	    else if(numPersonas >= 1001 && numPersonas <= 10000){
	    	fondo.setBackgroundResource(R.drawable.fondo_1001_10000);
	    }
	    else if(numPersonas >= 10001 && numPersonas <= 100000){
	    	fondo.setBackgroundResource(R.drawable.fondo_10001_100000);
	    }
	    else if(numPersonas > 100000){
	    	fondo.setBackgroundResource(R.drawable.fondo_100000_mas);
	    }
	    else {
	    	fondo.setBackgroundResource(R.drawable.fondo_vacio);
	    }
	}
	
	/**
	 * Método que guarda la posición que hemos compartido en la base de datos local
	 * @param fecha
	 * @throws ParseException
	 */
	private void guardarPosicion(String fecha) throws ParseException{
		SQLiteDatabase db = cuantosSomosDataBaseHelper.getWritableDatabase();
		
		NumPersonasPosicion numPersonasPosicion = NumPersonasPosicion.getNumPersonasPosicion(db, idPosicion, fecha);
		
		if(numPersonasPosicion == null){
			Posicion posicion = Posicion.getPosicion(db, idPosicion);
			
			if(posicion == null){
				Posicion.insert(db, 
								idPosicion, 
								posicionACompartir.optString("road"),
								posicionACompartir.optString("city") + ", " + posicionACompartir.optString("state"));
			}
			
			NumPersonasPosicion.insert(db, idPosicion, numPersonas, fecha);
		}
		else{
			NumPersonasPosicion.update(db, numPersonasPosicion.getId(), numPersonas, fecha);
		}
		
		db.close();
	}

	/**
	 * Método que comprueba si la Location leida es mejor que la última mejor leida
	 * @param location
	 * @param currentBestLocation
	 * @return
	 */
	private boolean esMejorLocation(Location location) {
		// Si no hay última mejor Location entonces la que hemos leido es mejor
		if (ultimaMejorLocation == null) {
			return true;
		}

		if(location.getLatitude() == ultimaMejorLocation.getLatitude() &&
				location.getLongitude() == ultimaMejorLocation.getLongitude()){
			return false;
		}

		// Comprobamos si la Location leida es vieja o nueva
		long tiempoDif = location.getTime() - ultimaMejorLocation.getTime();
		boolean esMuyNueva = tiempoDif > MAX_TIEMPO_DIF;
		boolean esMuyVieja = tiempoDif < -MAX_TIEMPO_DIF;
		boolean esNueva = tiempoDif > 0;

		if (esMuyNueva) {
			return true;
		} 
		else if (esMuyVieja) {
			return false;
		}

		// Comprobamos la precision de la Location leida
		int precisionDif = (int) (location.getAccuracy() - ultimaMejorLocation.getAccuracy());
		boolean esMenosPrecisa = precisionDif > 0;
		boolean esMasPrecisa = precisionDif < 0;
		boolean esMuyPocoPrecisa = precisionDif > MIN_PRECISION;
		// Comprobamos si el proveedor del Location leido es el mismo que el último mejor Location
		boolean esMismoProveedor = isSameProvider(location.getProvider(), ultimaMejorLocation.getProvider());

		if (esMasPrecisa) {
			return true;
		} 
		else if (esNueva && !esMenosPrecisa) {
			return true;
		} 
		else if (esNueva && !esMuyPocoPrecisa && esMismoProveedor) {
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @param provider1
	 * @param provider2
	 * @return
	 */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}

		return provider1.equals(provider2);
	}
	
	/**
	 * Clase que modela el envío de la posición del usuario al servidor
	 */
	private class InsertarPosicionUsuarioTask extends AsyncTask<JSONObject, Integer, JSONObject> {
		
		private static final String ANDROID = "ANDROID";
		private String fecha;
		
		@Override
    	protected void onPreExecute() {
			progressDailog.show();
    	}
		
		@Override
		protected JSONObject doInBackground(JSONObject... params) {
			JSONObject resultado = null;
			
			try {
				fecha = sdf.format(new Date());
				params[0].put("idUsuario", deviceId);
				params[0].put("fecha", fecha);
				params[0].put("plataforma", ANDROID);
				
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_CONNECTION);

				HttpClient httpClient = new DefaultHttpClient(httpParams);
				
				HttpGet peticion = new HttpGet(ConstantesURL.INSERTAR_POSICION_USUARIO_URL + URLEncoder.encode(params[0].toString()));
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
					idPosicion = resultado.getInt("idPosicion");
				} 
    			catch (JSONException e) {
    				showDialog(CONEXION_ERROR_DIALOG);
				}
    			
    			try {
					guardarPosicion(fecha);
				} 
    			catch (ParseException e) {
					e.printStackTrace();
				}
    	        
    	        cambiarFondo(numPersonas);
    	        
    	        somosButton.setText(getString(R.string.numeroPersonas, NumberFormat.getNumberInstance().format(numPersonas)));
    	        somosButton.setVisibility(View.VISIBLE);
    	        twitterButton.setVisibility(View.VISIBLE);
    	        cuantosSomosButton.setVisibility(View.GONE);
    	        
    	        fondo.addView(personaLayout);
    	        
    	        mover(numPersonas - numPersonasAnterior);
    	        numPersonasAnterior = numPersonas;
    		}
    		else{
    			showDialog(CONEXION_ERROR_DIALOG);
    		}
    	}
    }
	
	/**
	 * Clase que modela el envío de la petición para saber cuánta gente hay
	 */
	private class GetNumeroPersonasTask extends AsyncTask<JSONObject, Integer, JSONObject> {
		private String fecha;
		
		@Override
    	protected void onPreExecute() {
    		progressDailog.show();
    	}
		
		@Override
		protected JSONObject doInBackground(JSONObject... params) {
			JSONObject resultado = null;
			
			try {
				fecha = params[0].getString("fecha");
				
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_CONNECTION);

				HttpClient httpClient = new DefaultHttpClient(httpParams);
				
				HttpGet peticion = new HttpGet(ConstantesURL.GET_NUMERO_PERSONAS_URL + URLEncoder.encode(params[0].toString()));
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
				} 
    			catch (JSONException e) {
    				showDialog(CONEXION_ERROR_DIALOG);
				}
    			
    			try {
					guardarPosicion(fecha);
				} 
    			catch (ParseException e) {
					e.printStackTrace();
				}
    			
    			cambiarFondo(numPersonas);
		        
		        somosButton.setText(getString(R.string.numeroPersonas, NumberFormat.getNumberInstance().format(numPersonas)));
		        
		        fondo.addView(personaLayout);
		        
		        mover(numPersonas - numPersonasAnterior);
		        numPersonasAnterior = numPersonas;
    		}
    		else{
    			showDialog(CONEXION_ERROR_DIALOG);
    		}
    	}
    }
}
