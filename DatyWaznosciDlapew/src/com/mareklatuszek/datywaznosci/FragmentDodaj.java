package com.mareklatuszek.datywaznosci;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.zxing.WriterException;
import com.mareklatuszek.datywznosci.utilities.CommonUtilities;
import com.mareklatuszek.datywznosci.utilities.Contents;
import com.mareklatuszek.datywznosci.utilities.FinalVariables;
import com.mareklatuszek.datywznosci.utilities.QRCodeEncoder;

public class FragmentDodaj extends SherlockFragment implements OnClickListener, OnKeyListener, OnItemSelectedListener, FinalVariables {
	
	public static boolean terminWazIsSet = false;
	boolean takePictureStat = false;
	String currentDate = "";
	String code = "";
	String codeFormat = "";
	
	AdapterDB dbAdapter;
	CommonUtilities utilities = new CommonUtilities();
	
	View rootView;
	ImageView barcodeImage, obrazekImage;
	Button dataOtwButton, terminWazButton, zapiszButton, dodatkoweButton, kategorieButton;
	EditText nazwaTextBox, okresWazTextBox, opisTxtBox;
	Spinner okresWazSpinner, kategorieSpinner;
	LinearLayout podstawowe, dodatkowe, przypLayout, latDodatkoweEdit;
	View przypRow;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.fragment_dodaj, container, false);
		currentDate = utilities.getCurrentDate();
		dbAdapter = new AdapterDB(getActivity());
		
		code = tempgetCode();//TODO
	
		if(savedInstanceState == null) {
			initPodstawowe();
		} else {
			boolean dodatkoweStatus = savedInstanceState.getBoolean("dodatkowe");
			initPodstawowe();
			Product product = (Product) savedInstanceState.getSerializable("product");			
			if (dodatkoweStatus) {
				initDodatkowe();
				
				Bitmap bmp = savedInstanceState.getParcelable("image");
				obrazekImage.setImageBitmap(bmp);
				
				showDodatkowe();
			}
			setViewsFromProduct(product);
		}
		
		return rootView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
	 
		Product product = prepareDataToStore();
		
		if (dodatkowe != null) { //tylko dla obrazka
			Bitmap bitmap = getBitmapFromObrazek();
			bundle.putParcelable("image", bitmap);
			bundle.putBoolean("dodatkowe", true);

		} else {
			bundle.putBoolean("dodatkowe", false);
		}
		
	    bundle.putSerializable("product", product);     
	}
	 	
	@Override
	public void onClick(View view) {
		DialogDatePicker dialogDatePicker = new DialogDatePicker(getActivity(), view, okresWazTextBox, okresWazSpinner);
		
		switch (view.getId()) {
		case R.id.barcodeImage:
			((MainActivity)getActivity()).startScanner();
			break;
		case R.id.dataOtwButton:
        	dialogDatePicker.show();
			break;
		case R.id.terminWazButton:
        	dialogDatePicker.show();
			break;
		case R.id.zapiszButton:
			if (terminWazIsSet) {
				saveData();
			} else {
				// okienko, �e nale�y poda� termin wa�no�ci
			}			
			break;
		case R.id.dodatkoweButton:
			initDodatkowe();
			showDodatkowe();
			break;	
		case R.id.obrazekImage:
			takePhoto();
			break;
		case R.id.kategorieButton:
			DialogKategorie dialogKategorier = new DialogKategorie(getActivity(), kategorieSpinner);
			dialogKategorier.show();
			break;
		}
		
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		switch (v.getId()) {
		case R.id.okresWazTextBox:
			if (dodatkowe != null) {
				setTerminWaz();
			}
			break;
		}
		return false;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (dodatkowe != null) {
			setTerminWaz();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
			
	private void initPodstawowe() {		
		podstawowe = (LinearLayout) rootView.findViewById(R.id.podstawowe);
		barcodeImage = (ImageView) rootView.findViewById(R.id.barcodeImage);
		nazwaTextBox = (EditText) rootView.findViewById(R.id.nazwaTextBox);
		okresWazTextBox = (EditText) rootView.findViewById(R.id.okresWazTextBox);
		okresWazSpinner = (Spinner) rootView.findViewById(R.id.okresWazSpinner);
		zapiszButton = (Button) rootView.findViewById(R.id.zapiszButton);
		dodatkoweButton = (Button) rootView.findViewById(R.id.dodatkoweButton);
		
		Bundle extras = getArguments(); // dane przes�ane do fragmentu
		if (extras != null) {
			code = extras.getString("scanResultCode");
			codeFormat = extras.getString("scanResultCodeFormat");
			setDataFromScan(code, codeFormat); 
		}
		
		barcodeImage.setOnClickListener(this);
		zapiszButton.setOnClickListener(this);	
		dodatkoweButton.setOnClickListener(this);
		okresWazTextBox.setOnKeyListener(this);
		okresWazSpinner.setOnItemSelectedListener(this);
	}
	
	private void initDodatkowe() {		
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		latDodatkoweEdit = (LinearLayout) inflater.inflate(R.layout.lay_dodatkowe_edit, null);		
		dodatkowe = (LinearLayout) rootView.findViewById(R.id.dodatkowe);	
		dodatkowe.addView(latDodatkoweEdit);
		
		dataOtwButton = (Button) dodatkowe.findViewById(R.id.dataOtwButton);
		terminWazButton = (Button) dodatkowe.findViewById(R.id.terminWazButton);
		kategorieButton = (Button) dodatkowe.findViewById(R.id.kategorieButton);
		kategorieSpinner = (Spinner) dodatkowe.findViewById(R.id.kategorieSpinner);
		opisTxtBox = (EditText) dodatkowe.findViewById(R.id.opisTxtBox);
		obrazekImage = (ImageView) dodatkowe.findViewById(R.id.obrazekImage);
		przypLayout = (LinearLayout) dodatkowe.findViewById(R.id.przypomnieniaLayout);
		
		dataOtwButton.setOnClickListener(this);
		terminWazButton.setOnClickListener(this);
		kategorieButton.setOnClickListener(this);
		obrazekImage.setOnClickListener(this);
		
		dataOtwButton.setText(currentDate);
		
		initKategorie();
		initPrzypomnienia();
	}
	
//	private class InitKategorie extends AsyncTask<Void, Void, Void> {
//
//		ArrayList<String> kategorie = new ArrayList<String>();
//		
//		@Override
//		protected Void doInBackground(Void... params) {
//			kategorie.add("Brak kategorii");
//			dbAdapter.open();
//			kategorie.addAll(dbAdapter.getAllCategories());
//			dbAdapter.close();
//			return null;
//		}
//		
//		@Override
//		protected void onPostExecute(Void v) {
//			ArrayAdapter<String> spinnerAdapter;
//			spinnerAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, kategorie);
//			kategorieSpinner.setAdapter(spinnerAdapter);
//			Log.i("onPost", "initKAt");
//		}		
//	}
	
	private void initKategorie() {
		ArrayList<String> kategorie = new ArrayList<String>();
		
		dbAdapter.open();
		kategorie.addAll(dbAdapter.getAllCategories());
		kategorie.add("Brak kategorii");
		Collections.reverse(kategorie);
		dbAdapter.close();
		
		ArrayAdapter<String> spinnerAdapter;
		spinnerAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, kategorie);
		kategorieSpinner.setAdapter(spinnerAdapter);
	}
			
	private void showDodatkowe() {
		utilities.expandLinearLayout(dodatkowe);
		dodatkoweButton.setVisibility(View.GONE);
	}
		
	public void takePhoto() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        MainActivity.imageUri = Uri.fromFile(photo);
        takePictureStat = true;
        getActivity().startActivityForResult(intent, CAMERA_RQ_CODE);      
    }
		
	private Product prepareDataToStore() {			
		Product product = new Product();
		
		String nazwa = nazwaTextBox.getText().toString();
		String okresWaznosci = getPeriodFromBoxAndSpinner(okresWazTextBox, okresWazSpinner);//TODO
		String kod = code;
		String typKodu = codeFormat;
		
		product.setNazwa(nazwa);
		product.setOkresWaznosci(okresWaznosci);
			
		product.setCode(kod);
		product.setCodeFormat(typKodu);
		
		if (dodatkowe != null) {
			String dataOtwarcia = dataOtwButton.getText().toString();			
			String terminWaznosci = getTerminWaznosci();
			String kategoria = getKategoria();
			String obrazek = ""; //TODO
			String opis = opisTxtBox.getText().toString();
			ArrayList<HashMap<String, String>> przypomnienia = getPrzypomnienia();
			
			product.setDataOtwarcia(dataOtwarcia);	
			product.setTerminWaznosci(terminWaznosci);
			product.setKategoria(kategoria);
			product.setImage(obrazek);
			product.setOpis(opis);
			product.setPrzypomnienia(przypomnienia);
		} else {
			product.setDataOtwarcia(currentDate);
			product.setTerminWaznosci(utilities.parseOkresToDate(okresWaznosci));
		}
	
		return product;
	}
	
	private void saveData() {
		new AsyncTask<Void, Void, Void>() {
			ProgressDialog progressDialog;
				
			@Override
			protected void onPreExecute() {
				progressDialog =ProgressDialog.show(getActivity(), "Dodaję", "Dodawanie do bazy");
			}

			@Override
			protected Void doInBackground(Void... params) {
				storeAllToDatabase();	
				return null;
			}
			
			@Override
			protected void onPostExecute(Void v) {
				//TODO zrobic okienko jesli niepowodzenie
				terminWazIsSet = false;
				progressDialog.dismiss();
				((MainActivity) getActivity()).selectFragment(2); // prze��cza a ekran listy produkt�w
			}
		}.execute();
	}
	
	private boolean storeAllToDatabase() {
		Product product = prepareDataToStore();
		dbAdapter.open();

		//TODO sprawdza w bazie czy jest ju� taki produkt
		//je�li tak - proponuje inn� nazw�
		
		boolean storeStatus = dbAdapter.insertProduct(product);
		dbAdapter.close();
		
		return storeStatus; //je�li zapisze do poprawnie
	}
	
	private void setViewsFromProduct(Product product) {
		if (dodatkowe != null) {	
			String dataOtwarcia = product.getDataOtwarcia();			
			String terminWaznosci = product.getTerminWaznosci();
			String kategoria = product.getKategoria();
			int kategoriaId = utilities.getPosInSpinner(kategoria, kategorieSpinner);
			Log.i("setViews", "katId"+kategoriaId);
			String obrazek = ""; //TODO
			String opis = product.getOpis();
			ArrayList<HashMap<String, String>> przypomnienia = product.getPrzypomnienia();
			
			dataOtwButton.setText(dataOtwarcia);
			terminWazButton.setText(terminWaznosci);
			kategorieSpinner.setSelection(kategoriaId);
//			product.setImage(obrazek); TODO
			opisTxtBox.setText(opis);
			setPrzypomnienia(przypomnienia);
		}
		
		String nazwa = product.getNazwa();
		String okresWaznosci = product.getOkresWaznosci();
		String kod = product.getCode();
		String typKodu = product.getCodeFormat();
		this.code = kod;
		this.codeFormat = typKodu;
		String okresText = utilities.getTextFromOkresWaz(okresWaznosci);
		String okresSpinnVal = utilities.getSpinnerItemFromOkresWaz(okresWaznosci);
		int okresSpinPos = utilities.getPosInSpinner(okresSpinnVal, okresWazSpinner);
		
		nazwaTextBox.setText(nazwa);
		okresWazTextBox.setText(okresText);
		okresWazSpinner.setSelection(okresSpinPos);;
		setDataFromScan(kod, typKodu);				
	}
	
	private void setPrzypomnienia(ArrayList<HashMap<String, String>> przypomnienia) { //TODO naprawić, dubluje sie
		
		int przypCount = przypomnienia.size();
		
		przypLayout.removeAllViews();
		
		for(int i = 0; i < przypCount; i++) {
			
			HashMap<String, String> przypomnienie = przypomnienia.get(i);
			
			LayoutInflater inflater =  getActivity().getLayoutInflater();		
			String boxTxt = przypomnienie.get(PRZYP_TEXT_BOX);
			String spinner = przypomnienie.get(PRZYP_SPINNER);
			int spinnerPos = Integer.parseInt(spinner);
			String przypHour = przypomnienie.get(PRZYP_HOUR);//TODO	
						
			LinearLayout row = (LinearLayout) inflater.inflate(R.layout.listview_add_powiadomienia, null);
			EditText przypTextBox = (EditText) row.findViewById(R.id.przypTextBox);
			Spinner przypSpinner = (Spinner) row.findViewById(R.id.przypSpinner);
			Button przypButton = (Button) row.findViewById(R.id.przypButton);
						
			przypTextBox.setText(boxTxt);
			przypSpinner.setSelection(spinnerPos);
					
//			EditText temp = new EditText(getActivity());
//			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(54, LayoutParams.WRAP_CONTENT, 1);
//			temp.setLayoutParams(params);
//			temp.setText(boxTxt);
//			row.addView(temp);		
			
			if (i < (przypCount - 1)) {
				przypButton.setVisibility(View.GONE);
			} else {
				przypButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						initPrzypomnienia();
						View rowBefore = przypLayout.getChildAt(przypLayout.getChildCount() - 2);
						Button buttonBefore = (Button) rowBefore.findViewById(R.id.przypButton);
						buttonBefore.setVisibility(View.GONE);				
					}
				});				
			}
			
			przypLayout.addView(row);	
		}	
	}
	
	private void setTerminWaz() {
		String peroid = getPeriodFromBoxAndSpinner(okresWazTextBox, okresWazSpinner);
		String date = utilities.parseOkresToDate(peroid);
		terminWazButton.setText(date);
	}
	
	public void setDataFromScan(String code, String codeFormat) {
		this.code = code;
		this.codeFormat = codeFormat;
		Bitmap bmp = utilities.encodeCodeToBitmap(code, codeFormat, getActivity());
		barcodeImage.setImageBitmap(bmp);	
	}
		
	public void setCameraResult() { 
		Uri selectedImage = MainActivity.imageUri;
        getActivity().getContentResolver().notifyChange(selectedImage, null);
        ContentResolver cr = getActivity().getContentResolver();
        Bitmap bitmap;
        try {
             bitmap = android.provider.MediaStore.Images.Media
             .getBitmap(cr, selectedImage);

            obrazekImage.setImageBitmap(bitmap);
            Toast.makeText(getActivity(), selectedImage.toString(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT)
                    .show();
            Log.e("Camera", e.toString());
        }
        
        takePictureStat = false;
	}
	
	private String getKategoria() {
		int chosenKategoriaPos = kategorieSpinner.getSelectedItemPosition();
		String chosenKategoria = (String) kategorieSpinner.getItemAtPosition(chosenKategoriaPos);
		
		return chosenKategoria;
	}
	
	private String getTerminWaznosci() {
		if (terminWazIsSet) {
			return terminWazButton.getText().toString();
		} else {
			return "";
		}
	}
	
	private ArrayList<HashMap<String, String>> getPrzypomnienia() {
		ArrayList<HashMap<String, String>> przypomnienia = new ArrayList<HashMap<String,String>>();
		int przypCount = przypLayout.getChildCount();
		
		for(int i = 0; i < przypCount; i++) {
			HashMap<String, String> przypomnienie = new HashMap<String, String>();
			View row = przypLayout.getChildAt(i);
			
			EditText przypTextBox = (EditText) row.findViewById(R.id.przypTextBox);
			Spinner przypSpinner = (Spinner) row.findViewById(R.id.przypSpinner);
			
			String boxTxt = przypTextBox.getText().toString();
			String spinnerPos = String.valueOf(przypSpinner.getSelectedItemPosition());
			String przypHour = "14";//TODO
			String terminWaz = terminWazButton.getText().toString();
			String przypDate = "0";
			try {
				long dateInMillis = utilities.parsePrzypmnienieToDate(boxTxt, spinnerPos, terminWaz, przypHour);
				przypDate = String.valueOf(dateInMillis);
			} catch (ParseException e) {
				Log.i("getPrzypomnienia", "parse to date error");
			}
			
			przypomnienie.put(PRZYP_TEXT_BOX, boxTxt);
			przypomnienie.put(PRZYP_SPINNER, spinnerPos);
			przypomnienie.put(PRZYP_HOUR, przypHour);
			przypomnienie.put(PRZYP_DATE, przypDate);
			
			przypomnienia.add(przypomnienie);
		}
		
		return przypomnienia;
	}
	
	private String getPeriodFromBoxAndSpinner(EditText box, Spinner spinner) {
		String[] formatArray = getResources().getStringArray(R.array.array_date);
		int chosenFormatPos = spinner.getSelectedItemPosition();
		
		String value = box.getText().toString();
		String format = formatArray[chosenFormatPos];
		
		String peroid = value + ":" + format;
		
		return peroid;
	}
	
	private Bitmap getBitmapFromObrazek() {
		Bitmap bitmap;
		if (obrazekImage.getDrawable() instanceof BitmapDrawable) {
		    bitmap = ((BitmapDrawable) obrazekImage.getDrawable()).getBitmap();
		} else {
		    Drawable d = obrazekImage.getDrawable();
		    bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		    Canvas canvas = new Canvas(bitmap);
		    d.draw(canvas);
		}
		return bitmap;
	}
	
	private String tempgetCode() { //TODO wywalic
		long time = System.currentTimeMillis();
		return String.valueOf(time);
	}
	
	private void initPrzypomnienia() {
		final LayoutInflater inflater = getActivity().getLayoutInflater();
		LinearLayout row = (LinearLayout) inflater.inflate(R.layout.listview_add_powiadomienia, null);
		Button przycisk = (Button) row.findViewById(R.id.przypButton);
		przycisk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				initPrzypomnienia();	
				View viv =przypLayout.getChildAt(przypLayout.getChildCount() - 2);
				Button przypButton = (Button) viv.findViewById(R.id.przypButton);
				przypButton.setVisibility(View.GONE);
			}
		});
		przypLayout.addView(row);
	}
}
