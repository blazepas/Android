package com.mareklatuszek.datywaznosci;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.mareklatuszek.datywznosci.utilities.FinalVariables;

@SuppressWarnings("serial")
public class Product implements FinalVariables, Serializable {
	
	private String nazwa;
	private String dataOtwarcia;
	private String okresWaznosci;
	private String terminWaznosci;
	private String kategoria;
	private String code;
	private String codeFormat;
	private String image;
	private String opis;
	private ArrayList<HashMap<String, String>> przypomnienia;
	
	public Product() {		
		this.nazwa = "";
		this.dataOtwarcia = "";
		this.okresWaznosci = "0";
		this.terminWaznosci = "";
		this.kategoria = "";
		this.code = "";
		this.codeFormat = "";
		this.image = "";
		this.opis = "";
		this.przypomnienia = new ArrayList<HashMap<String,String>>();
	}
	
	public String getNazwa() {
		return nazwa;
	}
	
	public String getDataOtwarcia() {
		return dataOtwarcia;
	}

	public String getOkresWaznosci() {
		return okresWaznosci;
	}

	public String getTerminWaznosci() {
		return terminWaznosci;
	}

	public String getKategoria() {
		return kategoria;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getCodeFormat() {
		return codeFormat;
	}

	public String getImage() {
		return image;
	}
	
	public String getOpis() {
		return opis;
	}
	
	public ArrayList<HashMap<String, String>> getPrzypomnienia() {
		return przypomnienia;
	}
	
	public String getPrzypomnieniaToDB() {
		
		JSONArray jArr = new JSONArray();
		String przypomnieniaJson = "";
		
		try {
			
			for (int i = 0; i < przypomnienia.size(); i++) {
				JSONObject jObj = new JSONObject();
				HashMap<String, String> przypomnienie = przypomnienia.get(i);
				
				Log.i("przyp to json", przypomnienie+"");
				
				String textBox = przypomnienie.get(PRZYP_TEXT_BOX);
				String spinner = przypomnienie.get(PRZYP_SPINNER);
				String date = przypomnienie.get(PRZYP_DATE);
				String hour = przypomnienie.get(PRZYP_HOUR);
				
				jObj.put(PRZYP_TEXT_BOX, textBox);
				jObj.put(PRZYP_SPINNER, spinner);
				jObj.put(PRZYP_DATE, date);
				jObj.put(PRZYP_HOUR, hour);
				
				jArr.put(jObj);
			}
		
		} catch (JSONException e) {
			Log.i("przyp to json", "get err");
		} finally {
			przypomnieniaJson = jArr.toString();
		}
		
		return przypomnieniaJson;
	}
		
//	public String getGrupa(int rodzajGrupy) {
//		
//		switch (rodzajGrupy) {
//		case 0: // wg nazwy
//			return getPierwszaLitera();
//		case 1: // wg daty otwarcia
//			return getGrupaDataOtwarcia();
//		}
//		
//		return "0";
//		
//	}
//	
//	private String getPierwszaLitera() {
//		
//		if (nazwa.length() > 1) {
//			return nazwa.substring(0, 1);
//		} else {
//			return nazwa;
//		}
//		
//	}
//	
//	private String getGrupaDataOtwarcia() {
//		
//		return "";
//		
//	}
	
	
	public void setNazwa(String nazwa) {	
		this.nazwa = nazwa;
	}
	
	public void setDataOtwarcia(String dataOtwarcia) {
		this.dataOtwarcia = dataOtwarcia;
	}

	public void setOkresWaznosci(String okresWaznosci) {
		this.okresWaznosci = okresWaznosci;
	}

	public void setTerminWaznosci(String terminWaznosci) {
		this.terminWaznosci = terminWaznosci;
	}

	public void setKategoria(String kategoria) {
		this.kategoria = kategoria;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public void setCodeFormat(String codeFormat) {
		this.codeFormat = codeFormat;
	}
	
	public void setImage(String image) {
		this.image = image;
	}
	
	public void setOpis(String opis) {
		this.opis = opis;
	}
	
	public void setPrzypomnienia(ArrayList<HashMap<String, String>> przypomnienia) {
		this.przypomnienia = przypomnienia;
	}
	
	public void setPrzypomnieniaFromDB(String przypomnieniaJson) {
		ArrayList<HashMap<String, String>> przypomnienia = new ArrayList<HashMap<String,String>>();
		
		try {
			JSONArray jArr = new JSONArray(przypomnieniaJson);
			
			for (int i = 0; i < jArr.length(); i++) {
				JSONObject jObj = jArr.getJSONObject(i);

				HashMap<String, String> przypomnienie = new HashMap<String, String>();
				
				String textBox = jObj.getString(PRZYP_TEXT_BOX);
				String spinner = jObj.getString(PRZYP_SPINNER);
				String date = jObj.getString(PRZYP_DATE);
				String hour = jObj.getString(PRZYP_HOUR);
				
				przypomnienie.put(PRZYP_TEXT_BOX, textBox);
				przypomnienie.put(PRZYP_SPINNER, spinner);
				przypomnienie.put(PRZYP_DATE, date);
				przypomnienie.put(PRZYP_HOUR, hour);
				
				przypomnienia.add(przypomnienie);
			}
			
			Log.i("json to przyp", przypomnienia+"");
			
		} catch (JSONException e) {
			Log.i("json to przyp", "set err");
		}
		
		
		this.przypomnienia = przypomnienia;
	}


}
