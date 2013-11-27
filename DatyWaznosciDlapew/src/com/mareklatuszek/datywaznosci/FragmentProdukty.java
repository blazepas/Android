package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mareklatuszek.utilities.CommonUtilities;

public class FragmentProdukty extends SherlockFragment implements OnItemLongClickListener {
	
	AdapterDB dbAdapter;
	AdapterProductList listAdapter;
	ArrayList<Product> products = new ArrayList<Product>();
	CommonUtilities utilities = new CommonUtilities();
	
	ListView productsList;
	View rootView;
	Button footer;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);
		
		getSherlockActivity().getSupportActionBar().setTitle("Lista produktów");
		
		rootView = inflater.inflate(R.layout.fragment_produkty, container, false);

		new InitList().execute();
		
		return rootView;
	}
	
	public void onResume() {
		super.onResume();		
	}
		
	private void switchToProductFragment(int positon) {
		Product product = products.get(positon);
		((MainActivity) getActivity()).selectFragmentToShowProduct(product);
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
          case R.id.share2:
          	DialogShare dialogShare = new DialogShare(getActivity(), getFragmentManager(), getId());
          	dialogShare.show();
            break;
       }
       return true;
    }

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, final View v, final int pos, long arg3) {
		
        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
            	Product product = products.get(pos);
            	switch (item.getItemId()) {
            	case R.id.udostepnijPopup:
            		share(product);
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
        });

        popup.show();
		
		return true;
	}

	private void switchToEditFragment(Product product) {
		((MainActivity) getActivity()).selectFragmentToEditProduct(product);
	}
	
	private void share(Product product) {
		String productJson = utilities.getJsonFromProduct(product);
		utilities.sendEmail(productJson, getActivity());
	}
	
	public boolean shareAllProducts(String email) {
		JSONArray productList = utilities.getProductsTableToShare(products);
		String url = getResources().getString(R.string.send_products_list_url);
		boolean emailStatus = utilities.postData(email, productList, url);
		return emailStatus;
	}
	
	private void deleteProduct(Product product) {
		dbAdapter.open();
		boolean deleteStatus = dbAdapter.deleteProduct(product);
		dbAdapter.close();
		
		if (deleteStatus) {
			products.remove(product);
			listAdapter.notifyDataSetChanged();
			ArrayList<HashMap<String, String>> przypomnienia = product.getPrzypomnienia();
			String codeId = product.getCode();
			removeAlarms(przypomnienia, codeId);
		} else {
			Toast.makeText(getActivity(), "Usuwanie zakończone niepowodzeniem", 2000).show();
		}
	}
	
	private void removeAlarms(ArrayList<HashMap<String, String>> przypomnienia, String codeId) {
		utilities.cancelAlarms(przypomnienia, codeId, getActivity());
	}
	
	private class InitList extends AsyncTask<Void, Void, Void> {
		
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
			if (!products.isEmpty()) {
				listAdapter = new AdapterProductList(getActivity(), products);
				productsList.setAdapter(listAdapter);
				productsList.setOnItemLongClickListener(FragmentProdukty.this);
				productsList.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                        switchToProductFragment(pos);
                    }
				});
			}
		}
	}
}
