package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;

import com.mareklatuszek.datywznosci.utilities.CommonUtilities;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class AdapterDialogKategorie extends BaseAdapter {
	
	private AdapterDB adapterDb;
	private Activity mActivity;
	private LayoutInflater inflater=null;

	ArrayList<String> categories = new ArrayList<String>();
	CommonUtilities utilites = new CommonUtilities();
	
	TextView catNameTxtKat;
	Button usunButtonKat;
	
	public AdapterDialogKategorie(Activity mActivity, ArrayList<String> categories) {
		this.mActivity = mActivity;
		this.categories = categories;
		inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		adapterDb = new AdapterDB(mActivity);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return categories.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return categories.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View vi=convertView;
        vi = inflater.inflate(R.layout.listview_kategorie, null);
        
        catNameTxtKat = (TextView) vi.findViewById(R.id.catNameTxtKat);
        usunButtonKat = (Button) vi.findViewById(R.id.usunButtonKat);
         
        final String category = categories.get(position);
        catNameTxtKat.setText(category);
        
        usunButtonKat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				removeCategory(categories.get(position));
			}
		});
        
        return vi;
	}
	
	public ArrayList<String> getData() {
		return categories;
	}
	
	public boolean removeCategory(String category) {
		adapterDb.open();
		categories.remove(category);
		boolean status = adapterDb.deleteCategory(category);
		adapterDb.close();
		if(status) {
			categories.remove(category);
		}
		this.notifyDataSetChanged();
		return status;
	}
}
