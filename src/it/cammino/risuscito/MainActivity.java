package it.cammino.risuscito;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
    
//    private ActionBarDrawerToggle drawerToggle;
//    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ViewGroup mDrawerItemsListContainer;
    private Toolbar mActionBarToolbar;
    private Handler mHandler;
    
    // list of navdrawer items that were actually added to the navdrawer, in order
    private ArrayList<Integer> mNavDrawerItems = new ArrayList<Integer>();

    // views that correspond to each navdrawer item, null if not yet created
    private View[] mNavDrawerItemViews = null;
    
    protected static final int NAVDRAWER_ITEM_HOMEPAGE = 0;
    protected static final int NAVDRAWER_ITEM_SEARCH = 1;
    protected static final int NAVDRAWER_ITEM_INDEXES = 2;
    protected static final int NAVDRAWER_ITEM_LISTS = 3;
    protected static final int NAVDRAWER_ITEM_FAVORITES = 4;
    protected static final int NAVDRAWER_ITEM_SETTINGS = 5;
    protected static final int NAVDRAWER_ITEM_ABOUT = 6;
    protected static final int NAVDRAWER_ITEM_DONATE = 7;
    protected static final int NAVDRAWER_ITEM_INVALID = -1;
    protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;
    protected static final int NAVDRAWER_ITEM_COVER = -3;
    
    // titles for navdrawer items (indices must correspond to the above)
    private static final int[] NAVDRAWER_TITLE_RES_ID = new int[]{
            R.string.activity_homepage,
            R.string.search_name_text,
            R.string.title_activity_general_index,
            R.string.title_activity_custom_lists,
            R.string.action_favourites,
            R.string.title_activity_settings,
            R.string.title_activity_about,
            R.string.title_activity_donate
    };

    // icons for navdrawer items (indices must correspond to above array)
    private static final int[] NAVDRAWER_ICON_RES_ID = new int[] {
            R.drawable.ic_action_home_dark,  // My Schedule
            R.drawable.ic_action_search_dark,  // Explore
            R.drawable.ic_action_view_as_list_dark, // Map
            R.drawable.ic_action_add_to_queue_dark, // Social
            R.drawable.ic_action_favorite_dark, // Video Library
            R.drawable.ic_action_settings_dark,
            R.drawable.ic_action_about_dark,
            R.drawable.ic_action_good_dark
    };
    
    // delay to launch nav drawer item, to allow close animation to play
//    private static final int NAVDRAWER_LAUNCH_DELAY = 250;
    
    // fade in and fade out durations for the main content when switching between
    // different Activities of the app through the Nav Drawer
//    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
//    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;
    
    private static final String TAG_MAIN_FRAGMENT = "main_fragment";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
//    	Utility.updateThemeWithSlider(MainActivity.this);
//        FontLoader.setDefaultFont(FontLoader.ROBOTO_CONDENSED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mActionBarToolbar = (Toolbar) findViewById(R.id.risuscito_toolbar);
        setSupportActionBar(mActionBarToolbar);
        
        // setta il colore della barra di stato, solo da KITAKT in su
//        Utility.setupTransparentTints(MainActivity.this);
        
        mHandler = new Handler();
        
     // Now retrieve the DrawerLayout so that we can set the status bar color.
        // This only takes effect on Lollipop, or when using translucentStatusBar
        // on KitKat.
        
//        mPlanetTitles = getResources().getStringArray(R.array.pages_array);
//        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
//        drawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);
//        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.IndigoDark));
//        mDrawer = (GoogleNavigationDrawer) findViewById(R.id.navigation_drawer_container);
        
//        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
//                R.layout.drawer_list_item, mPlanetTitles));
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        /*
         * We get the drawerToggle object order to
         * allow showing the NavigationDrawer icon
         */
//        drawerToggle = new ActionBarDrawerToggle(this,
//                mDrawer,
//                R.drawable.ic_fa_bars,
//                R.string.app_name,
//                R.string.app_name);
        
//        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.drawable.ic_fa_bars, R.drawable.ic_fa_bars);

//        drawerLayout.setDrawerListener(drawerToggle); //Attach the DrawerListener
        setupNavDrawer();
        
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setLogo(R.drawable.transparent);
        
        if (findViewById(R.id.content_frame) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            transaction.replace(R.id.content_frame, new Risuscito(), String.valueOf(NAVDRAWER_ITEM_HOMEPAGE)).commit();
            
            setSelectedNavDrawerItem(NAVDRAWER_ITEM_HOMEPAGE);
        }
        
//        if (savedInstanceState == null) {
//            selectItem(0);
//        }

    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	checkScreenAwake();
    }
    
//    /* The click listner for ListView in the navigation drawer */
//    private class DrawerItemClickListener implements ListView.OnItemClickListener {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            selectItem(position);
//        }
//    }
//    
//    private void selectItem(int position) {
//        // update the main content by replacing fragments
//        Fragment fragment = null;
//
//        switch (position) {
//		case 0:
//			fragment = (Fragment) new Risuscito();
//			break;
//		case 1:
//			fragment = (Fragment) new GeneralIndex();
//			break;
////		case 2:
////			fragment = (Fragment) new GeneralSearch();
////            break;
//        default:
//        	fragment = (Fragment) new Risuscito();
//        	break;
//    	}
//        
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
//        transaction.replace(R.id.content_frame, fragment).commit();
//
//        // update selected item and title, then close the drawer
//        mDrawerList.setItemChecked(position, true);
//        setTitle(mPlanetTitles[position]);
//        drawerLayout.closeDrawer(mDrawerList);
//    }
    
    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses
     * of BaseActivity override this to indicate what nav drawer item corresponds to them
     * Return NAVDRAWER_ITEM_INVALID to mean that this Activity should not have a Nav Drawer.
     */
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }
    
    /**
     * Sets up the navigation drawer as appropriate. Note that the nav drawer will be
     * different depending on whether the attendee indicated that they are attending the
     * event on-site vs. attending remotely.
     */
    private void setupNavDrawer() {
        // What nav drawer item should be selected?
        int selfItem = getSelfNavDrawerItem();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }
        mDrawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.IndigoDark));
//        ScrimInsetsScrollView navDrawer = (ScrimInsetsScrollView)
//                mDrawerLayout.findViewById(R.id.navdrawer);
//        if (selfItem == NAVDRAWER_ITEM_INVALID) {
//            // do not show a nav drawer
//            if (navDrawer != null) {
//                ((ViewGroup) navDrawer.getParent()).removeView(navDrawer);
//            }
//            mDrawerLayout = null;
//            return;
//        }
//
//        if (navDrawer != null) {
//            final View chosenAccountContentView = findViewById(R.id.chosen_account_content_view);
//            final View chosenAccountView = findViewById(R.id.chosen_account_view);
//            final int navDrawerChosenAccountHeight = getResources().getDimensionPixelSize(
//                    R.dimen.navdrawer_chosen_account_height);
//            navDrawer.setOnInsetsCallback(new ScrimInsetsScrollView.OnInsetsCallback() {
//                @Override
//                public void onInsetsChanged(Rect insets) {
//                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)
//                            chosenAccountContentView.getLayoutParams();
//                    lp.topMargin = insets.top;
//                    chosenAccountContentView.setLayoutParams(lp);
//
//                    ViewGroup.LayoutParams lp2 = chosenAccountView.getLayoutParams();
//                    lp2.height = navDrawerChosenAccountHeight + insets.top;
//                    chosenAccountView.setLayoutParams(lp2);
//                }
//            });
//        }

        if (mActionBarToolbar != null) {
            mActionBarToolbar.setNavigationIcon(R.drawable.ic_drawer);
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            });
        }

//        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                // run deferred action, if we have one
//                if (mDeferredOnDrawerClosedRunnable != null) {
//                    mDeferredOnDrawerClosedRunnable.run();
//                    mDeferredOnDrawerClosedRunnable = null;
//                }
//                if (mAccountBoxExpanded) {
//                    mAccountBoxExpanded = false;
//                    setupAccountBoxToggle();
//                }
//                onNavDrawerStateChanged(false, false);
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                onNavDrawerStateChanged(true, false);
//            }
//
//            @Override
//            public void onDrawerStateChanged(int newState) {
//                onNavDrawerStateChanged(isNavDrawerOpen(), newState != DrawerLayout.STATE_IDLE);
//            }
//
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//                onNavDrawerSlide(slideOffset);
//            }
//        });

//        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        // populate the nav drawer with the correct items
        populateNavDrawer();

//        // When the user runs the app for the first time, we want to land them with the
//        // navigation drawer open. But just the first time.
//        if (!PrefUtils.isWelcomeDone(this)) {
//            // first run of the app starts with the nav drawer open
//            PrefUtils.markWelcomeDone(this);
//            mDrawerLayout.openDrawer(Gravity.START);
//        }
    }
    
    /** Populates the navigation drawer with the appropriate items. */
    private void populateNavDrawer() {
//        boolean attendeeAtVenue = PrefUtils.isAttendeeAtVenue(this);
        mNavDrawerItems.clear();

//        // decide which items will appear in the nav drawer
//        if (AccountUtils.hasActiveAccount(this)) {
//            // Only logged-in users can save sessions, so if there is no active account,
//            // there is no My Schedule
//            mNavDrawerItems.add(NAVDRAWER_ITEM_MY_SCHEDULE);
//        } else {
//            // If no active account, show Sign In
//            mNavDrawerItems.add(NAVDRAWER_ITEM_SIGN_IN);
//        }

        // Explore is always shown
        mNavDrawerItems.add(NAVDRAWER_ITEM_COVER);
        mNavDrawerItems.add(NAVDRAWER_ITEM_HOMEPAGE);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SEARCH);
        mNavDrawerItems.add(NAVDRAWER_ITEM_INDEXES);
        mNavDrawerItems.add(NAVDRAWER_ITEM_LISTS);
        mNavDrawerItems.add(NAVDRAWER_ITEM_FAVORITES);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SETTINGS);
        mNavDrawerItems.add(NAVDRAWER_ITEM_ABOUT);
        mNavDrawerItems.add(NAVDRAWER_ITEM_DONATE);
        
        
//        // If the attendee is on-site, show Map on the nav drawer
//        if (attendeeAtVenue) {
//            mNavDrawerItems.add(NAVDRAWER_ITEM_MAP);
//        }
//        mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);
//
//        // If attendee is on-site, show the People I've Met item
//        if (attendeeAtVenue) {
//            mNavDrawerItems.add(NAVDRAWER_ITEM_PEOPLE_IVE_MET);
//        }
//
//        // If the experts directory hasn't expired, show it
//        if (!Config.hasExpertsDirectoryExpired()) {
//            mNavDrawerItems.add(NAVDRAWER_ITEM_EXPERTS_DIRECTORY);
//        }
//
//        // Other items that are always in the nav drawer irrespective of whether the
//        // attendee is on-site or remote:
//        mNavDrawerItems.add(NAVDRAWER_ITEM_SOCIAL);
//        mNavDrawerItems.add(NAVDRAWER_ITEM_VIDEO_LIBRARY);
//        mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR_SPECIAL);
//        mNavDrawerItems.add(NAVDRAWER_ITEM_SETTINGS);
        
        createNavDrawerItems();
    }
    
    private void createNavDrawerItems() {
        mDrawerItemsListContainer = (ViewGroup) findViewById(R.id.navdrawer_items_list);
        if (mDrawerItemsListContainer == null) {
            return;
        }

        mNavDrawerItemViews = new View[mNavDrawerItems.size()];
        mDrawerItemsListContainer.removeAllViews();
        int i = 0;
        for (int itemId : mNavDrawerItems) {
            mNavDrawerItemViews[i] = makeNavDrawerItem(itemId, mDrawerItemsListContainer);
            mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
            ++i;
        }
    }
    
    private View makeNavDrawerItem(final int itemId, ViewGroup container) {
        boolean selected = getSelfNavDrawerItem() == itemId;
        int layoutToInflate = 0;
        if (itemId == NAVDRAWER_ITEM_SEPARATOR) {
            layoutToInflate = R.layout.navdrawer_separator;
        } else if (itemId == NAVDRAWER_ITEM_COVER) {
            layoutToInflate = R.layout.navdrawer_cover;
        } else {
            layoutToInflate = R.layout.navdrawer_item;
        }
        View view = getLayoutInflater().inflate(layoutToInflate, container, false);

        if (isSeparator(itemId)) {
            // we are done
            Utility.setAccessibilityIgnore(view);
            return view;
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        int iconId = itemId >= 0 && itemId < NAVDRAWER_ICON_RES_ID.length ?
                NAVDRAWER_ICON_RES_ID[itemId] : 0;
        int titleId = itemId >= 0 && itemId < NAVDRAWER_TITLE_RES_ID.length ?
                NAVDRAWER_TITLE_RES_ID[itemId] : 0;

        // set icon and text
        iconView.setVisibility(iconId > 0 ? View.VISIBLE : View.GONE);
        if (iconId > 0) {
            iconView.setImageResource(iconId);
        }
        titleView.setText(getString(titleId));

        formatNavDrawerItem(view, itemId, selected);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavDrawerItemClicked(itemId);
            }
        });

        return view;
    }

    private boolean isSeparator(int itemId) {
        return itemId == NAVDRAWER_ITEM_SEPARATOR || itemId == NAVDRAWER_ITEM_COVER;
    }

    private void formatNavDrawerItem(View view, int itemId, boolean selected) {
        if (isSeparator(itemId)) {
            // not applicable
            return;
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);

        // configure its appearance according to whether or not it's selected
        titleView.setTextColor(selected ?
                getResources().getColor(R.color.navdrawer_text_color_selected) :
                getResources().getColor(R.color.navdrawer_text_color));
        iconView.setColorFilter(selected ?
                getResources().getColor(R.color.navdrawer_icon_tint_selected) :
                getResources().getColor(R.color.navdrawer_icon_tint));
    }
        
    private void onNavDrawerItemClicked(final int itemId) {
        if (itemId == getSelfNavDrawerItem()) {
            mDrawerLayout.closeDrawer(Gravity.START);
            return;
        }

//        mHandler.postDelayed(new Runnable() {
//          @Override
//          public void run() {
//              goToNavDrawerItem(itemId);
//          }
//      }, NAVDRAWER_LAUNCH_DELAY);

        	
        goToNavDrawerItem(itemId);
        setSelectedNavDrawerItem(itemId);
      // fade out the main content
//      View mainContent = findViewById(R.id.content_frame);
//      if (mainContent != null) {
//          mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
//      }
        
//        if (isSpecialItem(itemId)) {
//            goToNavDrawerItem(itemId);
//        }
//      else {
//            // launch the target Activity after a short delay, to allow the close animation to play
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    goToNavDrawerItem(itemId);
//                }
//            }, NAVDRAWER_LAUNCH_DELAY);
//
//            // change the active item on the list so the user can see the item changed
//            setSelectedNavDrawerItem(itemId);
//            // fade out the main content
//            View mainContent = findViewById(R.id.main_content);
//            if (mainContent != null) {
//                mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
//            }
//        }

        mDrawerLayout.closeDrawer(Gravity.START);
    }
    
    private void goToNavDrawerItem(int item) {
    	
    	Fragment fragment = null;
        
        switch (item) {
		case NAVDRAWER_ITEM_HOMEPAGE:
			fragment = (Fragment) new Risuscito();
			break;
		case NAVDRAWER_ITEM_SEARCH:
			fragment = (Fragment) new GeneralSearch();
			break;
		case NAVDRAWER_ITEM_INDEXES:
			fragment = (Fragment) new GeneralIndex();
            break;
		case NAVDRAWER_ITEM_LISTS:
        	fragment = (Fragment) new CustomLists();
        	break;
		case NAVDRAWER_ITEM_FAVORITES:
        	fragment = (Fragment) new FavouritesActivity();
        	break;
//		case NAVDRAWER_ITEM_SETTINGS:
//        	fragment = (Fragment) new Settings();
//        	break;
		case NAVDRAWER_ITEM_ABOUT:
        	fragment = (Fragment) new AboutActivity();
        	break;
		case NAVDRAWER_ITEM_DONATE:
        	fragment = (Fragment) new DonateActivity();
        	break;
		default:
        	fragment = (Fragment) new Risuscito();
        	break;
    	}
        
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        transaction.replace(R.id.content_frame, fragment, String.valueOf(item)).commit();
    }
    
    /**
     * Sets up the given navdrawer item's appearance to the selected state. Note: this could
     * also be accomplished (perhaps more cleanly) with state-based layouts.
     */
    private void setSelectedNavDrawerItem(int itemId) {
        if (mNavDrawerItemViews != null) {
            for (int i = 0; i < mNavDrawerItemViews.length; i++) {
                if (i < mNavDrawerItems.size()) {
                    int thisItemId = mNavDrawerItems.get(i);
                    formatNavDrawerItem(mNavDrawerItemViews[i], thisItemId, itemId == thisItemId);
                }
            }
        }
    }
    
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        // Sync the toggle state after onRestoreInstanceState has occurred.
////        drawerToggle.syncState();
//    }
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
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	Risuscito myFragment = (Risuscito)getSupportFragmentManager().findFragmentByTag(String.valueOf(NAVDRAWER_ITEM_HOMEPAGE));
        	if (myFragment != null && myFragment.isVisible()) {
        		finish();
        	}
        	else {        		        		
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                transaction.replace(R.id.content_frame, new Risuscito(), String.valueOf(NAVDRAWER_ITEM_HOMEPAGE)).commit();
                
                setSelectedNavDrawerItem(NAVDRAWER_ITEM_HOMEPAGE);
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
			findViewById(R.id.content_frame).setKeepScreenOn(true);
		else
			findViewById(R.id.content_frame).setKeepScreenOn(true);
    }
    
}
