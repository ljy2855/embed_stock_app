package com.example.final_proj;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

public class StockDetailActivity extends Activity {
    private TextView stockNameTextView;
    private TextView previousPriceTextView;
    private TextView currentPriceTextView;
    private TextView stockChangeTextView;
    private Stock stock; // Stock object to display and update
    private BroadcastReceiver priceUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        // Initialize TextViews
        stockNameTextView = (TextView) findViewById(R.id.stockNameTextView);
        previousPriceTextView = (TextView) findViewById(R.id.previousPriceTextView);
        currentPriceTextView = (TextView) findViewById(R.id.currentPriceTextView);
        stockChangeTextView = (TextView) findViewById(R.id.stockChangeTextView);

        // Retrieve the Stock object passed from MainActivity
        stock = getIntent().getParcelableExtra("STOCK");
        updateUI(); // Initial UI update with stock details

        // Define and register BroadcastReceiver to receive price updates
        priceUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (StockPriceUpdateService.ACTION_UPDATE_STOCK_PRICE.equals(intent.getAction())) {
                    String stockName = intent.getStringExtra("STOCK_NAME");
                    double newPrice = intent.getDoubleExtra("NEW_PRICE", 0);
                    if (stock.getName().equals(stockName)) {
                        stock.setCurrentPrice(newPrice);
                        updateUI(); // Update the UI with the new stock price
                    }
                }
            }
        };

        // Register the BroadcastReceiver to listen for specific broadcast actions
        registerReceiver(priceUpdateReceiver, new IntentFilter(StockPriceUpdateService.ACTION_UPDATE_STOCK_PRICE));
    }

    private void updateUI() {
        stockNameTextView.setText(stock.getName());
        previousPriceTextView.setText(String.format("Previous Price: $%.2f", stock.getPreviousPrice()));
        currentPriceTextView.setText(String.format("Current Price: $%.2f", stock.getCurrentPrice()));
        double changePercent = (stock.getCurrentPrice() - stock.getPreviousPrice()) / stock.getPreviousPrice() * 100;
        stockChangeTextView.setText(String.format("Change: %.2f%%", changePercent));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister receiver to avoid memory leaks
        unregisterReceiver(priceUpdateReceiver);
    }
}
