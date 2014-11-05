package it.cammino.risuscito;

import it.gmariotti.changelibs.library.view.ChangeLogListView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ChangelogDialogFragment extends DialogFragment {
	
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ChangelogDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
    }
    
    // Use this instance of the interface to deliver action events
    ChangelogDialogListener mListener;
    OnKeyListener kListener;
    
    public void setListener(ChangelogDialogListener listener) {
        mListener = listener;
    }
    
    public void setOnKeyListener(OnKeyListener listener) {
    	kListener = listener;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_change_title));
        ChangeLogListView changelog = new ChangeLogListView(getActivity());
		builder.setView(changelog);
        builder.setPositiveButton(R.string.dialog_chiudi, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Send the positive button event back to the host activity
                       mListener.onDialogPositiveClick(ChangelogDialogFragment.this);
                   }
               });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        
        if (kListener != null)
        	dialog.setOnKeyListener(kListener);
        
        return dialog;
    }
}