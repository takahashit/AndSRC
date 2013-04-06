package jp.taka.hgtest;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

public class RecieveThread extends Thread{
	private final String TAG = "RecieveTread";
	private ConnectedListener mListener;
	View mv;
	
	public RecieveThread(Context context,ConnectedListener listener,View v){
		mListener = listener;
		mv = v;
		//Toast.makeText(context,
	    //        TAG+" Thread run", Toast.LENGTH_LONG).show(); 
	}
	
	@Override
	public void run(){
      mListener.onRecieve();
	}
}
