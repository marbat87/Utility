package it.cammino.risuscito;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.os.Bundle;
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
//    private Handler mHandler;
    
    // list of navdrawer items that were actually added to the navdrawer, in order
    private ArrayList<Integer> mNavDrawerItems = new ArrayList<Integer>();

    // views that correspond to each navdrawer item, null if not yet created
    private View[] mNavDrawerItemViews = null;
    
    protected static final String SELECTED_ITEM = "oggetto_selezionato";
    protected static final String TOOLBAR_TITLE = "titolo_selezionato";
    
    protected int selectedItem;
    
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
//    protected static final int NAVDRAWER_ITEM_COVER = -3;
    
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
        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mActionBarToolbar = (Toolbar) findViewById(R.id.risuscito_toolbar);
        setSupportActionBar(mActionBarToolbar);
        
        // setta il colore della barra di stato, solo da KITAKT in su
//        Utility.setupTransparentTints(MainActivity.this);
        
        setupNavDrawer();
        
        if (findViewById(R.id.content_frame) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
            	setSelectedNavDrawerItem(savedInstanceState.getInt(SELECTED_ITEM));
                return;
            }
            
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Risuscito(), String.valueOf(NAVDRAWER_ITEM_HOMEPAGE)).commit();
//            goToNavDrawerItem(NAVDRAWER_ITEM_HOMEPAGE);
            setSelectedNavDrawerItem(NAVDRAWER_ITEM_HOMEPAGE);
        }

    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	checkScreenAwake();
    }
    
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {		
		savedInstanceState.putInt(SELECTED_ITEM, selectedItem);
		super.onSaveInstanceState(savedInstanceState);
	}
    
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
//        int selfItem = getSelfNavDrawerItem();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }
        mDrawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.theme_primary_dark));


        if (mActionBarToolbar != null) {
            mActionBarToolbar.setNavigationIcon(R.drawable.ic_drawer);
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            });
        }

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        // populate the nav drawer with the correct items
        populateNavDrawer();


    }
    
    /** Populates the navigation drawer with the appropriate items. */
    private void populateNavDrawer() {
//        boolean attendeeAtVenue = PrefUtils.isAttendeeAtVenue(this);
        mNavDrawerItems.clear();

        mNavDrawerItems.add(NAVDRAWER_ITEM_HOMEPAGE);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SEARCH);
        mNavDrawerItems.add(NAVDRAWER_ITEM_INDEXES);
        mNavDrawerItems.add(NAVDRAWER_ITEM_LISTS);
        mNavDrawerItems.add(NAVDRAWER_ITEM_FAVORITES);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SETTINGS);
        mNavDrawerItems.add(NAVDRAWER_ITEM_ABOUT);
        mNavDrawerItems.add(NAVDRAWER_ITEM_DONATE);
        
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
//        } else if (itemId == NAVDRAWER_ITEM_COVER) {
//            layoutToInflate = R.layout.navdrawer;
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
        return itemId == NAVDRAWER_ITEM_SEPARATOR;
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
        	
        goToNavDrawerItem(itemId);
        setSelectedNavDrawerItem(itemId);

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
		case NAVDRAWER_ITEM_SETTINGS:
        	fragment = (Fragment) new PreferencesFragment();
        	break;
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
        
        //creo il nuovo fragment solo se non � lo stesso che sto gi� visualizzando
    	Fragment myFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(item));
    	if (myFragment == null || !myFragment.isVisible()) {
    		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    		transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
    		transaction.replace(R.id.content_frame, fragment, String.valueOf(item)).commit();
    	}
    }
    
    /**
     * Sets up the given navdrawer item's appearance to the selected state. Note: this could
     * also be accomplished (perhaps more cleanly) with state-based layouts.
     */
    private void setSelectedNavDrawerItem(int itemId) {
    	selectedItem = itemId;
        if (mNavDrawerItemViews != null) {
            for (int i = 0; i < mNavDrawerItemViews.length; i++) {
                if (i < mNavDrawerItems.size()) {
                    int thisItemId = mNavDrawerItems.get(i);
                    formatNavDrawerItem(mNavDrawerItemViews[i], thisItemId, itemId == thisItemId);
                }
            }
        }
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	Fragment myFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(NAVDRAWER_ITEM_HOMEPAGE));
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
			findViewById(R.id.content_frame).setKeepScreenOn(false);
    }
    
}
