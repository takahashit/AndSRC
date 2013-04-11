package jp.asciimw.androidbook.chapter1.androidversionbook;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailFragment extends Fragment {
    private TextView jaNameText = null;
    private TextView verNameText = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.detail_fragment,
                container);
        jaNameText = (TextView) view.findViewById(R.id.textplatformJaName);
        verNameText = (TextView) view.findViewById(R.id.textplatformApiLevel);
        return view;
    }

    public void showData(int param) {
        String jaName = getResources().getStringArray(R.array.platformJaName)[param];
        jaNameText.setText(jaName);
        String verName = getResources()
                .getStringArray(R.array.platformApiLevel)[param];
        verNameText.setText(verName);
    }
}