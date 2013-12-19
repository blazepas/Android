package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.json.JSONArray;

import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mareklatuszek.utilities.CommonUtilities;
import com.mareklatuszek.utilities.JavaMail;
import com.mareklatuszek.utilities.PremiumUtilities;

public class FragmentProdukty extends SherlockFragment implements OnClickListener {
	
	AdapterDB dbAdapter;
	AdapterProductList listAdapter;
	AdapterCustomSpinner adapterKategorieSpinner;
	ArrayList<Product> products = new ArrayList<Product>();
	ArrayList<Product> productsTemp = new ArrayList<Product>();
	ArrayList<String> categories = new ArrayList<String>();
	CommonUtilities utilities = new CommonUtilities();
	FragmentManager fM;
	
	ListView productsList;
	View rootView;
	LinearLayout dodajLay, kategorieDropDown;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);
		fM = getFragmentManager();

		utilities.setActionBarTitle("Lista produktów", getSherlockActivity());
		
		rootView = inflater.inflate(R.layout.fragment_produkty, container, false);
		dodajLay = (LinearLayout) rootView.findViewById(R.id.dodajLay);
		dodajLay.setOnClickListener(this);

		new InitList().execute(); //TODO dodać synchronizację do adapterDb
		
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
    		if (PremiumUtilities.APP_VERSION_NONE) {
      		  	Toast.makeText(getActivity(), "Aby korzystać z tej funkcji należy wykupic wersję premium", 2000).show();
      	  	} else {
      	  		DialogShare dialogShare = new DialogShare(getActivity(), product);
      	  		dialogShare.show();
      	  	} 
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
      try {
    	  boolean deviceHasMenuButton = ViewConfiguration.get(getActivity()).hasPermanentMenuKey();
          if (!deviceHasMenuButton) {
        	  menu.removeItem(R.id.shareMenuButton);
        	  menu.removeItem(R.id.scanMenuButton);
        	  menu.removeItem(R.id.addMenuButton);
          }
      } catch (NoSuchMethodError e) {
    	  Log.i("Fragment Produkty", "onCreateOptionsMenu ERROR");
      }
      

      super.onCreateOptionsMenu(menu, inflater);
    }
            
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
       switch (item.getItemId()) {
          case R.id.share:
          case R.id.shareMenuButton:
        	  if (products.size() > 0) {
        		  DialogShare dialogShare = new DialogShare(getActivity(), products);
              	  dialogShare.show();
        	  } else {
        		  Toast.makeText(getActivity(), "Brak produktów do udostępnienia", 2000).show();
        	  }
          	  
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
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dodajLay:
			DialogDodajProdukt dialog = new DialogDodajProdukt(getActivity());
			dialog.show();
			break;
		case R.id.kategorieDropDown:
			showSpinnerPopUp(v);
			break;
		}
		
	}
	
	private ArrayList<Product> getSortedList(String choice) {
		ArrayList<Product> sorted = new ArrayList<Product>();
		for (int i = 0; i < productsTemp.size(); i++) {
			Product product = productsTemp.get(i);
			String category = product.getKategoria();
			if (category.equals(choice)) {
				sorted.add(product);
			}
		}
		return sorted;
	}
	
	private void showSpinnerPopUp(final View clickedView) {

        final PopupWindow popupWindow = new PopupWindow(getActivity());
        ListView spinnerList = new ListView(getActivity());
        ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.dropdown));
        
        switch (clickedView.getId()) {
	 	case R.id.kategorieDropDown:
	     	spinnerList.setAdapter(adapterKategorieSpinner);
	     	spinnerList.setOnItemClickListener(new OnItemClickListener() {
	
	 			@Override
	 			public void onItemClick(AdapterView<?> arg0, View view, int arg2,long arg3) {
	 				String choice = (String) view.getTag();
	 				setCustomSpinner(choice, kategorieDropDown);
	 				popupWindow.dismiss();
	 				products = getSortedList(choice);
	 				listAdapter = new AdapterProductList(getActivity(), products, fM, getId(), productsList);
					productsList.setAdapter(listAdapter);
	 			}
	 		});
	     	break;
        }

        popupWindow.setFocusable(true); 
        popupWindow.setBackgroundDrawable(background); //TODO
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(clickedView.getLayoutParams().width);
        popupWindow.setContentView(spinnerList);

        popupWindow.showAsDropDown(clickedView);
    }
	
	private class InitSort extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {			
			kategorieDropDown = (LinearLayout) rootView.findViewById(R.id.kategorieDropDown);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {

				dbAdapter = new AdapterDB(getActivity());
				dbAdapter.open();
				categories = dbAdapter.getAllCategories();
				dbAdapter.close();
				
			} catch (Exception e) {
				// TODO
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			setCustomSpinner("wybierz kategorię", kategorieDropDown);
			adapterKategorieSpinner = new AdapterCustomSpinner(getActivity(), categories);
			kategorieDropDown.setOnClickListener(FragmentProdukty.this);
		}
	}
    
    private class InitList extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			productsList = (ListView) rootView.findViewById(R.id.productsList);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				dbAdapter = new AdapterDB(getActivity());
				dbAdapter.open();
				products = dbAdapter.getAllProducts();
				productsTemp = products;
				dbAdapter.close();
			} catch (Exception e) {
				// TODO
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			if (!products.isEmpty()) {
				listAdapter = new AdapterProductList(getActivity(), products, fM, getId(), productsList);
				productsList.setAdapter(listAdapter);
				
				registerForContextMenu(productsList); // TODO prawdopodobnie nie bedzie uzywane
			}
			
			new InitSort().execute();// lista kategorii
		}
	}
    
	private void setCustomSpinner(String title, LinearLayout viewToSet) {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		LinearLayout spinner = (LinearLayout) inflater.inflate(R.layout.spinner_button, null);
		
		TextView spinnerTxt = (TextView) spinner.findViewById(R.id.spinnerTxt);
		spinnerTxt.setText(title);
		
		viewToSet.removeAllViewsInLayout();
		viewToSet.addView(spinner);
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
			
			listAdapter = new AdapterProductList(getActivity(), products, fM, getId(), productsList);
			productsList.setAdapter(listAdapter);
			
			Toast.makeText(getActivity(), "Usunięto " + product.getNazwa(), 2000).show();
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
