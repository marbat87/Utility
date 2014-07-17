package it.cammino.risuscito;

import org.holoeverywhere.preference.ListPreference;
import org.holoeverywhere.preference.Preference;
import org.holoeverywhere.preference.Preference.OnPreferenceChangeListener;
import org.holoeverywhere.preference.PreferenceFragment;

import android.content.Intent;
import android.os.Bundle;

public class Settings extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setTitle(R.string.title_activity_settings);
		addPreferencesFromResource(R.xml.xml_settings);
		
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
        
        ListPreference applicationTheme = (ListPreference) findPreference("applicationTheme");
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
	
//    @Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			finish();
//            return true;	
//		}
//		return false;
//	}
		
}
