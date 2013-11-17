package com.mareklatuszek.datywaznosci;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.zxing.WriterException;
import com.mareklatuszek.datywznosci.utilities.CommonUtilities;
import com.mareklatuszek.datywznosci.utilities.Contents;
import com.mareklatuszek.datywznosci.utilities.FinalVariables;
import com.mareklatuszek.datywznosci.utilities.QRCodeEncoder;

public class FragmentDodaj extends SherlockFragment implements OnClickListener, FinalVariables {
	
	public static boolean terminWazIsSet = false;
	String currentDate = "";
	String code = "";
	String codeFormat = "";
	
	AdapterAddPowiadomienia przypAdapter;
	AdapterDB dbAdapter;
	CommonUtilities utilities = new CommonUtilities();
	
	ImageView barcodeImage;
	Button dataOtwButton, terminWazButton, zapiszButton;
	EditText nazwaTextBox, okresWazTextBox;
	Spinner okresWazSpinner, kategorieSpinner;
	LinearLayout przypLayout;
	View przypRow;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_dodaj, container, false);
		
		przypAdapter = new AdapterAddPowiadomienia(getActivity(), 1, getFragmentManager(), getId());
		dbAdapter = new AdapterDB(getActivity());
		
		barcodeImage = (ImageView) rootView.findViewById(R.id.barcodeImage);
		dataOtwButton = (Button) rootView.findViewById(R.id.dataOtwButton);
		terminWazButton = (Button) rootView.findViewById(R.id.terminWazButton);
		zapiszButton = (Button) rootView.findViewById(R.id.zapiszButton);
		nazwaTextBox = (EditText) rootView.findViewById(R.id.nazwaTextBox);
		okresWazTextBox = (EditText) rootView.findViewById(R.id.okresWazTextBox);
		okresWazSpinner = (Spinner) rootView.findViewById(R.id.okresWazSpinner);
		kategorieSpinner = (Spinner) rootView.findViewById(R.id.kategorieSpinner);
		przypLayout = (LinearLayout) rootView.findViewById(R.id.przypLayout);
	
		barcodeImage.setOnClickListener(this);
		dataOtwButton.setOnClickListener(this);
		terminWazButton.setOnClickListener(this);
		zapiszButton.setOnClickListener(this);
		
		Bundle extras = getArguments(); // dane przes³ane do fragmentu
		if (extras != null) {
			code = extras.getString("scanResultCode");
			codeFormat = extras.getString("scanResultCodeFormat");
			setDataFromScan(code, codeFormat); 
		}
		
		currentDate = getCurrentDate();
		dataOtwButton.setText(currentDate);
		
		View item = przypAdapter.getView(0, null, null);
		przypLayout.addView(item);
			
		return rootView;
	}
	
	@Override
	public void onClick(View view) {
		DialogDatePicker dialogDatePicker = new DialogDatePicker(getActivity(), view);
		
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
				// okienko, ¿e nale¿y podaæ termin wa¿noœci
			}
			
			break;
		}
	}
			
	private Bitmap encodeCodeToBitmap(String code, String codeFormat)
	{
		//konwersja zeskanowanego kodu na obrazek, na podstawie kodu i jego formatu
		
	    int qrCodeDimention = 150; //rozmiar obrazka

	    QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(code, null, Contents.Type.TEXT, codeFormat, qrCodeDimention);

	    try {
	        Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
	        return bitmap;
	    } catch (WriterException e) {
	    	//w razie niepowodzenia - domyœlna grafika
	    	Bitmap bitmap= BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.zxinglib_icon); 
	        return null;
	    }	
	}
	
	private Product prepareDataToStore() {	
		
		Product product = new Product();
		
		String nazwa = nazwaTextBox.getText().toString();
		String dataOtwarcia = dataOtwButton.getText().toString();
		String okresWaznosci = getPeriodFromBoxAndSpinner(okresWazTextBox, okresWazSpinner);//TODO
		String terminWaznosci = getTerminWaznosci();
		String kategoria = getKategoria();
		String kod = code;
		String typKodu = codeFormat;
		String obrazek = ""; //TODO
		String opis = ""; //TODO
		ArrayList<HashMap<String, String>> przypomnienia = preparePrzypomnienia(przypAdapter.getAdapterData(), terminWaznosci);;
		
		product.setNazwa(nazwa);
		product.setDataOtwarcia(dataOtwarcia);
		product.setOkresWaznosci(okresWaznosci);
		product.setTerminWaznosci(terminWaznosci);
		product.setKategoria(kategoria);
		product.setCode(kod);
		product.setCodeFormat(typKodu);
		product.setImage(obrazek);
		product.setOpis(opis);
		product.setPrzypomnienia(przypomnienia);
		
		return product;
	}
	
	private ArrayList<HashMap<String, String>> preparePrzypomnienia(ArrayList<HashMap<String, String>> przypomnienia, String terminWaznosci) {
		
		for (int i = 0; i < przypomnienia.size(); i++) {
			HashMap<String, String> przypomnienie = przypomnienia.get(i);
			String boxVal = przypomnienie.get(PRZYP_TEXT_BOX);
			String spinnerVal = przypomnienie.get(PRZYP_SPINNER);
			String notifHour = przypomnienie.get(PRZYP_HOUR);
			long date = 0;
			
			if(!boxVal.equals(""))
			{
				try {
					date = utilities.parsePrzypmnienieToDate(boxVal, spinnerVal, terminWaznosci, notifHour);
				} catch (ParseException e) {
					Log.i("preparePrzyp", "parese " + i + "error");
					date = 0;
				}
			}
			
			String przypDate = String.valueOf(date);
			przypomnienie.put(PRZYP_DATE, przypDate);		
		}
		
		
		return przypomnienia;
	}
	
	private void saveData() {
		new AsyncTask<Void, Void, Void>() {
			ProgressDialog progressDialog;
				
			@Override
			protected void onPreExecute() {
				progressDialog =progressDialog.show(getActivity(), "Dodajê", "Dodawanie do bazy");
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
				((MainActivity) getActivity()).selectFragment(2); // prze³¹cza a ekran listy produktów
			}
		}.execute();
	}
	
	private boolean storeAllToDatabase() {

		Product product = prepareDataToStore();
		dbAdapter.open();

		//TODO sprawdza w bazie czy jest ju¿ taki produkt
		//jeœli tak - proponuje inn¹ nazwê
		
		boolean storeStatus = dbAdapter.insertProduct(product);
		dbAdapter.close();
		
		return storeStatus; //jeœli zapisze do poprawnie
	}
	
	public void setDataFromScan(String code, String codeFormat) {	
		barcodeImage.setImageBitmap(encodeCodeToBitmap(code, codeFormat));	
	}
	
	public void setRowPrzypomnienia(int count) {
		
		przypAdapter.przypCount = count;
		przypAdapter.notifyDataSetChanged();
		
		przypLayout.removeAllViews();
		for (int i = 0; i < przypAdapter.getCount(); i++)
		{
			View item = przypAdapter.getView((i), null, null);
			przypLayout.addView(item);
		}
	}
	
	private String getKategoria() {
		String[] kategorieArray = getResources().getStringArray(R.array.array_temp_kategorie); // TODO tymczasowe rozwi¹zanie
		int chosenKategoriaPos = kategorieSpinner.getSelectedItemPosition();
		
		String chosenKategoria = kategorieArray[chosenKategoriaPos];
		
		return chosenKategoria;
	}
	
	private String getTerminWaznosci() {
		if (terminWazIsSet) {
			return terminWazButton.getText().toString();
		} else {
			return "";
		}
	}
	
	private String getPeriodFromBoxAndSpinner(EditText box, Spinner spinner) {
		String[] formatArray = getResources().getStringArray(R.array.array_date);
		int chosenFormatPos = spinner.getSelectedItemPosition();
		
		String value = box.getText().toString();
		String format = formatArray[chosenFormatPos];
		
		String peroid = value + ":" + format;
		
		return peroid;
	}
	
	private String getCurrentDate() {
		String dateFormat = "dd/MM/yyyy";
		String currentDate = "";
		
	    DateFormat formatter = new SimpleDateFormat(dateFormat);
	    Calendar calendar = Calendar.getInstance();
	    currentDate = formatter.format(calendar.getTime());
	    
	    return currentDate;
	}

}
