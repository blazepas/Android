package pl.mareklatuszek.tpp.fragments;

import java.text.ParseException;

import pl.mareklatuszek.tpp.MainActivity;
import pl.mareklatuszek.tpp.TPPApp;
import pl.mareklatuszek.tpp.Product;
import pl.mareklatuszek.tpp.R;
import pl.mareklatuszek.tpp.utilities.CommonUtilities;
import pl.mareklatuszek.tpp.utilities.FinalVariables;
import pl.mareklatuszek.tpp.views.TextViewBariol;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.SherlockFragment;

public class FragmentPrzypomnienie extends SherlockFragment implements FinalVariables {
	
	private Product product = new Product();
	CommonUtilities utilities = TPPApp.getUtilities();
	
	View rootView;
	TextViewBariol nazwaTxt, dataOtwTxt, terminWazTxt, kategoriaTxt, estimateTxt;
	RelativeLayout progressLay;

	@Override
	public void onResume() {
		super.onResume();
		
		String title = getString(R.string.frag_alarm_title);
		utilities.setActionBarTitle(title, getSherlockActivity());
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
		((MainActivity) getActivity()).selectFragment(SELECTION_ALARMS);
	}

}
