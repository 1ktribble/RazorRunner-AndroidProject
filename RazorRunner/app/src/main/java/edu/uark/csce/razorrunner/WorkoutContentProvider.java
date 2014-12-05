package edu.uark.csce.razorrunner;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class WorkoutContentProvider extends ContentProvider{

	public static final String PROVIDER_NAME = "edu.uark.csce.mobile.workoutprovider";
	public static final String URL = "content://" + PROVIDER_NAME + "/workouts";
	public static final Uri CONTENT_URI = Uri.parse(URL);
	
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "_workoutName";
	public static final String KEY_DATE = "_date";
	public static final String KEY_DISTANCE = "_distance";
	public static final String KEY_STEPS = "_steps";
	public static final String KEY_DURATION = "_time";
    public static final String KEY_LAT_A = "_latitudeA";
    public static final String KEY_LONG_A = "_longitudeA";
    public static final String KEY_LAT_B = "_latitudeB";
    public static final String KEY_LONG_B = "_longitudeB";

	private static HashMap<String, String> WorkoutMap;
	
	private MySQLiteOpenHelper myOpenHelper;
	private static final int ALLROWS = 1;
	private static final int SINGLEROW = 2;
	
	private static final UriMatcher myUriMatcher;
	static {
		myUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		myUriMatcher.addURI(PROVIDER_NAME, "workouts", ALLROWS);
		myUriMatcher.addURI(PROVIDER_NAME, "workouts/#", SINGLEROW);
	}
	
	public WorkoutContentProvider() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		myOpenHelper = new MySQLiteOpenHelper(getContext(), 
				MySQLiteOpenHelper.DATABASE_NAME, 
				null, 
				MySQLiteOpenHelper.DATABASE_VERSION);
		return true;
	}

	@SuppressWarnings("static-access")
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteDatabase db;
		try{
			db = myOpenHelper.getWritableDatabase();
		} catch(SQLiteException ex) {
			db = myOpenHelper.getReadableDatabase();
		}
		
		String groupBy = null;
		String having = null;
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		switch(myUriMatcher.match(uri)) {
			case SINGLEROW:
				String rowID = uri.getPathSegments().get(1);
				queryBuilder.appendWhere(KEY_ID + "=" + rowID);
				break;
			case ALLROWS:
				queryBuilder.setProjectionMap(WorkoutMap);
			default:
				break;
		}
		
		queryBuilder.setTables(myOpenHelper.DATABASE_TABLE);

		Cursor cursor = queryBuilder.query(db, projection, selection, 
				selectionArgs, groupBy, having, sortOrder);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch(myUriMatcher.match(uri)){
		case ALLROWS:
			return "vnd.android.cursor.dir/vnd.uark.workouts";
		case SINGLEROW:
			return "vnd.android.cursor.item/vnd.uark.workouts";
		default:
			throw new IllegalArgumentException("Unsupported URI: "+ uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = myOpenHelper.getWritableDatabase();
		
		String nullColHack = null;
		
		@SuppressWarnings("static-access")
		long id = db.insert(myOpenHelper.DATABASE_TABLE, 
				nullColHack, values);
		
		if(id > -1)
		{
			Uri insertedId = ContentUris.withAppendedId(CONTENT_URI, id);
			getContext().getContentResolver().notifyChange(insertedId, null);
			return insertedId;
		}
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = myOpenHelper.getWritableDatabase();
		switch (myUriMatcher.match(uri)) {
			case SINGLEROW:
				String rowID = uri.getPathSegments().get(1);
				selection = KEY_ID + "=" + rowID
						+ (!TextUtils.isEmpty(selection) ?
						" AND (" + selection + ')' : "");
			default:
				break;
		}

		if (selection == null) {
			selection = "1";
		}
		
		int deleteCount = db.delete(MySQLiteOpenHelper.DATABASE_TABLE, 
				selection, selectionArgs);
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return deleteCount;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = myOpenHelper.getWritableDatabase();
		switch (myUriMatcher.match(uri)) {
			case SINGLEROW:
				String rowID = uri.getPathSegments().get(1);
				selection = KEY_ID + "=" + rowID;
				if (!TextUtils.isEmpty(selection)) {
					String appendString = " and (" + selection + ')';
					selection += appendString;
				}
			default:
				break;
		}
		
		int updateCount = db.update(MySQLiteOpenHelper.DATABASE_TABLE, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		
		return updateCount;
	}

	public static class MySQLiteOpenHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "workoutDatabase.db";
		private static final int DATABASE_VERSION = 1;
		private static final String DATABASE_TABLE = "workoutTable";
		
		private static final String DATABASE_CREATE_CMD =
				"create table "+ DATABASE_TABLE + " (" + KEY_ID +
				" integer primary key autoincrement, " + 
				KEY_NAME + " text not null, " +
				KEY_DATE + " int, " +
                KEY_LAT_A + " text, " +
                KEY_LONG_A + " text, " +
                KEY_LAT_B + " text, " +
                KEY_LONG_B + " text, " +
				KEY_DISTANCE + " text, " +
				KEY_STEPS + " int, " +
				KEY_DURATION + " long);"
		;		
		private static final String DATABASE_DROP_CMD = 
				"drop table if it exists " + DATABASE_TABLE;
		
		private static final String DATABASE_SORT_RECORDS = 
				"SELECT * FROM Table ORDER BY date(" + KEY_DATE + ") DESC Limit 1";
		
		public MySQLiteOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(DATABASE_CREATE_CMD);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.w("TODOPROVIDER", "Upgrading from version " + oldVersion +
					" to " + newVersion + ". All data will be deleted."
					);
			db.execSQL(DATABASE_DROP_CMD);
			db.execSQL(DATABASE_CREATE_CMD);
			db.execSQL(DATABASE_SORT_RECORDS);
		}
		
	}
}
