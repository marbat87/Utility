package it.cammino.risuscito;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class InsertAvanzataFragment extends Fragment {

	private DatabaseCanti listaCanti;
	private String[] titoli;
	private EditText searchPar;
	private View rootView;
	private static String[][] aTexts;
	ListView lv;
	private int prevOrientation;
	private ProgressDialog mProgressDialog;
	private static Map<Character, Character> MAP_NORM;
	
	private int fromAdd;
	private int idLista;
	private int listPosition;
		
	//constructor
	public InsertAvanzataFragment() {}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(
				R.layout.activity_ricerca_avanzata, container, false);
				
		searchPar = (EditText) rootView.findViewById(R.id.textfieldRicerca);
		listaCanti = new DatabaseCanti(getActivity());
				
		lv = (ListView) rootView.findViewById(R.id.matchedList);
		searchPar.setText("");
		rootView.findViewById(R.id.button_search).setEnabled(false);
		
		Bundle bundle = getArguments(); 
		fromAdd = bundle.getInt("fromAdd");
        idLista = bundle.getInt("idLista");
        listPosition = bundle.getInt("position");
		
		try {
			InputStream in = getActivity().getAssets().open("fileout_new.xml");
        	CantiXmlParser parser = new CantiXmlParser();
            aTexts = parser.parse(in);
            in.close();
        } 	catch (XmlPullParserException e) {
        	e.printStackTrace();
        }
        	catch (IOException e) {
        	e.printStackTrace();
        }
		
		searchPar.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//abilita il pulsante solo se la stringa ha pi� di 3 caratteri, senza contare gli spazi
				if (s.toString().trim().length() >= 3)
					rootView.findViewById(R.id.button_search).setEnabled(true);
				else
					rootView.findViewById(R.id.button_search).setEnabled(false);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
			
		});
		
		Button ricerca = (Button) rootView.findViewById(R.id.button_search);
		ricerca.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
	            mProgressDialog = new ProgressDialog(getActivity());
	            mProgressDialog.setMessage(getString(R.string.search_running));
	            mProgressDialog.setIndeterminate(true);
	            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	            mProgressDialog.setCancelable(true);
	            mProgressDialog.setCanceledOnTouchOutside(false);
	            mProgressDialog.show();
				final SearchTask downloadTask = new SearchTask();
				downloadTask.execute(searchPar.getText().toString());
			}
		});
		
		Button pulisci = (Button) rootView.findViewById(R.id.button_pulisci);
		pulisci.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchPar.setText("");
				rootView.findViewById(R.id.search_no_results).setVisibility(View.GONE);
				lv.setVisibility(View.GONE);
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
    
    private class SearchTask extends AsyncTask<String, Integer, String> {

        @SuppressLint("NewApi")
		@Override
        protected String doInBackground(String... sSearchText) {
			
			// crea un manipolatore per il Database in modalit� READ
    		SQLiteDatabase db = listaCanti.getReadableDatabase();
        	
        	String[] words = sSearchText[0].split("\\W");
        	
			String text = "";
			String[] aResults = new String[300];
			int totalResults = 0;
			
			for (int k = 0; k < aTexts.length; k++) {
				
				if (aTexts[k][0] == null || aTexts[k][0].equalsIgnoreCase(""))
					break;
				
				boolean found = true;
				for (int j = 0; j < words.length; j++) {
					if (words[j].trim().length() > 1) {
						text = words[j].trim();
						text = text.toLowerCase(Locale.getDefault());
						
						if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
						    String nfdNormalizedString = Normalizer.normalize(text, Normalizer.Form.NFD); 
						    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
						    text =  pattern.matcher(nfdNormalizedString).replaceAll("");
						}
						else
							text = removeAccents(text);
						
						if (!aTexts[k][1].contains(text)) {
							found = false;
						}
					}
				}
				
				if (found) {
		    							
					// recupera il titolo colore e pagina del canto da aggiungere alla lista
		    		String query = "SELECT titolo, color, pagina"
		    		 +		"		FROM ELENCO"
		    		 +		"		WHERE source = '" + aTexts[k][0] + "'";
		    		
		    		Cursor lista = db.rawQuery(query, null);
	                
		    		if (lista.getCount() > 0) {
		    			lista.moveToFirst();
		    			aResults[totalResults++] = Utility.intToString(lista.getInt(2), 3) + lista.getString(1) + lista.getString(0);
					}
		    		// chiude il cursore
		    		lista.close();
				}
			}
			
			titoli = new String[totalResults];
			for (int i = 0; i < totalResults; i++)
				titoli[i] = aResults[i];
			
            return null;
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prevOrientation = getActivity().getRequestedOrientation();
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            	getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            	getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
            	getActivity(). setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            }
        }

        @Override
        protected void onPostExecute(String result) {
        	getActivity().setRequestedOrientation(prevOrientation);
        	if (mProgressDialog.isShowing())
        		mProgressDialog.dismiss();
        	
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
    			          			      
    			}
    		});
        	
    		if (titoli.length == 0) {
    			rootView.findViewById(R.id.search_no_results).setVisibility(View.VISIBLE);
    			lv.setVisibility(View.GONE);
    		}
    		else {
    			rootView.findViewById(R.id.search_no_results).setVisibility(View.GONE);
    			lv.setVisibility(View.VISIBLE);
    			registerForContextMenu(lv);
			}
        }
        
    }
    
    public static String removeAccents(String value)
    {
        if (MAP_NORM == null || MAP_NORM.size() == 0)
        {
            MAP_NORM = new HashMap<Character, Character>();
            MAP_NORM.put('�', 'A');
            MAP_NORM.put('�', 'A');
            MAP_NORM.put('�', 'A');
            MAP_NORM.put('�', 'A');
            MAP_NORM.put('�', 'A');
            MAP_NORM.put('�', 'E');
            MAP_NORM.put('�', 'E');
            MAP_NORM.put('�', 'E');
            MAP_NORM.put('�', 'E');
            MAP_NORM.put('�', 'I');
            MAP_NORM.put('�', 'I');
            MAP_NORM.put('�', 'I');
            MAP_NORM.put('�', 'I');
            MAP_NORM.put('�', 'U');
            MAP_NORM.put('�', 'U');
            MAP_NORM.put('�', 'U');
            MAP_NORM.put('�', 'U');
            MAP_NORM.put('�', 'O');
            MAP_NORM.put('�', 'O');
            MAP_NORM.put('�', 'O');
            MAP_NORM.put('�', 'O');
            MAP_NORM.put('�', 'O');
            MAP_NORM.put('�', 'N');
            MAP_NORM.put('�', 'C');
            MAP_NORM.put('�', 'A');
            MAP_NORM.put('�', 'O');
            MAP_NORM.put('�', 'S');
            MAP_NORM.put('�', '3');
            MAP_NORM.put('�', '2');
            MAP_NORM.put('�', '1');
            MAP_NORM.put('�', 'a');
            MAP_NORM.put('�', 'a');
            MAP_NORM.put('�', 'a');
            MAP_NORM.put('�', 'a');
            MAP_NORM.put('�', 'a');
            MAP_NORM.put('�', 'e');
            MAP_NORM.put('�', 'e');
            MAP_NORM.put('�', 'e');
            MAP_NORM.put('�', 'e');
            MAP_NORM.put('�', 'i');
            MAP_NORM.put('�', 'i');
            MAP_NORM.put('�', 'i');
            MAP_NORM.put('�', 'i');
            MAP_NORM.put('�', 'u');
            MAP_NORM.put('�', 'u');
            MAP_NORM.put('�', 'u');
            MAP_NORM.put('�', 'u');
            MAP_NORM.put('�', 'o');
            MAP_NORM.put('�', 'o');
            MAP_NORM.put('�', 'o');
            MAP_NORM.put('�', 'o');
            MAP_NORM.put('�', 'o');
            MAP_NORM.put('�', 'n');
            MAP_NORM.put('�', 'c');
        }

        if (value == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder(value);

        for(int i = 0; i < value.length(); i++) {
            Character c = MAP_NORM.get(sb.charAt(i));
            if(c != null) {
                sb.setCharAt(i, c.charValue());
            }
        }

        return sb.toString();
    }

}
