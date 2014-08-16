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
    
    public static final int[] BLUEGREY = new int[]{
        R.color.theme_bluegrey_color, R.color.BlueGreyDark
    };
    
    public static final int[] BROWN = new int[]{
        R.color.theme_brown_color, R.color.BrownDark
    };
    
    public static final int[] INDIGO = new int[]{
        R.color.theme_indigo_color, R.color.IndigoDark
    };
    
    public static final int[] RED = new int[]{
        R.color.theme_red_color, R.color.RedThemeDark
    };
    
    public static final int[] DEEPPURPLE = new int[]{
        R.color.theme_deeppurple_color, R.color.DeepPurpleDark
    };
    
    public static final int[] TEAL = new int[]{
        R.color.theme_teal_color, R.color.TealDark
    };
    
    public static int[] themeColor;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Utility.updateThemeWithSlider(MainActivity.this);
        FontLoader.setDefaultFont(FontLoader.ROBOTO_CONDENSED);
        super.onCreate(savedInstanceState);      
        
        getSupportActionBar().setLogo(R.drawable.transparent);
        
		//crea un istanza dell'oggetto DatabaseCanti
		listaCanti = new DatabaseCanti(this);
        
        sliderMenu = addonSlider().obtainDefaultSliderMenu(R.layout.main_menu);
        
//        addonSlider().setOverlayActionBar(false);

        sliderMenu.setInverseTextColorWhenSelected(false);
                
        switch (Utility.getChoosedTheme(MainActivity.this)) {
        	case 0:
        	case 1:
        		themeColor = INDIGO;
        		break;
        	case 2:
        	case 3:
        		themeColor = BLUEGREY;
        		break;
        	case 4:
        	case 5:
        		themeColor = RED;
        		break;
        	case 6:
        	case 7:
        		themeColor = DEEPPURPLE;
        		break;
        	case 8:
        	case 9:
        		themeColor = TEAL;
        		break;
        	case 10:
        	case 11:
        		themeColor = BROWN;
        		break;
        	default:
        		themeColor = INDIGO;
        		break;
        }  
        
        sliderMenu.add(R.string.activity_homepage,
        		Risuscito.class, themeColor).setIconAttr(R.attr.customHome)
        		.setTextAppereance(R.style.MenuDrawerFontWhite);
        
        sliderMenu.add(R.string.title_activity_search,
        		GeneralSearch.class, themeColor).setIconAttr(R.attr.customSearch)
        		.setTextAppereance(R.style.MenuDrawerFontWhite);
        
        sliderMenu.add(R.string.title_activity_general_index,
        		GeneralIndex.class, themeColor).setIconAttr(R.attr.customIndexes)
        		.setTextAppereance(R.style.MenuDrawerFontWhite);
        
        sliderMenu.add(R.string.title_activity_custom_lists,
        		CustomLists.class, themeColor).setIconAttr(R.attr.customLists)
        		.setTextAppereance(R.style.MenuDrawerFontWhite);
        
        sliderMenu.add(getString(R.string.title_activity_favourites) + " (" + getFavoritesCount() + ")",
        		FavouritesActivity.class, themeColor).setIconAttr(R.attr.customFavorite)
        		.setTextAppereance(R.style.MenuDrawerFontWhite);
        
        sliderMenu.add(R.string.title_activity_settings,
        		Settings.class, themeColor).setIconAttr(R.attr.customSettings)
        		.setTextAppereance(R.style.MenuDrawerFontWhite);
        
        sliderMenu.add(R.string.title_activity_about,
        		AboutActivity.class, themeColor).setIconAttr(R.attr.customChangelog)
        		.setTextAppereance(R.style.MenuDrawerFontWhite);
        
        sliderMenu.add(R.string.title_activity_donate,
        		DonateActivity.class, themeColor).setIconAttr(R.attr.customThanks)
        		.setTextAppereance(R.style.MenuDrawerFontWhite);
                
        checkScreenAwake();
                
    }
    
    @Override
    public void onResume() {
    	checkScreenAwake();
    	sliderMenu.remove(4);
    	SliderItem favoritesItem = new SliderItem()
    		.setLabel(getString(R.string.title_activity_favourites) + " (" + getFavoritesCount() + ")")
    		.setFragmentClass(FavouritesActivity.class)
    		.setTextAppereance(R.style.MenuDrawerFontWhite);
        sliderMenu.add(favoritesItem, 4).setIconAttr(R.attr.customFavorite).fillColors(themeColor);
    	 
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
