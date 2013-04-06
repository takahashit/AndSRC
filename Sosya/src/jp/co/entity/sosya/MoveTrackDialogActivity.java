package jp.co.entity.sosya;

import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MoveTrackDialogActivity extends Activity {
  public static final String PREFERENCES_FILE_NAME = "SosyaFile";
  private static final String TABLE_NAME = "schedule";	
  private TextView textView;
  private EditText textBox;
  private Button moveButton ;
  private int move_no = 0 ;
  private TextView del_textView;
  private EditText del_textBox;	
  private Button delButton ;
  private int del_no = 0 ;
  private DatabaseHelper dbHelper = null ;
  

  private void setEnabled(View view, boolean enabled) {
      view.setEnabled(enabled);
  }
	
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.move_dialog);
    textView = (TextView) findViewById(R.id.moveView);
    moveButton = (Button) findViewById(R.id.movebutton);

    SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);   
    String setTrackNo = settings.getString("MOVETRACKNO", "NONE");
    String msg = "表示軌跡番号：" + setTrackNo ;
    textView.setText(msg);
    setEnabled(moveButton,true); 
    
    del_textView = (TextView) findViewById(R.id.delView);
    delButton = (Button) findViewById(R.id.delbutton);
    dbHelper = new DatabaseHelper(this);
    msg = "削除番号：" + "0"  ;
    del_textView.setText(msg);
    setEnabled(delButton,true); 
    
 
    moveButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View arg0) {
    	  textBox = (EditText) findViewById(R.id.movetext);
    	  String str_no = textBox.getText().toString();
    	  if(str_no != null){
    	    int cnt = str_no.length();
    	    int i ;
    	    for(i = 0; i<cnt; i++){
    		    Character c = str_no.charAt(i);
    		    if(!Character.isDigit(c)){
    	          break ;
              }
    	    }
    	    if(i >= cnt){
    	       setEnabled(moveButton,false); 
    	       if(cnt > 0){
          	     SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);   
           	     SharedPreferences.Editor editor = settings.edit();
           	     editor.putString("MOVETRACKNO",str_no);
           	     editor.putString("MOVEON","1");
           	     editor.commit();
           	     String setTrackNo = settings.getString("MOVETRACKNO", "NONE");
    	         move_no = Integer.parseInt(setTrackNo);
    	       }else{
    	    	 move_no = 0;
    	       }
    	       String message = "表示軌跡番号：" + move_no ;
    	       textView.setText(message);		
            }else{
     		   textView.setText("数字を入力してください。");
    	    }
      }
     }	  
    });
    

    delButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View arg0) {
    	  del_textBox = (EditText) findViewById(R.id.deltext);
    	  String str_no = del_textBox.getText().toString();
    	  if(str_no != null){
    	    int cnt = str_no.length();
    	    int i ;
    	    for(i = 0; i<cnt; i++){
    		    Character c = str_no.charAt(i);
    		    if(!Character.isDigit(c)){
    	          break ;
              }
    	    }
    	    if(i >= cnt){
    	       setEnabled(delButton,false); 
    	       if(cnt > 0){
    	        del_no = Integer.parseInt(str_no);
    	       }else{
    	    	   del_no = 0;
    	       }
    	       String message = "削除番号：" + del_no ;
    	       del_textView.setText(message);
				
    	       SQLiteDatabase db = dbHelper.getWritableDatabase();
    	       ScheduleDao scheduleDao = new ScheduleDao(db);
    	       List<Schedule> schedulelist = scheduleDao.selectAll();
               if(schedulelist != null){
    	       for(Schedule schedule:schedulelist) {
    	    	    	  if(schedule == null) continue ;
    	    	    	  if(schedule.getTrackid() == del_no){
    	    	    	    db.delete(TABLE_NAME, "rowid = " + schedule.getRowid(), null);
    	    	    	  }
    	       }
               }
    	      db.close();
    	      db = null ;
            }else{
     		   del_textView.setText("数字を入力してください。");
    	    }
      }
     }	  
    });

  };
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.v("	MoveTrackDailogActivity", "onDestroy was called.");
  
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    Log.v("MoveTrackDailogActivity", "onConfigurationChanged was called.");
  }

}
