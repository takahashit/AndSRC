package jp.asciimw.androidbook.chapter1.androidversionbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class MainActivity extends FragmentActivity implements
        MainFragment.OnWordItemClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    private void showDetail(int param) {
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra("ID", param);
        startActivity(i);
    }

    @Override
    public void onButtonItemClick(int id) {
        if (isTabletSize()) {
            FragmentManager fm = getSupportFragmentManager();
            DetailFragment detailFragment = (DetailFragment) fm
                    .findFragmentById(R.id.detailFragment);
            detailFragment.showData(id);
        } else {
            showDetail(id);
        }
    }

    public boolean isTabletSize() {
        return getResources().getBoolean(R.bool.isTabletSize);
    }
}