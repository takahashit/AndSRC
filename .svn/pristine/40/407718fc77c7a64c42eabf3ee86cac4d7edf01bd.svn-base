package net.npaka.fragmentex;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

//詳細アクティビティ
public class DetailActivity extends Activity{
    //アクティビティ生成時に呼ばれる(6)
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        //インテントからのパラメータ取得
        int index=0;
        Bundle extras=getIntent().getExtras();
        if (extras!=null) index=extras.getInt("index");
        
        //レイアウトの生成
        FrameLayout layout=new FrameLayout(this);
        layout.setId(R.id.details);
        setContentView(layout);
        
        //アクティビティへのフラグメントの配置(8)
        DetailsFragment fragment=DetailsFragment.newInstance(index);
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.replace(R.id.details,fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
    
    //詳細フラグメントの生成(7)
    public static class DetailsFragment extends Fragment {
        //インスタンス生成時に呼ばれる(6)
        public static DetailsFragment newInstance(int index) {
            DetailsFragment fragment=new DetailsFragment();
            Bundle bundle=new Bundle();
            bundle.putInt("index",index);
            fragment.setArguments(bundle);//(8)
            return fragment;
        }

        //フラグメントのビュー生成時に呼ばれる
        @Override
        public View onCreateView(LayoutInflater inflater,
            ViewGroup container,Bundle bundle) {
            if (container==null) return null;
            TextView textView=new TextView(getActivity());
            textView.setText("ページ"+ 
                getArguments().getInt("index",0)+"の詳細");//(8)
            textView.setTextSize(24);
            return textView;
        }
    }    
}

