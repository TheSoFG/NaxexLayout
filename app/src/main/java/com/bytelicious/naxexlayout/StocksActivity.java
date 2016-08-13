package com.bytelicious.naxexlayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ACER PC on 8/13/2016.
 */
public class StocksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stocks);

        if (savedInstanceState == null) {

            getFragmentManager().beginTransaction().replace(R.id.fl_fragment, new StocksFragment()).commit();

        }

    }

}