package net.npaka.optionmenuex;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.LinearLayout;

//オプションメニューとアクションバー
public class OptionMenuEx extends Activity {
    //メニューアイテムID
    private static final int 
        MENU_ITEM0=0,
        MENU_ITEM1=1,
        MENU_ITEM2=2,
        MENU_ITEM3=3;

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        //アクションバーの有効化
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        //レイアウトの生成
        LinearLayout layout=new LinearLayout(this);
        layout.setBackgroundColor(Color.rgb(255,255,255));
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout); 
    }

    //オプションメニューの生成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        //オプションメニューへのアイテム0の追加
        MenuItem item0=menu.add(0,MENU_ITEM0,0,"アイテム0");
        item0.setIcon(android.R.drawable.ic_menu_camera);
        
        //オプションメニューへのアイテム1の追加
        MenuItem item1=menu.add(0,MENU_ITEM1,0,"アイテム1");
        item1.setIcon(android.R.drawable.ic_menu_call);
        
        //アクションバーへのメニューアイテム2の追加
        MenuItem item2=menu.add(0,MENU_ITEM2,0,"アイテム2");
        item2.setIcon(android.R.drawable.ic_menu_add);
        item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        //アクションバーへのメニューアイテム3の追加
        MenuItem item3=menu.add(0,MENU_ITEM3,0,"アイテム3");
        item3.setIcon(android.R.drawable.ic_menu_delete);
        item3.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }    

    //メニューアイテム選択イベントの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM0:
                showDialog(this,"","アイテム0を押した");
                return true;
            case MENU_ITEM1:
                showDialog(this,"","アイテム1を押した");
                return true;
            case MENU_ITEM2:
                showDialog(this,"","アイテム2を押した");
                return true;
            case MENU_ITEM3:
                showDialog(this,"","アイテム3を押した");
                return true;
        }
        return true;
    }    
    
    //ダイアログの表示
    private static void showDialog(Context context,String title,String text) {
        AlertDialog.Builder ad=new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(text);
        ad.setPositiveButton("OK",null);
        ad.show();
    }
}