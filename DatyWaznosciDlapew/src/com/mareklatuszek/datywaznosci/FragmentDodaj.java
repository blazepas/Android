package com.mareklatuszek.datywaznosci;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.mareklatuszek.utilities.BitmapLoader;
import com.mareklatuszek.utilities.CommonUtilities;
import com.mareklatuszek.utilities.FinalVariables;

public class FragmentDodaj extends SherlockFragment implements OnClickListener, OnKeyListener, FinalVariables {
	
	boolean takePictureStat = false;
	boolean dodatkoweIsVisible = false;
	boolean isFullVersion = true;
	boolean isScanned = false;
	int tempSpinnOkresPos = 0;
	String currentDate = "";
	String code = "";
	String codeFormat = "";
	
	AdapterDB dbAdapter;
	CommonUtilities utilities = new CommonUtilities();
	ArrayAdapter<String> spinnerAdapter;
	ArrayList<String> kategorie;
	AdapterCustomSpinner adapterOkresSpinner, adapterKategorieSpinner, adapterPryzpSpinner;
	
	View rootView;
	ImageView barcodeImage, obrazekImage, dodatkoweImage, kategroieImage, okresInfoImage, terminWazInfoImage, dataZuzInfoImage;
	Button zapiszButton;
	EditText nazwaTextBox, okresWazTextBox, opisTxtBox, dataOtwTxtBox, terminWazTextBox, dataZuzTextBox;
	LinearLayout podstawowe, dodatkowe, przypLayout, latDodatkoweEdit, okresWazDropDown, kategorieDropDown;
	View przypRow;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.fragment_dodaj, container, false);
		currentDate = utilities.getCurrentDate();
		dbAdapter = new AdapterDB(getActivity());

		utilities.setActionBarTitle("Dodaj produkt", getSherlockActivity());
		
		initPodstawowe();
		initDodatkowe();
		
		if(savedInstanceState == null) {
			Bundle extras = getArguments();
		
			if (extras != null) {//TODO
				Product bundleProduct = (Product) extras.getSerializable("product");
				setViewsFromProduct(bundleProduct);
				showDodatkowe();
			}
			
		} else {
			Product savedStateProduct = (Product) savedInstanceState.getSerializable("product");
			boolean ifDodatkoweExpand = savedInstanceState.getBoolean("dodatkowe");
			isScanned = savedInstanceState.getBoolean("isScanned");
			tempSpinnOkresPos = savedInstanceState.getInt("tempSpinnOkresPos");
			
			setViewsFromProduct(savedStateProduct);
			
			if (ifDodatkoweExpand) { 
				showDodatkowe();
			}
		}
	
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
		bundle.putInt("tempSpinnOkresPos", tempSpinnOkresPos);
	    bundle.putSerializable("product", productToSave);     
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	    android.view.MenuInflater inflater = getActivity().getMenuInflater();
	    
	    switch (v.getId()) {
	    case R.id.barcodeImage:
	    	inflater.inflate(R.menu.popup_get_code, menu);
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
    	case R.id.scanPopup:
    			((MainActivity)getActivity()).startScanner();
    		break;
    	case R.id.generatePopup:
    		if (!checkFormIsFill()) {
    			Toast.makeText(getActivity(), "Należy podać nazwę i okres ważności", 1500).show();
    		} else {
        		Product product = prepareDataToStore();
        		
				DialogGeneruj dialogGen = new DialogGeneruj(getActivity(),product , getFragmentManager(), getId());
				dialogGen.show();
    		}	
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
		DialogDatePicker dialogDatePicker = new DialogDatePicker(getActivity(), view, getFragmentManager(), getId());
		
		switch (view.getId()) {
		case R.id.przypDropDown:
		case R.id.kategorieDropDown:
		case R.id.okresWazDropDown:
			getSpinnerPopUp(view).showAsDropDown(view);
			break;
		case R.id.okresInfoImage:
		case R.id.dataZuzInfoImage:
		case R.id.terminWazInfoImage:
			showInfoPopUp(view);
			break;
		case R.id.barcodeImage:
			view.showContextMenu();
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
			DialogKategorie dialogKategorier 
				= new DialogKategorie(getActivity(), kategorieDropDown, adapterKategorieSpinner);
			dialogKategorier.show();
			break;
		}		
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		switch (v.getId()) {
		case R.id.okresWazTextBox:
	    	setTerminWaz();	    		
			break;
		}
		return false;
	}
						
	private void initPodstawowe() {		

		podstawowe = (LinearLayout) rootView.findViewById(R.id.podstawowe);
		barcodeImage = (ImageView) rootView.findViewById(R.id.barcodeImage);
		obrazekImage = (ImageView) rootView.findViewById(R.id.obrazekImage);
		nazwaTextBox = (EditText) rootView.findViewById(R.id.nazwaTextBox);
		okresWazTextBox = (EditText) rootView.findViewById(R.id.okresWazTextBox);
		okresWazDropDown = (LinearLayout) rootView.findViewById(R.id.okresWazDropDown);
		zapiszButton = (Button) rootView.findViewById(R.id.zapiszButton);
		dodatkoweImage = (ImageView) rootView.findViewById(R.id.dodatkoweImage);
		okresInfoImage = (ImageView) rootView.findViewById(R.id.okresInfoImage);
		
		Bundle extras = getArguments(); // TODO jesli zeskanuje
		if (extras != null) {
			code = extras.getString("scanResultCode");
			codeFormat = extras.getString("scanResultCodeFormat");
			setDataFromScan(code, codeFormat); 
		}
		
		initSpinnerOkres();
		
		registerForContextMenu(barcodeImage);
		registerForContextMenu(obrazekImage);
		
		barcodeImage.setOnClickListener(this);
		obrazekImage.setOnClickListener(this);
		okresInfoImage.setOnClickListener(this);
		zapiszButton.setOnClickListener(this);	
		okresWazDropDown.setOnClickListener(this);
		dodatkoweImage.setOnClickListener(this);
		okresWazTextBox.setOnKeyListener(this);
	}
	
	private void initDodatkowe() {		
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		latDodatkoweEdit = (LinearLayout) inflater.inflate(R.layout.lay_dodatkowe_edit, null);		
		dodatkowe = (LinearLayout) rootView.findViewById(R.id.dodatkowe);	
		dodatkowe.addView(latDodatkoweEdit);
		
		dataOtwTxtBox = (EditText) dodatkowe.findViewById(R.id.dataOtwTxtBox);
		terminWazTextBox = (EditText) dodatkowe.findViewById(R.id.terminWazTextBox);
		kategroieImage = (ImageView) dodatkowe.findViewById(R.id.kategroieImage);
		kategorieDropDown = (LinearLayout) dodatkowe.findViewById(R.id.kategorieDropDown);
		opisTxtBox = (EditText) dodatkowe.findViewById(R.id.opisTxtBox);
		przypLayout = (LinearLayout) dodatkowe.findViewById(R.id.przypomnieniaLayout);
		dataZuzTextBox = (EditText) dodatkowe.findViewById(R.id.dataZuzTextBox);
		terminWazInfoImage = (ImageView) dodatkowe.findViewById(R.id.terminWazInfoImage);
		dataZuzInfoImage = (ImageView) dodatkowe.findViewById(R.id.dataZuzInfoImage);
	
		dataOtwTxtBox.setOnClickListener(this);
		terminWazTextBox.setOnClickListener(this);
		kategroieImage.setOnClickListener(this);
		dataZuzTextBox.setOnClickListener(this);
		kategorieDropDown.setOnClickListener(this);
		terminWazInfoImage.setOnClickListener(this);
		dataZuzInfoImage.setOnClickListener(this);
		
		dataOtwTxtBox.setText(currentDate);
		
		initSpinnerKategorie();
		initPrzypomnienia();
		initSpinnerPrzypList();
	}
	
	private void initSpinnerKategorie() {
		kategorie = new ArrayList<String>();
		
		dbAdapter.open();
		kategorie.addAll(dbAdapter.getAllCategories());
		Collections.reverse(kategorie);
		dbAdapter.close();
		
		setCustomSpinner("wybierz kategorię", kategorieDropDown);
		adapterKategorieSpinner = new AdapterCustomSpinner(getActivity(), kategorie);
	}
	
	private void initSpinnerOkres() {
		setCustomSpinner(SPINNER_OKRES, okresWazDropDown);
		String[] okresSpinnData = getResources().getStringArray(R.array.array_date);
		adapterOkresSpinner = new AdapterCustomSpinner(getActivity(), okresSpinnData);
	}
	
	private void initSpinnerPrzypList() {
		String[] pryzpSpinnData = getResources().getStringArray(R.array.array_date);
		adapterPryzpSpinner = new AdapterCustomSpinner(getActivity(), pryzpSpinnData);
	}
	
	private void initPrzypomnienia() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final LinearLayout row = (LinearLayout) inflater.inflate(R.layout.listview_add_powiadomienia, null);
		Button removePrzypButton = (Button) row.findViewById(R.id.removePrzypButton);
		Button addPrzypButton = (Button) row.findViewById(R.id.addPrzypButton);
		Button godzButton = (Button) row.findViewById(R.id.godzButton);
		LinearLayout przypDropDown = (LinearLayout) row.findViewById(R.id.przypDropDown);
		
		setCustomSpinner(SPINNER_PRZPOMNIENIE, przypDropDown);
		
		przypDropDown.setOnClickListener(this);
		
		removePrzypButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				przypLayout.removeView(row);
				int rowsCount = przypLayout.getChildCount();
				
				if (rowsCount == 0) {
					initPrzypomnienia();
				} else {
					View viv = przypLayout.getChildAt(rowsCount - 1);
					Button przypButton = (Button) viv.findViewById(R.id.addPrzypButton);
					przypButton.setVisibility(View.VISIBLE);
				}
			}
		});
		
		godzButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogTimePicker dialogTimePicker = new DialogTimePicker(getActivity(), v);
				dialogTimePicker.show();				
			}
		});
		
		addPrzypButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				initPrzypomnienia();	
				View viv =przypLayout.getChildAt(przypLayout.getChildCount() - 2);
				Button przypButton = (Button) viv.findViewById(R.id.addPrzypButton);
				przypButton.setVisibility(View.GONE);
			}
		});
		przypLayout.addView(row);
	}
		
	private void showDodatkowe() {
		dodatkoweIsVisible = true;
		Drawable backg = getResources().getDrawable(R.drawable.collapse_dodatkowe);
		dodatkoweImage.setImageDrawable(backg);
        utilities.expandLinearLayout(dodatkowe);
	}
	
	private void hideDodatkowe() {		
		dodatkoweIsVisible = false;
		Drawable backg = getResources().getDrawable(R.drawable.expand_dodatkowe);		
		dodatkoweImage.setImageDrawable(backg);
        dodatkowe.setVisibility(View.GONE);
	}
		
	public void takePhoto() {	
		File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"TPP");
		directory.mkdirs();
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(android.os.Environment.getExternalStorageDirectory(),File.separator + "TPP" + File.separator 
        		+ (System.currentTimeMillis()/1000) + ".jpg");
        MainActivity.imageUri = Uri.fromFile(f);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        getActivity().startActivityForResult(intent, CAMERA_ADD_RQ_CODE);
    }
	
	public void pickImageFromGallery() {
		Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		getActivity().startActivityForResult(i, GALLERY_ADD_RQ_CODE);
	}
		
	private Product prepareDataToStore() {			
		Product product = new Product();
		
		String nazwa = nazwaTextBox.getText().toString();
		String okresWaznosci = getPeriodFromBoxAndSpinner(okresWazTextBox, okresWazDropDown);
		String kod = code;
		String typKodu = codeFormat;
		
		product.setNazwa(nazwa);
		product.setOkresWaznosci(okresWaznosci);			
		product.setCode(kod);
		product.setCodeFormat(typKodu);	
		product.setIsScanned(isScanned);

		String dataOtwarcia = dataOtwTxtBox.getText().toString();			
		String terminWaznosci = getTerminWaznosci();
		String kategoria = getKategoria();
		String dataZuz = dataZuzTextBox.getText().toString();
		String image = utilities.getRealPathFromURI(MainActivity.imageUri, getActivity());
		String opis = opisTxtBox.getText().toString();
		ArrayList<HashMap<String, String>> przypomnienia = getPrzypomnienia();
		
		product.setDataOtwarcia(dataOtwarcia);	
		product.setTerminWaznosci(terminWaznosci);
		product.setKategoria(kategoria);
		product.setDataZuzycia(dataZuz);
		product.setImage(image);
		product.setOpis(opis);
		product.setPrzypomnienia(przypomnienia);
	
		return product;
	}
	
	private void saveData() {
		new AsyncTask<Void, Void, Void>() {
			ProgressDialog progressDialog;
				
			@Override
			protected void onPreExecute() {
				progressDialog = ProgressDialog.show(getActivity(), "Dodaję", "Dodawanie do bazy");
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
		}.execute();
	}
	
	public void saveCodeFromDialogGeneruj(String code, String codeFormat) {
		this.code = code;
		this.codeFormat = codeFormat;
	}
	
	private boolean storeAllToDatabase() {
		Product product = prepareDataToStore();
		utilities.createThumb(product.getImage());
		dbAdapter.open();		
		boolean storeStatus = dbAdapter.insertProduct(product);
		dbAdapter.close();
		
		if (storeStatus) {
			ArrayList<HashMap<String, String>> przypomnienia = product.getPrzypomnienia();
			String nazwa = product.getNazwa();
			String code = product.getCode();
			setAlarms(przypomnienia, nazwa, code);
			isScanned = false;
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
		
		nazwaTextBox.setText(nazwa);
		okresWazTextBox.setText(okresText);
		setCustomSpinner(okresSpinnVal, okresWazDropDown);
		if (!kod.equals("")) {
			setCodeImage(kod, typKodu);
		}	
		
		String dataOtwarcia = product.getDataOtwarcia();			
		String terminWaznosci = product.getTerminWaznosci();
		String kategoria = product.getKategoria();
		String dataZuz = product.getDataZuzycia();
		String imagePath = product.getImage();
		Bitmap imageBmp = BitmapLoader.loadBitmap(imagePath, 100, 100);
		String opis = product.getOpis();
		ArrayList<HashMap<String, String>> przypomnienia = product.getPrzypomnienia();
		
		dataOtwTxtBox.setText(dataOtwarcia);
		terminWazTextBox.setText(terminWaznosci);
		setKategorieSpinner(kategoria);
		obrazekImage.setImageBitmap(imageBmp);
		opisTxtBox.setText(opis);
		setPrzypomnienia(przypomnienia);	
		dataZuzTextBox.setText(dataZuz);
	}
	
	private void setPrzypomnienia(ArrayList<HashMap<String, String>> przypomnienia) { //TODO naprawić, dubluje sie
		
		int przypCount = przypomnienia.size();
		
		if (przypCount > 0) {
			przypLayout.removeAllViews();
		}
		
		for(int i = 0; i < przypCount; i++) {
			
			HashMap<String, String> przypomnienie = przypomnienia.get(i);
			
			LayoutInflater inflater =  getActivity().getLayoutInflater();		
			String boxTxt = przypomnienie.get(PRZYP_TEXT_BOX);
			String spinner = przypomnienie.get(PRZYP_SPINNER);
			String przypHour = przypomnienie.get(PRZYP_HOUR);
						
			final LinearLayout row = (LinearLayout) inflater.inflate(R.layout.listview_add_powiadomienia, null);
			EditText przypTextBox = (EditText) row.findViewById(R.id.przypTextBox);
			Button removePrzypButton = (Button) row.findViewById(R.id.removePrzypButton);
			Button przypButton = (Button) row.findViewById(R.id.addPrzypButton);
			Button godzButton = (Button) row.findViewById(R.id.godzButton);
			LinearLayout przypDropDown = (LinearLayout) row.findViewById(R.id.przypDropDown);

			przypTextBox.setText(boxTxt);
			setCustomSpinner(spinner, przypDropDown);
			godzButton.setText(przypHour);
					
//			EditText temp = new EditText(getActivity());
//			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(54, LayoutParams.WRAP_CONTENT, 1);
//			temp.setLayoutParams(params);
//			temp.setText(boxTxt);
//			row.addView(temp);		
						
			przypDropDown.setOnClickListener(this);
			
			removePrzypButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					przypLayout.removeView(row);
					int rowsCount = przypLayout.getChildCount();
					
					if (rowsCount == 0) {
						initPrzypomnienia();
					} else {
						View viv = przypLayout.getChildAt(rowsCount - 1);
						Button przypButton = (Button) viv.findViewById(R.id.addPrzypButton);
						przypButton.setVisibility(View.VISIBLE);
					}				
				}
			});
			
			if (i < (przypCount - 1)) {
				przypButton.setVisibility(View.GONE);
			} else {
				przypButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						initPrzypomnienia();
						View rowBefore = przypLayout.getChildAt(przypLayout.getChildCount() - 2);
						Button buttonBefore = (Button) rowBefore.findViewById(R.id.addPrzypButton);
						buttonBefore.setVisibility(View.GONE);				
					}
				});				
			}
			
			godzButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					DialogTimePicker dialogTimePicker = new DialogTimePicker(getActivity(), v);
					dialogTimePicker.show();				
				}
			});
			
			przypLayout.addView(row);	
		}	
	}
	
	private void setTerminWaz() {
		String peroid = getPeriodFromBoxAndSpinner(okresWazTextBox, okresWazDropDown);
		String date = utilities.parseOkresToDate(peroid);
		terminWazTextBox.setText(date);
	}
	
	public void setTerminWazFromAdapter(String spinnText) {
		String txtBoxOkres = okresWazTextBox.getText().toString();
		String peroid = txtBoxOkres + ":" + spinnText;
		String date = utilities.parseOkresToDate(peroid);
		terminWazTextBox.setText(date);
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
			isScanned = false;
			bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.zxinglib_icon); 
			barcodeImage.setImageBitmap(bmp);
	    	Toast.makeText(getActivity(), "Błąd skanowania lub niepoprawny kod", 2000).show();
		}
		
	}
	
	private void setValidateCode(String code, String codeFormat) {
		isScanned = true;
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
		
	public void setCameraResult(Uri selectedImage) { 
        getActivity().getContentResolver().notifyChange(selectedImage, null);
        ContentResolver cr = getActivity().getContentResolver();
        Bitmap bitmap;
        try {
        	String path = utilities.getRealPathFromURI(selectedImage, getActivity());
        	bitmap = BitmapLoader.loadBitmap(path, 100, 100);
            obrazekImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Błąd aparatu!", Toast.LENGTH_SHORT).show();
            Log.e("Camera", e.toString());
        }
        
        takePictureStat = false;
	}
	
	public void setOkresWaz(String choosenDate) {
		okresWazTextBox.setOnKeyListener(null);
		
		String okres = utilities.parseDateToOkres(choosenDate);
		String box = utilities.getFirstValue(okres);
		String spinnItem = utilities.getSecondValue(okres);
		
		okresWazTextBox.setText(box);
		setCustomSpinner(spinnItem, okresWazDropDown);
		
		okresWazTextBox.setOnKeyListener(this);
	}
	
	private void setAlarms(ArrayList<HashMap<String, String>> przypomnienia, String nazwa, String productCode) {
		utilities.startAlarms(przypomnienia, nazwa, productCode, getActivity());
	}
	
	private void setCustomSpinner(String title, LinearLayout viewToSet) {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		LinearLayout spinner = (LinearLayout) inflater.inflate(R.layout.spinner_button, null);
		
		TextView spinnerTxt = (TextView) spinner.findViewById(R.id.spinnerTxt);
		spinnerTxt.setText(title);
		
		viewToSet.removeAllViewsInLayout();
		viewToSet.addView(spinner);
	}
	
	private void setKategorieSpinner(String kategoria) {
		if (kategoria.equals("")) {
			setCustomSpinner(SPINNER_KATEGORIE, kategorieDropDown);
		} else {
			setCustomSpinner(kategoria, kategorieDropDown);
		}
	}
	
	private String getKategoria() {
		String chosenKategoria = getChoiceFromCustomSpinner(kategorieDropDown);	
		if(chosenKategoria.equals(SPINNER_KATEGORIE)) {
			return "";
		} else {
			return chosenKategoria;
		}
	}
	
	private String getTerminWaznosci() {

		return terminWazTextBox.getText().toString();
	}
	
	private ArrayList<HashMap<String, String>> getPrzypomnienia() {
		ArrayList<HashMap<String, String>> przypomnienia = new ArrayList<HashMap<String,String>>();
		int przypCount = przypLayout.getChildCount();
		
		for(int i = 0; i < przypCount; i++) {
			HashMap<String, String> przypomnienie = new HashMap<String, String>();
			View row = przypLayout.getChildAt(i);
			
			LinearLayout przypDropDown = (LinearLayout) row.findViewById(R.id.przypDropDown);
			EditText przypTextBox = (EditText) row.findViewById(R.id.przypTextBox);
			Button godzButton = (Button) row.findViewById(R.id.godzButton);
			
			String boxTxt = przypTextBox.getText().toString();
			
			if (boxTxt.length() != 0 & !boxTxt.equals("0") & !boxTxt.equals(SPINNER_PRZPOMNIENIE)) {
				String spinnerChoice = getChoiceFromCustomSpinner(przypDropDown);
				String przypHour = godzButton.getText().toString();
				String terminWaz = terminWazTextBox.getText().toString();
				String przypDate = "0";
				try {
					long dateInMillis = utilities.parsePrzypmnienieToDate(boxTxt, spinnerChoice, terminWaz, przypHour);
					przypDate = String.valueOf(dateInMillis);
				} catch (ParseException e) {
					Log.i("getPrzypomnienia", "parse to date error");
				}
				
				przypomnienie.put(PRZYP_TEXT_BOX, boxTxt);
				przypomnienie.put(PRZYP_SPINNER, spinnerChoice);
				przypomnienie.put(PRZYP_HOUR, przypHour);
				przypomnienie.put(PRZYP_DATE, przypDate);
				
				przypomnienia.add(przypomnienie);
			}			
		}
		
		return przypomnienia;
	}
	
	private String getPeriodFromBoxAndSpinner(EditText box, LinearLayout okresWazDropDown) {		
		String value = box.getText().toString();
		String format = getChoiceFromCustomSpinner(okresWazDropDown);
		
		String peroid = value + ":" + format;
		
		return peroid;
	}
	
	private String getChoiceFromCustomSpinner(LinearLayout customSpinner) {
		TextView spinnetTxt = (TextView) customSpinner.findViewById(R.id.spinnerTxt);
		String choice = spinnetTxt.getText().toString();
		
		return choice;
	}
	
	private PopupWindow getSpinnerPopUp(final View clickedView) {

        final PopupWindow popupWindow = new PopupWindow(getActivity());
        ListView spinnerList = new ListView(getActivity());
        ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.dropdown));
        
        switch (clickedView.getId()) {
        case R.id.okresWazDropDown:
        	spinnerList.setAdapter(adapterOkresSpinner);
        	spinnerList.setOnItemClickListener(new OnItemClickListener() {

    			@Override
    			public void onItemClick(AdapterView<?> arg0, View view, int arg2,long arg3) 
    			{
    				String choice = (String) view.getTag();
    				setTerminWazFromAdapter(choice);
    				setCustomSpinner(choice, okresWazDropDown);
    				popupWindow.dismiss();
    			}
    		});
        	break;
        	
	 	case R.id.kategorieDropDown:
	     	spinnerList.setAdapter(adapterKategorieSpinner);
	     	spinnerList.setOnItemClickListener(new OnItemClickListener() {
	
	 			@Override
	 			public void onItemClick(AdapterView<?> arg0, View view, int arg2,long arg3) 
	 			{
	 				String choice = (String) view.getTag();
	 				setCustomSpinner(choice, kategorieDropDown);
	 				popupWindow.dismiss();
	 			}
	 		});
	     	break;
	     	
	 	case R.id.przypDropDown:
	 		spinnerList.setAdapter(adapterPryzpSpinner);
	     	spinnerList.setOnItemClickListener(new OnItemClickListener() {
	
	 			@Override
	 			public void onItemClick(AdapterView<?> arg0, View view, int arg2,long arg3) 
	 			{
	 				LinearLayout viewToSet = (LinearLayout) clickedView;
	 				String choice = (String) view.getTag();
	 				setCustomSpinner(choice, viewToSet);
	 				popupWindow.dismiss();
	 			}
	 		});
	 		break;
        }

        popupWindow.setFocusable(true); 
        popupWindow.setBackgroundDrawable(background); //TODO
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(clickedView.getLayoutParams().width);
        popupWindow.setContentView(spinnerList);

        return popupWindow;
    }
	
	private void showInfoPopUp(View clickedView) {

        final PopupWindow popupWindow = new PopupWindow(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        Drawable background = getResources().getDrawable(R.drawable.back_info_popup);
        int layoutId = R.layout.popup_info_right;
        int wrapContent = WindowManager.LayoutParams.WRAP_CONTENT;
        int posX = -265;
        int posY = -35;
        boolean leftSide = false;
        String message = "";

        switch (clickedView.getId()) {
        case R.id.okresInfoImage:
        	leftSide = false;
        	message = INFO_OKRES;
        	break;
        case R.id.terminWazInfoImage:
        	leftSide = true;
        	message = INFO_TERMIN_WAZNOSCI;
        	break;
        case R.id.dataZuzInfoImage:
        	leftSide = true;
        	message = INFO_DATA_ZUZYCIA;
        }
        
        if (leftSide) {
            layoutId = R.layout.popup_info_left;
            posX = -5;
            posY = -35;
        }
        
        RelativeLayout popupLay = (RelativeLayout) inflater.inflate(layoutId, null);
        TextView popupTxt = (TextView) popupLay.findViewById(R.id.popupTxt);
        popupTxt.setText(message);
        
        popupWindow.setFocusable(true); 
        popupWindow.setBackgroundDrawable(background); //TODO
        popupWindow.setHeight(wrapContent);
        popupWindow.setWidth(300);
        popupWindow.setContentView(popupLay);
        popupWindow.showAsDropDown(clickedView, posX, posY);

    }
							
	private void save() {		
		if (!checkFormIsFill()) {
			Toast.makeText(getActivity(), "Należy podać nazwę i okres ważności", 1500).show();
		} else if (code.equals("")) {	
			Product product = prepareDataToStore();
//			DialogGeneruj dialogGen = new DialogGeneruj(getActivity(),product , getFragmentManager(), getId());
//			dialogGen.show();	
			
//			TODO wyłączony dialog z generowaniem, za miast niego mozna dać opcję zapytania o wygenerowanie
//			aktualnie generuje kod w tle
			
			this.code = utilities.getJsonFromProduct(product);
			this.codeFormat = product.getCodeFormat();
			save();
		} else {
			saveData();
		}	
	
	}
	
	public void refreshKategorieSpinner(String category) {
		kategorie.remove(category);
		spinnerAdapter.notifyDataSetChanged();
	}
	
	private void showChoiceDialog(final String code, final String codeFormat) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setMessage("Produkt o takim kodzie znajduje się już w bazie. Możesz przejść do jego edycji lub do podglądu");
		dialog.setPositiveButton("Edytyj",new DialogInterface.OnClickListener() {

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
				switchToEditFragment(product);
			}
			
		});

		dialog.show();
	}
	
	private boolean checkFormIsFill() {
		String nazwa = nazwaTextBox.getText().toString();
		String termin = terminWazTextBox.getText().toString();
		
		if (nazwa.equals("") || termin.equals("") || termin.equals("Wprowadź datę")) {
			return false;
		} else {	
			return true;
		}
	}
	
	private void switchToEditFragment(Product product) {
		((MainActivity) getActivity()).selectFragmentToEditProduct(product);
	}
	
	private void switchToShowFragment(Product product) {
		((MainActivity) getActivity()).selectFragmentToShowProduct(product);
	}
	
}
