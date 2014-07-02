package it.cammino.risuscito;

import it.cammino.risuscito.TextDialogFragment.TextDialogListener;
import it.cammino.risuscito.ThreeButtonsDialogFragment.ThreeButtonsDialogListener;

import java.util.ArrayList;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.RelativeLayout;
import org.holoeverywhere.widget.Toast;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.espian.showcaseview.OnShowcaseEventListener;
import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.targets.ActionItemTarget;
import com.espian.showcaseview.targets.ViewTarget;
import com.mobeta.android.dslv.DragSortListView;

@SuppressLint("NewApi")
public class CreaListaActivity extends Activity
								implements TextDialogListener, ThreeButtonsDialogListener {

	private ListaPersonalizzata celebrazione;
	private DatabaseCanti listaCanti;
	private PositionAdapter adapter;
	private ArrayList<String> nomiElementi;
	private String titoloLista;
	private DragSortListView lv;
	private int prevOrientation;
	private boolean modifica;
	private int idModifica;
	private RetainedFragment dataFragment;
	private RetainedFragment dataFragment2;
	private int positionToRename;
	private RelativeLayout.LayoutParams lps;
	private boolean fakeItemCreated;
	private int screenWidth;
	private int screenHeight;
	private ArrayList<String> nomiCanti;
	private int positionLI;
	
	private static final String PREF_FIRST_OPEN = "prima_apertura_crealista";
	
	private final String AGGIUNGI_POSIZIONE_TAG = "1";
	private final String RINOMINA_POSIZIONE_TAG = "2";
	private final String SALVA_LISTA_TAG = "3";
			
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Utility.updateTheme(CreaListaActivity.this);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_crea_lista);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);	
	
		listaCanti = new DatabaseCanti(this);
		
        Bundle bundle = this.getIntent().getExtras();
        modifica = bundle.getBoolean("modifica");
        
        if (modifica) {
        	SQLiteDatabase db = listaCanti.getReadableDatabase();
        	
        	idModifica = bundle.getInt("idDaModif");
        	
    	    String query = "SELECT titolo_lista, lista"
    	      		+ "  FROM LISTE_PERS"
    	      		+ "  WHERE _id = " + idModifica;
    	    Cursor cursor = db.rawQuery(query, null);

    	    cursor.moveToFirst();
    	    titoloLista = cursor.getString(0);
    	    celebrazione = (ListaPersonalizzata) ListaPersonalizzata.deserializeObject(cursor.getBlob(1));
    	    cursor.close();
    	    db.close();
        }
        else
        	titoloLista = bundle.getString("titolo");
        
        getSupportActionBar().setTitle(titoloLista);
				
		lv = (DragSortListView) findViewById(android.R.id.list);
		
        lv.setDropListener(onDrop);
        lv.setRemoveListener(onRemove);

        dataFragment = (RetainedFragment) getSupportFragmentManager().findFragmentByTag("nomiElementi");
        if (dataFragment != null) {
            nomiElementi = dataFragment.getData();
        }
        else {
        	nomiElementi = new ArrayList<String>();
        	if (modifica) {
	        	for (int i = 0; i < celebrazione.getNumPosizioni(); i++)
	        		nomiElementi.add(celebrazione.getNomePosizione(i));
        	}
        }
        
        if (modifica) {
	        dataFragment2 = (RetainedFragment) getSupportFragmentManager().findFragmentByTag("nomiCanti");
	        if (dataFragment2 != null) {
	            nomiCanti = dataFragment2.getData();
	        }
	        else {
	        	nomiCanti = new ArrayList<String>();
	        	if (modifica) {
		        	for (int i = 0; i < celebrazione.getNumPosizioni(); i++) {
//		        		Log.i("CANTO", celebrazione.getCantoPosizione(i));
		        		nomiCanti.add(celebrazione.getCantoPosizione(i));
		        	}
	        	}
	        }
        }

        //Serve per settare il colore del testo a seconda del tema.
        //A quanto parae non si riesce usando gli attributes direttamente nel layout
        if (Utility.getChoosedTheme(CreaListaActivity.this) == 2)
        	positionLI = R.layout.position_list_item_dark;
        else
        	positionLI = R.layout.position_list_item_light;
        
        adapter = new PositionAdapter();
        lv.setAdapter(adapter);
        
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				blockOrientation();
				positionToRename = position;
				TextDialogFragment dialog = new TextDialogFragment();
				dialog.setCustomMessage(getString(R.string.posizione_rename));
				dialog.setListener(CreaListaActivity.this);
				dialog.setOnKeyListener(new Dialog.OnKeyListener() {

		            @Override
		            public boolean onKey(DialogInterface arg0, int keyCode,
		                    KeyEvent event) {
		                if (keyCode == KeyEvent.KEYCODE_BACK
		                		&& event.getAction() == KeyEvent.ACTION_UP) {
		                    arg0.dismiss();
							setRequestedOrientation(prevOrientation);
							return true;
		                }
		                return false;
		            }
		        });
		        dialog.show(getSupportFragmentManager(), RINOMINA_POSIZIONE_TAG);
		        dialog.setCancelable(false);
				return true;
			}
		});	
        
		Button saveExit = (Button) findViewById(R.id.button_save_exit);
		saveExit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (saveList())
					finish();
			}
		});
		
		View addPosizione = (View) findViewById(R.id.addPosizione);
		addPosizione.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				blockOrientation();
				TextDialogFragment dialog = new TextDialogFragment();
				dialog.setCustomMessage(getString(R.string.posizione_add_desc));
				dialog.setListener(CreaListaActivity.this);
				dialog.setOnKeyListener(new Dialog.OnKeyListener() {

		            @Override
		            public boolean onKey(DialogInterface arg0, int keyCode,
		                    KeyEvent event) {
		                if (keyCode == KeyEvent.KEYCODE_BACK
		                		&& event.getAction() == KeyEvent.ACTION_UP) {
		                    arg0.dismiss();
							setRequestedOrientation(prevOrientation);
							return true;
		                }
		                return false;
		            }
		        });
		        dialog.show(getSupportFragmentManager(), AGGIUNGI_POSIZIONE_TAG);
		        dialog.setCancelable(false);
			}
		});
		
		Display display = getWindowManager().getDefaultDisplay();
		if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			screenWidth = display.getWidth();
			screenHeight = display.getHeight();
		}
		else {
			Point size = new Point();
			display.getSize(size);
			screenWidth = size.x;
			screenHeight = size.y;
		}
		
        if(PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(PREF_FIRST_OPEN, true)) { 
            SharedPreferences.Editor editor = PreferenceManager
                    .getDefaultSharedPreferences(CreaListaActivity.this)
                    .edit();
            editor.putBoolean(PREF_FIRST_OPEN, false);
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            	editor.commit();
            } else {
            	editor.apply();
            }
        	showHelp();
        }
       	
		checkScreenAwake();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.help_menu, menu);
		return true;
	}
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_help:
			showHelp();
	        return true;
		case android.R.id.home:
			if (nomiElementi.size() > 0) {
				blockOrientation();
				ThreeButtonsDialogFragment dialog3 = new ThreeButtonsDialogFragment();
				dialog3.setCustomMessage(getString(R.string.save_list_question));
				dialog3.setListener(CreaListaActivity.this);
				dialog3.setOnKeyListener(new Dialog.OnKeyListener() {

		            @Override
		            public boolean onKey(DialogInterface arg0, int keyCode,
		                    KeyEvent event) {
		                if (keyCode == KeyEvent.KEYCODE_BACK
		                		&& event.getAction() == KeyEvent.ACTION_UP) {
		                    arg0.dismiss();
							setRequestedOrientation(prevOrientation);
							return true;
		                }
		                return false;
		            }
		        });
		        dialog3.show(getSupportFragmentManager(), SALVA_LISTA_TAG);
		        dialog3.setCancelable(false);
		        return true;
			}
			else {
				finish();
			}
			return true;	
			}
		return false;
	}
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (nomiElementi.size() > 0) {
				blockOrientation();
				ThreeButtonsDialogFragment dialog = new ThreeButtonsDialogFragment();
				dialog.setCustomMessage(getString(R.string.save_list_question));
				dialog.setListener(CreaListaActivity.this);
				dialog.setOnKeyListener(new Dialog.OnKeyListener() {

		            @Override
		            public boolean onKey(DialogInterface arg0, int keyCode,
		                    KeyEvent event) {
		                if (keyCode == KeyEvent.KEYCODE_BACK
		                		&& event.getAction() == KeyEvent.ACTION_UP) {
		                    arg0.dismiss();
							setRequestedOrientation(prevOrientation);
							return true;
		                }
		                return false;
		            }
		        });
		        dialog.show(getSupportFragmentManager(), SALVA_LISTA_TAG);
		        dialog.setCancelable(false);
		        return true;
			}
			else {
				finish();
				return true;
			}
        }
        return super.onKeyDown(keyCode, event);
    }
	
    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    String item = adapter.getItem(from);

                    adapter.remove(item);
                    adapter.insert(item, to);
                    
                    if (modifica) {
//                    	Log.i("SPOSTO CANTO", "da " + from + " a " + to);
                    	String canto = nomiCanti.remove(from);
                    	nomiCanti.add(to, canto);
                    }
                }
            };

    private DragSortListView.RemoveListener onRemove = 
            new DragSortListView.RemoveListener() {
                @Override
                public void remove(int which) {
                    adapter.remove(adapter.getItem(which));
                    
                    if (modifica) {
                    	nomiCanti.remove(which);
//                    	Log.i("RIMOSSO", which + "");

                    }
                }
            };
	
    private boolean saveList()  {
		celebrazione = new ListaPersonalizzata();
		celebrazione.setName(titoloLista);
		for (int i = 0; i < nomiElementi.size(); i++) {
			if (celebrazione.addPosizione(nomiElementi.get(i)) == -2) {
	    		Toast toast = Toast.makeText(getApplicationContext()
	    				, getString(R.string.lista_pers_piena), Toast.LENGTH_LONG);
	    		toast.show();
	    		return false;
			}
		}
		
		if (celebrazione.getNomePosizione(0).equalsIgnoreCase("")) {
    		Toast toast = Toast.makeText(getApplicationContext()
    				, getString(R.string.lista_pers_vuota), Toast.LENGTH_LONG);
    		toast.show();
    		return false;
		}
		
		if (modifica) {
    		for (int i = 0; i < nomiElementi.size(); i++) {
//    			Log.i("SALVO CANTO", nomiCanti.get(i));
    			celebrazione.addCanto(nomiCanti.get(i), i);
    		}
		}
		
    	SQLiteDatabase db = listaCanti.getReadableDatabase();
    	
    	ContentValues  values = new  ContentValues( );
    	values.put("titolo_lista" , titoloLista);
    	values.put("lista" , ListaPersonalizzata.serializeObject(celebrazione));
    	
    	if (modifica)
    		db.update("LISTE_PERS", values, "_id = " + idModifica, null);
    	else
    		db.insert("LISTE_PERS" , "" , values);
    	
    	db.close();
    	return true;
    }
            
            
    @Override
    public void onResume() {
    	super.onResume();
    	checkScreenAwake();
    }
    
	@Override
	public void onDestroy() {
		super.onDestroy();
		listaCanti.close();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
      dataFragment = new RetainedFragment();
      getSupportFragmentManager().beginTransaction().add(dataFragment, "nomiElementi").commit();
	  dataFragment.setData(nomiElementi);
	  if (modifica) {
		  dataFragment2 = new RetainedFragment();
		  getSupportFragmentManager().beginTransaction().add(dataFragment2, "nomiCanti").commit();
		  dataFragment2.setData(nomiCanti);
	  }
	  super.onSaveInstanceState(savedInstanceState);
	}
	
    //controlla se l'app deve mantenere lo schermo acceso
    public void checkScreenAwake() {
    	SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this);
		boolean screenOn = pref.getBoolean("screenOn", false);
		if (screenOn)
			lv.setKeepScreenOn(true);
		else
			lv.setKeepScreenOn(false);
    }
    
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String titolo) {
    	if (dialog.getTag().equals(AGGIUNGI_POSIZIONE_TAG)) {
	    	if (titolo == null || titolo.trim().equalsIgnoreCase("")) {
	    		Toast toast = Toast.makeText(getApplicationContext()
	    				, getString(R.string.titolo_pos_vuoto), Toast.LENGTH_SHORT);
	    		toast.show();
	    	}
	    	else {
	    		nomiElementi.add(titolo);
	    		if (modifica)
	    			nomiCanti.add("");
	            adapter.notifyDataSetChanged();
	    	}
    	}
    	else if (dialog.getTag().equals(RINOMINA_POSIZIONE_TAG)) {
	    	if (titolo == null || titolo.trim().equalsIgnoreCase("")) {
	    		Toast toast = Toast.makeText(getApplicationContext()
	    				, getString(R.string.titolo_pos_vuoto), Toast.LENGTH_SHORT);
	    		toast.show();
	    	}
	    	else {
	    		nomiElementi.set(positionToRename, titolo);
	            adapter.notifyDataSetChanged();
	    	}
    	}
    	dialog.dismiss();
    	setRequestedOrientation(prevOrientation);
    }
    
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
		setRequestedOrientation(prevOrientation);
    	if (saveList())
    		finish();
    }
    
    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {
    	dialog.dismiss();
    	setRequestedOrientation(prevOrientation);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    	if (dialog.getTag().equals(AGGIUNGI_POSIZIONE_TAG)
    			|| dialog.getTag().equals(RINOMINA_POSIZIONE_TAG)) {
    		dialog.dismiss();
    		setRequestedOrientation(prevOrientation);
    	}
    	else if (dialog.getTag().equals(SALVA_LISTA_TAG)) {
    		dialog.dismiss();
    		setRequestedOrientation(prevOrientation);
    		finish();
    	}
    }
    
    private class PositionAdapter extends ArrayAdapter<String> {    
        public PositionAdapter() {
        	super(getApplicationContext(), positionLI, R.id.position_name, nomiElementi);
        }
    }
    
    public static class RetainedFragment extends Fragment {

        // data object we want to retain
        private ArrayList<String> data;

        // this method is only called once for this fragment
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // retain this fragment
            setRetainInstance(true);
        }

        public void setData(ArrayList<String> data) {
            this.data = data;
        }

        public ArrayList<String> getData() {
            return data;
        }
    }
    
    public void blockOrientation() {
        prevOrientation = getRequestedOrientation();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }
    }
    
   	private void showHelp() {
   		if (nomiElementi.size() == 0) {
   			nomiElementi.add("CANTO DI ESEMPIO");
   			adapter.notifyDataSetChanged();
   			fakeItemCreated = true;
   		}
   		else {
   			fakeItemCreated = false;
   		}
   		blockOrientation();
	 	lps = new RelativeLayout.LayoutParams(
	 			ViewGroup.LayoutParams.WRAP_CONTENT,
	 			ViewGroup.LayoutParams.WRAP_CONTENT);
	 	lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	 	lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		int margin = ((Number) ( getApplicationContext().getResources().getDisplayMetrics().density * 12)).intValue();
		lps.setMargins(margin, margin, margin, margin);
		
		ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
		co.buttonLayoutParams = lps;
		
		//benvenuto del tutorial
   		ShowcaseView showcaseView = ShowcaseView.insertShowcaseView(
        		new ViewTarget(R.id.imagePlus, CreaListaActivity.this)
        		, CreaListaActivity.this
        		, R.string.title_activity_nuova_lista
        		, R.string.showcase_welcome_crea
        		, co);
		showcaseView.setShowcase(ShowcaseView.NONE);
		showcaseView.setButtonText(getString(R.string.showcase_button_next));
		showcaseView.setOnShowcaseEventListener(new OnShowcaseEventListener() {
			
			@Override
			public void onShowcaseViewShow(ShowcaseView showcaseView) { }
			
			@Override
			public void onShowcaseViewHide(ShowcaseView showcaseView) {
            	//spiegazione del pulsante aggiungi posizione
        		ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
        		co.buttonLayoutParams = lps;
		   		showcaseView = ShowcaseView.insertShowcaseView(
		        		new ViewTarget(R.id.imagePlus, CreaListaActivity.this)
		        		, CreaListaActivity.this
		        		, R.string.add_position
		        		, R.string.showcase_add_pos_desc
		        		, co);
				showcaseView.setButtonText(getString(R.string.showcase_button_next));
				showcaseView.setScaleMultiplier(0.5f);
				showcaseView.setOnShowcaseEventListener(new OnShowcaseEventListener() {

					@Override
					public void onShowcaseViewShow(ShowcaseView showcaseView) { }
					
					@Override
					public void onShowcaseViewHide(ShowcaseView showcaseView) {
						//spiegazione di come spostare le posizioni
		        		ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
		        		co.buttonLayoutParams = lps;
		        		ViewTarget listItem = new ViewTarget(
		        				adapter.getView(0, lv, lv).findViewById(R.id.drag_handle));
				   		showcaseView = ShowcaseView.insertShowcaseView(
				   				listItem
				        		, CreaListaActivity.this
				        		, R.string.posizione_reorder
				        		, R.string.showcase_reorder_desc
				        		, co);
						showcaseView.setButtonText(getString(R.string.showcase_button_next));
						showcaseView.setScaleMultiplier(0.5f);
						int[] coords = new int[2];
						adapter.getView(0, lv, lv).getLocationOnScreen(coords);
						showcaseView.animateGesture(coords[0], coords[1], coords[0], coords[1] + (screenHeight - coords[1])/3, true);
						showcaseView.setOnShowcaseEventListener(new OnShowcaseEventListener() {

							@Override
							public void onShowcaseViewShow(ShowcaseView showcaseView) { }
							
							@Override
							public void onShowcaseViewHide(ShowcaseView showcaseView) {
								//spiegazione di come rinominare le posizioni
				        		ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
				        		co.buttonLayoutParams = lps;
				        		ViewTarget listItem = new ViewTarget(
				        				adapter.getView(0, lv, lv).findViewById(R.id.position_name));
						   		showcaseView = ShowcaseView.insertShowcaseView(
						   				listItem
						        		, CreaListaActivity.this
						        		, R.string.posizione_rename
						        		, R.string.showcase_rename_desc
						        		, co);
								showcaseView.setButtonText(getString(R.string.showcase_button_next));
//								showcaseView.setScaleMultiplier(0.5f);
								showcaseView.setOnShowcaseEventListener(new OnShowcaseEventListener() {

									@Override
									public void onShowcaseViewShow(ShowcaseView showcaseView) { }
									
									@Override
									public void onShowcaseViewHide(ShowcaseView showcaseView) {
										//spiegazione di come cancellare le posizioni
						        		ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
						        		co.buttonLayoutParams = lps;
						        		ViewTarget listItem = new ViewTarget(
						        				adapter.getView(0, lv, lv).findViewById(R.id.position_name));
								   		showcaseView = ShowcaseView.insertShowcaseView(
								   				listItem
								        		, CreaListaActivity.this
								        		, R.string.posizione_delete
								        		, R.string.showcase_delete_desc
								        		, co);
										showcaseView.setButtonText(getString(R.string.showcase_button_next));
										int[] coords = new int[2];
										adapter.getView(0, lv, lv).findViewById(R.id.position_name).getLocationOnScreen(coords);
										showcaseView.animateGesture(coords[0], coords[1], coords[0] + (screenWidth - coords[0])/2, coords[1], true);
										showcaseView.setOnShowcaseEventListener(new OnShowcaseEventListener() {

											@Override
											public void onShowcaseViewShow(ShowcaseView showcaseView) { }
											
											@Override
											public void onShowcaseViewHide(ShowcaseView showcaseView) {
												//spiegazione di come salvare
								        		ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
								        		co.buttonLayoutParams = lps;
										   		showcaseView = ShowcaseView.insertShowcaseView(
										        		new ViewTarget(R.id.button_save_exit, CreaListaActivity.this)
										        		, CreaListaActivity.this
										        		, R.string.list_save_exit
										        		, R.string.showcase_saveexit_desc
										        		, co);
												showcaseView.setButtonText(getString(R.string.showcase_button_next));
												showcaseView.setScaleMultiplier(0.7f);
												showcaseView.setOnShowcaseEventListener(new OnShowcaseEventListener() {
		
													@Override
													public void onShowcaseViewShow(ShowcaseView showcaseView) { }
													
													@Override
													public void onShowcaseViewHide(ShowcaseView showcaseView) {
														//spiegazione di come rivedere il tutorial
														ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
														co.buttonLayoutParams = lps;	
												        ActionItemTarget target = new ActionItemTarget(CreaListaActivity.this, R.id.action_help);
												        showcaseView = ShowcaseView.insertShowcaseView(target
												        		, CreaListaActivity.this
												        		, R.string.showcase_end_title
												        		, R.string.showcase_help_general
												        		,co);
												        showcaseView.setScaleMultiplier(0.3f);
														showcaseView.setOnShowcaseEventListener(new OnShowcaseEventListener() {
		
															@Override
															public void onShowcaseViewShow(ShowcaseView showcaseView) { }
															
															@Override
															public void onShowcaseViewHide(ShowcaseView showcaseView) {
																if (fakeItemCreated) {
																	nomiElementi.remove(0);
																	adapter.notifyDataSetChanged();
																	fakeItemCreated = false;
																}
																setRequestedOrientation(prevOrientation);
															}		
															@Override
															public void onShowcaseViewDidHide(ShowcaseView showcaseView) { }
														});		
													}
													
													@Override
													public void onShowcaseViewDidHide(ShowcaseView showcaseView) { }
												});
											}
											
											@Override
											public void onShowcaseViewDidHide(ShowcaseView showcaseView) { }
										});
									}			
									@Override
									public void onShowcaseViewDidHide(ShowcaseView showcaseView) { }
								});
							}	
							@Override
							public void onShowcaseViewDidHide(ShowcaseView showcaseView) { }
						});
					}
					@Override
					public void onShowcaseViewDidHide(ShowcaseView showcaseView) { }
				});
            }
			@Override
			public void onShowcaseViewDidHide(ShowcaseView showcaseView) { }
		});
   	}
}
