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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mareklatuszek.utilities.CommonUtilities;
import com.mareklatuszek.utilities.TextViewBariol;

public class DialogGeneruj extends Dialog implements android.view.View.OnClickListener {
	
	FragmentActivity mActivity;
	Product product;
	FragmentManager fragmentManager; 
	int fragmentId;
	int fragPos;
	CommonUtilities utilities = new CommonUtilities();
	AdapterDialogGeneruj adapterPozostale;
	
	TextViewBariol nazwaTxt, dataTxt, terminWazTxt, pozostaleTxt, buttonTxt;;
	ImageView codeImage;
	ListView pozostaleList;
	LinearLayout generujButton;
	String codeFormat;
	String code;
	Bitmap bmp;
	
	boolean isGenerated = false;


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
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.generujButton:
			if (isGenerated) {
				dismiss();
				saveProduct();
			} else {
				generateCode();
			}
			break;
		}
		
	}
	
	private void initPodstawowe() {		
		nazwaTxt = (TextViewBariol) findViewById(R.id.nazwaTxt);
		dataTxt = (TextViewBariol) findViewById(R.id.dataTxt);
		terminWazTxt = (TextViewBariol) findViewById(R.id.terminWazTxt);
		codeImage = (ImageView) findViewById(R.id.codeImage);
		generujButton = (LinearLayout) findViewById(R.id.generujButton);
		buttonTxt = (TextViewBariol) findViewById(R.id.buttonTxt);
		
		String nazwa = product.getNazwa();
		String data = utilities.getCurrentDate();
		String terminWaz = product.getTerminWaznosci();

		nazwaTxt.setText(nazwa);
		dataTxt.setText(data);
		terminWazTxt.setText(terminWaz);
		
		generujButton.setOnClickListener(this);
	}
		
	private void saveProduct() {
		switch (fragPos) {
		case 1:
			FragmentDodaj fragmentDodaj = (FragmentDodaj) fragmentManager.findFragmentById(fragmentId);
			fragmentDodaj.saveCodeFromDialogGeneruj(code, codeFormat, bmp);
			break;
		case 6:
			FragmentEdytuj fragmentEdytuj = (FragmentEdytuj) fragmentManager.findFragmentById(fragmentId);
			fragmentEdytuj.saveCodeFromDialogGeneruj(code, codeFormat, bmp);
			break;
		default:
			Toast.makeText(mActivity, "Nie wygenerowano kodu!", 2000).show();
		}
	}
	
	private void generateCode() {
		code = utilities.getJsonFromProduct(product);
		codeFormat = product.getCodeFormat();
		bmp = utilities.encodeCodeToBitmap(code, codeFormat, mActivity);
		codeImage.setImageBitmap(bmp);
		
		buttonTxt.setText("zapisz");
		isGenerated = true;
		
		Toast.makeText(mActivity, "Kod został wygenerowany!", 2000).show();
	}
}