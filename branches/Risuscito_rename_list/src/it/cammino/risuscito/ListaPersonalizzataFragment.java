package it.cammino.risuscito;

import it.cammino.risuscito.GenericDialogFragment.GenericDialogListener;

import java.util.Locale;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.ViewPager;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

@SuppressWarnings("deprecation")
public class ListaPersonalizzataFragment extends Fragment
			implements GenericDialogListener {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	
	private int posizioneDaCanc;
	private View rootView;
	private ShareActionProvider mShareActionProvider;
	private DatabaseCanti listaCanti;
	private SQLiteDatabase db;
	private int fragmentIndex;
	private int idLista;
	private ListaPersonalizzata listaPersonalizzata;
	private int cantoIndex;
	private int prevOrientation;
	
	private final String RESET_LIST_TAG = "1";
	private final String RIMUOVI_CANTO_TAG = "2";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(
		R.layout.activity_lista_personalizzata, container, false);
			
		//crea un istanza dell'oggetto DatabaseCanti
		listaCanti = new DatabaseCanti(getActivity());
		
		rootView.findViewById(R.id.button_resetListPers).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				blockOrientation();
				GenericDialogFragment dialog = new GenericDialogFragment();
				dialog.setCustomMessage(getString(R.string.reset_list_question));
				dialog.setListener(ListaPersonalizzataFragment.this);
				dialog.setOnKeyListener(new Dialog.OnKeyListener() {

		            @Override
		            public boolean onKey(DialogInterface arg0, int keyCode,
		                    KeyEvent event) {
		                if (keyCode == KeyEvent.KEYCODE_BACK
		                		&& event.getAction() == KeyEvent.ACTION_UP) {
		                    arg0.dismiss();
							getActivity().setRequestedOrientation(prevOrientation);
		                }
		                return true;
		            }
		        });
				dialog.show(getChildFragmentManager(), RESET_LIST_TAG);
				dialog.setCancelable(false);
			}
		});
		
		setHasOptionsMenu(true);	
		
		return rootView;
	}
		   
    @Override
    public void onResume() {
    	super.onResume();
		fragmentIndex = getArguments().getInt("position");
		idLista = getArguments().getInt("idLista");
//		Log.i("fragmentIndex", fragmentIndex+"");
//		Log.i("idLista", idLista+"");
		
		db = listaCanti.getReadableDatabase();
		
		String query = "SELECT lista" +
				"  FROM LISTE_PERS" +
				"  WHERE _id =  " + idLista;   
		Cursor cursor = db.rawQuery(query, null);
	      
		// recupera l'oggetto lista personalizzata
		cursor.moveToFirst();
					    			
		listaPersonalizzata = (ListaPersonalizzata) ListaPersonalizzata.
			deserializeObject(cursor.getBlob(0));
		
		updateLista();
		ViewPager tempPager = (ViewPager) getActivity().findViewById(R.id.pager);
		if (mShareActionProvider != null && tempPager.getCurrentItem() == fragmentIndex)
			//aggiorna lo share intent usato per condividere la lista
			mShareActionProvider.setShareIntent(getDefaultIntent());
    }
    
	@Override
	public void onDestroy() {
		if (listaCanti != null)
			listaCanti.close();
		super.onDestroy();
	}
	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getActivity().getMenuInflater().inflate(R.menu.list_with_delete, menu);
		inflater.inflate(R.menu.list_with_delete, menu);
	    // Locate MenuItem with ShareActionProvider
	    MenuItem shareItem = menu.findItem(R.id.action_share);

	    // Fetch and store ShareActionProvider
	    mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
	    if (listaPersonalizzata != null)
	    	mShareActionProvider.setShareIntent(getDefaultIntent());
	    super.onCreateOptionsMenu(menu, inflater);
	    
	}

	private Intent getDefaultIntent() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, getTitlesList());
		intent.setType("text/plain");
		return intent;
	}
    
    private void openPagina(View v) {
    	// recupera il titolo della voce cliccata
		String cantoCliccato = ((TextView) v).getText().toString();
		cantoCliccato = Utility.duplicaApostrofi(cantoCliccato);
        		
		// crea un manipolatore per il DB in modalità READ
		db = listaCanti.getReadableDatabase();
	    
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
	    
    	Intent intent = new Intent(getActivity(), PaginaRenderActivity.class);
    	intent.putExtras(bundle);
    	startActivity(intent);
    }
    
    private void updateLista() {
		
//		Log.i("POSITION", fragmentIndex+" ");
//		Log.i("IDLISTA", idLista+" ");
//		Log.i("TITOLO", listaPersonalizzata.getName());
		
		LinearLayout linLayout = (LinearLayout) rootView.findViewById(R.id.listaScroll);
		linLayout.removeAllViews();

		for (cantoIndex = 0; cantoIndex < listaPersonalizzata.getNumPosizioni(); cantoIndex++) {
//			View view = getActivity().getLayoutInflater().inflate(R.layout.oggetto_lista_generico, null, false);
			View view = getActivity().getLayoutInflater().inflate(R.layout.oggetto_lista_generico, linLayout, false);
			
	   		((TextView) view.findViewById(R.id.titoloPosizioneGenerica))
	   			.setText(listaPersonalizzata.getNomePosizione(cantoIndex));
	   		
	   		((TextView) view.findViewById(R.id.id_posizione))
   			.setText(String.valueOf(cantoIndex));
	   				  	   		
//	   		Log.i("CANTO[" + cantoIndex + "]", listaPersonalizzata.getCantoPosizione(cantoIndex) + " ");
	   		
	   		if (listaPersonalizzata.getCantoPosizione(cantoIndex).length() == 0) {
	   		
				view.findViewById(R.id.addCantoGenerico).setVisibility(View.VISIBLE);
				view.findViewById(R.id.cantoGenerico).setVisibility(View.GONE);
				
				view.findViewById(R.id.addCantoGenerico).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
					    Bundle bundle = new Bundle();
					    bundle.putInt("fromAdd", 0);
					    bundle.putInt("idLista", idLista);
					    bundle.putInt("position", (Integer.valueOf(
					    		((TextView) v.findViewById(R.id.id_posizione))
					    		.getText().toString())));
//					    startSubActivity(bundle);
				    	Intent intent = new Intent(getActivity(), GeneralInsertSearch.class);
				    	intent.putExtras(bundle);
				    	startActivity(intent);
					}
				}); 
				
	   		}
	   		else {
	   			
				TextView temp = (TextView) view.findViewById(R.id.cantoGenerico);
				view.findViewById(R.id.addCantoGenerico).setVisibility(View.GONE);
				temp.setVisibility(View.VISIBLE);
				
				temp.setText(listaPersonalizzata.getCantoPosizione(cantoIndex).substring(10));
				((View) view.findViewById(R.id.cantoGenericoContainer))
				.setBackgroundColor(Color.parseColor(listaPersonalizzata.getCantoPosizione(cantoIndex).substring(3, 10)));
		   		((TextView) view.findViewById(R.id.id_da_canc))
	   			.setText(String.valueOf(cantoIndex));
				
		   		view.findViewById(R.id.cantoGenerico).setOnClickListener(new OnClickListener() {
				      @Override
				      public void onClick(View v) {
				    	  openPagina(v.findViewById(R.id.cantoGenerico));
				      }
		   		});
			   		
				// setta l'azione tenendo premuto sul canto
		   		view.findViewById(R.id.cantoGenerico).setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View view) {
						blockOrientation();
						posizioneDaCanc = Integer.valueOf(
					    		((TextView) ((LinearLayout)view.getParent()).findViewById(R.id.id_da_canc))
					    		.getText().toString());
//						Log.i("canto da rimuovere", posizioneDaCanc + " ");
						GenericDialogFragment dialog = new GenericDialogFragment();
						dialog.setCustomMessage(getString(R.string.list_remove));
						dialog.setListener(ListaPersonalizzataFragment.this);
						dialog.setOnKeyListener(new Dialog.OnKeyListener() {

				            @Override
				            public boolean onKey(DialogInterface arg0, int keyCode,
				                    KeyEvent event) {
				                if (keyCode == KeyEvent.KEYCODE_BACK
				                		&& event.getAction() == KeyEvent.ACTION_UP) {
				                    arg0.dismiss();
									getActivity().setRequestedOrientation(prevOrientation);
				                }
				                return true;
				            }
				        });
		                dialog.show(getChildFragmentManager(), RIMUOVI_CANTO_TAG);
		                dialog.setCancelable(false);
						return false;
					}
				});
	   		
	   		}
	   		
	   		linLayout.addView(view);
		}
    
	}
    
    private String getTitlesList() {
    	
    	Locale l = Locale.getDefault();
    	String result = "";
    	
    	//titolo
    	result +=  "-- "  + listaPersonalizzata.getName().toUpperCase(l) + " --\n";
    	
    	//tutti i canti
    	for (int i = 0; i < listaPersonalizzata.getNumPosizioni(); i++) {
    		result += listaPersonalizzata.getNomePosizione(i).toUpperCase(l) + "\n";		
    		if (!listaPersonalizzata.getCantoPosizione(i).equalsIgnoreCase(""))
    			result += listaPersonalizzata.getCantoPosizione(i).substring(10)
    					+ " - PAG." + Integer.valueOf(listaPersonalizzata.
    								getCantoPosizione(i).substring(0,3));
    		else
    			result += ">> da scegliere <<";
    		if (i < listaPersonalizzata.getNumPosizioni() - 1)
    			result += "\n";
    	}   	
    	    	
    	return result;
    	
    }
    
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
    	db = listaCanti.getReadableDatabase();
    	ContentValues  values = new  ContentValues( );
    	
    	if (dialog.getTag().equals(RIMUOVI_CANTO_TAG)) { 	    	
    		listaPersonalizzata.removeCanto(posizioneDaCanc);		
			
    	}
    	else if (dialog.getTag().equals(RESET_LIST_TAG)) {    		
    		for (int i = 0; i < listaPersonalizzata.getNumPosizioni(); i++)
    			listaPersonalizzata.removeCanto(i);
    	}
			
    	values.put("lista" , ListaPersonalizzata.serializeObject(listaPersonalizzata));
    	db.update("LISTE_PERS", values, "_id = " + idLista, null );
		db.close();
		updateLista();
		//aggiorna lo share intent usato per condividere la lista
		mShareActionProvider.setShareIntent(getDefaultIntent());
		getActivity().setRequestedOrientation(prevOrientation);
		
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
        getActivity().setRequestedOrientation(prevOrientation);
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
    
}