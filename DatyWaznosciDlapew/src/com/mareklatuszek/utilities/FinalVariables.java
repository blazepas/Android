package com.mareklatuszek.utilities;

public interface FinalVariables {
	
	public static final String PREMIUM_APP_URI = "com.mareklatuszek.datywaznosci.premium";
	public static final String IMAGE_DIRECTORY_NAME = "TPP";

	public static final int DB_VERSION = 4;
	public static final String DB_DEBUG_TAG = "Product database"; 
	public static final String DB_NAME = "database.db";
	public static final String DB_PRODUCT_TABLE = "product";
	public static final String DB_CATEGORIES_TABLE = "categories";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final String CELL_DEFAULT = "TEXT";

    public static final String KEY_ID = "_id";
	public static final String DB_NAZWA = "nazwa";
	public static final String DB_DATA_OTWARCIA = "dataOtw";
	public static final String DB_OKRES_WAZNOSCI = "okresWaz";
	public static final String DB_TERMIN_WAZNOSCI = "terminWaz";
	public static final String DB_KATEGORIA = "kategoria";
	public static final String DB_CODE = "code";
	public static final String DB_CODE_FORMAT = "codeFormat";
	public static final String DB_OBRAZEK = "obrazek";
	public static final String DB_OPIS = "opis";
	public static final String DB_PRZYPOMNIENIA = "przypomnienia";
	public static final String DB_DATA_ZUZYCIA = "dataZuz";
	public static final String DB_END_DATE = "endDate";
	public static final String DB_IS_SCANNED = "isScanned";
	
	public static final String DB_CAT_CATEGORY = "cat_category";
	
	public static final String DROP_PRODUCT_TABLE = "DROP TABLE IF EXISTS " + DB_PRODUCT_TABLE;
	public static final String DROP_CATEGORIES_TABLE = "DROP TABLE IF EXISTS " + DB_CATEGORIES_TABLE;
	public static final String DB_CREATE_PRODUCT_TABLE = "CREATE TABLE " + DB_PRODUCT_TABLE + "( " +
            KEY_ID + " " + ID_OPTIONS + ", " +
            DB_NAZWA + " " + CELL_DEFAULT + ", " +
            DB_DATA_OTWARCIA + " " + CELL_DEFAULT + ", " +
            DB_OKRES_WAZNOSCI + " " + CELL_DEFAULT + ", " +
            DB_TERMIN_WAZNOSCI + " " + CELL_DEFAULT + ", " +
            DB_KATEGORIA + " " + CELL_DEFAULT + ", " +
            DB_CODE + " " + CELL_DEFAULT + ", " +
            DB_CODE_FORMAT + " " + CELL_DEFAULT + ", " +
            DB_OBRAZEK + " " + CELL_DEFAULT + ", " +
            DB_OPIS + " " + CELL_DEFAULT + ", " +
            DB_PRZYPOMNIENIA + " " + CELL_DEFAULT + ", " +
            DB_IS_SCANNED + " " + CELL_DEFAULT + ", " +
            DB_DATA_ZUZYCIA + " " + CELL_DEFAULT + ", " +
            DB_END_DATE + " " + CELL_DEFAULT +
            ");";
	
	public static final String DB_CREATE_CATEGORIES_TABLE = "CREATE TABLE " + DB_CATEGORIES_TABLE + "( " +
			 KEY_ID + " " + ID_OPTIONS + ", " +
			 DB_CAT_CATEGORY + " " + CELL_DEFAULT + ");";	
	
	public static final String PRZYP_TEXT_BOX = "przypTextBox";
	public static final String PRZYP_SPINNER = "przypSpinner";
	public static final String PRZYP_DATE = "przypDate";
	public static final String PRZYP_HOUR = "przypHour";
	
	//wartości z array_date w array resources
	public static final String SPINNER_DATE_DAY = "Dzień"; 
	public static final String SPINNER_DATE_WEEK = "Tydzień"; 
	public static final String SPINNER_DATE_MONTH = "Miesiąc"; 
	public static final String SPINNER_DATE_YEAR = "Rok"; 
	
	public static final String SPINNER_OKRES = "wybierz termin";
	public static final String SPINNER_KATEGORIE = "wybierz kategorię"; 
	public static final String SPINNER_PRZPOMNIENIE = "wybierz termin";
	
	public static final int CAMERA_ADD_RQ_CODE = 100;
	public static final int CAMERA_EDIT_RQ_CODE = 200;
	public static final int GALLERY_ADD_RQ_CODE = 300;
	public static final int GALLERY_EDIT_RQ_CODE = 400;
		
	public static final String INFO_OKRES = "Czas przydatności produktu do użycia/spożycia po otwarciu produktu"
			+ " (data podawana na niektórych opakowaniach) np. 2 miesiące"; 
	public static final String INFO_TERMIN_WAZNOSCI = "Po tym terminie produkt nie nadaje się do użycia/spożycia"
			+ " jest przeterminowany (data nadrukowana na opakowaniu)."; 
	public static final String INFO_DATA_ZUZYCIA = "Po tym terminie produkt nie nadaje się do użycia/spożycia"
			+ " jest przeterminowany (data nadrukowana na opakowaniu)."; 
	public static final String INFO_SKANOWANIE = "Po tym terminie produkt nie nadaje się do użycia/spożycia"
			+ " jest przeterminowany (data nadrukowana na opakowaniu)."; 
	public static final String INFO_WLASNY_PRODUKT = "Po tym terminie produkt nie nadaje się do użycia/spożycia"
			+ " jest przeterminowany (data nadrukowana na opakowaniu)."; 
	
	public static final int INFO_MENU_0_ITEM_ID = 100;
	public static final int INFO_MENU_1_ITEM_ID = 200;
	
	public static final int SWIPE_MIN_DISTANCE = 50;
	public static final int SWIPE_MAX_OFF_PATH = 250;
	public static final int SWIPE_THRESHOLD_VELOCITY = 200;

}
