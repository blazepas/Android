package com.mareklatuszek.datywaznosci;

import java.io.ObjectInputStream.GetField;

import com.mareklatuszek.datywznosci.utilities.CommonUtilities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class DialogDatePicker extends Dialog implements android.view.View.OnClickListener {
	
	View viewToSetDate;
	DatePicker datePicker;
	Button okButton, anulujButton;
	FragmentManager fragmentManager; 
	int fragmentId;
	int fragPos;
	CommonUtilities utilities = new CommonUtilities();

	public DialogDatePicker(Context context, View viewToSetDate, FragmentManager fragmentManager, int fragmentId) {
		super(context);
		this.viewToSetDate = viewToSetDate;
		this.fragmentManager = fragmentManager;
		this.fragmentId = fragmentId;
		fragPos = MainActivity.currentFragmentPos;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_date_picker);
		
		datePicker = (DatePicker) findViewById(R.id.datePicker);
		okButton = (Button) findViewById(R.id.okButton);
		anulujButton = (Button) findViewById(R.id.cancelButton);
		
		okButton.setOnClickListener(this);
		anulujButton.setOnClickListener(this);

	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.okButton:
			setChosenDate();
			break;
		case R.id.cancelButton:
			break;
		default:
			break;
		}
		dismiss();
		
	}
	
	private void setChosenDate() {
		String choosenDate = getChosenDate();
		switch (viewToSetDate.getId()) {
		case R.id.dataOtwButton:
			((Button) viewToSetDate).setText(choosenDate);
			break;
		case R.id.terminWazButton:		
			((Button) viewToSetDate).setText(choosenDate);
			setOkresWaz(choosenDate);
			break;
		case R.id.dataZuzButton:		
			((Button) viewToSetDate).setText(choosenDate);
			break;
		}
		
	}
	
	private String getChosenDate() {
		String choosenDate = "";
		choosenDate = datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear();
		return choosenDate;
	}
		
	private void setOkresWaz(String choosenDate) {
		switch (fragPos) {
		case 1:
			FragmentDodaj fragmentDodaj = (FragmentDodaj) fragmentManager.findFragmentById(fragmentId);
	    	fragmentDodaj.setOkresWaz(choosenDate);
			break;
		case 6:
			FragmentEdytuj fragmentEdytuj = (FragmentEdytuj) fragmentManager.findFragmentById(fragmentId);
			fragmentEdytuj.setOkresWaz(choosenDate);
			break;
		default:
			Toast.makeText(getOwnerActivity(), "Nie zapisano daty!", 2000).show();
		}
		
	}
		
}