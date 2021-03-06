package jp.taka.hgtest;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class SampleService extends Service {
	private Context mycontext ;
    private final static String TAG = "SampleService";
    private final IBinder mBinder = new LocalBinder();
	private SendThread mSendThread;
	private RecieveThread mRecieveThread;
		//"10.8.64.229";
	private Socket		 socket; //ソケット
	private InputStream  in;		//入力ストリーム
	private OutputStream out;	//出力ストリーム
	private ConnectedListener mConnectedListener;

    public class LocalBinder extends Binder {
        SampleService getService() {
            return SampleService.this;
        }
    }
 
    @Override
    public void onCreate() {
       	mycontext = getApplicationContext();
        Toast.makeText(mycontext,TAG+" onCreate",
        		  Toast.LENGTH_LONG).show();
        Log.d(TAG,"onCreate");

 
    }	

    public void sendStart(ConnectedListener listener,View v) {
       	mSendThread = new SendThread(getApplicationContext(),listener,this,v);
       	mSendThread.start();
    }
    
    public void recieveStart(ConnectedListener listener,View v) {
       	mRecieveThread = new RecieveThread(mycontext,listener,v);
       	mRecieveThread.start();
    }

	@Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
 
    @Override
    public void onDestroy() {
       	disconnect();
    	 Toast.makeText(
    			    this,"終了します。",Toast.LENGTH_SHORT).show();	
        Log.i(TAG,"onDestroy");
    }
 
    // Methods for client
    public void userFunction() {
        Toast.makeText(getApplicationContext(),TAG+" userFunction",
      		  Toast.LENGTH_LONG).show();
        Log.d(TAG,"userFunction");
       //// Thread sendThread = new Thread(null, backRun,
       //  "send");
       // mSendThread.start();
    }
	

	
    //接続
    private void connect(String ip,int port){
    	int size;
    	byte[] w=new byte[1024];
    	try{
    		//ソケット通信
    		//addText("接続中");
    		socket=new Socket(ip,port);
    		
    		out=socket.getOutputStream();
    		in =socket.getInputStream();
    		
    		//addText("接続完了");
    		
    		//受信ループ//
    		while(socket!=null && socket.isConnected()){
    			//データの受信
    			size=in.read(w);
    			if(size<=0)continue;
    			//onRecieave();
    			//ラベルへの文字列追加
    			//addText(str);
    		}
    	}catch(Exception e){
    		 StringWriter s = new StringWriter();
    		    PrintWriter prt = new PrintWriter(s);
    		    e.printStackTrace(prt);
    		    Log.d(TAG, s.toString());
    		//addText("通信失敗しました");
    	}
    }
    
	//切断
    private void disconnect(){
    	try{
    		socket.close();
    		socket=null;
    	}catch(Exception e){	
    	}
    }
    
    //	送信
    public void sendData(){
    	
    		try{
    			//データの送信
    			if(socket!=null && socket.isConnected()){
    				byte[] w= new byte[2];
    				w[0]='1';
    				w[1]='9';
    				out.write(w);
    				out.flush();
    				//edtSend.setText("",TextView.BufferType.NORMAL);
    			}
    		}catch(Exception e){
    			  Log.e("sendData", "例外出力", e);
    			//addText("通信失敗しました");
    		}
    }

	
    public void recData(Context context){
    	
		//mConnectedListener.onRecieve();
     	Toast.makeText(
     	    	context,"受信しました。",Toast.LENGTH_SHORT).show();
    }

}
