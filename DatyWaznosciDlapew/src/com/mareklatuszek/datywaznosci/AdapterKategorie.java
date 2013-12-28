package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mareklatuszek.utilities.CommonUtilities;
import com.mareklatuszek.utilities.TextViewBariol;

public class AdapterKategorie extends BaseAdapter {
	
	private Activity mActivity;
	private LayoutInflater inflater=null;
	private FragmentManager fragmentManager;
	private int fragmentId;
	ArrayList<String> categories = new ArrayList<String>();
	CommonUtilities utilites = new CommonUtilities();
	
	TextViewBariol catNameTxt;
	LinearLayout deleteCat;
	
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
		View vi = initRow(position);
        
		return vi;
	}
	
	private View initRow(int position) {
		View vi = inflater.inflate(R.layout.listview_kategorie, null);
        
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
         
        final String category = categories.get(position);
        catNameTxt.setText(category);
        
        deleteCat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(category);
				
			}
		});
        
        return vi;
	}
	
	private void showDialog(final String category) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
		dialog.setTitle("Usuwanie");
		dialog.setMessage("Czy na pewno usunąć ketegorię: " + category + "?");
		dialog.setPositiveButton("Tak",new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteCategory(category);
			}
		});

		dialog.setNegativeButton("Anuluj",new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
	
	private void deleteCategory(String category) {		
		FragmentKategorie fragmentKategorie = (FragmentKategorie) fragmentManager.findFragmentById(fragmentId);
		fragmentKategorie.removeCategory(category);
	}

}
