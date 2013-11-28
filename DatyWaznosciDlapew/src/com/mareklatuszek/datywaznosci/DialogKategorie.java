package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class DialogKategorie extends Dialog implements android.view.View.OnClickListener {	
	
	AdapterDB adapterDb;
	AdapterDialogKategorie adapterCat;
	ArrayList<String> categories = new ArrayList<String>();
	Activity mActivity;
	FragmentManager fragmentManager; 
	int fragmentId;
	
	View viewToSetKat;
	LinearLayout kategorieRoot;
	LinearLayout kategorieChild;
	EditText categoryTxtBoxKat;
	Button dodajButtonKat;
	ListView categoryList;
	Button okButton;

	public DialogKategorie(Activity mActivity, View viewToSetKat, FragmentManager fragmentManager, int fragmentId) {
		super(mActivity);
		this.viewToSetKat = viewToSetKat;
		this.mActivity = mActivity;
		this.fragmentManager = fragmentManager;
		this.fragmentId = fragmentId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Kategorie");
		setContentView(R.layout.dialog_kategorie);

		kategorieRoot = (LinearLayout) findViewById(R.id.kategorieRoot);
		okButton = (Button) findViewById(R.id.okButton);
		
		okButton.setOnClickListener(this);
		
		initKategorieLay();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dodajButtonKat:
			add();
			break;
		case R.id.okButton:
			dismiss();
			break;	
		}		
	}
	
	private void add() {
		String category = categoryTxtBoxKat.getText().toString();
		if (category.equals("")) {
			Toast.makeText(mActivity, "Proszę podać nazwę kategorii", 2000).show();
		} else {
			if (categories.contains(category)) {
				Toast.makeText(mActivity, "Podana kategoria jest już w bazie", 2000).show();
			} else {
				addCategory(category);
				categoryTxtBoxKat.setText("");
			}	
		}	
	}
	
	private void initKategorieLay() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		kategorieChild = (LinearLayout) inflater.inflate(R.layout.fragment_kategorie, null);			
		kategorieRoot.addView(kategorieChild);
		
		categoryTxtBoxKat = (EditText) kategorieChild.findViewById(R.id.categoryTxtBoxKat);
		dodajButtonKat = (Button) kategorieChild.findViewById(R.id.dodajButtonKat);
		dodajButtonKat.setOnClickListener(this);
		
		initList();
	}
	
	private void initList() {
		
		new AsyncTask<Void, Void, Void>() {
			
			@Override
			protected void onPreExecute() {
				adapterDb = new AdapterDB(mActivity);
				categoryList = (ListView) kategorieChild.findViewById(R.id.categoryList);
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
				adapterCat = new AdapterDialogKategorie(mActivity, categories, viewToSetKat);
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
			adapterCat.notifyDataSetChanged();
			setSpinner();
			dismiss();
		}
		
		return status;
	}
	
	private void setSpinner() {
		Spinner spinner = (Spinner) viewToSetKat;
		
		ArrayList<String> kategorie = new ArrayList<String>();
		kategorie.add("Brak kategorii");
		kategorie.addAll(adapterCat.getData());
		ArrayAdapter<String> spinnerAdapter;
		spinnerAdapter= new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_dropdown_item, kategorie);
		spinner.setAdapter(spinnerAdapter);
		
		if (kategorie.size() > 0) {
			spinner.setSelection(1);
		}
	}
	
		
}