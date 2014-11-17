package it.cammino.risuscito;

import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ShareActionProvider;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.widgets.SnackBar;

public class CantiEucarestiaFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	
	private int posizioneDaCanc;
	private String titoloDaCanc;
	private View rootView;
	private ShareActionProvider mShareActionProvider;
	private DatabaseCanti listaCanti;
	private SQLiteDatabase db;
	private String[] titoliCanti;
	private int prevOrientation;
	
//	private final String RIMUOVI_CANTO_TAG = "1";
//	private final String RESETTA_LISTA_TAG = "2";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(
		R.layout.activity_canti_eucarestia, container, false);
		
		//crea un istanza dell'oggetto DatabaseCanti
		listaCanti = new DatabaseCanti(getActivity());
		
		rootView.findViewById(R.id.button_floating_action).setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				blockOrientation();
//				GenericDialogFragment dialog = new GenericDialogFragment();
//				dialog.setCustomMessage(getString(R.string.reset_list_question));
//				dialog.setListener(CantiEucarestiaFragment.this);
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
//                dialog.show(getChildFragmentManager(), RESETTA_LISTA_TAG);
//                dialog.setCancelable(false);
                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.dialog_reset_list_title)
                .content(R.string.reset_list_question)
                .positiveText(R.string.confirm)  // the default is 'Accept', this line could be left out
                .negativeText(R.string.dismiss)  // leaving this line out will remove the negative button
                .callback(new MaterialDialog.FullCallback() {
                	@Override
                	public void onPositive(MaterialDialog dialog) {
                		db = listaCanti.getReadableDatabase();
                		String sql = "DELETE FROM CUST_LISTS" +
                				" WHERE _id =  2 ";
                		db.execSQL(sql);
                		db.close();
                		updateLista();
                		mShareActionProvider.setShareIntent(getDefaultIntent());
                		getActivity().setRequestedOrientation(prevOrientation);
                	}

                	@Override
                	public void onNeutral(MaterialDialog dialog) {}

                	@Override
                	public void onNegative(MaterialDialog dialog) {
                		getActivity().setRequestedOrientation(prevOrientation);
                	}
                })
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
		});
			
		updateLista();
		
		setHasOptionsMenu(true);
		
		return rootView;
	}
		   
    @Override
    public void onResume() {
//    	Log.i("CANTI EUCARESTIA", "ON RESUME");
    	super.onResume();
		updateLista();
//		ViewPager tempPager = (ViewPager) getActivity().findViewById(R.id.view_pager);
//		Log.i("CURRENT ITEM EUCARESTIA", tempPager.getCurrentItem()+"");
//		if (mShareActionProvider != null && tempPager.getCurrentItem() == 1)
//			mShareActionProvider.setShareIntent(getDefaultIntent());
			
    }
    
	@Override
	public void onDestroy() {
		if (listaCanti != null)
			listaCanti.close();
		super.onDestroy();
	}
	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    MenuItem shareItem = menu.findItem(R.id.action_share);
	    mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
	    ViewPager tempPager = (ViewPager) getActivity().findViewById(R.id.view_pager);
	    if (mShareActionProvider != null && tempPager.getCurrentItem() == 1)
	    	mShareActionProvider.setShareIntent(getDefaultIntent());
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	private Intent getDefaultIntent() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, getTitlesList());
		intent.setType("text/plain");
		return intent;
	}
	
    private void startSubActivity(Bundle bundle) {
    	Intent intent = new Intent(getActivity(), GeneralInsertSearch.class);
    	intent.putExtras(bundle);
    	startActivity(intent);
    	getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold_on);
   	}
    
    private void openPagina(View v) {
    	// recupera il titolo della voce cliccata
		String cantoCliccato = ((TextView) v).getText().toString();
		cantoCliccato = Utility.duplicaApostrofi(cantoCliccato);
        		
		// crea un manipolatore per il DB in modalit� READ
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
    	getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold_on);
    }
    
    private void updateLista() {
        	
		String[] titoloCanto = getTitoliFromPosition(1);
		
		if (titoloCanto.length == 0) {
			rootView.findViewById(R.id.addCantoIniziale1).setVisibility(View.VISIBLE);
			rootView.findViewById(R.id.cantoIniziale1).setVisibility(View.GONE);
			
			rootView.findViewById(R.id.addCantoIniziale1).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
				    Bundle bundle = new Bundle();
				    bundle.putInt("fromAdd", 1);
				    bundle.putInt("idLista", 2);
				    bundle.putInt("position", 1);
				    startSubActivity(bundle);
				}
			});
		}
		else {
			TextView temp = (TextView) rootView.findViewById(R.id.cantoIniziale1);
			rootView.findViewById(R.id.addCantoIniziale1).setVisibility(View.GONE);
			temp.setVisibility(View.VISIBLE);
			temp.setText(titoloCanto[0].substring(7));
			((View) rootView.findViewById(R.id.cantoIniziale1Container))
			.setBackgroundColor(Color.parseColor(titoloCanto[0].substring(0,7)));
			
			rootView.findViewById(R.id.cantoIniziale1).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					openPagina(v);
				}
			});
			
			// setta l'azione tenendo premuto sul canto
	   		rootView.findViewById(R.id.cantoIniziale1).setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
//					blockOrientation();
					posizioneDaCanc = 1;
					titoloDaCanc = Utility.duplicaApostrofi(((TextView) view).getText().toString());
//					GenericDialogFragment dialog = new GenericDialogFragment();
//					dialog.setCustomMessage(getString(R.string.list_remove));
//					dialog.setListener(CantiEucarestiaFragment.this);
//					dialog.setOnKeyListener(new Dialog.OnKeyListener() {
//
//			            @Override
//			            public boolean onKey(DialogInterface arg0, int keyCode,
//			                    KeyEvent event) {
//			                if (keyCode == KeyEvent.KEYCODE_BACK
//			                		&& event.getAction() == KeyEvent.ACTION_UP) {
//			                    arg0.dismiss();
//								getActivity().setRequestedOrientation(prevOrientation);
//								return true;
//			                }
//			                return false;
//			            }
//			        });
//	                dialog.show(getChildFragmentManager(), RIMUOVI_CANTO_TAG);
//	                dialog.setCancelable(false);
					snackBarRimuoviCanto();
					return false;
				}
			});
		}
		
		SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean showSeconda = pref.getBoolean("showSecondaEucarestia", false);
		
		if (showSeconda) {
			
			rootView.findViewById(R.id.groupCantoSeconda).setVisibility(View.VISIBLE);
			
			titoloCanto = getTitoliFromPosition(6);
			
			if (titoloCanto.length == 0) {
				rootView.findViewById(R.id.addCantoSeconda).setVisibility(View.VISIBLE);
				rootView.findViewById(R.id.cantoSeconda).setVisibility(View.GONE);
				
				rootView.findViewById(R.id.addCantoSeconda).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
					    Bundle bundle = new Bundle();
					    bundle.putInt("fromAdd", 1);
					    bundle.putInt("idLista", 2);
					    bundle.putInt("position", 6);
					    startSubActivity(bundle);
					}
				});
			}
			else {
				TextView temp = (TextView) rootView.findViewById(R.id.cantoSeconda);
				rootView.findViewById(R.id.addCantoSeconda).setVisibility(View.GONE);
				temp.setVisibility(View.VISIBLE);
				temp.setText(titoloCanto[0].substring(7));
				((View) rootView.findViewById(R.id.cantoSecondaContainer))
				.setBackgroundColor(Color.parseColor(titoloCanto[0].substring(0,7)));
				
				rootView.findViewById(R.id.cantoSeconda).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						openPagina(v);
					}
				});
				
				// setta l'azione tenendo premuto sul canto
		   		rootView.findViewById(R.id.cantoSeconda).setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View view) {
//						blockOrientation();
						posizioneDaCanc = 6;
						titoloDaCanc = Utility.duplicaApostrofi(((TextView) view).getText().toString());
//						GenericDialogFragment dialog = new GenericDialogFragment();
//						dialog.setCustomMessage(getString(R.string.list_remove));
//						dialog.setListener(CantiEucarestiaFragment.this);
//						dialog.setOnKeyListener(new Dialog.OnKeyListener() {
//
//				            @Override
//				            public boolean onKey(DialogInterface arg0, int keyCode,
//				                    KeyEvent event) {
//				                if (keyCode == KeyEvent.KEYCODE_BACK
//				                		&& event.getAction() == KeyEvent.ACTION_UP) {
//				                    arg0.dismiss();
//									getActivity().setRequestedOrientation(prevOrientation);
//									return true;
//				                }
//				                return false;
//				            }
//				        });
//		                dialog.show(getChildFragmentManager(), RIMUOVI_CANTO_TAG);
//		                dialog.setCancelable(false);
						snackBarRimuoviCanto();
						return false;
					}
				});
			}
    	}
		else
			rootView.findViewById(R.id.groupCantoSeconda).setVisibility(View.GONE);
		
		titoloCanto = getTitoliFromPosition(2);
		
		if (titoloCanto.length == 0) {
			rootView.findViewById(R.id.addCantoPace).setVisibility(View.VISIBLE);
			rootView.findViewById(R.id.cantoPace).setVisibility(View.GONE);
			
			rootView.findViewById(R.id.addCantoPace).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
				    Bundle bundle = new Bundle();
				    bundle.putInt("fromAdd", 1);
				    bundle.putInt("idLista", 2);
				    bundle.putInt("position", 2);
				    startSubActivity(bundle);
				}
			});
		}
		else {
			TextView temp = (TextView) rootView.findViewById(R.id.cantoPace);
			rootView.findViewById(R.id.addCantoPace).setVisibility(View.GONE);
			temp.setVisibility(View.VISIBLE);
			temp.setText(titoloCanto[0].substring(7));
			((View) rootView.findViewById(R.id.cantoPaceContainer))
			.setBackgroundColor(Color.parseColor(titoloCanto[0].substring(0,7)));
			
			rootView.findViewById(R.id.cantoPace).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					openPagina(v);
				}
			});
			
			// setta l'azione tenendo premuto sul canto
	   		rootView.findViewById(R.id.cantoPace).setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {		
//					blockOrientation();
					posizioneDaCanc = 2;
					titoloDaCanc = Utility.duplicaApostrofi(((TextView) view).getText().toString());
//					GenericDialogFragment dialog = new GenericDialogFragment();
//					dialog.setCustomMessage(getString(R.string.list_remove));
//					dialog.setListener(CantiEucarestiaFragment.this);
//					dialog.setOnKeyListener(new Dialog.OnKeyListener() {
//
//			            @Override
//			            public boolean onKey(DialogInterface arg0, int keyCode,
//			                    KeyEvent event) {
//			                if (keyCode == KeyEvent.KEYCODE_BACK
//			                		&& event.getAction() == KeyEvent.ACTION_UP) {
//			                    arg0.dismiss();
//								getActivity().setRequestedOrientation(prevOrientation);
//								return true;
//			                }
//			                return false;
//			            }
//			        });
//	                dialog.show(getChildFragmentManager(), RIMUOVI_CANTO_TAG);
//	                dialog.setCancelable(false);
					snackBarRimuoviCanto();
					return false;
				}
			});
		}
		
		titoliCanti = getTitoliFromPosition(3);
		
		LinearLayout lv = (LinearLayout) rootView.findViewById(R.id.cantiPaneList);
		lv.removeAllViews();
		
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		for (int current = 0; current < titoliCanti.length; current++) {
//			View view = inflater.inflate(R.layout.canto_added, null, false);
			View view = inflater.inflate(R.layout.canto_added, lv, false);

		   //initialize the view
	   		((TextView) view.findViewById(R.id.canto))
	   			.setText(titoliCanti[current].substring(7));
			 
	   		String colore = titoliCanti[current].substring(0, 7);
	   		view.findViewById(R.id.canto_container).
	   				setBackgroundColor(Color.parseColor(colore));		   
		   
	   		view.findViewById(R.id.canto).setOnClickListener(new OnClickListener() {
			      @Override
			      public void onClick(View v) {
			    	  openPagina(v.findViewById(R.id.canto));
			      }
	   		});
		   		
			// setta l'azione tenendo premuto sul canto
	   		view.findViewById(R.id.canto).setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
//					blockOrientation();
					posizioneDaCanc = 3;
					titoloDaCanc = Utility.duplicaApostrofi(((TextView) view).getText().toString());
//					GenericDialogFragment dialog = new GenericDialogFragment();
//					dialog.setCustomMessage(getString(R.string.list_remove));
//					dialog.setListener(CantiEucarestiaFragment.this);
//					dialog.setOnKeyListener(new Dialog.OnKeyListener() {
//
//			            @Override
//			            public boolean onKey(DialogInterface arg0, int keyCode,
//			                    KeyEvent event) {
//			                if (keyCode == KeyEvent.KEYCODE_BACK
//			                		&& event.getAction() == KeyEvent.ACTION_UP) {
//			                    arg0.dismiss();
//								getActivity().setRequestedOrientation(prevOrientation);
//								return true;
//			                }
//			                return false;
//			            }
//			        });
//	                dialog.show(getChildFragmentManager(), RIMUOVI_CANTO_TAG);
//	                dialog.setCancelable(false);
					snackBarRimuoviCanto();
					return false;
				}
			});
	   		
	   		lv.addView(view);
		}
		
		titoliCanti = getTitoliFromPosition(4);
		
		lv = (LinearLayout) rootView.findViewById(R.id.cantiVinoList);
		lv.removeAllViews();
		
		inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		for (int current = 0; current < titoliCanti.length; current++) {
//			View view = inflater.inflate(R.layout.canto_added, null, false);
			View view = inflater.inflate(R.layout.canto_added, lv, false);

			//initialize the view	   		
	   		((TextView) view.findViewById(R.id.canto))
	   			.setText(titoliCanti[current].substring(7));
			
	   		String colore = titoliCanti[current].substring(0, 7);
	   		view.findViewById(R.id.canto_container).
   					setBackgroundColor(Color.parseColor(colore));
	   		
	   		// setta l'azione al click sul canto
	   		view.findViewById(R.id.canto).setOnClickListener(new OnClickListener() {
		      @Override
		      public void onClick(View v) {
		    	  openPagina(v.findViewById(R.id.canto));
		      }
	   		});
	   		
			// setta l'azione tenendo premuto sul canto
	   		view.findViewById(R.id.canto).setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
//					blockOrientation();
					posizioneDaCanc = 4;
					titoloDaCanc = Utility.duplicaApostrofi(((TextView) view).getText().toString());
//					GenericDialogFragment dialog = new GenericDialogFragment();
//					dialog.setCustomMessage(getString(R.string.list_remove));
//					dialog.setListener(CantiEucarestiaFragment.this);
//					dialog.setOnKeyListener(new Dialog.OnKeyListener() {
//
//			            @Override
//			            public boolean onKey(DialogInterface arg0, int keyCode,
//			                    KeyEvent event) {
//			                if (keyCode == KeyEvent.KEYCODE_BACK
//			                		&& event.getAction() == KeyEvent.ACTION_UP) {
//			                    arg0.dismiss();
//								getActivity().setRequestedOrientation(prevOrientation);
//								return true;
//			                }
//			                return false;
//			            }
//			        });
//	                dialog.show(getChildFragmentManager(), RIMUOVI_CANTO_TAG);
//	                dialog.setCancelable(false);
					snackBarRimuoviCanto();
					return false;	
				}
			});
	   		
		   lv.addView(view);
		}
		
		titoloCanto = getTitoliFromPosition(5);
		
		if (titoloCanto.length == 0) {
			rootView.findViewById(R.id.addCantoFinale1).setVisibility(View.VISIBLE);
			rootView.findViewById(R.id.cantoFinale1).setVisibility(View.GONE);
			
			rootView.findViewById(R.id.addCantoFinale1).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
				    Bundle bundle = new Bundle();
				    bundle.putInt("fromAdd", 1);
				    bundle.putInt("idLista", 2);
				    bundle.putInt("position", 5);
				    startSubActivity(bundle);
				}
			});
		}
		else {
			TextView temp = (TextView) rootView.findViewById(R.id.cantoFinale1);
			rootView.findViewById(R.id.addCantoFinale1).setVisibility(View.GONE);
			temp.setVisibility(View.VISIBLE);
			temp.setText(titoloCanto[0].substring(7));
			((View) rootView.findViewById(R.id.cantoFinale1Container))
			.setBackgroundColor(Color.parseColor(titoloCanto[0].substring(0,7)));
			
			rootView.findViewById(R.id.cantoFinale1).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					openPagina(v);
				}
			});
			
			// setta l'azione tenendo premuto sul canto
	   		rootView.findViewById(R.id.cantoFinale1).setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
//					blockOrientation();
					posizioneDaCanc = 5;
					titoloDaCanc = Utility.duplicaApostrofi(((TextView) view).getText().toString());
//					GenericDialogFragment dialog = new GenericDialogFragment();
//					dialog.setCustomMessage(getString(R.string.list_remove));
//					dialog.setListener(CantiEucarestiaFragment.this);
//					dialog.setOnKeyListener(new Dialog.OnKeyListener() {
//
//			            @Override
//			            public boolean onKey(DialogInterface arg0, int keyCode,
//			                    KeyEvent event) {
//			                if (keyCode == KeyEvent.KEYCODE_BACK
//			                		&& event.getAction() == KeyEvent.ACTION_UP) {
//			                    arg0.dismiss();
//								getActivity().setRequestedOrientation(prevOrientation);
//								return true;
//			                }
//			                return false;
//			            }
//			        });
//	                dialog.show(getChildFragmentManager(), RIMUOVI_CANTO_TAG);
//	                dialog.setCancelable(false);
					snackBarRimuoviCanto();
					return false;
				}
			});
		}
		
		rootView.findViewById(R.id.addCantoPane).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    Bundle bundle = new Bundle();
			    bundle.putInt("fromAdd", 1);
			    bundle.putInt("idLista", 2);
			    bundle.putInt("position", 3);
			    startSubActivity(bundle);
			}
		});
		
		rootView.findViewById(R.id.addCantoVino).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    Bundle bundle = new Bundle();
			    bundle.putInt("fromAdd", 1);
			    bundle.putInt("idLista", 2);
			    bundle.putInt("position", 4);
			    startSubActivity(bundle);
			}
		});
    
	}
    
    private String getTitlesList() {
    	
    	Locale l = Locale.getDefault();
    	String result = "";
    	String[] temp = null;
    	
    	//titolo
    	result +=  "-- CELEBRAZIONE DELL\'EUCARESTIA --\n";
    	
    	//canto iniziale
    	temp = getTitoloToSendFromPosition(1);
    	
    	result += getResources().getString(R.string.canto_iniziale).toUpperCase(l);
    	result += "\n";
    	
    	if (temp[0] == null || temp[0].equalsIgnoreCase(""))
    		result += ">> da scegliere <<";
    	else
    		result += temp[0];
    	
    	result += "\n";
    	
    	//deve essere messa anche la seconda lettura? legge le impostazioni
		SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean showSeconda = pref.getBoolean("showSecondaEucarestia", false);
    	
		if (showSeconda) {
    	//canto alla seconda lettura
	    	temp = getTitoloToSendFromPosition(6);
	    	
	    	result += getResources().getString(R.string.seconda_lettura).toUpperCase(l);
	    	result += "\n";
	    	
	    	if (temp[0] == null || temp[0].equalsIgnoreCase(""))
	    		result += ">> da scegliere <<";
	    	else
	    		result += temp[0];
	    	
	    	result += "\n";
		}
//		else
//			Log.i("SECONDA LETTURA", "IGNORATA");
		
    	//canto alla pace
    	temp = getTitoloToSendFromPosition(2);
    	
    	result += getResources().getString(R.string.canto_pace).toUpperCase(l);
    	result += "\n";
    	
    	if (temp[0] == null || temp[0].equalsIgnoreCase(""))
    		result += ">> da scegliere <<";
    	else
    		result += temp[0];
    	
    	result += "\n";
    	
    	//canti al pane
    	temp = getTitoloToSendFromPosition(3);;
    	
    	result += getResources().getString(R.string.canto_pane).toUpperCase(l);
    	result += "\n";
    	
	    if (temp[0] == null || temp[0].equalsIgnoreCase("")) {
			result += ">> da scegliere <<";
			result += "\n";
	    }
		else {
	    	for (int i = 0; i < temp.length; i++) {
		    	if (temp[i] != null && !temp[i].equalsIgnoreCase("")) {
		    		result += temp[i];
		    		result += "\n";
		    	}
		    	else
		    		break;
	    	}
		}
    	
    	//canti al vino
    	temp = getTitoloToSendFromPosition(4);
    	
    	result += getResources().getString(R.string.canto_vino).toUpperCase(l);
    	result += "\n";
    	
	    if (temp[0] == null || temp[0].equalsIgnoreCase("")) {
			result += ">> da scegliere <<";
			result += "\n";
	    }
		else {
	    	for (int i = 0; i < temp.length; i++) {
		    	if (temp[i] != null && !temp[i].equalsIgnoreCase("")) {
		    		result += temp[i];
		    		result += "\n";
		    	}
		    	else
		    		break;
	    	}
		}
    	
    	//canto finale
    	temp = getTitoloToSendFromPosition(5);
    	
    	result += getResources().getString(R.string.canto_fine).toUpperCase(l);
    	result += "\n";
    	
    	if (temp[0] == null || temp[0].equalsIgnoreCase(""))
    		result += ">> da scegliere <<";
    	else
    		result += temp[0];	    	
    	    	
    	return result;
    	
    }
    
    private String[] getTitoliFromPosition(int position) {
		
    	db = listaCanti.getReadableDatabase();
    	
	    String query = "SELECT B.titolo, color" +
	      		"  FROM CUST_LISTS A" +
	      		"  	   , ELENCO B" +
	      		"  WHERE A._id = 2" +
	      		"  AND   A.position = " + position + 
	      		"  AND   A.id_canto = B._id" +
	      		"  ORDER BY A.timestamp ASC";
	    Cursor cursor = db.rawQuery(query, null);
	     
	    int total = cursor.getCount();
	    
	    String[] result = new String[total];   

	    cursor.moveToFirst();
	    for (int i = 0; i < total; i++) {
    		result[i] =  cursor.getString(1) + cursor.getString(0);
    		cursor.moveToNext();
	    }
	    
	    cursor.close();
	    db.close();
    
	    return result;
    }
    
    //recupera il titolo del canto in posizione "position" nella lista 2
    private String[] getTitoloToSendFromPosition(int position) {
		
    	db = listaCanti.getReadableDatabase();
    	
	    String query = "SELECT B.titolo, B.pagina" +
	      		"  FROM CUST_LISTS A" +
	      		"  	   , ELENCO B" +
	      		"  WHERE A._id = 2" +
	      		"  AND   A.position = " + position + 
	      		"  AND   A.id_canto = B._id" +
	      		"  ORDER BY A.timestamp ASC";
	    Cursor cursor = db.rawQuery(query, null);
	     
	    int total = cursor.getCount();
	    int resultLen = 1;
	    if (total > 1)
	    	resultLen = total;
	    
	    String[] result = new String[resultLen];
	    
	    cursor.moveToFirst();
	    for (int i = 0; i < total; i++) {
	    	result[i] =  cursor.getString(0) + " - PAG." + cursor.getInt(1);
	    	cursor.moveToNext();
	    }
	    
	    cursor.close();
	    db.close();
    
	    return result;
    }
    
//    @Override
//    public void onDialogPositiveClick(DialogFragment dialog) {  
//    	db = listaCanti.getReadableDatabase();
//    	String sql = "";
//    	
//    	if (dialog.getTag().equals(RIMUOVI_CANTO_TAG)) {	
//			sql = "DELETE FROM CUST_LISTS" +
//	      		"  WHERE _id =  2 " +
//  				"    AND position = " + posizioneDaCanc +
//  				"	 AND id_canto = (SELECT _id FROM ELENCO" +
//  				"					WHERE titolo = '" + titoloDaCanc + "')"; 
//    	}
//    	else if (dialog.getTag().equals(RESETTA_LISTA_TAG)) {
//			sql = "DELETE FROM CUST_LISTS" +
//				" WHERE _id =  2 ";
//    	}
//		db.execSQL(sql);
//		db.close();
//		updateLista();
//		//aggiorna lo share intent usato per condividere la lista
//		mShareActionProvider.setShareIntent(getDefaultIntent());
//		dialog.dismiss();
//		getActivity().setRequestedOrientation(prevOrientation);
//		
//    }
//
//    @Override
//    public void onDialogNegativeClick(DialogFragment dialog) {
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
    
    public void snackBarRimuoviCanto() {
    	SnackBar snackbar = 
    	new SnackBar(getActivity(),
    			getString(R.string.list_remove),
    			getString(R.string.snackbar_remove),
			new OnClickListener() {

			@Override
			public void onClick(View v) {
				db = listaCanti.getReadableDatabase();
				String sql = "DELETE FROM CUST_LISTS" +
			      		"  WHERE _id =  2 " +
		  				"    AND position = " + posizioneDaCanc +
		  				"	 AND id_canto = (SELECT _id FROM ELENCO" +
		  				"					WHERE titolo = '" + titoloDaCanc + "')"; 
				db.execSQL(sql);
				db.close();
				updateLista();
				mShareActionProvider.setShareIntent(getDefaultIntent());
			}
		});
    	snackbar.setColorButton(getResources().getColor(R.color.theme_accent));
    	snackbar.show();
    }
    
}