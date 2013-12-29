package dialogs;

import pl.mareklatuszek.tpp.MainActivity;
import pl.mareklatuszek.tpp.PopUpInfo;
import pl.mareklatuszek.tpp.R;
import utilities.CommonUtilities;
import utilities.FinalVariables;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DialogDodajProdukt extends Dialog implements FinalVariables, android.view.View.OnClickListener {
	
	Activity mActivity;
	CommonUtilities utilities = new CommonUtilities();
	
	LinearLayout scan, custom, scanInfoArea, customInfoArea;
	ImageView scanInfoImage, customInfoImage;
	
	public DialogDodajProdukt(Activity mActivity) {
		super(mActivity);
		this.mActivity = mActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_dodaj_produkt);

		scan = (LinearLayout) findViewById(R.id.scan);
		custom = (LinearLayout) findViewById(R.id.custom);
		scanInfoArea = (LinearLayout) findViewById(R.id.scanInfoArea);
		customInfoArea = (LinearLayout) findViewById(R.id.customInfoArea);

		scan.setOnClickListener(this);
		custom.setOnClickListener(this);
		scanInfoArea.setOnClickListener(this);
		customInfoArea.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		PopUpInfo popup;
		switch (v.getId()) {
		case R.id.scan:
			dismiss();
			scanCode();
			break;
		case R.id.custom:
			selectFragmentDodaj();
			dismiss();
			break;
		case R.id.scanInfoArea:
			popup = new PopUpInfo(mActivity, v);
			popup.showPopUp();
			break;
		case R.id.customInfoArea:
			popup = new PopUpInfo(mActivity, v);
			popup.showPopUp();
			break;
		}
	}
	
	private void selectFragmentDodaj() {
		((MainActivity) mActivity).selectFragment(1);
	}
	
	private void scanCode() {
		((MainActivity) mActivity).selectFragment(0);
	}
	
	
}