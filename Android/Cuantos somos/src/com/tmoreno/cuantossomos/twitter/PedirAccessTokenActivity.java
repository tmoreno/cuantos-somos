package com.tmoreno.cuantossomos.twitter;

import com.tmoreno.cuantossomos.twitter.tasks.OAuthRequestTokenTask;
import com.tmoreno.cuantossomos.twitter.tasks.RecuperarAccessTokenTask;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Actividad que abre el formulario de autenticación de Twitter para dar
 * acceso a la app a la cuenta de Twitter del usuario
 */
public class PedirAccessTokenActivity extends Activity {
	
    private OAuthConsumer consumer; 
    private OAuthProvider provider;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	
		try {
    		consumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
    	    provider = new CommonsHttpOAuthProvider(Constants.REQUEST_URL, Constants.ACCESS_URL, Constants.AUTHORIZE_URL);
    	} 
		catch (Exception e) {
    		Log.e(getClass().getName(), "Error al crear el consumer o el provider", e);
		}

		new OAuthRequestTokenTask(this, consumer, provider).execute();
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		Uri uri = intent.getData();
		
		if (uri != null && uri.getScheme().equals(Constants.OAUTH_CALLBACK_SCHEME)) {
			new RecuperarAccessTokenTask(this, consumer, provider, prefs).execute(uri);
			finish();	
		}
	}
}
