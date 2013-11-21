package com.mareklatuszek.datywaznosci;

import java.io.ObjectInputStream.GetField;

import com.mareklatuszek.datywznosci.utilities.CommonUtilities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

public class DialogDatePicker extends Dialog implements android.view.View.OnClickListener {
	
	View viewToSetDate;
	EditText okresWazTextBox;
	Spinner okresWazSpinner;
	DatePicker datePicker;
	Button okButton, anulujButton;
	
	CommonUtilities utilities = new CommonUtilities();

	public DialogDatePicker(Context context, View viewToSetDate, EditText okresWazTextBox, Spinner okresWazSpinner) {
		super(context);
		this.viewToSetDate = viewToSetDate;
		this.okresWazTextBox = okresWazTextBox;
		this.okresWazSpinner = okresWazSpinner;
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
		}
		
	}
	
	private String getChosenDate() {
		String choosenDate = "";
		choosenDate = datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear();
		return choosenDate;
	}
	
	private void setOkresWaz(String choosenDate) {
		String okres = utilities.parseDateToOkres(choosenDate);
		String box = utilities.getTextFromOkresWaz(okres);
		String spinnItem = utilities.getSpinnerItemFromOkresWaz(okres);
		int spinnPos = utilities.getPosInSpinner(spinnItem, okresWazSpinner);
		
		okresWazTextBox.setText(box);
		okresWazSpinner.setSelection(spinnPos);	
	}
		
}