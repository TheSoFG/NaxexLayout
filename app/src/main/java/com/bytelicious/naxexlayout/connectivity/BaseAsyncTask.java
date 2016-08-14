package com.bytelicious.naxexlayout.connectivity;

import android.os.AsyncTask;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ACER PC on 8/11/2016.
 * Class to be reused for getting the SessionID and the stocks
 */
public abstract class BaseAsyncTask<T, K, V> extends AsyncTask<T, K, V> {

    protected URL defaultURL;
    protected URL specificStocksURL;

    protected static final String baseURL = "http://eu.tradenetworks.com/QuotesBox/quotes/GetQuotesBySymbols?languageCode=en-US&amp;symbols=";

    public static final String ASP_NET_SESSION = "ASP.NET_SessionId";
    public static final String COOKIES = "Set-Cookie";

    public BaseAsyncTask() {

        try {

            defaultURL = new URL("http://eu.tradenetworks.com/QuotesBox/quotes/GetQuotesBySymbols?languageCode=en-US&symbols=EURUSD,GBPUSD,USDCHF,USDJPY,AUDUSD,USDCAD,GBPJPY,EURGBP,EURJPY,AUDCAD");

        } catch (MalformedURLException e) {

            e.printStackTrace();

        }

    }

}