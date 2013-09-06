package edu.cmu.carannotationv2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class backsaveddata extends SQLiteOpenHelper {

	public static final String DATA_BASE_NAME="to_send.db";
	public static final int    DATABASE_VERISON=1;
	public static final String table_to_upload="toupload";
	public static final String COLUMN_ID="_id";
	public static final String COLUMN_LOCATION_LATI="Location_Lati";
	public static final String COLUMN_LOCATION_LONGTI="Location_Longti";
	public static final String RECT_BOTTOM0="Rect_Bottom_0";
	public static final String RECT_BOTTOM1="Rect_Bottom_1";
	public static final String RECT_BOTTOM2="Rect_Bottom_2";
	public static final String RECT_BOTTOM3="Rect_Bottom_3";
	public static final String RECT_BOTTOM4="Rect_Bottom_4";
	public static final String RECT_LEFT0="Rect_Left_0";
	public static final String RECT_LEFT1="Rect_Left_1";
	public static final String RECT_LEFT2="Rect_Left_2";
	public static final String RECT_LEFT3="Rect_Left_3";
	public static final String RECT_LEFT4="Rect_Left_4";
	public static final String RECT_TOP0="Rect_Top_0";
	public static final String RECT_TOP1="Rect_Top_1";
	public static final String RECT_TOP2="Rect_Top_2";
	public static final String RECT_TOP3="Rect_Top_3";
	public static final String RECT_TOP4="Rect_Top_4";
	public static final String RECT_RIGHT0="Rect_Right_0";
	public static final String RECT_RIGHT1="Rect_Right_1";
	public static final String RECT_RIGHT2="Rect_Right_2";
	public static final String RECT_RIGHT3="Rect_Right_3";
	public static final String RECT_RIGHT4="Rect_Right_4";
	public static final String MAKE0="make_0";
	public static final String MAKE1="make_1";
	public static final String MAKE2="make_2";
	public static final String MAKE3="make_3";
	public static final String MAKE4="make_4";
	public static final String MODEL0="model_0";
	public static final String MODEL1="model_1";
	public static final String MODEL2="model_2";
	public static final String MODEL3="model_3";
	public static final String MODEL4="model_4";
	public static final String USR="usr";
	public static final String ABS_PATH="path";
	public static final String IMG_NAME="imgName";
	public static final String FLASH="flash";
	public static final String EXPOURE="exposuretime";
	public static final String IMAGE_MAKE="imagemake";
	public static final String IMAGE_MODEL="imagemodel";
	public static final String WHITEBALANCE="whitebalance";

	
	public static final String CREATE_TABLE="create table if not exists " +
        table_to_upload + " ("+ COLUMN_ID+ " integer primary key autoincrement,"
                + COLUMN_LOCATION_LATI + " ,"
                + COLUMN_LOCATION_LONGTI + " ,"
                + RECT_BOTTOM0 + " ,"
                + RECT_BOTTOM1 + " ," 
                + RECT_BOTTOM2 + " ,"
                + RECT_BOTTOM3 + " ,"
                + RECT_BOTTOM4 + " ,"
                + RECT_LEFT0   + " ,"
                + RECT_LEFT1   + " ,"
                + RECT_LEFT2   + " ,"
                + RECT_LEFT3   + " ,"
                + RECT_LEFT4   + " ,"
                + RECT_TOP0    + " ,"
                + RECT_TOP1    + " ,"
                + RECT_TOP2    + " ,"
                + RECT_TOP3    + " ,"
                + RECT_TOP4    + " ,"
                + RECT_RIGHT0  + " ,"
                + RECT_RIGHT1  + " ,"
                + RECT_RIGHT2  + " ,"
                + RECT_RIGHT3  + " ,"
                + RECT_RIGHT4  + " ,"
                + MAKE0        + " ," 
                + MAKE1        + " ," 
                + MAKE2        + " ," 
                + MAKE3        + " ," 
                + MAKE4        + " ," 
                + MODEL0       + " ," 
                + MODEL1       + " ," 
                + MODEL2       + " ," 
                + MODEL3       + " ," 
                + MODEL4       + " ," 
                + USR          + " ,"
                + ABS_PATH     + " ," 
	            + IMG_NAME     + " ,"
	            + FLASH        + " ,"
	            + EXPOURE      + " ,"
	            + IMAGE_MAKE   + " ,"
	            + IMAGE_MODEL  + " ,"
	            + WHITEBALANCE + " ,"
                		+" )";
	public backsaveddata(Context context) {
		super(context, DATA_BASE_NAME,null, DATABASE_VERISON );
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		
	}

}
