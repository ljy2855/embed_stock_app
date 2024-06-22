package com.example.FinalProj;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import com.example.FinalProj.R;

public class MainActivity extends Activity {
	private static final double INITIAL_BALANCE = 10000.00;
	private static final int REQUEST_CODE = 1;
    private ListView listView;
    private TextView balanceTextView;
    private TextView totalAssetsTextView;
    private ArrayList<Stock> stocks;
    private ListView ownedStocksListView;
    private StockOwnershipAdapter ownershipAdapter;
    private StockAdapter adapter; // Assume StockAdapter is a custom adapter that can handle Stock objects
    private BroadcastReceiver priceUpdateReceiver;
    private UserAccount userAccount;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JniDriver driver = new JniDriver();
        driver.printLCD("test");
        
        userAccount = UserAccount.initialize(INITIAL_BALANCE);
        // Initialize the ListView and Stock list
        listView = (ListView) findViewById(R.id.stockListView);
        balanceTextView = (TextView) findViewById(R.id.balanceTextView);
        totalAssetsTextView = (TextView) findViewById(R.id.totalAssetsTextView);
        ownedStocksListView = (ListView) findViewById(R.id.ownedStocksListView);
        stocks = new ArrayList<Stock>();
        stocks.add(new Stock("Apple Inc.", 150.00, 153.25, 0.05));
        stocks.add(new Stock("Google", 1200.50, 1185.75, 0.03));
        stocks.add(new Stock("Amazon", 3100.00, 3150.00, 0.07));
        stocks.add(new Stock("Microsoft", 200.00, 204.20, 0.02));

        // Initialize the adapter and set it to the ListView
        adapter = new StockAdapter(this , stocks);
        listView.setAdapter(adapter);
        
        // Set an item click listener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            Stock selectedStock = stocks.get(position);
	            Intent intent = new Intent(MainActivity.this, StockDetailActivity.class);
	            intent.putExtra("STOCK", selectedStock);
	            startActivity(intent);
            }
        });
        ArrayList<Map.Entry<String, Integer>> ownedStocksList = new ArrayList<Map.Entry<String, Integer>>(userAccount.getOwnedStocks().entrySet());
        ownershipAdapter = new StockOwnershipAdapter(this, ownedStocksList);
        ownedStocksListView.setAdapter(ownershipAdapter);

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
                updateUI();
                }
            }
        };

        // Register the BroadcastReceiver to listen for specific broadcast actions
        registerReceiver(priceUpdateReceiver, new IntentFilter(StockPriceUpdateService.ACTION_UPDATE_STOCK_PRICE));

        // Start the StockPriceUpdateService
        startService(new Intent(this, StockPriceUpdateService.class));
        updateUI();
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
    
    @Override
    protected void onResume() {
        super.onResume();
        updateOwnedStocksList(); // 보유 주식 리스트 업데이트 및 어댑터 갱신
        updateUI();  // UI 업데이트
    }

    private void updateOwnedStocksList() {
        ArrayList<Map.Entry<String, Integer>> ownedStocksList = new ArrayList<Map.Entry<String, Integer>>(userAccount.getOwnedStocks().entrySet());
        if (ownershipAdapter == null) {
            ownershipAdapter = new StockOwnershipAdapter(this, ownedStocksList);
            ownedStocksListView.setAdapter(ownershipAdapter);
        } else {
            ownershipAdapter.clear();
            ownershipAdapter.addAll(ownedStocksList);
            ownershipAdapter.notifyDataSetChanged();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            userAccount = (UserAccount) data.getSerializableExtra("USER_ACCOUNT");
            updateUI();
        }
    }
    private void updateUI() {
    	double balance = userAccount.getBalance(); // Direct access
        double totalAssets = balance;
        
        for (Map.Entry<String, Integer> entry : userAccount.getOwnedStocks().entrySet()) {
            for (Stock stock : stocks) {
                if (stock.getName().equals(entry.getKey())) {
                    totalAssets += stock.getCurrentPrice() * entry.getValue();
                }
            }
        }

        balanceTextView.setText(String.format("Balance: $%.2f", balance));
        totalAssetsTextView.setText(String.format("Total Assets: $%.2f", totalAssets));
    }
}
