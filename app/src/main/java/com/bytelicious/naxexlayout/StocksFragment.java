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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bytelicious.naxexlayout.connectivity.BaseAsyncTask;
import com.bytelicious.naxexlayout.pojos.Stock;
import com.bytelicious.naxexlayout.pojos.StockKVP;
import com.bytelicious.naxexlayout.utils.AvailableStocksDialogFragment;
import com.bytelicious.naxexlayout.utils.GridItemDecorator;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by ACER PC on 8/13/2016.
 * Main container
 */
public class StocksFragment extends Fragment implements View.OnClickListener {

    FloatingActionButton fabAddStock;

    RecyclerView recyclerView;
    int columnsForRecyclerView = 2;

    // stocks used to be sent to the RecyclerView adapter
    ArrayList<Stock> stocks;

    // used for debugging purposes
    Random random = new Random();

    // used for traversing data from and to the DialogFragment
    // that is shown after the FAB is clicked
    public static final String KEY_AVAILABLE_STOCKS = "availableStocks";
    public static final String KEY_SELECTED_STOCKS = "selectedStocks";
    public static final int REQUEST_ADD_STOCKS = 1337;

    private static List<String> allowedStockNames = Arrays.asList("EURUSD", "GBPUSD", "USDCHF", "USDJPY", "AUDUSD", "USDCAD", "GBPJPY", "EURGBP", "EURJPY", "AUDCAD");

    // the stocks' names and their state (picked/unpicked)
    private ArrayList<StockKVP> allowedStocks = null;

    // ASP.NET_SessionId
    String sessionId = null;

    // used to refresh data every XXX(500ms) amount of time
    Handler refreshStocksHandler = null;
    Runnable getStocks = null;
    private static final int delayBetweenRefreshes = 500;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stocks, container, false);

        // preserving fragment instance on orientation change (also doesn't break AsyncTasks)
        setRetainInstance(true);

        //first we retrieve the sessionID
        if (sessionId == null) {

            SessionIDAsyncTask sessionIDAsyncTask = new SessionIDAsyncTask();

            sessionIDAsyncTask.execute();

        }

        fabAddStock = (FloatingActionButton) view.findViewById(R.id.fab_add_stock);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_layout);

        stocks = new ArrayList<>();

        // defaulting to first 4 stocks being available
        // By default the application starts with EURUSD, GBPUSD, USDCHF, USDJPY.
        if (allowedStocks == null) {

            allowedStocks = new ArrayList<>();

            StockKVP stockKVP;

            for (int i = 0; i < allowedStockNames.size(); ++i) {

                stockKVP = new StockKVP();
                stockKVP.name = allowedStockNames.get(i);
                stockKVP.picked = i < 4;
                allowedStocks.add(stockKVP);

            }

        }

        // the runnable that is refreshing the stocks
        getStocks = new Runnable() {

            @Override
            public void run() {

                StockAsyncTask stockAsyncTask = new StockAsyncTask();

                ArrayList<String> inputs = new ArrayList<>();

                // sending the sessionId together with the stocks we gathered
                inputs.add(sessionId);

                for (StockKVP stockKVP : allowedStocks) {

                    if (stockKVP.picked) {

                        inputs.add(stockKVP.name);

                    }

                }

                stockAsyncTask.execute(inputs.toArray(new String[inputs.size()]));

                refreshStocksHandler.postDelayed(getStocks, delayBetweenRefreshes);

            }

        };

        reefreshStocks(recyclerView, stocks);

        setDeleteOnSwipe();

        setDifferentLayouts();

        fabAddStock.setOnClickListener(this);

        recyclerView.setAdapter(new RecyclerViewStockAdapter(getActivity(), stocks, new RecyclerViewStockAdapter.StockActionListener() {

            @Override
            public void onBuyClicked(View v, String stockName, double price) {

                Toast.makeText(getActivity(), String.format(getResources().getString(R.string.buying_toast), stockName, String.valueOf(price)), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onSellClicked(View v, String stockName, double price) {

                Toast.makeText(getActivity(), String.format(getResources().getString(R.string.selling_toast), stockName, String.valueOf(price)), Toast.LENGTH_SHORT).show();

            }

        }));

        return view;

    }

    /**
     * Refreshing the stocks by re-adding them in the recyclerview's adapter
     */
    public void reefreshStocks(RecyclerView rv, ArrayList<Stock> newStocks) {

        if (newStocks != null && !newStocks.isEmpty()) {

            if (rv.getAdapter() != null) {

                // temporary workaround -- the api doesn't filter so I filter manually
                //-------------------------- to_be_removed --------------------------
                ArrayList<Stock> filteredStock = new ArrayList<>();

                for (StockKVP allowedStock : allowedStocks) {

                    if (allowedStock.picked) {

                        for (Stock receivedStock : newStocks) {

                            if (allowedStock.name.equals(receivedStock.displayName)) {

                                filteredStock.add(receivedStock);

                            }

                        }

                    }

                }

                ((RecyclerViewStockAdapter) (recyclerView.getAdapter())).replaceData(filteredStock);
                //-------------------------- to_be_removed --------------------------

//                ((RecyclerViewStockAdapter) (recyclerView.getAdapter())).replaceData(newStocks);

            }

        }

    }

    /**
     * Adding delete on swipe to the {@link RecyclerView}
     */
    public void setDeleteOnSwipe() {

        ItemTouchHelper.SimpleCallback swipeToDeleteCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {

                return false;

            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {

                // stock is removed from the adapter and the allowed stocks are also refreshed (its status)
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

    /**
     * Depending on the orientation of the screen there are two different layouts.
     * <br> The application should have one screen with two orientations: boxes and list.
     */
    public void setDifferentLayouts() {

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {

            // setting the grid layout and its decoration
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columnsForRecyclerView));
            recyclerView.addItemDecoration(new GridItemDecorator(columnsForRecyclerView, 10, true));

        }

    }

    /**
     * Used to start periodic updates
     */
    public void startRequestingStocks() {

        refreshStocksHandler = new Handler();

        refreshStocksHandler.post(getStocks);

    }

    /**
     * Stopping periodic updates
     */
    public void stopRequestingStocks() {

        refreshStocksHandler.removeCallbacks(getStocks);

    }

    @Override
    public void onResume() {

        super.onResume();

        // we need to restart periodic updates on orientation change
        // only if there is a sessionId (we have created everything once already)
        if (sessionId != null) {

            startRequestingStocks();

        }

    }

    @Override
    public void onPause() {

        super.onPause();

        // always stopping updates when hiding the app
        stopRequestingStocks();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQUEST_ADD_STOCKS:

                // receiving the newly selected stocks and refreshing the data
                if (resultCode == Activity.RESULT_OK) {

                    if (data != null && data.hasExtra(KEY_SELECTED_STOCKS)) {

                        allowedStocks = data.getParcelableArrayListExtra(KEY_SELECTED_STOCKS);

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

                // launching the dialog fragment that allows the user to pick stocks to show
                AvailableStocksDialogFragment availableStocksDialogFragment = new AvailableStocksDialogFragment();
                Bundle args = new Bundle();
                args.putParcelableArrayList(KEY_AVAILABLE_STOCKS, allowedStocks);
                availableStocksDialogFragment.setArguments(args);
                availableStocksDialogFragment.setTargetFragment(this, REQUEST_ADD_STOCKS);
                availableStocksDialogFragment.show(getFragmentManager(), null);

                break;

            default:

                break;

        }

    }

    /**
     * Retrieving the ASP.NET_SessionId
     */
    public class SessionIDAsyncTask extends BaseAsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... strings) {

            String sessionId = null;

            try {

                HttpURLConnection connection = (HttpURLConnection) defaultURL.openConnection();
                List<String> cookies = connection.getHeaderFields().get(COOKIES);

                for (String cookie : cookies) {

                    if (cookie.contains(ASP_NET_SESSION)) {

                        sessionId = cookie.substring(cookie.indexOf("=") + 1, cookie.indexOf(";"));
                        break;

                    }

                }
            } catch (IOException e) {

                e.printStackTrace();

            }

            return sessionId;

        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            StocksFragment.this.sessionId = s;

            // once we obtain the sessionId we can start requesting the stocks
            startRequestingStocks();

        }

    }

    /**
     * Getting the requested stocks
     */
    public class StockAsyncTask extends BaseAsyncTask<String, Void, ArrayList<Stock>> {

        @Override
        protected ArrayList<Stock> doInBackground(String... strings) {

            try {

                String sessionId = strings[0];

                if (sessionId != null) {

                    StringBuilder sb = new StringBuilder();

                    for (int i = 1; i < strings.length; i++) {

                        if (i > 1) {

                            sb.append(",");

                        }

                        sb.append(strings[i]);

                    }

                    //--------------- important ---------------
                    //--------------- to_be_removed ---------------
                    // Here would be the place to hand in the specified stocks
                    // as of 14/08/2016 still doesn't work (did work on the 11th)
//                    specificStocksURL = new URL(baseURL + sb.toString());
                    specificStocksURL = defaultURL;
                    //--------------- to_be_removed ---------------
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

                        // using jackson to deserialize to object array
                        ObjectMapper mapper = new ObjectMapper();
                        // used to convert first capital case letters to camel case
                        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);

                        // adding deserializer for the date
                        SimpleModule dateModule = new SimpleModule();
                        dateModule.addDeserializer(Date.class, new StockDateDeserializer());
                        mapper.registerModule(dateModule);

                        //creating the jsonArray and converting it to Stock[]
                        JSONArray arrayOfStocks = new JSONArray(jsonStocks.substring(1, jsonStocks.length() - 1));

                        Stock[] stocks = mapper.readValue(arrayOfStocks.toString(), Stock[].class);

                        // which is translated to an ArrayList
                        if (stocks != null) {

                            return new ArrayList<>(Arrays.asList(stocks));

                        }

                    } else {

                        // if there is an error (which there was since 11th)
                        // save the data and log it
                        br = new BufferedReader(new InputStreamReader(specificStockConnection.getErrorStream()));

                        String jsonStocks = "";

                        String stock;

                        while ((stock = br.readLine()) != null) {

                            jsonStocks += stock;

                        }

                        Log.d("Naxex", "failed to download data\n" + jsonStocks);

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

            if (stocks != null) {

                // temporary, data isn't changing and I need to test the design
                //--------------- to_be_removed ---------------
                int decimalPlaces = 4;
                for (int i = 0; i < stocks.size(); ++i) {

                    stocks.get(i).ask = round(random.nextDouble(), decimalPlaces);
                    stocks.get(i).bid = round(random.nextDouble(), decimalPlaces);
                    stocks.get(i).changeOrientation = random.nextInt(3);

                }
                //--------------- to_be_removed ---------------

                StocksFragment.this.stocks = stocks;

                reefreshStocks(recyclerView, StocksFragment.this.stocks);

            }

        }

    }

    /**
     * Used to truncate the doubles to only 4 symbols after
     * decimal point, probably useless if used with real data
     * @param value -- double value to trunkate
     * @param decimalPlaces -- number of decimal places to truncate to
     * @return -- truncated double value
     */
    private double round(double value, int decimalPlaces) {

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);

        return bd.doubleValue();

    }

}