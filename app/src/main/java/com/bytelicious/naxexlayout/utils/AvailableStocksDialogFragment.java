package com.bytelicious.naxexlayout.utils;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.bytelicious.naxexlayout.MainActivity;
import com.bytelicious.naxexlayout.R;
import com.bytelicious.naxexlayout.pojos.StockKVP;

import java.util.ArrayList;

public class AvailableStocksDialogFragment extends DialogFragment implements View.OnClickListener {

    Button btnAddAvailableStock;
    ListView lvAvailableStocks;

    private OnDFResult onDFResult;

    public interface OnDFResult {

        void onDFResult(Intent data);

    }

    ArrayList<StockKVP> availableStocks = null;

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_add_available_stock:

                if (onDFResult != null) {

                    availableStocks = ((AvailableStocksBaseAdapter)lvAvailableStocks.getAdapter()).getModifiedData();

                    Intent intent = new Intent();
                    intent.putExtra(MainActivity.KEY_SELECTED_STOCKS, availableStocks);

                    onDFResult.onDFResult(intent);

                    dismiss();

                }

                break;

            default:

                break;

        }

    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.available_stocks_dialog_fragment, container, false);

        btnAddAvailableStock = (Button) rootView.findViewById(R.id.btn_add_available_stock);
        lvAvailableStocks = (ListView) rootView.findViewById(R.id.lv_available_stocks);

        Bundle args = getArguments();

        if (args != null && args.containsKey(MainActivity.KEY_AVAILABLE_STOCKS)) {

            availableStocks = args.getParcelableArrayList(MainActivity.KEY_AVAILABLE_STOCKS);

        }

        lvAvailableStocks.setAdapter(new AvailableStocksBaseAdapter(getActivity(), availableStocks));

        btnAddAvailableStock.setOnClickListener(this);

        return rootView;

    }

    public void setOnDFResult(OnDFResult onDFResult) {

        this.onDFResult = onDFResult;

    }

}