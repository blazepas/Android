package com.mareklatuszek.datywaznosci;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

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
	
	Bitmap codeBmp;
	
		
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
		codeBmp = utilities.encodeCodeToBitmap(code, codeFormat, getActivity());
		
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
		Bitmap bitmap = codeBmp;
		Uri u = null;
		
		try {
			File mFile = savebitmap(bitmap);
			u = Uri.fromFile(mFile);
		} catch (IOException e) {
			Log.i("sendEmail", "save file error");
		}
		//TODO jesli nie ma kardy sd 

		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("application/image");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"marek.lat@gmail.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, "Test");
		i.putExtra(Intent.EXTRA_TEXT   , "W załączniku przesyłam kod, który po zeskanowaniu programem TPP doda go do bazy danych");
		i.putExtra(Intent.EXTRA_STREAM, u);
		try {
		    startActivity(Intent.createChooser(i, "Wysyłanie..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(getActivity(), "Brak klienta email", Toast.LENGTH_SHORT).show();
		}
	}
	
	private File savebitmap(Bitmap bmp) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

		File f = new File(Environment.getExternalStorageDirectory() + File.separator + "code.jpg");
		f.createNewFile();

		FileOutputStream fo = new FileOutputStream(f);
		fo.write(bytes.toByteArray());

		fo.close();
		return f;
	}
}
