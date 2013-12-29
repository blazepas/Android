package com.mareklatuszek.datywaznosci;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.mareklatuszek.utilities.TextViewBariol;

public class FragmentPrzypomnienie extends SherlockFragment implements FinalVariables {
	
	private Product product = new Product();
	CommonUtilities utilities = new CommonUtilities();
	
	View rootView;
	TextViewBariol nazwaTxt, dataOtwTxt, terminWazTxt, kategoriaTxt, estimateTxt;
	RelativeLayout progressLay;

	@Override
	public void onResume() {
		super.onResume();
		utilities.setActionBarTitle("Przypomnienie", getSherlockActivity());
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_przypomnienie, container, false);
		
		Bundle extras = getArguments();
		if (extras != null) {
			product = (Product) extras.getSerializable("product");	
			initView();
		} else {
			switchToPrzypomnieniaFragment();
		}
		
		
		return rootView;
	}
	
	private void initView() {
		nazwaTxt = (TextViewBariol) rootView.findViewById(R.id.nazwaTxt);
		dataOtwTxt = (TextViewBariol) rootView.findViewById(R.id.dataOtwTxt);
		terminWazTxt = (TextViewBariol) rootView.findViewById(R.id.terminWazTxt);
		kategoriaTxt = (TextViewBariol) rootView.findViewById(R.id.kategoriaTxt);
		estimateTxt = (TextViewBariol) rootView.findViewById(R.id.estimateTxt);
		progressLay = (RelativeLayout) rootView.findViewById(R.id.progressLay);
		
		String nazwa = product.getNazwa();
		String dataOtw = product.getDataOtwarcia();
		String terminWaz = product.getTerminWaznosci();
		String kategoria = product.getKategoria();
		String endDate = product.getEndDate();
		String estimate = getEstimate(endDate);		
		int progress = utilities.getProgress(dataOtw, endDate);
		Drawable progressDrawable = getResources().getDrawable(R.drawable.progress_bar_bg);
		
		nazwaTxt.setText(nazwa);
		dataOtwTxt.setText(dataOtw);
		terminWazTxt.setText(terminWaz);
		kategoriaTxt.setText(kategoria);
		estimateTxt.setText(estimate);
		
		setProggres(progress, progressDrawable);
	}
	
	private String getEstimate(String endDate) {
		long endTime = 0;
		try {
			endTime = utilities.parseDate(endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
        String estimate = utilities.dateToWords(System.currentTimeMillis(), endTime);
		
		return estimate;
	}
	
	private void setProggres(int progress, Drawable progressDrawable) {
		// ominiecie buga androida
		int fillParent = LayoutParams.FILL_PARENT;
		LayoutParams params = new LayoutParams(fillParent, fillParent);
		
		ProgressBar progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleHorizontal);
		progressBar.setProgress(progress);
		progressBar.setProgressDrawable(progressDrawable);
		progressBar.setLayoutParams(params);
		
		progressLay.addView(progressBar, 0);
	}

	private void switchToPrzypomnieniaFragment() {
		((MainActivity) getActivity()).selectFragment(2);;
	}

}
