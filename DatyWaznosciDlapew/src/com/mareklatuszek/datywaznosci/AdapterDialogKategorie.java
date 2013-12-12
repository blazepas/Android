package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.mareklatuszek.utilities.CommonUtilities;

public class AdapterDialogKategorie extends BaseAdapter {
	
	private AdapterDB adapterDb;
	private FragmentActivity mActivity;
	private LayoutInflater inflater = null;
	private View spinner;

	AdapterCustomSpinner kategorieAdapter;
	ArrayList<String> kategorie;
	CommonUtilities utilites = new CommonUtilities();
	
	TextView catNameTxtKat;
	Button usunButtonKat;
	
	public AdapterDialogKategorie(FragmentActivity mActivity, AdapterCustomSpinner kategorieAdapter, View spinner) {
		this.mActivity = mActivity;
		this.kategorieAdapter = kategorieAdapter;
		this.spinner = spinner;

		inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		adapterDb = new AdapterDB(mActivity);
		kategorie = kategorieAdapter.getArrayListData();
	}

	@Override
	public int getCount() {
		return kategorie.size();
	}

	@Override
	public Object getItem(int position) {
		return kategorie.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View vi=convertView;
        vi = inflater.inflate(R.layout.listview_kategorie, null);
        
        catNameTxtKat = (TextView) vi.findViewById(R.id.catNameTxtKat);
        usunButtonKat = (Button) vi.findViewById(R.id.usunButtonKat);
         
        final String category = kategorie.get(position);
        catNameTxtKat.setText(category);
        
        usunButtonKat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				removeCategory(category, position);
			}
		});
        
        return vi;
	}
	
	public List<String> getData() {
		return kategorie;
	}
	
	public boolean removeCategory(String category, int pos) {		
		adapterDb.open();
		boolean status = adapterDb.deleteCategory(category);
		adapterDb.close();

		if(status) {
			kategorie.remove(category);
		}

		kategorieAdapter.setArrayListData(kategorie);
		this.notifyDataSetChanged();
		
		return status;
	}
}
