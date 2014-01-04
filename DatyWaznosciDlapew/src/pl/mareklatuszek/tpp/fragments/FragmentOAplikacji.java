package pl.mareklatuszek.tpp.fragments;

import pl.mareklatuszek.tpp.TPPApp;
import pl.mareklatuszek.tpp.R;
import pl.mareklatuszek.tpp.dialogs.DialogRegulamin;
import pl.mareklatuszek.tpp.utilities.CommonUtilities;
import pl.mareklatuszek.tpp.utilities.FinalVariables;
import pl.mareklatuszek.tpp.views.TextViewBariol;
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

public class FragmentOAplikacji extends SherlockFragment implements FinalVariables {
	
	CommonUtilities utilities = TPPApp.getUtilities();
	
	View rootView;
	TextViewBariol versionTxt, regulaminTxt;


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
		regulaminTxt = (TextViewBariol) rootView.findViewById(R.id.regulaminTxt);
		
		versionTxt.setText(getAppVersion());
		regulaminTxt.setText(getRegulationsTxt());
		
		regulaminTxt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showRegulations();
				
			}
		});
			
		return rootView;
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
		String regulations; 
		regulations = "<html><body><u>" + getString(R.string.tv_regulations) + "</u></body></html>";
		return Html.fromHtml(regulations);
	}
	
	private void showRegulations() {
		DialogRegulamin dialog = new DialogRegulamin(getActivity());
		dialog.show();
	}
	
}
