package pl.jacek.jablonka.android.tpp.dialogs;

import pl.jacek.jablonka.android.tpp.R;
import pl.jacek.jablonka.android.tpp.utilities.CommonUtilities;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TimePicker;

public class DialogTimePicker extends Dialog implements android.view.View.OnClickListener {
	
	View viewToSetTime;
	TimePicker timePicker;
	Button okButton, anulujButton;
	CommonUtilities utilities = new CommonUtilities();

	public DialogTimePicker(Context context, View viewToSetTime) {
		super(context);
		this.viewToSetTime = viewToSetTime;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_time_picker);
		
		timePicker = (TimePicker) findViewById(R.id.timePicker);
		okButton = (Button) findViewById(R.id.okButton);
		anulujButton = (Button) findViewById(R.id.cancelButton);
		
		timePicker.setIs24HourView(true);
		
		okButton.setOnClickListener(this);
		anulujButton.setOnClickListener(this);

	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.okButton:
			setChosenTime();
			break;
		case R.id.cancelButton:
			break;
		default:
			break;
		}
		dismiss();
		
	}
	
	private void setChosenTime() {
		String choosenTime = getChosenTime();
		((Button) viewToSetTime).setText(choosenTime);
		
		
	}
	
	private String getChosenTime() {
		String choosenTime = "";
		choosenTime = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
		return choosenTime;
	}
				
}