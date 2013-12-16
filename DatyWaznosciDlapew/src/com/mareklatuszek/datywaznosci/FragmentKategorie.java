package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.mareklatuszek.utilities.CommonUtilities;

public class FragmentKategorie extends SherlockFragment implements OnClickListener, OnItemLongClickListener{
	
	AdapterDB adapterDb;
	AdapterKategorie adapterCat;
	ArrayList<String> categories = new ArrayList<String>();
	
	View rootView;
	EditText categoryTxtBoxKat;
	Button dodajButtonKat;
	ListView categoryList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_kategorie, container, false);
		
		CommonUtilities utilities = new CommonUtilities();
		utilities.setActionBarTitle("Kategorie", getSherlockActivity());
		
		categoryTxtBoxKat = (EditText) rootView.findViewById(R.id.categoryTxtBoxKat);
		dodajButtonKat = (Button) rootView.findViewById(R.id.dodajButtonKat);
		dodajButtonKat.setOnClickListener(this);
		
		initList();
				
		return rootView;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dodajButtonKat:
			add();
			break;
		}
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, final View v, final int pos, long arg3) {
		Vibrator vibe = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
		vibe.vibrate(25);
		
//        PopupMenu popup = new PopupMenu(getActivity(), v);
//        popup.getMenuInflater().inflate(R.menu.popupCategories, popup.getMenu());
//        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//
//            @Override
//            public boolean onMenuItemClick(android.view.MenuItem item) {
//            	String category = categories.get(pos);
//            	switch (item.getItemId()) {
//            	case R.id.edytujPopup:
//            		editCategory(category);
//            		break;
//            	case R.id.usunPopup:
//            		removeCategory(category);
//            		break;
//            	}
//            	           	
//                return true;
//            }
//        });
//
//        popup.show();
		
		return false;
	}
	
	private void add() {
		String category = categoryTxtBoxKat.getText().toString();
		if (category.equals("")) {
			Toast.makeText(getActivity(), "Proszę podać nazwę kategorii", 2000).show();
		} else {
			if (categories.contains(category)) {
				Toast.makeText(getActivity(), "Podana kategoria jest już w bazie", 2000).show();
			} else {
				addCategory(category);
				categoryTxtBoxKat.setText("");
			}	
		}	
	}
	
	private void initList() {
		
		new AsyncTask<Void, Void, Void>() {
			
			@Override
			protected void onPreExecute() {
				adapterDb = new AdapterDB(getActivity());
				categoryList = (ListView) rootView.findViewById(R.id.categoryList);
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				adapterDb.open();
				categories = adapterDb.getAllCategories();
				adapterDb.close();
				return null;
			}
			
			@Override
			protected void onPostExecute(Void v) {
				adapterCat = new AdapterKategorie(getActivity(), categories, getFragmentManager(), getId());
				categoryList.setAdapter(adapterCat);
			}
		}.execute();
	}
	
	private boolean addCategory(String category) {
		adapterDb.open();
		boolean status = adapterDb.insertCategory(category);
		adapterDb.close();
		if (status) {
			categories.add(0, category);
		}
		refreshCategories();
		return status;
	}
	
	public boolean removeCategory(String category) {
		adapterDb.open();
		categories.remove(category);
		boolean status = adapterDb.deleteCategory(category);
		adapterDb.close();
		if(status) {
			categories.remove(category);
			Log.i("remove", category);
		}
		refreshCategories();
		return status;
	}
	
//	private edit(String category) {
//		//TODO dialog
//	}
	
	public void refreshCategories() {
		adapterCat.notifyDataSetChanged();
	}

}
