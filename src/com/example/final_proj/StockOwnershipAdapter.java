package com.example.final_proj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class StockOwnershipAdapter extends ArrayAdapter<Map.Entry<String, Integer>> {
    private Context context;
    private List<Map.Entry<String, Integer>> stocks;

    public StockOwnershipAdapter(Context context, List<Map.Entry<String, Integer>> stocks) {
        super(context, R.layout.stock_ownership_item, stocks);
        this.context = context;
        this.stocks = stocks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.stock_ownership_item, parent, false);
        }

        TextView textViewStockName = (TextView) convertView.findViewById(R.id.textViewStockName);
        TextView textViewStockQuantity = (TextView) convertView.findViewById(R.id.textViewStockQuantity);

        Map.Entry<String, Integer> stockEntry = stocks.get(position);
        textViewStockName.setText(stockEntry.getKey());
        textViewStockQuantity.setText(String.valueOf(stockEntry.getValue()));

        return convertView;
    }
}
