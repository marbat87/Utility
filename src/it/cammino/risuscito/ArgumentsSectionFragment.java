package it.cammino.risuscito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

public class ArgumentsSectionFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */

	private DatabaseCanti listaCanti;
	private List<Map<String, String>> groupData;
    private List<List<Map<String, String>>> childData;
  	private static final String NAME = "NAME";
  	private SongRowAdapter mAdapter;
    private ExpandableListView expList;
    int lastExpandedGroupPosition = 0;
	private String titoloDaAgg;
	private int idListaDaAgg;
	private int posizioneDaAgg;
	private ListaPersonalizzata[] listePers;
	private int[] idListe;
	private int idListaClick;
	private int idPosizioneClick;
	private int prevOrientation;
	
	private final int ID_FITTIZIO = 99999999;
	private final int ID_BASE = 100;
	
	private LUtils mLUtils;
	
//	private final String LISTA_PERSONALIZZATA_TAG = "1";
//	private final String LISTA_PREDEFINITA_TAG = "2";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
		R.layout.fragment_argomenti_index, container, false);
		    		    
        expList = (ExpandableListView) rootView.findViewById(R.id.argomentiList);
        
		//crea un istanza dell'oggetto DatabaseCanti
		listaCanti = new DatabaseCanti(getActivity());
        
		// crea un manipolatore per il Database in modalit� READ
		SQLiteDatabase db = listaCanti.getReadableDatabase();
		
		// lancia la ricerca di tutti i gli argomenti in DB e li dispone in ordine alfabetico
		String query = "SELECT _id, nome" +
				"		FROM ARG_NAMES" +
				"		ORDER BY nome ASC";
		Cursor arguments = db.rawQuery(query, null);
		
		//recupera il numero di argomenti trovati
		int total = arguments.getCount();
		arguments.moveToFirst();
		    		    		
		groupData = new ArrayList<Map<String, String>>();
        childData = new ArrayList<List<Map<String, String>>>();
        
        for (int i = 0; i < total; i++) {
        	
            Map<String, String> curGroupMap = new HashMap<String, String>();
            groupData.add(curGroupMap);
            
            curGroupMap.put(NAME, arguments.getString(1));
            int argId =  arguments.getInt(0);
            arguments.moveToNext();
            
            query = "SELECT B.titolo, color, pagina" +
    				"		FROM ARGOMENTI A, ELENCO B " +
    				"       WHERE A._id = " + argId + 
    				"       AND A.id_canto = B._id " + 
    				"		ORDER BY TITOLO ASC";
    		Cursor argCanti = db.rawQuery(query, null);
    		
    		//recupera il numero di canti per l'argomento
    		int totCanti = argCanti.getCount();
    		argCanti.moveToFirst();
    		
            List<Map<String, String>> children = new ArrayList<Map<String, String>>();

            for (int j = 0; j < totCanti; j++) {
                Map<String, String> curChildMap = new HashMap<String, String>();
                children.add(curChildMap);
                curChildMap.put(NAME, Utility.intToString(argCanti.getInt(2),3)
                		+ argCanti.getString(1) + argCanti.getString(0));
                argCanti.moveToNext();
            }
            childData.add(children);
            
            argCanti.close();
            
        }
        
        mAdapter = new SongRowAdapter();
        
        expList.setAdapter(mAdapter);
        
        arguments.close();
		
        // fa in modo che la visuale scolli al gruppo cliccato
        expList.setOnGroupClickListener(new OnGroupClickListener() {
			
        	@Override
        	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
        	        long id) {

        	    parent.smoothScrollToPosition(groupPosition);

        	    if (parent.isGroupExpanded(groupPosition)) {
        	        parent.collapseGroup(groupPosition);
        	    } else {
        	        parent.expandGroup(groupPosition);
        	    }

        	    return true;
        	}
		});
        
        expList.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int arg0) {
				if(arg0 != lastExpandedGroupPosition){
					expList.collapseGroup(lastExpandedGroupPosition);
				}
		        lastExpandedGroupPosition = arg0;
			}
		});
        
//        db.close();
        
		// setta l'azione al click su ogni voce dell'elenco
        expList.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				TextView exptv = (TextView)v.findViewById(R.id.text_title);
	    		
	    		String cantoCliccato = exptv.getText().toString();
	    		cantoCliccato = Utility.duplicaApostrofi(cantoCliccato);
	    			    		
	    		// crea un manipolatore per il Database in modalit� READ
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
			    startSubActivity(bundle, v);
	    		
				return false;
			}
		
        });
			
		query = "SELECT _id, lista" +
				"		FROM LISTE_PERS" +
				"		ORDER BY _id ASC";
		Cursor lista = db.rawQuery(query, null);
				
		listePers = new ListaPersonalizzata[lista.getCount()];
		idListe = new int[lista.getCount()];
		
		lista.moveToFirst();    		    		
		for (int i = 0; i < lista.getCount(); i++) {
			idListe[i] = lista.getInt(0);
			listePers[i] = (ListaPersonalizzata) ListaPersonalizzata.
					deserializeObject(lista.getBlob(1));
			lista.moveToNext();
		}
		
		lista.close();
		db.close();
        
        registerForContextMenu(expList);
        
        mLUtils = LUtils.getInstance(getActivity());
        
		return rootView;
	}
		   
	@Override
	public void onDestroy() {
		if (listaCanti != null)
			listaCanti.close();
		super.onDestroy();
	}
	
    private void startSubActivity(Bundle bundle, View view) {
    	Intent intent = new Intent(getActivity(), PaginaRenderActivity.class);
    	intent.putExtras(bundle);
//    	startActivity(intent);
//    	getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold_on);
    	mLUtils.startActivityWithTransition(intent, view, "CLICKED");
   	}
    
    private class SongRowAdapter extends SimpleExpandableListAdapter {
    	
    	SongRowAdapter() {
    		super(	getActivity(),
                    groupData,
                    R.layout.simple_expandable_list_item_1,
                    new String[] { NAME },
                    new int[] { android.R.id.text1 },
                    childData,
                    R.layout.row_item,
                    new String[] { NAME },
                    new int[] { R.id.text_title }
                    );
    	}
    	
    	@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

    		View row = super.getChildView(groupPosition, childPosition,
    				isLastChild, convertView, parent);
    		TextView canto = (TextView) row.findViewById(R.id.text_title);
    		
    		String totalString = canto.getText().toString();
    		int tempPagina = Integer.valueOf(totalString.substring(0,3));
    		String pagina = String.valueOf(tempPagina);
    		String colore = totalString.substring(3, 10);
    		
    		canto.setText(totalString.substring(10));
	        		
    		TextView textPage = (TextView) row.findViewById(R.id.text_page);
    		textPage.setText(pagina);
    		View fullRow = (View) row.findViewById(R.id.full_row);
    		fullRow.setBackgroundColor(Color.parseColor(colore));
    		
    		return row;
		}
    	
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        
        ExpandableListView.ExpandableListContextMenuInfo info =
        	    (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD)  {
	        titoloDaAgg = ((TextView) info.targetView.findViewById(R.id.text_title)).getText().toString();
	        menu.setHeaderTitle("Aggiungi canto a:");
	        
	        for (int i = 0; i < idListe.length; i++) {
	        	SubMenu subMenu = menu.addSubMenu(ID_FITTIZIO, Menu.NONE, 10+i, listePers[i].getName());
	        	for (int k = 0; k < listePers[i].getNumPosizioni(); k++) {
	        		subMenu.add(ID_BASE + i, k, k, listePers[i].getNomePosizione(k));
	        	}
	        }
	        
	        MenuInflater inflater = getActivity().getMenuInflater();
	        inflater.inflate(R.menu.add_to, menu);
        }
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	if (getUserVisibleHint()) {
	        switch (item.getItemId()) {
	            case R.id.add_to_favorites:
	                addToFavorites(titoloDaAgg);
	                return true;
	            case R.id.add_to_p_iniziale:
	                addToListaNoDup(1, 1, titoloDaAgg);
	                return true;
	            case R.id.add_to_p_prima:
	            	addToListaNoDup(1, 2, titoloDaAgg);
	                return true;
	            case R.id.add_to_p_seconda:
	            	addToListaNoDup(1, 3, titoloDaAgg);
	                return true;
	            case R.id.add_to_p_terza:
	            	addToListaNoDup(1, 4, titoloDaAgg);
	                return true;
	            case R.id.add_to_p_fine:
	            	addToListaNoDup(1, 5, titoloDaAgg);
	                return true;
	            case R.id.add_to_e_iniziale:
	            	addToListaNoDup(2, 1, titoloDaAgg);
	                return true;
	            case R.id.add_to_e_pace:
	            	addToListaNoDup(2, 2, titoloDaAgg);
	                return true;
	            case R.id.add_to_e_pane:
	                addToListaDup(2, 3, titoloDaAgg);
	                return true;
	            case R.id.add_to_e_vino:
	            	addToListaDup(2, 4, titoloDaAgg);
	                return true;
	            case R.id.add_to_e_fine:
	            	addToListaNoDup(2, 5, titoloDaAgg);
	                return true;
	            default:
	            	idListaClick = item.getGroupId();
	            	idPosizioneClick = item.getItemId();
	            	if (idListaClick != ID_FITTIZIO && idListaClick >= 100) {
	            		idListaClick -= 100;
	            		if (listePers[idListaClick]
	            				.getCantoPosizione(idPosizioneClick).equalsIgnoreCase("")) {
		            		String cantoCliccatoNoApex = Utility.duplicaApostrofi(titoloDaAgg);		    			    
		    		        SQLiteDatabase db = listaCanti.getReadableDatabase();
		            		
		    		        String query = "SELECT color, pagina" +
				    				"		FROM ELENCO" +
				    				"		WHERE titolo = '" + cantoCliccatoNoApex + "'";
				    		Cursor cursor = db.rawQuery(query, null);
			    			
				    		cursor.moveToFirst();
				    							    		
				    		listePers[idListaClick].addCanto(Utility.intToString(
				    				cursor.getInt(1), 3) + cursor.getString(0) + titoloDaAgg, idPosizioneClick);
			    						    				
		    		    	ContentValues  values = new  ContentValues( );
		    		    	values.put("lista" , ListaPersonalizzata.serializeObject(listePers[idListaClick]));
		    		    	db.update("LISTE_PERS", values, "_id = " + idListe[idListaClick], null );	
		    		    	db.close();
		    		    	
		    	    		Toast.makeText(getActivity()
		    	    				, getString(R.string.list_added), Toast.LENGTH_SHORT).show();
	            		}
		    		    else {
		    		    	if (listePers[idListaClick].getCantoPosizione(idPosizioneClick).substring(10)
		    		    			.equalsIgnoreCase(titoloDaAgg)) {
		    		    		Toast toast = Toast.makeText(getActivity()
		    		    				, getString(R.string.present_yet), Toast.LENGTH_SHORT);
		    		    		toast.show();
		    		    	}
		    		    	else {
			    		    	blockOrientation();
//			    				GenericDialogFragment dialog = new GenericDialogFragment();
//			    				dialog.setListener(ArgumentsSectionFragment.this);
//			    				dialog.setCustomMessage(getString(R.string.dialog_present_yet) + " " 
//			    						+ listePers[idListaClick].getCantoPosizione(idPosizioneClick)
//			    						.substring(10)
//			    						+ getString(R.string.dialog_wonna_replace));
//			    				dialog.setOnKeyListener(new Dialog.OnKeyListener() {
//	
//			    		            @Override
//			    		            public boolean onKey(DialogInterface arg0, int keyCode,
//			    		                    KeyEvent event) {
//			    		                if (keyCode == KeyEvent.KEYCODE_BACK
//			    		                		&& event.getAction() == KeyEvent.ACTION_UP) {
//			    		                    arg0.dismiss();
//			    							getActivity().setRequestedOrientation(prevOrientation);
//			    							return true;
//			    		                }
//			    		                return false;
//			    		            }
//			    		        });
//			    	            dialog.show(getChildFragmentManager(), LISTA_PERSONALIZZATA_TAG);
//			    	            dialog.setCancelable(false);
			    		    	MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
			                    .title(R.string.dialog_replace_title)
			                    .content(getString(R.string.dialog_present_yet) + " " 
			    						+ listePers[idListaClick].getCantoPosizione(idPosizioneClick)
			    						.substring(10)
			    						+ getString(R.string.dialog_wonna_replace))
			                    .positiveText(R.string.confirm)  // the default is 'Accept', this line could be left out
			                    .negativeText(R.string.dismiss)  // leaving this line out will remove the negative button
			                    .callback(new MaterialDialog.Callback() {
			                    	@Override
			                    	public void onPositive(MaterialDialog dialog) {
			                    		SQLiteDatabase db = listaCanti.getReadableDatabase();
			                        	String cantoCliccatoNoApex = Utility.duplicaApostrofi(titoloDaAgg);	
			                    		String query = "SELECT color, pagina" +
			                    				"		FROM ELENCO" +
			                    				"		WHERE titolo = '" + cantoCliccatoNoApex + "'";
			                    		Cursor cursor = db.rawQuery(query, null);
			                			
			                    		cursor.moveToFirst();
			                    							    		
			                    		listePers[idListaClick].addCanto(Utility.intToString(
			                    				cursor.getInt(1), 3) + cursor.getString(0) + titoloDaAgg, idPosizioneClick);
			                						    				
			                	    	ContentValues  values = new  ContentValues( );
			                	    	values.put("lista" , ListaPersonalizzata.serializeObject(listePers[idListaClick]));
			                	    	db.update("LISTE_PERS", values, "_id = " + idListe[idListaClick], null );
			                	    	getActivity().setRequestedOrientation(prevOrientation);
		                    			Toast.makeText(getActivity()
		                    					, getString(R.string.list_added), Toast.LENGTH_SHORT).show();
			                    	}

			                    	@Override
			                    	public void onNegative(MaterialDialog dialog) {
			                    		getActivity().setRequestedOrientation(prevOrientation);
			                    	}
			                    })
//			                    .titleColor(getResources().getColor(android.R.color.black))
			                    .build();
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
			                    dialog.show();
			                    dialog.setCancelable(false);
		    		    	}
		    		    }    		
		    		    return true;
	            	}
	            	else
	            		return super.onContextItemSelected(item);
	        }
    	}
    	else
    		return false;
    }
   
    //aggiunge il canto premuto ai preferiti
    public void addToFavorites(String titolo) {
    	
    	SQLiteDatabase db = listaCanti.getReadableDatabase();
    	
    	String titoloNoApex = Utility.duplicaApostrofi(titolo);
    	
		String sql = "UPDATE ELENCO" +
				"  SET favourite = 1" + 
				"  WHERE titolo =  \'" + titoloNoApex + "\'";
		db.execSQL(sql);
		db.close();
		
		Toast toast = Toast.makeText(getActivity()
				, getString(R.string.favorite_added), Toast.LENGTH_SHORT);
		toast.show();
		
		//permette di aggiornare il numero dei preferiti nel menu laterale
//		((MainActivity) getActivity()).onResume();
		
    }
    
    //aggiunge il canto premuto ad una lista e in una posizione che ammetta duplicati
    public void addToListaDup(int idLista, int listPosition, String titolo) {
    	
    	String titoloNoApex = Utility.duplicaApostrofi(titolo);
    	
    	SQLiteDatabase db = listaCanti.getReadableDatabase();
    	
    	String sql = "INSERT INTO CUST_LISTS ";
    	sql+= "VALUES (" + idLista + ", " 
    			 + listPosition + ", "
    			 + "(SELECT _id FROM ELENCO"
    			 + " WHERE titolo = \'" + titoloNoApex + "\')"
    			 + ", CURRENT_TIMESTAMP)";
    	
    	try {
    		db.execSQL(sql);
    		Toast.makeText(getActivity()
    				, getString(R.string.list_added), Toast.LENGTH_SHORT).show();
    	} catch (SQLException e) {
    		Toast toast = Toast.makeText(getActivity()
    				, getString(R.string.present_yet), Toast.LENGTH_SHORT);
    		toast.show();
    	}
    	
    	db.close();
    }
    
  //aggiunge il canto premuto ad una lista e in una posizione che NON ammetta duplicati
    public void addToListaNoDup(int idLista, int listPosition, String titolo) {
    	
    	String titoloNoApex = Utility.duplicaApostrofi(titolo);
    	
    	SQLiteDatabase db = listaCanti.getReadableDatabase();
    	
		// cerca se la posizione nella lista � gi� occupata
		String query = "SELECT B.titolo" +
				"		FROM CUST_LISTS A" +
				"		   , ELENCO B" +
				"		WHERE A._id = " + idLista +
				"         AND A.position = " + listPosition +
				"         AND A.id_canto = B._id";
		Cursor lista = db.rawQuery(query, null);
		
		int total = lista.getCount();
		
		if (total > 0) {
			lista.moveToFirst();    
			String titoloPresente = lista.getString(0);
    		lista.close();
    		db.close();
    		
			if (titolo.equalsIgnoreCase(titoloPresente)) {
	    		Toast toast = Toast.makeText(getActivity()
	    				, getString(R.string.present_yet), Toast.LENGTH_SHORT);
	    		toast.show();
			}
			else {
				idListaDaAgg = idLista;
				posizioneDaAgg = listPosition;
	    		blockOrientation();
//				GenericDialogFragment dialog = new GenericDialogFragment();
//				dialog.setListener(ArgumentsSectionFragment.this);
//				dialog.setCustomMessage(getString(R.string.dialog_present_yet) + " " +  titoloPresente
//						+ getString(R.string.dialog_wonna_replace));
//				dialog.setOnKeyListener(new Dialog.OnKeyListener() {
//	
//		            @Override
//		            public boolean onKey(DialogInterface arg0, int keyCode,
//		                    KeyEvent event) {
//		                if (keyCode == KeyEvent.KEYCODE_BACK
//		                		&& event.getAction() == KeyEvent.ACTION_UP) {
//		                    arg0.dismiss();
//							getActivity().setRequestedOrientation(prevOrientation);
//							return true;
//		                }
//		                return false;
//		            }
//		        });
//	            dialog.show(getChildFragmentManager(), LISTA_PREDEFINITA_TAG);
//	            dialog.setCancelable(false);
	    		MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.dialog_replace_title)
                .content(getString(R.string.dialog_present_yet) + " " + titoloPresente
						+ getString(R.string.dialog_wonna_replace))
                .positiveText(R.string.confirm)  // the default is 'Accept', this line could be left out
                .negativeText(R.string.dismiss)  // leaving this line out will remove the negative button
                .callback(new MaterialDialog.Callback() {
                	@Override
                	public void onPositive(MaterialDialog dialog) {
                		SQLiteDatabase db = listaCanti.getReadableDatabase();
                    	String cantoCliccatoNoApex = Utility.duplicaApostrofi(titoloDaAgg);	
                		String sql = "UPDATE CUST_LISTS "
            	    			+ "SET id_canto = (SELECT _id  FROM ELENCO"
            	    			+ " WHERE titolo = \'" + cantoCliccatoNoApex + "\')"
            	    			+ "WHERE _id = " + idListaDaAgg 
            	    			+ "  AND position = " + posizioneDaAgg;
            	    	db.execSQL(sql);	
            	    	getActivity().setRequestedOrientation(prevOrientation);
            			Toast.makeText(getActivity()
            					, getString(R.string.list_added), Toast.LENGTH_SHORT).show();
                	}

                	@Override
                	public void onNegative(MaterialDialog dialog) {
                		getActivity().setRequestedOrientation(prevOrientation);
                	}
                })
//                .titleColor(getResources().getColor(android.R.color.black))
                .build();
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
                dialog.show();
                dialog.setCancelable(false);
			}
    		return;
		}
		
		lista.close();
    	
    	String sql = "INSERT INTO CUST_LISTS "
    	         + "VALUES (" + idLista + ", " 
    			 + listPosition + ", "
    			 + "(SELECT _id FROM ELENCO"
    			 + " WHERE titolo = \'" + titoloNoApex + "\')"
    			 + ", CURRENT_TIMESTAMP)"; 	
    	db.execSQL(sql);
    	db.close();
		
		Toast.makeText(getActivity()
				, getString(R.string.list_added), Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onDialogPositiveClick(DialogFragment dialog) {
//    	SQLiteDatabase db = listaCanti.getReadableDatabase();
//    	String cantoCliccatoNoApex = Utility.duplicaApostrofi(titoloDaAgg);	
//    	
//    	if (dialog.getTag().equals(LISTA_PREDEFINITA_TAG)) {
//        	String sql = "UPDATE CUST_LISTS "
//        			+ "SET id_canto = (SELECT _id  FROM ELENCO"
//        			+ " WHERE titolo = \'" + cantoCliccatoNoApex + "\')"
//        			+ "WHERE _id = " + idListaDaAgg 
//        			+ "  AND position = " + posizioneDaAgg;
//        	db.execSQL(sql);      	
//        	
//    	}
//    	else if (dialog.getTag().equals(LISTA_PERSONALIZZATA_TAG)){    		
//	        String query = "SELECT color, pagina" +
//    				"		FROM ELENCO" +
//    				"		WHERE titolo = '" + cantoCliccatoNoApex + "'";
//    		Cursor cursor = db.rawQuery(query, null);
//			
//    		cursor.moveToFirst();
//    							    		
//    		listePers[idListaClick].addCanto(Utility.intToString(
//    				cursor.getInt(1), 3) + cursor.getString(0) + titoloDaAgg, idPosizioneClick);
//						    				
//	    	ContentValues  values = new  ContentValues( );
//	    	values.put("lista" , ListaPersonalizzata.serializeObject(listePers[idListaClick]));
//	    	db.update("LISTE_PERS", values, "_id = " + idListe[idListaClick], null );	
//    	}
//    	db.close();
//        dialog.dismiss();
//        getActivity().setRequestedOrientation(prevOrientation);
//        
//		Toast.makeText(getActivity()
//				, getString(R.string.list_added), Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onDialogNegativeClick(DialogFragment dialog) {
//        // User touched the dialog's negative button
//        dialog.dismiss();
//        getActivity().setRequestedOrientation(prevOrientation);
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
