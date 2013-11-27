package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.mareklatuszek.datywznosci.utilities.CommonUtilities;

public class AdapterKategorie extends BaseAdapter {
	
	private Activity mActivity;
	private LayoutInflater inflater=null;
	private FragmentManager fragmentManager;
	private int fragmentId;
	ArrayList<String> categories = new ArrayList<String>();
	CommonUtilities utilites = new CommonUtilities();
	
	TextView catNameTxtKat;
	Button usunButtonKat;
	
	public AdapterKategorie(Activity mActivity, ArrayList<String> categories, FragmentManager fragmentManager, int fragmentId) {
		this.mActivity = mActivity;
		this.categories = categories;
		this.fragmentManager = fragmentManager;
		this.fragmentId = fragmentId;
		inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
        vi = inflater.inflate(R.layout.listview_kategorie, null);
        
        catNameTxtKat = (TextView) vi.findViewById(R.id.catNameTxtKat);
        usunButtonKat = (Button) vi.findViewById(R.id.usunButtonKat);
         
        final String category = categories.get(position);
        catNameTxtKat.setText(category);
        
        usunButtonKat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				deleteCategory(category);
				
			}
		});
        
		return vi;
	}
	
	private void deleteCategory(String category) {		
		FragmentKategorie fragmentKategorie = (FragmentKategorie) fragmentManager.findFragmentById(fragmentId);
		fragmentKategorie.removeCategory(category);
	}

}
