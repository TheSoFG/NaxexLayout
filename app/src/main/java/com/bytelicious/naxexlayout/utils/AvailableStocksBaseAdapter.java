package com.bytelicious.naxexlayout.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bytelicious.naxexlayout.R;
import com.bytelicious.naxexlayout.pojos.StockKVP;

import java.util.ArrayList;

public class AvailableStocksBaseAdapter extends BaseAdapter {

    private ArrayList<StockKVP> availableItems;
    private Context context;

    public AvailableStocksBaseAdapter(Context context, ArrayList<StockKVP> items) {

        this.availableItems = items;
        this.context = context;

    }

    @Override
    public int getCount() {

        return availableItems.size();

    }

    @Override
    public Object getItem(int i) {

        return availableItems.get(i);

    }

    @Override
    public long getItemId(int i) {

        return 0;

    }

    public ArrayList<StockKVP> getModifiedData() {

        return availableItems;

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final ViewHolder vh;

        if (view == null) {

            view = LayoutInflater.from(context).inflate(R.layout.lv_item, viewGroup, false);

            vh = new ViewHolder();
            vh.cbPicked = (CheckBox) view.findViewById(R.id.cb_stock_picked);
            vh.tvName = (TextView) view.findViewById(R.id.tv_stock_name);

            view.setTag(vh);

        } else {

            vh = (ViewHolder) view.getTag();

        }

        vh.cbPicked.setChecked(availableItems.get(i).picked);
        vh.tvName.setText(availableItems.get(i).name);

        final int position = i;
        vh.cbPicked.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                availableItems.get(position).picked = vh.cbPicked.isChecked();

            }
        });

        return view;

    }

    private static class ViewHolder {

        public CheckBox cbPicked;
        public TextView tvName;

    }

}
