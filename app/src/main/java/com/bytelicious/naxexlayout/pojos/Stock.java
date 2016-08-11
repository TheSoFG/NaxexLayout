package com.bytelicious.naxexlayout.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by ACER PC on 8/11/2016.
 * <p/>
 * Ask, Bid – the rate you should write in Sell/Buy boxes
 * Change – not in use
 * ChangeOrientation – { UP=1 , Down=2, Unchanged =3} – If the change is UP – make the quote green, of the change is Down – make it red, if the change is unchanged make it black
 * Currency – not in use
 * Date – not in use
 * DisplayName – same as Currency. Write as the name of Symbol.
 * High, Low – Not in use
 */
public class Stock implements Parcelable {

    public String currency;
    public double bid;
    public double ask;
    public double high;
    public double low;
    public int changeOrientation;
    public double change;
    public double changePercentage;
    public Date date;
    public String displayName;

    public Stock() {

    }

    protected Stock(Parcel in) {

        currency = in.readString();
        bid = in.readDouble();
        ask = in.readDouble();
        high = in.readDouble();
        low = in.readDouble();
        changeOrientation = in.readInt();
        change = in.readDouble();
        changePercentage = in.readDouble();

        int noDate = in.readInt();
        if (noDate == 1) {

            date = null;

        } else if (noDate == 0) {

            date = new Date(in.readLong());

        }

        displayName = in.readString();

    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(currency);
        parcel.writeDouble(bid);
        parcel.writeDouble(ask);
        parcel.writeDouble(high);
        parcel.writeDouble(low);
        parcel.writeInt(changeOrientation);
        parcel.writeDouble(change);
        parcel.writeDouble(changePercentage);

        if (date == null) {

            parcel.writeInt(1);

        } else {

            parcel.writeInt(0);
            parcel.writeLong(date.getTime());

        }

        parcel.writeString(displayName);

    }

    public static final Creator<Stock> CREATOR = new Creator<Stock>() {
        @Override
        public Stock createFromParcel(Parcel in) {
            return new Stock(in);
        }

        @Override
        public Stock[] newArray(int size) {
            return new Stock[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}