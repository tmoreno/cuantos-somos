package com.tmoreno.cuantossomos;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AcercaDeActivity extends Activity {
	
	private static final String FACEBOOK_URL = "http://www.facebook.com/pages/Cu%C3%A1ntos-somos/270303983084604";
	private static final String TWITTER_URL = "https://twitter.com/cuantos_somos";
	private static final String WEB_URL = "http://www.cuantos-somos.com/";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acercade);
        
        String [] modoContacto = {getString(R.string.facebook), 
        						  getString(R.string.twitter),
        						  getString(R.string.web)};
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, modoContacto); 

        ListView list = (ListView) findViewById(R.id.siguenosListView);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String url = "";
				
				switch (position) {
				case 0:
					url = FACEBOOK_URL;
					break;
					
				case 1:
					url = TWITTER_URL;
					break;
					
				default:
					url = WEB_URL;
					break;
				}
				
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(intent);
			}
		});
    }
}
