package it.cammino.risuscito;

import org.holoeverywhere.app.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;

public class AboutActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Utility.updateTheme(AboutActivity.this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		checkScreenAwake();
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
            return true;	
		}
		return false;
	}
	
    @Override
    public void onResume() {
    	super.onResume();
    }
    	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

    //controlla se l'app deve mantenere lo schermo acceso
    private void checkScreenAwake() {
    	
    	SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this);
		boolean screenOn = pref.getBoolean("screenOn", false);
		View about = (View) findViewById(R.id.aboutText);
		if (screenOn)
			about.setKeepScreenOn(true);
		else
			about.setKeepScreenOn(false);
		
    }
	    
}
