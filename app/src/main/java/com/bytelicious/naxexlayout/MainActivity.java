package com.bytelicious.naxexlayout;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.bytelicious.naxexlayout.pojos.Stock;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<Stock> stocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rv_layout);

        stocks = new ArrayList<>();
        Stock stock = new Stock();
        stock.ask = 1.2;
        stock.bid = 2.3;
        stocks.add(stock);
        stock = new Stock();
        stock.ask = 4.5;
        stock.bid = 5.6;
        stocks.add(stock);
        stock = new Stock();
        stock.ask = 7.8;
        stock.bid = 8.9;
        stocks.add(stock);

        recyclerView.setAdapter(new RecyclerViewStockAdapter(this, stocks, new RecyclerViewStockAdapter.StockActionListener() {

            @Override
            public void onBuyClicked(View v, int position) {

                Toast.makeText(MainActivity.this, "buying " + (position + 1), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onSellClicked(View v, int position) {

                Toast.makeText(MainActivity.this, "selling " + (position + 1), Toast.LENGTH_SHORT).show();

            }
        }));

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            recyclerView.setLayoutManager(new LinearLayoutManager(this));


        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {

            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        }

    }

}