package jp.asciimw.androidbook.chapter1.androidversionbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class DetailActivity extends FragmentActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        Intent i = getIntent();
        int id = i.getIntExtra("ID", 0);

        FragmentManager fm = getSupportFragmentManager();
        DetailFragment detailFragment = (DetailFragment)fm.findFragmentById(R.id.detailFragment);
        detailFragment.showData(id);
    }
}