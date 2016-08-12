package com.bytelicious.naxexlayout.pojos;

import android.os.Parcel;
import android.os.Parcelable;

public class StockKVP implements Parcelable {

    public String name;
    public boolean picked;

    public StockKVP() {

    }

    protected StockKVP(Parcel in) {

        name = in.readString();
        picked = in.readByte() != 0;

    }

    public static final Creator<StockKVP> CREATOR = new Creator<StockKVP>() {

        @Override
        public StockKVP createFromParcel(Parcel in) {

            return new StockKVP(in);
        }

        @Override
        public StockKVP[] newArray(int size) {

            return new StockKVP[size];

        }
    };

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(name);
        parcel.writeByte((byte) (picked ? 1 : 0));

    }
}
