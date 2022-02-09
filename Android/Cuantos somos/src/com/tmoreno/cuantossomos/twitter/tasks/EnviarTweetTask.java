package com.tmoreno.cuantossomos.twitter.tasks;

import oauth.signpost.OAuth;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.tmoreno.cuantossomos.twitter.Constants;
import com.tmoreno.cuantossomos.twitter.exception.CuentaTwitterNoRegistradaException;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Clase que envía el tweet de cuánta gente hay en la posición en la que
 * está el usuario
 */
public class EnviarTweetTask extends AsyncTask<String, Void, Void> {
	
	private SharedPreferences preferencias;
	private boolean tweetDuplicado;
	private boolean cuentaTwitterNoRegistrada;
	
	public EnviarTweetTask(SharedPreferences preferencias){
		this.preferencias = preferencias;
	}
	
	public boolean isTweetDuplicado() {
		return tweetDuplicado;
	}
	
	public boolean isCuentaTwitterNoRegistrada() {
		return cuentaTwitterNoRegistrada;
	}

	@Override
	protected Void doInBackground(String... params) {
		String textoTweet = params[0];
		
		String token = preferencias.getString(OAuth.OAUTH_TOKEN, "");
		String secret = preferencias.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		
		try {
			if(token == null || "".equals(token) || secret == null || "".equals(secret)){
				throw new CuentaTwitterNoRegistradaException();
			}
			
			AccessToken accessToken = new AccessToken(token, secret);
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
			twitter.setOAuthAccessToken(accessToken);
			
			twitter.updateStatus(textoTweet);
		}
		catch(TwitterException e) {
			if("Status is a duplicate.".equals(e.getErrorMessage())) {
				tweetDuplicado = true;
			}
			cancel(true);
		}
		catch(CuentaTwitterNoRegistradaException e) {
			cuentaTwitterNoRegistrada = true;
			cancel(true);
		}
		catch(Throwable e) {
			Log.e(getClass().getName(), "Error enviar el tweet", e);
			cancel(true);
		}
		
		return null;
	}
}
