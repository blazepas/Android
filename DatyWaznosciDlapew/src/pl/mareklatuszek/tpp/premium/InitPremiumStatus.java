package pl.mareklatuszek.tpp.premium;

import pl.mareklatuszek.tpp.atapters.AdapterMenu;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class InitPremiumStatus extends AsyncTask<Void, Void, Void> {
	private boolean isTrial = false;
	private boolean isPremium = false;
	private boolean isPremiumInstalled = false;
	private boolean isVerificated = false;
	private boolean isFirstRun = false;
	
	private PremiumUtilities premUtils;
	AdapterMenu menuAdapter;
	
	public InitPremiumStatus(Activity mActivity, AdapterMenu menuAdapter) {
		premUtils = new PremiumUtilities(mActivity);
		this.menuAdapter = menuAdapter;
	}

	@Override
	protected Void doInBackground(Void... params) {
		
		isPremium = premUtils.isPremium();
		
		if (isPremium) {
			return null;
		} else {
			isPremiumInstalled = premUtils.isPremiumInstalled();
			
			if(isPremiumInstalled) {
				isVerificated = premUtils.isVerificated();
				if (isVerificated) {					
					premUtils.setPremium();
					isPremium = true;
					isTrial = false;
					return null;
				}
			}
		}
		
		isFirstRun = premUtils.isFirstRun();
		
		if (isFirstRun) {
			premUtils.setFirstRunFalse();
			premUtils.setTrial();
			isTrial = true;
		} else {
			isTrial = premUtils.isTrial();
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
		
		menuAdapter.notifyDataSetChanged();
	}
}
