package com.mareklatuszek.datywaznosci;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.mareklatuszek.utilities.BitmapLoader;
import com.mareklatuszek.utilities.CommonUtilities;
import com.mareklatuszek.utilities.FinalVariables;

public class FragmentEdytuj extends SherlockFragment implements OnClickListener, FinalVariables {
	
	boolean takePictureStat = false;
	boolean dodatkoweIsVisible = false;
	boolean isScanned = false;
	boolean orientationChanged = false;
	String currentDate = "";
	String code = "";
	String codeFormat = "";
	String oldCode = "";
	
	AdapterDB dbAdapter;
	ArrayAdapter<String> spinnerAdapter;
	ArrayList<String> kategorie;
	CommonUtilities utilities = new CommonUtilities();
	AdapterCustomSpinner adapterOkresSpinner, adapterKategorieSpinner;
	ArrayList<HashMap<String, String>> oldPrzypomnienia = new ArrayList<HashMap<String,String>>();
	
	View rootView;
	ImageView barcodeImage, obrazekImage, dodatkoweImage, kategroieImage, okresInfoImage, terminWazInfoImage, dataZuzInfoImage;
	EditText nazwaTextBox, okresWazTextBox, opisTxtBox, dataOtwTxtBox, terminWazTextBox, dataZuzTextBox;
	LinearLayout podstawowe, dodatkowe, latDodatkoweEdit, zapiszButton;
	CustomSpinner okresWazDropDown, kategorieDropDown;
	CustomViewPrzypomnienia przypLayout;
	View przypRow;
	ScrollView scrollView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.fragment_dodaj, container, false);
		currentDate = utilities.getCurrentDate();
		dbAdapter = new AdapterDB(getActivity());	
		
		utilities.setActionBarTitle("Edycja produktu", getSherlockActivity());
				
		initPodstawowe();
		initDodatkowe();
		
		if(savedInstanceState == null) {
			Bundle extras = getArguments();
		
			if (extras != null) {//TODO
				Product bundleProduct = (Product) extras.getSerializable("product");
				oldCode = bundleProduct.getCode();
				oldPrzypomnienia = bundleProduct.getPrzypomnienia();
				isScanned = bundleProduct.getIsScanned();
				
				setViewsFromProduct(bundleProduct);
			}
			
		} else {
			orientationChanged = true;
			Product savedStateProduct = (Product) savedInstanceState.getSerializable("product");
			oldCode = savedInstanceState.getString("oldCode");
			oldPrzypomnienia = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("oldPrzypomnienia");
			dodatkoweIsVisible = savedInstanceState.getBoolean("dodatkowe");
			isScanned = savedInstanceState.getBoolean("isScanned");
			
			setViewsFromProduct(savedStateProduct);
		}

		if (dodatkoweIsVisible) { 
			showDodatkowe();
		}
		orientationChanged = false;

		return rootView;
	}
		
	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);

		Product productToSave = prepareDataToStore();
		
		if (dodatkoweIsVisible) { 
			bundle.putBoolean("dodatkowe", true);
		} else {
			bundle.putBoolean("dodatkowe", false);
		}
		
		bundle.putBoolean("isScanned", isScanned);
		bundle.putString("oldCode", oldCode);
		bundle.putSerializable("oldPrzypomnienia", oldPrzypomnienia);
	    bundle.putSerializable("product", productToSave);     
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	    android.view.MenuInflater inflater = getActivity().getMenuInflater();
	    
	    switch (v.getId()) {
	    case R.id.barcodeImage:
	    	inflater.inflate(R.menu.popup_edit_get_code, menu);
	    	break;
	    case R.id.obrazekImage:
	    	inflater.inflate(R.menu.popup_get_image, menu);
	    	break;
	    }

	}
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		super.onContextItemSelected(item);
	    
	    switch (item.getItemId()) {
	    case R.id.generatePopup:
    		Product product = prepareDataToStore();
			DialogGeneruj dialogGen = new DialogGeneruj(getActivity(),product , getFragmentManager(), getId());
			dialogGen.show();
    		break;
	    case R.id.gallery:
    		pickImageFromGallery();
    		break;
    	case R.id.camera:
    		takePhoto();
    		break; 
    	}
	    return true;
	}
	 	
	@Override
	public void onClick(View view) {
		DialogDatePicker dialogDatePicker = new DialogDatePicker(getActivity(), view);
		
		switch (view.getId()) {
		case R.id.okresInfoImage:
		case R.id.dataZuzInfoImage:
		case R.id.terminWazInfoImage:
			PopUpInfo popUpInfo = new PopUpInfo(getActivity(), view);
			popUpInfo.showPopUp();
			break;
		case R.id.barcodeImage:
			if (!isScanned) {
				if(checkFormIsFill()) {
					view.showContextMenu();
				} else {
					Toast.makeText(getActivity(), "Należy podać nazwę i okres ważności", 1500).show();
				}	
			}
			break;
		case R.id.dataOtwTxtBox:
        	dialogDatePicker.show();
			break;
		case R.id.terminWazTextBox:
        	dialogDatePicker.show();
			break;
		case R.id.dataZuzTextBox:
			dialogDatePicker.show();
			break;
		case R.id.zapiszButton:
			save();
			break;
		case R.id.dodatkoweImage:
			if (dodatkoweIsVisible) {
				hideDodatkowe();
			} else {
				showDodatkowe();
			}
			break;	
		case R.id.obrazekImage:
			view.showContextMenu();
			break;
		case R.id.kategroieImage:
			DialogKategorie dialogKategorier = new DialogKategorie(getActivity(), kategorieDropDown, adapterKategorieSpinner);
			dialogKategorier.show();
			break;
		}
		
	}
						
	private void initPodstawowe() {		
		podstawowe = (LinearLayout) rootView.findViewById(R.id.podstawowe);
		barcodeImage = (ImageView) rootView.findViewById(R.id.barcodeImage);
		obrazekImage = (ImageView) rootView.findViewById(R.id.obrazekImage);
		nazwaTextBox = (EditText) rootView.findViewById(R.id.nazwaTextBox);
		terminWazTextBox = (EditText) rootView.findViewById(R.id.terminWazTextBox);
		terminWazInfoImage = (ImageView) rootView.findViewById(R.id.terminWazInfoImage);
		okresWazTextBox = (EditText) rootView.findViewById(R.id.okresWazTextBox);
		okresWazDropDown = (CustomSpinner) rootView.findViewById(R.id.okresWazDropDown);
		okresInfoImage = (ImageView) rootView.findViewById(R.id.okresInfoImage);
		zapiszButton = (LinearLayout) rootView.findViewById(R.id.zapiszButton);
		dodatkoweImage = (ImageView) rootView.findViewById(R.id.dodatkoweImage);
		scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
		
//		Bundle extras = getArguments(); // wylaczone bo nie będzie narazie skanowany kod z poziomu edycji
//		code = extras.getString("scanResultCode");
//		codeFormat = extras.getString("scanResultCodeFormat");
//		if (code != null || codeFormat != null) {	
//			setDataFromScan(code, codeFormat); 
//		}
		
		initSpinnerOkres();
		
		registerForContextMenu(barcodeImage);
		registerForContextMenu(obrazekImage);
		
		barcodeImage.setOnClickListener(this);
		obrazekImage.setOnClickListener(this);
		terminWazTextBox.setOnClickListener(this);
		terminWazInfoImage.setOnClickListener(this);
		okresInfoImage.setOnClickListener(this);
		dodatkoweImage.setOnClickListener(this);
		zapiszButton.setOnClickListener(this);	

	}
	
	private void initDodatkowe() {		
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		latDodatkoweEdit = (LinearLayout) inflater.inflate(R.layout.lay_dodatkowe_edit, null);		
		dodatkowe = (LinearLayout) rootView.findViewById(R.id.dodatkowe);	
		dodatkowe.addView(latDodatkoweEdit);		
		dataOtwTxtBox = (EditText) dodatkowe.findViewById(R.id.dataOtwTxtBox);
		kategroieImage = (ImageView) dodatkowe.findViewById(R.id.kategroieImage);
		kategorieDropDown = (CustomSpinner) dodatkowe.findViewById(R.id.kategorieDropDown);
		opisTxtBox = (EditText) dodatkowe.findViewById(R.id.opisTxtBox);
		przypLayout = (CustomViewPrzypomnienia) dodatkowe.findViewById(R.id.przypomnieniaLayout);
		dataZuzTextBox = (EditText) dodatkowe.findViewById(R.id.dataZuzTextBox);		
		dataZuzInfoImage = (ImageView) dodatkowe.findViewById(R.id.dataZuzInfoImage);
	
		initSpinnerKategorie();
		dataOtwTxtBox.setText(currentDate);
			
		dataOtwTxtBox.setOnClickListener(this);		
		kategroieImage.setOnClickListener(this);
		dataZuzTextBox.setOnClickListener(this);
		dataZuzInfoImage.setOnClickListener(this);
	}
		
	private void initSpinnerKategorie() {
		kategorie = new ArrayList<String>();
		
		dbAdapter.open();
		kategorie.addAll(dbAdapter.getAllCategories());
		Collections.reverse(kategorie);
		dbAdapter.close();
		
		adapterKategorieSpinner = new AdapterCustomSpinner(getActivity(), kategorie);
		kategorieDropDown.setText(SPINNER_KATEGORIE);
		kategorieDropDown.setAdapter(adapterKategorieSpinner);
	}
	
	private void initSpinnerOkres() {
		String[] okresSpinnData = getResources().getStringArray(R.array.array_date);
		adapterOkresSpinner = new AdapterCustomSpinner(getActivity(), okresSpinnData);
		okresWazDropDown.setText(SPINNER_OKRES);
		okresWazDropDown.setAdapter(adapterOkresSpinner);
	}
			
	private void showDodatkowe() {
		dodatkoweIsVisible = true;
		Drawable backg = getResources().getDrawable(R.drawable.collapse_dodatkowe);
		dodatkoweImage.setImageDrawable(backg);
		
		if (orientationChanged) {
			utilities.expandLinearLayout(dodatkowe);
		} else {
			// przesuwa widok na koniec ekranu
			dodatkowe.setVisibility(View.VISIBLE);
	        scrollView.post(new Runnable() { public void run() { scrollView.fullScroll(View.FOCUS_DOWN); } });
		}
	}
	
	private void hideDodatkowe() {		
		dodatkoweIsVisible = false;
		Drawable backg = getResources().getDrawable(R.drawable.expand_dodatkowe);		
		dodatkoweImage.setImageDrawable(backg);
        dodatkowe.setVisibility(View.GONE);
	}
		
	public void takePhoto() {        
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = utilities.getImageMediaFile();
        if (f != null) {
        	MainActivity.imageUri = Uri.fromFile(f);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            getActivity().startActivityForResult(intent, CAMERA_EDIT_RQ_CODE);	
        } else {
        	//TODO
        }
    }
	
	public void pickImageFromGallery() {
		Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		getActivity().startActivityForResult(i, GALLERY_EDIT_RQ_CODE);
	}
		
	private Product prepareDataToStore() {			
		Product product = new Product();
		
		String nazwa = nazwaTextBox.getText().toString();
		String terminWaznosci = getTerminWaznosci();
		String okresWaznosci = getOkresWaz(okresWazTextBox, okresWazDropDown);
		String kod = code;
		String typKodu = codeFormat;
		
		product.setNazwa(nazwa);
		product.setOkresWaznosci(okresWaznosci);			
		product.setCode(kod);
		product.setCodeFormat(typKodu);	
		product.setIsScanned(isScanned);

		String dataOtwarcia = dataOtwTxtBox.getText().toString();			
		String kategoria = getKategoria();
		String dataZuz = dataZuzTextBox.getText().toString();
		String obrazek = utilities.getRealPathFromURI(MainActivity.imageUri, getActivity());
		String opis = opisTxtBox.getText().toString();
		String endDate = getEndDate(terminWaznosci, okresWaznosci, dataOtwarcia);
		ArrayList<HashMap<String, String>> przypomnienia = przypLayout.getPrzypomnienia(endDate);
		
		product.setDataOtwarcia(dataOtwarcia);	
		product.setTerminWaznosci(terminWaznosci);
		product.setKategoria(kategoria);
		product.setDataZuzycia(dataZuz);
		product.setImage(obrazek);
		product.setOpis(opis);
		product.setEndDate(endDate);
		product.setPrzypomnienia(przypomnienia);		
	
		return product;
	}
	
	private class SaveData extends AsyncTask<Void, Void, Void> {
		ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(getActivity(), "Dodaję", "Zapisywanie do bazy");
		}

		@Override
		protected Void doInBackground(Void... params) {
			storeAllToDatabase();	
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			//TODO zrobic okienko jesli niepowodzenie
			progressDialog.dismiss();
			MainActivity.imageUri = Uri.parse("");
			((MainActivity) getActivity()).selectFragment(2); // prze��cza a ekran listy produkt�w
		}	
	}
		
	public void saveCodeFromDialogGeneruj(String code, String codeFormat, Bitmap bmp) {
		this.code = code;
		this.codeFormat = codeFormat;
		barcodeImage.setImageBitmap(bmp);
	}
	
	private boolean storeAllToDatabase() {
		Product product = prepareDataToStore();
		utilities.createThumb(product.getImage());
		dbAdapter.open();		
		boolean storeStatus = dbAdapter.updateProduct(product, oldCode);
		dbAdapter.close();
		
		if (storeStatus) {
			ArrayList<HashMap<String, String>> przypomnienia = product.getPrzypomnienia();
			String nazwa = product.getNazwa();
			String code = product.getCode();
			setAlarms(przypomnienia, nazwa, code); // gdy zmienia się id produktu, zaktualizować id alarmu!
		}
		
		return storeStatus; //je�li zapisze do poprawnie
	}
	
	private void setViewsFromProduct(Product product) {
	
		String nazwa = product.getNazwa();
		String okresWaznosci = product.getOkresWaznosci();
		String kod = product.getCode();
		String typKodu = product.getCodeFormat();
		String okresText = utilities.getFirstValue(okresWaznosci);
		String okresSpinnVal = utilities.getSecondValue(okresWaznosci);
		isScanned = product.getIsScanned();
		
		nazwaTextBox.setText(nazwa);
		setOkresWaznosci(okresText, okresSpinnVal);
		if (!kod.equals("")) {
			setCodeImage(kod, typKodu);
		}
		
		String dataOtwarcia = product.getDataOtwarcia();			
		String terminWaznosci = product.getTerminWaznosci();
		String kategoria = product.getKategoria();
		String dataZuz = product.getDataZuzycia();
		String imagePath = product.getImage();
		MainActivity.imageUri = Uri.parse(imagePath);
		
		if(!imagePath.equals("")) {
			Bitmap imageBmp = BitmapLoader.loadBitmap(imagePath, 100, 100);
			obrazekImage.setImageBitmap(imageBmp);
		}
		
		String opis = product.getOpis();
		ArrayList<HashMap<String, String>> przypomnienia = product.getPrzypomnienia();
		
		dataOtwTxtBox.setText(dataOtwarcia);
		terminWazTextBox.setText(terminWaznosci);
		setKategoria(kategoria);
		opisTxtBox.setText(opis);
		przypLayout.setPrzypomnienia(przypomnienia);
		dataZuzTextBox.setText(dataZuz);
	}
		
	public void setDataFromScan(String code, String codeFormat) {
		dbAdapter.open();
		boolean isInDB = dbAdapter.chckIfProductIsInDB(code);
		dbAdapter.close();
		
		if(isInDB) {
			showChoiceDialog(code, codeFormat);
		} else {
			setValidateCode(code, codeFormat);
		}
	}
	
	private void setCodeImage(String code, String codeFormat) {
		Bitmap bmp = utilities.encodeCodeToBitmap(code, codeFormat, getActivity());
		if (bmp != null) {
			this.code = code;
			this.codeFormat = codeFormat;
			barcodeImage.setImageBitmap(bmp);
		} else {
			// jesli niepoprawny kod
			this.code = "";
			this.codeFormat = "";
			bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.zxinglib_icon); 
			barcodeImage.setImageBitmap(bmp);
	    	Toast.makeText(getActivity(), "Błąd skanowania lub niepoprawny kod", 2000).show();
		}
	}
		
	public void setCameraResult(Uri selectedImage) { 
        getActivity().getContentResolver().notifyChange(selectedImage, null);
        ContentResolver cr = getActivity().getContentResolver();
        Bitmap bitmap;
        try {
        	String path = utilities.getRealPathFromURI(selectedImage, getActivity());
        	bitmap = BitmapLoader.loadBitmap(path, 100, 100);

            obrazekImage.setImageBitmap(bitmap);
            Toast.makeText(getActivity(), selectedImage.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Błąd aparatu!", Toast.LENGTH_SHORT).show();
            Log.e("Camera", e.toString());
        }
        
        takePictureStat = false;
	}
	
	private void setValidateCode(String code, String codeFormat) {
		if (utilities.validateCode(code, codeFormat)) { //jeśli kod zawiera produkt

			Product product = utilities.parseCodeToProduct(code);
			product.setCode(code);
			product.setCodeFormat(codeFormat);
			setViewsFromProduct(product);
							
			Log.i("Validate", "true");
		} else {
			Log.i("Validate", "false");
			setCodeImage(code, codeFormat);
		}
	}
	
	private void setOkresWaznosci(String okresText, String okresSpinnVal) {
		if (okresSpinnVal.equals("")) {
			okresSpinnVal = SPINNER_OKRES;
		}
		okresWazTextBox.setText(okresText);
		okresWazDropDown.setText(okresSpinnVal);
	}
	
	private void setAlarms(ArrayList<HashMap<String, String>> przypomnienia, String nazwa, String productCode) {
		utilities.cancelAlarms(oldPrzypomnienia, productCode, getActivity());
		utilities.startAlarms(przypomnienia, nazwa, productCode, getActivity());
	}
		
	private void setKategoria(String kategoria) {
		if (kategoria.equals("")) {
			kategorieDropDown.setText(SPINNER_KATEGORIE);
		} else {
			kategorieDropDown.setText(kategoria);
		}
	}

	private String getKategoria() {
		String chosenKategoria = kategorieDropDown.getText();	
		if(chosenKategoria.equals(SPINNER_KATEGORIE)) {
			return "";
		} else {
			return chosenKategoria;
		}
	}
	
	private String getTerminWaznosci() {

		return terminWazTextBox.getText().toString();
	}
		
	private String getOkresWaz(EditText box, CustomSpinner customDropDown) {		
		String value = box.getText().toString();
		String format = customDropDown.getText();
		
		if(value.length() == 0 || value.equals("0") || format.equals(SPINNER_OKRES)) {
			return ":";
		} else {
			String peroid = value + ":" + format;
			return peroid;
		}
	}
	
	private String getChoiceFromCustomSpinner(LinearLayout customSpinner) {
		TextView spinnetTxt = (TextView) customSpinner.findViewById(R.id.spinnerTxt);
		String choice = spinnetTxt.getText().toString();
		
		return choice;
	}
	
	private String getEndDate(String terminWazosci, String okresWaznosci, String dataOtw) {	
		if (terminWazosci.equals("")) {
			String peroid = getOkresWaz(okresWazTextBox, okresWazDropDown);
			String date = utilities.parseOkresToDate(peroid, dataOtw);
			return date;
		} else {	
			return terminWazosci;
		}
	}
						
	private void save() {		
		if (checkFormIsFill()) {
			if (code.equals("")) {	
				Product product = prepareDataToStore();				
				this.code = utilities.getJsonFromProduct(product);
				this.codeFormat = product.getCodeFormat();
				save();
			} else {
				new SaveData().execute();
			}	
		}
	}
	
	public void refreshKategorieSpinner(String category) {
		kategorie.remove(category);
		spinnerAdapter.notifyDataSetChanged();
	}
	
	private void showChoiceDialog(final String code, final String codeFormat) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setMessage("Produkt o takim kodzie znajduje się już w bazie. Możesz wrócić do edycji lub do podglądu");
		dialog.setPositiveButton("Edytuj",new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dbAdapter.open();
				Product product = dbAdapter.getProduct(code);
				dbAdapter.close();
				switchToShowFragment(product);
			}
			
		});

		dialog.setNegativeButton("Podgląd",new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dbAdapter.open();
				Product product = dbAdapter.getProduct(code);
				dbAdapter.close();
				((MainActivity) getActivity()).selectFragmentToShowProduct(product);
			}
			
		});

		dialog.show();
	}
	
	private boolean checkFormIsFill() {
		String nazwa = nazwaTextBox.getText().toString();
		String termin = terminWazTextBox.getText().toString();
		String okres = okresWazTextBox.getText().toString();
		String okresSpinnChoice = getChoiceFromCustomSpinner(okresWazDropDown);
		
		if (nazwa.equals("")) {
			Toast.makeText(getActivity(), "Należy podać nazwę produktu", 1500).show();
			return false;
		} else if (termin.equals("") & (okres.equals("") | okresSpinnChoice.equals(SPINNER_OKRES))) {
			Toast.makeText(getActivity(), "Należy podać termin ważności lub okres trwałości produktu", 2000).show();
			return false;
		} else {
			return true;
		}
	}
	
	private void switchToShowFragment(Product product) {
		((MainActivity) getActivity()).selectFragmentToShowProduct(product);
	}
}	