package it.cammino.risuscito;

import it.cammino.risuscito.GenericDialogFragment.GenericDialogListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.ImageButton;
import org.holoeverywhere.widget.RelativeLayout;
import org.holoeverywhere.widget.SeekBar;
import org.holoeverywhere.widget.SeekBar.OnSeekBarChangeListener;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.espian.showcaseview.OnShowcaseEventListener;
import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.targets.ActionItemTarget;
import com.espian.showcaseview.targets.ViewTarget;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class PaginaRenderActivity extends Activity implements GenericDialogListener {
    
	private DatabaseCanti listaCanti;
	private String pagina;
	private int idCanto;
	private static MediaPlayer mediaPlayer;
	private int favoriteFlag;
	private CheckBox favouriteCheckBox;
	ImageButton play_button, stop_button, rewind_button, ff_button, save_file, delete_file, play_scroll, stop_scroll;
	SeekBar scroll_speed_bar;
	TextView speed_text;
	private ProgressDialog loadingMp3;
	private PhoneStateListener phoneStateListener;
	private static OnAudioFocusChangeListener afChangeListener;
	private static AudioManager am;
	private String url;
	private int prevOrientation;
	private String primaNota;
	private String notaSalvata;
	public static String notaCambio;
	private String primoBarre;
	private String barreSalvato;
	private static String barreCambio;

	enum MP_State {
	    Idle, Initialized, Prepared, Started, Paused, 
	    Stopped, PlaybackCompleted, End, Error, Preparing}
	    
	static MP_State mediaPlayerState;
	
	private ProgressDialog mProgressDialog;
	private boolean localFile;
	private String localUrl;
	
	private WebView paginaView;
	private int defaultZoomLevel = 0;
	private int defaultScrollX = 0;
	private int defaultScrollY = 0;
	
	private static final String PREF_FIRST_OPEN = "prima_apertura_new";
	private static final String PREF_FIRST_OPEN_SCROLL = "prima_apertura_scroll";
	
	private Handler mHandler = new Handler();
	final Runnable mScrollDown = new Runnable()
	{
	    public void run()
	    {
	    	try {
	    		paginaView.scrollBy(0, Integer.valueOf(speedValue));
	    	}
	    	catch (NumberFormatException e) {
	    		paginaView.scrollBy(0, 0);
	    	}
	    	
	        mHandler.postDelayed(this, SCROLL_SLEEP);
	    }
	};
	public static String speedValue;
	private int savedSpeed;
	public static boolean scrollPlaying;
	private RelativeLayout.LayoutParams lps;
	
	private final double MAX_SPEED = 50.0;
	private final long SCROLL_SLEEP = 700;
	private final String SAVE_DIALOG_TAG = "1";
	private final String DELETE_DIALOG_TAG = "2";
	private final String SALVA_ACCORDO_TAG = "3";
	
	private ProgressDialog mExportDialog;
	private String localPDFPath;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		Utility.updateTheme(PaginaRenderActivity.this);
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_pagina_render);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        listaCanti = new DatabaseCanti(this);

        // recupera il numero della pagina da visualizzare dal parametro passato dalla chiamata
        Bundle bundle = this.getIntent().getExtras();
        pagina = bundle.getString("pagina");
        idCanto = bundle.getInt("idCanto");
        
        getRecordLinkAndZoom();
        
		paginaView = (WebView) findViewById(R.id.cantoView);
		
		try {
			primaNota = CambioAccordi.recuperaPrimoAccordo(getAssets().open(pagina + ".htm"));
			primoBarre = CambioAccordi.recuperaBarre(getAssets().open(pagina + ".htm"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
    	SQLiteDatabase db = listaCanti.getReadableDatabase();
	    
	    String query = "SELECT saved_tab, saved_barre, saved_speed" +
	      		"  FROM ELENCO" +
	      		"  WHERE _id =  " + idCanto;   
	    Cursor cursor = db.rawQuery(query, null);
	      
	    cursor.moveToFirst();
	    notaSalvata = cursor.getString(0);
	    barreSalvato = cursor.getString(1);
	    savedSpeed = cursor.getInt(2);	    
	    cursor.close();
	    db.close();
	    
        //recupera i pulsanti
        play_button = (ImageButton) findViewById(R.id.play_song);
        stop_button = (ImageButton) findViewById(R.id.stop_song);
        rewind_button = (ImageButton) findViewById(R.id.rewind_song);
        ff_button = (ImageButton) findViewById(R.id.fast_forward_song);
        save_file = (ImageButton) findViewById(R.id.save_file);
        delete_file = (ImageButton) findViewById(R.id.delete_file);
        play_scroll = (ImageButton) findViewById(R.id.play_scroll);
        stop_scroll = (ImageButton) findViewById(R.id.stop_scroll);
        scroll_speed_bar = (SeekBar) findViewById(R.id.speed_seekbar);
        speed_text = (TextView) findViewById(R.id.speed_text);
        
        if (!url.equalsIgnoreCase("")) {
        	
        	am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    		afChangeListener = new OnAudioFocusChangeListener() {
    		    public void onAudioFocusChange(int focusChange) {
    		        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
    		            // Lower the volume
    		        	if (mediaPlayerState == MP_State.Started) {
    		        		mediaPlayer.setVolume(0.1f, 0.1f);
    		        	}
    		        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
    		            // Raise it back to normal
    		        	if (mediaPlayerState == MP_State.Started) {
    		        		mediaPlayer.setVolume(1.0f, 1.0f);
    		        	}
    		        }
    		    }
    		};
        	
    		localUrl = Utility.retrieveMediaFileLink(this, url);
    		
    		if (localUrl.equalsIgnoreCase("")) {
    			save_file.setEnabled(true);
    			save_file.setVisibility(View.VISIBLE);
    			delete_file.setEnabled(false);
    			delete_file.setVisibility(View.GONE);
    		}
    		else {
    			save_file.setEnabled(false);
    			save_file.setVisibility(View.GONE);
    			delete_file.setEnabled(true);
    			delete_file.setVisibility(View.VISIBLE);
    		}
    		
        	if (mediaPlayer == null) {
	    		mediaPlayer = new MediaPlayer();
	    		mediaPlayerState = MP_State.Idle;
	    		mediaPlayer.setOnErrorListener(mediaPlayerOnErrorListener);
	    		
	        	//mostra i pulsanti per il lettore musicale
	        	play_button.setVisibility(View.VISIBLE);
	        	stop_button.setVisibility(View.VISIBLE);
	        	rewind_button.setVisibility(View.VISIBLE);
	        	ff_button.setVisibility(View.VISIBLE);
	        	((ImageView) findViewById(R.id.no_record_message)).setVisibility(View.GONE);
	    		

	    		if (localUrl.equalsIgnoreCase("")) {
	    			localFile = false;
	    			cmdSetDataSource(url);
	    		}
	    		else {
	    			localFile = true;
	    			cmdSetDataSource(localUrl);
	    		}
	        	    	
	        	//disabilita il pulsante non utilizzabili in modalit� stop
	            stop_button.setEnabled(false);
	            rewind_button.setEnabled(false);
	            ff_button.setEnabled(false);
        	}
        	else {
        		switch (mediaPlayerState) {
        		case Started:
            		play_button.setSelected(true);
            		stop_button.setEnabled(true);
            		ff_button.setEnabled(true);
            		rewind_button.setEnabled(true);
            		break;
        		case Paused:
        			play_button.setSelected(false);
        			stop_button.setEnabled(true);
        			ff_button.setEnabled(false);
        			rewind_button.setEnabled(false);
        			break;
        		default:
        			play_button.setSelected(false);
        			stop_button.setEnabled(false);
        			ff_button.setEnabled(false);
        			rewind_button.setEnabled(false);
        			break;
        		}
        	}
        			
            
            // aggiunge il clicklistener sul pulsante play
            play_button.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				
    				//controlla la presenza di una connessione internet
    				if (!Utility.isOnline(PaginaRenderActivity.this) 
    						&& !localFile)  {
    					Toast toast = Toast.makeText(PaginaRenderActivity.this
    							, getString(R.string.no_connection), Toast.LENGTH_SHORT);
    					toast.show();
    					return;
    				}
    				
    				switch (mediaPlayerState) {
    					case Paused:
    						cmdStart();
    						break;
    					case Started:
    						cmdPause();
    						break;
    					case Initialized:
    						cmdPrepare();
    						break;
    					case Stopped:
    					case PlaybackCompleted:
    					default:
    			    		localUrl = Utility.retrieveMediaFileLink(getApplicationContext(), url);
    			    		if (localUrl.equalsIgnoreCase("")) {
    			    			localFile = false;
    			    			cmdSetDataSource(url);
    			    			save_file.setEnabled(true);
    			    			save_file.setVisibility(View.VISIBLE);
    			    			delete_file.setEnabled(false);
    			    			delete_file.setVisibility(View.GONE);
    			    			
    			    		}
    			    		else {
    			    			localFile = true;
    			    			cmdSetDataSource(localUrl);
    			    			save_file.setEnabled(false);
    			    			save_file.setVisibility(View.GONE);
    			    			delete_file.setEnabled(true);
    			    			delete_file.setVisibility(View.VISIBLE);
    			    		}
    			    		
    			    		if (mediaPlayerState == MP_State.Initialized)
    			    			cmdPrepare();
    						break;
    				}
    			}
            });
            
            // setta il comportamento al click sul pulsante stop
            stop_button.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				cmdStop();
    			}
    		});
            
            // tenendo premuto il pulsante fast forward, si va avanti veloce
            ff_button.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    final Runnable r = new Runnable() {
                        
                    	public void run() {

     						int currentPosition = mediaPlayer.getCurrentPosition();
     						// check if seekForward time is lesser than song duration
     						if (currentPosition + 5000 <= mediaPlayer.getDuration()) {
     							// forward song
     							mediaPlayer.seekTo(currentPosition + 5000);
     						} else {
     							// forward to end position
     							mediaPlayer.seekTo(mediaPlayer.getDuration());
     						}
                        	
                            if(ff_button.isPressed()){
                            	ff_button.postDelayed(this, 1000); //delayed for 1 sec
                            }else{

                                ff_button.postInvalidate();
                                ff_button.invalidate();
                            }
                        }
                    };

                    ff_button.post(r);

                    return true;
                }
            });
			
            // tenendo premuto il pulsante rewind, si ravvolge
            rewind_button.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    final Runnable r = new Runnable() {
                        
                    	public void run() {

    			        	int currentPosition = mediaPlayer.getCurrentPosition();
    			        	// check if seekBackward time is greater than 0 sec
    			            if (currentPosition - 5000 >= 0) {
    			                // forward song
    			                mediaPlayer.seekTo(currentPosition - 5000);
    			            } else {
    			                // backward to starting position
    			                mediaPlayer.seekTo(0);
    			            }
                        	
                            if(rewind_button.isPressed()){
                            	rewind_button.postDelayed(this, 1000); //delayed for 1 sec
                            }else{

                            	rewind_button.postInvalidate();
                            	rewind_button.invalidate();
                            }
                        }
                    };

                    rewind_button.post(r);

                    return true;
                }
			});
            
            phoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                    	 //Incoming call: Pause music
                    	if (mediaPlayerState == MP_State.Started)
                    		cmdPause();
                    } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    	//A call is dialing, active or on hold
                    	if (mediaPlayerState == MP_State.Started)
                    		cmdPause();
                    }
                    super.onCallStateChanged(state, incomingNumber);
                }
            };
            TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if(mgr != null) {
                mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
            
            save_file.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					blockOrientation();
					GenericDialogFragment dialog = new GenericDialogFragment();
					dialog.setCustomMessage(getString(R.string.dialog_download_mp3));
					dialog.setListener(PaginaRenderActivity.this);
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
	                dialog.show(getSupportFragmentManager(), SAVE_DIALOG_TAG);
	                dialog.setCancelable(false);
				}
			});

            delete_file.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					blockOrientation();
					GenericDialogFragment dialog = new GenericDialogFragment();
					dialog.setCustomMessage(getString(R.string.dialog_delete_mp3));
					dialog.setListener(PaginaRenderActivity.this);
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
	                dialog.show(getSupportFragmentManager(), DELETE_DIALOG_TAG);
	                dialog.setCancelable(false);
				}
			});
            
        }
        else {
        	// nasconde i pulsanti
			Toast toast = Toast.makeText(PaginaRenderActivity.this
					, getString(R.string.no_record), Toast.LENGTH_SHORT);
			toast.show();
        	play_button.setVisibility(View.GONE);
        	stop_button.setVisibility(View.GONE);
        	rewind_button.setVisibility(View.GONE);
        	ff_button.setVisibility(View.GONE);
        	save_file.setVisibility(View.GONE);
        	delete_file.setVisibility(View.GONE);
        	((ImageView) findViewById(R.id.no_record_message)).setVisibility(View.VISIBLE);

        }
        
        scroll_speed_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
 
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                speedValue = String.valueOf(progress);
                double tempValue = progress / MAX_SPEED;
                int textValue = (int)(tempValue * 100);
                speed_text.setText(textValue + "%"); 
            }
 
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        stop_scroll.setVisibility(View.GONE);

        play_scroll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				play_scroll.setVisibility(View.GONE);
				stop_scroll.setVisibility(View.VISIBLE);
				scrollPlaying = true;
				mScrollDown.run();
			}
		});
        
        stop_scroll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				play_scroll.setVisibility(View.VISIBLE);
				stop_scroll.setVisibility(View.GONE);
				scrollPlaying = false;
				mHandler.removeCallbacks(mScrollDown);
				
			}
		});
        
        boolean showHelp1 = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(PREF_FIRST_OPEN, true);
        
        boolean showHelp2 = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(PREF_FIRST_OPEN_SCROLL, true);
        
        if(showHelp1) {     
            SharedPreferences.Editor editor = PreferenceManager
                    .getDefaultSharedPreferences(PaginaRenderActivity.this)
                    .edit();
            editor.putBoolean(PREF_FIRST_OPEN, false);
            editor.putBoolean(PREF_FIRST_OPEN_SCROLL, false);
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            	editor.commit();
            } else {
            	editor.apply();
            }
            
            final Runnable mMyRunnable = new Runnable() {
            	@Override
            	public void run() {
            		showHelp(); 
                }
            };
            Handler myHandler = new Handler();
            myHandler.postDelayed(mMyRunnable, 2000);
//     		showHelp(); 
        }
        else {
        	if (showHelp2){
        		SharedPreferences.Editor editor = PreferenceManager
	                    .getDefaultSharedPreferences(PaginaRenderActivity.this)
	                    .edit();
	            editor.putBoolean(PREF_FIRST_OPEN_SCROLL, false);
	            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
	            	editor.commit();
	            } else {
	            	editor.apply();
	            }
        		blockOrientation();
        		showScrollHelp(); 
        	}
        }
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.canto, menu);
		return true;
	}
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
        	if (notaCambio == null || notaSalvata == null
      	   	  || barreCambio == null || barreSalvato == null
      	   	  || (notaCambio.equals(notaSalvata)
      	   	  && barreCambio.equals(barreSalvato))) {
        		pulisciVars();
				finish();
	            return true;
			}
			else {
				blockOrientation();
				GenericDialogFragment dialog = new GenericDialogFragment();
				dialog.setCustomMessage(getString(R.string.dialog_save_tab));
				dialog.setListener(PaginaRenderActivity.this);
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
                dialog.show(getSupportFragmentManager(), SALVA_ACCORDO_TAG);
                dialog.setCancelable(false);
                break;
			}
		case R.id.action_exp_pdf:
			(new PdfExportTask()).execute();
			return true;
		case R.id.action_settings:
			startActivity(new Intent(this, Settings.class));
			return true;
		case R.id.action_about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		case R.id.action_help_canto:
			showHelp();
			return true;
		case R.id.action_save_tab:
			if (!notaSalvata.equalsIgnoreCase(notaCambio)) {
				notaSalvata = notaCambio;
	    		SQLiteDatabase db = listaCanti.getReadableDatabase();
	    		String sql = "UPDATE ELENCO" +
	    				"  SET saved_tab = \'" + notaCambio + "\' " + 
	    				"  WHERE _id =  " + idCanto;
	    		db.execSQL(sql);
	    		db.close();
				Toast toast = Toast.makeText(PaginaRenderActivity.this
						, getString(R.string.tab_saved), Toast.LENGTH_SHORT);
				toast.show();
			}
			else {
				Toast toast = Toast.makeText(PaginaRenderActivity.this
						, getString(R.string.tab_not_saved), Toast.LENGTH_SHORT);
				toast.show();
			}
			return true;
		case R.id.action_reset_tab:
			notaCambio = primaNota;
			HashMap<String, String> convMap = CambioAccordi.diffSemiToni(primaNota, notaCambio);
			SaveZoom();
			if (convMap != null) {
				String nuovoFile = cambiaAccordi(convMap, barreCambio);
				if (nuovoFile != null)
					paginaView.loadUrl("file://" + nuovoFile);
			}
			else {		
				paginaView.loadUrl("file:///android_asset/" + pagina + ".htm");
			}
			if (defaultZoomLevel > 0)
				paginaView.setInitialScale(defaultZoomLevel);
			paginaView.setWebViewClient(new MyWebViewClient());
			return true;
		case R.id.action_save_barre:
			if (!barreSalvato.equalsIgnoreCase(barreCambio)) {
				barreSalvato = barreCambio;
	    		SQLiteDatabase db = listaCanti.getReadableDatabase();
	    		String sql = "UPDATE ELENCO" +
	    				"  SET saved_barre = \'" + barreCambio + "\' " + 
	    				"  WHERE _id =  " + idCanto;
	    		db.execSQL(sql);
	    		db.close();
				Toast toast = Toast.makeText(PaginaRenderActivity.this
						, getString(R.string.barre_saved), Toast.LENGTH_SHORT);
				toast.show();
			}
			else {
				Toast toast = Toast.makeText(PaginaRenderActivity.this
						, getString(R.string.barre_not_saved), Toast.LENGTH_SHORT);
				toast.show();
			}
			return true;
		case R.id.action_reset_barre:
			barreCambio = primoBarre;
			HashMap<String, String> convMap1 = CambioAccordi.diffSemiToni(primaNota, notaCambio);
			SaveZoom();
			if (convMap1 != null) {
				String nuovoFile = cambiaAccordi(convMap1, barreCambio);
				if (nuovoFile != null)
					paginaView.loadUrl("file://" + nuovoFile);
			}
			else {		
				paginaView.loadUrl("file:///android_asset/" + pagina + ".htm");
			}
			if (defaultZoomLevel > 0)
				paginaView.setInitialScale(defaultZoomLevel);
			paginaView.setWebViewClient(new MyWebViewClient());
			return true;
		default:
			if (item.getGroupId() == R.id.menu_gruppo_note) {
				notaCambio = String.valueOf(item.getTitleCondensed());
				HashMap<String, String> convMap2 = CambioAccordi.diffSemiToni(primaNota, notaCambio);
				SaveZoom();
				if (convMap2 != null) {
					String nuovoFile = cambiaAccordi(convMap2, barreCambio);
					if (nuovoFile != null)
						paginaView.loadUrl("file://" + nuovoFile);
				}
				else {		
					paginaView.loadUrl("file:///android_asset/" + pagina + ".htm");
				}
				if (defaultZoomLevel > 0)
					paginaView.setInitialScale(defaultZoomLevel);
				paginaView.setWebViewClient(new MyWebViewClient());
				return true;
			}
			if (item.getGroupId() == R.id.menu_gruppo_barre) {
				barreCambio = String.valueOf(item.getTitleCondensed());
				HashMap<String, String> convMap3 = CambioAccordi.diffSemiToni(primaNota, notaCambio);
				SaveZoom();
				if (convMap3 != null) {
					String nuovoFile = cambiaAccordi(convMap3, barreCambio);
					if (nuovoFile != null)
						paginaView.loadUrl("file://" + nuovoFile);
				}
				else {		
					paginaView.loadUrl("file:///android_asset/" + pagina + ".htm");
				}
				if (defaultZoomLevel > 0)
					paginaView.setInitialScale(defaultZoomLevel);
				paginaView.setWebViewClient(new MyWebViewClient());
				return true;
			}
		}
		return false;
	}
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if (notaCambio == null || notaSalvata == null
        	  || barreCambio == null || barreSalvato == null
        	  || (notaCambio.equals(notaSalvata)
        	  && barreCambio.equals(barreSalvato))) {
        		pulisciVars();
				finish();
	            return true;
        	}
        	else {
        		blockOrientation();
				GenericDialogFragment dialog = new GenericDialogFragment();
				dialog.setCustomMessage(getString(R.string.dialog_save_tab));
				dialog.setListener(PaginaRenderActivity.this);
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
                dialog.show(getSupportFragmentManager(), SALVA_ACCORDO_TAG);
                dialog.setCancelable(false);
                return true;
        	}
        }
        return super.onKeyUp(keyCode, event);
    }
    
    @Override
    public void onResume() {
    	
        favouriteCheckBox = (CheckBox) findViewById(R.id.favorite);
        checkScreenAwake();
    	favoriteFlag = selectFavouriteFromSource(pagina);
        
        if (favoriteFlag == 1)
        	favouriteCheckBox.setChecked(true);
        else
        	favouriteCheckBox.setChecked(false);
        
        favouriteCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {			
				
				buttonView.playSoundEffect(android.view.SoundEffectConstants.CLICK);
				int favouriteFlag = 0;
				if (isChecked) {
					favouriteFlag = 1;
					Toast toast = Toast.makeText(PaginaRenderActivity.this
							, getString(R.string.favorite_added), Toast.LENGTH_SHORT);
					toast.show();
				}
				else {
					Toast toast = Toast.makeText(PaginaRenderActivity.this
							, getString(R.string.favorite_removed), Toast.LENGTH_SHORT);
					toast.show();
				}
				
				Bundle bundle = PaginaRenderActivity.this.getIntent().getExtras();
		        String pagina = bundle.getString("pagina");
				
				updateFavouriteFlag(favouriteFlag, pagina);
			}
		});
        
        if (am != null && mediaPlayerState == MP_State.Started) {
			am.requestAudioFocus(afChangeListener,
	                // Use the music stream.
	                AudioManager.STREAM_MUSIC,
	                // Request permanent focus.
	                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
        }
	        
	    if (notaSalvata == null) {
	    	if (notaCambio == null) {
	    		notaSalvata = notaCambio = primaNota;
	    	}
	    	else {
	    		notaSalvata = primaNota;
	    	}
	    }
	    else {
//	    	Log.i("NOTA SALVATA", notaSalvata);
//	    	Log.i("AVVIO", "notaCambio = " + notaCambio);
	    	if (notaCambio == null) {
	    		notaCambio = notaSalvata;
	    	}
	    }    
	    
	    if (barreSalvato == null) {
	    	if (barreCambio == null) {
	    		barreSalvato = barreCambio = primoBarre;
	    	}
	    	else {
	    		barreSalvato = primoBarre;
	    	}
	    }
	    else {
//	    	Log.i("BARRESALVATO", barreSalvato);
	    	if (barreCambio == null) {
	    		barreCambio = barreSalvato;
	    	}
	    }    
	    
	    if (speedValue == null) {
//	    	Log.i("SONO APPENA ENTRATO", "setto " + savedSpeed);
	    	scroll_speed_bar.setProgress(savedSpeed);
	        speedValue = String.valueOf(scroll_speed_bar.getProgress());
	        double tempValue = scroll_speed_bar.getProgress() / MAX_SPEED;
	        int textValue = (int) (tempValue * 100);
	        speed_text.setText(textValue + "%");
	    }
	    else {
//	    	Log.i("ROTAZIONE", "setto " + speedValue);
	    	scroll_speed_bar.setProgress(Integer.valueOf(speedValue));
	        double tempValue = scroll_speed_bar.getProgress() / MAX_SPEED;
	        int textValue = (int) (tempValue * 100);
	        speed_text.setText(textValue + "%");
	    }
	    
	    if (scrollPlaying == true)
	    	play_scroll.performClick();
	    
		HashMap<String, String> convMap = CambioAccordi.diffSemiToni(primaNota, notaCambio);
		if (convMap != null) {
			String nuovoFile = cambiaAccordi(convMap, barreCambio);
			if (nuovoFile != null)
				paginaView.loadUrl("file://" + nuovoFile);
		}
		else
			paginaView.loadUrl("file:///android_asset/" + pagina + ".htm");
	    
		WebSettings webSettings = paginaView.getSettings();
		webSettings.setUseWideViewPort(true);
		webSettings.setSupportZoom(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setBuiltInZoomControls(true);
		if (defaultZoomLevel > 0)
			paginaView.setInitialScale(defaultZoomLevel);
		paginaView.setWebViewClient(new MyWebViewClient());
        
        super.onResume();
    }
    
	@Override
	public void onDestroy() {
		SaveZoom();
		if (am != null)
			am.abandonAudioFocus(afChangeListener);	
		super.onDestroy();
		listaCanti.close();
	}
    
	public void pulisciVars() {
		SaveZoom();
		
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
			mediaPlayerState = MP_State.Idle;
		}
		TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if(mgr != null)
		    mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);

		
		notaCambio = null;
		barreCambio = null;
		
		SaveSpeed();	
		if (scrollPlaying == true) {
			play_scroll.setVisibility(View.VISIBLE);
			stop_scroll.setVisibility(View.GONE);
			scrollPlaying = false;
			mHandler.removeCallbacks(mScrollDown);
		}
		speedValue = null;
	}
	
    //controlla se l'app deve mantenere lo schermo acceso
    public void checkScreenAwake() {
    	SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this);
		boolean screenOn = pref.getBoolean("screenOn", false);
		if (screenOn)
			favouriteCheckBox.setKeepScreenOn(true);
		else
			favouriteCheckBox.setKeepScreenOn(false);
    }
    
    //recupera il flag preferito per la pagina
    public int selectFavouriteFromSource(String source) {
    	
    	SQLiteDatabase db = listaCanti.getReadableDatabase();
	    
	    String query = "SELECT favourite" +
	      		"  FROM ELENCO" +
	      		"  WHERE _id =  " + idCanto;   
	    Cursor cursor = db.rawQuery(query, null);
	      
	    cursor.moveToFirst();
	    int favouriteFlag = cursor.getInt(0);
	    
	    cursor.close();
	    db.close();
    	return favouriteFlag;
    }
    
    //aggiorna il flag che indica se la pagina � tra i preferiti
    public void updateFavouriteFlag(int favouriteFlag, String source) {
    	
    	SQLiteDatabase db = listaCanti.getReadableDatabase();
    	
		String sql = "UPDATE ELENCO" +
				"  SET favourite = " + favouriteFlag + " " + 
				"  WHERE _id =  " + idCanto;
		db.execSQL(sql);
		db.close();
		
    }
    
    //recuper e setta il record per la registrazione
    private void getRecordLinkAndZoom() {
    	
    	SQLiteDatabase db = listaCanti.getReadableDatabase();
    	
	    String query = "SELECT link, zoom, scroll_x , scroll_y" +
	      		"  FROM ELENCO" +
	      		"  WHERE _id =  " + idCanto;   
	    Cursor cursor = db.rawQuery(query, null);
	      
	    cursor.moveToFirst();
	    if (cursor.getString(0) != null && cursor.getString(0) != "")
	    	url = cursor.getString(0);
	    else
	    	url = "";
	    
	    defaultZoomLevel = cursor.getInt(1);
	    defaultScrollX = cursor.getInt(2);
	    defaultScrollY = cursor.getInt(3);
	    
	    cursor.close();
	    db.close();
	    		
    }
    
    private void cmdSetDataSource(String path){
    	if(mediaPlayerState == MP_State.Idle){
    		try {
    			if (!localFile)
    				mediaPlayer.setDataSource(path);
    			else {
    				FileInputStream fileInputStream = new FileInputStream(path);
    				mediaPlayer.setDataSource(fileInputStream.getFD());         
    				fileInputStream.close();
    			}
    			mediaPlayerState = MP_State.Initialized;
    		} catch (IllegalArgumentException e) {
    			Toast.makeText(PaginaRenderActivity.this, 
    					e.toString(), Toast.LENGTH_SHORT).show();
    			e.printStackTrace();
    		} catch (IllegalStateException e) {
    			Toast.makeText(PaginaRenderActivity.this, 
    					e.toString(), Toast.LENGTH_SHORT).show();
    			e.printStackTrace();
    		} catch (IOException e) {
    			Toast.makeText(PaginaRenderActivity.this, 
    					e.toString(), Toast.LENGTH_SHORT).show();
    			e.printStackTrace();
    		}
    	}else{
    		Toast.makeText(PaginaRenderActivity.this, 
					"Invalid State@cmdSetDataSource - skip", 
					Toast.LENGTH_SHORT).show();
    	}

    	if (mediaPlayerState != MP_State.Initialized)
    		showMediaPlayerState();
    }
    
    private void cmdPrepare(){
    	blockOrientation();
        loadingMp3 = new ProgressDialog(PaginaRenderActivity.this);
        loadingMp3.setMessage(getString(R.string.wait));
        loadingMp3.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				setRequestedOrientation(prevOrientation);
			}
		});
    	loadingMp3.show();
    	mediaPlayer.setOnPreparedListener(mediaPlayerOnPreparedListener);
    	mediaPlayer.setOnCompletionListener(mediaPlayerOnCompletedListener);
    	
    	if(mediaPlayerState == MP_State.Initialized
    		||mediaPlayerState == MP_State.Stopped){
    		try {
    			mediaPlayer.prepareAsync();
    		} catch (IllegalStateException e) {
    			Toast.makeText(PaginaRenderActivity.this, 
    					e.toString(), Toast.LENGTH_SHORT).show();
    			e.printStackTrace();
    		}
    	}else{
    		Toast.makeText(PaginaRenderActivity.this, 
					"Invalid State@cmdPrepare() - skip", 
					Toast.LENGTH_SHORT).show();
    	}
    	
    	if (mediaPlayerState != MP_State.Prepared
    		&& mediaPlayerState != MP_State.Initialized)
    		showMediaPlayerState();
    }
    
    private void cmdStart(){
    	if(mediaPlayerState == MP_State.Prepared
        		||mediaPlayerState == MP_State.Started
        		||mediaPlayerState == MP_State.Paused
        		||mediaPlayerState == MP_State.PlaybackCompleted){
    		
    		//gestisce l'abbassamento del volume in caso di altre riproduzioni (sms, etc.)
    		int result = am.requestAudioFocus(afChangeListener,
                    // Use the music stream.
                    AudioManager.STREAM_MUSIC,
                    // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

    		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
	    		mediaPlayer.start();
	    		play_button.setSelected(true);
	    		stop_button.setEnabled(true);
	    		ff_button.setEnabled(true);
	    		rewind_button.setEnabled(true);
	    		mediaPlayerState = MP_State.Started;
    		}
    		else {
    			Toast.makeText(PaginaRenderActivity.this, 
    					"AudioFocus non consentito", 
    					Toast.LENGTH_SHORT).show();
    		}
    			
    	}else{
    		Toast.makeText(PaginaRenderActivity.this, 
					"Invalid State@cmdStart() - skip", 
					Toast.LENGTH_SHORT).show();
    	}
		showMediaPlayerState();
    }
    
    private void cmdPause(){
    	if(mediaPlayerState == MP_State.Started
        		||mediaPlayerState == MP_State.Paused){
    		mediaPlayer.pause();
    		am.abandonAudioFocus(afChangeListener);
			play_button.setSelected(false);
			stop_button.setEnabled(true);
			ff_button.setEnabled(false);
			rewind_button.setEnabled(false);
    		mediaPlayerState = MP_State.Paused;
    	}else{
    		Toast.makeText(PaginaRenderActivity.this, 
					"Invalid State@cmdPause() - skip", 
					Toast.LENGTH_SHORT).show();
    	}
		showMediaPlayerState();
    }
    
    private void cmdStop(){
		if(mediaPlayerState == MP_State.Started
		||mediaPlayerState == MP_State.Paused) {
			mediaPlayer.stop();
			mediaPlayer.reset();
			am.abandonAudioFocus(afChangeListener);
			play_button.setSelected(false);
			stop_button.setEnabled(false);
			ff_button.setEnabled(false);
			rewind_button.setEnabled(false);
			mediaPlayerState = MP_State.Stopped;
			showMediaPlayerState();
			mediaPlayerState = MP_State.Idle;
    	}else{
    		Toast.makeText(PaginaRenderActivity.this, 
					"Invalid State@cmdStop() - skip", 
					Toast.LENGTH_SHORT).show();
    	}
    }
    
    private void showMediaPlayerState(){
    	
    	String state = "";
    	
    	switch(mediaPlayerState){
    	case Idle:
    		state = "Idle";
    		break;
    	case Initialized:
    		state = "Initialized";
    		break;
    	case Prepared:
    		state = "Prepared";
    		break;
    	case Started:
    		state = "Started";
    		break;
    	case Paused:
    		state = "Paused";
    		break;
    	case Stopped:
    		state = "Stopped";
    		break;
    	case PlaybackCompleted:
    		state = "PlaybackCompleted";
    		break;
    	case End:
    		state = "End";
    		break;
    	case Error:
    		state = "Error";
    		break;
    	case Preparing:
    		state = "Preparing";
    		break;
    	default:
    		state = "Unknown!";
    	}
    	
		Toast toast = Toast.makeText(PaginaRenderActivity.this
				, "Stato del lettore: " + state, Toast.LENGTH_SHORT);
		toast.show();
    }
    
    OnErrorListener mediaPlayerOnErrorListener
    = new OnErrorListener(){

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {			
			try {
				if (loadingMp3.isShowing())
					loadingMp3.dismiss();
			}
			catch (IllegalArgumentException e) {}
			mediaPlayerState = MP_State.Error;
			showMediaPlayerState();			
			return false;
		}
	};
		
	OnPreparedListener mediaPlayerOnPreparedListener
	=  new OnPreparedListener() {
		
		@Override
		public void onPrepared(MediaPlayer mp) {
			try {
				if (loadingMp3.isShowing())
					loadingMp3.dismiss();
			}
			catch (IllegalArgumentException e) {}			
			mediaPlayerState = MP_State.Prepared;
			cmdStart();
		}
	};
	
	OnCompletionListener mediaPlayerOnCompletedListener
	=  new OnCompletionListener() {
		
		@Override
		public void onCompletion(MediaPlayer mp) {
			cmdStop();
			mediaPlayerState = MP_State.PlaybackCompleted;
			showMediaPlayerState();
		}
	};
	
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
    	if (dialog.getTag().equals(SAVE_DIALOG_TAG))  {  		
	    	final DownloadTask downloadTask = new DownloadTask(this);
	    	SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this);
			int saveLocation = Integer.parseInt(pref.getString("saveLocation", "0"));
			if (saveLocation == 1) {
				File[] fileArray = ContextCompat.getExternalFilesDirs(this, null);
				String localFile = fileArray[0].getAbsolutePath()
						+ "/"
						+ Utility.filterMediaLink(url);
				downloadTask.execute(url, localFile);
			}
			else {
				String localFile = this.getFilesDir()
						+ "/"
						+ Utility.filterMediaLink(url);
				downloadTask.execute(url, localFile);
			}
	    	
	    	mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
	    	    @Override
	    	    public void onCancel(DialogInterface dialog) {
	    	        downloadTask.cancel(true);
	    	        setRequestedOrientation(prevOrientation);
	    	    }
	    	});
    	
    	}
    	else if (dialog.getTag().equals(DELETE_DIALOG_TAG))  {         
    		File fileToDelete = new File(localUrl);
    		fileToDelete.delete();
    		Toast.makeText(this, getString(R.string.file_delete), Toast.LENGTH_SHORT).show();
            
            if (mediaPlayerState == MP_State.Started
            		|| mediaPlayerState == MP_State.Paused)
            	cmdStop();
            
            mediaPlayer = new MediaPlayer();
    		mediaPlayerState = MP_State.Idle;
    		mediaPlayer.setOnErrorListener(mediaPlayerOnErrorListener);
            
    		localUrl = Utility.retrieveMediaFileLink(getApplicationContext(), url);
    		if (localUrl.equalsIgnoreCase("")) {
    			localFile = false;
    			cmdSetDataSource(url);
    			save_file.setEnabled(true);
    			save_file.setVisibility(View.VISIBLE);
    			delete_file.setEnabled(false);
    			delete_file.setVisibility(View.GONE);
    		}
    		else {
    			localFile = true;
    			cmdSetDataSource(localUrl);
    			save_file.setEnabled(false);
    			save_file.setVisibility(View.GONE);
    			delete_file.setEnabled(true);
    			delete_file.setVisibility(View.VISIBLE);
    		}
    		dialog.dismiss();
    		setRequestedOrientation(prevOrientation);
    	}
    	else if (dialog.getTag().equals(SALVA_ACCORDO_TAG))  {  		
    		SQLiteDatabase db = listaCanti.getReadableDatabase();
    		String sql = "UPDATE ELENCO" +
    				"  SET saved_tab = \'" + notaCambio + "\' " + 
    				"    , saved_barre = \'" + barreCambio + "\' " + 
    				"  WHERE _id =  " + idCanto;
    		db.execSQL(sql);
    		db.close();
    		pulisciVars();
			finish();
    	}
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    	if (dialog.getTag().equals(SALVA_ACCORDO_TAG)) {
    		pulisciVars();
			finish();
    	}
    	else {
    		dialog.dismiss();
    		setRequestedOrientation(prevOrientation);
    	}
    }

    private void SaveZoom(){
    		defaultZoomLevel = (int) (paginaView.getScale() *100);
    		defaultScrollX = paginaView.getScrollX();
    		defaultScrollY = paginaView.getScrollY();
        	
    		SQLiteDatabase db = listaCanti.getReadableDatabase();
        	
    		String sql = "UPDATE ELENCO" +
    				"  SET zoom = " + defaultZoomLevel + " " + 
    					", scroll_x = " + defaultScrollX + " " + 
    					", scroll_y = " + defaultScrollY + " " +
    				"  WHERE _id =  " + idCanto;
    		db.execSQL(sql);
    		db.close();
    }
    
    private void SaveSpeed(){   	
		SQLiteDatabase db = listaCanti.getReadableDatabase();		
		String sql = "UPDATE ELENCO" +
				"  SET saved_speed = " + speedValue + 
				"  WHERE _id =  " + idCanto;
		db.execSQL(sql);
		db.close();
    }
    
    protected void blockOrientation() {
        prevOrientation = getRequestedOrientation();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }
    }
       
   	private class MyWebViewClient extends WebViewClient {
	    @Override
	    public void onPageFinished(WebView view, String url) {
	    	view.postDelayed(new Runnable() {
	    		@Override
	    		public void run() {
	    			if (defaultScrollX > 0 || defaultScrollY > 0)
	    				paginaView.scrollTo(defaultScrollX, defaultScrollY);
	    		}
	    		// Delay the scrollTo to make it work
	    	}, 500);
	    	super.onPageFinished(view, url);
	    }
	}
   	
   	private String cambiaAccordi(HashMap<String, String> conversione, String barre) {
		String cantoTrasportato = this.getFilesDir() + "/temporaneo.htm";
		
		try {
	        BufferedReader br = new BufferedReader(
	        		new InputStreamReader(  
	        				getAssets().open(pagina + ".htm"), "UTF-8"));
	        
	        String line = br.readLine();
			
            BufferedWriter out = new BufferedWriter(
            		new OutputStreamWriter(
            		new FileOutputStream(cantoTrasportato), "UTF-8"));
	        
            Pattern pattern = Pattern.compile("Do#|Do|Re|Mib|Mi|Fa#|Fa|Sol#|Sol|La|Sib|Si");
	        while (line != null) {
	        	if (line.contains("A13F3C") && !line.contains("<H2>") && !line.contains("<H4>")) {
//	        		Log.i("RIGA", line);
		        	Matcher matcher = pattern.matcher(line);
		        	StringBuffer sb = new StringBuffer();
		        	while(matcher.find()) {
		        	    matcher.appendReplacement(sb, conversione.get(matcher.group(0)));
		        	}
		        	matcher.appendTail(sb);
		        	out.write(sb.toString());
	                out.newLine();
	        	}
	        	else {
	        		if (line.contains("<H3>")) {
	        			if (barre != null && !barre.equals("0")) {
	        				String oldLine = "<H4><FONT COLOR=\"#A13F3C\"><I>Barr� al " + barre +  " tasto</I></FONT></H4>";
	        				out.write(oldLine);
	        				out.newLine();
	        			}
        				out.write(line);
        				out.newLine();
	        		}
	        		else {
	        			if (!line.contains("Barr�") && !line.contains("Barr&#232;")) {
	        				out.write(line);
	        				out.newLine();
	        			}
	        		}
	        	}
	        	line = br.readLine();
	        }
	        br.close();
	        out.flush();
	        out.close();
			return cantoTrasportato;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
   	}
   	
   	private void showHelp() {
   		blockOrientation();
        ActionItemTarget targetNew = new ActionItemTarget(this, R.id.tonalita);
        ShowcaseView showCase = ShowcaseView.insertShowcaseView(targetNew
        		, this
        		, R.string.action_tonalita
        		, R.string.showcase_tonalita_desc);
        showCase.setButtonText(getString(R.string.showcase_button_next));
        showCase.setScaleMultiplier(0.4f);
        showCase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
			
			@Override
			public void onShowcaseViewShow(ShowcaseView showcaseView) { }
			
			@Override
			public void onShowcaseViewHide(ShowcaseView showcaseView) {
		        ActionItemTarget target = new ActionItemTarget(PaginaRenderActivity.this, R.id.tonalita);
		        ShowcaseView showCase = ShowcaseView.insertShowcaseView(target
		        		, PaginaRenderActivity.this
		        		, "1) " + getString(R.string.action_trasporta)
		        		, getString(R.string.showcase_chtab_desc));
		        showCase.setButtonText(getString(R.string.showcase_button_next));
		        showCase.setScaleMultiplier(0.4f);
		        showCase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
					
					@Override
					public void onShowcaseViewShow(ShowcaseView showcaseView) { }
					
					@Override
					public void onShowcaseViewHide(ShowcaseView showcaseView) {
				        ActionItemTarget target = new ActionItemTarget(PaginaRenderActivity.this, R.id.tonalita);
				        ShowcaseView showCase = ShowcaseView.insertShowcaseView(
				        		target
				        		, PaginaRenderActivity.this
				        		, "2) " + getString(R.string.action_salva_tonalita)
				        		, getString(R.string.showcase_savetab_desc));
				        showCase.setButtonText(getString(R.string.showcase_button_next));
				        showCase.setScaleMultiplier(0.4f);
				        showCase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
							
							@Override
							public void onShowcaseViewShow(ShowcaseView showcaseView) { }
							
							@Override
							public void onShowcaseViewHide(ShowcaseView showcaseView) {
								ActionItemTarget target = new ActionItemTarget(PaginaRenderActivity.this, R.id.tonalita);
						        ShowcaseView showCase = ShowcaseView.insertShowcaseView(target
						        		, PaginaRenderActivity.this
						        		, "3) " + getString(R.string.action_reset_tonalita)
						        		, getString(R.string.showcase_restab_desc));
						        showCase.setButtonText(getString(R.string.showcase_button_next));
						        showCase.setScaleMultiplier(0.4f);
						        showCase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
									
									@Override
									public void onShowcaseViewShow(ShowcaseView showcaseView) { }
									
									@Override
									public void onShowcaseViewHide(ShowcaseView showcaseView) {
										ActionItemTarget target = new ActionItemTarget(PaginaRenderActivity.this, R.id.barre);
								        ShowcaseView showCase = ShowcaseView.insertShowcaseView(target
								        		, PaginaRenderActivity.this
								        		, R.string.action_barre
								        		, R.string.showcase_barre_desc);
								        showCase.setButtonText(getString(R.string.showcase_button_next));
								        showCase.setScaleMultiplier(0.4f);
								        showCase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
											
											@Override
											public void onShowcaseViewShow(ShowcaseView showcaseView) { }
											
											@Override
											public void onShowcaseViewHide(ShowcaseView showcaseView) {
												ActionItemTarget target = new ActionItemTarget(PaginaRenderActivity.this, R.id.barre);
										        ShowcaseView showCase = ShowcaseView.insertShowcaseView(target
										        		, PaginaRenderActivity.this
										        		, "1) " + getString(R.string.action_trasporta)
										        		, getString(R.string.showcase_chbarre_desc));
										        showCase.setButtonText(getString(R.string.showcase_button_next));
										        showCase.setScaleMultiplier(0.4f);
										        showCase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
													
													@Override
													public void onShowcaseViewShow(ShowcaseView showcaseView) { }
													
													@Override
													public void onShowcaseViewHide(ShowcaseView showcaseView) {
														ActionItemTarget target = new ActionItemTarget(PaginaRenderActivity.this, R.id.barre);
												        ShowcaseView showCase = ShowcaseView.insertShowcaseView(target
												        		, PaginaRenderActivity.this
												        		, "2) " + getString(R.string.action_salva_tonalita)
												        		, getString(R.string.showcase_savebarre_desc));
												        showCase.setButtonText(getString(R.string.showcase_button_next));
												        showCase.setScaleMultiplier(0.4f);
												        showCase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
															
															@Override
															public void onShowcaseViewShow(ShowcaseView showcaseView) { }
															
															@Override
															public void onShowcaseViewHide(ShowcaseView showcaseView) {
																ActionItemTarget target = new ActionItemTarget(PaginaRenderActivity.this, R.id.barre);
														        ShowcaseView showCase = ShowcaseView.insertShowcaseView(target
														        		, PaginaRenderActivity.this
														        		, "3) " + getString(R.string.action_reset_barre)
														        		, getString(R.string.showcase_resbarre_desc));
														        showCase.setButtonText(getString(R.string.showcase_button_next));
														        showCase.setScaleMultiplier(0.4f);
														        showCase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
																	
																	@Override
																	public void onShowcaseViewShow(ShowcaseView showcaseView) { }
																	
																	@Override
																	public void onShowcaseViewHide(ShowcaseView showcaseView) {
																		showScrollHelp();
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
			
			@Override
			public void onShowcaseViewDidHide(ShowcaseView showcaseView) { }
		});
   	}
    
   	public void showScrollHelp() {
   		lps = new RelativeLayout.LayoutParams(
	 			ViewGroup.LayoutParams.WRAP_CONTENT,
	 			ViewGroup.LayoutParams.WRAP_CONTENT);
	 	lps.addRule(RelativeLayout.ALIGN_PARENT_TOP);
	 	lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		int marginTop = ((Number) ( getApplicationContext().getResources().getDisplayMetrics().density * 40)).intValue();
		int marginRight = ((Number) ( getApplicationContext().getResources().getDisplayMetrics().density * 15)).intValue();
		lps.setMargins(marginTop, marginTop, marginRight, marginTop);
		
		ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
		co.buttonLayoutParams = lps;
   		
        ShowcaseView showCase = ShowcaseView.insertShowcaseView(
        		new ViewTarget(R.id.play_scroll, PaginaRenderActivity.this)
        		, PaginaRenderActivity.this
        		, R.string.play_scroll
        		, R.string.showcase_scroll_desc
        		, co);
        showCase.setButtonText(getString(R.string.showcase_button_next));
        showCase.setScaleMultiplier(0.3f);
        showCase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
			
			@Override
			public void onShowcaseViewShow(ShowcaseView showcaseView) { }
			
			@Override
			public void onShowcaseViewHide(ShowcaseView showcaseView) {
				ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
				co.buttonLayoutParams = lps;
		        ShowcaseView showCase = ShowcaseView.insertShowcaseView(
		        		new ViewTarget(R.id.speed_seekbar, PaginaRenderActivity.this)
		        		, PaginaRenderActivity.this
		        		, R.string.scroll_seekbar
		        		, R.string.showcase_seekbar_desc
		        		, co);
		        showCase.setButtonText(getString(R.string.showcase_button_next));
		        showCase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
					
					@Override
					public void onShowcaseViewShow(ShowcaseView showcaseView) { }
					
					@Override
					public void onShowcaseViewHide(ShowcaseView showcaseView) {
				        ShowcaseView showCase = ShowcaseView.insertShowcaseView(
				        		new ViewTarget(R.id.speed_seekbar, PaginaRenderActivity.this)
				        		, PaginaRenderActivity.this
				        		, R.string.showcase_end_title
				        		, R.string.showcase_help_general);
				        showCase.setShowcase(ShowcaseView.NONE);
				        showCase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
							
							@Override
							public void onShowcaseViewShow(ShowcaseView showcaseView) { }
							
							@Override
							public void onShowcaseViewHide(ShowcaseView showcaseView) {
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
   	  	
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            // take CPU lock to prevent CPU from going off if the user 
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                 getClass().getName());
            wl.acquire();

            try {
                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(sUrl[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    // expect HTTP 200 OK, so we don't mistakenly save error report 
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                         return "Server returned HTTP " + connection.getResponseCode() 
                             + " " + connection.getResponseMessage();

                    // this will be useful to display download percentage
                    // might be -1: server did not report the length
                    int fileLength = connection.getContentLength();

                    // download the file
                    input = connection.getInputStream();
                    output = new FileOutputStream(sUrl[1]);

                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled())
                            return null;
                        total += count;
                        // publishing the progress....
                        if (fileLength > 0) // only if total length is known
                            publishProgress((int) (total * 100 / fileLength));
                        output.write(data, 0, count);
                    }
                } catch (Exception e) {
                    return e.toString();
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } 
                    catch (IOException ignored) { }

                    if (connection != null)
                        connection.disconnect();
                }
            } finally {
                wl.release();
            }
            return null;
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mProgressDialog == null) {
	            mProgressDialog = new ProgressDialog(PaginaRenderActivity.this);
	            mProgressDialog.setMessage(getString(R.string.download_running));
	            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	            mProgressDialog.setCancelable(true);
	            mProgressDialog.setCanceledOnTouchOutside(false);
            }
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
        	if (mProgressDialog.isShowing())
        		mProgressDialog.dismiss();
        	setRequestedOrientation(prevOrientation);
            if (result != null)
                Toast.makeText(context,"Errore nel download: "+result, Toast.LENGTH_LONG).show();
            else {
                Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT).show();
                
                if (mediaPlayerState == MP_State.Started
                		|| mediaPlayerState == MP_State.Paused)
                	cmdStop();
                mediaPlayer = new MediaPlayer();
	    		mediaPlayerState = MP_State.Idle;
	    		mediaPlayer.setOnErrorListener(mediaPlayerOnErrorListener);
                
	    		localUrl = Utility.retrieveMediaFileLink(getApplicationContext(), url);
	    		if (localUrl.equalsIgnoreCase("")) {
	    			localFile = false;
	    			cmdSetDataSource(url);
	    			save_file.setEnabled(true);
	    			save_file.setVisibility(View.VISIBLE);
	    			delete_file.setEnabled(false);
	    			delete_file.setVisibility(View.GONE);
	    		}
	    		else {
	    			localFile = true;
	    			cmdSetDataSource(localUrl);
	    			save_file.setEnabled(false);
	    			save_file.setVisibility(View.GONE);
	    			delete_file.setEnabled(true);
	    			delete_file.setVisibility(View.VISIBLE);
	    		}
            }
        }
    }
    
    private class PdfExportTask extends AsyncTask<String, Integer, String> {

        public PdfExportTask() {}

        @Override
        protected String doInBackground(String... sUrl) {
        	HashMap<String, String> testConv = CambioAccordi.diffSemiToni(primaNota, notaCambio);
			String urlHtml = "";
			if (testConv != null) {
				String nuovoFile = cambiaAccordi(testConv, barreCambio);
				if (nuovoFile != null)
					urlHtml = nuovoFile;
			}
			else {		
				urlHtml = "file:///android_asset/" + pagina + ".htm";
			}
			// step 1
			Float margin = 15f;
	        Document document = new Document(PageSize.A4, margin, margin, margin, margin);
	        // step 2
			try {
		    	SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(PaginaRenderActivity.this);
				int saveLocation = Integer.parseInt(pref.getString("saveLocation", "0"));
				localPDFPath = "";
				if (saveLocation == 1) {
					File[] fileArray = ContextCompat.getExternalFilesDirs(PaginaRenderActivity.this, null);
					localPDFPath = fileArray[0].getAbsolutePath();
				}
				else {
					localPDFPath = PaginaRenderActivity.this.getFilesDir().toString();
				}
				localPDFPath += "/output.pdf";
//				Log.i("localPath", localPDFPath);
//				PdfWriter.getInstance(document, new FileOutputStream(ContextCompat.getExternalFilesDirs(getApplicationContext(), null)[0]
//						.getAbsolutePath() + "/output.pdf"));
				PdfWriter.getInstance(document, new FileOutputStream(localPDFPath));
				// step 3
				document.open();
				Font myFonColor = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
				// step 4
	 	       	try {
	 				String line = null;
		            BufferedReader br = new BufferedReader(
		            		new InputStreamReader(  
		                    new FileInputStream(urlHtml), "UTF-8")); 

		            line = br.readLine();
		            while (line != null) {
		            	if ((line.contains("000000")
		            	  || line.contains("A13F3C"))
		            	  && !line.contains("BGCOLOR")) {
			            	if (line.contains("000000")) {
			            		myFonColor = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
			            	}
			            	
			            	if (line.contains("A13F3C")) {
			            		myFonColor = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.RED);
			            	}
		            		line = line.replaceAll("<H4>", "");
		            		line = line.replaceAll("</H4>", "");
		            		line = line.replaceAll("<FONT COLOR=\"#000000\">", "");
		            		line = line.replaceAll("<FONT COLOR=\"#A13F3C\">", "");
		            		line = line.replaceAll("</FONT>", "");
		            		line = line.replaceAll("<H5>", "");
		            		line = line.replaceAll("<H3>", "");
		            		line = line.replaceAll("<H2>", "");
		            		line = line.replaceAll("</H5>", "");
		            		line = line.replaceAll("</H3>", "");
		            		line = line.replaceAll("</H2>", "");
		            		line = line.replaceAll("<I>", "");
		            		line = line.replaceAll("</I>", "");
		            		line = line.replaceAll("<B>", "");
		            		line = line.replaceAll("</B>", "");
		            		line = line.replaceAll("<br>", "");
			            	
//		            		Log.i("LINE", line);
		            	    Paragraph paragraph = new Paragraph(line, myFonColor);
		            		document.add(paragraph); 
		            	}
		            	else {
		            		if (line.equals("")) {
		            			document.add(Chunk.NEWLINE);
		            		}
		            	}
		            		
		            	line = br.readLine();
		            }
		            br.close();
		            
				} catch (IOException e) {
					e.printStackTrace();
				}
	 	       	//step 5
		        document.close();    
		        
//		        Log.i("DONE", "PDF Created!");
			} 
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (DocumentException e) {
				e.printStackTrace();
			}
            return null;
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
			if (mExportDialog == null) {
				mExportDialog = new ProgressDialog(PaginaRenderActivity.this);
				mExportDialog.setMessage(getString(R.string.export_running));
				mExportDialog.setIndeterminate(true);
				mExportDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				mExportDialog.setCancelable(true);
				mExportDialog.setCanceledOnTouchOutside(false);
			}
			mExportDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
        	if (mExportDialog.isShowing())
        		mExportDialog.dismiss();
//        	File file = new File(ContextCompat.getExternalFilesDirs(getApplicationContext(), null)[0].getAbsolutePath() + "/output.pdf");
        	File file = new File(localPDFPath);
	        Intent target = new Intent(Intent.ACTION_VIEW);
	        target.setDataAndType(Uri.fromFile(file),"application/pdf");
	        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        Intent intent = Intent.createChooser(target, getString(R.string.open_pdf));
	        try {
	            startActivity(intent);
	        } catch (ActivityNotFoundException e) {
				Toast toast = Toast.makeText(PaginaRenderActivity.this
						, getString(R.string.no_pdf_reader), Toast.LENGTH_SHORT);
				toast.show();
	        }  
        }
    }

}