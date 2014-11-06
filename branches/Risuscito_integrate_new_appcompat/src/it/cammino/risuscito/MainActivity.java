package it.cammino.risuscito;

import org.arasthel.googlenavdrawermenu.views.GoogleNavigationDrawer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {
    
    public GoogleNavigationDrawer mDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private String[] mPlanetTitles;
    private ListView mDrawerList;
    private DrawerLayout drawerLayout;
    
//    private static final String TAG_MAIN_FRAGMENT = "main_fragment";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
//    	Utility.updateThemeWithSlider(MainActivity.this);
//        FontLoader.setDefaultFont(FontLoader.ROBOTO_CONDENSED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.risuscito_toolbar);
        setSupportActionBar(toolbar);
        
        // setta il colore della barra di stato, solo da KITAKT in su
        Utility.setupTransparentTints(MainActivity.this);
        
     // Now retrieve the DrawerLayout so that we can set the status bar color.
        // This only takes effect on Lollipop, or when using translucentStatusBar
        // on KitKat.
        
        mPlanetTitles = getResources().getStringArray(R.array.pages_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        drawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);
//        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.IndigoDark));
//        mDrawer = (GoogleNavigationDrawer) findViewById(R.id.navigation_drawer_container);
        
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        /*
         * We get the drawerToggle object order to
         * allow showing the NavigationDrawer icon
         */
//        drawerToggle = new ActionBarDrawerToggle(this,
//                mDrawer,
//                R.drawable.ic_fa_bars,
//                R.string.app_name,
//                R.string.app_name);
        
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.drawable.ic_fa_bars, R.drawable.ic_fa_bars);

        drawerLayout.setDrawerListener(drawerToggle); //Attach the DrawerListener
        
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setLogo(R.drawable.transparent);
        
//        if (findViewById(R.id.content_layout) != null) {
//
//            // However, if we're being restored from a previous state,
//            // then we don't need to do anything and should return or else
//            // we could end up with overlapping fragments.
//            if (savedInstanceState != null) {
//                return;
//            }
//
//            // Create a new Fragment to be placed in the activity layout
//            Risuscito firstFragment = new Risuscito();
//            
//            // In case this activity was started with special instructions from an
//            // Intent, pass the Intent's extras to the fragment as arguments
//            firstFragment.setArguments(getIntent().getExtras());
//            
//            // Add the fragment to the 'fragment_container' FrameLayout
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.content_layout, firstFragment, TAG_MAIN_FRAGMENT).commit();
//        }
        
        if (savedInstanceState == null) {
            selectItem(0);
        }

    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	checkScreenAwake();
    }
    
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    
    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;

        switch (position) {
		case 0:
			fragment = (Fragment) new Risuscito();
			break;
		case 1:
			fragment = (Fragment) new GeneralIndex();
			break;
//		case 2:
//			fragment = (Fragment) new GeneralSearch();
//            break;
        default:
        	fragment = (Fragment) new Risuscito();
        	break;
    	}
        
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        transaction.replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        drawerLayout.closeDrawer(mDrawerList);
    }
    
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        mDrawer.setOnNavigationSectionSelected(new GoogleNavigationDrawer.OnNavigationSectionSelected() {
//            @Override
//            public void onSectionSelected(View v, int i, long l) {
//                
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//            	switch (i) {
//				case 1:
//					transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
//					transaction.replace(R.id.content_layout, new Risuscito(), TAG_MAIN_FRAGMENT);
//					break;
//				case 2:
//					transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
//	                transaction.replace(R.id.content_layout, new GeneralSearch());
//					break;
//				case 3:
//					transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
//	                transaction.replace(R.id.content_layout, new GeneralIndex());
//	                break;
//				case 4:
//					transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
//	                transaction.replace(R.id.content_layout, new CustomLists());
//	                break;
//				case 5:
//					transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
//	                transaction.replace(R.id.content_layout, new FavouritesActivity());
//	                break;
//				case 6:
//					transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
//					transaction.replace(R.id.content_layout, new FavouritesActivity());
////	                transaction.replace(R.id.content_layout, new Settings());
//	                break;
//				case 7:
//					transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
//	                transaction.replace(R.id.content_layout, new AboutActivity());
//	                break;
//				case 8:
//					transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
//	                transaction.replace(R.id.content_layout, new DonateActivity());
//	                break;
//	            default:
//					transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
//	            	transaction.replace(R.id.content_layout, new Risuscito(), TAG_MAIN_FRAGMENT);
//            	}
//
//                // Replace whatever is in the fragment_container view with this fragment,
//                // and add the transaction to the back stack so the user can navigate back
////                transaction.addToBackStack(null);
//
//                // Commit the transaction
//                transaction.commit();
//            }
//        });
//        drawerToggle.syncState();
//    }
    
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//        	Risuscito myFragment = (Risuscito)getSupportFragmentManager().findFragmentByTag(TAG_MAIN_FRAGMENT);
//        	if (myFragment != null && myFragment.isVisible()) {
//        		finish();
//        	}
//        	else {
//        		mDrawer.check(1);
//        		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//				transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
//        		transaction.replace(R.id.content_layout, new Risuscito(), TAG_MAIN_FRAGMENT);
//        		transaction.commit();
//        	}
//        	return true;
//        }
//        return super.onKeyUp(keyCode, event);
//    }
    
    //controlla se l'app deve mantenere lo schermo acceso
    public void checkScreenAwake() {
    	SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this);
			boolean screenOn = pref.getBoolean("screenOn", false);
		if (screenOn)
			findViewById(R.id.content_frame).setKeepScreenOn(true);
		else
			findViewById(R.id.content_frame).setKeepScreenOn(true);
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
