package jp.co.entity.sosya;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
//import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;


public class CallbackService extends Service {

	private static final int TRACKING_MESSAGE = 1;
	private static final int SERVER_MESSAGE = 2;
	private static final int RECEIVE_MESSAGE = 3;
    private Context mApplicationContext;

    private int count = 0;
    private int count2 = 0;
    private int count3 = 0;
    private int interval = 10;
    private int interval2 = 1800;
    private int interval3 = 10;
 
    private RemoteCallbackList<ICallbackListener> listeners = new RemoteCallbackList<ICallbackListener>();
    
    private Handler handler = new Handler() {	
    
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TRACKING_MESSAGE) {
                int numListeners = listeners.beginBroadcast();
                for (int i = 0; i < numListeners; i++) {
                    try {
                        listeners.getBroadcastItem(i).receiveMessage(
                                "message NO." + count);
                    } catch (RemoteException e) {
                        Log.e("CallbackService", e.getMessage(), e);
                    }
                }
                listeners.finishBroadcast();
                count++;
              //  SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);   
              //  String setInterval = settings.getString("INTERVAL", "NONE");
              //  interval = Integer.parseInt(setInterval);           
             
                //interval2 = AppPreferences.getServerFreq(mApplicationContext); 	
              //  handler.sendEmptyMessage(TRACKING_MESSAGE);
               // handler.sendEmptyMessage(SERVER_MESSAGE); 
                interval = AppPreferences.getTrackingFreq(mApplicationContext); //2011
                // 次回のメッセージを10秒後に送信
                handler.sendEmptyMessageDelayed(TRACKING_MESSAGE, interval * 1000);
            } else if(msg.what == SERVER_MESSAGE){
                  if(AppPreferences.getSendStop(mApplicationContext) == 0){
                     int numListeners = listeners.beginBroadcast();
                     for (int i = 0; i < numListeners; i++) {
                        try {
                            listeners.getBroadcastItem(i).receiveMessage(
                                 "message NO." + count2);
                        } catch (RemoteException e) {
                         Log.e("CallbackService", e.getMessage(), e);
                        }     	
                     } 
               
                     listeners.finishBroadcast();
                     count2++;
                     interval2 = AppPreferences.getServerFreq(mApplicationContext); 
                     handler.sendEmptyMessageDelayed(SERVER_MESSAGE, interval2 * 1000);
                  }
            } else if(msg.what == RECEIVE_MESSAGE){
                int numListeners = listeners.beginBroadcast();
                for (int i = 0; i < numListeners; i++) {
                   try {
                       listeners.getBroadcastItem(i).receiveMessage(
                            "message NO." + count3);
                   } catch (RemoteException e) {
                    Log.e("CallbackService", e.getMessage(), e);
                   }     	
                } 
          
                listeners.finishBroadcast();
                count3++;
                interval3 = AppPreferences.getServerFreq(mApplicationContext); 
                handler.sendEmptyMessageDelayed(RECEIVE_MESSAGE, interval3 * 1000);              	  
            } else {	
                   super.dispatchMessage(msg);
            } 
        }
    };
    
    @Override
    public void onCreate() {
        Log.d("CallbackService", "on create");
        mApplicationContext = this.getApplicationContext();
        handler.sendEmptyMessage(TRACKING_MESSAGE);
        if(AppPreferences.getSendStop(mApplicationContext) == 0){
          handler.sendEmptyMessage(SERVER_MESSAGE);
        }
        handler.sendEmptyMessage(RECEIVE_MESSAGE);
    }
    
    public IBinder onBind(Intent intent) {
        return stub;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public Handler getHandler() {
		return handler;
	}

	private final ICallbackService.Stub stub = new ICallbackService.Stub() {
        public void addListener(ICallbackListener listener)
                throws RemoteException {
            listeners.register(listener);
        }

        public void removeListener(ICallbackListener listener)
                throws RemoteException {
            listeners.unregister(listener);
        }
    };
}
