package it.cammino.risuscito;

import it.cammino.risuscito.GenericDialogFragment.GenericDialogListener;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.TextView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class FavouritesActivity extends Fragment implements GenericDialogListener {

  	private DatabaseCanti listaCanti;
  	private String[] titoli;
  	private String cantoDaCanc;
  	private SongRowAdapter listAdapter;
  	private ListView lv;
  	private int prevOrientation;
  	private View rootView;
	  	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		getSupportActionBar().setTitle(R.string.title_activity_favourites);
		rootView = inflater.inflate(R.layout.activity_favourites, container, false);
		
//		final ActionBar actionBar = getSupportActionBar();
//		actionBar.setDisplayHomeAsUpEnabled(true);
		
		//crea un istanza dell'oggetto DatabaseCanti
		listaCanti = new DatabaseCanti(getActivity());
		    		    		
//		updateFavouritesList();
		
//		checkScreenAwake();
		
		return rootView;
	}

    @Override
    public void onResume() {
    	updateFavouritesList();
//    	checkScreenAwake();
    	super.onResume();
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
    
	@Override
	public void onDestroy() {
		if (listaCanti != null)
			listaCanti.close();
		super.onDestroy();
	}

//    //controlla se l'app deve mantenere lo schermo acceso
//    private void checkScreenAwake() {
//    	
//    	SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this);
//		boolean screenOn = pref.getBoolean("screenOn", false);
//		lv = (ListView) findViewById(R.id.favouritesList);
//		if (screenOn)
//			lv.setKeepScreenOn(true);
//		else
//			lv.setKeepScreenOn(false);
//		
//    }
	
    private void startSubActivity(Bundle bundle) {
    	Intent intent = new Intent(getActivity(), PaginaRenderActivity.class);
    	intent.putExtras(bundle);
    	startActivity(intent);
    	getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold_on);
   	}
	
    private void updateFavouritesList() {
    	
    	// crea un manipolatore per il Database in modalit� READ
		SQLiteDatabase db = listaCanti.getReadableDatabase();
		
		// lancia la ricerca dei preferiti
		String query = "SELECT titolo, color" +
				"		FROM ELENCO" +
				"		WHERE favourite = 1" +
				"		ORDER BY TITOLO ASC";
		Cursor lista = db.rawQuery(query, null);
		
		//recupera il numero di record trovati
		int total = lista.getCount();
		
		//nel caso sia presente almeno un preferito, viene nascosto il testo di nessun canto presente
		TextView noResults = (TextView) rootView.findViewById(R.id.no_favourites);
		TextView hintRemove = (TextView) rootView.findViewById(R.id.hint_remove);
		if (total > 0) {
			noResults.setVisibility(View.GONE);
			hintRemove.setVisibility(View.VISIBLE);
		}
		else	{
			noResults.setVisibility(View.VISIBLE);
			hintRemove.setVisibility(View.GONE);
		}
		
		// crea un array e ci memorizza i titoli estratti
		titoli = new String[lista.getCount()];		                      
		lista.moveToFirst();    		    		
		for (int i = 0; i < total; i++) {
			
			titoli[i] = lista.getString(1) + lista.getString(0);
			lista.moveToNext();
		}
		
		// chiude il cursore
		lista.close();

		// crea un oggetto di tipo ListView
		lv = (ListView) rootView.findViewById(R.id.favouritesList);
		listAdapter = new SongRowAdapter();
		lv.setAdapter(listAdapter);
		
		// setta l'azione al click su ogni voce dell'elenco
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    
				// recupera il titolo della voce cliccata
				String cantoCliccato = ((TextView) view.findViewById(R.id.text_title))
						.getText().toString();
				cantoCliccato = Utility.duplicaApostrofi(cantoCliccato);
			    
				// crea un manipolatore per il DB in modalit� READ
				SQLiteDatabase db = listaCanti.getReadableDatabase();
			    
				// esegue la query per il recupero del nome del file della pagina da visualizzare
			    String query = "SELECT source, _id" +
			      		"  FROM ELENCO" +
			      		"  WHERE titolo =  '" + cantoCliccato + "'";   
			    Cursor cursor = db.rawQuery(query, null);
			      
			    // recupera il nome del file
			    cursor.moveToFirst();
			    String pagina = cursor.getString(0);
			    int idCanto = cursor.getInt(1);
			    
			    // chiude il cursore
			    cursor.close();
			    db.close();
			        			    
			    // crea un bundle e ci mette il parametro "pagina", contente il nome del file della pagina da visualizzare  
			    Bundle bundle = new Bundle();
			    bundle.putString("pagina", pagina);
			    bundle.putInt("idCanto", idCanto);
			    
			    // lancia l'activity che visualizza il canto passando il parametro creato
			    startSubActivity(bundle);
			          			      
			}
		});
		
		// setta l'azione al click prolungato  su ogni voce dell'elenco
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		        cantoDaCanc = ((TextView) view.findViewById(R.id.text_title)).getText().toString();
				cantoDaCanc = Utility.duplicaApostrofi(cantoDaCanc);
				blockOrientation();
				GenericDialogFragment dialog = new GenericDialogFragment();
				dialog.setCustomMessage(getString(R.string.favorite_remove));
				dialog.setListener(FavouritesActivity.this);
				dialog.setOnKeyListener(new Dialog.OnKeyListener() {

		            @Override
		            public boolean onKey(DialogInterface arg0, int keyCode,
		                    KeyEvent event) {
		                if (keyCode == KeyEvent.KEYCODE_BACK
		                		&& event.getAction() == KeyEvent.ACTION_UP) {
		                    arg0.dismiss();
							getActivity().setRequestedOrientation(prevOrientation);
							return true;
		                }
		                return false;
		            }
		        });
                dialog.show(getChildFragmentManager());
				return true;
			}
		});	
		
//		registerForContextMenu(lv);
    }
    
    private class SongRowAdapter extends ArrayAdapter<String> {
    	
    	SongRowAdapter() {
    		super(getActivity(), R.layout.row_item, R.id.text_title, titoli);
    	}
    	
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		
    		View row=super.getView(position, convertView, parent);
    		
    		TextView canto = (TextView) row.findViewById(R.id.text_title);
    		String cantoCliccato = canto.getText().toString();
    		String colore = cantoCliccato.substring(0, 7);
    		
    		((TextView) row.findViewById(R.id.text_title))
    			.setText(cantoCliccato.substring(7));
		        		
    		TextView textPage = (TextView) row.findViewById(R.id.text_page);
    		textPage.setVisibility(View.GONE);
    		View fullRow = (View) row.findViewById(R.id.full_row);
    		fullRow.setBackgroundColor(Color.parseColor(colore));
    		
    		return(row);
    	}
    }
    
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {  
		SQLiteDatabase db = listaCanti.getReadableDatabase();
		String sql = "UPDATE ELENCO" +
				"  SET favourite = 0" + 
				"  WHERE titolo =  '" + cantoDaCanc + "'";
		db.execSQL(sql);
		db.close();
		updateFavouritesList();
		dialog.dismiss();
		getActivity().setRequestedOrientation(prevOrientation);
		
		//permette di aggiornare il numero dei preferiti nel menu laterale
//		((MainActivity) getActivity()).onResume();
		
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
        getActivity().setRequestedOrientation(prevOrientation);
    }
    
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v,
//                                    ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
//        cantoDaCanc = ((TextView) info.targetView.findViewById(R.id.text_title)).getText().toString();
//		cantoDaCanc = Utility.duplicaApostrofi(cantoDaCanc);
//        MenuInflater inflater = this.getMenuInflater();
//        inflater.inflate(R.menu.favorites_menu, menu);
//    }
//    
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//    	switch (item.getItemId()) {
//	    	case R.id.remove_favorite:
//        		SQLiteDatabase db = listaCanti.getReadableDatabase();
//        		String sql = "UPDATE ELENCO" +
//        				"  SET favourite = 0" + 
//        				"  WHERE titolo =  '" + cantoDaCanc + "'";
//        		db.execSQL(sql);
//        		db.close();
//        		updateFavouritesList();
//        		return true;
//	    	default:
//	    		return super.onContextItemSelected(item);
//    	}
//    }
    
    public void blockOrientation() {
        prevOrientation = getActivity().getRequestedOrientation();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        	getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
        	getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }
    }
    
}