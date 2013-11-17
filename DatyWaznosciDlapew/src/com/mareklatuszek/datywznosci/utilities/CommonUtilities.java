package com.mareklatuszek.datywznosci.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonUtilities {
	
	public int getProgress(String dataOtw, String terminWaz) {
		
		int progress = 100;
		
		try {
			
			double start = parseDate(dataOtw);
			double end = parseDate(terminWaz);
			double current = (new Date()).getTime();
			
			double x = end - start;
			double y = end - current;
			double z = y / x;
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
			
			if (dateFormatVal == 0) { //jesli dzien (kolejnosc z resource arrays "array_date" - trzymaæ siê tej kolejnosci!)
				notifTime.add(Calendar.DAY_OF_YEAR, -dateVal);
			} else if (dateFormatVal == 1) { //jesli miesi¹c
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
			time = "za godzinê";
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
			time = "za tydzieñ";
			return time;
		} else if (difference >= 1209600 & difference < 2419200) {
			time = "za " + (difference/604800) + " tygodni";
			return time;
		} else if (difference >= 2419200 & difference < 4838400) {
			time = "za miesi¹c";
			return time;
		} else if (difference >= 4838400 & difference < 9676800) {
			time = "za " + (difference/2419200) + " miesi¹ce";
			return time;
		} else if (difference >= 9676800 & difference < 29030400) {
			time = "za " + (difference/2419200) + " miesiêcy";
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

}
