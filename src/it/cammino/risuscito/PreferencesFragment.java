package it.cammino.risuscito;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class PreferencesFragment extends Fragment {
	
	private SwitchCompat screenSwitch;
	
	private static final String SCREEN_ON = "screenOn";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.preference_screen, container, false);
		((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_activity_settings);
		
		screenSwitch = (SwitchCompat) rootView.findViewById(R.id.screen_on);
		
		View screenSwitchView = rootView.findViewById(R.id.screen_on_layout);
		screenSwitchView.setOnClickListener(new OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				
				SharedPreferences.Editor editor = PreferenceManager
	                    .getDefaultSharedPreferences(getActivity())
	                    .edit();
				
				if (screenSwitch.isChecked()) {
					screenSwitch.setChecked(false);
		            editor.putBoolean(SCREEN_ON, false);

				}
				else {
					screenSwitch.setChecked(true);
					editor.putBoolean(SCREEN_ON, true);
				}
				
	            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
	            	editor.commit();
	            } else {
	            	editor.apply();
	            }
			}
		});
		
		return rootView;
	}
	    
}
