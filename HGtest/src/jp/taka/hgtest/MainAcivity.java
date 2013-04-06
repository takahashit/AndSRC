package jp.taka.hgtest;





import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainAcivity extends Activity implements ConnectedListener{
    private final static String TAB = "MainActivity";
    private SampleService mSampleService;
    private Activity mActivity;
    private TextView mTextView ;
    private Context mContext;
    private boolean recvOn = false; 
    View mView;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
   
        mActivity = this;
        mTextView = (TextView) findViewById(R.id.t_view);
        
        Button button = (Button) findViewById(R.id.startButton);
        Button button2 = (Button) findViewById(R.id.recieveButton);
        
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
      	  	  if(recvOn){
 	  		  	 Toast.makeText(mActivity,
 	  		          "Activity"+" Recv on", Toast.LENGTH_LONG).show();
 	  	  }
            	mSampleService.sendStart((ConnectedListener)mActivity,v);
 
            }


        });
        
        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	recvOn = false;
            	 mSampleService.recData(getApplicationContext());
            	
            }
            

        });
    }
  
    @Override
    public void onStart() {
        Log.i(TAB,"onStart");
        super.onStart();
        
        Intent intent = new Intent(getApplicationContext(), SampleService.class);
        bindService(intent, mSampleServiceConnection, Context.BIND_AUTO_CREATE);
    }
    

    @Override
    protected void onResume() {
        super.onResume();
        mContext = getApplicationContext();
	  	  Toast.makeText(mContext,
	  	         "Activity"+"  onResume", Toast.LENGTH_LONG).show();
        

        
    }
    
    @Override
    protected void onPause() {
        super.onPause();
	  	  Toast.makeText(mContext,
		  	         "Activity"+"  onPause", Toast.LENGTH_LONG).show();

    }
    
    @Override
    public void onStop() {
        Log.i(TAB,"onStop");
        super.onStop();
        // Unbind
        unbindService(mSampleServiceConnection);
    }
 
    private ServiceConnection mSampleServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SampleService.LocalBinder binder = (SampleService.LocalBinder)service;
            mSampleService = binder.getService();
            mTextView.setText("onConected");
        }

		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
    };
   
	@Override
	public void onRecieve() {
		//Context context = mContext.getApplicationContext();
		//
		//Context context = mContext.getApplicationContext();
		//
		

		  		try{
		  			Toast.makeText(mActivity,
		  		         "Activity"+" Recv on", Toast.LENGTH_LONG).show();
		  			}
		  			catch(Exception e){
		  				Log.e("MainActivity", e.getMessage(), e);
		  			}

		recvOn = true;
		 Log.i(TAB,"onRecieve !!");
	
	}


}