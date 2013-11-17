package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;

import android.os.Bundle;
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
	ArrayList<String> categories = new ArrayList<String>();
			
	EditText categoryTxtBoxKat;
	Button dodajButtonKat;
	ListView categoryList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_kategorie, container, false);
		
		categoryTxtBoxKat = (EditText) rootView.findViewById(R.id.categoryTxtBoxKat);
		dodajButtonKat = (Button) rootView.findViewById(R.id.dodajButtonKat);
		categoryList = (ListView) rootView.findViewById(R.id.categoryList);
		
		//kategorie chyba w dialogu lepiej daæ
		//TODO dorobiæ adapter
		
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
				//TODO odswierz liste
			}
			break;
		}
		
	}
	
	private boolean addCategory(String category) {
		adapterDb.open();
		boolean status = adapterDb.insertCategory(category);
		adapterDb.close();		
		return status;
	}

	

}
