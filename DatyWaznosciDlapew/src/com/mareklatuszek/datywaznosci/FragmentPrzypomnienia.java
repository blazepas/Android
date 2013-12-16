package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.PopupMenu.OnMenuItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.mareklatuszek.utilities.CommonUtilities;
import com.mareklatuszek.utilities.FinalVariables;

public class FragmentPrzypomnienia extends SherlockFragment implements FinalVariables, OnItemClickListener{
	
	View rootView;
	ListView listPow;
	AdapterDB adapterDb;
	AdapterPrzypomnienia adapterPrzyp;
	ArrayList<HashMap<String, String>> dataToAdpter = new ArrayList<HashMap<String,String>>();
	ArrayList<Product> products = new ArrayList<Product>();
	CommonUtilities utilities = new CommonUtilities();	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_przypomnienia, container, false);
		adapterDb = new AdapterDB(getActivity());
		
		utilities.setActionBarTitle("Przypomnienia", getSherlockActivity());
		
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
				
				break; // pokazuje tylko ten jeden produkt
			}
		}		
	}
		
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	    android.view.MenuInflater inflater = getActivity().getMenuInflater();
	    inflater.inflate(R.menu.popup_przypomnienia, menu);
	}
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		super.onContextItemSelected(item);		
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int pos = info.position;
		
	    switch (item.getItemId()) {
    	case R.id.usunPopup:
    		removePrzypomnienie(pos);
    		break;
    	}
	    return true;
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
				dataToAdpter = utilities.sortPrzypomnieniaAll(fillPrzypomnienia(products));
				adapterPrzyp = new AdapterPrzypomnienia(getActivity(), dataToAdpter);
				return null;
			}
			
			@Override
			protected void onPostExecute(Void v) {
				listPow.setAdapter(adapterPrzyp);
				listPow.setOnItemClickListener(FragmentPrzypomnienia.this);
				registerForContextMenu(listPow);
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
			utilities.cancelAlarm(alarmTime, productId, getActivity());
			initList();
		}
	}
}
