package pl.jacek.jablonka.android.tpp.verification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import pl.jacek.jablonka.android.tpp.utilities.FinalVariables;
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
	
	public static final long PEROID_TRIAL = 2592000000L; // 30 dni  // milisekundy
	public static final long PEROID_PREMIUM = 31536000000L; // 1 rok

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

	public long getInstallDate() {
		long installDate = preferences.getLong(PREFERENCES_INSTALL_DATE, 0);
		return installDate;
	}
	

	public boolean isServerVerificate() {
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
		long installDate = getInstallDate();
		boolean isPremiumInstalled = isPremiumInstalled();
		
		if(isPremiumInstalled) {
			if(installDate == 0) {
				setInstallDate();
				return true;
			} else {
				long difference = currentTime - installDate;
				
				if(difference < PEROID_PREMIUM) {
					return true;
				} else {
					return false;
				}
			}

		} else {
			if(installDate == 0) {
				setInstallDate();				
			}
			return false;
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
}
