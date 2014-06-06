package it.cammino.risuscito;

import it.cammino.risuscito.GenericDialogFragment.GenericDialogListener;
import it.cammino.risuscito.TextDialogFragment.TextDialogListener;

import java.util.Locale;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.Toast;
import org.holoeverywhere.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.MenuItem;

public class CustomLists extends Activity
						implements GenericDialogListener, TextDialogListener {

	protected SectionsPagerAdapter mSectionsPagerAdapter;
	private String[] titoliListe;
	private int[] idListe;
	private DatabaseCanti listaCanti;
	private ActionBar actionBar;
	private int listaDaCanc;
	private int prevOrientation;
		
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	protected ViewPager mViewPager;
  	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Utility.updateTheme(CustomLists.this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_lists);
		
		actionBar = getSupportActionBar();
		
		// Specify that tabs should be displayed in the action bar.
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		//crea un istanza dell'oggetto DatabaseCanti
		listaCanti = new DatabaseCanti(getApplicationContext());
		
		updateLista();
		
		// Create the adapter that will return a fragment for each of the three
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
	    mViewPager = (ViewPager) findViewById(R.id.pager);
	    mViewPager.setAdapter(mSectionsPagerAdapter);
	    mViewPager.setCurrentItem(0);
	    mViewPager.setOnPageChangeListener(
	    		new ViewPager.SimpleOnPageChangeListener() {
	    			@Override
	                public void onPageSelected(int position) {
	                    // When swiping between pages, select the
	                    // corresponding tab.
	    				actionBar.setSelectedNavigationItem(position);
	    			}
	    		}
	    );
    	
	    // Create a tab listener that is called when the user changes tabs.
	    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
	        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
	        	// When the tab is selected, switch to the
	            // corresponding page in the ViewPager.
	            mViewPager.setCurrentItem(tab.getPosition());
	        }

	        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // hide the given tab
	        }

	        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // probably ignore this event
	        }
	    };
	    
	    actionBar.removeAllTabs();
        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(tabListener));
        }

	}

    @Override
    public void onResume() {
    	super.onResume();
    	updateLista();
    	mSectionsPagerAdapter.notifyDataSetChanged();
	    if (mSectionsPagerAdapter.getCount() > actionBar.getTabCount()) {
		    // Create a tab listener that is called when the user changes tabs.
		    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
		        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
		        	// When the tab is selected, switch to the
		            // corresponding page in the ViewPager.
		            mViewPager.setCurrentItem(tab.getPosition());
		        }

		        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
		            // hide the given tab
		        }

		        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
		            // probably ignore this event
		        }
		    };
	    	
	    	actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(
                            		mSectionsPagerAdapter.getCount() - 1))
                            .setTabListener(tabListener));
	    }

    	checkScreenAwake();
    }
    
	@Override
	public void onDestroy() {
		if (listaCanti != null)
			listaCanti.close();
		super.onDestroy();
	}
    
    //controlla se l'app deve mantenere lo schermo acceso
    public void checkScreenAwake() {
    	SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this);
		boolean screenOn = pref.getBoolean("screenOn", false);
		if (screenOn)
			mViewPager.setKeepScreenOn(true);
		else
			mViewPager.setKeepScreenOn(false);
    }
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
            return true;
		case R.id.action_add_list:
			blockOrientation();
			TextDialogFragment dialog = new TextDialogFragment();
			dialog.setCustomMessage(getString(R.string.lista_add_desc));
			dialog.setListener(CustomLists.this);
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
			return true;
		case R.id.action_edit_list:
			Bundle bundle = new Bundle();
			bundle.putInt("idDaModif", idListe[mViewPager.getCurrentItem() - 2]);
			bundle.putBoolean("modifica", true);
			startActivity(new Intent(this, CreaListaActivity.class).putExtras(bundle));
			return true;
		case R.id.action_remove_list:
			blockOrientation();
			listaDaCanc = mViewPager.getCurrentItem() - 2;
			GenericDialogFragment dialogR = new GenericDialogFragment();
			dialogR.setListener(CustomLists.this);
			dialogR.setCustomMessage(getString(R.string.list_delete));
			dialogR.setOnKeyListener(new Dialog.OnKeyListener() {

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
			dialogR.show(getSupportFragmentManager());
			dialogR.setCancelable(false);
			return true;
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
    
    private void updateLista() {
		
    	SQLiteDatabase db = listaCanti.getReadableDatabase();
    	
	    String query = "SELECT titolo_lista, lista, _id"
	      		+ "  FROM LISTE_PERS A"
	      		+ "  ORDER BY _id ASC";
	    Cursor cursor = db.rawQuery(query, null);
	     
	    int total = cursor.getCount();
//	    Log.i("RISULTATI", total+"");
	    
	    titoliListe = new String[total];
	    idListe = new int[total];

	    cursor.moveToFirst();
	    for (int i = 0; i < total; i++) {
//    		Log.i("LISTA IN POS[" + i + "]:", cursor.getString(0));
	    	titoliListe[i] =  cursor.getString(0);
    		idListe[i] = cursor.getInt(2);
	    	cursor.moveToNext();
	    }
	    
	    cursor.close();
	    db.close();
    
    }
    
    
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
            switch (position) {
            case 0:
                return new CantiParolaFragment();
            case 1:
                return new CantiEucarestiaFragment();
            default:
            	Bundle bundle=new Bundle();
//            	Log.i("INVIO", "position = " + position);
//            	Log.i("INVIO", "idLista = " + idListe[position - 2]);
            	
            	bundle.putInt("position", position);
            	bundle.putInt("idLista", idListe[position - 2]);
            	ListaPersonalizzataFragment listaPersFrag = new ListaPersonalizzataFragment();
            	listaPersFrag.setArguments(bundle);
            	return listaPersFrag;
            }
		}

		@Override
		public int getCount() {
			return 2 + titoliListe.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_activity_canti_parola).toUpperCase(l);
			case 1:
				return getString(R.string.title_activity_canti_eucarestia).toUpperCase(l);
			default:
				return titoliListe[position - 2].toUpperCase(l);
			}
		}
		
	    @Override
	    public int getItemPosition(Object object){
	        return PagerAdapter.POSITION_NONE;
	    }
	}
	
	//chiamato quando si conferma di voler creare una lista
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String titolo) {
        // User touched the dialog's positive button
    	if (titolo == null || titolo.trim().equalsIgnoreCase("")) {
    		Toast toast = Toast.makeText(getApplicationContext()
    				, getString(R.string.titolo_pos_vuoto), Toast.LENGTH_SHORT);
    		toast.show();
    		dialog.dismiss();
    		setRequestedOrientation(prevOrientation);
    	}
    	else {
    		dialog.dismiss();
    		setRequestedOrientation(prevOrientation);
			Bundle bundle = new Bundle();
			bundle.putString("titolo", titolo);
			bundle.putBoolean("modifica", false);
			startActivity(new Intent(this, CreaListaActivity.class).putExtras(bundle));			
    	}
		
    }
    
    //chiamato quando si conferma di voler cancellare una lista
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
    	SQLiteDatabase db = listaCanti.getReadableDatabase();
    	
//    	Log.i("INDICE DA CANC", listaDaCanc+" ");
    	
	    String sql = "DELETE FROM LISTE_PERS"
	      		+ " WHERE _id = " + idListe[listaDaCanc];
	    db.execSQL(sql);
		db.close();
		
		updateLista();
		mSectionsPagerAdapter.notifyDataSetChanged();
		actionBar.removeTabAt(listaDaCanc + 2);
		setRequestedOrientation(prevOrientation);
    }
    
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        dialog.dismiss();
        setRequestedOrientation(prevOrientation);
    }
    
    public void blockOrientation() {
        prevOrientation = getRequestedOrientation();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }
    }
	
}
