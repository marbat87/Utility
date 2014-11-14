package it.cammino.risuscito;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutActivity extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.activity_about, container, false);
		
		Toolbar toolbar = ((Toolbar) getActivity().findViewById(R.id.risuscito_toolbar));
		toolbar.setTitle(R.string.title_activity_about);

		return rootView;
	}
	    
}
