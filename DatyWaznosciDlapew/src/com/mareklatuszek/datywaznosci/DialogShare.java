package com.mareklatuszek.datywaznosci;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mareklatuszek.datywznosci.utilities.CommonUtilities;

public class DialogShare extends Dialog implements android.view.View.OnClickListener {
	CommonUtilities utilities = new CommonUtilities();
	int fragId;
	FragmentManager fm;
	
	Button okButton, anulujButton;
	EditText emailTxtBox;
	
	public DialogShare(Context context, FragmentManager fm, int fragId) {
		super(context);
		this.fragId = fragId;
		this.fm = fm;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Wyślij listę produktów");
		setContentView(R.layout.dialog_share);
		
		emailTxtBox = (EditText) findViewById(R.id.emailTxtBox);
		okButton = (Button) findViewById(R.id.okButton);
		anulujButton = (Button) findViewById(R.id.cancelButton);
				
		okButton.setOnClickListener(this);
		anulujButton.setOnClickListener(this);

	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.okButton:
			new Share().execute();
			break;
		case R.id.cancelButton:
			break;
		default:
			break;
		}
		dismiss();
		
	}
		
	private class Share extends AsyncTask<Void, Void, Void> {
		
		boolean emailStatus = false;
		
		@Override
		protected void onPreExecute() {
			Toast.makeText(getContext(), "Wysyłanie...", 1000).show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			String email = emailTxtBox.getText().toString();
			
			if (!email.equals("")) {
				FragmentProdukty fragmentProdukty = (FragmentProdukty) fm.findFragmentById(fragId);
				emailStatus = fragmentProdukty.shareAllProducts(email);
			}	
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			if (emailStatus) {
				Toast.makeText(getContext(), "Wysłano!", 1000).show();
			} else {
				Toast.makeText(getContext(), "Nie wysłano!", 1000).show();
			}
		}
		
		
	}
	
	
				
}