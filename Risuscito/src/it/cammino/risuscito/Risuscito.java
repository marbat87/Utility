package it.cammino.risuscito;

import it.cammino.risuscito.ChangelogDialogFragment.ChangelogDialogListener;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.Button;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class Risuscito extends Activity implements ChangelogDialogListener {

	private static final String VERSION_KEY = "PREFS_VERSION_KEY";
	private static final String NO_VERSION = "";
	private int prevOrientation;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Utility.updateTheme(Risuscito.this);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_risuscito);
		
        // recupera il pulsante che lancia l'elenco dei canti in ordine alfabetico
        Button buttonAlpha = (Button) findViewById(R.id.generalIndex);       
        buttonAlpha.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		startSubActivityPage();
        	}
        });
        
        // recupera il pulsante che lancia l'elenco dei canti per pagina
        Button buttonNum = (Button) findViewById(R.id.predefinedLists);       
        buttonNum.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		startSubActivityLists();
        	}
        }); 
        
        // recupera il pulsante che lancia la ricerca per titolo
        Button buttonSearchText = (Button) findViewById(R.id.titleSearch);       
        buttonSearchText.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		startSubActivitySearch();
        	}
        });
        
        checkScreenAwake();
        
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        
        // get version numbers
        String lastVersion = sp.getString(VERSION_KEY, NO_VERSION);
        String thisVersion = "";
//        Log.i("Changelog", "lastVersion: " + lastVersion);
        try {
            thisVersion = this.getPackageManager().getPackageInfo(
                    this.getPackageName(), 0).versionName;
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
						setRequestedOrientation(prevOrientation);
						return true;
	                }
	                return false;
	            }
	        });
	    	dialog.show(getSupportFragmentManager());
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
        
	}

    @Override
    public void onResume() {
    	super.onResume();
    	checkScreenAwake();
    }
    
    //controlla se l'app deve mantenere lo schermo acceso
    public void checkScreenAwake() {
    	SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this);
		boolean screenOn = pref.getBoolean("screenOn", false);
		ImageView imageView = (ImageView) findViewById(R.id.imageView1);
		if (screenOn)
			imageView.setKeepScreenOn(true);
		else
			imageView.setKeepScreenOn(false);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.risuscito, menu);
		return true;
	}
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, Settings.class));
			return true;
		case R.id.action_favourites:
			startActivity(new Intent(this, FavouritesActivity.class));
			return true;
		case R.id.action_about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		}
		return false;
	}
    
    //metodo che lancia l'indice
    private void startSubActivityPage() {
    	startActivity(new Intent(this, GeneralIndex.class));
    }
    
    //metodo che lancia la ricerca per titolo
    private void startSubActivitySearch() {
    	startActivity(new Intent(this, GeneralSearch.class));
    }
    
    //metodo che lancia le liste personalizzate
    private void startSubActivityLists() {
    	startActivity(new Intent(this, CustomLists.class));
    }
    
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
    	dialog.dismiss();
    	setRequestedOrientation(prevOrientation);
    }
    
    public void blockOrientation() {
        prevOrientation = this.getRequestedOrientation();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
        	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }
    }

}
