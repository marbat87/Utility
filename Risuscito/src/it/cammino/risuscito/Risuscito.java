package it.cammino.risuscito;

import it.cammino.risuscito.ChangelogDialogFragment.ChangelogDialogListener;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

public class Risuscito extends Fragment implements ChangelogDialogListener {

	private static final String VERSION_KEY = "PREFS_VERSION_KEY";
	private static final String NO_VERSION = "";
	private int prevOrientation;
	private View rootView;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getSupportActionBar().setSubtitle(R.string.activity_homepage);
		rootView = inflater.inflate(R.layout.activity_risuscito, container, false);
		
        // recupera il pulsante che lancia l'elenco dei canti in ordine alfabetico
//        Button buttonAlpha = (Button) rootView.findViewById(R.id.generalIndex);       
//        buttonAlpha.setOnClickListener(new View.OnClickListener() {
//        	@Override
//        	public void onClick(View v) {
//        		startSubActivityPage();
//        	}
//        });
        
        // recupera il pulsante che lancia l'elenco dei canti per pagina
//        Button buttonNum = (Button) rootView.findViewById(R.id.predefinedLists);       
//        buttonNum.setOnClickListener(new View.OnClickListener() {
//        	@Override
//        	public void onClick(View v) {
//        		startSubActivityLists();
//        	}
//        }); 
        
        // recupera il pulsante che lancia la ricerca per titolo
//        Button buttonSearchText = (Button) rootView.findViewById(R.id.titleSearch);       
//        buttonSearchText.setOnClickListener(new View.OnClickListener() {
//        	@Override
//        	public void onClick(View v) {
//        		startSubActivitySearch();
//        	}
//        });
        
//        checkScreenAwake();
        
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        
        // get version numbers
        String lastVersion = sp.getString(VERSION_KEY, NO_VERSION);
        String thisVersion = "";
//        Log.i("Changelog", "lastVersion: " + lastVersion);
        try {
            thisVersion = getActivity().getPackageManager().getPackageInfo(
                    getActivity().getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            thisVersion = NO_VERSION;
//            Log.i("Changelog", "could not get version name from manifest!");
            e.printStackTrace();
        }
//        Log.i("Changelog", "appVersion: " + thisVersion);
        
        if (!thisVersion.equals(lastVersion)) {
        	blockOrientation();
	    	ChangelogDialogFragment dialog = new ChangelogDialogFragment();
	    	dialog.setListener(this);
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
	    	dialog.show(getFragmentManager());
	    	dialog.setCancelable(false);
	        SharedPreferences.Editor editor = sp.edit();
	        editor.putString(VERSION_KEY, thisVersion);
	        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
	        	editor.commit();
	        } else {
	        	editor.apply();
	        }
        }
        
        PaginaRenderActivity.notaCambio = null;
        PaginaRenderActivity.speedValue = null;
        PaginaRenderActivity.scrollPlaying = false;
        
//        setHasOptionsMenu(true);
        
        return rootView;
	}

//    @Override
//    public void onResume() {
//    	super.onResume();
//    	checkScreenAwake();
//    }
    
    //controlla se l'app deve mantenere lo schermo acceso
//    public void checkScreenAwake() {
//    	SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(getActivity());
//		boolean screenOn = pref.getBoolean("screenOn", false);
//		ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView1);
//		if (screenOn)
//			imageView.setKeepScreenOn(true);
//		else
//			imageView.setKeepScreenOn(false);
//    }
    
//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		// Inflate the menu; this adds items to the action bar if it is present.
////		super.onCreateOptionsMenu(menu);
//		getActivity().getMenuInflater().inflate(R.menu.risuscito, menu);
//		super.onCreateOptionsMenu(menu, inflater);
////		return true;
//	}
	
//    @Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case R.id.action_settings:
//			startActivity(new Intent(getActivity(), Settings.class));
//			return true;
//		case R.id.action_favourites:
//			startActivity(new Intent(getActivity(), FavouritesActivity.class));
//			return true;
//		case R.id.action_donate:
//			startActivity(new Intent(getActivity(), DonateActivity.class));
//			return true;
//		case R.id.action_about:
//			startActivity(new Intent(getActivity(), AboutActivity.class));
//			return true;
//		}
//		return false;
//	}
    
//    //metodo che lancia l'indice
//    private void startSubActivityPage() {
//    	startActivity(new Intent(getActivity(), GeneralIndex.class));
//    }
//    
//    //metodo che lancia la ricerca per titolo
//    private void startSubActivitySearch() {
//    	startActivity(new Intent(getActivity(), GeneralSearch.class));
//    }
//    
//    //metodo che lancia le liste personalizzate
//    private void startSubActivityLists() {
//    	startActivity(new Intent(getActivity(), CustomLists.class));
//    }
    
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
    	dialog.dismiss();
    	getActivity().setRequestedOrientation(prevOrientation);
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
