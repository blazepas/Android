package pl.mareklatuszek.tpp.fragments;

import java.util.ArrayList;

import pl.mareklatuszek.tpp.TPPApp;
import pl.mareklatuszek.tpp.R;
import pl.mareklatuszek.tpp.atapters.AdapterDB;
import pl.mareklatuszek.tpp.atapters.AdapterKategorie;
import pl.mareklatuszek.tpp.utilities.CommonUtilities;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class FragmentKategorie extends SherlockFragment implements OnClickListener {
	
	AdapterDB adapterDb;
	AdapterKategorie adapterCat;
	ArrayList<String> categories = new ArrayList<String>();
	
	View rootView;
	EditText categoryTxtBoxKat;
	ImageView addCat;
	ListView categoryList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_kategorie, container, false);
		
		CommonUtilities utilities = TPPApp.getUtilities();
		String title = getString(R.string.frag_kategorie_title);
		utilities.setActionBarTitle(title, getSherlockActivity());
		
		categoryTxtBoxKat = (EditText) rootView.findViewById(R.id.categoryTxtBoxKat);
		addCat = (ImageView) rootView.findViewById(R.id.addCat);
		addCat.setOnClickListener(this);
		
		new InitList().execute();
				
		return rootView;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addCat:
			add();
			break;
		}
	}
		
	private void add() {
		String category = categoryTxtBoxKat.getText().toString();
		if (category.equals("")) {
			String message = getString(R.string.toast_empty_category_name);
			Toast.makeText(getActivity(), message, 2000).show();
		} else {
			if (categories.contains(category)) {
				String message = getString(R.string.toast_empty_category_name);
				Toast.makeText(getActivity(), message, 2000).show();
			} else {
				addCategory(category);
				categoryTxtBoxKat.setText("");
			}	
		}	
	}
	
	private class InitList extends AsyncTask<Void, Void, Void> {
		
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
	
	public void refreshCategories() {
		adapterCat.notifyDataSetChanged();
	}

}
