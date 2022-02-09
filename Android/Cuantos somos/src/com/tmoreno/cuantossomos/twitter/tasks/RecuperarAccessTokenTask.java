package com.tmoreno.cuantossomos.twitter.tasks;

import com.tmoreno.cuantossomos.CuantosSomosActivity;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Clase que guarda los tokens de autenticación
 */
public class RecuperarAccessTokenTask extends AsyncTask<Uri, Void, Void> {

	private Context context;
	private OAuthProvider provider;
	private OAuthConsumer consumer;
	private SharedPreferences prefs;
	
	public RecuperarAccessTokenTask(Context context, OAuthConsumer consumer,OAuthProvider provider, SharedPreferences prefs) {
		this.context = context;
		this.consumer = consumer;
		this.provider = provider;
		this.prefs = prefs;
	}

	@Override
	protected Void doInBackground(Uri...params) {
		try {
			String oauth_verifier = params[0].getQueryParameter(OAuth.OAUTH_VERIFIER);
			provider.retrieveAccessToken(consumer, oauth_verifier);

			Editor edit = prefs.edit();
			edit.putString(OAuth.OAUTH_TOKEN, consumer.getToken());
			edit.putString(OAuth.OAUTH_TOKEN_SECRET, consumer.getTokenSecret());
			edit.commit();
			
			context.startActivity(new Intent(context, CuantosSomosActivity.class));
		} 
		catch (Exception e) {
			Log.e(getClass().getName(), "Error al recuperar el Access Token ", e);
		}

		return null;
	}
}