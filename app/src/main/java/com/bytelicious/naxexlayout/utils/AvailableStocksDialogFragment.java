package com.bytelicious.naxexlayout.utils;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.bytelicious.naxexlayout.R;
import com.bytelicious.naxexlayout.StocksFragment;
import com.bytelicious.naxexlayout.pojos.StockKVP;

import java.util.ArrayList;

/**
 * Dialog fragment that shows a list of stocks and their state (picked/unpicked)
 */
public class AvailableStocksDialogFragment extends DialogFragment implements View.OnClickListener {

    Button btnAddAvailableStock;
    ListView lvAvailableStocks;

    ArrayList<StockKVP> availableStocks = null;

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_add_available_stock:

                availableStocks = ((AvailableStocksBaseAdapter) lvAvailableStocks.getAdapter()).getModifiedData();

                Intent intent = new Intent();
                intent.putExtra(StocksFragment.KEY_SELECTED_STOCKS, availableStocks);

                if (getTargetFragment() != null) {

                    getTargetFragment().onActivityResult(
                            getTargetRequestCode(), Activity.RESULT_OK, intent);

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

        if (args != null && args.containsKey(StocksFragment.KEY_AVAILABLE_STOCKS)) {

            availableStocks = args.getParcelableArrayList(StocksFragment.KEY_AVAILABLE_STOCKS);

        }

        //using the custom base adapter to allow the user to pick stocks to be shown
        lvAvailableStocks.setAdapter(new AvailableStocksBaseAdapter(getActivity(), availableStocks));

        btnAddAvailableStock.setOnClickListener(this);

        return rootView;

    }

}