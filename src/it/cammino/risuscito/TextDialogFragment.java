package it.cammino.risuscito;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.EditText;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.text.InputType;

public class TextDialogFragment extends DialogFragment {
    
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface TextDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String titoloPosizione);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
    
    // Use this instance of the interface to deliver action events
    TextDialogListener mListener;
    OnKeyListener kListener;
    private String customMessage;
    
    public void setListener(TextDialogListener listener) {
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
    	final EditText input = new EditText(getActivity());
//    	if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1)
//    		input.setTextColor(Color.B);
    	input.setInputType(InputType.TYPE_CLASS_TEXT);
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(customMessage)
        	   .setView(input)
               .setPositiveButton(R.string.dialog_chiudi, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   String title = input.getText().toString();
                       // Send the positive button event back to the host activity
                       mListener.onDialogPositiveClick(TextDialogFragment.this, title);
                   }
               })
               .setNegativeButton(R.string.aggiungi_dismiss, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Send the negative button event back to the host activity
                       mListener.onDialogNegativeClick(TextDialogFragment.this);
                   }
               });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        
        if (kListener != null)
        	dialog.setOnKeyListener(kListener);
        
        return dialog;
    }
}