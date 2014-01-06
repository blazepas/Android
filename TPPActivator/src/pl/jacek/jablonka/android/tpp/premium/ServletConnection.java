package pl.jacek.jablonka.android.tpp.premium;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class ServletConnection {
	
	Context context;
	
	public ServletConnection(Context context) {
		this.context = context;
	}
	
	public void verificatePremium() {
		
		if (isNetworkOnline()) {
			boolean status = false;
			String requestJson = getRequestJson();
			String responseMessage = postData(requestJson);
			
			if (responseMessage != null) {
				status = checkResponse(responseMessage);
				Toast.makeText(context, "positive", 2000).show();
			} else {
				status = false;
			}
		} else {
			// TODO
		}
	}

	private String postData(String data) {
		String responseMessage;
		
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("http://susuwatari.herokuapp.com/tppPremiumCheck");

	    try {
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	        nameValuePairs.add(new BasicNameValuePair(TppPremiumCheckConsts.PREMIUM_CHECK_JSON_DATA, data));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        HttpResponse response = httpclient.execute(httppost);
	        
	        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
			    builder.append(line).append("\n");
			}
			
			responseMessage = builder.toString();
			Log.i("ServletConnection sended data", data);
			Log.i("ServletConnection response", responseMessage);
			
			int resposneCode = response.getStatusLine().getStatusCode();
			if (resposneCode != 200) {
				responseMessage = null;
			}

	    } catch (ClientProtocolException e) {
	    	responseMessage = null;
	    } catch (IOException e) {
	    	responseMessage = null;
	    }
	    
	    return responseMessage;
	} 
	
	private boolean checkResponse(String responseMessage) {
		try {
			JSONObject responseJson = new JSONObject(responseMessage);
			int premiumStatus = responseJson.getInt("loginStatus");
			
			switch (premiumStatus) {
			case -1:
				return false;
			case 0:
				return true;
			case 1:
				return true;
			}
			
		} catch (JSONException e) {
			Log.i("ServletConnection", "checkResponse() JSONException");
			return false;
		} catch (NullPointerException e) {
			Log.i("ServletConnection", "checkResponse() NullPointerException");
			return false;
		}
		
		return false;
	}
	
	private String getRequestJson() {
		String login = getEmail(context);
		String appVersion = getAppVersion();
		int premium = 0;		
		String hash = getHash(login, premium, appVersion);
		
		JSONObject data = new JSONObject();
		
		try {
			data.put(TppPremiumCheckConsts.HASH, hash);
			data.put(TppPremiumCheckConsts.LOGIN, login);
			data.put(TppPremiumCheckConsts.PREMIUM, premium);
			data.put(TppPremiumCheckConsts.CREATED, "");
			data.put(TppPremiumCheckConsts.APPVERSION, appVersion);
			data.put("id", "");
		} catch (JSONException e) {
			Log.i("ServletConnection", "checkResponse() JSONException");
			return "";
		}
		
				
		return data.toString();
	}
	
	private String getAppVersion() {
		String appVersion;
		try {
			appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			Log.i("get app version", "app name not found");
			appVersion = "1.0.0";
		}
		
		return appVersion;
	}
	
	private String getEmail(Context context) {
	    AccountManager accountManager = AccountManager.get(context); 
	    Account account = getAccount(accountManager);
	
	    if (account == null) {
	      return "";
	    } else {
	      return account.name;
	    }
	}
	
	private Account getAccount(AccountManager accountManager) {
	    Account[] accounts = accountManager.getAccountsByType("com.google");
	    Account account;
	    if (accounts.length > 0) {
	      account = accounts[0];      
	    } else {
	      account = null;
	    }
	    return account;
	}
		
	private String getHash(String login, int premium, String appVersion) {
		
		StringBuffer sbHash = new StringBuffer();
		sbHash.append(login);
		sbHash.append(premium);
		sbHash.append(appVersion);
		sbHash.append(TppPremiumCheckConsts.HASH_SECRET);

		String generatedHash = DigestUtils.md5Hex(sbHash.toString());
		
		return generatedHash;		
	}
	
	private boolean isNetworkOnline() {
	    boolean status = false;
	    try {
	        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
}
