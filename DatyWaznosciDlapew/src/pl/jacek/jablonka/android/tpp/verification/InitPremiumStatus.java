package pl.jacek.jablonka.android.tpp.verification;

import pl.jacek.jablonka.android.tpp.R;
import pl.jacek.jablonka.android.tpp.atapters.AdapterMenu;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class InitPremiumStatus extends AsyncTask<Void, Void, Void> {
	private boolean isTrial = false;
	private boolean isPremium = false;
	private boolean isPremiumInstalled = false;
	private boolean isVerificated = false;
	private boolean isFirstRun = false;
	
	private PremiumUtilities premUtils;
	private ListView menuList;
	
	public InitPremiumStatus(Activity mActivity, ListView menuList) {
		premUtils = new PremiumUtilities(mActivity);
		this.menuList = menuList;
	}

	@Override
	protected Void doInBackground(Void... params) {
		
		isPremium = premUtils.isPremium();
		if (isPremium) {
			isVerificated = premUtils.isVerificated();			
			if (!isVerificated) {
				isVerificated = premUtils.isServerVerificate();
				premUtils.setVerificated(isVerificated);
			}
		} else {
			isPremiumInstalled = premUtils.isPremiumInstalled();
			if(isPremiumInstalled) {
				premUtils.setPremium();
				isPremium = true;
				isTrial = false;
				isVerificated = premUtils.isServerVerificate();
				premUtils.setVerificated(isVerificated);
			} else {
				//TODO sprawdzać czy nie jest zarejestrowany na serwerze
				isFirstRun = premUtils.isFirstRun();
				if (isFirstRun) {
					premUtils.setFirstRunFalse();
					premUtils.setTrial();
					isTrial = true;
				} else {
					isTrial = premUtils.isTrial();
				}
			}
		}

		return null;
	}
	
	@Override
	protected void onPostExecute(Void v) {
		
		if (isTrial) {
			Log.i("init version", "trial");
			PremiumUtilities.APP_VERSION_TRIAL = true;
			PremiumUtilities.APP_VERSION_PREMIUM = false;
			PremiumUtilities.APP_VERSION_NONE = false;
		} else if (isPremium) {
			Log.i("init version", "premium");
			PremiumUtilities.APP_VERSION_TRIAL = false;
			PremiumUtilities.APP_VERSION_PREMIUM = true;
			PremiumUtilities.APP_VERSION_NONE = false;
		} else {
			Log.i("init version", "trial is over");
			PremiumUtilities.APP_VERSION_TRIAL = false;
			PremiumUtilities.APP_VERSION_PREMIUM = false;
			PremiumUtilities.APP_VERSION_NONE = true;
		}

		changeMenu();
	}
	
	private void changeMenu() {
		AdapterMenu menuAdapter = (AdapterMenu) menuList.getAdapter();
		menuAdapter.notifyDataSetChanged();
		
		if (isPremium) {
			LinearLayout parent = (LinearLayout) menuList.getParent();
			ImageView premium = (ImageView) parent.findViewById(R.id.premiumLogo);
			premium.setVisibility(View.VISIBLE);
		}
		
	}
}
