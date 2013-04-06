package jp.co.entity.sosya;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.google.android.maps.GeoPoint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import jp.co.entity.sosya.SosyaActivity;
import jp.co.entity.sosya.R;
import jp.co.entity.sosya.AppPreferences;
import jp.co.entity.sosya.GPSRecord;

public class GPSReceive {
	
	protected static GeoPoint posReal(final Location myPos ,final Context mycontext){ // from
		
        String serverUrl = AppPreferences.getServerUrl(mycontext);
        String loginId = AppPreferences.getLoginId(mycontext);
        String posRadiusStr = AppPreferences.getReceiveRadius(mycontext);

        double lat = myPos.getLatitude();
        double lon = myPos.getLongitude();
        String latStr = Double.toString(lat);
        String logStr = Double.toString(lon);       
       // debug 
		 int latitudeE6 = (int) (36.56 * 1E6) ;
		 int longitudeE6 = (int) (136.662502 * 1E6);  
        GeoPoint gp = new GeoPoint(latitudeE6, longitudeE6);

        try {
            String locationFeed = "http://" + serverUrl + "/api?loginid="
                    + loginId + "&mode=pos" + "&posRadius=" + posRadiusStr
                    + "latitude=" + latStr + "longitude=" + logStr ;
            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 10 * 1000); // 接続のタイムアウト
            HttpConnectionParams.setSoTimeout(params, 2 * 1000); // データ取得のタイムアウト
            HttpClient objHttp = new DefaultHttpClient(params);

            HttpGet objGet = new HttpGet(locationFeed);
            HttpResponse objResponse = objHttp.execute(objGet);

            if (objResponse.getStatusLine().getStatusCode() == 200) {
                // 正常取得
                InputStream objStream = objResponse.getEntity()
                        .getContent();
                String jsonString = convertStreamToString(objStream);
           }
        } catch (SocketTimeoutException e) {
          // mHandler.post(new Runnable() {
        //	   public void run() {
                   Toast.makeText(mycontext,
                            R.string.cannot_connect_server,
                            Toast.LENGTH_LONG).show();
         //       }
         //   });
        } catch (IOException e) {
         //  mHandler.post(new Runnable() {
          //      public void run() {
                    Toast.makeText(mycontext, R.string.io_error,
                            Toast.LENGTH_LONG).show();
         //       }
         //   });
        } finally {
        } 
        return gp ;
        
    } // to	    

/**
 * InputStreamからStringへ変換する
 */
public static String convertStreamToString(InputStream is) throws IOException { 
                                                                         // from
    if (is != null) {
        Writer writer = new StringWriter();

        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }
        return writer.toString();
    } else {
        return "";
    }
} //to


protected static Calendar actionHistoryFrom;
/*
 * 行動履歴を視覚的に表示
 */
protected static void doDrawActionHistory(final Context mycontext) { // (6) from
    String serverUrl = AppPreferences.getServerUrl(mycontext);
    String loginId = AppPreferences.getLoginId(mycontext); 
    int mHours = AppPreferences.getReceiveTime(mycontext);
    final TimeZone utc = TimeZone.getTimeZone("UTC");
    actionHistoryFrom = Calendar.getInstance(utc);
    actionHistoryFrom.add(Calendar.HOUR_OF_DAY,mHours * -1);
    Date fromDate = actionHistoryFrom.getTime();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d-kk:mm");
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    String dateBeginStr = sdf.format(fromDate);

    try {
        String locationFeed = "http://" + serverUrl + "/api?loginid="
                + loginId + "&mode=hist" + "&datebegin=" + dateBeginStr;
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 10 * 1000); // 接続のタイムアウト
        HttpConnectionParams.setSoTimeout(params, 2 * 1000); // データ取得のタイムアウト
        HttpClient objHttp = new DefaultHttpClient(params);

        HttpGet objGet = new HttpGet(locationFeed);
        HttpResponse objResponse = objHttp.execute(objGet);

        if (objResponse.getStatusLine().getStatusCode() == 200) {
            // 正常取得
            InputStream objStream = objResponse.getEntity()
                    .getContent();
            String jsonString = convertStreamToString(objStream);

            Type type = new TypeToken<List<GPSRecord>>() {
            }.getType();

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Gson gson = gsonBuilder.create();

            List<GPSRecord> locationList = gson.fromJson(jsonString,
                    type);

            if (locationList.size() > 1) {
                CheckPointOverlay.setActionHistory(locationList);
                CheckPointOverlay.setDisplayActionHistory(true);
                final GPSRecord locr = locationList.get(0);

              //  mHandler.post(new Runnable() {
              //      public void run() {

                        // マップ上の位置を更新する
                        Double geoLat = locr.getLatitude() * 1E6;
                        Double geoLng = locr.getLongitude() * 1E6;
                        GeoPoint point = new GeoPoint(geoLat
                                .intValue(), geoLng.intValue());

                        SosyaActivity.mapView.getController().animateTo(point);
                        SosyaActivity.mapView.invalidate();
                //    }
             //   });
            } else {
             //   mHandler.post(new Runnable() {
             //       public void run() {
                      //  Toast.makeText(mApplicationContext,
                      //          R.string.history_not_found,
                      //          Toast.LENGTH_LONG).show();
             //       }
            //    });
            }
        }
    } catch (SocketTimeoutException e) {
      //  mHandler.post(new Runnable() {
      //      public void run() {
       //         Toast.makeText(mApplicationContext,
       //                 R.string.cannot_connect_server,
       //                 Toast.LENGTH_LONG).show();
       //     }
       // });
    } catch (IOException e) {
      //  mHandler.post(new Runnable() {
      //      public void run() {
      //          Toast.makeText(mApplicationContext, R.string.io_error,
     //                   Toast.LENGTH_LONG).show();
     //       }
      //  });
    } finally {
    }
} // (6) to	    



}
