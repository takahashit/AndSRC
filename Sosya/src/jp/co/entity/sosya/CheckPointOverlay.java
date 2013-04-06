package jp.co.entity.sosya;

import java.util.ArrayList;
import java.util.List;

import jp.co.entity.sosya.GPSRecord;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

import android.content.Context;

public class CheckPointOverlay extends ItemizedOverlay<OverlayItem>{
	public List<OverlayItem> items = new ArrayList<OverlayItem>();
	private ArrayList<GeoPoint> gpl = new ArrayList<GeoPoint>() ;
	private ArrayList<Integer> trnl = new ArrayList<Integer>() ;
	private OverlayItem selectedItem = null;
	private OverlayItem selectedItem2 = null;
	private int lastID = 0;
	private boolean stopSw = false ;
	private boolean trackOn = false ;
	

	
	public CheckPointOverlay(Drawable d) {
		super(d);
		// 初期追加にエラーが発生しないよう、populateを実行
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return items.get(i);

	}

	@Override
	public int size() {
		return items.size();
	}

	public void addPoint(OverlayItem item) {
		items.add(item);
		populate();
	}
	
	
	public void setPos(GeoPoint get_gp,int trackNo,boolean flag) {
		gpl.add(get_gp); 
		trnl.add(trackNo);
		trackOn = true ;
		populate();
	}
	
	public void offPos() {
		trackOn = false;
		populate();
	}
	
	public void clear() {
		items.clear();
		selectedItem = null;
		lastID = 0 ;
		populate();
	}
	

	public void startStatus() {
		stopSw = false ;
		selectedItem = null; // タップなしで表示するのを防ぐ
		lastID = 0 ;         //
		populate();
	}

	public void stopStatus() {
		stopSw = true ;
		populate();
	}

	@Override
	protected boolean onTap(int index) {
	   selectedItem = items.get(index);
	   if(lastID < index){
		    lastID = index ;
       }
	  return false;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		int id  ;
		float x2 = (float)0.0 ;
		float y2 = (float)0.0;
		GeoPoint p2 = null ;
		int color1 = 255 ;
		int color2 = 0 ;
		// CheckPointOverlayのマーカー描画
		super.draw(canvas, mapView, false); // nouse shadow    
		int chenge = 0;
		if(trackOn == true){
			
		   for(int i = 0; i < gpl.size(); i++){
		     GeoPoint trackGeo  = gpl.get(i);
		     if(trackGeo == null) continue;
		     
		     Point trackp = mapView.getProjection().toPixels(trackGeo, null);
		     Integer in_trackNo = trnl.get(i);
		    
		     Paint paint;
		     paint = new Paint();
		     paint.setAntiAlias(true);
		     if(chenge < 0){
			    color1 -= 64 ;
			    color2 += 32 ;
			    if(color1 < 65)color1 = 255;
			    if(color2 > 223)color2 = 0 ;
		     }
		    // paint.setARGB(255, 255, 32, 0); // ｵﾚﾝｼﾞ
		     paint.setARGB(255, color1, color2, 0);
		     if(chenge != in_trackNo && in_trackNo > 0 ){
			    String str_no = "No." + in_trackNo ;
			    canvas.drawText(str_no, trackp.x, trackp.y + 22 , paint);
			    x2 = trackp.x ;
			    y2 = trackp.y ; 
		     }else{
		      Path linePath  = new Path();
		      paint.setStyle(Paint.Style.STROKE);
		      paint.setStrokeWidth(3);
		      linePath.moveTo(trackp.x,trackp.y);
		      linePath.lineTo(x2,y2);
			  canvas.drawPath(linePath , paint);
			  x2 = trackp.x ;
			  y2 = trackp.y ;  
		     }
		     chenge = in_trackNo ;
		   }
		   
		}
		
		// タップされた点リストを描画
		if (stopSw && !shadow && selectedItem != null && lastID > 0) { // nouse shadow

		  for(id = 0;id <= lastID ; id++ ){
			selectedItem = items.get(id);
			if(id == lastID ){
			 selectedItem2 = items.get(id);
			}else{
			 selectedItem2 = items.get(id+1);
		    }
			GeoPoint p = selectedItem.getPoint();
			Point pos = mapView.getProjection().toPixels(p, null);
			if(!shadow && selectedItem2 != null){ // nouse shadow
			   p2 = selectedItem2.getPoint();
			  if(p2 != null){
			     Point pos2 = mapView.getProjection().toPixels(p2, null);
			     x2 = pos2.x + 8 ;
			     y2 = pos2.y + 18 ; // 2010.01.13
			  }

			}

			Paint paint;
			paint = new Paint();
			paint.setAntiAlias(true);
			//paint.setARGB(255, 255, 0, 0); red
			//paint.setARGB(255, 0, 128, 0); green
			//paint.setARGB(255, 128, 128, 0); ｶｰｷ
			//paint.setARGB(255, 128, 0, 0); 茶
			//paint.setARGB(255, 128, 0, 255); 紫
			paint.setARGB(255, 255, 32, 0); // ｵﾚﾝｼﾞ
			
			// 取得した画面上の位置に、描画を行う
			String title = selectedItem.getTitle();
			if(title != null){
			   canvas.drawText(title, pos.x, pos.y + 20 , paint);
			
			   String snipet = selectedItem.getSnippet();
    	        if(snipet != null){
	    		  canvas.drawText(snipet, pos.x - 18 , pos.y + 30, paint);
	           }
	        }
			if(title != "stop"){ 
			  if(!shadow && selectedItem2 != null && pos != null && p2 != null){ // nouse shadow
			    int x = pos.x + 8 ;
			    int y = pos.y + 18 ; // 2010.01.13
			    canvas.drawLine((float)x,(float)y ,x2,y2 , paint); // 2010.01.13
			  }
			}

		 }

		}
 
	        // 履歴を表示
	        if (displayHistory) { // (1) from
	            drawActionHistory(canvas, mapView, shadow);
	        } // (1) to
	}
	
  	private static boolean displayHistory = false;

 	private static List<GPSRecord> locationRecordList;
 	    	 	 
    /**
     * 行動履歴を表示するか設定する
     * 
     * @param displayActionHistory
     */
    public static void setDisplayActionHistory(boolean displayActionHistory) { // (1)
    	displayHistory = displayActionHistory;
    }
	
	  /**
     * 行動履歴を表示しているか
     * 
     * @return
     */
    public boolean isDisplayActionHistory() {
        return displayHistory;
    }

	   /*
     * 行動履歴を設定する
     */
    public static void setActionHistory(List<GPSRecord> list) {
        locationRecordList = list;
    } // (1) to

    /**
     * 行動履歴を描画する
     * 
     * @param canvas
     * @param mapView
     * @param shadow
     */
    private void drawActionHistory(Canvas canvas, MapView mapView, // (2) from
            boolean shadow) {

        Projection projection = mapView.getProjection();

        // 描画するペンを設定
        Paint paint = new Paint();
        paint.setARGB(128, 0, 255, 0);
        paint.setStrokeWidth(3.0f);
        paint.setAntiAlias(true);

        int cnt = 0;
        float oldpx = 0, oldpy = 0;
        for (GPSRecord loc : locationRecordList) {

            double lat = loc.getLatitude() * 1E6;
            double lon = loc.getLongitude() * 1E6;

            GeoPoint geoPoint = new GeoPoint((int) lat, (int) lon);

            // 画面の解像度にあわせて位置を変換する
            Point point = new Point();
            projection.toPixels(geoPoint, point);

            if (cnt > 0) {
                // 線を描画する
                canvas.drawLine(oldpx, oldpy, point.x, point.y, paint);
            }
            
            cnt++;
            oldpx = point.x;
            oldpy = point.y;
        }
    } // (2) to
    
    
}
