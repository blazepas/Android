package pl.jacek.jablonka.android.tpp;

import pl.jacek.jablonka.android.tpp.utilities.FinalVariables;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HelperDB extends SQLiteOpenHelper implements FinalVariables {
    public HelperDB(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE_PRODUCT_TABLE);
        db.execSQL(DB_CREATE_CATEGORIES_TABLE);
 
        Log.d(DB_DEBUG_TAG, "Database creating...");
        Log.d(DB_DEBUG_TAG, "Table " + DB_PRODUCT_TABLE + " and " + DB_CATEGORIES_TABLE + " ver." + DB_VERSION + " created");
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_PRODUCT_TABLE); //TODO zrobi� przepisywanie do nowej tabeli
        db.execSQL(DROP_CATEGORIES_TABLE);
 
        Log.d(DB_DEBUG_TAG, "Database updating...");
        Log.d(DB_DEBUG_TAG, "Table " + DB_PRODUCT_TABLE + " and " + DB_CATEGORIES_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
        Log.d(DB_DEBUG_TAG, "All data is lost.");
 
        onCreate(db);
    }

	
}
