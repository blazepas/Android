package pl.jacek.jablonka.android.tpp.dialogs;

import java.util.ArrayList;

import pl.jacek.jablonka.android.tpp.R;
import pl.jacek.jablonka.android.tpp.atapters.AdapterCustomSpinner;
import pl.jacek.jablonka.android.tpp.atapters.AdapterDB;
import pl.jacek.jablonka.android.tpp.atapters.AdapterDialogKategorie;
import pl.jacek.jablonka.android.tpp.views.CustomSpinner;
import pl.jacek.jablonka.android.tpp.views.EditTextBariol;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class DialogKategorie extends Dialog 
		implements android.view.View.OnClickListener, OnItemClickListener {	
	
	AdapterDB adapterDb;
	AdapterDialogKategorie adapterCat;
	AdapterCustomSpinner kategorieAdapter;
	ArrayList<String> categories = new ArrayList<String>();
	FragmentActivity mActivity;
	
	CustomSpinner spinnerKategorie;
	LinearLayout kategorieRoot, kategorieChild, okButton;
	EditTextBariol categoryTxtBoxKat;
	ImageView addCat;
	ListView categoryList;

	public DialogKategorie(FragmentActivity mActivity, 
			CustomSpinner spinnerKategorie, AdapterCustomSpinner kategorieAdapter) {
		super(mActivity);
		this.spinnerKategorie = spinnerKategorie;
		this.mActivity = mActivity;
		this.kategorieAdapter = kategorieAdapter;
		this.categories = kategorieAdapter.getArrayListData();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_kategorie);

		kategorieRoot = (LinearLayout) findViewById(R.id.kategorieRoot);
		okButton = (LinearLayout) findViewById(R.id.okButton);
		
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
			add();
			break;	
		}		
	}
	

	@Override
	public void onItemClick(AdapterView<?> arg0, View vi, int pos, long arg3) {
		String category = (String) vi.getTag();
		spinnerKategorie.setText(category);
		dismiss();
	}	
	
	private void add() {
		String category = categoryTxtBoxKat.getText().toString();
		if (category.equals("")) {
			String message = mActivity.getString(R.string.toast_empty_category_name);
			Toast.makeText(mActivity, message, 2000).show();
		} else {
			if (categories.contains(category)) {
				String message = mActivity.getString(R.string.toast_category_is_in_db);
				Toast.makeText(mActivity, message, 2000).show();
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
				categoryList.setOnItemClickListener(DialogKategorie.this);
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
			spinnerKategorie.setText(category);
			dismiss();
		}
		
		return status;
	}	
}