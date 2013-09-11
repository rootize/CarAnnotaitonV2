package edu.cmu.carannotationv2;

public class database {

	
	public static final String DATABASE_TABLE = "makemodel";
	public static final String DATABASE_NAME = "CAR.db";
	public static final String KEY_ID = "_id";
	public static final String CAR_MAKE = "make";
	public static final String CAR_MODEL = "model";
	public static final String INSERT_ITEM_PREFIX = "INSERT INTO "
			+ DATABASE_TABLE + "( " + CAR_MAKE + " , " + CAR_MODEL + " )"
			+ " VALUES (";
	public static final String INSERT_ITEM_SUFFIX = " );";
	public static final String CREATE_TABLE1 = "DROP TABLE IF EXISTS "
			+ DATABASE_TABLE;
	public static final String CREATE_TABLE2 = "CREATE TABLE "
			+ DATABASE_TABLE + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + CAR_MAKE
			+ " TEXT NOT NULL , " + CAR_MODEL + " TEXT NOT NULL )";
	public static final String SELECT_MAKE = "SELECT DISTINCT " + CAR_MAKE
			+ " from " + DATABASE_TABLE;
	public static final String SELECT_MODEL_ALL = "SELECT DISTINCT "
			+ CAR_MODEL + " from " + DATABASE_TABLE;
	public static final String SELECT_ONE_MODEL_PREFIX = SELECT_MODEL_ALL
			+ " where " + CAR_MAKE + " = '";
	public static final String SELECT_ONE_MODEL_SUFFIX = "';";
	public static final String NONE_EXISTING = " I dont know this car";
	public static final String BLANK_FISRT_ITEM = "";

}
