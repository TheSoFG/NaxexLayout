package com.bytelicious.naxexlayout.connectivity;

import android.os.AsyncTask;
import android.webkit.CookieManager;

import com.bytelicious.naxexlayout.MainActivity;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ACER PC on 8/11/2016.
 */
public class StockAsyncTask extends AsyncTask<Void, Void, Void> {

    URL defaultURL;
    URL specificStocksURL;

    private static final String baseURL = "http://eu.tradenetworks.com/QuotesBox/quotes/GetQuotesBySymbols?languageCode=en-US&amp;symbols=";

    public static final String ASP_NET_SESSION = "ASP.NET_SessionId";
    public static final String COOKIES = "Set-Cookie";

    public StockAsyncTask() {

        try {

            defaultURL = new URL("http://eu.tradenetworks.com/QuotesBox/quotes/GetQuotesBySymbols?languageCode=en-US&amp;symbols=EURUSD,GBPUSD,USDCHF,USDJPY,AUDUSD,USDCAD,GBPJPY,EURGBP,EURJPY,AUDCAD");

        } catch (MalformedURLException e) {

            e.printStackTrace();

        }

    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {

            String sessionId = null;

            HttpURLConnection connection = (HttpURLConnection) defaultURL.openConnection();
            List<String> cookies = connection.getHeaderFields().get(COOKIES);
            for (String cookie : cookies) {

                if (cookie.contains(ASP_NET_SESSION)) {

                    sessionId = cookie.substring(cookie.indexOf("=") + 1, cookie.indexOf(";"));

                }

            }

            if (sessionId != null) {

                specificStocksURL = new URL(baseURL);
                HttpURLConnection specificStockConnection = (HttpURLConnection) specificStocksURL.openConnection();

                specificStockConnection.setRequestProperty(ASP_NET_SESSION, sessionId);

                BufferedReader br =
                        new BufferedReader(
                                new InputStreamReader(specificStockConnection.getInputStream()));

                String jsonStocks = "";

                String stock;

                while((stock = br.readLine()) != null) {

                    jsonStocks += stock;

                }

                ObjectMapper mapper = new ObjectMapper();
                JSONArray arrayOfStocks = new JSONArray(jsonStocks);

                if(arrayOfStocks != null) {



                }


            }

        } catch (IOException e) {

            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}