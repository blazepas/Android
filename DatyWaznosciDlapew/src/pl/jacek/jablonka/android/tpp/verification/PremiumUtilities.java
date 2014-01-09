package pl.jacek.jablonka.android.tpp.verification;

import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class PremiumUtilities {
	
	public static boolean APP_VERSION_TRIAL = false;
	public static boolean APP_VERSION_PREMIUM = false;
	public static boolean APP_VERSION_NONE = true;
	
	public static final String PREMIUM_APP_URI = "pl.jacek.jablonka.android.tpp.premium";
	public static final String OLD_PREMIUM_APP_URI = "pl.mareklatuszek.tpp.premium";
	
	public static final String PREFERENCES_PREMIUM = "premium";
	public static final String PREFERENCES_END_TRIAL = "endTrial";
	public static final String PREFERENCES_END_PREMIUM = "endPremium";
	public static final String PREFERENCES_INSTALL_DATE = "installDate";
	public static final String PREFERENCES_PREMIUM_INSTALL_DATE = "premiumInstallDate";
	public static final String PREFERENCES_IS_VERIFICATED = "isVerificated";
	
	public static final long PEROID_TRIAL = TimeUnit.MILLISECONDS.convert(30L, TimeUnit.DAYS); // 30 dni  // milisekundy
	public static final long PEROID_PREMIUM = TimeUnit.MILLISECONDS.convert(365L, TimeUnit.DAYS); // 1 rok

	private Activity mActivity;
	private SharedPreferences preferences;
	
	public PremiumUtilities(Activity mActivity) {
		this.mActivity = mActivity;
		this.preferences = mActivity.getSharedPreferences(PREFERENCES_PREMIUM, Activity.MODE_PRIVATE);
	}
		
	public void setInstallDate() {
		long installDate = System.currentTimeMillis();
		
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(PREFERENCES_INSTALL_DATE, installDate);
		editor.commit();
	}
	
	public void setPremiumInstallDate() {
		long installDate = System.currentTimeMillis();
		
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(PREFERENCES_PREMIUM_INSTALL_DATE, installDate);
		editor.commit();
	}

	public long getInstallDate() {
		long installDate = preferences.getLong(PREFERENCES_INSTALL_DATE, 0);
		return installDate;
	}
	
	public long getPremiumInstallDate() {
		long installDate = preferences.getLong(PREFERENCES_PREMIUM_INSTALL_DATE, 0);
		return installDate;
	}
	
	public boolean isVerificatdOnServer() {
		if (!isNetworkOnline()) {
			return false;
		} else {
			ServletConnection conn = new ServletConnection(mActivity);
			return conn.verificatePremium();
		}
	}
	
	public boolean isTrial(long currentTime) {
		long installDate = getInstallDate();
		
		if(installDate == 0) {
			setInstallDate();
			return true;
		} else {
			long difference = currentTime - installDate;
			if(difference < PEROID_TRIAL) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public boolean isPremium(long currentTime) {
		long premiumIstallDate = getPremiumInstallDate();
		boolean isPremiumInstalled = isPremiumInstalled();
		
		if(premiumIstallDate == 0) {
			if(isPremiumInstalled) {
				setPremiumInstallDate();
				return true;
			} else {
				return false;
			}
		} else {
			long difference = currentTime - premiumIstallDate;
			
			if(difference < PEROID_PREMIUM) {
				return true;
			} else {
				return false;
			}
		}

	}
	
	public boolean isNetworkOnline() {
	    boolean status = false;
	    try {
	        ConnectivityManager cm = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo netInfo = cm.getNetworkInfo(0);
	        if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
	            status= true;
	        } else {
	            netInfo = cm.getNetworkInfo(1);
	            if(netInfo!=null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
	                status= true;
	            }
	        }
	    } catch(Exception e) {
	        e.printStackTrace();  
	        return false;
	    }
	    return status;
	} 
	
	public boolean isPremiumInstalled() {
//        PackageManager pm = mActivity.getPackageManager();
//		String installerName = null;
//		
//        try {
//        	
//        	installerName = pm.getInstallerPackageName(FinalVariables.PREMIUM_APP_URI);
//        }
//        catch (Exception e) {
//        	
//        	return false;
//        }
//        
//        if (installerName != null) {
//        	
//        	if (installerName.equals("com.android.vending")) {
//        		
//        		return true;       		
//        	} else {
//        		
//        		return false;      		
//        	}          	
//        } else {
//        	
//        	return false;
//        }
		
		if (TESTisRealPremiumInstalled()) {
			return true;
		} else {
			return TESTisOlsPremiumInstalled();
		}

    }
	
	private boolean TESTisRealPremiumInstalled() {
		PackageManager pm = mActivity.getPackageManager();
		String installerName = null;
		
        try {
        	
        	installerName = pm.getInstallerPackageName(PREMIUM_APP_URI);
        }
        catch (Exception e) {
        	return false;
        }
        
        if (installerName != null) {
        	
        	if (installerName.equals("com.android.vending")) {
        		return true;       		
        	} else {
        		return false;      		
        	}          	
        } else {
        	return false;
        }
	}
	
	private boolean TESTisOlsPremiumInstalled() {
		PackageManager pm = mActivity.getPackageManager();
        try {
                pm.getPackageInfo(OLD_PREMIUM_APP_URI, PackageManager.GET_ACTIVITIES);
                return true;
        }
        catch (PackageManager.NameNotFoundException e) {
                return false;
        }
	}
	
	public void checkVerification() {
		// symulacja opóźnienia odpowiedzi z serwera
        try {
                Thread.sleep(5000);
        } catch (InterruptedException e) {
                e.printStackTrace();
        }
        Log.i("init version", "po opoznieniu");
//		boolean isVerificated = preferences.getBoolean(PREFERENCES_IS_VERIFICATED, false);
//		if (!isVerificated) {
//			isVerificated = isVerificatdOnServer();
//			
//			SharedPreferences.Editor editor = preferences.edit();
//			editor.putBoolean(PREFERENCES_IS_VERIFICATED, isVerificated);
//			editor.commit();
//		}
	}
}
