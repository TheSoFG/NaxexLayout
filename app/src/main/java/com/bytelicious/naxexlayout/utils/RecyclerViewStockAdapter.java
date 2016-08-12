package com.bytelicious.naxexlayout.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bytelicious.naxexlayout.R;
import com.bytelicious.naxexlayout.pojos.Stock;

import java.util.ArrayList;

/**
 * Created by ACER PC on 8/11/2016.
 */
public class RecyclerViewStockAdapter extends RecyclerView.Adapter<RecyclerViewStockAdapter.StockViewHolder> {

    private ArrayList<Stock> stocks;
    private Context context;
    private StockActionListener stockActionListener;

    public RecyclerViewStockAdapter(Context context, ArrayList<Stock> stocks, StockActionListener stockActionListener) {

        this.context = context;
        this.stocks = stocks;
        this.stockActionListener = stockActionListener;

    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_element, null);

        StockViewHolder stockViewHolder = new StockViewHolder(view, stockActionListener);

        return stockViewHolder;

    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {

        holder.tvAsk.setText(String.valueOf(stocks.get(position).ask));
        holder.tvBid.setText(String.valueOf(stocks.get(position).bid));

    }

    @Override
    public int getItemCount() {

        return stocks.size();

    }

    public interface StockActionListener {

        void onBuyClicked(View v, int position);

        void onSellClicked(View v, int position);

    }

    public class StockViewHolder extends RecyclerView.ViewHolder {

        private StockActionListener stockActionListener;

        protected Button btnBuy;
        protected Button btnSell;
        protected TextView tvAsk;
        protected TextView tvBid;

        public StockViewHolder(final View itemView, final StockActionListener newStockActionListener) {

            super(itemView);

            this.stockActionListener = newStockActionListener;

            btnBuy = (Button) itemView.findViewById(R.id.btn_buy);
            btnSell = (Button) itemView.findViewById(R.id.btn_sell);
            tvAsk = (TextView) itemView.findViewById(R.id.tv_ask);
            tvBid = (TextView) itemView.findViewById(R.id.tv_bid);

            btnBuy.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    stockActionListener.onBuyClicked(itemView, getAdapterPosition());

                }

            });

            btnSell.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    stockActionListener.onSellClicked(itemView, getAdapterPosition());

                }

            });

        }

    }

    public Stock removeAtPosition(int position) {

//        if(position > -1 && position < stocks.size()) {

//            boolean removed = stocks.remove(position) != null;
            Stock removedStock = stocks.remove(position);
            notifyDataSetChanged();
            return removedStock;
//            return removed;
//
//        }
//
//        return false;
    }

    public void add(Stock newStock) {

        stocks.add(newStock);
        notifyDataSetChanged();

    }

    public ArrayList<Stock> getStocks() {

        return stocks;

    }

    public void replaceData(ArrayList<Stock> newData) {

        this.stocks = newData;
        notifyDataSetChanged();

    }

}
