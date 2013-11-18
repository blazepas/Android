package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class FragmentKategorie extends SherlockFragment implements OnClickListener{
	
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
			String category = categoryTxtBoxKat.getText().toString();
			if (category.equals("")) {
				Toast.makeText(getActivity(), "Proszê podaæ nazwê kategorii", 2000).show();
			} else {
				addCategory(category);
			}
			break;
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
			categories.add(category);
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
	
	public void refreshCategories() {
		adapterCat.notifyDataSetChanged();
		//TODO odswierzanie we fragmencie Dodaj
	}

	

}
