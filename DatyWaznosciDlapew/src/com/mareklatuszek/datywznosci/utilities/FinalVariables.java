package com.mareklatuszek.datywznosci.utilities;

public interface FinalVariables {

	public static final int DB_VERSION = 1;
	public static final String DB_DEBUG_TAG = "Product database"; 
	public static final String DB_NAME = "database.db";
	public static final String DB_PRODUCT_TABLE = "product";
	public static final String DB_CATEGORIES_TABLE = "categories";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final String CELL_DEFAULT = "TEXT DEFAULT 0";

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
            DB_OPIS + " " + CELL_DEFAULT + 
            ");";
	
	public static final String DB_CREATE_CATEGORIES_TABLE = "CREATE TABLE " + DB_CATEGORIES_TABLE + "( " +
			 KEY_ID + " " + ID_OPTIONS + ", " +
			 DB_NAZWA + " " + CELL_DEFAULT + ");";
	
	
	public static final String PRZYP_TEXT_BOX = "przypTextBox";
	public static final String PRZYP_SPINNER = "przypSpinner";

}
