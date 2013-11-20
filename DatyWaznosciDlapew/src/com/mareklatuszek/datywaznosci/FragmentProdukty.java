package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class FragmentProdukty extends SherlockFragment {
	
	AdapterDB dbAdapter;
	AdapterProductList listAdapter;
	ArrayList<Product> products = new ArrayList<Product>();
	
	ListView productsList;
	View rootView;
	Button footer;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.fragment_produkty, container, false);
		
		initList();
		
		return rootView;
	}
	
	public void onResume() {
		super.onResume();
		
	}
	
	private void initList() {
		new AsyncTask<Void, Void, Void>() {
			
			@Override
			protected void onPreExecute() {
				productsList = (ListView) rootView.findViewById(R.id.productsList);
				
				//TODO zrobic oddzielny layout
				footer = new Button(getActivity());
				footer.setText("Dodaj");
				footer.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						((MainActivity) getActivity()).selectFragment(1);
					}
				});
				//TODO
				
				productsList.addFooterView(footer);
			}

			@Override
			protected Void doInBackground(Void... params) {
				dbAdapter = new AdapterDB(getActivity());
				dbAdapter.open();
				products = dbAdapter.getAllProducts();
				dbAdapter.close();
				return null;
			}
			
			@Override
			protected void onPostExecute(Void v) {
				listAdapter = new AdapterProductList(getActivity(), products);
				productsList.setAdapter(listAdapter);
				productsList.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
							switchToProductFragment(pos);
							Log.i("itemClick", pos+"");
					}
				});
			}
		}.execute();
	}
	
	private void switchToProductFragment(int positon) {
		Product product = products.get(positon);
		((MainActivity) getActivity()).selectFragmentToShowProduct(product);
	}

}
