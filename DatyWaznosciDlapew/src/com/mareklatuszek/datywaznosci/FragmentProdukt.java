package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mareklatuszek.utilities.BitmapLoader;
import com.mareklatuszek.utilities.CommonUtilities;
import com.mareklatuszek.utilities.FinalVariables;
import com.mareklatuszek.utilities.PremiumUtilities;

public class FragmentProdukt extends SherlockFragment implements OnClickListener {
	
	private Product product = new Product();
	CommonUtilities utilities = new CommonUtilities();
	AdapterProduktPrzypomnienia adapterPrzyp;
	
	View rootView;
	LinearLayout layDodatkoweShow, dodatkowe, przypomnieniaLayout;
	TextView nazwaTxt, okresTxt, dataOtwTxt, terminWazTxt, kategoriaTxt, opisTxt, dataZuzTxt, isScannedTxt;
	ImageView barcodeImage, obrazekImage;
	
	Bitmap codeBmp;
	Bitmap imageBmp;
	
	@Override
	public void onResume() {
		super.onResume();
		utilities.setActionBarTitle("Podgląd produktu", getSherlockActivity());
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_produkt, container, false);
		
		Bundle extras = getArguments();
		if (extras != null) {
			product = (Product) extras.getSerializable("product");	
			
	        setHasOptionsMenu(true);
			
			initPodstawowe();
			initDodatkowe();
		} else {
			switchToProductsFragment();
		}
		
		return rootView;
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      inflater.inflate(R.menu.items_product, menu);
      try {
    	  boolean deviceHasMenuButton = ViewConfiguration.get(getActivity()).hasPermanentMenuKey();
          if (!deviceHasMenuButton) {
        	  menu.removeItem(R.id.editMenuButton);
        	  menu.removeItem(R.id.shareMenuButton);
        	  menu.removeItem(R.id.deleteMenuButton);
          }
      } catch (NoSuchMethodError e) {
    	  Log.i("Fragment Produkt", "onCreateOptionsMenu ERROR");
      }
      

      super.onCreateOptionsMenu(menu, inflater);
    }
        
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
       switch (item.getItemId()) {
          case R.id.edit:
          case R.id.editMenuButton:
        	switchToEditFragment(product);
            break;
          case R.id.share:
          case R.id.shareMenuButton:
        	  if (PremiumUtilities.APP_VERSION_NONE) {
        		  Toast.makeText(getActivity(), "Aby korzystać z tej funkcji należy wykupic wersję premium", 2000).show();
        	  } else {
        		  DialogShare dialogShare = new DialogShare(getActivity(), product);
            	  dialogShare.show();
        	  } 
            break;
          case R.id.delete:
          case R.id.deleteMenuButton:
        	 showChoiceDeleteDialog(product);
            break;
       }
       return true;
    }
    
    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.barcodeImage:
			DialogObrazek showBarcode = new DialogObrazek(getActivity(), codeBmp);
			showBarcode.show();
			break;
		case R.id.obrazekImage:
			DialogObrazek showImage = new DialogObrazek(getActivity(), imageBmp);
			showImage.show();
			break;
		}
	}
	
	private void initPodstawowe() {
		
		nazwaTxt = (TextView) rootView.findViewById(R.id.nazwaTxt);
		okresTxt = (TextView) rootView.findViewById(R.id.okresTxt);
		isScannedTxt = (TextView) rootView.findViewById(R.id.isScannedTxt);
		barcodeImage = (ImageView) rootView.findViewById(R.id.barcodeImage);
		
		String nazwa = product.getNazwa();
		String okres = product.getOkresWaznosci();
		String code = product.getCode();
		String codeFormat = product.getCodeFormat();
		codeBmp = utilities.encodeCodeToBitmap(code, codeFormat, getActivity());
		
		nazwaTxt.setText(nazwa);
		okresTxt.setText(okres);
		barcodeImage.setImageBitmap(codeBmp);
		barcodeImage.setOnClickListener(this);
		if (product.getIsScanned()) {
			isScannedTxt.setText("Zeskanowany");
		} else {
			isScannedTxt.setText("Własny produkt");
		}
		
	}
	
	private void initDodatkowe() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		layDodatkoweShow = (LinearLayout) inflater.inflate(R.layout.lay_dodatkowe_show, null);		
		dodatkowe = (LinearLayout) rootView.findViewById(R.id.dodatkowe);	
		dodatkowe.addView(layDodatkoweShow);
		
		dataOtwTxt = (TextView) dodatkowe.findViewById(R.id.dataOtwTxt);
		terminWazTxt = (TextView) dodatkowe.findViewById(R.id.terminWazTxt);
		kategoriaTxt = (TextView) dodatkowe.findViewById(R.id.kategoriaTxt);
		opisTxt = (TextView) dodatkowe.findViewById(R.id.opisTxt);
		przypomnieniaLayout = (LinearLayout) dodatkowe.findViewById(R.id.przypomnieniaLayout);
		obrazekImage = (ImageView) dodatkowe.findViewById(R.id.obrazekImage);
		dataZuzTxt = (TextView) dodatkowe.findViewById(R.id.dataZuzTxt);
		
		String dataOtw = product.getDataOtwarcia();
		String terminWaz = product.getTerminWaznosci();
		String kategoria = product.getKategoria();
		String opis = product.getOpis();
		String image = product.getImage();
		String dataZuz = product.getDataZuzycia();
		
		if (!image.equals("")) {
			String imagePath = product.getImage();
			imageBmp = BitmapLoader.loadBitmap(imagePath, 100, 100);
			obrazekImage.setImageBitmap(imageBmp);
			obrazekImage.setOnClickListener(this);
		}
		
		
		dataOtwTxt.setText(dataOtw);
		terminWazTxt.setText(terminWaz);
		kategoriaTxt.setText(kategoria);
		opisTxt.setText(opis);
		dataZuzTxt.setText(dataZuz);
		initPrzypomnienia();
		
	}
	
	private void initPrzypomnienia() {
		ArrayList<HashMap<String, String>> przypomnienia = product.getPrzypomnienia();
		ArrayList<HashMap<String, String>> sortedPrzypomnienia = utilities.sortPrzypomnieniaAll(przypomnienia);
		adapterPrzyp = new AdapterProduktPrzypomnienia(getActivity(), sortedPrzypomnienia);
		
		for (int i = 0; i < adapterPrzyp.getCount(); i++)
		{
			View item = adapterPrzyp.getView((i), null, null);
			przypomnieniaLayout.addView(item);
		}
	}
	
	private void showChoiceDeleteDialog(final Product product) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle("Usuwanie");
		dialog.setMessage("Czy na pewno usunąć " + product.getNazwa() + "?");
		dialog.setPositiveButton("Tak",new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteProduct(product);
			}
		});

		dialog.setNegativeButton("Anuluj",new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
	
	private void switchToEditFragment(Product product) {
		((MainActivity) getActivity()).selectFragmentToEditProduct(product);
	}
	
	private void switchToProductsFragment() {
		((MainActivity) getActivity()).selectFragment(2);;
	}
	
	private void deleteProduct(Product product) {
		AdapterDB dbAdapter = new AdapterDB(getActivity());
		dbAdapter.open();
		boolean deleteStatus = dbAdapter.deleteProduct(product);
		dbAdapter.close();
		
		if (deleteStatus) {
			ArrayList<HashMap<String, String>> przypomnienia = product.getPrzypomnienia();
			String codeId = product.getCode();
			utilities.cancelAlarms(przypomnienia, codeId, getActivity());
			Toast.makeText(getActivity(), "Usunięto " + product.getNazwa(), 2000).show();
			
			switchToProductsFragment();
		} else {
			Toast.makeText(getActivity(), "Usuwanie zakończone niepowodzeniem", 2000).show();
		}
	}

}
