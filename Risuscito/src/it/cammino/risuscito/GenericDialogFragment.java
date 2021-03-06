package it.cammino.risuscito;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;


public class GenericDialogFragment extends DialogFragment {
    
	private String customMessage;
	
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface GenericDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
    
    // Use this instance of the interface to deliver action events
    GenericDialogListener mListener;
    OnKeyListener kListener;
    
    public void setListener(GenericDialogListener listener) {
        mListener = listener;
   }
    
    public void setCustomMessage(String message) {
    	customMessage = message;
   }
    
    public void setOnKeyListener(OnKeyListener listener) {
    	kListener = listener;
   }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(customMessage);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Send the positive button event back to the host activity
                       mListener.onDialogPositiveClick(GenericDialogFragment.this);
                   }
               })
               .setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Send the negative button event back to the host activity
                       mListener.onDialogNegativeClick(GenericDialogFragment.this);
                   }
               });
        
        Dialog dialog = builder.create();
        
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
        
        if (kListener != null)
        	dialog.setOnKeyListener(kListener);
        
        return dialog;
    }
}