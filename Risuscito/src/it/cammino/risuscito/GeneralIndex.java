package it.cammino.risuscito;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.ViewPager;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

public class GeneralIndex extends Fragment { 

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private PagerSlidingTabStrip tabs;
	private int defaultIndex;
  	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
				
		getSupportActionBar().setTitle(R.string.title_activity_general_index);
		View rootView = inflater.inflate(R.layout.activity_general_index, container, false);
		
		// Create the adapter that will return a fragment for each of the three
		mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
	    mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
	    mViewPager.setAdapter(mSectionsPagerAdapter);
//	    mViewPager.setCurrentItem(0);

    	SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(getActivity());
		defaultIndex = Integer.parseInt(pref.getString("defaultIndex", "0"));
//		mViewPager.setCurrentItem(defaultIndex);
		mViewPager.postDelayed(new Runnable() {

	        @Override
	        public void run() {
	        	mViewPager.setCurrentItem(defaultIndex);
	        }
	    }, 100);
	    tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
	    tabs.setViewPager(mViewPager);

//		checkScreenAwake();
		
//		setHasOptionsMenu(true);
		
		return rootView;

	}

//    @Override
//    public void onResume() {
//    	super.onResume();
//    	checkScreenAwake();
//    }
	
//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		getActivity().getMenuInflater().inflate(R.menu.risuscito, menu);
//		super.onCreateOptionsMenu(menu, inflater);
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

//    //controlla se l'app deve mantenere lo schermo acceso
//    public void checkScreenAwake() {
//    	SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(getActivity());
//		boolean screenOn = pref.getBoolean("screenOn", false);
//		if (screenOn)
//			mViewPager.setKeepScreenOn(true);
//		else
//			mViewPager.setKeepScreenOn(false);
//    }

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
            switch (position) {
            case 0:
                return new AlphabeticSectionFragment();
            case 1:
                return new NumericSectionFragment();
            case 2:
                return new ArgumentsSectionFragment();
            default:
                return new AlphabeticSectionFragment();
            }
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
//			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
//				return getString(R.string.letter_order_text).toUpperCase(l);
				return getString(R.string.letter_order_text);
			case 1:
//				return getString(R.string.page_order_text).toUpperCase(l);
				return getString(R.string.page_order_text);
			case 2:
//				return getString(R.string.arg_search_text).toUpperCase(l);
				return getString(R.string.arg_search_text);
			}
			return null;
		}
	}
			
}