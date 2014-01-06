package pl.jacek.jablonka.android.tpp.dialogs;

import java.text.ParseException;

import pl.jacek.jablonka.android.tpp.MainActivity;
import pl.jacek.jablonka.android.tpp.Product;
import pl.jacek.jablonka.android.tpp.R;
import pl.jacek.jablonka.android.tpp.TPPApp;
import pl.jacek.jablonka.android.tpp.utilities.CommonUtilities;
import pl.jacek.jablonka.android.tpp.utilities.FinalVariables;
import pl.jacek.jablonka.android.tpp.views.TextViewBariol;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DialogPrzypomnienie extends Dialog {
	
	Product product;
	CommonUtilities utilities = TPPApp.getUtilities();
	Activity mActivity;
	

	TextViewBariol nazwaTxt, dataOtwTxt, terminWazTxt, kategoriaTxt, estimateTxt;
	RelativeLayout progressLay;

	public DialogPrzypomnienie(Activity mActivity, Product product) {
		super(mActivity);
		this.product = product;
		this.mActivity = mActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_przypomnienie);
		
		initView();
	}
	
	private void initView() {
		nazwaTxt = (TextViewBariol) findViewById(R.id.nazwaTxt);
		dataOtwTxt = (TextViewBariol) findViewById(R.id.dataOtwTxt);
		terminWazTxt = (TextViewBariol) findViewById(R.id.terminWazTxt);
		kategoriaTxt = (TextViewBariol) findViewById(R.id.kategoriaTxt);
		estimateTxt = (TextViewBariol) findViewById(R.id.estimateTxt);
		progressLay = (RelativeLayout) findViewById(R.id.progressLay);
		
		String nazwa = product.getNazwa();
		String dataOtw = product.getDataOtwarcia();
		String terminWaz = product.getTerminWaznosci();
		String kategoria = product.getKategoria();
		String endDate = product.getEndDate();
		String estimate = getEstimate(endDate);		
		int progress = utilities.getProgress(dataOtw, endDate);
		Drawable progressDrawable = mActivity.getResources().getDrawable(R.drawable.progress_bar_bg);
		
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
		
		ProgressBar progressBar = new ProgressBar(mActivity, null, android.R.attr.progressBarStyleHorizontal);
		progressBar.setProgress(progress);
		progressBar.setProgressDrawable(progressDrawable);
		progressBar.setLayoutParams(params);
		
		progressLay.addView(progressBar, 0);
	}
		
}