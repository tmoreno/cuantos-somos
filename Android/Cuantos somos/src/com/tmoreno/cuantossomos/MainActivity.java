package com.tmoreno.cuantossomos;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainActivity extends TabActivity{
	
	public static final String ALTURA_TAB_LAYOUT = "ALTURA_TAB_LAYOUT";
	private static final String CUANTOS_SOMOS_TAB = "CuantosSomosTab";
	private static final String LUGARES_TAB = "LugaresTab";
	private static final String ACERCA_DE_TAB = "AcercaDeTab";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        TabHost tabHost = getTabHost();
        
        TabSpec cuantosSomosTab = createTabSpec(tabHost, 
        										CUANTOS_SOMOS_TAB, 
        										getString(R.string.cuantossomos), 
        										R.drawable.cuantossomos,
        										CuantosSomosActivity.class);
        
        TabSpec lugaresTab = createTabSpec(tabHost, 
        								   LUGARES_TAB, 
        								   getString(R.string.lugares),
        								   R.drawable.lugares,
        								   LugaresActivityGroup.class);
        
        TabSpec acercaDeTab = createTabSpec(tabHost, 
											ACERCA_DE_TAB, 
											getString(R.string.acercade),
											R.drawable.info,
											AcercaDeActivity.class);
 
        tabHost.addTab(cuantosSomosTab);
        tabHost.addTab(lugaresTab);
        tabHost.addTab(acercaDeTab);
        
        // Necesario para saber la altura de la barra de pestañas y así poder
        // compartir esa información con el resto de actividades
        ViewTreeObserver vto = tabHost.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
            	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            	if(!prefs.contains(ALTURA_TAB_LAYOUT)){
            		Editor edit = prefs.edit();
					edit.putInt(ALTURA_TAB_LAYOUT, findViewById(R.id.tabsLayout).getHeight()/2);
					edit.commit();
            	}
            }
        });
    }
	
	private TabSpec createTabSpec(TabHost tabHost, String tag, String text, int idImage, Class<?> clazz){
		View tabView = createTabView(this, text, idImage);
		Intent intent = new Intent(this, clazz);
		
		TabSpec tabSpec = tabHost.newTabSpec(tag);
        tabSpec.setIndicator(tabView);
        tabSpec.setContent(intent);
        
        return tabSpec;
	}
	
	private View createTabView(Context context, String text, int idImage) {
		View view = LayoutInflater.from(context).inflate(R.layout.tab, null);
		
		ImageView iv = (ImageView) view.findViewById(R.id.tabsImage);
		iv.setImageDrawable(getResources().getDrawable(idImage));
		
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		
		return view;
	}
}