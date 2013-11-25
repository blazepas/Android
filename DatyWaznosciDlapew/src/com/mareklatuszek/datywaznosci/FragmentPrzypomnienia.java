package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupMenu;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.mareklatuszek.datywznosci.utilities.CommonUtilities;
import com.mareklatuszek.datywznosci.utilities.FinalVariables;

public class FragmentPrzypomnienia extends SherlockFragment implements FinalVariables, OnItemClickListener, OnItemLongClickListener{
	
	View rootView;
	ListView listPow;
	AdapterDB adapterDb;
	AdapterPrzypomnienia adapterPrzyp;
	ArrayList<HashMap<String, String>> dataToAdpter = new ArrayList<HashMap<String,String>>();
	ArrayList<Product> products = new ArrayList<Product>();
	CommonUtilities utiliteis = new CommonUtilities();	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_przypomnienia, container, false);
		adapterDb = new AdapterDB(getActivity());
		
		getSherlockActivity().getSupportActionBar().setTitle("Przypomnienia");
		
		initList();
		
		return rootView;
	}	

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		HashMap<String, String> przypomnienie = (HashMap<String, String>) adapterPrzyp.getItem(pos);
		String przypomnienieCode = przypomnienie.get(DB_CODE);
		
		for (int i = 0; i < products.size(); i++) {
			Product product = products.get(i);
			String productCode = product.getCode();
			
			if (przypomnienieCode.contains(productCode)) {				
				
				DialogPrzypomnienie dialog = new DialogPrzypomnienie(getActivity(), product);
				dialog.show();
				
				break; // pokazyje tylko ten jeden produkt
			}
		}		
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View v, final int pos, long arg3) {
		Vibrator vibe = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
		vibe.vibrate(25);
		
        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
            	Product product = products.get(pos);
            	switch (item.getItemId()) {
            	case R.id.udostepnijPopup:

            		break;
            	case R.id.edytujPopup:
 
            		break;
            	case R.id.usunPopup:
            		removePrzypomnienie(pos);
            		break;
            	}
            	           	
                return true;
            }
        });

        popup.show();
		return false;
	}
	
	private void initList() {
		
		new AsyncTask<Void, Void, Void>() {
			
			@Override
			protected void onPreExecute() {
				listPow = (ListView) rootView.findViewById(R.id.listPow);
			}

			@Override
			protected Void doInBackground(Void... params) {				
				adapterDb.open();
				products = adapterDb.getAllProducts();
				adapterDb.close();
				dataToAdpter = utiliteis.sortPrzypomnieniaAll(fillPrzypomnienia(products));
				adapterPrzyp = new AdapterPrzypomnienia(getActivity(), dataToAdpter);
				return null;
			}
			
			@Override
			protected void onPostExecute(Void v) {
				listPow.setAdapter(adapterPrzyp);
				listPow.setOnItemClickListener(FragmentPrzypomnienia.this);
				listPow.setOnItemLongClickListener(FragmentPrzypomnienia.this);
			}
		}.execute();
		
	}
	
	private ArrayList<HashMap<String, String>> fillPrzypomnienia(ArrayList<Product> products) {
		
		ArrayList<HashMap<String, String>> przypomnieniaAll = new ArrayList<HashMap<String, String>>(); //wszystkie przypomnienia bez powiazania z produktem
		
		for (int i = 0; i < products.size(); i++) {
			
			Product product = products.get(i);
			ArrayList<HashMap<String,String>> przypomnienia = product.getPrzypomnienia(); //przypomnienia przypisane do produktu
			
			for(int a = 0; a < przypomnienia.size(); a++) {
				String nazwa = product.getNazwa();
				String dataOtw = product.getDataOtwarcia();
				String terminWaz = product.getTerminWaznosci();
				String przypDate = przypomnienia.get(a).get(PRZYP_DATE);
				String code = product.getCode();
				
				HashMap<String, String> przypomnienie = new HashMap<String, String>(); // przypomnienie jako oddzielny byt
				przypomnienie.put(DB_NAZWA, nazwa);
				przypomnienie.put(DB_DATA_OTWARCIA, dataOtw);
				przypomnienie.put(DB_TERMIN_WAZNOSCI, terminWaz);
				przypomnienie.put(PRZYP_DATE, przypDate);
				przypomnienie.put(DB_CODE, code);
				
				przypomnieniaAll.add(przypomnienie);				
			}
		}
		return przypomnieniaAll;
		
	}

	private void removePrzypomnienie(int pos) {
		HashMap<String, String> przypomnienie = (HashMap<String, String>) adapterPrzyp.getItem(pos);
		String alarmTime = przypomnienie.get(PRZYP_DATE);
		String productId = przypomnienie.get(DB_CODE);
		
		adapterDb.open();
		boolean removeStatus = adapterDb.deletePrzypomnienie(productId, alarmTime);
		adapterDb.close();
		
		if (removeStatus) {
			utiliteis.cancelAlarm(alarmTime, productId, getActivity());
			initList();
		}
	}
}
