package com.mareklatuszek.datywznosci.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import com.google.zxing.WriterException;
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
	
	public long parsePrzypmnienieToDate(String boxVal, String spinnerVal, String terminWaz, String notifHour) throws ParseException {
		if(boxVal.equals("")) {
			return 0;
		} else {
			int dateVal = Integer.parseInt(boxVal);
			int dateFormatVal = Integer.parseInt(spinnerVal);
			long endDate = parseDate(terminWaz);
			
			Calendar notifTime = Calendar.getInstance();
			notifTime.setTimeInMillis(endDate);
			
			if (dateFormatVal == 0) { //jesli dzien (kolejnosc z resource arrays "array_date" - trzyma� si� tej kolejnosci!)
				notifTime.add(Calendar.DAY_OF_YEAR, -dateVal);
			} else if (dateFormatVal == 1) { //jesli miesi�c
				notifTime.add(Calendar.MONTH, -dateVal);
			} else if (dateFormatVal == 2) { //jesli rok
				notifTime.add(Calendar.YEAR, -dateVal);
			}
			
			if (notifHour.equals("")) {
				notifTime.add(Calendar.HOUR_OF_DAY, 14);
			} else {
				int hour = Integer.parseInt(notifHour);// format to 0-23
				notifTime.add(Calendar.HOUR_OF_DAY, hour);
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
	
	public String dateToWords(long startTimeInMillis)
	{
		String time = "";
		long startTimeInSec = startTimeInMillis / 1000;
		long currentTimeInSec = (int) (System.currentTimeMillis() / 1000L);
		long difference = startTimeInSec - currentTimeInSec;
		
		if(difference < 3600) {
			time = "za godzin�";
			return time;
		} else if (difference >= 3600 & difference < 14400) {
			time = "za " + (difference/3600) + " godziny";
			return time;
		} else if (difference >= 14400 & difference < 75600) {
			time = "za " + (difference/3600) + " godzin";
			return time;
		} else if (difference >= 75600 & difference < 86400) {
			time = "za " + (difference/3600) + " godziny";
			return time;
		} else if (difference >= 86400 & difference < 172800) {
			time = "jutro";
			return time;
		} else if (difference >= 172800 & difference < 259200) {
			time = "pojutrze";
			return time;
		} else if (difference >= 259200 & difference < 604800) {
			time = "za " + (difference/86400) + " dni";
			return time;
		} else if (difference >= 604800 & difference < 1209600) {
			time = "za tydzień";
			return time;
		} else if (difference >= 1209600 & difference < 2419200) {
			time = "za " + (difference/604800) + " tygodnie";
			return time;
		} else if (difference >= 2419200 & difference < 4838400) {
			time = "za miesiąc";
			return time;
		} else if (difference >= 4838400 & difference < 9676800) {
			time = "za " + (difference/2419200) + " miesiące";
			return time;
		} else if (difference >= 9676800 & difference < 29030400) {
			time = "za " + (difference/2419200) + " miesięcy";
			return time;
		} else if (difference >= 29030400 & difference < 58060800) {
			time = "za rok";
			return time;
		} else if (difference >= 58060800 & difference < 145152000) {
			time = "za " + (difference/29030400) + " lata";
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
	
	@SuppressWarnings("deprecation")
	public void expandLinearLayout(final LinearLayout v) {
	    v.measure(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	    v.getLayoutParams().height = 0;
	    v.setVisibility(View.VISIBLE);
	    
	    Animation a = new Animation()
	    {
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) {
	        	int width = LinearLayout.LayoutParams.WRAP_CONTENT;
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
	    	//w razie niepowodzenia - domy�lna grafika
	    	Bitmap bitmap= BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.zxinglib_icon); 
	        return bitmap;
	    }	
	}
	
}
