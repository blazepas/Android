package com.mareklatuszek.datywaznosci;

import java.io.ObjectInputStream.GetField;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;

public class DialogDatePicker extends Dialog implements android.view.View.OnClickListener {
	
	View viewToSetDate;
	DatePicker datePicker;
	Button okButton, anulujButton;

	public DialogDatePicker(Context context, View viewToSetDate) {
		super(context);
		this.viewToSetDate = viewToSetDate;
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
			FragmentDodaj.terminWazIsSet = true;
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
			break;
		}
		
	}
	
	private String getChosenDate() {
		String choosenDate = "";
		choosenDate = datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear();
		return choosenDate;
	}
		
}