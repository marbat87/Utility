package it.cammino.risuscito;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;

import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

public class Utility {
	
    //metodo che restituisce la stringa di input senza la pagina all'inizio
    public static String truncatePage(String input) {
    	
    	int length = input.length();
    	int start;
    	
    	for (start = 0; start < length; start++) {
    		
    		if (input.charAt(start) == ')') {
    			start += 2;
    			break;
    		}
    	}
    	
    	return input.substring(start);
    }
    
    //metodo che duplica tutti gli apici presenti nella stringa
    public static String duplicaApostrofi(String input) {
    	
    	String result = input;
    	int massimo  = result.length() - 1;
    	char apice = '\'';
    	
    	for (int i = 0; i <= massimo; i++) {
    		if (result.charAt(i) == apice ) {
    			result = result.substring(0, i+1) + apice + result.substring(i+1);
    			massimo++;
    			i++;
    		}
    	}
    	
    	return result;
    }
    
    public static String intToString(int num, int digits) {
//        assert digits > 0 : "Invalid number of digits";
        if (BuildConfig.DEBUG && !(digits > 0))
        		throw new AssertionError("Campo digits non valido");

        // create variable length array of zeros
        char[] zeros = new char[digits];
        Arrays.fill(zeros, '0');
        // format number as String
        DecimalFormat df = new DecimalFormat(String.valueOf(zeros));

        return df.format(num);
    }
    
	public static boolean isOnline(Activity activity) {
	    ConnectivityManager cm =
	        (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	/* Filtra il link di input per tenere solo il nome del file */
	public static String filterMediaLink(String link) {
	    if (link.length() == 0)
	    	return link;
	    else {
	    	int start = link.indexOf(".com/");
	    	return link.substring(start + 5);
	    }
	}
	
	public static String retrieveMediaFileLink(Context activity, String link) {
		
		if (isExternalStorageReadable()) {
			File[] fileArray = ContextCompat.getExternalFilesDirs(activity, null);
			File fileExt = new File(fileArray[0], filterMediaLink(link));
			if (fileExt.exists()) {
//				Log.i("FILE esterno:", fileExt.getAbsolutePath());
				return fileExt.getAbsolutePath();
			}
//			Log.i("FILE esterno:", "NON TROVATO");
		}
		
		File fileInt = new File(activity.getFilesDir(), filterMediaLink(link));
		if (fileInt.exists()) {
//			Log.i("FILE interno:", fileInt.getAbsolutePath());
			return fileInt.getAbsolutePath();
		}
//		Log.i("FILE INTERNO:", "NON TROVATO");
		return "";
	}
	
	public static void updateTheme(Activity activity) {
	       SharedPreferences sp = PreferenceManager
	                .getDefaultSharedPreferences(activity);
//	       Log.i("THEME SET", sp.getString("applicationTheme", "0") + " ");
	       if (!sp.getString("applicationTheme", "0").equals("")) {
		       switch (Integer.valueOf(sp.getString("applicationTheme", "0"))) {
		        	case 0:
		        		activity.setTheme(R.style.AppThemeMixed);
		        		break;
		        	case 1:
		        		activity.setTheme(R.style.AppThemeLight);
		        		break;
		        	case 2:
		        		activity.setTheme(R.style.AppThemeDark);
		        		break;
		        	default:
		        		break;
		        }
			}
	}
	
	public static void updateThemeWithSlider(Activity activity) {
	       SharedPreferences sp = PreferenceManager
	                .getDefaultSharedPreferences(activity);
//	       Log.i("THEME SET WITH SLIDER", sp.getString("applicationTheme", "0") + " ");
	       if (!sp.getString("applicationTheme", "0").equals("")) {
		       switch (Integer.valueOf(sp.getString("applicationTheme", "0"))) {
		        	case 0:
		        		activity.setTheme(R.style.AppThemeSliderMixed);
		        		break;
		        	case 1:
		        		activity.setTheme(R.style.AppThemeSliderLight);
		        		break;
		        	case 2:
		        		activity.setTheme(R.style.AppThemeSliderDark);
		        		break;
		        	default:
		        		break;
		        }
			}
	}
	
	public static int getChoosedTheme(Activity activity) {
	       SharedPreferences sp = PreferenceManager
	                .getDefaultSharedPreferences(activity);
//	       Log.i("THEME CHOOSED", sp.getString("applicationTheme", "0") + " ");
	       if (!sp.getString("applicationTheme", "-1").equals(""))
	    	   return Integer.valueOf(sp.getString("applicationTheme", "0"));
	       else
	    	   return 0;
	       
	}
	
}
