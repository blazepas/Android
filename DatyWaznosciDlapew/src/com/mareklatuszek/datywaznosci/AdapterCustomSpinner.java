package com.mareklatuszek.datywaznosci;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class AdapterCustomSpinner extends BaseAdapter{
	
	private LayoutInflater inflater = null;
	private Context mActivity;
	private String[] data;
	
	public AdapterCustomSpinner() {
		this.data = new String[0];
	}
	
	public AdapterCustomSpinner(Context mActivity, String[] data) {
		this.mActivity = mActivity;
		inflater = (LayoutInflater)mActivity.getSystemService(mActivity.LAYOUT_INFLATER_SERVICE);
		this.data = data;
	}
	
	public AdapterCustomSpinner(FragmentActivity mActivity, ArrayList<String> data) {
		this.mActivity = mActivity;
		inflater = (LayoutInflater)mActivity.getSystemService(mActivity.LAYOUT_INFLATER_SERVICE);
		String[] kategorieSpinnData = new String[data.size()];
		this.data = data.toArray(kategorieSpinnData);
	}

	@Override
	public int getCount() {
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		return data[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (inflater != null) {
			View vi= inflater.inflate(R.layout.spinner_okres, null);
			TextView okresTxt = (TextView) vi.findViewById(R.id.okres);

			String okres = data[position];
			
			okresTxt.setText(okres);
			vi.setTag(okres);
			
			return vi;
		} else {
			return convertView;
		}
		
	}
	
	public void setData(String[] data) {
		this.data = data;
	}
	
	public void setArrayListData(ArrayList<String> data) {
		int size = data.size();
		String[] newData = new String[size];
		newData = data.toArray(newData);
		
		this.data = newData;
	}
	
	public String[] getArrayData() {
		return this.data;
	}
	
	public ArrayList<String> getArrayListData() {
		ArrayList<String> listData = new ArrayList<String>(Arrays.asList(this.data));
		return listData;
	}
}
