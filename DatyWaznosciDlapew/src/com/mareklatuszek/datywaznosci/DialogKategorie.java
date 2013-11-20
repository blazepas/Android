package com.mareklatuszek.datywaznosci;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
	
	View viewToSetKat;
	LinearLayout kategorieRoot;
	LinearLayout kategorieChild;
	EditText categoryTxtBoxKat;
	Button dodajButtonKat;
	ListView categoryList;
	Button okButton, anulujButton;

	public DialogKategorie(Activity mActivity, View viewToSetKat) {
		super(mActivity);
		this.viewToSetKat = viewToSetKat;
		this.mActivity = mActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_kategorie);

		kategorieRoot = (LinearLayout) findViewById(R.id.kategorieRoot);
		okButton = (Button) findViewById(R.id.okButton);
		anulujButton = (Button) findViewById(R.id.cancelButton);
		
		okButton.setOnClickListener(this);
		anulujButton.setOnClickListener(this);
		
		initKategorieLay();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dodajButtonKat:
			String category = categoryTxtBoxKat.getText().toString();
			if (category.equals("")) {
				Toast.makeText(getContext(), "Proszę podać nazwę kategorii", 2000).show();
			} else {
				addCategory(category);
			}
			break;
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
				adapterCat = new AdapterDialogKategorie(mActivity, categories);
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
	}
	
		
}