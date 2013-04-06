package jp.co.entity.sosya;

public class Schedule {
	private int rowid;
	private String date;
	private String time;
	private int latitudeE6;
	private int longitudeE6;
	private int hour ;
   	private int minute ;
	private int second ;
	private int distance ;
	private int trackid ;
	private int speed ;
	
	public int getRowid() {
		return rowid;
	}
	
	public String getStrRowid() {
		
		return Integer.toString(rowid);
	}

	public void setRowid(int rowid) {
		this.rowid = rowid;
	}
	
	public int getTrackid() {
		return trackid;
	}

	public String getStrTrackid() {
		return Integer.toString(trackid);
	}
	
	public void setTrackid(int trackid) {
		this.trackid = trackid;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getLatitudeE6() {
		return latitudeE6;
	}
	
	public String getStrLatitude() {
		double lat = (double) latitudeE6 / 1E6 ;
		return Double.toString(lat);
	}

	public void setLatitudeE6(int latitudeE6) {
		this.latitudeE6 = latitudeE6;
	}
	

	public int getLongitudeE6() {
		return longitudeE6;
	}
	
	public String getStrLongitude() {
		double lng = (double) longitudeE6 / 1E6 ;
		return Double.toString(lng);	
	}
		
	public void setLongitudeE6(int longitudeE6) {
		this.longitudeE6 = longitudeE6;
	}
	
	public int getHour() {
		return hour;	
    }
	
	public String getStrHour() {
		return Integer.toString(hour);	
    }

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}
	
	public String getStrMinute() {
		return Integer.toString(minute);
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}
	
	public int getSecond() {
		return second;
	}
	
	public String getStrSecond() {
		return Integer.toString(second);
	}
	
	public void setSecond(int second) {
		this.second = second;
	}
	
	public int getDistance() {
		return distance;
	}
	
	public String getStrDistance() {
		return Integer.toString(distance);
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public int getSpeed() {
	    return speed ;
	}
	
	public String getStrSpeed() {
	    return Integer.toString(speed) ;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}


}
