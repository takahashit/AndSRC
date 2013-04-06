package jp.co.entity.sosya;



import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import com.google.gson.GsonBuilder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class GPSSend {
    private static Handler mHandler = new Handler(Looper.getMainLooper());     
    /*
     * 位置情報更新
     */
	protected static void sendReal(final Location location,final Context mycontext) {

        if (location == null)
            return;

        double lat = location.getLatitude();
        double lon = location.getLongitude();

        // 送信先サーバのURLの設定
        String serverUrl = AppPreferences.getServerUrl(mycontext);
        if (serverUrl.length() == 0)
            return;

        // ユーザIDの設定
        String loginId = AppPreferences.getLoginId(mycontext);

        // URLを設定
        String postUrl = "http://" + serverUrl + "/api";

        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                HttpVersion.HTTP_1_1);

        // タイムアウトを設定
        HttpConnectionParams.setConnectionTimeout(params, 5000);
        HttpConnectionParams.setSoTimeout(params, 3000);

        // POSTメソッド設定
        HttpClient httpclient = new DefaultHttpClient(params);
        HttpPost httppost = new HttpPost(postUrl);

        try {
            // 送信するパラメータを設定
           List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

            nameValuePairs.add(new BasicNameValuePair("loginid", loginId));
            nameValuePairs.add(new BasicNameValuePair("mode", "real"));
            nameValuePairs.add(new BasicNameValuePair("latitude", Double
                    .toString(lat)));
            nameValuePairs.add(new BasicNameValuePair("longitude", Double
                    .toString(lon)));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
                    HTTP.UTF_8));
            HttpResponse response = httpclient.execute(httppost);
            response.getEntity();
        } catch (SocketTimeoutException e) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {  
                    Toast toast = Toast.makeText(mycontext,
                            R.string.cannot_connect_server,
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            });

        } catch (IOException e) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(mycontext,
                            R.string.io_error, Toast.LENGTH_LONG);
                    toast.show();
                }
            });
      
            

        } finally {

        }

    }
	
	
	protected static <Gson> void sendTracking(final Context mycontext) {
        // 送信先サーバのURLの設定
        String serverUrl = AppPreferences.getServerUrl(mycontext);
        if (serverUrl.length() == 0)
            return;

        DatabaseHelper dbHelper = new DatabaseHelper(mycontext);
    	  SQLiteDatabase db = dbHelper.getWritableDatabase();
    	  ScheduleDao scheduleDao = new ScheduleDao(db); 
    	  List<Schedule> schedulelist = scheduleDao.selectAll();
  	    db.close();  
  	    
      ArrayList<Map<String, String>> sendLine = new ArrayList<Map<String,String>> ();
      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
    
        // ユーザIDの設定
       String loginId = AppPreferences.getLoginId(mycontext);

        // URLを設定
        String postUrl = "http://" + serverUrl + "/api";
        
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                HttpVersion.HTTP_1_1);

        // タイムアウトを設定
        HttpConnectionParams.setConnectionTimeout(params, 5000);
        HttpConnectionParams.setSoTimeout(params, 3000);

        // POSTメソッド設定
        HttpClient httpclient = new DefaultHttpClient(params);
        HttpPost httppost = new HttpPost(postUrl);
        

//	   int done = 0 ; 
	   int empty = 0 ;
	   
	   
        Map<String,String> data;
	  
	   for(Schedule schedule:schedulelist) {
	       if(schedule == null)continue ;
	       data = new HashMap<String,String>();
	       data.put("rowid", schedule.getStrRowid());
	       data.put("latitude", schedule.getStrLatitude());
	       data.put("longitude",schedule.getStrLongitude());
	       data.put("hour",schedule.getStrHour());
	       data.put("minute",schedule.getStrMinute());
	       data.put("second",schedule.getStrSecond());
	       data.put("distance",schedule.getStrDistance());
	       data.put("trackid",schedule.getStrTrackid());
	       data.put("speed",schedule.getStrSpeed());
	       sendLine.add(data) ;
	       empty++ ;
	   }
	   
	   if(empty == 0) {
		   Toast.makeText(mycontext,"送信データがありません。",Toast.LENGTH_SHORT).show();
	   }else{  
         nameValuePairs.add(new BasicNameValuePair("loginid", loginId));
         nameValuePairs.add(new BasicNameValuePair("mode", "track"));
         // jsonデータを生成する
         GsonBuilder gsonBuilder = new GsonBuilder();
         gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
         com.google.gson.Gson gson = gsonBuilder.create();
         String jsonstr = ((com.google.gson.Gson) gson).toJson(sendLine);
         nameValuePairs.add(new BasicNameValuePair("jsonString",jsonstr.toString()));     
         try {
             // 送信するパラメータを設定
             httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
                     HTTP.UTF_8));
             HttpResponse response = httpclient.execute(httppost);
             response.getEntity();
         } catch (SocketTimeoutException e) {
             mHandler.post(new Runnable() {
                 @Override
                 public void run() {  
                     Toast toast = Toast.makeText(mycontext,
                             R.string.cannot_connect_server,
                             Toast.LENGTH_LONG);
                     toast.show();
                 }
             });

         } catch (IOException e) {
             mHandler.post(new Runnable() {
                 @Override
                 public void run() {
                     Toast toast = Toast.makeText(mycontext,
                             R.string.io_error, Toast.LENGTH_LONG);
                     toast.show();
                 }
             });

         } finally {
        	 Toast.makeText(mycontext,"データを送信しました。",Toast.LENGTH_LONG).show(); 
         }
         
	   }
} 

	/**
     * 日時の文字列をDate型に変換する
     */
    private Date parseDatetime(String dateTime, String tz) {

        Date parsed = null;
        try {
            String timezone = "UTC";
            if (tz != null)
                timezone = tz;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d-kk:mm");
            sdf.setTimeZone(TimeZone.getTimeZone(timezone));
            parsed = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsed;
    }

}