package com.mareklatuszek.datywaznosci;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;


import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.mareklatuszek.datywznosci.utilities.BitmapLoader;
import com.mareklatuszek.datywznosci.utilities.CommonUtilities;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class FragmentProdukt extends SherlockFragment implements OnClickListener {
	
	private Product product = new Product();
	CommonUtilities utilities = new CommonUtilities();
	AdapterProduktPrzypomnienia adapterPrzyp;
	
	View rootView;
	LinearLayout layDodatkoweShow, dodatkowe, przypomnieniaLayout;
	TextView nazwaTxt, okresTxt, dataOtwTxt, terminWazTxt, kategoriaTxt, opisTxt;
	ImageView barcodeImage, obrazekImage;
	
	Bitmap codeBmp;
	Bitmap imageBmp;
	
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_produkt, container, false);
		
		getSherlockActivity().getSupportActionBar().setTitle("Podgląd produktu");
				
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
        	String productJson = utilities.getJsonFromProduct(product);
          	utilities.sendEmail(productJson, getActivity());
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
		String image = product.getImage();
		
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
	
}
