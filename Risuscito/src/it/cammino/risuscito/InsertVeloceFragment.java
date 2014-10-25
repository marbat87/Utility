package it.cammino.risuscito;

import org.holoeverywhere.FontLoader;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.indris.material.RippleView;

public class InsertVeloceFragment extends Fragment {

	private DatabaseCanti listaCanti;
	private String[] titoli;
	private EditText searchPar;
	private View rootView;
	ListView lv;
	
	private int fromAdd;
	private int idLista;
	private int listPosition;
		
	//constructor
	public InsertVeloceFragment() {}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(
				R.layout.activity_ricerca_titolo, container, false);
				
		searchPar = (EditText) rootView.findViewById(R.id.textfieldRicerca);
		listaCanti = new DatabaseCanti(getActivity());
				
		lv = (ListView) rootView.findViewById(R.id.matchedList);
		 
		Bundle bundle = getArguments(); 
		fromAdd = bundle.getInt("fromAdd");
        idLista = bundle.getInt("idLista");
        listPosition = bundle.getInt("position");
		
		searchPar.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				if (s.length() >= 3) {
					
					rootView.findViewById(R.id.search_no_results).setVisibility(View.GONE);
					
					String titolo = Utility.duplicaApostrofi(s.toString());
					
					// crea un manipolatore per il Database in modalit� READ
		    		SQLiteDatabase db = listaCanti.getReadableDatabase();
		    		
		    		
		    		// lancia la ricerca di tutti i titoli presenti in DB e li dispone in ordine alfabetico
		    		String query = "SELECT titolo, color, pagina" +
		    				"		FROM ELENCO" +
		    				"		WHERE titolo like '%" + titolo + "%'" +
		    				"		ORDER BY titolo ASC";
		    		Cursor lista = db.rawQuery(query, null);
		    		
		    		//recupera il numero di record trovati
		    		int total = lista.getCount();
		    		
		    		// crea un array e ci memorizza i titoli estratti
		    		titoli = new String[lista.getCount()];		                      
		    		lista.moveToFirst();    		    		
		    		for (int i = 0; i < total; i++) {
		    			titoli[i] = Utility.intToString(lista.getInt(2), 3) + lista.getString(1) + lista.getString(0);
		    			lista.moveToNext();
		    		}
		    		
		    		// chiude il cursore
		    		lista.close();
		    		
		    		// crea un list adapter per l'oggetto di tipo ListView
		    		lv.setAdapter(new SongRowAdapter());

		    		
		    		// setta l'azione al click su ogni voce dell'elenco
		    		lv.setOnItemClickListener(new OnItemClickListener() {
		    			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    			    
		    				// recupera il titolo della voce cliccata
		    				String cantoCliccato = ((TextView) view.findViewById(R.id.text_title))
									.getText().toString();
		    				String cantoCliccatoNoApex = Utility.duplicaApostrofi(cantoCliccato);		    			    
			    					    		        
		    		        SQLiteDatabase db = listaCanti.getReadableDatabase();
		    		        
		    		        if (fromAdd == 1)  {
		    		        	// chiamato da una lista predefinita
				    			String query = "SELECT _id" +
				    						"  FROM ELENCO" +
				    						"  WHERE titolo =  '" + cantoCliccatoNoApex + "'";   
				    			Cursor cursor = db.rawQuery(query, null);
				    			      
				    			// recupera il nome del file
				    			cursor.moveToFirst();
				    			int idCanto = cursor.getInt(0);
				    			    
				    			// chiude il cursore
				    			cursor.close();
			    		        	
				    			String sql = "INSERT INTO CUST_LISTS ";
			    				sql+= "VALUES (" + idLista + ", " 
	   								 + listPosition + ", "
	   								 + idCanto
	   								 + ", CURRENT_TIMESTAMP)";
			    				
			    				try {
			    					db.execSQL(sql);
			    				} catch (SQLException e) {
			    					Toast toast = Toast.makeText(getActivity()
			    							, getString(R.string.present_yet), Toast.LENGTH_SHORT);
			    					toast.show();
			    				}
		    		        }
			    			else {
			    				//chiamato da una lista personalizzata
				    			String query = "SELECT lista" +
				    						"  FROM LISTE_PERS" +
				    						"  WHERE _id =  " + idLista;   
				    			Cursor cursor = db.rawQuery(query, null);
				    			      
				    			// recupera l'oggetto lista personalizzata
				    			cursor.moveToFirst();
				    							    			
				    			ListaPersonalizzata listaPersonalizzata = (ListaPersonalizzata) ListaPersonalizzata.
				    					deserializeObject(cursor.getBlob(0));
				    			    
				    			// chiude il cursore
				    			cursor.close();
			    		        
					    		// lancia la ricerca di tutti i titoli presenti in DB e li dispone in ordine alfabetico
					    		query = "SELECT color, pagina" +
					    				"		FROM ELENCO" +
					    				"		WHERE titolo = '" + cantoCliccatoNoApex + "'";
					    		cursor = db.rawQuery(query, null);
				    			
					    		cursor.moveToFirst();
					    							    		
				    			listaPersonalizzata.addCanto(Utility.intToString(cursor.getInt(1), 3) + cursor.getString(0) + cantoCliccato, listPosition);
				    						    				
			    		    	ContentValues  values = new  ContentValues( );
			    		    	values.put("lista" , ListaPersonalizzata.serializeObject(listaPersonalizzata));
			    		    	db.update("LISTE_PERS", values, "_id = " + idLista, null );	
			    		    	db.close();
			    			}
			    			    
			    			getActivity().finish();
			    			getActivity().overridePendingTransition(0, R.anim.slide_out_right);
		    			          			      
		    			}
		    		});
		    				    		
		    		if (total == 0)
		    			rootView.findViewById(R.id.search_no_results).setVisibility(View.VISIBLE);    		
				}
				else {
					if (s.length() == 0) {
						lv.setAdapter(null);
						rootView.findViewById(R.id.search_no_results).setVisibility(View.GONE);
					}
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
			
		});
		
		RippleView pulisci = (RippleView) rootView.findViewById(R.id.pulisci_ripple);
		pulisci.setTypeface(FontLoader.ROBOTO_MEDIUM.getTypeface(getActivity()));
//		Button pulisci = (Button) rootView.findViewById(R.id.button_pulisci);
		pulisci.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchPar.setText("");
				rootView.findViewById(R.id.search_no_results).setVisibility(View.GONE);
			}
		});
		
		setHasOptionsMenu(true);
		
		return rootView;
	}
	
    @Override
    public void onResume() {
    	super.onResume();
    }
    
	@Override
	public void onDestroy() {
		if (listaCanti != null)
			listaCanti.close();
		super.onDestroy();
	}
	
    private class SongRowAdapter extends ArrayAdapter<String> {
    	
    	SongRowAdapter() {
    		super(getActivity(), R.layout.row_item, R.id.text_title, titoli);
    	}
    	
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		
    		View row=super.getView(position, convertView, parent);
    		TextView canto = (TextView) row.findViewById(R.id.text_title);
    		
    		String totalString = canto.getText().toString();
    		
    		int tempPagina = Integer.valueOf(totalString.substring(0,3));
    		String pagina = String.valueOf(tempPagina);
    		String colore = totalString.substring(3, 10);
    		    		
    		((TextView) row.findViewById(R.id.text_title))
			.setText(totalString.substring(10));
    		
    		TextView textPage = (TextView) row.findViewById(R.id.text_page);
    		textPage.setText(pagina);
    		View fullRow = (View) row.findViewById(R.id.full_row);
    		fullRow.setBackgroundColor(Color.parseColor(colore));
    		
    		return(row);
    	}
    }

}
