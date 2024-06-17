package com.example.final_proj;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private ListView listView;
    private List<Stock> stocks;
    private StockAdapter adapter; // Assume StockAdapter is a custom adapter that can handle Stock objects
    private BroadcastReceiver priceUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize the ListView and Stock list
        listView = (ListView) findViewById(R.id.stockListView);
        stocks = new ArrayList<Stock>();
        stocks.add(new Stock("Apple Inc.", 150.00, 153.25, 0.05));
        stocks.add(new Stock("Google", 1200.50, 1185.75, 0.03));
        stocks.add(new Stock("Amazon", 3100.00, 3150.00, 0.07));
        stocks.add(new Stock("Microsoft", 200.00, 204.20, 0.02));

        // Initialize the adapter and set it to the ListView
        adapter = new StockAdapter(this, stocks);
        listView.setAdapter(adapter);
        
        // Set an item click listener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	Stock selectedStock = stocks.get(position);
            	Intent intent = new Intent(MainActivity.this, StockDetailActivity.class);
            	intent.putExtra("STOCK", selectedStock); // Passing the stock object to StockDetailActivity
            	startActivity(intent);
            }
        });

        // Define and register BroadcastReceiver to receive stock price updates
        priceUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (StockPriceUpdateService.ACTION_UPDATE_STOCK_PRICE.equals(intent.getAction())) {
                    String stockName = intent.getStringExtra("STOCK_NAME");
                    double newPrice = intent.getDoubleExtra("NEW_PRICE", 0);
                    for (Stock stock : stocks) {
                        if (stock.getName().equals(stockName)) {
                            stock.setCurrentPrice(newPrice); // Update the price of the matched stock
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged(); // Notify the adapter to refresh the list view
                }
            }
        };

        // Register the BroadcastReceiver to listen for specific broadcast actions
        registerReceiver(priceUpdateReceiver, new IntentFilter(StockPriceUpdateService.ACTION_UPDATE_STOCK_PRICE));

        // Start the StockPriceUpdateService
        startService(new Intent(this, StockPriceUpdateService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Ensure receiver is registered when activity starts or restarts
        registerReceiver(priceUpdateReceiver, new IntentFilter(StockPriceUpdateService.ACTION_UPDATE_STOCK_PRICE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unregister receiver to avoid memory leaks
        unregisterReceiver(priceUpdateReceiver);
    }
}
