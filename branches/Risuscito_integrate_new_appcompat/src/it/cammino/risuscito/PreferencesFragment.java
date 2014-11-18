package it.cammino.risuscito;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

public class PreferencesFragment extends Fragment {
	
	private int prevOrientation;
	private SwitchCompat screenSwitch;
	private SwitchCompat secondaSwitch;
	private int saveEntries;
	
	private static final String SCREEN_ON = "screenOn";
	private static final String SHOW_SECONDA = "showSecondaEucarestia";
	private static final String DEFAULT_INDEX = "defaultIndex";
	private static final String SAVE_LOCATION = "saveLocation";
	
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
	            
	            ((MainActivity) getActivity()).checkScreenAwake();
			}
		});
		
		// controllo l'attuale impostazione di always on
		if (PreferenceManager
				.getDefaultSharedPreferences(getActivity())
				.getBoolean(SCREEN_ON, false))
			screenSwitch.setChecked(true);
		else
			screenSwitch.setChecked(false);
		
		secondaSwitch = (SwitchCompat) rootView.findViewById(R.id.show_seconda_eucarestia);
		
		View secondaSwitchView = rootView.findViewById(R.id.show_seconda_eucarestia_layout);
		secondaSwitchView.setOnClickListener(new OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				
				SharedPreferences.Editor editor = PreferenceManager
	                    .getDefaultSharedPreferences(getActivity())
	                    .edit();
				
				if (secondaSwitch.isChecked()) {
					secondaSwitch.setChecked(false);
		            editor.putBoolean(SHOW_SECONDA, false);

				}
				else {
					secondaSwitch.setChecked(true);
					editor.putBoolean(SHOW_SECONDA, true);
				}
				
	            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
	            	editor.commit();
	            } else {
	            	editor.apply();
	            }
			}
		});
		
		// controllo l'attuale impostazione della visualizzazione seconda lettura
		if (PreferenceManager
				.getDefaultSharedPreferences(getActivity())
				.getBoolean(SHOW_SECONDA, false))
			secondaSwitch.setChecked(true);
		else
			secondaSwitch.setChecked(false);
		
		View defaultIndexView = rootView.findViewById(R.id.default_index_layout);
		defaultIndexView.setOnClickListener(new OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				
				blockOrientation();
				MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.default_index_title)
                .items(R.array.pref_default_index_entries)
                .itemsCallbackSingleChoice(PreferenceManager
        				.getDefaultSharedPreferences(getActivity())
        				.getInt(DEFAULT_INDEX, 0), new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, String text) {
                    	SharedPreferences.Editor editor = PreferenceManager
        	                    .getDefaultSharedPreferences(getActivity())
        	                    .edit();
        				
                    	editor.putInt(DEFAULT_INDEX, which);
        				
        	            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
        	            	editor.commit();
        	            } else {
        	            	editor.apply();
        	            }
        	            
        	            getActivity().setRequestedOrientation(prevOrientation);
                    }
                })
                .positiveText(R.string.single_choice_ok)
                .positiveColorRes(R.color.theme_accent)
                .build();
				dialog.setOnKeyListener(new Dialog.OnKeyListener() {
			        @Override
			        public boolean onKey(DialogInterface arg0, int keyCode,
			        		KeyEvent event) {
			        	if (keyCode == KeyEvent.KEYCODE_BACK
			        			&& event.getAction() == KeyEvent.ACTION_UP) {
			        		arg0.dismiss();
			        		getActivity().setRequestedOrientation(prevOrientation);
			        		return true;
			            }
			            return false;
			        }
		        });
                dialog.show();
                dialog.setCancelable(false);
			}
		});
		
		View saveLocationView = rootView.findViewById(R.id.save_location_layout);
		
		if (Utility.isExternalStorageReadable()) {
			saveEntries = R.array.save_location_sd_entries;
        }
        else {
        	saveEntries = R.array.save_location_nosd_entries;
        }
		
		saveLocationView.setOnClickListener(new OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				
				blockOrientation();
				MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.save_location_title)
                .items(saveEntries)
                .itemsCallbackSingleChoice(PreferenceManager
        				.getDefaultSharedPreferences(getActivity())
        				.getInt(SAVE_LOCATION, 0), new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, String text) {
                    	SharedPreferences.Editor editor = PreferenceManager
        	                    .getDefaultSharedPreferences(getActivity())
        	                    .edit();
        				
                    	editor.putInt(SAVE_LOCATION, which);
        				
        	            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
        	            	editor.commit();
        	            } else {
        	            	editor.apply();
        	            }
        	            
        	            getActivity().setRequestedOrientation(prevOrientation);
                    }
                })
                .positiveText(R.string.single_choice_ok)
                .positiveColorRes(R.color.theme_accent)
                .build();
				dialog.setOnKeyListener(new Dialog.OnKeyListener() {
			        @Override
			        public boolean onKey(DialogInterface arg0, int keyCode,
			        		KeyEvent event) {
			        	if (keyCode == KeyEvent.KEYCODE_BACK
			        			&& event.getAction() == KeyEvent.ACTION_UP) {
			        		arg0.dismiss();
			        		getActivity().setRequestedOrientation(prevOrientation);
			        		return true;
			            }
			            return false;
			        }
		        });
                dialog.show();
                dialog.setCancelable(false);
			}
		});
		
		return rootView;
	}

    public void blockOrientation() {
        prevOrientation = getActivity().getRequestedOrientation();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        	getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
        	getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }
    }
	
}
