package jp.co.entity.sosya;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	//private static final String DATABASE_NAME = "sosyadata5";
	private static final String DATABASE_NAME = "sosyadata";
	private static final int DATABASE_VERSION = 1;

	private static final String CREATE_SCHEDULE_TABLE_SQL = "create table schedule "
			+ "(rowid integer primary key autoincrement, "
			+ "trackid integer default 0, "
			+ "date text not null, "
			+ "time text not null, "
			+ "latitudeE6 integer default 0, "  
			+ "longitudeE6 integer default 0, " 
			+ "hour integer default 0, "
			+ "minute integer default 0, "
			+ "second integer default 0, "
			+ "distance integer default 0, "
			+ "speed integer default 0);" ;

	private static final String DROP_SCHEDULE_TABLE_SQL = "drop table if exists schedule";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_SCHEDULE_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_SCHEDULE_TABLE_SQL);
		onCreate(db);
	}
}
