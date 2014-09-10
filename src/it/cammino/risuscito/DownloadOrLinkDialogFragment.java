package it.cammino.risuscito;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;


public class DownloadOrLinkDialogFragment extends DialogFragment {
    	
    public interface DownloadOrLinkDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
        public void onDialogNeutralClick(DialogFragment dialog);
    }
    
    // Use this instance of the interface to deliver action events
    DownloadOrLinkDialogListener mListener;
    OnKeyListener kListener;
    
    public void setListener(DownloadOrLinkDialogListener listener) {
        mListener = listener;
   }
    
    public void setOnKeyListener(OnKeyListener listener) {
    	kListener = listener;
   }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.downlink_message);
        builder.setPositiveButton(R.string.downlink_download, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the positive button event back to the host activity
                mListener.onDialogPositiveClick(DownloadOrLinkDialogFragment.this);
            }
        })
        .setNeutralButton(R.string.downlink_choose, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the negative button event back to the host activity
                mListener.onDialogNeutralClick(DownloadOrLinkDialogFragment.this);
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the negative button event back to the host activity
                mListener.onDialogNegativeClick(DownloadOrLinkDialogFragment.this);
            }
        });
        
        Dialog dialog = builder.create();
        
        if (kListener != null)
        	dialog.setOnKeyListener(kListener);
        
        return dialog;
    }
}