package it.cammino.risuscito;

import java.util.Locale;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GeneralIndex extends Fragment { 

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	SlidingTabLayout mSlidingTabLayout = null;
//	private PagerSlidingTabStrip tabs;
	private int defaultIndex;
  	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
				
//		getSupportActionBar().setTitle(R.string.title_activity_general_index);
		View rootView = inflater.inflate(R.layout.activity_general_index, container, false);
		((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_activity_general_index);
		
		// Create the adapter that will return a fragment for each of the three
		mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
//	    mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
//	    mViewPager.setAdapter(mSectionsPagerAdapter);
//	    mViewPager.setCurrentItem(0);

	    mViewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
	    
//	    int i;
//        for (i = 0; i < Config.CONFERENCE_DAYS.length; i++) {
//            mScheduleAdapters[i] = new MyScheduleAdapter(this, getLUtils());
//        }

        mViewPager.setAdapter(mSectionsPagerAdapter);
	    
        mSlidingTabLayout = (SlidingTabLayout) rootView.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);

//        setSlidingTabLayoutContentDescriptions();

//        TypedValue outValue = new TypedValue();
//        getActivity().getApplicationContext().getTheme().resolveAttribute(android.R.attr.colorAccent,
//                outValue, true);
        
        Resources res = getResources();
        mSlidingTabLayout.setSelectedIndicatorColors(res.getColor(R.color.theme_accent));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);
        
//	    if (savedInstanceState == null) {
//	    	SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(getActivity());
//			defaultIndex = Integer.parseInt(pref.getString("defaultIndex", "0"));
//			mViewPager.setCurrentItem(defaultIndex);
////			mViewPager.postDelayed(new Runnable() {
////	
////		        @Override
////		        public void run() {
////		        	mViewPager.setCurrentItem(defaultIndex);
////		        }
////		    }, 100);
//	    }
//	    else {
//	    	mViewPager.setCurrentItem(savedInstanceState.getInt("pageViewed", 0));
//	    }
//	    tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
//	    tabs.setViewPager(mViewPager);

		return rootView;

	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pageViewed", mViewPager.getCurrentItem());
    }
	
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
            case 3:
                return new SalmiSectionFragment();
            default:
                return new AlphabeticSectionFragment();
            }
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
					return getString(R.string.letter_order_text).toUpperCase(l);
				else
					return getString(R.string.letter_order_text);
			case 1:
				if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
					return getString(R.string.page_order_text).toUpperCase(l);
				else
					return getString(R.string.page_order_text);
			case 2:
				if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
					return getString(R.string.arg_search_text).toUpperCase(l);
				else
					return getString(R.string.arg_search_text);
			case 3:
				if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
					return getString(R.string.salmi_musica_index).toUpperCase(l);
				else
					return getString(R.string.salmi_musica_index);
			}
			return null;
		}
	}
			
}