package edu.cmu.carannotationv2;

import android.database.sqlite.SQLiteDatabase;

public class db_toSend_item_dataSource {

	private SQLiteDatabase database;
	private backsaveddata dbHelper;
	private String[] allColumns={dbHelper.COLUMN_ID,dbHelper.COLUMN_LOCATION_LATI,dbHelper.COLUMN_LOCATION_LONGTI,};
}
