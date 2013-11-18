package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;
import java.util.HashMap;

import com.mareklatuszek.datywznosci.utilities.CommonUtilities;
import com.mareklatuszek.datywznosci.utilities.FinalVariables;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterProduktPrzypomnienia extends BaseAdapter implements FinalVariables {
	
	private Activity mActivity;
	private ArrayList<HashMap<String, String>> przypomnienia = new ArrayList<HashMap<String,String>>();
	private LayoutInflater inflater = null;
	
	CommonUtilities utilities = new CommonUtilities();
	
	TextView kiedyPow;
	
	public AdapterProduktPrzypomnienia(Activity mActivity, ArrayList<HashMap<String, String>> przypomnienia) {
		this.mActivity = mActivity;
		inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.przypomnienia = przypomnienia;
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
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
        vi = inflater.inflate(R.layout.listview_produkt_przypomnienia, null);
        
        kiedyPow = (TextView) vi.findViewById(R.id.kiedyPow);
        
        String przypDate = przypomnienia.get(position).get(PRZYP_DATE);
        long notifTime = Long.parseLong(przypDate);
        String date = utilities.dateToWords(notifTime);
        
        kiedyPow.setText(date);
        
		return vi;
	}

}
