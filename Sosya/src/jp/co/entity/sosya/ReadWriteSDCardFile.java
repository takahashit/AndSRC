package jp.co.entity.sosya;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ReadWriteSDCardFile extends Activity {

    private static final String LOGTAG = "FileStorage";

   // private TextView readOutput;
    private TextView fileList ;
    private TextView statusview ;
    private String FileName = null ;
    private String delFileName = null ;
   // public static final int WRITE_ID = 0 ;  
   // public static final int READ_ID = 1 ;
   // public static final int FILE_LIST_ID = 0 ;
   // public static final int FILE_NO_ID = 3 ;
    private EditText textBox;
    private Button setNoButton,delNoButton,listButton,writeButton ;
    private int fileNo = 0, delfileNo = 0 ;
    
    public static final String PREFERENCES_FILE_NAME = "SosyaFile";
	
	private SharedPreferences settings ;   
	private SharedPreferences.Editor editor ;
    
    private void setEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
    }
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.read_write_sdcard_file);
      //  this.readOutput = (TextView) findViewById(R.id.readwrite_sd_output);
        this.fileList = (TextView) findViewById(R.id.sd_filelist);
        this.statusview = (TextView) findViewById(R.id.statusView);
        setNoButton = (Button) findViewById(R.id.no_button);
        setEnabled(setNoButton,true);
        delNoButton = (Button) findViewById(R.id.del_button);
        setEnabled(delNoButton,true);
        listButton = (Button) findViewById(R.id.list_button);
        setEnabled(listButton,true);
        writeButton = (Button) findViewById(R.id.write_button);
        setEnabled(writeButton,true);   
        setNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
          	  textBox = (EditText) findViewById(R.id.file_no_text);
          	  String message = textBox.getText().toString();
          	  if(message != null){
          	    int cnt = message.length();
          	    int i ;
          	    for(i = 0; i<cnt; i++){
          		    Character c = message.charAt(i);
          		    if(!Character.isDigit(c)){
          	          break ;
                    }
          	    }
          	    if(i >= cnt){
          	       setEnabled(setNoButton,false); 
             	   fileNo = Integer.parseInt(message);
             	   setFileName();
                  }else{
           		   //textView.setText("数字を入力してください。");
          	    }
            }
           }	  
          });
        
        delNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
          	  textBox = (EditText) findViewById(R.id.file_no_text);
          	  String message = textBox.getText().toString();
          	  if(message != null){
          	    int cnt = message.length();
          	    int i ;
          	    for(i = 0; i<cnt; i++){
          		    Character c = message.charAt(i);
          		    if(!Character.isDigit(c)){
          	          break ;
                    }
          	    }
          	    if(i >= cnt){
          	       setEnabled(delNoButton,false); 
             	   delfileNo = Integer.parseInt(message);
             	   delFileName();
                  }else{
           		   //textView.setText("数字を入力してください。");
          	    }
            }
           }	  
          });   
        
        
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
          	     //  setEnabled(listButton,false);
          	       outFileList();
          	     //  setEnabled(listButton,true);
           }	  
          });   

        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
          	     //  setEnabled(listButton,false);
          	       writeSDcard();
          	     //  setEnabled(listButton,true);
           }	  
          });   

//        };
        
        
      //  ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
      //          android.R.layout.simple_list_item_1, new String[] { "ＳＤカード書き込み", 
      //  		"ＳＤカード読み込み","ファイルリスト" });
      //  		android.R.layout.simple_list_item_1, new String[] { "ファイルリスト" });
      //  setListAdapter(adapter);
        
    }     
  //      @Override
 //       protected void onListItemClick(ListView l, View v, int position, long id) {
 //         super.onListItemClick(l, v, position, id);
 //         switch (position) {
   //       case WRITE_ID :
   //     	  writeSDcard();
   //     	  break;
   //       case READ_ID :
   //        	  readSDcard();
   //     	  break;
   //       case FILE_LIST_ID :
   //        	  outFileList();
   //     	  break;
   //     //  case FILE_NO_ID :
   //     //   	  setFileName();
   //     //	  break;
   //       }
          
  //      }
        
        @SuppressWarnings("static-access")
		private void writeSDcard(){        
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ScheduleDao scheduleDao = new ScheduleDao(db);
        List<Schedule> schedulelist = scheduleDao.selectAll();
        db.close(); 

        if(schedulelist.size() < 1){
        	return ;
        }
        
        StringBuffer buf = new StringBuffer();

        int daSw = 0 ; 
        int empty = 0 ;      
	    Calendar ds = Calendar.getInstance() ;
		
	    String str_date =  ds.get(ds.YEAR) + "_" ;
	    str_date += (ds.get(ds.MONTH) + 1)  + "_";
	    str_date += (ds.get(ds.DAY_OF_MONTH)) ;
		
		String str_time = "-" + ds.get(ds.HOUR_OF_DAY) ;
		str_time += "_" + ds.get(ds.MINUTE) ;
		str_time += "_" + ds.get(ds.SECOND) ;	
		 
        
        
       //String fileName = "" + System.currentTimeMillis() + ".txt";
        
        
        String fileName = "Sosya-" + str_date + str_time + ".txt";
        
        // create structure /sdcard/unlocking_android and then WRITE
        File sdDir = new File("/sdcard/");
        if (sdDir.exists() && sdDir.canWrite()) {
            File uadDir = new File(sdDir.getAbsolutePath() + "/unlocking_android");
            uadDir.mkdir();
            if (uadDir.exists() && uadDir.canWrite()) {
                //String FileList[] = uadDir.list() ;
                //StringBuffer listbuf = new StringBuffer();
                //for(int i = 0 ; i < FileList.length ; i++){
               // 	listbuf.append(FileList[i]);
               // 	listbuf.append("\n");
               // }
               // this.fileList.setText(listbuf);
                File file = new File(uadDir.getAbsolutePath() + "/" + fileName);
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    Log.e(ReadWriteSDCardFile.LOGTAG, "error creating file", e);
                }

                // now that we have the structure we want, write to the file
                if (file.exists() && file.canWrite()) {
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                      //  fos.write("I fear you speak upon the rack, where men enforced do speak anything.".getBytes());
 
                        if(schedulelist != null){
                        	int recNo = 0;
                            for(Schedule schedule:schedulelist) {
                            	if(schedule == null) continue ;
                                int latitudeE6 = schedule.getLatitudeE6();
                                int longitudeE6 = schedule.getLongitudeE6();
                                String st_lat = Integer.toString(latitudeE6);
                                String st_long = Integer.toString(longitudeE6); 
                            	if(schedule.getSecond() == 0 && schedule.getMinute() == 0 &&
                            			schedule.getHour() == 0){
                                  buf.append("No");
                                  buf.append(schedule.getTrackid()) ;
                                  buf.append(",");
                                  buf.append(st_lat);
                                  buf.append("v");
                                  buf.append(st_long);
                                  buf.append(",");
                                  buf.append(schedule.getDate());
                                  buf.append(",");
                                  buf.append(schedule.getTime());
                                  buf.append("\n");
                        		  daSw = 1;
                        		  empty = 1;
                        		  recNo += 1 ;
                            	}else{
                                  if(daSw == 1){
                            		if(schedule.getTrackid() == -1){
                            		  daSw = 0;
                            		  recNo = 0;
                                      buf.append("end");  
                                      buf.append(",");
                                      buf.append(st_lat);
                                      buf.append("v");
                                      buf.append(st_long);
                                      buf.append(",");
                                      buf.append(schedule.getDistance());
                                      buf.append(",");
                                      buf.append(schedule.getHour());
                                      buf.append(":");
                                      buf.append(schedule.getMinute());
                                      buf.append(":");
                                      buf.append(schedule.getSecond());
                                      buf.append("\n");               
                                    }else{
                                    	String st_recNo = Integer.toString(recNo);
                                        buf.append(st_recNo);
                                        buf.append(",");
                                        buf.append(st_lat);
                                        buf.append("v");
                                        buf.append(st_long);
                                        buf.append(",");
                                        buf.append(schedule.getDistance());
                                        buf.append(",");
                                        buf.append(schedule.getHour());
                                        buf.append(":");
                                        buf.append(schedule.getMinute());
                                        buf.append(":");
                                        buf.append(schedule.getSecond());
                                        buf.append("\n");               
                                        recNo += 1;
                                    }                        	    
                                  }
                            	}	
                            }
                            
                            }
                            
                            if(empty == 0) {
                                buf.append("データがありません。");
                            }
                           String w_data = buf.toString();
                           fos.write(w_data.getBytes());               
                        
                    } catch (FileNotFoundException e) {
                        Log.e(ReadWriteSDCardFile.LOGTAG, "ERROR", e);
                    } catch (IOException e) {
                        Log.e(ReadWriteSDCardFile.LOGTAG, "ERROR", e);
                    } finally {
                        if (fos != null) {
                            try {
                                fos.flush();
                                fos.close();
                                this.statusview.setText("書込:" + fileName);
                            } catch (IOException e) {
                                // swallow
                            }
                        }
                    }
                } else {
                   Log.e(ReadWriteSDCardFile.LOGTAG, "error writing to file");
                }

            } else {
                Log.e(ReadWriteSDCardFile.LOGTAG, "ERROR, unable to write to /sdcard/unlocking_android");
            }
        } else {
            Log
                .e(
                    ReadWriteSDCardFile.LOGTAG,
                    "ERROR, /sdcard path not available "
                        + "(did you create an SD image with the mksdcard tool, and start emulator with -sdcard <path_to_file> option?");
        }
        
        }
    private void  readSDcard() {
      

	    settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0);   
	    editor = settings.edit();
        String getTrackId = settings.getString("TRACKID", "0");
        int track_id = Integer.parseInt(getTrackId);
        if(track_id < 1){
     	 track_id = 1 ;
     	 editor.putString("TRACKID","1");
     	 editor.commit();
        }

        // READ
        File rFile = new File("/sdcard/unlocking_android/" + FileName);
      
        if (rFile.exists() && rFile.canRead()) {
        	
            DatabaseHelper dbHelper = new DatabaseHelper(this);
      	    SQLiteDatabase db = dbHelper.getWritableDatabase();
      	    ScheduleDao scheduleDao = new ScheduleDao(db);
      	    
            FileInputStream fis = null;
            //StringBuffer buf = new StringBuffer();
            String readData = null;
            try {
                fis = new FileInputStream(rFile);
                byte[] reader = new byte[fis.available()];
                while (fis.read(reader) != -1) {
                    readData = new String(reader);
                }
            } catch (IOException e) {
                Log.e(ReadWriteSDCardFile.LOGTAG, e.getMessage(), e);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        // swallow
                    }
                }
            }
            // DB write
            String str_time = null, str_date = null;  
            Schedule sdSchedule = new Schedule();	
            String oneRecord[] = readData.split("\n");
           // StringBuffer debug = new StringBuffer();
            for(int i = 0; i < oneRecord.length ; i++){
                String  soneData[] = oneRecord[i].split(",");
                int p  = 0 ;
                if(soneData[0].indexOf("No",p) > -1){
                    sdSchedule.setTrackid(track_id) ;
                  //  debug.append("No" +track_id + ",");
                    String  foneData[] = oneRecord[i].split(",");
                    str_date = foneData[2];
                    str_time = foneData[3];
                    sdSchedule.setDate(str_date);
                    sdSchedule.setTime(str_time);
                   	String pos[] = foneData[1].split("v");
                   	int latitudeE6 = Integer.parseInt(pos[0]);
                    sdSchedule.setLatitudeE6(latitudeE6);
                    int longitudeE6 = Integer.parseInt(pos[1]);
                    sdSchedule.setLongitudeE6(longitudeE6);
                    sdSchedule.setHour(0);
                    sdSchedule.setMinute(0);
                    sdSchedule.setSecond(0);
                    sdSchedule.setDistance(0);
                    sdSchedule.setSpeed(0);
                    scheduleDao.insert(sdSchedule);
               	    continue;
                }
                if(soneData[0].indexOf("end",p) > -1){
                   // debug.append("end" +track_id + ",");
                    sdSchedule.setTrackid(-1) ;
                   	String epos[] = soneData[1].split("v");
                   	int elatitudeE6 = Integer.parseInt(epos[0]);
                    sdSchedule.setLatitudeE6(elatitudeE6);
                    int elongitudeE6 = Integer.parseInt(epos[1]);
                    sdSchedule.setLongitudeE6(elongitudeE6);
                    String etimeData[] = soneData[3].split(":");
                    int all_hour = Integer.parseInt(etimeData[0]);
                    sdSchedule.setHour(all_hour);
                    int all_minute = Integer.parseInt(etimeData[1]);
               	    sdSchedule.setMinute(all_minute);
                    int all_second = Integer.parseInt(etimeData[2]);
               	    sdSchedule.setSecond(all_second);
                    int all_distance = Integer.parseInt(soneData[2]);
               	    sdSchedule.setDistance(all_distance);
               	  	double all_sec = all_hour * 3600.0 + all_minute * 60.0 + all_second ;
                   	double kmPhour = (double) (all_distance / all_sec * 3.6) ;
                   	int int_kmPhour = (int)  kmPhour * 100;
                   	kmPhour = int_kmPhour / 100 ;
               	    sdSchedule.setSpeed(int_kmPhour);
                    scheduleDao.insert(sdSchedule);
                    
            		track_id += 1 ;
            		String str_track_id = "" ;
            		str_track_id += track_id ;
               	    editor.putString("TRACKID",str_track_id);
               	    editor.commit();
               	    continue;
                }
            //    debug.append("data" +track_id + ",");
                sdSchedule.setTrackid(track_id) ;
                sdSchedule.setDate(str_date);
                sdSchedule.setTime(str_time);  	
                String spos[] = soneData[1].split("v");
               	int slatitudeE6 = Integer.parseInt(spos[0]);
                sdSchedule.setLatitudeE6(slatitudeE6);
                int slongitudeE6 = Integer.parseInt(spos[1]);
                sdSchedule.setLongitudeE6(slongitudeE6);
                String timeData[] = soneData[3].split(":");
                int all_hour = Integer.parseInt(timeData[0]);
                sdSchedule.setHour(all_hour);
                int all_minute = Integer.parseInt(timeData[1]);
           	    sdSchedule.setMinute(all_minute);
                int all_second = Integer.parseInt(timeData[2]);
           	    sdSchedule.setSecond(all_second);
                int all_distance = Integer.parseInt(soneData[2]);
           	    sdSchedule.setDistance(all_distance);
           	  	double all_sec = all_hour * 3600.0 + all_minute * 60.0 + all_second ;
               	double kmPhour = (double) (all_distance / all_sec * 3.6) ;
               	int int_kmPhour = (int)  kmPhour * 100;
               	kmPhour = int_kmPhour / 100 ;
           	    sdSchedule.setSpeed(int_kmPhour);
                scheduleDao.insert(sdSchedule);
            }
            db.close();
        } else {
           this.statusview.setText(FileName + "がSDカードにありません。");
        }
    }
        
    private void outFileList(){      
       File sdDir = new File("/sdcard/");
       if (sdDir.exists()) {
          File uadDir = new File(sdDir.getAbsolutePath() + "/unlocking_android");
          if (uadDir.exists() && uadDir.canRead()) {
                    String FileList[] = uadDir.list() ;
                    StringBuffer listbuf = new StringBuffer();
                    for(int i = 0 ; i < FileList.length ; i++){
                    	listbuf.append("" + (i + 1) + ":");
                    	listbuf.append(FileList[i]);
                    	listbuf.append("\n");
                    }
                    this.fileList.setText(listbuf);
 
          }
        }  
    }
    
    
    private void setFileName(){    
 
        File sdDir = new File("/sdcard/");
        if (sdDir.exists()) {
           File uadDir = new File(sdDir.getAbsolutePath() + "/unlocking_android");
           if (uadDir.exists() && uadDir.canRead()) {
              String FileList[] = uadDir.list() ; 
              int ii = fileNo -1 ;
              FileName = FileList[ii] ;
              File rFile = new File("/sdcard/unlocking_android/" + FileName);
              if (rFile.exists() && rFile.canRead()) {
                this.statusview.setText("読込:" + fileNo + "." + FileName);
                readSDcard();
              }else{
                this.statusview.setText(FileName + "がSDカードにありません。");
              }
           }else{
               this.statusview.setText( uadDir + "がSDカードにありません。");
           }
       
         }  
        setEnabled(setNoButton,true);
    	
    }
    
   private void delFileName(){    
        File sdDir = new File("/sdcard/");
        if (sdDir.exists()) {
           File uadDir = new File(sdDir.getAbsolutePath() + "/unlocking_android");
           if (uadDir.exists()) {
              String FileList[] = uadDir.list() ; 
              int ii = delfileNo -1 ;
              delFileName = FileList[ii] ;
 
           }
        
           File dFile = new File(uadDir + "/" + delFileName);
         
           if (dFile.exists()) {
             dFile.delete();
             this.statusview.setText("削除:" + delfileNo + "." + delFileName);
           } else {
             this.statusview.setText(delFileName + "がSDカードにありません。");
           }
        }
        setEnabled(delNoButton,true);
   }
   
}
