package pl.jacek.jablonka.android.tpp.fragments;

import pl.jacek.jablonka.android.tpp.R;
import pl.jacek.jablonka.android.tpp.TPPApp;
import pl.jacek.jablonka.android.tpp.dialogs.DialogPremium;
import pl.jacek.jablonka.android.tpp.dialogs.DialogRegulamin;
import pl.jacek.jablonka.android.tpp.utilities.CommonUtilities;
import pl.jacek.jablonka.android.tpp.utilities.FinalVariables;
import pl.jacek.jablonka.android.tpp.verification.PremiumUtilities;
import pl.jacek.jablonka.android.tpp.views.TextViewBariol;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class FragmentOAplikacji extends SherlockFragment implements FinalVariables, OnClickListener {
	
	CommonUtilities utilities = TPPApp.getUtilities();
	
	View rootView;
	TextViewBariol versionTxt, versionTypeTxt, regulaminTxt;


	@Override
	public void onResume() {
		super.onResume();
		
		String title = getString(R.string.frag_about_title);
		utilities.setActionBarTitle(title, getSherlockActivity());
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_o_aplikacji, container, false);
		
		versionTxt = (TextViewBariol) rootView.findViewById(R.id.versionTxt);
		versionTypeTxt = (TextViewBariol) rootView.findViewById(R.id.versionTypeTxt);
		regulaminTxt = (TextViewBariol) rootView.findViewById(R.id.regulaminTxt);
		
		versionTxt.setText(getAppVersion());
		versionTypeTxt.setText(getVersionType());
		regulaminTxt.setText(getRegulationsTxt());
		
		regulaminTxt.setOnClickListener(this);
			
		return rootView;
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.regulaminTxt:
			showRegulations();
			break;
		case R.id.versionTypeTxt:
			DialogPremium dialog = new DialogPremium(getActivity());
			dialog.show();
			break;		
		}
		
	}
	
	private String getAppVersion() {
		String appVersion;
		try {
			appVersion = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			Log.i("get app version", "app name not found");
			appVersion = "1.0.0";
		}
		
		appVersion = getString(R.string.tv_version) + " " + appVersion;
		return appVersion;
	}
	
	private Spanned getRegulationsTxt() {
		String hypertext; 
		String regulations = getString(R.string.tv_regulations);
		hypertext = "<html><body><u>" + regulations + "</u></body></html>";
		return Html.fromHtml(hypertext);
	}
	
	private Spanned getVersionType() {
		SharedPreferences preferences;
		preferences = getActivity()
				.getSharedPreferences(PremiumUtilities.PREFERENCES_PREMIUM, Activity.MODE_PRIVATE);
		
		
		long currentDate = System.currentTimeMillis();
		String versionType = "";
		String days = getString(R.string.tv_days);
				
		if (PremiumUtilities.APP_VERSION_TRIAL) {
			
			long installDate = preferences.getLong(PremiumUtilities.PREFERENCES_INSTALL_DATE, 0);		
			long endTrialDate = installDate + PremiumUtilities.PEROID_TRIAL;			
			long difference = endTrialDate - currentDate;
			int daysCount = (int) (difference / 86400000L); // dni
			
			String endDate = utilities.parseMillisToDate(endTrialDate);
			String versionLeft = getString(R.string.tv_version_trial_left);			

			versionType = versionLeft + " " + daysCount + " " + days + " (" + endDate + ")";
		} else if (PremiumUtilities.APP_VERSION_PREMIUM) {
			
			long premiumInstallDate = preferences.getLong(PremiumUtilities.PREFERENCES_PREMIUM_INSTALL_DATE, 0);
			long endPremiumDate = premiumInstallDate + PremiumUtilities.PEROID_PREMIUM;
			long difference = endPremiumDate - currentDate;
			int daysCount = (int) (difference / 86400000L); // dni
			
			String endDate = utilities.parseMillisToDate(endPremiumDate);
			String versionLeft = getString(R.string.tv_version_premium_left);

			versionType = versionLeft + " " + daysCount + " " + days + " (" + endDate + ")";						
		} else {

			String versionNon = getString(R.string.tv_version_non);
			String buyPremium = getString(R.string.tv_buy_premium_non);
			String toUnlock = getString(R.string.tv_to_unlock);
			versionType = versionNon + ". " + "<html><body><u>" + buyPremium + "</u></body></html>" + ", " + toUnlock;
			versionTypeTxt.setOnClickListener(this);
		}
		
		return Html.fromHtml(versionType);
	}
	
	private void showRegulations() {
		DialogRegulamin dialog = new DialogRegulamin(getActivity());
		dialog.show();
	}
	
}
