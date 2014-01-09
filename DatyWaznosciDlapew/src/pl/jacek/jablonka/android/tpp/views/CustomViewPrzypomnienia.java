package pl.jacek.jablonka.android.tpp.views;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import pl.jacek.jablonka.android.tpp.R;
import pl.jacek.jablonka.android.tpp.TPPApp;
import pl.jacek.jablonka.android.tpp.atapters.AdapterCustomSpinner;
import pl.jacek.jablonka.android.tpp.utilities.CommonUtilities;
import pl.jacek.jablonka.android.tpp.utilities.FinalVariables;
import pl.jacek.jablonka.android.tpp.verification.PremiumUtilities;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CustomViewPrzypomnienia extends LinearLayout implements FinalVariables {

	private Context contex;
	private AdapterCustomSpinner adapter;
	private LayoutInflater inflater;
	private CommonUtilities utilities = TPPApp.getUtilities();
	String spinnerTitle;

	public CustomViewPrzypomnienia(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.contex = context;
		
		String[] okresSpinnData = getResources().getStringArray(R.array.array_date);
		adapter = new AdapterCustomSpinner(context, okresSpinnData);

		inflater = LayoutInflater.from(context);
		spinnerTitle = context.getString(R.string.spinner_title_alarm);

		initEmptyRow(0);
	}
		
	private void initEmptyRow(int position) {
		LinearLayout row = (LinearLayout) inflater.inflate(R.layout.row_add_przypomnienie, null);
		row.setTag(position);
		
		EditTextBariol textBox = (EditTextBariol) row.findViewById(R.id.textBox);
		CustomSpinner spinner = (CustomSpinner) row.findViewById(R.id.spinner);
		ImageView buttonImage = (ImageView) row.findViewById(R.id.buttonImage);

		textBox.setId(position);
		textBox.setOnKeyListener(new EditBoxListener());
		
		spinner.setAdapter(adapter);
		spinner.setText(spinnerTitle);
		
		buttonImage.setOnClickListener(new ButtonListener());
		
		addView(row, position);
	}
	
	private void initRow(String boxTxt, String spinnerChoice, int position) {
		LinearLayout row = (LinearLayout) inflater.inflate(R.layout.row_add_przypomnienie, null);
		row.setTag(position);
		
		EditTextBariol textBox = (EditTextBariol) row.findViewById(R.id.textBox);
		CustomSpinner spinner = (CustomSpinner) row.findViewById(R.id.spinner);
		ImageView buttonImage = (ImageView) row.findViewById(R.id.buttonImage);
		
		textBox.setId(position);
		textBox.setText(boxTxt);
		textBox.setOnKeyListener(new EditBoxListener());
		
		spinner.setText(spinnerChoice);
		spinner.setAdapter(adapter);

		buttonImage.setOnClickListener(new ButtonListener());
		buttonImage.setImageResource(R.drawable.button_remove_przyp);		
		
		addView(row);
	}
	
	private class ButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (checkPremium()) {
				View currentRow = (View) v.getParent();
				int pos = indexOfChild(currentRow);
				int lastPos = getChildCount() - 1;
				
				if (pos == lastPos) {
					initEmptyRow(lastPos + 1);
					changeBeforeButton(currentRow);

				} else {
					removeView((View) v.getParent());
				}
			}
			
		}
	}
	
	private class EditBoxListener implements OnKeyListener {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (checkPremium()) {
				v.setEnabled(true);
			} else {
				v.setEnabled(false);
			}
			return false;
		}
		
	}
			
	private void changeBeforeButton(View currentRow) {
		ImageView beforeButton = (ImageView) currentRow.findViewById(R.id.buttonImage);
		beforeButton.setImageResource(R.drawable.button_remove_przyp);		
	}
	
	public void setPrzypomnienia(ArrayList<HashMap<String,String>> przypomnienia) {
		int count = przypomnienia.size();
		removeAllViewsInLayout();
		for (int i = 0; i < count; i ++) {
			HashMap<String,String> przypomnienie = przypomnienia.get(i);
			
			String boxTxt = przypomnienie.get(PRZYP_TEXT_BOX);
			String spinnerChoice = przypomnienie.get(PRZYP_SPINNER);
			String przypHour = "14:00";
			
			initRow(boxTxt, spinnerChoice, i);
		}
		
		initEmptyRow(count);	
	}
	
	public ArrayList<HashMap<String,String>> getPrzypomnienia(String endDate) {
		ArrayList<HashMap<String, String>> przypomnienia = new ArrayList<HashMap<String,String>>();
		
		int count = getChildCount();

		for (int i = 0; i < count; i++) {
			View currentRow = getChildAt(i);
			HashMap<String, String> przypomnienie = new HashMap<String, String>();
			int tag = (Integer) currentRow.getTag();
			
			EditTextBariol textBox = (EditTextBariol) currentRow.findViewById(tag);
			CustomSpinner spinner = (CustomSpinner) currentRow.findViewById(R.id.spinner);
			
			String boxTxt = textBox.getText().toString();
			String spinnerChoice = spinner.getText();
			String przypHour = "14:00";
			String przypDate = "0";
			
			if (boxTxt.length() != 0 & !boxTxt.equals("0") & !spinnerChoice.equals(spinnerTitle)) {
				try {
					long dateInMillis = utilities.parsePrzypmnienieToDate(boxTxt, spinnerChoice, endDate, przypHour);
					przypDate = String.valueOf(dateInMillis);
				} catch (ParseException e) {
					Log.i("getPrzypomnienia", "parse to date error");
				}
				
				przypomnienie.put(PRZYP_TEXT_BOX, boxTxt);
				przypomnienie.put(PRZYP_SPINNER, spinnerChoice);
				przypomnienie.put(PRZYP_HOUR, przypHour);
				przypomnienie.put(PRZYP_DATE, przypDate);
				
				przypomnienia.add(przypomnienie);
			}
			
		}

		return przypomnienia;
	}
		
	private boolean checkPremium() {
		if (PremiumUtilities.APP_VERSION_NONE) {
			String message = contex.getString(R.string.toast_alarms_no_premium);
			Toast.makeText(contex, message, 3000).show();
			return false;
		} else {
			return true;
		}
	}
}
