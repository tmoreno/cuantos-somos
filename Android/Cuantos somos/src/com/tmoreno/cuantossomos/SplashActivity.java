package com.tmoreno.cuantossomos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

	private static final long SPLASH_DISPLAY_LENGTH = 1000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		Handler handler = new Handler();
		handler.postDelayed(getRunnableStartApp(), SPLASH_DISPLAY_LENGTH);
	}

	private Runnable getRunnableStartApp() {
		Runnable runnable = new Runnable(){
			public void run(){
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				finish();
			}
		};
		
		return runnable;
	}
}
