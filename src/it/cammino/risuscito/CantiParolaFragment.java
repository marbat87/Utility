package it.cammino.risuscito;

import java.util.Locale;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.widgets.SnackBar;

public class CantiParolaFragment extends Fragment {
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
	private int prevOrientation;
	
//	private final String RIMUOVI_CANTO_TAG = "1";
//	private final String RESETTA_LISTA_TAG = "2";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(
				R.layout.activity_canti_parola, container, false);
		
		//crea un istanza dell'oggetto DatabaseCanti
		listaCanti = new DatabaseCanti(getActivity());
		updateLista();
		
		rootView.findViewById(R.id.cantoIniziale).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openPagina(v);
			}
		});
		
		// setta l'azione tenendo premuto sul canto
   		rootView.findViewById(R.id.cantoIniziale).setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
//				blockOrientation();
				posizioneDaCanc = 1;
				titoloDaCanc = Utility.duplicaApostrofi(((TextView) view).getText().toString());
//				GenericDialogFragment dialog = new GenericDialogFragment();
//				dialog.setCustomMessage(getString(R.string.list_remove));
//				dialog.setListener(CantiParolaFragment.this);
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
//				dialog.show(getChildFragmentManager(), RIMUOVI_CANTO_TAG);
//		        dialog.setCancelable(false);
				snackBarRimuoviCanto();
				return false;
			}
		});
		
		rootView.findViewById(R.id.addCantoIniziale).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    Bundle bundle = new Bundle();
			    bundle.putInt("fromAdd", 1);
			    bundle.putInt("idLista", 1);
			    bundle.putInt("position", 1);
			    startSubActivity(bundle);
			}
		});
		
		rootView.findViewById(R.id.primaLettura).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openPagina(v);
			}
		});
		
		// setta l'azione tenendo premuto sul canto
   		rootView.findViewById(R.id.primaLettura).setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
//				blockOrientation();
				posizioneDaCanc = 2;
				titoloDaCanc = Utility.duplicaApostrofi(((TextView) view).getText().toString());
//				GenericDialogFragment dialog = new GenericDialogFragment();
//				dialog.setCustomMessage(getString(R.string.list_remove));
//				dialog.setListener(CantiParolaFragment.this);
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
//                dialog.show(getChildFragmentManager(), RIMUOVI_CANTO_TAG);
//                dialog.setCancelable(false);
				snackBarRimuoviCanto();
				return false;
			}
		});
		
		rootView.findViewById(R.id.addPrimaLettura).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    Bundle bundle = new Bundle();
			    bundle.putInt("fromAdd", 1);
			    bundle.putInt("idLista", 1);
			    bundle.putInt("position", 2);
			    startSubActivity(bundle);
			}
		});
		
		rootView.findViewById(R.id.secondaLettura).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openPagina(v);
			}
		});
		
		// setta l'azione tenendo premuto sul canto
   		rootView.findViewById(R.id.secondaLettura).setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
//				blockOrientation();
				posizioneDaCanc = 3;
				titoloDaCanc = Utility.duplicaApostrofi(((TextView) view).getText().toString());
//				GenericDialogFragment dialog = new GenericDialogFragment();
//				dialog.setCustomMessage(getString(R.string.list_remove));
//				dialog.setListener(CantiParolaFragment.this);
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
//                dialog.show(getChildFragmentManager(), RIMUOVI_CANTO_TAG);
//                dialog.setCancelable(false);
				snackBarRimuoviCanto();
				return false;
			}
		});
		
		rootView.findViewById(R.id.addSecondaLettura).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    Bundle bundle = new Bundle();
			    bundle.putInt("fromAdd", 1);
			    bundle.putInt("idLista", 1);
			    bundle.putInt("position", 3);
			    startSubActivity(bundle);
			}
		});
		
		rootView.findViewById(R.id.terzaLettura).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openPagina(v);
			}
		});
		
		// setta l'azione tenendo premuto sul canto
   		rootView.findViewById(R.id.terzaLettura).setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
//				blockOrientation();
				posizioneDaCanc = 4;
				titoloDaCanc = Utility.duplicaApostrofi(((TextView) view).getText().toString());
//				GenericDialogFragment dialog = new GenericDialogFragment();
//				dialog.setCustomMessage(getString(R.string.list_remove));
//				dialog.setListener(CantiParolaFragment.this);
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
//                dialog.show(getChildFragmentManager(), RIMUOVI_CANTO_TAG);
//                dialog.setCancelable(false);
				snackBarRimuoviCanto();
				return false;
			}
		});
		
		rootView.findViewById(R.id.addTerzaLettura).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    Bundle bundle = new Bundle();
			    bundle.putInt("fromAdd", 1);
			    bundle.putInt("idLista", 1);
			    bundle.putInt("position", 4);
			    startSubActivity(bundle);
			}
		});
		
		rootView.findViewById(R.id.cantoFinale).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openPagina(v);
			}
		});
		
		// setta l'azione tenendo premuto sul canto
   		rootView.findViewById(R.id.cantoFinale).setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
//				blockOrientation();
				posizioneDaCanc = 5;
				titoloDaCanc = Utility.duplicaApostrofi(((TextView) view).getText().toString());
//				GenericDialogFragment dialog = new GenericDialogFragment();
//				dialog.setCustomMessage(getString(R.string.list_remove));
//				dialog.setListener(CantiParolaFragment.this);
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
//                dialog.show(getChildFragmentManager(), RIMUOVI_CANTO_TAG);
//                dialog.setCancelable(false);
				snackBarRimuoviCanto();
				return false;
			}
		});
		
		rootView.findViewById(R.id.addCantoFinale).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    Bundle bundle = new Bundle();
			    bundle.putInt("fromAdd", 1);
			    bundle.putInt("idLista", 1);
			    bundle.putInt("position", 5);
			    startSubActivity(bundle);
			}
		});
		
		rootView.findViewById(R.id.button_floating_action).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				blockOrientation();
//				GenericDialogFragment dialog = new GenericDialogFragment();
//				dialog.setCustomMessage(getString(R.string.reset_list_question));
//				dialog.setListener(CantiParolaFragment.this);
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
                				" WHERE _id =  1 ";
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
                .titleColor(getResources().getColor(android.R.color.black))
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
		
		setHasOptionsMenu(true);
		
		return rootView;
	}
	
    @Override
    public void onResume() {
//    	Log.i("CANTI PAROLA", "ON RESUME");
    	super.onResume();
		updateLista();
//	    ViewPager tempPager = (ViewPager) getActivity().findViewById(R.id.view_pager);
//		Log.i("CURRENT ITEM PAROLA", tempPager.getCurrentItem()+"");
//		if (mShareActionProvider != null && tempPager.getCurrentItem() == 0)
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
	    if (mShareActionProvider != null && tempPager.getCurrentItem() == 0)
	    	mShareActionProvider.setShareIntent(getDefaultIntent());
	    super.onCreateOptionsMenu(menu, inflater);
	}

	private Intent getDefaultIntent() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, getTitlesList());
		intent.setType("text/plain");
		return intent;
   }
	
    private void updateLista() {
        
		String titoloCanto = getTitoloFromPosition(1);
		
		if (titoloCanto.equalsIgnoreCase("")) {
			rootView.findViewById(R.id.addCantoIniziale).setVisibility(View.VISIBLE);
			rootView.findViewById(R.id.cantoIniziale).setVisibility(View.GONE);
		}
		else {
			TextView temp = (TextView) rootView.findViewById(R.id.cantoIniziale);
			rootView.findViewById(R.id.addCantoIniziale).setVisibility(View.GONE);
			temp.setVisibility(View.VISIBLE);
			temp.setText(titoloCanto.substring(7));
			((View) rootView.findViewById(R.id.cantoInizialeContainer))
					.setBackgroundColor(Color.parseColor(titoloCanto.substring(0,7)));
		}
		
		titoloCanto = getTitoloFromPosition(2);
		
		if (titoloCanto.equalsIgnoreCase("")) {
			rootView.findViewById(R.id.addPrimaLettura).setVisibility(View.VISIBLE);
			rootView.findViewById(R.id.primaLettura).setVisibility(View.GONE);
		}
		else {
			TextView temp = (TextView) rootView.findViewById(R.id.primaLettura);
			rootView.findViewById(R.id.addPrimaLettura).setVisibility(View.GONE);
			temp.setVisibility(View.VISIBLE);
			temp.setText(titoloCanto.substring(7));
			((View) rootView.findViewById(R.id.primaLetturaContainer))
			.setBackgroundColor(Color.parseColor(titoloCanto.substring(0,7)));
		}
		
		titoloCanto = getTitoloFromPosition(3);
		
		if (titoloCanto.equalsIgnoreCase("")) {
			rootView.findViewById(R.id.addSecondaLettura).setVisibility(View.VISIBLE);
			rootView.findViewById(R.id.secondaLettura).setVisibility(View.GONE);
		}
		else {
			TextView temp = (TextView) rootView.findViewById(R.id.secondaLettura);
			rootView.findViewById(R.id.addSecondaLettura).setVisibility(View.GONE);
			temp.setVisibility(View.VISIBLE);
			temp.setText(titoloCanto.substring(7));
			((View) rootView.findViewById(R.id.secondaLetturaContainer))
			.setBackgroundColor(Color.parseColor(titoloCanto.substring(0,7)));
		}
		
		titoloCanto = getTitoloFromPosition(4);
		
		if (titoloCanto.equalsIgnoreCase("")) {
			rootView.findViewById(R.id.addTerzaLettura).setVisibility(View.VISIBLE);
			rootView.findViewById(R.id.terzaLettura).setVisibility(View.GONE);
		}
		else {
			TextView temp = (TextView) rootView.findViewById(R.id.terzaLettura);
			rootView.findViewById(R.id.addTerzaLettura).setVisibility(View.GONE);
			temp.setVisibility(View.VISIBLE);
			temp.setText(titoloCanto.substring(7));
			((View) rootView.findViewById(R.id.terzaLetturaContainer))
			.setBackgroundColor(Color.parseColor(titoloCanto.substring(0,7)));
		}
		
		titoloCanto = getTitoloFromPosition(5);
		
		if (titoloCanto.equalsIgnoreCase("")) {
			rootView.findViewById(R.id.addCantoFinale).setVisibility(View.VISIBLE);
			rootView.findViewById(R.id.cantoFinale).setVisibility(View.GONE);
		}
		else {
			TextView temp = (TextView) rootView.findViewById(R.id.cantoFinale);
			rootView.findViewById(R.id.addCantoFinale).setVisibility(View.GONE);
			temp.setVisibility(View.VISIBLE);
			temp.setText(titoloCanto.substring(7));
			((View) rootView.findViewById(R.id.cantoFinaleContainer))
			.setBackgroundColor(Color.parseColor(titoloCanto.substring(0,7)));
		}
		
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
    
    //recupera il titolo del canto in posizione "position" nella lista
    private String getTitoloFromPosition(int position) {
		
    	db = listaCanti.getReadableDatabase();
    	
	    String query = "SELECT B.titolo, color" +
	      		"  FROM CUST_LISTS A" +
	      		"  	   , ELENCO B" +
	      		"  WHERE A._id = 1" +
	      		"  AND   A.position = " + position + 
	      		"  AND   A.id_canto = B._id";
	    Cursor cursor = db.rawQuery(query, null);
	     
	    int total = cursor.getCount();
	    String result = "";
	    
	    if (total == 1) {
	    	cursor.moveToFirst();
	    	result =  cursor.getString(1) + cursor.getString(0);
	    }
	    
	    cursor.close();
	    db.close();
    
	    return result;
    }
    
    private String getTitlesList() {
    	
    	Locale l = Locale.getDefault();
    	String result = "";
    	String temp = "";
    	
    	//titolo
    	result +=  "-- CELEBRAZIONE DELLA PAROLA --\n";
    	
    	//canto iniziale
    	temp = getTitoloToSendFromPosition(1);
    	
    	result += getResources().getString(R.string.canto_iniziale).toUpperCase(l);
    	result += "\n";
    	
    	if (temp.equalsIgnoreCase(""))
    		result += ">> da scegliere <<";
    	else
    		result += temp;
    	
    	result += "\n";
    	
    	//prima lettura
    	temp = getTitoloToSendFromPosition(2);
    	
    	result += getResources().getString(R.string.prima_lettura).toUpperCase(l);
    	result += "\n";
    	
    	if (temp.equalsIgnoreCase(""))
    		result += ">> da scegliere <<";
    	else
    		result += temp;
    	
    	result += "\n";
    	
    	//seconda lettura
    	temp = getTitoloToSendFromPosition(3);
    	
    	result += getResources().getString(R.string.seconda_lettura).toUpperCase(l);
    	result += "\n";
    	
    	if (temp.equalsIgnoreCase(""))
    		result += ">> da scegliere <<";
    	else
    		result += temp;
    	
    	result += "\n";
    	
    	//terza lettura
    	temp = getTitoloToSendFromPosition(4);
    	
    	result += getResources().getString(R.string.terza_lettura).toUpperCase(l);
    	result += "\n";
    	
    	if (temp.equalsIgnoreCase(""))
    		result += ">> da scegliere <<";
    	else
    		result += temp;
    	
    	result += "\n";
    	
    	//canto finale
    	temp = getTitoloToSendFromPosition(5);
    	
    	result += getResources().getString(R.string.canto_fine).toUpperCase(l);
    	result += "\n";
    	
    	if (temp.equalsIgnoreCase(""))
    		result += ">> da scegliere <<";
    	else
    		result += temp;	    	
    	    	
    	return result;
    	
    }
    
    //recupera il titolo del canto in posizione "position" nella lista "list"
    private String getTitoloToSendFromPosition(int position) {
		
    	db = listaCanti.getReadableDatabase();
    	
	    String query = "SELECT B.titolo, B.pagina" +
	      		"  FROM CUST_LISTS A" +
	      		"  	   , ELENCO B" +
	      		"  WHERE A._id = 1" +
	      		"  AND   A.position = " + position + 
	      		"  AND   A.id_canto = B._id";
	    Cursor cursor = db.rawQuery(query, null);
	     
	    int total = cursor.getCount();
	    String result = "";
	    
	    if (total == 1) {
	    	cursor.moveToFirst();
	    	result =  cursor.getString(0) + " - PAG." + cursor.getInt(1);
	    }
	    
	    cursor.close();
	    db.close();
    
	    return result;
    }
    
//    @Override
//    public void onDialogPositiveClick(DialogFragment dialog) {
//		db = listaCanti.getReadableDatabase();
//		String sql = "";
//		if (dialog.getTag().equals(RIMUOVI_CANTO_TAG)) {
//			sql = "DELETE FROM CUST_LISTS" +
//	      		"  WHERE _id =  1 " +
//  				"    AND position = " + posizioneDaCanc +
//  				"	 AND id_canto = (SELECT _id FROM ELENCO" +
//  				"					WHERE titolo = '" + titoloDaCanc + "')";
//		}
//		else if (dialog.getTag().equals(RESETTA_LISTA_TAG)) {
//			sql = "DELETE FROM CUST_LISTS" +
//				" WHERE _id =  1 ";
//		}
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
			      		"  WHERE _id =  1 " +
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