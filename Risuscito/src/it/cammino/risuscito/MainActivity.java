package it.cammino.risuscito;

import org.holoeverywhere.FontLoader;
import org.holoeverywhere.addon.AddonSlider;
import org.holoeverywhere.addon.Addons;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.slider.SliderItem;
import org.holoeverywhere.slider.SliderMenu;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;

@Addons(AddonSlider.class)
public class MainActivity extends Activity {
    public AddonSlider.AddonSliderA addonSlider() {
        return addon(AddonSlider.class);
    }
		
    private SliderMenu sliderMenu;
    private DatabaseCanti listaCanti;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Utility.updateThemeWithSlider(MainActivity.this);
        FontLoader.setDefaultFont(FontLoader.ROBOTO_CONDENSED);
        super.onCreate(savedInstanceState);      
        
        getSupportActionBar().setLogo(R.drawable.transparent);
        
		//crea un istanza dell'oggetto DatabaseCanti
		listaCanti = new DatabaseCanti(this);
        
        sliderMenu = addonSlider().obtainDefaultSliderMenu(R.layout.main_menu);

        sliderMenu.add(R.string.activity_homepage,
        		Risuscito.class, SliderMenu.BLUE).setIconAttr(R.attr.customHome);
        sliderMenu.add(R.string.title_activity_search,
        		GeneralSearch.class, SliderMenu.BLUE).setIconAttr(R.attr.customSearch);
        sliderMenu.add(R.string.title_activity_general_index,
        		GeneralIndex.class, SliderMenu.BLUE).setIconAttr(R.attr.customIndexes);
        sliderMenu.add(R.string.title_activity_custom_lists,
        		CustomLists.class, SliderMenu.BLUE).setIconAttr(R.attr.customLists);
        sliderMenu.add(getString(R.string.title_activity_favourites) + " (" + getFavoritesCount() + ")",
        		FavouritesActivity.class, SliderMenu.BLUE).setIconAttr(R.attr.customFavorite);
        sliderMenu.add(R.string.title_activity_settings,
        		Settings.class, SliderMenu.BLUE).setIconAttr(R.attr.customSettings);
        sliderMenu.add(R.string.title_activity_about,
        		AboutActivity.class, SliderMenu.BLUE).setIconAttr(R.attr.customChangelog);
        sliderMenu.add(R.string.title_activity_donate,
        		DonateActivity.class, SliderMenu.BLUE).setIconAttr(R.attr.customThanks);
                
        checkScreenAwake();
                
    }
    
    @Override
    public void onResume() {
    	checkScreenAwake();
    	sliderMenu.remove(4);
    	SliderItem favoritesItem = new SliderItem()
        	.setLabel(getString(R.string.title_activity_favourites) + " (" + getFavoritesCount() + ")")
        .setFragmentClass(FavouritesActivity.class);
        sliderMenu.add(favoritesItem, 4).setIconAttr(R.attr.customFavorite).fillColors(SliderMenu.BLUE);
    	 
    	super.onResume();
    }    
    
	@Override
	public void onDestroy() {
		listaCanti.close();
		super.onDestroy();
	}
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (sliderMenu.getCurrentPage() == 0) {
				finish();
				return true;
			}
			else {
				sliderMenu.setCurrentPage(0);
				return true;
			}
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
    
    private int getFavoritesCount() {
    	
    	// crea un manipolatore per il Database in modalità READ
		SQLiteDatabase db = listaCanti.getReadableDatabase();
		
		// lancia la ricerca dei preferiti
		String query = "SELECT count(*)" +
				"		FROM ELENCO" +
				"		WHERE favourite = 1";
		Cursor curs = db.rawQuery(query, null);
		
		curs.moveToFirst();
		
		//recupera il numero di record trovati
		int total = curs.getInt(0);
		
		// chiude il cursore
		curs.close();
		
		db.close();
		
		return total;
    }
    
}
