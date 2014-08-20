package it.cammino.risuscito;

import org.arasthel.googlenavdrawermenu.views.GoogleNavigationDrawer;
import org.holoeverywhere.FontLoader;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;

import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

    private DatabaseCanti listaCanti;
    
    public GoogleNavigationDrawer mDrawer;
    private ActionBarDrawerToggle drawerToggle;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Utility.updateThemeWithSlider(MainActivity.this);
        FontLoader.setDefaultFont(FontLoader.ROBOTO_CONDENSED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
        getSupportActionBar().setLogo(R.drawable.transparent);
        
		//crea un istanza dell'oggetto DatabaseCanti
		listaCanti = new DatabaseCanti(this);
        
        if (findViewById(R.id.content_layout) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            Risuscito firstFragment = new Risuscito();
            
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());
            
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_layout, firstFragment).commit();
        }

    }
    
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        drawerToggle.syncState();
//    }
    
    @Override
    public void onResume() {
    	mDrawer = (GoogleNavigationDrawer) findViewById(R.id.navigation_drawer_container);
        
        String[] mainSections = getResources().getStringArray(R.array.navigation_main_sections);
        mainSections[4] +=  " (" + getFavoritesCount() + ")";

        mDrawer.setListViewSections(mainSections, // Main sections
        		getResources().getStringArray(R.array.navigation_secondary_sections), // Secondary sections
                getDrawablesMain(), // Main sections icon ids
                getDrawablesSecondary()); // Secondary sections icon ids
        
//        mDrawer.setShouldChangeTitle(MainActivity.this, true);
        
        //Prepare the drawerToggle in order to be able to open/close the drawer
        drawerToggle = new ActionBarDrawerToggle(this,
                mDrawer,
                R.drawable.ic_fa_bars,
                R.string.app_name,
                R.string.app_name);
      
        //Attach the DrawerListener
        mDrawer.setDrawerListener(drawerToggle);
        
        drawerToggle.syncState();
        
        mDrawer.setOnNavigationSectionSelected(new GoogleNavigationDrawer.OnNavigationSectionSelected() {
            @Override
            public void onSectionSelected(View v, int i, long l) {
                
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            	switch (i) {
				case 1:
	                transaction.replace(R.id.content_layout, new Risuscito());
					break;
				case 2:
	                transaction.replace(R.id.content_layout, new GeneralSearch());
					break;
				case 3:
	                transaction.replace(R.id.content_layout, new GeneralIndex());
	                break;
				case 4:
	                transaction.replace(R.id.content_layout, new CustomLists());
	                break;
				case 5:
	                transaction.replace(R.id.content_layout, new FavouritesActivity());
	                break;
				case 6:
	                transaction.replace(R.id.content_layout, new Settings());
	                break;
				case 7:
	                transaction.replace(R.id.content_layout, new AboutActivity());
	                break;
				case 8:
	                transaction.replace(R.id.content_layout, new DonateActivity());
	                break;
	            default:
	                transaction.replace(R.id.content_layout, new Risuscito());
            	}

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });
    	super.onResume();
    }    
    
	@Override
	public void onDestroy() {
		listaCanti.close();
		super.onDestroy();
	}
    
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if (mDrawer. == 0) {
//				finish();
//				return true;
//			}
//			else {
//				mDrawer.setCurrentPage(0);
//				return true;
//			}
//        }
//        return super.onKeyUp(keyCode, event);
//    }
    
    //controlla se l'app deve mantenere lo schermo acceso
    public void checkScreenAwake() {
    	SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this);
			boolean screenOn = pref.getBoolean("screenOn", false);
		if (screenOn)
			findViewById(R.id.slider_menu).setKeepScreenOn(true);
		else
			findViewById(R.id.slider_menu).setKeepScreenOn(true);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
         * Declare the behaviour of clicking at the
         * application icon, opening and closing the drawer
         */
        if(item.getItemId() == android.R.id.home) {
            if(mDrawer != null) {
                if(mDrawer.isDrawerMenuOpen()) {
                    mDrawer.closeDrawerMenu();
                } else {
                    mDrawer.openDrawerMenu();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
    
    private int getFavoritesCount() {
    	
    	// crea un manipolatore per il Database in modalità READ
		SQLiteDatabase db = listaCanti.getReadableDatabase();
		
		// lancia la ricerca dei preferiti
		String query = "SELECT count(*)" +
				"		FROM ELENCO" +
				"		WHERE favourite = 1";
		Cursor curs = db.rawQuery(query, null);
		
		curs.moveToFirst();
		
		//recupera il numero di record trovati
		int total = curs.getInt(0);
		
		// chiude il cursore
		curs.close();
		
		db.close();
		
		return total;
    }
    
    private int[] getDrawablesMain(){
    	
    	int theme = Utility.getChoosedTheme(MainActivity.this);
    	TypedArray imgs = null;
    	
    	if (theme%2 == 0)
    		imgs = getResources()
    			.obtainTypedArray(R.array.drawable_ids_main_light);
    	else
    		imgs = getResources()
			.obtainTypedArray(R.array.drawable_ids_main_dark);

        int[] mainSectionDrawables = new int[imgs.length()];

        for(int i=0;i<imgs.length();i++){
            mainSectionDrawables[i] = imgs.getResourceId(i, 0);
        }

        return mainSectionDrawables;
    }
    
    private int[] getDrawablesSecondary(){
    	
    	int theme = Utility.getChoosedTheme(MainActivity.this);
    	TypedArray imgs = null;
    	
    	if (theme%2 == 0)
    		imgs = getResources()
    			.obtainTypedArray(R.array.drawable_ids_secondary_light);
    	else
    		imgs = getResources()
			.obtainTypedArray(R.array.drawable_ids_secondary_dark);

        int[] mainSectionDrawables = new int[imgs.length()];

        for(int i=0;i<imgs.length();i++){
            mainSectionDrawables[i] = imgs.getResourceId(i, 0);
        }

        return mainSectionDrawables;
    }
    
}
