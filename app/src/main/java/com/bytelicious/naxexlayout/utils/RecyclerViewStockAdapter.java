package com.bytelicious.naxexlayout.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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

    StyleSpan boldSpan;
    RelativeSizeSpan sizeSpan;
    ForegroundColorSpan greenColorSpan;
    ForegroundColorSpan redColorSpan;

    public RecyclerViewStockAdapter(Context context, ArrayList<Stock> stocks, StockActionListener stockActionListener) {

        this.context = context;
        this.stocks = stocks;
        this.stockActionListener = stockActionListener;

        boldSpan = new StyleSpan(Typeface.BOLD);
        sizeSpan = new RelativeSizeSpan(2f);
        greenColorSpan = new ForegroundColorSpan(Color.GREEN);
        redColorSpan = new ForegroundColorSpan(Color.RED);

    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_element, null);

        StockViewHolder stockViewHolder = new StockViewHolder(view, stockActionListener);

        return stockViewHolder;

    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {

        holder.tvAsk.setText(stylizeNumbers(String.valueOf(stocks.get(position).ask), stocks.get(position).changeOrientation));
        holder.tvBid.setText(stylizeNumbers(String.valueOf(stocks.get(position).bid), stocks.get(position).changeOrientation));
        holder.tvStockName.setText(stocks.get(position).displayName);

    }

    @Override
    public int getItemCount() {

        return stocks.size();

    }

    public interface StockActionListener {

        void onBuyClicked(View v, String stockName, double price);

        void onSellClicked(View v, String stockName, double price);

    }

    public class StockViewHolder extends RecyclerView.ViewHolder {

        private StockActionListener stockActionListener;

        protected Button btnBuy;
        protected Button btnSell;
        protected TextView tvAsk;
        protected TextView tvBid;
        protected TextView tvStockName;

        public StockViewHolder(final View itemView, final StockActionListener newStockActionListener) {

            super(itemView);

            this.stockActionListener = newStockActionListener;

            btnBuy = (Button) itemView.findViewById(R.id.btn_buy);
            btnSell = (Button) itemView.findViewById(R.id.btn_sell);
            tvAsk = (TextView) itemView.findViewById(R.id.tv_ask);
            tvBid = (TextView) itemView.findViewById(R.id.tv_bid);
            tvStockName = (TextView) itemView.findViewById(R.id.tv_stock_name);

            btnBuy.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    stockActionListener.onBuyClicked(itemView, stocks.get(getAdapterPosition()).displayName, stocks.get(getAdapterPosition()).ask);

                }

            });

            btnSell.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    stockActionListener.onSellClicked(itemView, stocks.get(getAdapterPosition()).displayName, stocks.get(getAdapterPosition()).bid);

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

    private SpannableStringBuilder stylizeNumbers(String number, int orientation) {

        String data = String.valueOf(number);
        SpannableStringBuilder sb = new SpannableStringBuilder(data);

        switch (orientation) {

            // up
            case 1:

                sb.setSpan(greenColorSpan, 0, data.length(), 0);

                break;

            // down
            case 2:

                sb.setSpan(redColorSpan, 0, data.length(), 0);

                break;

            // unchanged
            case 3:

                //black color? current is gray

                break;

            default:

                break;

        }

        sb.setSpan(boldSpan, number.length() - 2, number.length(), 0);
        sb.setSpan(sizeSpan, number.length() - 2, number.length(), 0);

        return sb;

    }

}
