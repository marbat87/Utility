package it.cammino.risuscito;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ViewPager;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

public class GeneralSearch extends Fragment {

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
  	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		getSupportActionBar().setSubtitle(R.string.title_activity_search);
		View rootView = inflater.inflate(R.layout.activity_general_search, container, false);
		
		// Create the adapter that will return a fragment for each of the three
		mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
	    mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
	    mViewPager.setAdapter(mSectionsPagerAdapter);
	    mViewPager.setCurrentItem(0);
	    
	    PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
	    tabs.setViewPager(mViewPager);
		
//		checkScreenAwake();
  
//	    setHasOptionsMenu(true);
	    
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
                return new RicercaVeloceFragment();
            case 1:
                return new RicercaAvanzataFragment();
            default:
                return new RicercaVeloceFragment();
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