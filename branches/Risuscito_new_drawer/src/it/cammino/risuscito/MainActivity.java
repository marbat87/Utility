package it.cammino.risuscito;

import org.arasthel.googlenavdrawermenu.views.GoogleNavigationDrawer;
import org.holoeverywhere.FontLoader;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {
    
    public GoogleNavigationDrawer mDrawer;
    private ActionBarDrawerToggle drawerToggle;
    
    private static final String TAG_MAIN_FRAGMENT = "main_fragment";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Utility.updateThemeWithSlider(MainActivity.this);
        FontLoader.setDefaultFont(FontLoader.ROBOTO_CONDENSED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mDrawer = (GoogleNavigationDrawer) findViewById(R.id.navigation_drawer_container);

        /*
         * We get the drawerToggle object order to
         * allow showing the NavigationDrawer icon
         */
        drawerToggle = new ActionBarDrawerToggle(this,
                mDrawer,
                R.drawable.ic_fa_bars,
                R.string.app_name,
                R.string.app_name);

        mDrawer.setDrawerListener(drawerToggle); //Attach the DrawerListener
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setLogo(R.drawable.transparent);
        
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
                    .add(R.id.content_layout, firstFragment, TAG_MAIN_FRAGMENT).commit();
        }

    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawer.setOnNavigationSectionSelected(new GoogleNavigationDrawer.OnNavigationSectionSelected() {
            @Override
            public void onSectionSelected(View v, int i, long l) {
                
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            	switch (i) {
				case 1:
	                transaction.replace(R.id.content_layout, new Risuscito(), TAG_MAIN_FRAGMENT);
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
	            	transaction.replace(R.id.content_layout, new Risuscito(), TAG_MAIN_FRAGMENT);
            	}

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
//                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });
        drawerToggle.syncState();
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	Risuscito myFragment = (Risuscito)getSupportFragmentManager().findFragmentByTag(TAG_MAIN_FRAGMENT);
        	if (myFragment != null && myFragment.isVisible()) {
        		finish();
        	}
        	else {
        		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        		transaction.replace(R.id.content_layout, new Risuscito(), TAG_MAIN_FRAGMENT);
        		transaction.commit();
        	}
        	return true;
        }
        return super.onKeyUp(keyCode, event);
    }
    
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
    
}
