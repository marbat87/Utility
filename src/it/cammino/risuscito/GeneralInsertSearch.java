package it.cammino.risuscito;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.ViewPager;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

public class GeneralInsertSearch extends Activity {

	SectionsPagerAdapter mSectionsPagerAdapter;
	
	private int fromAdd;
	private int idLista;
	private int listPosition;
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
  	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Utility.updateTheme(GeneralInsertSearch.this);
		super.onCreate(savedInstanceState);
		
		ActionBar actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setLogo(R.drawable.transparent);
		
		setContentView(R.layout.activity_general_search);
		
		Bundle bundle = GeneralInsertSearch.this.getIntent().getExtras();
		fromAdd = bundle.getInt("fromAdd");
        idLista = bundle.getInt("idLista");
        listPosition = bundle.getInt("position");
		
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
	    mViewPager.setAdapter(mSectionsPagerAdapter);
	    mViewPager.setCurrentItem(0);
	    
	    PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
	    tabs.setViewPager(mViewPager);
		
//		final ActionBar actionBar = getSupportActionBar();
//		actionBar.setDisplayHomeAsUpEnabled(true);
	    
//	    mViewPager = (ViewPager) findViewById(R.id.pager);
//	    mViewPager.setAdapter(mSectionsPagerAdapter);
//	    mViewPager.setOnPageChangeListener(
//	    		new ViewPager.SimpleOnPageChangeListener() {
//	    			@Override
//	                public void onPageSelected(int position) {
//	                    // When swiping between pages, select the
//	                    // corresponding tab.
//	    				actionBar.setSelectedNavigationItem(position);
//	    			}
//	    		}
//	    );
//	    
//	    // Create a tab listener that is called when the user changes tabs.
//	    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
//	        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
//	        	// When the tab is selected, switch to the
//	            // corresponding page in the ViewPager.
//	            mViewPager.setCurrentItem(tab.getPosition());
//	        }
//
//	        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
//	            // hide the given tab
//	        }
//
//	        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
//	            // probably ignore this event
//	        }
//	    };
//	    
//        // For each of the sections in the app, add a tab to the action bar.
//        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
//            // Create a tab with text corresponding to the page title defined by the adapter.
//            // Also specify this Activity object, which implements the TabListener interface, as the
//            // listener for when this tab is selected.
//            actionBar.addTab(
//                    actionBar.newTab()
//                            .setText(mSectionsPagerAdapter.getPageTitle(i))
//                            .setTabListener(tabListener));
//        }
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

//		mViewPager.setCurrentItem(0);
		checkScreenAwake();

	}

    @Override
    public void onResume() {
    	super.onResume();
    	checkScreenAwake();
    }
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.insert_search, menu);
//		return true;
//	}
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
            // app icon in action bar clicked; go home
//			NavUtils.navigateUpFromSameTask(this);
			finish();
            return true;
//		case R.id.action_settings:
//			startActivity(new Intent(this, Settings.class));
//			return true;
//		case R.id.action_donate:
//			startActivity(new Intent(this, DonateActivity.class));
//			return true;
//		case R.id.action_about:
//			startActivity(new Intent(this, AboutActivity.class));
//			return true;
		}
		return false;
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
    
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
            switch (position) {
            case 0:
            	Bundle bundle = new Bundle();
			    bundle.putInt("fromAdd", fromAdd);
			    bundle.putInt("idLista", idLista);
			    bundle.putInt("position", listPosition);
			    InsertVeloceFragment insertVeloceFrag = new InsertVeloceFragment();
			    insertVeloceFrag.setArguments(bundle);
            	return insertVeloceFrag;
            case 1:
            	Bundle bundle1 = new Bundle();
			    bundle1.putInt("fromAdd", fromAdd);
			    bundle1.putInt("idLista", idLista);
			    bundle1.putInt("position", listPosition);
			    InsertAvanzataFragment insertAvanzataFrag = new InsertAvanzataFragment();
			    insertAvanzataFrag.setArguments(bundle1);
            	return insertAvanzataFrag;
            default:
            	Bundle bundle2 = new Bundle();
			    bundle2.putInt("fromAdd", fromAdd);
			    bundle2.putInt("idLista", idLista);
			    bundle2.putInt("position", listPosition);
			    InsertVeloceFragment insertVeloceFrag2 = new InsertVeloceFragment();
			    insertVeloceFrag2.setArguments(bundle2);
            	return insertVeloceFrag2;
            }
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
//			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
//				return getString(R.string.fast_search_title).toUpperCase(l);
				return getString(R.string.fast_search_title);
			case 1:
//				return getString(R.string.advanced_search_title).toUpperCase(l);
				return getString(R.string.advanced_search_title);
			}
			return null;
		}
	}
			
}