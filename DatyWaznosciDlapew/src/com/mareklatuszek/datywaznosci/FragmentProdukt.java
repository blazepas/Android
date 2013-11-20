package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.mareklatuszek.datywznosci.utilities.CommonUtilities;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class FragmentProdukt extends SherlockFragment {
	
	private Product product = new Product();
	CommonUtilities utilities = new CommonUtilities();
	AdapterProduktPrzypomnienia adapterPrzyp;
	
	View rootView;
	LinearLayout layDodatkoweShow, dodatkowe, przypomnieniaLayout;
	TextView nazwaTxt, okresTxt, dataOtwTxt, terminWazTxt, kategoriaTxt, opisTxt;
	ImageView barcodeImage, obrazekImage;
	
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_produkt, container, false);
				
		Bundle extras = getArguments();
		product = (Product) extras.getSerializable("product");	
		
        setHasOptionsMenu(true);
		
		initPodstawowe();
		initDodatkowe();
		
		return rootView;
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      inflater.inflate(R.menu.items, menu);

      super.onCreateOptionsMenu(menu, inflater);
    }
        
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
       switch (item.getItemId()) {
          case R.id.edit:
        	switchToEditFragment(product);
            break;
          case R.id.share:
          	sendEmail();
            break;
       }
       return true;
    }
	
	private void initPodstawowe() {
		
		nazwaTxt = (TextView) rootView.findViewById(R.id.nazwaTxt);
		okresTxt = (TextView) rootView.findViewById(R.id.okresTxt);
		barcodeImage = (ImageView) rootView.findViewById(R.id.barcodeImage);
		
		String nazwa = product.getNazwa();
		String okres = product.getOkresWaznosci();
		String code = product.getCode();
		String codeFormat = product.getCodeFormat();
		Bitmap codeBmp = utilities.encodeCodeToBitmap(code, codeFormat, getActivity());
		
		nazwaTxt.setText(nazwa);
		okresTxt.setText(okres);
		barcodeImage.setImageBitmap(codeBmp);
		
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
		
		String dataOtw = product.getDataOtwarcia();
		String terminWaz = product.getTerminWaznosci();
		String kategoria = product.getKategoria();
		String opis = product.getOpis();
		
		
		dataOtwTxt.setText(dataOtw);
		terminWazTxt.setText(terminWaz);
		kategoriaTxt.setText(kategoria);
		opisTxt.setText(opis);
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
	
	private void switchToEditFragment(Product product) {
		((MainActivity) getActivity()).selectFragmentToEditProduct(product);
	}
	
	private void sendEmail() {
		String json = utilities.getJsonFromProduct(product);

		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"marek.lat@gmail.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, "Śmiało wysyłać, dam znać czy działa");
		i.putExtra(Intent.EXTRA_TEXT   , json);
		try {
		    startActivity(Intent.createChooser(i, "Wysyłanie..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(getActivity(), "Brak klientów email", Toast.LENGTH_SHORT).show();
		}
	}

}
