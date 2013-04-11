package net.npaka.fragmentex;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

//フラグメントの利用
public class FragmentEx extends Activity {
    //アクティビティ起動時に呼ばれる
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        //レイアウトの指定(1)
        setContentView(R.layout.main);
    }
    
    //リストフラグメントの生成(2)
    public static class TitlesFragment extends ListFragment {
        private int pos=-1;

        //アクティビティ生成完了時に呼ばれる(3)
        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);
            setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1, 
                new String[]{"ページ0","ページ1","ページ2"}));
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            if (isTablet(getActivity())) showDetails(0);
        }

        //リスト要素クリック時に呼ばれる
        @Override
        public void onListItemClick(ListView l,View v,int pos,long id) {
            showDetails(pos);
        }

        //詳細の表示
        private void showDetails(int index) {
            Context context=getActivity().getApplication();
            
            //フラグメントの切り換え(4)
            if (isTablet(context)) {
                getListView().setItemChecked(index,true);
                if (pos==index) return;
                DetailActivity.DetailsFragment fragment=
                    DetailActivity.DetailsFragment.newInstance(index);
                FragmentTransaction ft=getFragmentManager().beginTransaction();
                ft.replace(R.id.details,fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                pos=index;
            }
            //アクティビティの起動
            else {
                getListView().setItemChecked(index,false);
                Intent intent=new Intent(context,DetailActivity.class);
                intent.putExtra("index",index);
                getActivity().startActivity(intent);
            }
        }
    }
    
    //タブレットかどうかの取得(5)
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout&
            Configuration.SCREENLAYOUT_SIZE_MASK)==
            Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
}
