package jp.co.entity.sosya;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class ScheduleDao {

	private static final String TABLE_NAME = "schedule";
	
	private static final String COLUMN_ID = "rowid";
	private static final String COLUMN_TRACKID = "trackid";
	private static final String COLUMN_DATE = "date";
	private static final String COLUMN_TIME = "time";
	private static final String COLUMN_LATITUDE = "latitudeE6";
	private static final String COLUMN_LONGITUDE = "longitudeE6";
	private static final String COLUMN_HOUR = "hour";
	private static final String COLUMN_MINUTE = "minute";	
	private static final String COLUMN_SECOND = "second";
	private static final String COLUMN_DISTANCE = "distance";
	private static final String COLUMN_SPEED = "speed";
	
	private static final String[] COLUMNS = {COLUMN_ID, COLUMN_TRACKID, 
		   COLUMN_DATE, COLUMN_TIME,COLUMN_LATITUDE ,COLUMN_LONGITUDE ,
		   COLUMN_HOUR ,COLUMN_MINUTE ,COLUMN_SECOND ,COLUMN_DISTANCE ,
		   COLUMN_SPEED};
	
	private SQLiteDatabase db;
	
	public ScheduleDao(SQLiteDatabase db) {
		this.db = db;
	}
	
	public void cleanup() {
		if (this.db != null) {
			this.db.close();
			this.db = null;
		}
	}
	
	public long insert(Schedule schedule) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_TRACKID, schedule.getTrackid());
		values.put(COLUMN_DATE, schedule.getDate());
		values.put(COLUMN_TIME, schedule.getTime());
		values.put(COLUMN_LATITUDE, schedule.getLatitudeE6());
		values.put(COLUMN_LONGITUDE, schedule.getLongitudeE6());
		values.put(COLUMN_HOUR, schedule.getHour());
		values.put(COLUMN_MINUTE, schedule.getMinute());
		values.put(COLUMN_SECOND, schedule.getSecond());
		values.put(COLUMN_DISTANCE, schedule.getDistance());
		values.put(COLUMN_SPEED, schedule.getSpeed());
		return db.insert(TABLE_NAME, null, values);
	}
	
	public int update(Schedule schedule) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_TRACKID, schedule.getTrackid());
		values.put(COLUMN_DATE, schedule.getDate());
		values.put(COLUMN_TIME, schedule.getTime());
		values.put(COLUMN_LATITUDE, schedule.getLatitudeE6());
		values.put(COLUMN_LONGITUDE, schedule.getLongitudeE6());
		values.put(COLUMN_HOUR, schedule.getHour());
		values.put(COLUMN_MINUTE, schedule.getMinute());
		values.put(COLUMN_SECOND, schedule.getSecond());	
		values.put(COLUMN_DISTANCE, schedule.getDistance());	
		values.put(COLUMN_SPEED, schedule.getSpeed());		
		String whereClause = "rowid = " + schedule.getRowid();
		return db.update(TABLE_NAME, values, whereClause, null);
	}
	

	
	public List<Schedule> selectAll() {
		List<Schedule> scheduleList = new ArrayList<Schedule>();
		Cursor cursor = db.query(TABLE_NAME,COLUMNS, null, null, null, null, COLUMN_DATE);
		while(cursor.moveToNext()) {
			Schedule schedule = new Schedule();
			schedule.setRowid(cursor.getInt(0));
			schedule.setTrackid(cursor.getInt(1));
			schedule.setDate(cursor.getString(2));
			schedule.setTime(cursor.getString(3));
			schedule.setLatitudeE6(cursor.getInt(4));
			schedule.setLongitudeE6(cursor.getInt(5));
			schedule.setHour(cursor.getInt(6));
			schedule.setMinute(cursor.getInt(7));
			schedule.setSecond(cursor.getInt(8));
			schedule.setDistance(cursor.getInt(9));
			schedule.setSpeed(cursor.getInt(10));
			scheduleList.add(schedule);
		
		}
		return scheduleList;
	}
	
	public Schedule selectById(int rowId) {
		String selection = "rowid = " + rowId;
		Cursor cursor = db.query(TABLE_NAME,COLUMNS, selection, null, null, null, null);
		while(cursor.moveToNext()) {
			Schedule schedule = new Schedule();
			schedule.setRowid(cursor.getInt(0));
			schedule.setTrackid(cursor.getInt(1));
			schedule.setDate(cursor.getString(2));
			schedule.setTime(cursor.getString(3));
			schedule.setLatitudeE6(cursor.getInt(4));
			schedule.setLongitudeE6(cursor.getInt(5));
			schedule.setHour(cursor.getInt(6));
			schedule.setMinute(cursor.getInt(7));
			schedule.setSecond(cursor.getInt(8));
			schedule.setDistance(cursor.getInt(9));
			schedule.setSpeed(cursor.getInt(10));
			return schedule;
		}
		return null;
	}
	
	public int delete(int rowId) {
		return db.delete(TABLE_NAME, "rowid = " + rowId, null);
	}
	
}
