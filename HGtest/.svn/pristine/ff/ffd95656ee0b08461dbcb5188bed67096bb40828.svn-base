package jp.taka.hgtest;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

public class SendThread extends Thread{
	private final String TAG = "SendTread";
	private ConnectedListener mConnectedListener;
	private SampleService mService;
	private View mv;
	
	public SendThread(Context context,ConnectedListener listener,SampleService service,View v){
		mConnectedListener = listener;
		  Toast.makeText(context,
			      TAG+" Thread run", Toast.LENGTH_LONG).show();
		mService = service;
		mv = v;
	}
	
	@Override
	public void run(){

         mService.recieveStart(mConnectedListener,mv);
                    
	}
}
