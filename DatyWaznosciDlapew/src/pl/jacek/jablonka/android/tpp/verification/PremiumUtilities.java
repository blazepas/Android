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
	
	private static final String FIRST_RUN = "isFirstRun";
	private static final String IS_PREMIUM = "isPremium";
	private static final String IS_VERIFICATED = "isVerificate";
	private static final String END_TRIAL_DATE_MILLIS = "endTrialDateMillis";

	private Activity mActivity;
	private SharedPreferences preferences;
	
	public PremiumUtilities(Activity mActivity) {
		this.mActivity = mActivity;
		this.preferences = mActivity.getSharedPreferences("premium", Activity.MODE_PRIVATE);
	}
	
	public void setTrial() {
		SharedPreferences.Editor editor = preferences.edit();
		
		long endTrialTime = System.currentTimeMillis() + 2592000000L;
		editor.putLong(END_TRIAL_DATE_MILLIS, endTrialTime);
		editor.commit();
	}
	
	public void setPremium() {
		SharedPreferences.Editor editor = preferences.edit();
		
		editor.putBoolean(IS_PREMIUM, true);
		editor.commit();
	}
	
	public void setVerificated(boolean isVerificated) {
		SharedPreferences.Editor editor = preferences.edit();
		
		editor.putBoolean(IS_VERIFICATED, isVerificated);
		editor.commit();
	}
	
	public void setFirstRunFalse() {
		SharedPreferences.Editor editor = preferences.edit();
		
		editor.putBoolean(FIRST_RUN, false);
		editor.commit();
	}
	
	public boolean isFirstRun() {
		boolean isFirstRun = preferences.getBoolean(FIRST_RUN, true);
		return isFirstRun;
	}
	
	public boolean isTrial() {
		long endTrialTime = preferences.getLong(END_TRIAL_DATE_MILLIS, 0);
		long currentTime = System.currentTimeMillis();
		
		return currentTime < endTrialTime;
	}
	
	public boolean isPremium() {
		boolean status = preferences.getBoolean(IS_PREMIUM, false);
		
		return status;
	}
	
	public boolean isVerificated() {
		boolean status = preferences.getBoolean(IS_VERIFICATED, false);
		
		return status;
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
        PackageManager pm = mActivity.getPackageManager();
        boolean app_installed = false;
        try {
	        pm.getPackageInfo(FinalVariables.PREMIUM_APP_URI, PackageManager.GET_ACTIVITIES);
	        app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
        	app_installed = false;
        }
        return app_installed ;
    }
	
	public boolean isServerVerificate() {
		if (!isNetworkOnline()) {
			return false;
		} else {
			ServletConnection conn = new ServletConnection(mActivity);
			return conn.verificatePremium();
		}
	}	
}
