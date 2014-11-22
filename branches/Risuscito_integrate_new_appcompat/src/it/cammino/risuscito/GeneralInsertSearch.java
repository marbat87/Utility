package it.cammino.risuscito;

import java.util.Locale;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;

public class GeneralInsertSearch extends ActionBarActivity {

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	SlidingTabLayout mSlidingTabLayout = null;
	
	private int fromAdd;
	private int idLista;
	private int listPosition;
	
//	/**
//	 * The {@link ViewPager} that will host the section contents.
//	 */
//	ViewPager mViewPager;
  	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		Utility.updateTheme(GeneralInsertSearch.this);
		super.onCreate(savedInstanceState);
//		
//		ActionBar actionbar = getSupportActionBar();
//		actionbar.setDisplayHomeAsUpEnabled(true);
//		actionbar.setLogo(R.drawable.transparent);
		
		setContentView(R.layout.activity_insert_search);
		
		Toolbar toolbar = ((Toolbar) findViewById(R.id.risuscito_toolbar));
		toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
		setSupportActionBar(toolbar);
		
        // setta il colore della barra di stato, solo su KITKAT
        Utility.setupTransparentTints(GeneralInsertSearch.this);
		
		Bundle bundle = GeneralInsertSearch.this.getIntent().getExtras();
		fromAdd = bundle.getInt("fromAdd");
        idLista = bundle.getInt("idLista");
        listPosition = bundle.getInt("position");
		
//		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//		mViewPager = (ViewPager) findViewById(R.id.pager);
//	    mViewPager.setAdapter(mSectionsPagerAdapter);
//	    mViewPager.setCurrentItem(0);
//	    
//	    PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
//	    tabs.setViewPager(mViewPager);
//		
////				mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
//	    mViewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
//        mViewPager.setAdapter(mSectionsPagerAdapter);
        
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
	    mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        
        Resources res = getResources();
        mSlidingTabLayout.setSelectedIndicatorColors(res.getColor(R.color.theme_accent));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);
        
		checkScreenAwake();

	}

    @Override
    public void onResume() {
    	super.onResume();
    	checkScreenAwake();
    }
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			overridePendingTransition(0, R.anim.slide_out_right);
            return true;
		}
		return false;
	}
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(0, R.anim.slide_out_right);
        }
        return super.onKeyUp(keyCode, event);
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
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
					return getString(R.string.fast_search_title).toUpperCase(l);
				else
					return getString(R.string.fast_search_title);
			case 1:
				if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
					return getString(R.string.advanced_search_title).toUpperCase(l);
				else
					return getString(R.string.advanced_search_title);
			}
			return null;
		}
	}
			
}