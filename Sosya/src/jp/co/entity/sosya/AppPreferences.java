package jp.co.entity.sosya;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class AppPreferences extends PreferenceActivity {

    public static final String KEY_SERVER_FREQ = "server_freq";
    public static final String KEY_TRACKING_FREQ = "tracking_freq";
    public static final String KEY_LOGIN_ID = "login_id";
    public static final String KEY_SERVER_URL = "server_url";
    public static final String KEY_SEND_STOP = "send_stop";
    public static final String KEY_RECEIVE_RADIUS = "receive_radius";
    public static final String KEY_RECEIVE_FREQ = "receive_freq";
    public static final String KEY_RECEIVE_TIME = "receive_time";
    public static final String KEY_RECEIVE_STOP = "receive_stop";
    
    public void onCreate(Bundle icicle) {

        super.onCreate(icicle);
        // 設定画面の設定
        addPreferencesFromResource(R.layout.apppreferences);
    }

    /*
     * サーバー送信間隔を取得
     */
    public static int getServerFreq(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        String freq = pref.getString(KEY_SERVER_FREQ, "1800");
        return Integer.parseInt(freq);
    }

    /*
     * トラッキング間隔を取得
     */
    public static int getTrackingFreq(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        String freq = pref.getString(KEY_TRACKING_FREQ, "10");
        return Integer.parseInt(freq);
    }    /*
              * 　ログインID取得
     */
    public static String getLoginId(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getString(KEY_LOGIN_ID, "default");
    }

    /*
     * サーバーのURL取得
     */
    public static String getServerUrl(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getString(KEY_SERVER_URL, "10.0.0.2");
    }

    /*
     * 送信の停止取得
     */
    public static int getSendStop(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        String freq = pref.getString(KEY_SEND_STOP, "1");
        return Integer.parseInt(freq);
    }
    
    /*
     * 位置情報受信半径の取得
     */
    public static String getReceiveRadius(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return pref.getString(KEY_RECEIVE_RADIUS, "10000");
    }
    
    /*
     * 軌跡履歴期間の取得
     */
    public static int getReceiveTime(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        String freq = pref.getString(KEY_RECEIVE_TIME, "168");
        return Integer.parseInt(freq);
    }
    
    /*
     * 受信間隔を取得
     */
    public static int getReceiveFreq(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        String freq = pref.getString(KEY_RECEIVE_FREQ, "1800");
        return Integer.parseInt(freq);
    }  
    
    /*
     * 受信の停止取得
     */
    public static int getReceiveStop(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        String freq = pref.getString(KEY_RECEIVE_STOP, "1");
        return Integer.parseInt(freq);
    }
       
    @Override
    protected void onDestroy() {
      super.onDestroy();
      Log.v("AppPreferences", "onDestroy was called.");
    
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      Log.v("AppPreferences", "onConfigurationChanged was called.");
    }


}
