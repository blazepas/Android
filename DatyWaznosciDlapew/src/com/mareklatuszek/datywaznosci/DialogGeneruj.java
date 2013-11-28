package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mareklatuszek.utilities.CommonUtilities;

public class DialogGeneruj extends Dialog implements android.view.View.OnClickListener {
	
	FragmentActivity mActivity;
	Product product;
	FragmentManager fragmentManager; 
	int fragmentId;
	int fragPos;
	CommonUtilities utilities = new CommonUtilities();
	AdapterDialogGeneruj adapterPozostale;
	
	TextView nazwaTxt, dataTxt, terminWazTxt, pozostaleTxt;
	ImageView codeImage;
	ListView pozostaleList;
	Button okButton, cancelButton;
	private String codeFormat;
	private String code;


	public DialogGeneruj(FragmentActivity mActivity, Product product, FragmentManager fragmentManager, int fragmentId) {
		super(mActivity);
		this.mActivity = mActivity;
		this.product = product;
		this.fragmentManager = fragmentManager;
		this.fragmentId = fragmentId;
		fragPos = MainActivity.currentFragmentPos;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_generuj);
		
		initPodstawowe();
		initPozostałe();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.okButton:
			dismiss();
			saveProduct();
			break;
		case R.id.cancelButton:
			dismiss();
			break;
		}
		
	}
	
	private void initPodstawowe() {		
		nazwaTxt = (TextView) findViewById(R.id.nazwaTxt);
		dataTxt = (TextView) findViewById(R.id.dataTxt);
		terminWazTxt = (TextView) findViewById(R.id.terminWazTxt);
		codeImage = (ImageView) findViewById(R.id.codeImage);
		okButton = (Button) findViewById(R.id.okButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		
		String nazwa = product.getNazwa();
		String data = utilities.getCurrentDate();
		String terminWaz = product.getTerminWaznosci();
		code = utilities.getJsonFromProduct(product);
		codeFormat = product.getCodeFormat();
		
		nazwaTxt.setText(nazwa);
		dataTxt.setText(data);
		terminWazTxt.setText(terminWaz);
		Bitmap bmp = utilities.encodeCodeToBitmap(code, codeFormat, mActivity);
		codeImage.setImageBitmap(bmp);
		okButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
	}
	
	private void initPozostałe() {
		pozostaleTxt = (TextView) findViewById(R.id.pozostaleTxt);
		ArrayList<HashMap<String, String>> pozostale = getPozostaleList();
		if (pozostale.size() > 0) {
			pozostaleList = (ListView) findViewById(R.id.pozostaleList);
			adapterPozostale = new AdapterDialogGeneruj(mActivity, pozostale);
			pozostaleList.setAdapter(adapterPozostale);
			
		} else {
			pozostaleTxt.setVisibility(View.GONE);
		}
	}
	
	private ArrayList<HashMap<String, String>> getPozostaleList() {
		ArrayList<HashMap<String, String>> pozostale = new ArrayList<HashMap<String,String>>();
		
		String dataOtwarcia = product.getDataOtwarcia();
		String kategoria = product.getKategoria();
		int przypCount = product.getPrzypomnienia().size();
		
		if (!dataOtwarcia.equals("")) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("first", "Data otwarcia: ");
			map.put("second", dataOtwarcia);
			pozostale.add(map);
		} 
		
		if (!kategoria.equals("")) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("first", "Kategoria: ");
			map.put("second", kategoria);
			pozostale.add(map);
		} 
		
		if (przypCount > 0) {
			HashMap<String, String> map = new HashMap<String, String>();
			String count = String.valueOf(przypCount);
			map.put("first", "Liczba przypomnień: ");
			map.put("second", count);
			pozostale.add(map);
		}
		
		return pozostale;
	}
	
	private void saveProduct() {
		switch (fragPos) {
		case 1:
			FragmentDodaj fragmentDodaj = (FragmentDodaj) fragmentManager.findFragmentById(fragmentId);
			fragmentDodaj.saveCodeFromDialogGeneruj(code, codeFormat);
			break;
		case 6:
			FragmentEdytuj fragmentEdytuj = (FragmentEdytuj) fragmentManager.findFragmentById(fragmentId);
			fragmentEdytuj.saveProductFromDialogGeneruj(code, codeFormat);
			break;
		default:
			Toast.makeText(mActivity, "Nie wygenerowano kodu!", 2000).show();
		}
		
	}
	
}