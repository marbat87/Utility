package it.cammino.risuscito;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class AboutActivity extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getSupportActionBar().setSubtitle(R.string.title_activity_about);
		View rootView = inflater.inflate(R.layout.activity_about, container, false);
//		Utility.updateTheme(AboutActivity.this);
//		super.onCreate(savedInstanceState);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		setContentView(R.layout.activity_about);
//		checkScreenAwake();
		
		return rootView;
	}

//    @Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			finish();
//            return true;	
//		}
//		return false;
//	}
//	
//    @Override
//    public void onResume() {
//    	super.onResume();
//    }
//    	
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//	}

//    //controlla se l'app deve mantenere lo schermo acceso
//    private void checkScreenAwake() {
//    	
//    	SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this);
//		boolean screenOn = pref.getBoolean("screenOn", false);
//		View about = (View) findViewById(R.id.aboutText);
//		if (screenOn)
//			about.setKeepScreenOn(true);
//		else
//			about.setKeepScreenOn(false);
//		
//    }
	    
}
