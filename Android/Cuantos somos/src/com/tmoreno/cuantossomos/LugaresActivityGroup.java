package com.tmoreno.cuantossomos;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

public class LugaresActivityGroup extends ActivityGroup {
	
	public static final int CONEXION_ERROR_DIALOG = 0;
	
	private List<String> activities;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (activities == null){
        	activities = new ArrayList<String>();
        }
        
        Intent intent = new Intent(this, LugaresActivity.class);
        startChildActivity(LugaresActivity.class.getSimpleName(), intent);
    }
	
	public void startChildActivity(String nombreActivity, Intent intent) {
		Window window = getLocalActivityManager().startActivity(nombreActivity, intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		if (window != null) {
			activities.add(nombreActivity);
			setContentView(window.getDecorView());
		}
	}
	
	@Override
	public Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		switch (id) {
			case CONEXION_ERROR_DIALOG:
				builder.setMessage(getString(R.string.buscarError));
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
	
	/**
	 * This is called when a child activity of this one calls its finish method.
	 * This implementation calls {@link LocalActivityManager#destroyActivity} on
	 * the child activity and starts the previous activity. If the last child
	 * activity just called finish(),this activity (the parent), calls finish to
	 * finish the entire group.
	 */
	@Override
	public void finishFromChild(Activity child) {
		LocalActivityManager manager = getLocalActivityManager();
		int index = activities.size() - 1;

		if (index < 1) {
			finish();
		}
		else{
			manager.destroyActivity(activities.get(index), true);
			activities.remove(index);
			
			String activityAnterior = activities.get(index - 1);
			Intent intent = manager.getActivity(activityAnterior).getIntent();
			Window window = manager.startActivity(activityAnterior, intent);
			setContentView(window.getDecorView());
		}
	}
	
	/**
	 * The primary purpose is to prevent systems before
	 * android.os.Build.VERSION_CODES.ECLAIR from calling their default
	 * KeyEvent.KEYCODE_BACK during onKeyDown.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// preventing default implementation previous to
			// android.os.Build.VERSION_CODES.ECLAIR
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Overrides the default implementation for KeyEvent.KEYCODE_BACK so that
	 * all systems call onBackPressed().
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackPressed();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	/**
	 * If a Child Activity handles KeyEvent.KEYCODE_BACK. Simply override and
	 * add this method.
	 */
	@Override
	public void onBackPressed() {
		if (activities.size() > 1) {
			String nombreActivity = activities.get(activities.size() - 1);
			Activity current = getLocalActivityManager().getActivity(nombreActivity);
			current.finish();
		}
	}
}
