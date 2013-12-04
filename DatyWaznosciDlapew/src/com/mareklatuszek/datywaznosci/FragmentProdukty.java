package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mareklatuszek.utilities.CommonUtilities;
import com.mareklatuszek.utilities.JavaMail;

public class FragmentProdukty extends SherlockFragment {
	
	AdapterDB dbAdapter;
	AdapterProductList listAdapter;
	ArrayList<Product> products = new ArrayList<Product>();
	CommonUtilities utilities = new CommonUtilities();
	
	ListView productsList;
	View rootView;
	LinearLayout footer;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);
		
		getSherlockActivity().getSupportActionBar().setTitle("Lista produktów");
		
		rootView = inflater.inflate(R.layout.fragment_produkty, container, false);

		new InitList().execute();
		
		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    android.view.MenuInflater inflater = getActivity().getMenuInflater();
	    inflater.inflate(R.menu.popup_product_list, menu);
	}
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		super.onContextItemSelected(item);
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
	    int pos = info.position;
	    
	    Product product = products.get(pos);
    	switch (item.getItemId()) {
    	case R.id.udostepnijPopup:
    		DialogShare dialogShare = new DialogShare(getActivity(), product);
       	  	dialogShare.show();
    		break;
    	case R.id.edytujPopup:
    		switchToEditFragment(product);
    		break;
    	case R.id.usunPopup:
    		deleteProduct(product);
    		break;
    	}
	    return true;
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      inflater.inflate(R.menu.items_product_list, menu);

      super.onCreateOptionsMenu(menu, inflater);
    }
            
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
       switch (item.getItemId()) {
          case R.id.share:
          case R.id.shareMenuButton:
          	  DialogShare dialogShare = new DialogShare(getActivity(), products);
          	  dialogShare.show();
            break;
          case R.id.scan:
          case R.id.scanMenuButton:
        	  scanCode();
            break;
          case R.id.add:
          case R.id.addMenuButton:
        	  selectFragmentDodaj();
            break;
       }
       return true;
    }
    
    private class InitList extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			productsList = (ListView) rootView.findViewById(R.id.productsList);
			footer = initFooter();
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
			if (!products.isEmpty()) {
				listAdapter = new AdapterProductList(getActivity(), products, getFragmentManager(), getId());
				productsList.setAdapter(listAdapter);
//				productsList.setOnItemClickListener(new OnItemClickListener() {
//
//                    @Override
//                    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
//                        switchToProductFragment(pos);
//                    }
//				});
				
				registerForContextMenu(productsList); // TODO prawdopodobnie nie bedzie uzywane
			}
		}
	}
	
	private LinearLayout initFooter() {
		
		//TODO zrobic oddzielny layout
		
		LinearLayout footerLay = new LinearLayout(getActivity());
		Button skanowanieButton = new Button(getActivity());
		Button wlasnyButton = new Button(getActivity());
		
		footerLay.setOrientation(LinearLayout.VERTICAL);
		
		skanowanieButton.setText("Skanowanie produktu");
		wlasnyButton.setText("Własny produkt");
		
		skanowanieButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				scanCode();
			}
		});
		
		wlasnyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectFragmentDodaj();
			}
		});
		
		footerLay.addView(skanowanieButton);
		footerLay.addView(wlasnyButton);
		
		return footerLay;
	}
    
	public void switchToProductFragment(int positon) {
		Product product = products.get(positon);
		((MainActivity) getActivity()).selectFragmentToShowProduct(product);
	}

	private void switchToEditFragment(Product product) {
		((MainActivity) getActivity()).selectFragmentToEditProduct(product);
	}
			
	public void deleteProduct(Product product) {
		dbAdapter.open();
		boolean deleteStatus = dbAdapter.deleteProduct(product);
		dbAdapter.close();
		
		if (deleteStatus) {
			products.remove(product);
			listAdapter.notifyDataSetChanged();
			ArrayList<HashMap<String, String>> przypomnienia = product.getPrzypomnienia();
			String codeId = product.getCode();
			removeAlarms(przypomnienia, codeId);
			listAdapter = new AdapterProductList(getActivity(), products, getFragmentManager(), getId());
			productsList.setAdapter(listAdapter);
		} else {
			Toast.makeText(getActivity(), "Usuwanie zakończone niepowodzeniem", 2000).show();
		}
	}
	
	private void removeAlarms(ArrayList<HashMap<String, String>> przypomnienia, String codeId) {
		utilities.cancelAlarms(przypomnienia, codeId, getActivity());
	}
		
	public void selectFragmentDodaj() {
		((MainActivity) getActivity()).selectFragment(1);
	}
	
	private void scanCode() {
		((MainActivity) getActivity()).selectFragment(0);
	}
}
