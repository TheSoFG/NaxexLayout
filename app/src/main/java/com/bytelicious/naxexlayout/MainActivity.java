package com.bytelicious.naxexlayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.bytelicious.naxexlayout.pojos.Stock;
import com.bytelicious.naxexlayout.pojos.StockKVP;
import com.bytelicious.naxexlayout.utils.AvailableStocksDialogFragment;
import com.bytelicious.naxexlayout.utils.RecyclerViewStockAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AvailableStocksDialogFragment.OnDFResult {

    FloatingActionButton fabAddStock;

    RecyclerView recyclerView;

    ArrayList<Stock> stocks;
    ArrayList<Stock> mockStocks;

    public static final String KEY_AVAILABLE_STOCKS = "availableStocks";
    public static final String KEY_SELECTED_STOCKS = "selectedStocks";

    private static List<String> allowedStockNames = Arrays.asList("EURUSD", "GBPUSD", "USDCHF", "USDJPY", "AUDUSD", "USDCAD", "GBPJPY", "EURGBP", "EURJPY", "AUDCAD");

    private ArrayList<StockKVP> allowedStocks = null;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        StockAsyncTask stockAsyncTask = new StockAsyncTask();
//
//        stockAsyncTask.execute();

        fabAddStock = (FloatingActionButton) findViewById(R.id.fab_add_stock);
        recyclerView = (RecyclerView) findViewById(R.id.rv_layout);

        if (savedInstanceState != null) {

            allowedStocks = savedInstanceState.getParcelableArrayList(KEY_SELECTED_STOCKS);
            mockStocks = savedInstanceState.getParcelableArrayList("mock");

        }

        if (allowedStocks == null) {

            allowedStocks = new ArrayList<>();

            StockKVP stockKVP = null;

            for (int i = 0; i < allowedStockNames.size(); ++i) {

                stockKVP = new StockKVP();
                stockKVP.name = allowedStockNames.get(i);
                stockKVP.picked = i < 4;
                allowedStocks.add(stockKVP);

            }

        }

        stocks = new ArrayList<>();
        if(mockStocks == null) {

            mockStocks = new ArrayList<>();

            Stock stock = new Stock();
            stock.displayName = allowedStockNames.get(0);
            stock.ask = 1.2;
            stock.bid = 2.3;
            mockStocks.add(stock);

            Stock stock1 = new Stock();
            stock1.displayName = allowedStockNames.get(1);
            stock1.ask = 4.5;
            stock1.bid = 5.6;
            mockStocks.add(stock1);

            Stock stock2 = new Stock();
            stock2.displayName = allowedStockNames.get(2);
            stock2.ask = 7.8;
            stock2.bid = 8.9;
            mockStocks.add(stock2);

            Stock stock3 = new Stock();
            stock3.displayName = allowedStockNames.get(3);
            stock3.ask = 2347.8;
            stock3.bid = 8.2349;
            mockStocks.add(stock3);

            Stock stock4 = new Stock();
            stock4.displayName = allowedStockNames.get(4);
            stock4.ask = 7.2348;
            stock4.bid = 234238.9;
            mockStocks.add(stock4);

            stock = new Stock();
            stock.displayName = allowedStockNames.get(5);
            stock.ask = 7345.68;
            stock.bid = 448.2349;
            mockStocks.add(stock);

            stock = new Stock();
            stock.displayName = allowedStockNames.get(6);
            stock.ask = 47.88;
            stock.bid = 8432.923;
            mockStocks.add(stock);

            stock = new Stock();
            stock.displayName = allowedStockNames.get(7);
            stock.ask = 57.8;
            stock.bid = 678.94;
            mockStocks.add(stock);

            stock = new Stock();
            stock.displayName = allowedStockNames.get(8);
            stock.ask = 78.98;
            stock.bid = 88.69;
            mockStocks.add(stock);

            Stock stock10 = new Stock();
            stock10.displayName = allowedStockNames.get(9);
            stock10.ask = 1237.1238;
            stock10.bid = 123128.1239;
            mockStocks.add(stock10);

        }

        fixStocks();

        fabAddStock.setOnClickListener(this);

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

        setDeleteOnSwipe();

        fixOrientation();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(KEY_SELECTED_STOCKS, allowedStocks);
        outState.putParcelableArrayList("mock", mockStocks);

    }

    public void fixStocks() {

        stocks.clear();

        for(StockKVP allowedStock : allowedStocks) {

            if(allowedStock.picked) {

                for(Stock stock : mockStocks) {

                    if(stock.displayName.equals(allowedStock.name)) {

                        stocks.add(stock);

                    }

                }

            }

        }

        if(recyclerView.getAdapter() != null) {

            ((RecyclerViewStockAdapter) (recyclerView.getAdapter())).replaceData(stocks);

        }

    }

    public void setDeleteOnSwipe() {

        ItemTouchHelper.SimpleCallback swipeToDeleteCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {

                return false;

            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {

                Stock removedStock = ((RecyclerViewStockAdapter) (recyclerView.getAdapter())).removeAtPosition(viewHolder.getAdapterPosition());
                for(StockKVP stockKVP : allowedStocks) {

                    if(removedStock.displayName.equals(stockKVP.name)) {

                        stockKVP.picked = false;

                    }

                }

            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);

        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    public void fixOrientation() {

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            recyclerView.setLayoutManager(new LinearLayoutManager(this));


        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {

            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fab_add_stock:

                AvailableStocksDialogFragment availableStocksDialogFragment = new AvailableStocksDialogFragment();
                Bundle args = new Bundle();
                args.putParcelableArrayList(KEY_AVAILABLE_STOCKS, allowedStocks);
                availableStocksDialogFragment.setArguments(args);
                availableStocksDialogFragment.setOnDFResult(this);
                availableStocksDialogFragment.show(getFragmentManager(), null);

                break;

            default:

                break;

        }

    }

    @Override
    public void onDFResult(Intent data) {

        if (data != null && data.hasExtra(KEY_SELECTED_STOCKS)) {

            allowedStocks = data.getParcelableArrayListExtra(KEY_SELECTED_STOCKS);
            fixStocks();

        }

    }

}