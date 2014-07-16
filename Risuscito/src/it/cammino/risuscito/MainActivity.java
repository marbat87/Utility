package it.cammino.risuscito;

import org.holoeverywhere.addon.AddonSlider;
import org.holoeverywhere.addon.Addons;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.slider.SliderMenu;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

@Addons(AddonSlider.class)
public class MainActivity extends Activity {
    public AddonSlider.AddonSliderA addonSlider() {
        return addon(AddonSlider.class);
    }
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Utility.updateThemeWithSlider(MainActivity.this);
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_risuscito);
        
        final SliderMenu sliderMenu = addonSlider().obtainDefaultSliderMenu(R.layout.main_menu);
//        addonSlider().setOverlayActionBar(false);
        sliderMenu.add(R.string.activity_homepage,
        		Risuscito.class, SliderMenu.BLUE).setIconAttr(R.attr.customHome);
        sliderMenu.add(R.string.title_activity_search,
        		GeneralSearch.class, SliderMenu.BLUE).setIconAttr(R.attr.customSearch);
        sliderMenu.add(R.string.title_activity_general_index,
        		GeneralIndex.class, SliderMenu.BLUE).setIconAttr(R.attr.customIndexes);
        sliderMenu.add(R.string.title_activity_custom_lists,
        		CustomLists.class, SliderMenu.BLUE).setIconAttr(R.attr.customLists);
        sliderMenu.add(R.string.title_activity_favourites,
        		FavouritesActivity.class, SliderMenu.BLUE).setIconAttr(R.attr.customFavorite);
        sliderMenu.add(R.string.title_activity_settings,
        		Settings.class, SliderMenu.BLUE).setIconAttr(R.attr.customSettings);
        sliderMenu.add(R.string.title_activity_about,
        		AboutActivity.class, SliderMenu.BLUE).setIconAttr(R.attr.customChangelog);
        sliderMenu.add(R.string.title_activity_donate,
        		DonateActivity.class, SliderMenu.BLUE).setIconAttr(R.attr.customThanks);
        
        checkScreenAwake();
        
        // We are should provide activity to ThemePicker
//        ((DemoThemePicker) findViewById(R.id.themePicker)).setActivity(this);
        
    }
    
    @Override
    public void onResume() {
    	checkScreenAwake();
    	super.onResume();
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
}
