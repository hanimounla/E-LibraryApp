package com.mounla.hani.e_library;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class FragmentProgressDialog extends DialogFragment {
	ProgressDialog dialog;
	
	public FragmentProgressDialog() {
		super();
	}
	
	@Override
	public ProgressDialog onCreateDialog(Bundle savedInstanceState) {
		dialog = new ProgressDialog(getActivity());
		dialog.setMessage("Downloading Book...");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		return dialog;
	}
	
	@Override
	public void onCancel(DialogInterface d) {
		super.onCancel(d);
		
//		AsyncTask1.interruptTask();
	}
	
	public void setProgress(int p) {
		if(dialog != null)
			dialog.setProgress(p);
	}
}