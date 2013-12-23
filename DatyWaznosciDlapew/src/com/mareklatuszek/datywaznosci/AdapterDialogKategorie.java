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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mareklatuszek.utilities.CommonUtilities;
import com.mareklatuszek.utilities.TextViewBariol;

public class AdapterDialogKategorie extends BaseAdapter {
	
	private AdapterDB adapterDb;
	private FragmentActivity mActivity;
	private LayoutInflater inflater = null;
	private CustomSpinner spinnerKategorie;

	AdapterCustomSpinner kategorieAdapter;
	ArrayList<String> kategorie;
	CommonUtilities utilites = new CommonUtilities();
	
	TextViewBariol catNameTxt;
	LinearLayout deleteCat;
	
	public AdapterDialogKategorie(FragmentActivity mActivity, AdapterCustomSpinner kategorieAdapter, CustomSpinner spinnerKategorie) {
		this.mActivity = mActivity;
		this.kategorieAdapter = kategorieAdapter;
		this.spinnerKategorie = spinnerKategorie;

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
		View vi = convertView;
		vi = initRow(vi, position);
		
		return vi;
	}
	
	private View initRow(View vi, final int position) {
        vi = inflater.inflate(R.layout.listview_kategorie, null);
        
        int rowBackground;
		int deleteButtonBg;
        
        if((position % 2) == 0) { // produkt parzysty lub nie
			rowBackground = R.color.categories_even;
			deleteButtonBg = R.color.categories_delete_even_bg;
		} else {
			rowBackground = R.color.categories_odd;
			deleteButtonBg = R.color.categories_delete_odd_bg;
		}
        
        catNameTxt = (TextViewBariol) vi.findViewById(R.id.catNameTxt);
        deleteCat = (LinearLayout) vi.findViewById(R.id.deleteCat);
         
        vi.setBackgroundResource(rowBackground);
        deleteCat.setBackgroundResource(deleteButtonBg);
        
        final String category = kategorie.get(position);
        catNameTxt.setText(category);
        
        deleteCat.setOnClickListener(new OnClickListener() {
			
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
		spinnerKategorie.setText(kategorie.get(0));
		this.notifyDataSetChanged();
		
		return status;
	}
}
