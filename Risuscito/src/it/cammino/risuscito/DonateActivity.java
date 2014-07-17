package it.cammino.risuscito;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Button;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class DonateActivity extends Fragment {

	private final int TEXTZOOM = 90;
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		getSupportActionBar().setTitle(R.string.title_activity_donate);
		View rootView = inflater.inflate(R.layout.activity_donate, container, false);
		
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		setContentView(R.layout.activity_donate);
		
		WebView donateView = (WebView) rootView.findViewById(R.id.donate_text);
		donateView.setBackgroundColor(0);
		String text = "";
		
        if (Utility.getChoosedTheme(getActivity()) == 2) {
        	text = "<html><head>"
		          + "<style type=\"text/css\">body{color: #FFFFFF;}"
		          + "</style></head>"
		          + "<body>"                          
		          + getString(R.string.donate_long_text)
		          + "</body></html>";
        }
        else {
        	text = "<html><head>"
  		          + "<style type=\"text/css\">body{color: #000000;}"
  		          + "</style></head>"
  		          + "<body>"                          
  		          + getString(R.string.donate_long_text)
  		          + "</body></html>";
        }
		
		donateView.loadData(text, "text/html; charset=utf-8", "UTF-8");

		WebSettings wSettings = donateView.getSettings();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			wSettings.setTextZoom(TEXTZOOM);
		else
			wSettings.setTextSize(WebSettings.TextSize.SMALLER);
		
		((Button) rootView.findViewById(R.id.donateButton)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				String url = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=" 
//						+ "marbat87%40outlook%2eit&lc=IT&item_name=Donazione%20Risuscit%c3%b2%20Android"
//						+ "&item_number=THX_RISUSCITO_APP&no_note=0&cn="
//						+ "Aggiungi%20istruzioni%20speciali%20per%20il%20venditore%3a&no_shipping=1&"
//						+ "currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted";
				String url = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=ENA7HP2LQKQ3G";

				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(browserIntent);
			}
		});
		
//		checkScreenAwake();
		
		return rootView;
	}

//    @Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			finish();
//            return true;	
//		}
//		return false;
//	}
	
//    @Override
//    public void onResume() {
//    	super.onResume();
//    }
    	
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//	}

//    //controlla se l'app deve mantenere lo schermo acceso
//    private void checkScreenAwake() {
//    	
//    	SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this);
//		boolean screenOn = pref.getBoolean("screenOn", false);
//		View about = (View) findViewById(R.id.donateButton);
//		if (screenOn)
//			about.setKeepScreenOn(true);
//		else
//			about.setKeepScreenOn(false);
//		
//    }
	    
}
