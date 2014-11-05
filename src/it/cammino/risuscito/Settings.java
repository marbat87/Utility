package it.cammino.risuscito;


import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint("NewApi")
public class Settings extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.xml_settings);
//		getSupportActionBar().setTitle(R.string.title_activity_settings);
		
//		final ActionBar actionBar = getSupportActionBar();
//		actionBar.setDisplayHomeAsUpEnabled(true);
		
		ListPreference saveLocation = (ListPreference) findPreference("saveLocation");
        if (Utility.isExternalStorageReadable()) {
        	saveLocation.setEntries(
        			getResources().getStringArray(R.array.save_location_sd_entries));
        	saveLocation.setEntryValues(
        			getResources().getStringArray(R.array.save_location_sd_values));
        }
        else {
        	saveLocation.setEntries(
        			getResources().getStringArray(R.array.save_location_nosd_entries));
        	saveLocation.setEntryValues(
        			getResources().getStringArray(R.array.save_location_nosd_values));
        }
        
        ListPreference applicationTheme = (ListPreference) findPreference("applicationThemeNew");
        applicationTheme.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
		        Intent i = getActivity().getPackageManager()
		                .getLaunchIntentForPackage(getActivity().getPackageName() );
		        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(i);
				return true;
			}
		});
                
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//		getSupportActionBar().setTitle(R.string.title_activity_settings);
		
		Toolbar toolbar = ((Toolbar) getActivity().findViewById(R.id.risuscito_toolbar));
		toolbar.setTitle(R.string.title_activity_settings);
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}
