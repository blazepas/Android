package com.mareklatuszek.utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.zxing.WriterException;
import com.mareklatuszek.datywaznosci.Product;
import com.mareklatuszek.datywaznosci.R;

public class CommonUtilities implements FinalVariables {
	
	public int getProgress(String dataOtw, String terminWaz) {
		
		int progress = 100;
		
		try {
			
			double start = parseDate(dataOtw);
			double end = parseDate(terminWaz);
			double current = (new Date()).getTime();
			
			double pr = (((end - current) / (end - start))) * 100;
			
			progress = (int) pr;
			
		} catch (ParseException e) {
			return 100;
		}
		
		return progress;		
	}
	
	public long parsePrzypmnienieToDate(String boxVal, String spinnerChoice, String terminWaz, String notifHour) throws ParseException {
		if(boxVal.equals("")) {
			return 0;
		} else {
			int dateVal = Integer.parseInt(boxVal);
			long endDate = parseDate(terminWaz);
			
			Calendar notifTime = Calendar.getInstance();
			notifTime.setTimeInMillis(endDate);
			
			if (spinnerChoice.equals(SPINNER_DATE_DAY)) { //jesli dzien (kolejnosc z resource arrays "array_date" - trzyma� si� tej kolejnosci!)
				notifTime.add(Calendar.DAY_OF_YEAR, -dateVal);
			} else if (spinnerChoice.equals(SPINNER_DATE_MONTH)) {
				notifTime.add(Calendar.MONTH, -dateVal );
			} else if (spinnerChoice.equals(SPINNER_DATE_YEAR)) { 
				notifTime.add(Calendar.YEAR, -dateVal);
			}
			
			if (notifHour.equals("")) { //format np 14:0
				notifTime.add(Calendar.HOUR_OF_DAY, 14);
			} else {
				String hour = getFirstValue(notifHour);
				String min = getSecondValue(notifHour);
				int h = Integer.parseInt(hour);// format to 0-23
				int m = Integer.parseInt(min);// format to 0-23
				notifTime.add(Calendar.HOUR_OF_DAY, h);
				notifTime.add(Calendar.MINUTE, m);
			}
						
			long przypomnienieDate = notifTime.getTimeInMillis();
			
			return przypomnienieDate;
		}
	}
	
	public long parseDate(String dateToParse) throws ParseException {
		String toParse = dateToParse;
		SimpleDateFormat formatter = new SimpleDateFormat("d/M/yyyy");
		Date date = formatter.parse(toParse);
		long millis = date.getTime();
		
		return millis;
	}
	
	public String parseDateToOkres(String date) {
		Calendar currCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();
		currCal.set(Calendar.HOUR_OF_DAY, 13);
		long currentTime = currCal.getTimeInMillis();
		long endTime = 0;
		try {
			endTime = parseDate(date);
			endCal.setTimeInMillis(endTime);
			endCal.set(Calendar.HOUR_OF_DAY, 14);
			endTime = endCal.getTimeInMillis();
			
			long difference = endTime - currentTime;
			long diffDay = difference / 86400000L;
			long diffMonth = difference / 2592000000L;
			long diffYear = difference / 31536000000L;
			
			if(diffYear >= 1L) {
				int i = (int) diffYear;
				String okres = i + ":" + SPINNER_DATE_YEAR;
				return okres;

			} else if (diffMonth >= 1L) {
				int i = (int) diffMonth;
				String okres = i + ":" + SPINNER_DATE_MONTH;
				return okres;

			} else if (diffDay >= 1L) {
				int i = (int) diffDay;
				String okres = i + ":" + SPINNER_DATE_DAY;
				return okres;
			} else {
				return "";
			}
			
		} catch (ParseException e) {
			Log.i("utils", "parseDateToOkres");
			return "";
		}		
	}
	
	public String parseOkresToDate(String okres) {
		Calendar cal = Calendar.getInstance();
		String box = getFirstValue(okres);
		String okresDate = "";
		int val = 0;
		
		if (!box.equals("")) {
			val = Integer.parseInt(box);
		} else {
			return "";
		}
		
		String format = getSecondValue(okres);		
		
		if(format.contains(SPINNER_DATE_DAY)) {
			cal.add(Calendar.DAY_OF_YEAR, val);
		} else if (format.contains(SPINNER_DATE_MONTH)) {
			cal.add(Calendar.MONTH, val);
		} else if (format.contains(SPINNER_DATE_YEAR)) {
			cal.add(Calendar.YEAR, val);
		}
		
		long okresInMillis = cal.getTimeInMillis();
		okresDate = parseMillisToDate(okresInMillis);
		
		return okresDate;
	}
	
	public Product parseCodeToProduct(String code){
		Product product = new Product();
		
		try {
			JSONObject jObj = new JSONObject(code);
			
			String nazwa = jObj.getString(DB_NAZWA);
			String dataOtwarcia = jObj.getString(DB_DATA_OTWARCIA);
			String kategoria = jObj.getString(DB_KATEGORIA);
			String okresWaznosci = jObj.getString(DB_OKRES_WAZNOSCI);
			String opis = jObj.getString(DB_OPIS);
			String przypomnieniaJson = jObj.getString(DB_PRZYPOMNIENIA);
			String terminWaznosci = jObj.getString(DB_TERMIN_WAZNOSCI);
			String dataZuz = jObj.getString(DB_DATA_ZUZYCIA);
			
			product.setCodeFormat("QR_CODE");
			product.setNazwa(nazwa);
			product.setDataOtwarcia(dataOtwarcia);			
			product.setKategoria(kategoria);		
			product.setOkresWaznosci(okresWaznosci);		
			product.setOpis(opis);
			product.setPrzypomnieniaFromDB(przypomnieniaJson);
			product.setTerminWaznosci(terminWaznosci);
			product.setDataZuzycia(dataZuz);
			
			return product;
		} catch (JSONException e) {
			return product;
		}			
	}
	
	public String dateToWords(long startTimeInMillis, long endTimeInMillis)
	{
		String time = "";
		long endTimeInSec = endTimeInMillis / 1000;
		long startTimeInSec = (int) (startTimeInMillis / 1000L);
		long difference = endTimeInSec - startTimeInSec;
		
		if (difference < 0) {
			time = "Powiadomino";
			return time;
		} else if (0 < difference & difference < 3600) {
			time = "godzinę";
			return time;
		} else if (difference >= 3600 & difference < 14400) {
			time = (difference/3600) + " godziny";
			return time;
		} else if (difference >= 14400 & difference < 75600) {
			time = (difference/3600) + " godzin";
			return time;
		} else if (difference >= 75600 & difference < 86400) {
			time = (difference/3600) + " godziny";
			return time;
		} else if (difference >= 86400 & difference < 172800) {
			time = "1 dzień";
			return time;
		} else if (difference >= 172800 & difference < 259200) {
			time = "2 dni";
			return time;
		} else if (difference >= 259200 & difference < 604800) {
			time = (difference/86400) + " dni";
			return time;
		} else if (difference >= 604800 & difference < 1209600) {
			time = "tydzień";
			return time;
		} else if (difference >= 1209600 & difference < 2419200) {
			time = (difference/604800) + " tygodnie";
			return time;
		} else if (difference >= 2419200 & difference < 4838400) {
			time = "miesiąc";
			return time;
		} else if (difference >= 4838400 & difference < 10540800) {
			time = (difference/2419200) + " mies.";
			return time;
		} else if (difference >= 10540800 & difference < 29030400) {
			time = (difference/2419200) + " mies.";
			return time;
		} else if (difference >= 29030400 & difference < 58060800) {
			time = "rok";
			return time;
		} else if (difference >= 58060800 & difference < 145152000) {
			time = (difference/29030400) + " lata";
			return time;
		} else {
			time = (difference/29030400) + "lat";
			return time;
		}
		
	}
	
	public ArrayList<HashMap<String, String>> sortPrzypomnieniaAll(ArrayList< HashMap< String,String >> toSort) {
		
	    Collections.sort(toSort, new Comparator<HashMap< String,String >>() {

	        @Override
	        public int compare(HashMap<String, String> first, HashMap<String, String> second) {
	        	//sortuje wg daty przypomenia
	        	String firstValue = first.get(PRZYP_DATE);
	            String secondValue = second.get(PRZYP_DATE);
	            return firstValue.compareTo(secondValue);
	        }
	    });
	    
		return toSort;
	}
	
	public String getCurrentDate() {
		String dateFormat = "dd/MM/yyyy";
		String currentDate = "";
		
	    DateFormat formatter = new SimpleDateFormat(dateFormat);
	    Calendar calendar = Calendar.getInstance();
	    currentDate = formatter.format(calendar.getTime());
	    
	    return currentDate;
	}
	
	public String parseMillisToDate(long timeInMillis) {
		String dateFormat = "dd/MM/yyyy";
		String date = "";
		
	    DateFormat formatter = new SimpleDateFormat(dateFormat);
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(timeInMillis);
	    date = formatter.format(calendar.getTime());
	    
	    return date;
	}
	
	@SuppressWarnings("deprecation")
	public void expandLinearLayout(final LinearLayout v) {
	    v.measure(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	    v.getLayoutParams().height = 0;
	    v.setVisibility(View.VISIBLE);
	    
	    Animation a = new Animation()
	    {
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) {
	        	int width = LinearLayout.LayoutParams.FILL_PARENT;
	        	int height = LinearLayout.LayoutParams.WRAP_CONTENT;
	        	LayoutParams params = new LinearLayout.LayoutParams(width, height);// rozmiary
	        	
	        	v.setLayoutParams(params);
	            v.requestLayout();
	        }

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };

	    a.setDuration(500);
	    v.startAnimation(a);	    
	}
	
	public Bitmap encodeCodeToBitmap(String code, String codeFormat, FragmentActivity mActivity)
	{
		//konwersja zeskanowanego kodu na obrazek, na podstawie kodu i jego formatu
				
	    int qrCodeDimention = 150; //rozmiar obrazka

	    QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(code, null, Contents.Type.TEXT, codeFormat, qrCodeDimention);

	    try {
	        Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
	        return bitmap;
	    } catch (WriterException e) {
	        return null;
	    } catch (IllegalArgumentException e) {
	        return null;
	    }
	}
	
	public int getPosInSpinner(String category, Spinner spinner) {
		
		int spinnSize = spinner.getCount();
		
		for (int i = 0; i < spinnSize; i++) {
			String item = (String) spinner.getItemAtPosition(i);
			if (item.contains(category)) {
				return i;
			}
		}
		
		return 0;
	}
	
	public String getFirstValue(String okres) {
		String box = "";
		Pattern pattern = Pattern.compile(":");
		
		Matcher matcher = pattern.matcher(okres);
		if (matcher.find()) {
			box = okres.substring(0, matcher.start());
		}
		
		return box;
	}
	
	public String getSecondValue(String okres) {
		String spinn = "";	
		Pattern pattern = Pattern.compile(":");
		
		Matcher matcher = pattern.matcher(okres);
		if (matcher.find()) {
			spinn = okres.substring(matcher.end());
		}
				
		return spinn;
	}
	
	public String getJsonFromProduct(Product product) {
		String json = "";
		try {
			String nazwa = product.getNazwa();
			String okres = product.getOkresWaznosci();
			String codeFormat = product.getCodeFormat();
			String dataOtw = product.getDataOtwarcia();
			String terminWaz = product.getTerminWaznosci();
			String kategoria = product.getKategoria();
			String opis = product.getOpis();
			String dataZuz = product.getDataZuzycia();
			
			JSONArray przypoimnienia = new JSONArray(product.getPrzypomnieniaToDB());
			
			JSONObject jObj = new JSONObject();
			jObj.put(DB_NAZWA, nazwa);
			jObj.put(DB_OKRES_WAZNOSCI, okres);
			jObj.put(DB_CODE_FORMAT, codeFormat);
			jObj.put(DB_DATA_OTWARCIA, dataOtw);
			jObj.put(DB_TERMIN_WAZNOSCI, terminWaz);
			jObj.put(DB_KATEGORIA, kategoria);
			jObj.put(DB_OPIS, opis);
			jObj.put(DB_PRZYPOMNIENIA, przypoimnienia);
			jObj.put(DB_DATA_ZUZYCIA, dataZuz);
		
			json = jObj.toString();
		} catch (JSONException e) {
			Log.i("utils", "getJsonFromProduct");
		}
		return json;
	}
	
	public JSONArray getProductsTableJSON(ArrayList<Product> products) {
		
		JSONArray lista = new JSONArray();
			
		for (int i = 0; i < products.size(); i++) {
			Product product = products.get(i);
			
			String nazwa = product.getNazwa();
			String dataOtw = product.getDataOtwarcia();
		    String terminWaz = product.getTerminWaznosci();
		    String kategoria = product.getKategoria();
		    
		    JSONObject jObj = new JSONObject();
		    try {
				jObj.put(DB_NAZWA, nazwa);
				jObj.put(DB_DATA_OTWARCIA, dataOtw);
				jObj.put(DB_TERMIN_WAZNOSCI, terminWaz);
				jObj.put(DB_KATEGORIA, kategoria);
			} catch (JSONException e) {
				Log.i("utilities", "get product list to email");
			}
		    
		    lista.put(jObj);
		   
		}
		
		return lista;
	}
	
	public String getProductsTableHTML(ArrayList<Product> products) {
		
		StringBuilder table = new StringBuilder();
		table.append("<html><body><table border='1'>");
			
		for (int i = 0; i < products.size(); i++) {
			StringBuilder row = new StringBuilder();
			
			Product product = products.get(i);		
			String nazwa = product.getNazwa();
			String dataOtw = product.getDataOtwarcia();
		    String terminWaz = product.getTerminWaznosci();
		    String kategoria = product.getKategoria();
		    
		    row.append("<tr>");
		    
		    row.append("<td>" + nazwa + "</td>");
		    row.append("<td>" + dataOtw + "</td>");
		    row.append("<td>" + terminWaz + "</td>");
		    row.append("<td>" + kategoria + "</td>");
		    
		    row.append("</tr>");
		    
		    table.append(row);
		}
		
		table.append("</table></body></html>");
		
		return table.toString();
	}
	
	public String getProductTableHTML(Product product) {
			
		String nazwa = product.getNazwa();
		String dataOtw = product.getDataOtwarcia();
		String terminWaz = product.getTerminWaznosci();
		long dataOtwMillis;
		long terminWazMillis;
		long currentTime = System.currentTimeMillis();
		String okresWazPeroid;
		try {
			dataOtwMillis = parseDate(dataOtw);
			terminWazMillis = parseDate(terminWaz);
			okresWazPeroid = dateToWords(dataOtwMillis, terminWazMillis);
		} catch (ParseException e) {
			okresWazPeroid = "";
			terminWazMillis = currentTime;
		}
		
		String terminPeroid = dateToWords(currentTime, terminWazMillis);		   
	    String kategoria = product.getKategoria();
	    
	    StringBuilder table = new StringBuilder();
		table.append("<html><body><table border='1'>");
	    
	    table.append("<tr>");
	    table.append("<td>Nazwa</td>");
	    table.append("<td>" + nazwa + "</td>");
	    table.append("</tr>");
	    
	    table.append("<tr>");
	    table.append("<td>Data otwarcia</td>");
	    table.append("<td>" + dataOtw + "</td>");
	    table.append("</tr>");
	    
	    table.append("<tr>");
	    table.append("<td>Okres ważności</td>");
	    table.append("<td>" + okresWazPeroid + "</td>");
	    table.append("</tr>");
	    
	    table.append("<tr>");
	    table.append("<td>Okres ważności mija za</td>");
	    table.append("<td>" + terminPeroid + "</td>");
	    table.append("</tr>");
	    
	    table.append("<tr>");
	    table.append("<td>Termin ważności</td>");
	    table.append("<td>" + terminWaz + "</td>");
	    table.append("</tr>");
	    
	    table.append("<tr>");
	    table.append("<td>Kategoria</td>");
	    table.append("<td>" + kategoria + "</td>");
	    table.append("</tr>");
	    
	    table.append("<tr>");
	    table.append("<td>Obrazek</td>");
	    table.append("<td><img src=\"cid:image\"></td>");
	    table.append("</tr>");
	    
	    table.append("<tr>");
	    table.append("<td>Kod</td>");
	    table.append("<td><img src=\"cid:image2\"></td>");
	    table.append("</tr>");
	
		table.append("</table></body></html>");
		
		return table.toString();
	}
	
	public String getRealPathFromURI(Uri contentUri, FragmentActivity mActivity) {
	    String[] proj = { MediaStore.Images.Media.DATA };
	    Cursor cursor = mActivity.managedQuery(contentUri, proj, null, null, null);
	    if (cursor == null) {
	      return contentUri.getPath();
	    }
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	
	public int getPixelsFromDp(int dp, Activity mActivity) {
		float scale = mActivity.getResources().getDisplayMetrics().density;
		int pixels = (int) (dp * scale + 0.5f);
		return pixels;
	}
	
	public void setActionBarTitle(String title, SherlockFragmentActivity mActivity) {
		LinearLayout actionBarLayout = (LinearLayout) mActivity.getSupportActionBar().getCustomView();
		actionBarLayout.setOrientation(LinearLayout.VERTICAL);
		TextView titleTxt = (TextView) actionBarLayout.findViewById(R.id.title);
		titleTxt.setText(title);
		mActivity.getSupportActionBar().setCustomView(actionBarLayout);
	}
	
	public boolean validateCode(String code, String codeFormat) {
		if(codeFormat.equals("QR_CODE")){
			try {
				Product product = new Product();
				String productJson = getJsonFromProduct(product);
				
				JSONObject jObjCode = new JSONObject(code);
				JSONObject jObjEmpty = new JSONObject(productJson);
				JSONArray emptyNames = jObjEmpty.names();

				for (int i = 0; i < emptyNames.length(); i++) {
					if (!jObjCode.has(emptyNames.getString(i))) {
						return false;
					}
				}
				return true;
			} catch (JSONException e) {
				Log.i("Utils", "code is not product");
				return false;
			}	
		} else {
			return false;
		}
	}
	
	public void sendEmail(String productJson, FragmentActivity mActivity) {			
		try {
			Bitmap bitmap = encodeCodeToBitmap(productJson, "QR_CODE", mActivity);
			Uri u = null;
			
			File mFile = savebitmap(bitmap);
			u = Uri.fromFile(mFile);
						
			//TODO jesli nie ma kardy sd 

			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("application/image");
			i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"marek.lat@gmail.com"});
			i.putExtra(Intent.EXTRA_SUBJECT, "Test");
			i.putExtra(Intent.EXTRA_TEXT   , "W załączniku przesyłam kod, który po zeskanowaniu programem TPP zostanie dodany do bazy");
			i.putExtra(Intent.EXTRA_STREAM, u);
			
			mActivity.startActivity(Intent.createChooser(i, "Wysyłanie..."));
			
			
		} catch (IOException e) {
			Log.i("sendEmail", "save file error");
			Toast.makeText(mActivity, "Brak karty SD", Toast.LENGTH_SHORT).show();
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(mActivity, "Brak klienta email", Toast.LENGTH_SHORT).show();
		}			
	}
	
	public boolean sendEmailWithProductList(String email, String table) {
		try {  
            JavaMail sender = new JavaMail();
            sender.sendMailWithList("Lista produktów z programu TPP", table, email);
        } catch (Exception e) {   
             return false; 
        } 
		return true;
	}
	
	public boolean sendEmailWithSingleProduct(String email, Product product, FragmentActivity mActivity) {
		boolean sendStatus = false;
		try {  
			String table = getProductTableHTML(product);
			String imagePath = product.getImage();
			if (imagePath != null && !imagePath.isEmpty()) {
				imagePath += "thumb";
				
			}	
			String productJson = getJsonFromProduct(product);
			Bitmap bitmap = encodeCodeToBitmap(productJson, "QR_CODE", mActivity);
			Uri u = null;
			
			File mFile = savebitmap(bitmap);
			u = Uri.fromFile(mFile);
			String codePath = getRealPathFromURI(u, mActivity);
			
            JavaMail sender = new JavaMail();
            sendStatus = sender.sendMailWithProduct("Mój produkt", table, imagePath, codePath, email);
        } catch (Exception e) {   
        	Log.i("sendEmailWithSingleProduct", "error");
             return sendStatus; 
        } 
		return sendStatus;
	}
			
	public File savebitmap(Bitmap bmp) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

		File f = new File(Environment.getExternalStorageDirectory() + File.separator + "code.jpg");
		f.createNewFile();

		FileOutputStream fo = new FileOutputStream(f);
		fo.write(bytes.toByteArray());

		fo.close();
		return f;
	}
	
	public void startAlarms(ArrayList<HashMap<String, String>> przypomnienia, String nazwa, String productCode, FragmentActivity mActivity) {
		for (int i = 0; i < przypomnienia.size(); i++) {
			HashMap<String, String> przypomnienie = przypomnienia.get(i);
			String time = przypomnienie.get(PRZYP_DATE);
			startAlarm(time, nazwa, productCode, mActivity);
		}
	}
	
	public void cancelAlarms(ArrayList<HashMap<String, String>> przypomnienia, String productCode, FragmentActivity mActivity) {
		for (int i = 0; i < przypomnienia.size(); i++) {
			HashMap<String, String> przypomnienie = przypomnienia.get(i);
			String time = przypomnienie.get(PRZYP_DATE);
			cancelAlarm(time, productCode, mActivity);
		}
	}
	
	public void startAlarm(String alarmTimeInMillis, String nazwa, String productCode, FragmentActivity mActivity) {
		long alarmTime = Long.parseLong(alarmTimeInMillis);	
	    AlarmManager alarmManager = (AlarmManager) mActivity.getSystemService(mActivity.ALARM_SERVICE);
	    
	    Calendar cal = new GregorianCalendar();
	    cal.setTimeInMillis(alarmTime);
	    
	    long when = cal.getTimeInMillis(); // czas powiadomienia
	    int intentId = productCode.hashCode();
	    Intent intent = new Intent(mActivity, ReminderService.class);
	    intent.putExtra("message", "Przypomnienie o koncu ważności produktu: " + nazwa);
	    intent.putExtra("productCode", productCode);
	    intent.putExtra("timeInMillis", alarmTimeInMillis);
	    PendingIntent pendingIntent = PendingIntent.getService(mActivity, intentId, intent, 0);
	    alarmManager.set(AlarmManager.RTC_WAKEUP, when, pendingIntent);
	            
	    Log.i("Alarm ustawiony", drukujCzas(cal));
          
	}
	
	public void cancelAlarm(String alarmTimeInMillis, String productCode, FragmentActivity mActivity) {
		long alarmTime = Long.parseLong(alarmTimeInMillis);		
	    AlarmManager alarmManager = (AlarmManager) mActivity.getSystemService(mActivity.ALARM_SERVICE);
	    
	    Calendar cal = new GregorianCalendar();
	    cal.setTimeInMillis(alarmTime);
	    
	    int intentId = productCode.hashCode();
	    Intent intent = new Intent(mActivity, ReminderService.class);
	    PendingIntent pendingIntent = PendingIntent.getService(mActivity, intentId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	    alarmManager.cancel(pendingIntent);
	            
	    Log.i("Alarm usuniety", drukujCzas(cal));
        
	}
	
	public String drukujCzas(Calendar kalendarz) {
		
		String data=""+kalendarz.get(Calendar.DAY_OF_MONTH)+":"+(kalendarz.get(Calendar.MONTH)+1)+":"+kalendarz.get(Calendar.YEAR);
	    String time=""+kalendarz.get(Calendar.HOUR_OF_DAY)+":"+kalendarz.get(Calendar.MINUTE)+":"+kalendarz.get(Calendar.SECOND);
		
		return data + " - " + time;
	}
	
	public ArrayList<HashMap<String, String>> removePrzypomnienie(ArrayList<HashMap<String, String>> przypomnienia, String timeInMillis) {
	
		for (int i = 0; i < przypomnienia.size(); i++) {
			HashMap<String, String> przypomnienie = przypomnienia.get(i);
			String tempPrzyp = przypomnienie.get(PRZYP_DATE);
			if (tempPrzyp.equals(timeInMillis)) {
				przypomnienia.remove(i);
			}
		}
		
		return przypomnienia;
	}

	public boolean postData(String email, JSONArray json, String url) {
	    HttpClient httpclient = new DefaultHttpClient();

	    Log.i("post", "post");
	    try { 
	        HttpPost httppost = new HttpPost(url);

	        List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);    
	        nvp.add(new BasicNameValuePair("json", json.toString()));
	        nvp.add(new BasicNameValuePair("email", email)); 
	        httppost.setEntity(new UrlEncodedFormEntity(nvp));
	        HttpResponse response = httpclient.execute(httppost); 

	        if(response.getStatusLine().getStatusCode() == 200) {
	            return true;
	        } else {
	        	return false;
	        }

	    } catch (Exception e) {
	    	Log.i("utilities", "post data error");
	        return false;
	    } 
	}
	
	public void createThumb(String path) {
        try {
        	Bitmap b = BitmapFactory.decodeFile(path);
    		Bitmap out = Bitmap.createScaledBitmap(b, 50, 50, false);

            File file = new File(path + "thumb");
            FileOutputStream fOut = new FileOutputStream(file);
            out.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            b.recycle();
            out.recycle();

        } catch (Exception e) { // TODO
        	Log.i("utilities", "create thumb error");
        }
	}
	
	public void collapseView(final View v) {
	    final int initialHeight = v.getMeasuredHeight();

	    Animation a = new Animation()
	    {
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) {
	            if(interpolatedTime == 1){
	                v.setVisibility(View.GONE);
	            }else{
	                v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
	                v.requestLayout();
	            }
	        }

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };

	    // 1dp/ms
	    a.setDuration(150);
	   
	    v.startAnimation(a);

	}
	
	public void expandView(final View v, final int targetHeightInPx) {
		
	    v.getLayoutParams().height = 0;
	    v.setVisibility(View.VISIBLE);
	    Animation a = new Animation()
	    {
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) {
	        	if(interpolatedTime == 1){
	        		 v.getLayoutParams().height = targetHeightInPx;
	            }else{
	                v.getLayoutParams().height = (int)(targetHeightInPx * interpolatedTime);
	               
	            }
	        	v.requestLayout();
	        }

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };

	    // 1dp/ms
	    a.setDuration((int)(targetHeightInPx / v.getContext().getResources().getDisplayMetrics().density));
	    v.startAnimation(a);
	}
	
}
