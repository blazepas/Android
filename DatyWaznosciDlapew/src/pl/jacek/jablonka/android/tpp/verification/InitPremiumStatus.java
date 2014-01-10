package pl.jacek.jablonka.android.tpp.verification;

import pl.jacek.jablonka.android.tpp.MainActivity;
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
	
	Activity mActivity;
	private PremiumUtilities premUtils;
	private ListView menuList;
	
	public InitPremiumStatus(Activity mActivity, ListView menuList) {
		premUtils = new PremiumUtilities(mActivity);
		this.menuList = menuList;
		this.mActivity = mActivity;
	}

	@Override
	protected Void doInBackground(Void... params) {
		long currentTime = System.currentTimeMillis();		
		isPremium = premUtils.isPremium(currentTime);
		
		if (!isPremium) {
			isTrial = premUtils.isTrial(currentTime);
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
			new Verify().execute();
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
		} else {
			((MainActivity) mActivity).runAds();
		}
		
	}
	
	private class Verify extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			premUtils.checkVerification();
			return null;
		}
	}
}
