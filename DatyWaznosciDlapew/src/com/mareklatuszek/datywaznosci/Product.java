package com.mareklatuszek.datywaznosci;

import java.util.ArrayList;
import java.util.HashMap;

import com.mareklatuszek.datywznosci.utilities.FinalVariables;

public class Product implements FinalVariables {
	
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


}
