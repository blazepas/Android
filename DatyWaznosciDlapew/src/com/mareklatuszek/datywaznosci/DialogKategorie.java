package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;
import java.util.List;

import com.mareklatuszek.utilities.EditTextBariol;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DialogKategorie extends Dialog implements android.view.View.OnClickListener {	
	
	AdapterDB adapterDb;
	AdapterDialogKategorie adapterCat;
	AdapterCustomSpinner kategorieAdapter;
	ArrayList<String> categories = new ArrayList<String>();
	FragmentActivity mActivity;
	
	CustomSpinner spinnerKategorie;
	LinearLayout kategorieRoot;
	LinearLayout kategorieChild;
	EditTextBariol categoryTxtBoxKat;
	ImageView addCat;
	ListView categoryList;
	Button okButton;

	public DialogKategorie(FragmentActivity mActivity, CustomSpinner spinnerKategorie, AdapterCustomSpinner kategorieAdapter) {
		super(mActivity);
		this.spinnerKategorie = spinnerKategorie;
		this.mActivity = mActivity;
		this.kategorieAdapter = kategorieAdapter;
		this.categories = kategorieAdapter.getArrayListData();
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
		case R.id.addCat:
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
		
		categoryTxtBoxKat = (EditTextBariol) kategorieChild.findViewById(R.id.categoryTxtBoxKat);
		addCat = (ImageView) kategorieChild.findViewById(R.id.addCat);
		addCat.setOnClickListener(this);
		
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
				adapterCat = new AdapterDialogKategorie(mActivity, kategorieAdapter, spinnerKategorie);
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
			kategorieAdapter.setArrayListData(categories);
			adapterCat.notifyDataSetChanged();
			spinnerKategorie.setText(category);;
			dismiss();
		}
		
		return status;
	}		
}