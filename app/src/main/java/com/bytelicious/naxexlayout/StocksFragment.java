package com.bytelicious.naxexlayout;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bytelicious.naxexlayout.connectivity.BaseAsyncTask;
import com.bytelicious.naxexlayout.pojos.Stock;
import com.bytelicious.naxexlayout.pojos.StockKVP;
import com.bytelicious.naxexlayout.utils.AvailableStocksDialogFragment;
import com.bytelicious.naxexlayout.utils.RecyclerViewStockAdapter;
import com.bytelicious.naxexlayout.utils.StockDateDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by ACER PC on 8/13/2016.
 */
public class StocksFragment extends Fragment implements View.OnClickListener {

    FloatingActionButton fabAddStock;

    RecyclerView recyclerView;

    ArrayList<Stock> stocks;

    public static final String KEY_AVAILABLE_STOCKS = "availableStocks";
    public static final String KEY_SELECTED_STOCKS = "selectedStocks";

    private static List<String> allowedStockNames = Arrays.asList("EURUSD", "GBPUSD", "USDCHF", "USDJPY", "AUDUSD", "USDCAD", "GBPJPY", "EURGBP", "EURJPY", "AUDCAD");

    private ArrayList<StockKVP> allowedStocks = null;

    public static final int REQUEST_ADD_STOCKS = 1337;

    String sessionId = null;

    Handler refreshStocksHandler = null;
    Runnable getStocks = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_main, container, false);

        setRetainInstance(true);

        if (sessionId == null) {

            SessionIDAsyncTask sessionIDAsyncTask = new SessionIDAsyncTask();

            sessionIDAsyncTask.execute();

        }

        getStocks = new Runnable() {

            @Override
            public void run() {

                StockAsyncTask stockAsyncTask = new StockAsyncTask();
                stockAsyncTask.execute(sessionId);

                refreshStocksHandler.postDelayed(getStocks, 500);

            }

        };

        fabAddStock = (FloatingActionButton) view.findViewById(R.id.fab_add_stock);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_layout);

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

        fixStocks();

        fabAddStock.setOnClickListener(this);

        recyclerView.setAdapter(new RecyclerViewStockAdapter(getActivity(), stocks, new RecyclerViewStockAdapter.StockActionListener() {

            @Override
            public void onBuyClicked(View v, int position) {

                Toast.makeText(getActivity(), "buying " + (position + 1), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onSellClicked(View v, int position) {

                Toast.makeText(getActivity(), "selling " + (position + 1), Toast.LENGTH_SHORT).show();

            }
        }));

        setDeleteOnSwipe();

        fixOrientation();

        return view;

    }

    public void fixStocks() {

        if (stocks != null && !stocks.isEmpty()) {

            if (recyclerView.getAdapter() != null) {

                //FIXME temporary workaround -- the api doesn't filter so I filter manually

                ArrayList<Stock> filteredStock = new ArrayList<>();

                for (StockKVP allowedStock : allowedStocks) {

                    if (allowedStock.picked) {

                        for (Stock receivedStock : stocks) {

                            if (allowedStock.name.equals(receivedStock.displayName)) {

                                filteredStock.add(receivedStock);

                            }

                        }

                    }

                }

                ((RecyclerViewStockAdapter) (recyclerView.getAdapter())).replaceData(filteredStock);
//                ((RecyclerViewStockAdapter) (recyclerView.getAdapter())).replaceData(stocks);

            }

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
                for (StockKVP stockKVP : allowedStocks) {

                    if (removedStock.displayName.equals(stockKVP.name)) {

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

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {

            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        }

    }

    public void startRequestingStocks() {

        refreshStocksHandler = new Handler();

        refreshStocksHandler.post(getStocks);

    }

    @Override
    public void onResume() {

        super.onResume();

        if(sessionId != null) {

            startRequestingStocks();

        }

    }

    @Override
    public void onPause() {

        super.onPause();

        stopRequestingStocks();

    }

    public void stopRequestingStocks() {

        refreshStocksHandler.removeCallbacks(getStocks);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQUEST_ADD_STOCKS:

                if (resultCode == Activity.RESULT_OK) {

                    if (data != null && data.hasExtra(KEY_SELECTED_STOCKS)) {

                        allowedStocks = data.getParcelableArrayListExtra(KEY_SELECTED_STOCKS);
                        fixStocks();

                    }

                }

                break;

            default:

                break;

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
//                availableStocksDialogFragment.setOnDFResult(this);
                availableStocksDialogFragment.setTargetFragment(this, REQUEST_ADD_STOCKS);
                availableStocksDialogFragment.show(getFragmentManager(), null);

                break;

            default:

                break;

        }

    }

    public class SessionIDAsyncTask extends BaseAsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... strings) {

            String sessionId = null;

            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) defaultURL.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<String> cookies = connection.getHeaderFields().get(COOKIES);
            for (String cookie : cookies) {

                if (cookie.contains(ASP_NET_SESSION)) {

                    sessionId = cookie.substring(cookie.indexOf("=") + 1, cookie.indexOf(";"));
                    break;

                }

            }

            return sessionId;

        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            StocksFragment.this.sessionId = s;

            startRequestingStocks();

        }
    }

    public class StockAsyncTask extends BaseAsyncTask<String, Void, ArrayList<Stock>> {

        @Override
        protected ArrayList<Stock> doInBackground(String... strings) {

            try {

                String sessionId = strings[0];

                if (sessionId != null) {

                    // Here would be the place to hand in the specified stocks
                    // as of 13/08/2016 still doesn't work (did work on the 11th)
//                specificStocksURL = new URL(baseURL + "EURUSD,GBPUSD");
                    specificStocksURL = defaultURL;
                    HttpURLConnection specificStockConnection = (HttpURLConnection) specificStocksURL.openConnection();

                    specificStockConnection.setRequestProperty(ASP_NET_SESSION, sessionId);

                    BufferedReader br;
                    if (specificStockConnection.getErrorStream() == null) {

                        br = new BufferedReader(new InputStreamReader(specificStockConnection.getInputStream()));

                        String jsonStocks = "";

                        String stock;

                        while ((stock = br.readLine()) != null) {

                            jsonStocks += stock;

                        }

                        ObjectMapper mapper = new ObjectMapper();
                        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);

                        SimpleModule dateModule = new SimpleModule();
                        dateModule.addDeserializer(Date.class, new StockDateDeserializer());
                        mapper.registerModule(dateModule);
                        JSONArray arrayOfStocks = new JSONArray(jsonStocks.substring(1, jsonStocks.length() - 1));

                        if (arrayOfStocks != null) {

                            Stock[] stocks = mapper.readValue(arrayOfStocks.toString(), Stock[].class);

                            if (stocks != null) {

                                return new ArrayList(Arrays.asList(stocks));

                            }

                        }

                    } else {

                        br = new BufferedReader(new InputStreamReader(specificStockConnection.getErrorStream()));

                        String jsonStocks = "";

                        String stock;

                        while ((stock = br.readLine()) != null) {

                            jsonStocks += stock;

                        }

                        if (!jsonStocks.equals("")) {

                        }

                    }


                }

            } catch (IOException e) {

                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Stock> stocks) {

            super.onPostExecute(stocks);

            StocksFragment.this.stocks = stocks;

            fixStocks();

        }

    }

}
