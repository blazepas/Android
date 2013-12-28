package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;
import java.util.HashMap;

import com.mareklatuszek.utilities.FinalVariables;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class AdapterAddPrzypomnienia extends BaseAdapter implements FinalVariables {
	
	private Activity mActivity;
	private LayoutInflater inflater;
	private ArrayList<HashMap<String, String>> przypomnienia = new ArrayList<HashMap<String,String>>();

	public AdapterAddPrzypomnienia(Activity mActivity) {
		this.mActivity = mActivity;
		inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		int size = przypomnienia.size();
		if (size == 0) {
			return 1;
		} else {
			return size;
		}
	}

	@Override
	public Object getItem(int arg0) {
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi;
		if (przypomnienia.size() == 0) {
			vi = getEmptyRow();
		} else {
			HashMap<String, String> przypomnienie = przypomnienia.get(position);
			String boxVal = przypomnienie.get(PRZYP_TEXT_BOX);
			String spinnerVal = przypomnienie.get(PRZYP_SPINNER);
			String przypHour = przypomnienie.get(PRZYP_HOUR);
			
			vi = getRow(boxVal, spinnerVal);
		}
		
//		ImageView button = (ImageView) vi.findViewById(R.id.button);
//		button.setOnClickListener(getButtonListener(position));
		
		return vi;
		
	}
	
	private View getRow(String boxVal, String spinnerVal) {
		View vi = inflater.inflate(R.layout.row_add_przypomnienie, null);
		return vi;
	}
	
	private View getEmptyRow() {
		View vi = inflater.inflate(R.layout.row_add_przypomnienie, null);
		return vi;
	}
	
	private OnClickListener getButtonListener(final int position) {
		OnClickListener listener;
		
		int lastPos = getCount() - 1;
		if (position == lastPos) {
			listener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					addRow();
					AdapterAddPrzypomnienia.this.notifyDataSetChanged();
				}
			};
		} else {
			listener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					removeRow(position);
					AdapterAddPrzypomnienia.this.notifyDataSetChanged();
				}
			};
		}
		
		return listener;
	}
	
	private void addRow() {
		HashMap<String, String> przypomnienie = new HashMap<String, String>();
		String boxVal = "";
		String spinnerVal = "";
		String przypHour = "14:00";
		przypomnienie.put(PRZYP_TEXT_BOX, boxVal);
		przypomnienie.put(PRZYP_SPINNER, spinnerVal);
		przypomnienie.put(PRZYP_HOUR, przypHour);
		przypomnienia.add(przypomnienie);
	}
	
	private void removeRow(int position) {
		przypomnienia.remove(position);
	}
	
}
