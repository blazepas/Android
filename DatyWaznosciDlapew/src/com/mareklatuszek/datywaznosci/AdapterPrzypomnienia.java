package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mareklatuszek.utilities.CommonUtilities;
import com.mareklatuszek.utilities.FinalVariables;

public class AdapterPrzypomnienia extends BaseAdapter implements FinalVariables {
	private Activity mActivity;
	ArrayList<HashMap<String, String>> przypomnienia = new ArrayList<HashMap<String,String>>();
	private LayoutInflater inflater = null;
	private CommonUtilities utilities = new CommonUtilities();
	
	TextView nazwaPow, pozostaloPow, terminWazPow;
	ProgressBar progressPow;
	
	public AdapterPrzypomnienia(Activity mActivity, ArrayList<HashMap<String, String>> przypomnienia) {
		this.mActivity = mActivity;
		this.przypomnienia = przypomnienia;		
		inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return przypomnienia.size();
	}

	@Override
	public Object getItem(int position) {
		return przypomnienia.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
        vi = inflater.inflate(R.layout.listview_powiadomienia, null);
        
        nazwaPow = (TextView) vi.findViewById(R.id.nazwaPow);  
        pozostaloPow = (TextView) vi.findViewById(R.id.pozostaloPow);
        terminWazPow = (TextView) vi.findViewById(R.id.terminWazPow);
        progressPow = (ProgressBar) vi.findViewById(R.id.progressPow);  
        
        HashMap<String, String> przypomnienie = przypomnienia.get(position);
        
        String nazwa = przypomnienie.get(DB_NAZWA);
        String dataOtw = przypomnienie.get(DB_DATA_OTWARCIA);
        String terminWaz = przypomnienie.get(DB_TERMIN_WAZNOSCI);
        long powiadomienieDate = Long.parseLong(przypomnienie.get(PRZYP_DATE));
        long currentTime = System.currentTimeMillis();
        String pozostaloText = "za " + utilities.dateToWords(currentTime, powiadomienieDate);
        int progress = utilities.getProgress(dataOtw, terminWaz);
        
        nazwaPow.setText(nazwa);
        pozostaloPow.setText(pozostaloText);
        terminWazPow.setText(terminWaz);
        progressPow.setProgress(progress);
               
		return vi;
	}
	
	
}
