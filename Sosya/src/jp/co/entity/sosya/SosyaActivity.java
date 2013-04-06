package jp.co.entity.sosya;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import jp.co.entity.sosya.CallbackService;
import jp.co.entity.sosya.CheckPointOverlay;
import jp.co.entity.sosya.DatabaseHelper;
import jp.co.entity.sosya.SosyaActivity;
import jp.co.entity.sosya.ICallbackListener;
import jp.co.entity.sosya.ICallbackService;
import jp.co.entity.sosya.MoveTrackDialogActivity;
import jp.co.entity.sosya.R;
import jp.co.entity.sosya.ReadWriteSDCardFile;
import jp.co.entity.sosya.Schedule;
import jp.co.entity.sosya.ScheduleDao;
import jp.co.entity.sosya.AppPreferences;
//import jp.co.entity.sosya.TimeDialogActivity;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class SosyaActivity extends MapActivity {
    static MapView mapView;
    static TextView textView;
	private CheckPointOverlay checkPointOverlay; // add 2009.12.16
	private MyLocationOverlay myLocationOverlay; // add 2010.02.01
    private Location meLocation;
    private SharedPreferences settings ;   
    private SharedPreferences.Editor editor ;
    public static final String PREFERENCES_FILE_NAME = "SosyaFile";
	private static final String TABLE_NAME = "schedule";
    private static final int VIEW_GROUP_ID = 1;
    private static final int ACTION_HISTORY = 2;
    public static final int SET_SAT_ID = 3;
    public static final int LOGIN_GAE_ID = 4 ;
    public static final int SETTING_ID = 5 ;
    public static final int DELETE_TRACK_ID = 6 ;
   // public static final int DISPLAY_TRACKS_ID = 7 ;
    private static final int MOVE2TRACK_ID = 7;
    private static final int SDCARD_ID = 8;
    public static final int KEYCODE_DPAD_DOWN = 20;
    public static final int KEYCODE_DPAD_LEFT = 21;
    public static final int KEYCODE_DPAD_RIGHT = 22;
    public static final int KEYCODE_DPAD_UP = 19;
    private final long j_max = 2147483647 ; // LocationUpdate minTime
   // private final long j_max = 60000 ; // LocationUpdate minTime
    private final float minDistance = 5 ;     // LocationUpdate minDistance
    //private final float minDistance = 0 ;     // LocationUpdate minDistance
    private static final int TRACKING_MESSAGE = 1; // 2010.01.14
    private static final int SERVER_MESSAGE = 2; 
    private static final int RECEIVE_MESSAGE = 3;
    private ICallbackService service;              // 2010.01.14
    protected Location gloc = null ;
    protected double all_distance = 0.0 ;
    protected Calendar d ,d2 ,d3 ;
    protected int all_hour = 0, all_minute = 0 ,all_second = 0 ;
    protected int track_id = 1 ;
    protected int time_on = 0 ;
    protected int rectime_on = 0 ;
    protected boolean start_on = false ;
    protected boolean timecall_on = false ;
    protected boolean track_disp = false ;
	private Button startButton;
	private Button checkButton;
	private Button stopButton;
	private Button trackButton;
	private Button sendButton;
	private Button currentButton;
	private Button recPosButton;
//	private boolean server_on = true ;

//	private  DatabaseHelper dbHelper = null ;
//	private  SQLiteDatabase db = null; 
//	private ScheduleDao scheduleDao = null;

 
	private View.OnClickListener startListener = new View.OnClickListener() {
		public void onClick(View arg0) { 
            addPointStart() ; // 2010.0113
		}
	};
	
	private View.OnClickListener currentListener = new View.OnClickListener() {
		public void onClick(View arg0) { 
			moveToCurrentLocation() ; // 2011.10.12
		}
	};
	
	private View.OnClickListener checkListener = new View.OnClickListener() {
		public void onClick(View arg0) {
    	    addPointLocation(); // 2010.01.15
		}
	};
	
	private View.OnClickListener stopListener = new View.OnClickListener() {
		public void onClick(View arg0) {
			addPointStop();   // 2010.01.13
		}
	};

	private View.OnClickListener trackListener = new View.OnClickListener() {
		public void onClick(View arg0) {
        	display_tracks(); // 2010.05.12
        	track_disp = true;
		}
	};
	
	private View.OnClickListener sendListener = new View.OnClickListener() {
		public void onClick(View arg0) {
        	GPSSend.sendTracking(getApplicationContext());
		}
	};
	
	private View.OnClickListener recPosListener = new View.OnClickListener() {
		public void onClick(View arg0) {
	       	if(rectime_on == 0){
	       		rectime_on  = 1 ;
	            Intent intent = new Intent(SosyaActivity.this,
	                   CallbackService.class);
	            bindService(intent, conn2, BIND_AUTO_CREATE);
	       	}
		}
	};
	
    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
        	
            //int message_id = msg.what ;
        	//switch (message_id) {
        	//case 1 :    // TRACKING_MESSAGE
        	// GPSから取得
            if (msg.what == TRACKING_MESSAGE) {
            	 Log.d("SosyaActivity","TRACKING_MESSAGE" );
        		if(start_on && time_on == 2){
        	      timecall_on = true ;
          		  addPointLocation(); // 2010.01.19
        		}
          		// サーバーへリアル位置送信
            }else if (msg.what == SERVER_MESSAGE) {
            	if(AppPreferences.getSendStop(getApplicationContext()) == 0){	
            		 Log.d("SosyaActivity","SERVER_MESSAGE on" );
                    GPSSend.sendReal(gloc,getApplicationContext());
                }else{
                	Log.d("SosyaActivity","SERVER_MESSAGE off" );
                }
            	// サーバーからリアル位置受信
            }else if (msg.what == RECEIVE_MESSAGE) {
            	if(AppPreferences.getReceiveStop(getApplicationContext()) == 0){	   		
            	  Log.d("SosyaActivity","RECEIVE_MESSAGE on" ); 
            	  
                  if(start_on == false){
                      // MyLocationOverlayを有効化
                      myLocationOverlay.enableMyLocation();
                   }
                   Location location = locationManager
                           .getLastKnownLocation(LocationManager.GPS_PROVIDER);
               	   
                   if (location != null && myLocationOverlay.getMyLocation() != null) {	  
            	  
            	     GeoPoint rp = GPSReceive.posReal(location ,getApplicationContext()) ;
			   //   int latitudeE6 = (int) (rp.getLatitude() * 1E6);
			   //   int longitudeE6 = (int) (rp.getLongitude() * 1E6);
			   //   GeoPoint gp = new GeoPoint(latitudeE6, longitudeE6);  
			         mapView.getController().animateTo(rp);
				     Drawable marker = getResources().getDrawable(R.drawable.point);
				     marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());  
				     checkPointOverlay = new CheckPointOverlay(marker);
				     mapView.getOverlays().add(checkPointOverlay);	    	
				// set start marker
					 OverlayItem item = new OverlayItem(rp, "start" , "");
					 item.setMarker(marker);
					 checkPointOverlay.addPoint(item);			  
				     mapView.invalidate(); 
            	  }else{
                      textView.setText("サーバーに現在位置を送信しようとしています。現在位置マークが表示されるまでお待ち下さい。");
                      Toast.makeText(getApplicationContext(),"位置が特定されていません。",Toast.LENGTH_SHORT).show();       	   
            	  }
            	}else{
            	  Log.d("SosyaActivity","RECEIVE_MESSAGE off" );
            	  if(rectime_on == 2){
            	     rectime_on = 0 ;
    		         try {
    		               service.removeListener(receivelistener);
    		         } catch (RemoteException e) {
    		            Log.e("SosyaActivity", e.getMessage(), e);
    		         }
    		           unbindService(conn2); 
    		           Log.d("SosyaActivity","RECEIVE_MESSAGE unbindService " );
            	  }
            	}
            }else {	  
                super.dispatchMessage(msg);
            }
            
        }

    };

    private ICallbackListener trackinglistener = new ICallbackListener.Stub() {
        public void receiveMessage(String message) throws RemoteException {
            handler.sendMessage(handler.obtainMessage(TRACKING_MESSAGE, message));
        }
    };
	
    private ICallbackListener serverlistener = new ICallbackListener.Stub() {
        public void receiveMessage(String message) throws RemoteException {
            handler.sendMessage(handler.obtainMessage(SERVER_MESSAGE, message));  
        }
    };
    
    private ICallbackListener receivelistener = new ICallbackListener.Stub() {
        public void receiveMessage(String message) throws RemoteException {
            handler.sendMessage(handler.obtainMessage(RECEIVE_MESSAGE, message));  
        }
    };
    
    private ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName,
                IBinder binder) {
            // binderからサービスを取得
            service = ICallbackService.Stub.asInterface(binder);
            try {
                // サービスにリスナを設定
            	if(time_on == 1){
                   service.addListener(trackinglistener);
                   service.addListener(serverlistener);
                   time_on = 2;
                }
            	//if(rectime_on == 1){
                //  service.addListener(receivelistener);
                //  rectime_on = 2 ;
            	//}
            } catch (RemoteException e) {
                Log.e("SosyaActivity", e.getMessage(), e);
            }
           
        }

        public void onServiceDisconnected(ComponentName componentName) {
        }
    };
    
    private ServiceConnection conn2 = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName,
                IBinder binder) {
            // binderからサービスを取得
            service = ICallbackService.Stub.asInterface(binder);
            try {
                // サービスにリスナを設定
            	//if(time_on == 1){
                //   service.addListener(trackinglistener);
                //   service.addListener(serverlistener);
                //   time_on = 2;
                //}
            	if(rectime_on == 1){
                  service.addListener(receivelistener);
                  rectime_on = 2 ;
            	}
            } catch (RemoteException e) {
                Log.e("SosyaActivity", e.getMessage(), e);
            }
           
        }

        public void onServiceDisconnected(ComponentName componentName) {
        }
    };
    
    
    private void setEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        view.invalidate();
    }
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mapView = (MapView) findViewById(R.id.Map);
        textView = (TextView) findViewById(R.id.PosisionView);

        locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                Log.d("SosyarActivity", "location changed to "
                        + location.getLongitude() + " "
                        + location.getLatitude());
            }

            public void onProviderDisabled(String provider) {
                Log.d("SosyaActivity", "provider changed to "
                        + provider);
            }

            public void onProviderEnabled(String provider) {
                Log.d("SosyaActivity", "provider " + provider
                        + " enabled");
            }

            public void onStatusChanged(String provider, int status,
                    Bundle extras) {
                Log.d("SosyaActivity", "provider " + provider
                        + "'s status is changed to " + status);

            }

        };
        
        startButton = (Button) findViewById(R.id.startButton);
        currentButton = (Button) findViewById(R.id.currentButton);
        checkButton = (Button) findViewById(R.id.checkButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        trackButton = (Button) findViewById(R.id.trackButton);
        sendButton = (Button) findViewById(R.id.sendButton);
        recPosButton = (Button) findViewById(R.id.recPosButton);
        
        startButton.setOnClickListener(startListener);
        currentButton.setOnClickListener(currentListener);
        checkButton.setOnClickListener(checkListener);
        stopButton.setOnClickListener(stopListener);
        trackButton.setOnClickListener(trackListener);
        sendButton.setOnClickListener(sendListener);
        recPosButton.setOnClickListener(recPosListener);
        
        myLocationOverlay = new MyLocationOverlay(getApplicationContext(),
                mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.onProviderEnabled(LocationManager.GPS_PROVIDER);
        //myLocationOverlay.enableCompass();
        textView.setText("現在位置マークが表示されたらスタートボタンをタップして下さい。");

        // 一番初めに現在位置が配信された際に実行されるコールバック
        myLocationOverlay.runOnFirstFix(new Runnable() {
        	public void run() {
            	// 中心位置を現在位置に移動する
                mapView.getController().animateTo(
                        myLocationOverlay.getMyLocation());
            }
        });
        
        mapView.getOverlays().add(myLocationOverlay);

		// ItemizedOverlay first dummy add 2009.12.16
		Drawable marker = getResources().getDrawable(R.drawable.track);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());  
		checkPointOverlay = new CheckPointOverlay(marker);
	    mapView.getOverlays().add(checkPointOverlay);	    
		// end add 2009.12.16

	    settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);   
	    editor = settings.edit();
	 //   String setInterval = settings.getString("INTERVAL", "NONE");
	    String setTrackID = settings.getString("TRACKID", "NONE");
	    String setMoveTrackNo = settings.getString("MOVETRACKNO", "NONE");
	    String moveON = settings.getString("MOVEON", "NONE");
	 //   if(setInterval == "NONE"){
	 //     editor.putString("INTERVAL","10");
	 //     editor.commit();
	 //   }
	    if(setTrackID == "NONE"){
		   editor.putString("TRACKID","1");
		   editor.commit();
		}
	    if(setMoveTrackNo == "NONE"){
			   editor.putString("MOVETRACKNO","1");
			   editor.commit();
		}
	    if(moveON == "NONE"){
			   editor.putString("MOVEON","0");
			   editor.commit();
		}
	    
	  
        // LocationListener
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        List<String> allProviders = locationManager.getAllProviders();
        for (int i = 0; i < allProviders.size(); i++) {
            Log.d("SosyaActivity", "provider [" + i + "] is "
                    + allProviders.get(i));
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, j_max,
                minDistance, locationListener);
        
	    
        mapView.setBuiltInZoomControls(true);
		mapView.setClickable(true);
        mapView.setEnabled(true);
        mapView.invalidate();
    
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      //  menu.add(VIEW_GROUP_ID, LOGIN_GAE_ID, 1,
      //          R.string.login_gae).setIcon(R.drawable.start);    
        menu.add(VIEW_GROUP_ID, SETTING_ID, 2,
                R.string.setting);    
    	menu.add(VIEW_GROUP_ID, ACTION_HISTORY, 3,
                R.string.rec_track);        
        menu.add(VIEW_GROUP_ID, SET_SAT_ID, 4,
                R.string.set_sat);
        menu.add(VIEW_GROUP_ID, DELETE_TRACK_ID, 5,
                 R.string.delete_tracks);       
     //   menu.add(VIEW_GROUP_ID, DISPLAY_TRACKS_ID, 1,
     //           R.string.display_tracks);
        menu.add(VIEW_GROUP_ID, MOVE2TRACK_ID, 1,
                   R.string.move2track);
        menu.add(VIEW_GROUP_ID, SDCARD_ID, 6,
                R.string.sd_card); 

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        super.onMenuItemSelected(featureId, item);
        switch (item.getItemId()) {
     //   case LOGIN_GAE_ID:
     //   	Intent intent_gae = new Intent(SosyaActivity.this, GAEActivity.class);
     //   	startActivity(intent_gae);
     //   	break;
        case SETTING_ID:
        // 	Intent intent_time = new Intent(SosyaActivity.this, TimeDialogActivity.class);
        //	startActivity(intent_time);
             	Intent intent_url = new Intent(SosyaActivity.this, AppPreferences.class);
            	startActivity(intent_url);
            	break;
        case SET_SAT_ID:
        	boolean nowSatellite = mapView.isSatellite();
            mapView.setSatellite(!nowSatellite);
            break;
        case DELETE_TRACK_ID:    
        	delete_now_track();
            break;
        case MOVE2TRACK_ID:
            Intent intent_movetrack = new Intent(SosyaActivity.this,MoveTrackDialogActivity.class);
        	startActivity(intent_movetrack);
        	break;
        case SDCARD_ID:
            //DB Close
            Intent intent_sdcard = new Intent(SosyaActivity.this,ReadWriteSDCardFile.class);
        	startActivity(intent_sdcard);
        	track_disp = true ;
        	break; 	
        case ACTION_HISTORY: // 行動履歴表示
        	
        	GPSReceive.doDrawActionHistory(getApplicationContext());
            //CheckPointOverlay.setDisplayActionHistory(false);
            //mapView.invalidate();
            break;
      	
        }
        return true;
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    @Override
    public boolean onKeyDown(int KeyCode,KeyEvent event){
    	boolean result =  super.onKeyDown(KeyCode,event);
    	
		GeoPoint MovePoint = mapView.getMapCenter();
        Point pos = mapView.getProjection().toPixels(MovePoint,null);
	
        switch (KeyCode) {

        case KEYCODE_DPAD_DOWN:
        MovePoint =  mapView.getProjection().fromPixels(pos.x,pos.y + 100);
            break;
        case KEYCODE_DPAD_LEFT:
            MovePoint =  mapView.getProjection().fromPixels(pos.x - 100,pos.y);
            break;
        case KEYCODE_DPAD_RIGHT:
            MovePoint =  mapView.getProjection().fromPixels(pos.x + 100,pos.y);
            break;
        case KEYCODE_DPAD_UP:
            MovePoint =  mapView.getProjection().fromPixels(pos.x,pos.y - 100);
            break;
            
        }	
        
 		mapView.getController().setCenter(MovePoint);
 		      
        return result ;
    }

    private void moveToCurrentLocation() {
        if(start_on == false){
           // MyLocationOverlayを有効化
           myLocationOverlay.enableMyLocation();
        }
        Location location = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	   
        if (location != null && myLocationOverlay.getMyLocation() != null) {
	         textView.setText("緯度 = " + location.getLatitude()
	                    + "\n経度 = " + location.getLongitude());
	        int latitudeE6 = (int) (location.getLatitude() * 1E6);
	        int longitudeE6 = (int) (location.getLongitude() * 1E6);
	        
	        GeoPoint gp = new GeoPoint(latitudeE6, longitudeE6);
	        mapView.getController().animateTo(gp);
	       
        } else {    
            textView.setText("現在位置マークが表示されたらスタートボタンをタップして下さい。");
            Toast.makeText(this,"位置が特定されていません。",Toast.LENGTH_SHORT).show();
        }
    }

  
    @SuppressWarnings("static-access")
	private void addPointLocation() {
    	int int_distance ;
    	int int_all_distance ;
    	@SuppressWarnings("unused")
		int int_remain ;
    	int int_kmPhour ;
    	double kmPhour ; 
    	double all_sec ;
    	int hour ;
    	int minute ;
    	int second ;
    	int carry ;

	   if(!start_on){
          Toast.makeText(this,"スタートしていません！！",Toast.LENGTH_SHORT).show();
          return ;
		}
    	
        Location loc2 =  locationManager
        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        if (loc2 != null && gloc != null) {
	      int latitudeE6 = (int) (loc2.getLatitude() * 1E6);
	      int longitudeE6 = (int) (loc2.getLongitude() * 1E6);
	      GeoPoint gp = new GeoPoint(latitudeE6, longitudeE6);
	      mapView.getController().animateTo(gp);
        
          d2 =Calendar.getInstance() ;

    	  second = d2.get(d2.SECOND) - d3.get(d3.SECOND);
    	  if(second < 0){
    		second = 60 + second ;
    		minute = d2.get(d2.MINUTE) - 1 ;
    		minute = minute - d3.get(d3.MINUTE);
    	  }else{
        	minute = d2.get(d2.MINUTE) - d3.get(d3.MINUTE);
    	  }
    	  if(minute < 0){
    		minute = 60 + minute ;
    		hour = d2.get(d2.HOUR_OF_DAY) - 1 ;
    		hour = hour - d3.get(d3.HOUR_OF_DAY);
    	  }else{
    		hour = d2.get(d2.HOUR_OF_DAY) -  d3.get(d3.HOUR_OF_DAY);
    	  }
    	  all_second += second ;
    	  carry = all_second / 60 ;
    	  all_second %= 60 ;
    	  all_minute += carry ;
       	  all_minute += minute ;
       	  carry = all_minute / 60 ;
       	  all_minute %= 60 ;
    	  all_hour += carry ;
    	  all_hour += hour ;
    	  
		  String message = "[" + all_hour + ":" ;
		  message += all_minute + "." ;  
		  message += all_second + "]" ;  
    	  
		  d3 = d2 ;	

  	      double lat1, lng1, lat2, lng2;
	      lat1 = gloc.getLatitude() ;
	      lng1 = gloc.getLongitude() ;
	      lat2 = loc2.getLatitude() ;
	      lng2 = loc2.getLongitude()  ;

		 float f_d2[] = new float[2] ;
		  
		 meLocation.distanceBetween(lat1,lng1,lat2,lng2,f_d2);

		  int_distance  = (int) f_d2[0] ;
		  all_distance += f_d2[0] ;
		  int_all_distance = (int) all_distance ;
		  
		  if(timecall_on){
		    OverlayItem item = new OverlayItem(gp, "", "");
			Drawable marker = getResources().getDrawable(R.drawable.track);
			marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight()); 
			item.setMarker(marker);
			checkPointOverlay.addPoint(item);
			timecall_on = false ;
			
    		all_sec = hour * 3600.0 + minute * 60.0 + second ;
		    kmPhour = (double)( f_d2[0] / all_sec * 3.6) ;
	   		int_kmPhour = (int)  kmPhour * 100;
    		kmPhour = int_kmPhour / 100 ;
  	        textView.setText("間隔 =" + int_distance + "m 時速=" + kmPhour + "km/h\n" + "走行距離 = " + int_all_distance + 
	    		  "m 走行時間 = " + all_hour + "時間" + all_minute + "分" + all_second + "秒"  ) ;   

		  }else{
    		message += "[" + int_all_distance + "m]" ;      		  
       
    		if(f_d2[1] > 0.0){
		      OverlayItem item = new OverlayItem(gp, "point", message);	
			  Drawable marker = getResources().getDrawable(R.drawable.point);
			  marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight()); 
			  item.setMarker(marker);
			  checkPointOverlay.addPoint(item);
		    }else{
		      OverlayItem item = new OverlayItem(gp, "point", message);	
			  Drawable marker = getResources().getDrawable(R.drawable.point2);
			  marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());  
			  item.setMarker(marker);
			  checkPointOverlay.addPoint(item);
		    }
    		
    		all_sec = all_hour * 3600.0 + all_minute * 60.0 + all_second ;
    		kmPhour = (double) (all_distance / all_sec * 3.6) ;
    		int_kmPhour = (int)  kmPhour * 100;
    		kmPhour = int_kmPhour / 100 ;
    		double mh ;
    		double remain ;
            if( kmPhour > 0.0){
            	if(42195 > all_distance){
            	  remain = 42195 - all_distance ;
                  remain = remain/1000 ;
                  mh = remain/kmPhour ;
            	}else{
            	  mh = 42.195/kmPhour ;
            	}
     	      }else{
      		    kmPhour = 0.0;
      		    mh = 0.0 ;
      	      }
            int mhour = (int) mh  ;
            double mm = mh - mhour ;
            mhour  += all_hour ;
            double mmmm = mm * 60 ;
            int int_minute = (int)mmmm ;
            double ms = mmmm - int_minute ;
            int_minute += all_minute ;
            int mss = (int)(ms *60) ;
            mss += all_second ;
            
        	carry = mss / 60 ;
        	mss %= 60 ;
        	int_minute += carry ;
           	carry = int_minute / 60 ;
           	int_minute %= 60 ;
        	mhour += carry ;   
            
            String str_mara  = "予測マラソンタイム ="  + mhour +  "時間" + int_minute  + "分" + mss + "秒" ;
    		String laptime = "ラップ［" + int_all_distance + "m:"+ all_hour + "時間" + all_minute + "分" + all_second + "秒］" +
    	    + kmPhour + "km/h\n"  + str_mara ;
            Toast.makeText(this,laptime,Toast.LENGTH_LONG).show();
  	        textView.setText("時速=" + kmPhour + "km/h\n" + "走行距離 = " + int_all_distance + 
	    		  "m 走行時間 = " + all_hour + "時間" + all_minute + "分" + all_second + "秒\n" 
	    		   + str_mara ) ;   

		  }	
		  DatabaseHelper dbHelper = new DatabaseHelper(this);
		  SQLiteDatabase db = dbHelper.getWritableDatabase();
		  ScheduleDao scheduleDao = new ScheduleDao(db);

	       Schedule newSchedule = new Schedule();
	       
	       newSchedule.setTrackid(track_id) ;
	       
	       String str_date = d2.get(d.YEAR) + "-" ;
	       str_date += (d2.get(d.MONTH) + 1) + "-" ;
	       str_date += (d2.get(d.DAY_OF_MONTH)) ;

	       newSchedule.setDate(str_date) ;
	       
	       String str_time = d2.get(d.HOUR_OF_DAY) + ":" ;
	       str_time += d2.get(d.MINUTE) + ":" ;
		   str_time += d2.get(d.SECOND) ;	   
	       newSchedule.setTime(str_time);
	        
	       newSchedule.setLatitudeE6(latitudeE6);
	       newSchedule.setLongitudeE6(longitudeE6);
	        
	       newSchedule.setHour(all_hour);
	       newSchedule.setMinute(all_minute);
	       newSchedule.setSecond(all_second);
	       newSchedule.setDistance(int_all_distance);
	       newSchedule.setSpeed(int_kmPhour);
	       scheduleDao.insert(newSchedule);
		   db.close();    
	       gloc = loc2 ;
	      
           mapView.invalidate();
        
        } else {    
            textView.setText("現在位置マークが表示されるまでお待ち下さい。");
            Toast.makeText(this,"位置が特定されていません。",Toast.LENGTH_SHORT).show();
        }

	}

    
	@SuppressWarnings("static-access")
	private void addPointStart() {

      gloc = locationManager
          .getLastKnownLocation(LocationManager.GPS_PROVIDER);

	  if (gloc != null && myLocationOverlay.getMyLocation() != null) {
		  checkPointOverlay.startStatus();
		  all_distance = 0.0 ;
		  all_hour = 0; all_minute = 0 ;all_second = 0 ;
		  
		 int latitudeE6 = (int) (gloc.getLatitude() * 1E6);
		 int longitudeE6 = (int) (gloc.getLongitude() * 1E6);
		 GeoPoint gp = new GeoPoint(latitudeE6, longitudeE6);

		 mapView.getController().animateTo(gp);
	       
		d=Calendar.getInstance() ;
		String message = "[" + d.get(d.HOUR_OF_DAY) + ":" ;
		message += d.get(d.MINUTE) + "." ;
		message += d.get(d.SECOND) + "]" ;	
		
       DatabaseHelper dbHelper = new DatabaseHelper(this);
	   SQLiteDatabase db = dbHelper.getWritableDatabase();
	   ScheduleDao scheduleDao = new ScheduleDao(db);

       String getTrackId = settings.getString("TRACKID", "0");
       track_id = Integer.parseInt(getTrackId);
       if(track_id < 1){
    	 track_id = 1 ;
    	 editor.putString("TRACKID","1");
    	 editor.commit();
       }
       
	   Schedule newSchedule = new Schedule();
		       
       newSchedule.setTrackid(track_id) ;
        
       String str_date = d.get(d.YEAR) + "-" ;
       str_date += (d.get(d.MONTH) + 1) + "-" ;
       str_date += (d.get(d.DAY_OF_MONTH)) ;
       newSchedule.setDate(str_date) ;
        
       String str_time = d.get(d.HOUR_OF_DAY) + ":" ;
       str_time += d.get(d.MINUTE) + ":" ;
       str_time += d.get(d.SECOND) ;	   
       newSchedule.setTime(str_time);
        
       newSchedule.setLatitudeE6(latitudeE6);
       newSchedule.setLongitudeE6(longitudeE6);
        
       newSchedule.setHour(0);
       newSchedule.setMinute(0);
       newSchedule.setSecond(0);
       newSchedule.setDistance(0);
       newSchedule.setSpeed(0);
       scheduleDao.insert(newSchedule);
	   db.close();
        
		hidetrack(); // 2010.05.13
		// ItemizedOverlay first dummy add 2010.05.13
		Drawable marker = getResources().getDrawable(R.drawable.track);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());  
		checkPointOverlay = new CheckPointOverlay(marker);
		mapView.getOverlays().add(checkPointOverlay);	    
		// end add 2010.05.13
		// set start marker
		OverlayItem item = new OverlayItem(gp, "start" , message);
		marker = getResources().getDrawable(R.drawable.start);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());  
		item.setMarker(marker);
		checkPointOverlay.addPoint(item);

		d3 = d ;
		start_on = true ;
		setEnabled(checkButton, true);
		track_disp = false;

		textView.setText("NO." + track_id + "緯度 = " + gloc.getLatitude()
                + "\n経度 = " + gloc.getLongitude());

       	time_on = 1 ;
        Intent intent = new Intent(SosyaActivity.this,
                   CallbackService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
        Log.d("SosyaActivity","start on" );        
        setEnabled(startButton, false);
        setEnabled(checkButton, true);
        setEnabled(stopButton, true);
        Toast.makeText(this,"スタートしました。",Toast.LENGTH_SHORT).show();
		mapView.invalidate();

	  } else {    
        textView.setText("現在位置マークが表示されるまでお待ち下さい。");
        Toast.makeText(this,"位置が特定されていません。",Toast.LENGTH_SHORT).show();
      }
	  
	}

	@SuppressWarnings("static-access")
	private void addPointStop() {
    	int int_all_distance ;
    	int hour ;
    	int minute ;
    	int second ;
    	int carry ;
   	    double all_sec ;
   	    double kmPhour ;
   	    int int_kmPhour ;
 
        Location loc2 =  locationManager
        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        if (loc2 != null && gloc != null && start_on) {
	        int latitudeE6 = (int) (loc2.getLatitude() * 1E6);
	        int longitudeE6 = (int) (loc2.getLongitude() * 1E6);
	        GeoPoint gp = new GeoPoint(latitudeE6, longitudeE6);
	        mapView.getController().animateTo(gp);   
        
        d2 =Calendar.getInstance() ;
        
        DatabaseHelper dbHelper = new DatabaseHelper(this);  
 	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    ScheduleDao scheduleDao = new ScheduleDao(db);
		
        Schedule newSchedule = new Schedule();
        
        newSchedule.setTrackid(-1) ;
        
        String str_date = d.get(d.YEAR) + "-" ;
        str_date += (d.get(d.MONTH) + 1) + "-" ;
        str_date += (d.get(d.DAY_OF_MONTH)) ;
        newSchedule.setDate(str_date) ;
         
        String str_time = d.get(d.HOUR_OF_DAY) + ":" ;
        str_time += d.get(d.MINUTE) + ":" ;
        str_time += d.get(d.SECOND) ;	   
        newSchedule.setTime(str_time);
        
        newSchedule.setLatitudeE6(latitudeE6);
        newSchedule.setLongitudeE6(longitudeE6);
        
    	second = d2.get(d2.SECOND) - d3.get(d3.SECOND);
    	if(second < 0){
    		second = 60 + second ;
    		minute = d2.get(d2.MINUTE) - 1 ;
    		minute = minute - d3.get(d3.MINUTE);
    	}else{
        	minute = d2.get(d2.MINUTE) - d3.get(d3.MINUTE);
    	}
    	if(minute < 0){
    		minute = 60 + minute ;
    		hour = d2.get(d2.HOUR_OF_DAY) - 1 ;
    		hour = hour - d3.get(d3.HOUR_OF_DAY);
    	}else{
    		hour = d2.get(d2.HOUR_OF_DAY) -  d3.get(d3.HOUR_OF_DAY);
    	}
    	all_second += second ;
    	carry = all_second / 60 ;
    	all_second %= 60 ;
    	all_minute += carry ;
       	all_minute += minute ;
       	carry = all_minute / 60 ;
       	all_minute %= 60 ;
    	all_hour += carry ;
    	all_hour += hour ;
    	
        newSchedule.setHour(all_hour);
        newSchedule.setMinute(all_minute);
        newSchedule.setSecond(all_second);
        
		String message = "[" + all_hour + ":" ;
		message += all_minute + "." ;  
		message += all_second + "]" ;  

	    double lat1, lng1, lat2, lng2;
	    lat1 = gloc.getLatitude() ;
	    lng1 = gloc.getLongitude() ;
	    lat2 = loc2.getLatitude() ;
	    lng2 = loc2.getLongitude()  ;

		float f_d2[] = new float[1] ;
		  
	    meLocation.distanceBetween(lat1,lng1,lat2,lng2,f_d2);
		
		all_distance += f_d2[0] ;
		int_all_distance = (int) all_distance ;
        newSchedule.setDistance(int_all_distance);
		
        String laptime = "ゴール［" + int_all_distance + "m : "+ all_hour + "時間" + all_minute + "分" + all_second + "秒］" ;
        Toast.makeText(this,laptime,Toast.LENGTH_LONG).show();		
		message += "[" + int_all_distance + "m]" ;  		
		// set stop marker
		OverlayItem item = new OverlayItem(gp, "stop" , message);
		Drawable marker = getResources().getDrawable(R.drawable.stop);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());  
		item.setMarker(marker);
		checkPointOverlay.addPoint(item);

		all_sec = all_hour * 3600.0 + all_minute * 60.0 + all_second ;
		kmPhour = (double) (all_distance / all_sec * 3.6) ;
		int_kmPhour = (int) kmPhour * 100 ;
		kmPhour = int_kmPhour / 100 ;
		
		newSchedule.setSpeed(int_kmPhour);
	    scheduleDao.insert(newSchedule);
	    db.close();

        textView.setText("平均時速 = " + kmPhour + "km/h\n" + 
        "走行距離 = " + int_all_distance + "m" + " 走行時間 = " + all_hour + "時間" + all_minute + "分" + all_second + "秒" ) ;  
        
		start_on = false ;
	 	checkPointOverlay.stopStatus();
		setEnabled(checkButton, false);
		
		track_id += 1 ;
		String str_track_id = "" ;
		str_track_id += track_id ;

   	    editor.putString("TRACKID",str_track_id);
   	    editor.commit();
   	 
        myLocationOverlay.disableMyLocation();

        setEnabled(startButton, true);
		setEnabled(stopButton, false);
		if(time_on == 2){				
	       time_on = 0 ;
		   try {
		        service.removeListener(trackinglistener);
		        service.removeListener(serverlistener);
		        //service.removeListener(receivelistener);
		   } catch (RemoteException e) {
		        Log.e("SosyaActivity", e.getMessage(), e);
		   }
		   Log.d("SosyaActivity","stop!!" );     
		  // if(rectime_on == 2){ // 位置情報受信中は、bindを切らない。
		     unbindService(conn); 
		  // }
		}         
        
		mapView.invalidate();

        } else {    
          textView.setText("現在位置マークが表示されるまでお待ち下さい。");
          Toast.makeText(this,"位置が特定されていません。",Toast.LENGTH_SHORT).show();
        }
        
	}
	
	private void delete_now_track() {
 
      DatabaseHelper dbHelper = new DatabaseHelper(this);
	  SQLiteDatabase db = dbHelper.getWritableDatabase();
	  ScheduleDao scheduleDao = new ScheduleDao(db); 
	  List<Schedule> schedulelist = scheduleDao.selectAll();

	  for(Schedule schedule:schedulelist) {
	    db.delete(TABLE_NAME, "rowid = " + schedule.getRowid(), null);
	  }
	  db.close();

      settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);   
	  editor = settings.edit();
	  editor.putString("TRACKID","1");
	  editor.commit();
	  textView.setText("");
	  
	  hidetrack();
	  // ItemizedOverlay first dummy add 2010.05.12
	  Drawable marker = getResources().getDrawable(R.drawable.track);
	  marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());  
	  checkPointOverlay = new CheckPointOverlay(marker);
	  mapView.getOverlays().add(checkPointOverlay);	    
	  // end add 2010.05.12
      CheckPointOverlay.setDisplayActionHistory(false);
	  mapView.invalidate();
	}
	
	private void display_tracks() {
	 setEnabled(trackButton, false);
	 Toast.makeText(this,"データ読み込み中。",Toast.LENGTH_SHORT).show();// test    
	  hidetrack();
	// ItemizedOverlay first dummy add 2010.05.12
	Drawable marker = getResources().getDrawable(R.drawable.track);
	marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());  
	checkPointOverlay = new CheckPointOverlay(marker);
	mapView.getOverlays().add(checkPointOverlay);	    
	// end add 2010.05.12	  

    //  DatabaseHelper dbHelper = new DatabaseHelper(this);
    DatabaseHelper dbHelper = new DatabaseHelper(this);
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    ScheduleDao scheduleDao = new ScheduleDao(db);
    List<Schedule> schedulelist2 = scheduleDao.selectAll();
    db.close(); 

    StringBuffer buf = new StringBuffer();

    int daSw = 0 ; 
    int empty = 0 ;
    if(schedulelist2 != null){
    for(Schedule schedule2:schedulelist2) {
    	if(schedule2 == null)continue ;
    	
        int latitudeE6 = schedule2.getLatitudeE6();
        int longitudeE6 = schedule2.getLongitudeE6();
        GeoPoint gp = new GeoPoint(latitudeE6, longitudeE6);
	    mapView.getController().animateTo(gp);
    	
    	if(schedule2.getSecond() == 0 && schedule2.getMinute() == 0 &&
    			schedule2.getHour() == 0){

          buf.append("No.");
          buf.append(schedule2.getTrackid()) ;
          buf.append("|日時");
          buf.append(schedule2.getDate());
          buf.append(",");
          buf.append(schedule2.getTime());
          buf.append("|");
          marker = getResources().getDrawable(R.drawable.start);
	      marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		      OverlayItem item = new OverlayItem(gp, "No.1", "");
		  item.setMarker(marker);
		  checkPointOverlay.addPoint(item);
          checkPointOverlay.setPos(gp,schedule2.getTrackid(),true);
		  daSw = 1;
		  empty = 1;

    	}else{
          if(daSw == 1){
    		if(schedule2.getTrackid() == -1){
    		  daSw = 0;
              buf.append("距離 =");
              buf.append(schedule2.getDistance());
              buf.append("m\n");
              buf.append("走行時間|");
              buf.append(schedule2.getHour());
              buf.append(":");
              buf.append(schedule2.getMinute());
              buf.append(":");
              buf.append(schedule2.getSecond());
              buf.append("|");
              buf.append("平均速度 =");
              double kmPhour ;
              double mh ;
      		  double remain ;
      		  int carry ;
              if(schedule2.getSpeed() > 0.0){
                kmPhour = schedule2.getSpeed() / 100 ;
            	if(42195 > schedule2.getDistance()){
              	  remain = 42195 - schedule2.getDistance() ;
                  remain = remain/1000 ;
                  mh = remain/kmPhour ;
            	}else{
            	  mh = 42.195/kmPhour ;
            	}
     	      }else{
      		    kmPhour = 0.0;
      		    mh = 0.0 ;
      	      }
              buf.append(kmPhour);
              buf.append("km/h\n");

              int mhour = (int) mh  ;
              double mm = mh - mhour ;
              mhour  += schedule2.getHour();
              double mmmm = mm * 60 ;
              int int_minute = (int)mmmm ;
              double ms = mmmm - int_minute ;
              int_minute += schedule2.getMinute() ;
              int mss = (int)(ms *60) ;
              mss += schedule2.getSecond() ;  
              carry = mss / 60 ;
              mss %= 60 ;
              int_minute += carry ;
              carry = int_minute / 60 ;
              int_minute %= 60 ;
              mhour += carry ;   
                
              buf.append("予測マラソンタイム =");
              buf.append(mhour);
              buf.append("時間");
              buf.append(int_minute);
              buf.append("分");    
              buf.append(mss);
              buf.append("秒\n");               
              marker = getResources().getDrawable(R.drawable.stop);
 	          marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		          OverlayItem item = new OverlayItem(gp, "", "");
		      item.setMarker(marker);
		      checkPointOverlay.addPoint(item);
              checkPointOverlay.setPos(gp,-1,true);

            }else{
              checkPointOverlay.setPos(gp,schedule2.getTrackid(),true);
    	    }

          }
   		     
    	}
    }

    }
    
    if(empty == 0) {
        buf.append("データがありません。");
    }
    
    textView.setText(buf.toString());    
    mapView.invalidate();
	setEnabled(trackButton, true);
}
	
    
	private void moveToTrack() {
		
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ScheduleDao scheduleDao = new ScheduleDao(db);
        List<Schedule> schedulelist = scheduleDao.selectAll();
        db.close(); 
        
        SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);   
        String moveTrackNo = settings.getString("MOVETRACKNO", "NONE");
        if(moveTrackNo != "NONE"){
          int move_no = Integer.parseInt(moveTrackNo);
          if(schedulelist != null){
 	         for(Schedule schedule:schedulelist) {
 	    	     if(schedule == null) continue ;
 	    	     if(schedule.getTrackid() == move_no){
 	    	    	 
 		     	   GeoPoint MovePoint = mapView.getMapCenter();
 	 	           Point pos = mapView.getProjection().toPixels(MovePoint,null);
 		           MovePoint =  mapView.getProjection().fromPixels(pos.x,pos.y - 100);    
 	 	  		   mapView.getController().setCenter(MovePoint);
 	 	  		   
 	    	       int latitudeE6 = schedule.getLatitudeE6();
 	    	       int longitudeE6 = schedule.getLongitudeE6();
 	    	       GeoPoint gp = new GeoPoint(latitudeE6, longitudeE6);	 
 	    	       mapView.getController().animateTo(gp);
 	    	       
 	    	       mapView.invalidate();
 	    	       break ;
 	    	     }
 	         }
          }
        }
	}
	
	private void reDisplay() {
		hidetrack();  
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ScheduleDao scheduleDao = new ScheduleDao(db);
        List<Schedule> schedulelist4 = scheduleDao.selectAll();
        db.close(); 
 
        StringBuffer buf = new StringBuffer();
        
		// ItemizedOverlay first dummy add
		Drawable marker = getResources().getDrawable(R.drawable.track);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());  
		checkPointOverlay = new CheckPointOverlay(marker);
	    mapView.getOverlays().add(checkPointOverlay);	    

        int daSw = 0 ; 
        int empty = 0 ;
        if(schedulelist4 != null){
          for(Schedule schedule4:schedulelist4) {
        	if(schedule4 == null) continue ;
            int latitudeE6 = schedule4.getLatitudeE6();
            int longitudeE6 = schedule4.getLongitudeE6();
            GeoPoint gp = new GeoPoint(latitudeE6, longitudeE6);
 
        	if(schedule4.getSecond() == 0 && schedule4.getMinute() == 0 &&
        			schedule4.getHour() == 0){
           	  mapView.getController().animateTo(gp);
              buf.append("No.");
              buf.append(schedule4.getTrackid()) ;
              buf.append("|日時");
              buf.append(schedule4.getDate());
              buf.append(",");
              buf.append(schedule4.getTime());
              buf.append("|");
      		  marker = getResources().getDrawable(R.drawable.start);
    	      marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
   		      OverlayItem item = new OverlayItem(gp, "No.1", "");
    		  item.setMarker(marker);
    		  checkPointOverlay.addPoint(item);
              checkPointOverlay.setPos(gp,schedule4.getTrackid(),true);
    		  daSw = 1;
    		  empty = 1;
        	}else{
              if(daSw == 1){
        		if(schedule4.getTrackid() == -1){
        		  daSw = 0;
                  buf.append("距離 =");
                  buf.append(schedule4.getDistance());
                  buf.append("m\n");
                  buf.append("走行時間|");
                  buf.append(schedule4.getHour());
                  buf.append(":");
                  buf.append(schedule4.getMinute());
                  buf.append(":");
                  buf.append(schedule4.getSecond());
                  buf.append("|");
                  buf.append("平均速度 =");
                  double kmPhour ;
                  double mh ;
                  double remain ;
                  int carry ;
                  if(schedule4.getSpeed() > 0.0){
                    kmPhour = schedule4.getSpeed() / 100 ;
                    //  mh = 42.195/kmPhour ;
                    if(schedule4.getDistance() < 42195){
                    	remain = (double)(42195 - schedule4.getDistance()) ;
                        remain = remain/1000 ;
                        mh = remain/kmPhour ;
                    }else{
                        mh = 42.195/kmPhour ;	
                    }

         	      }else{
          		    kmPhour = 0.0;
          		    mh = 0.0 ;
          	      }
                  buf.append(kmPhour);
                  buf.append("km/h\n");
 
                  int mhour = (int) mh  ;
                  double mm = mh - mhour ;
                  mhour  += schedule4.getHour();
                  double mmmm = mm * 60 ;
                  int int_minute = (int)mmmm ;
                  double ms = mmmm - int_minute ;
                  int_minute += schedule4.getMinute();
                  int mss = (int)(ms *60) ;
                  mss += schedule4.getSecond();
                  carry = mss / 60 ;
                  mss %= 60 ;
                  int_minute += carry ;
                  carry = int_minute / 60 ;
                  int_minute %= 60 ;
                  mhour += carry ;   
 
                  buf.append("予測マラソンタイム =");
                  buf.append(mhour);
                  buf.append("時間");
                  buf.append(int_minute);
                  buf.append("分");    
 
                  buf.append(mss);
                  buf.append("秒\n");               
    		      marker = getResources().getDrawable(R.drawable.stop);
     	          marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
   		          OverlayItem item = new OverlayItem(gp, "", "");
    		      item.setMarker(marker);
    		      checkPointOverlay.addPoint(item);
                  checkPointOverlay.setPos(gp,-1,true);
                }else{
                  checkPointOverlay.setPos(gp,schedule4.getTrackid(),true);
        	    }
              }
        	}
          }
         // mapView.invalidate(); // 2010.4.27
        }
        mapView.invalidate(); // 2010.4.27    
        if(empty == 0) {
            buf.append("データがありません。");
        }
        
        textView.setText(buf.toString());    
 
        mapView.setBuiltInZoomControls(true);
		mapView.setClickable(true);
        mapView.setEnabled(true);
        //mapView.invalidate(); // 2010.4.27
 
	}
	
	private void hidetrack() {
		List<Overlay> overlays = mapView.getOverlays();
		if (overlays.contains(checkPointOverlay)) {
			mapView.getOverlays().remove(checkPointOverlay);
		}

	}
	
    private LocationListener locationListener;
    private LocationManager locationManager;	

    @Override
    protected void onResume() {
        // MyLocationOverlayを有効
        myLocationOverlay.enableMyLocation();
        mapView.invalidate();
        super.onResume();

    }
    
    @Override
   protected void onRestart() {
	   mapView.invalidate();
       super.onRestart();
       
       SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);   
       String move_on = settings.getString("MOVEON", "NONE");
       
   //    if(AppPreferences.getSendStop(getApplicationContext()) == 1){
//	      if(server_on == true ){
//    	    try {
//			   service.removeListener(serverlistener);
//			   server_on = false ;
//		    } catch (RemoteException e) {
//			   Log.e("SosyaActivity", e.getMessage(), e);
//		    }
//	      }
//       	}else{
//       	  if(server_on == false ){  		
//            try {
//            // サービスにリスナを設定
//               service.addListener(serverlistener);
//               server_on = true ;
//            } catch (RemoteException e) {
//              Log.e("SosyaActivity", e.getMessage(), e);
//            }
//       	  }
//       	}
       if( move_on == "1" ){
          moveToTrack();
 	      editor.putString("MOVEON","0");
	      editor.commit();
       }else{
    	 if(time_on == 2){
    		 track_disp = false ; // 計測中は軌跡線を表示しない。
    	 }
    	 if(track_disp == true){	
           reDisplay();
    	 }
       }
   }
    
    @Override
    protected void onPause() {
        super.onPause();
        mapView.invalidate();
    }

    @Override
    protected void onDestroy() {
      super.onDestroy();
	  
		  if(time_on == 2){
	        time_on = 0 ;
	        try {
			  service.removeListener(trackinglistener);
			  service.removeListener(serverlistener);
		    } catch (RemoteException e) {
			  Log.e("SosyaActivity", e.getMessage(), e);
		    }
		    unbindService(conn);
		  }

		  if(rectime_on == 2){
			  rectime_on = 0 ;
			  try {
				  service.removeListener(receivelistener); 
			    } catch (RemoteException e) {
				  Log.e("SosyaActivity", e.getMessage(), e);
			    }
			    unbindService(conn2); 
		  }
		 
	          

	   myLocationOverlay.disableMyLocation();
       myLocationOverlay.disableCompass();
      
       locationManager.removeUpdates(locationListener);
       locationManager = null;      	
       track_disp = false;
       Log.v("SosyaActivity", "onDestroy was called.");
 
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      Log.v("SosyaActivity", "onConfigurationChanged was called.");
    }


}

