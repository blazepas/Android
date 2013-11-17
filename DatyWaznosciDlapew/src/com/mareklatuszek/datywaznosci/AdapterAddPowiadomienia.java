package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;
import java.util.HashMap;

import com.mareklatuszek.datywznosci.utilities.FinalVariables;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class AdapterAddPowiadomienia extends BaseAdapter implements FinalVariables {

	private LayoutInflater inflater=null;
	private Activity mActivity;
	public int przypCount;
	FragmentManager fragmentManager;
	int fragmentDodajId;
	
	private ArrayList<HashMap<String, String>> adapterData = new ArrayList<HashMap<String,String>>();

	private EditText przypTextBox;
	private Spinner przypSpinner;
	private Button przypButton;
	
	public AdapterAddPowiadomienia(Activity mActivity, int przypCount, FragmentManager fragmentManager, int fragmentDodajId) {
		this.mActivity = mActivity;
		inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		adapterData = initiateAdapterData();
		this.fragmentManager = fragmentManager;
		this.fragmentDodajId = fragmentDodajId;
	}
		
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return przypCount;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View vi=convertView;
        vi = inflater.inflate(R.layout.listview_add_powiadomienia, null);
        
        final int count = getCount();
  
        przypTextBox = (EditText) vi.findViewById(R.id.przypTextBox);
        przypSpinner = (Spinner) vi.findViewById(R.id.przypSpinner);
        przypButton = (Button) vi.findViewById(R.id.przypButton);
        
        if (count == 1) {
        	//jesli jest to pierwsza inicicja
        	setViewsAtributes(1);
        } else {
        	setViewsAtributes(position);
        }
        
        if ((position + 1) != (adapterData.size())) {
          	przypButton.setVisibility(View.GONE);
        }
        
        
        przypTextBox.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				EditText textBox = (EditText) v;
				String textBoxTxt = textBox.getText().toString();
				editRowAdapterData(position, PRZYP_TEXT_BOX, textBoxTxt);
				return false;
			}
		});
        
        przypSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String spinnerPosition = String.valueOf(arg2);
				editRowAdapterData(position, PRZYP_SPINNER, spinnerPosition);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
        
        przypButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addRowInFragment(adapterData.size());
			}
		});
        
		return vi;
	}
	
	private void setViewsAtributes(int position) {
		int dataSize = adapterData.size();
		HashMap<String, String> rowData;
		
		if (dataSize < (position + 1)) {			
			rowData = addRowAdapterData();		
		} else {
			rowData = adapterData.get(position); 
		}
		
        String textBoxtText = rowData.get(PRZYP_TEXT_BOX);
        int spinnerPos = Integer.parseInt(rowData.get(PRZYP_SPINNER));
        
        przypTextBox.setText(textBoxtText);
        przypSpinner.setSelection(spinnerPos);
    	
	}
	
	public ArrayList<HashMap<String, String>> getAdapterData() {
		return adapterData;
	}
	
	private ArrayList<HashMap<String, String>> initiateAdapterData() {
		ArrayList<HashMap<String, String>> allData = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> rowData = new HashMap<String, String>();
		
		rowData.put(PRZYP_TEXT_BOX, "");
		rowData.put(PRZYP_SPINNER, "0");
		
		allData.add(rowData);
		
		return allData;
	}
	
	private void addRowInFragment(int count) {
		int newCount = count + 1; 
    	FragmentDodaj fragmentDodaj = (FragmentDodaj) fragmentManager.findFragmentById(fragmentDodajId);
    	fragmentDodaj.setRowPrzypomnienia(newCount);
	}
		
	private HashMap<String, String> addRowAdapterData() {
		HashMap<String, String> rowData = new HashMap<String, String>();
		
		rowData.put(PRZYP_TEXT_BOX, "");
		rowData.put(PRZYP_SPINNER, "0");
		
		adapterData.add(rowData);
		return rowData;	
	}
	
	private void editRowAdapterData(int position, String key, String value) {
		
		HashMap<String, String> rowData = adapterData.get(position);
		rowData.put(key, value);
		adapterData.set(position, rowData);
	}
		
	

}
