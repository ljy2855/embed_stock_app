package com.example.FinalProj;

import java.util.ArrayList;
import java.util.Map;

import com.example.FinalProj.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Handler;

public class StockDetailActivity extends Activity {
    private TextView stockNameTextView;
    private TextView previousPriceTextView;
    private TextView currentPriceTextView;
    private TextView stockChangeTextView;
    private TextView balanceTextView;
    private TextView totalAssetsTextView;
    private ListView ownedStocksListView;
    private StockOwnershipAdapter ownershipAdapter;
    private Stock stock; // Stock object to display and update
    private BroadcastReceiver priceUpdateReceiver;
    private UserAccount userAccount;
    private boolean isSell = false;
    JniDriver driver = new JniDriver();

    private Handler handler = new Handler(Looper.getMainLooper()); // Handler attached to the UI thread

    private Thread keyCheckThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    int keyState = driver.readKey();
                    if (keyState == 1) { // HOME button
                        isSell = !isSell;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateUI();
                            }
                        });
                    } else if (keyState == 2) { // RESET button
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (isSell) {
                                    performSell();
                                } else {
                                    performBuy();
                                }
                            }
                        });
                    }
                    Thread.sleep(500); // Check every 200 milliseconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore the interrupted status
                }
            }
        }
    });

    private void performBuy() {
        if (userAccount.buyStock(stock, 1)) {
            updateUI();
            driver.printTransactionEvent();
        }
    }

    private void performSell() {
        if (userAccount.sellStock(stock, 1)) {
            updateUI();
            driver.printTransactionEvent();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        // Initialize TextViews
        stockNameTextView = (TextView) findViewById(R.id.stockNameTextView);
        previousPriceTextView = (TextView) findViewById(R.id.previousPriceTextView);
        currentPriceTextView = (TextView) findViewById(R.id.currentPriceTextView);
        stockChangeTextView = (TextView) findViewById(R.id.stockChangeTextView);
        balanceTextView = (TextView) findViewById(R.id.balanceTextViewDetail);
        totalAssetsTextView = (TextView) findViewById(R.id.totalAssetsTextViewDetail);
        ownedStocksListView = (ListView) findViewById(R.id.ownedStocksListView);
        // Retrieve the Stock object passed from MainActivity
        stock = getIntent().getParcelableExtra("STOCK");
        userAccount = UserAccount.getInstance();
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

        Button buyButton = (Button) findViewById(R.id.buttonBuy);
        Button sellButton = (Button) findViewById(R.id.buttonSell);

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userAccount.buyStock(stock, 1)) { // 가정: 한 주를 매수
                    updateUI();
                    driver.printTransactionEvent();

                }
            }
        });

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userAccount.sellStock(stock, 1)) { // 가정: 한 주를 매도
                    updateUI();
                    driver.printTransactionEvent();
                }
            }
        });
    }

    private void updateUI() {
        stockNameTextView.setText(stock.getName());
        previousPriceTextView.setText(String.format("Previous Price: $%.2f", stock.getPreviousPrice()));
        currentPriceTextView.setText(String.format("Current Price: $%.2f", stock.getCurrentPrice()));
        double changePercent = (stock.getCurrentPrice() - stock.getPreviousPrice()) / stock.getPreviousPrice() * 100;
        stockChangeTextView.setText(String.format("Change: %.2f%%", changePercent));
        userAccount = UserAccount.getInstance();
        double balance = userAccount.getBalance();
        balanceTextView.setText(String.format("Current Balance: $%.2f", balance));

        double totalAssets = balance;
        Map<String, Integer> stocksOwned = userAccount.getOwnedStocks();

        for (Map.Entry<String, Integer> entry : stocksOwned.entrySet()) {

            totalAssets += entry.getValue() * stock.getCurrentPrice(); // 가정: 모든 주식이 현재 가격을 가짐
        }
        // 현재 보유 주식 업데이트
        ArrayList<Map.Entry<String, Integer>> ownedStocksList = new ArrayList<Map.Entry<String, Integer>>(
                stocksOwned.entrySet());
        if (ownershipAdapter == null) {
            ownershipAdapter = new StockOwnershipAdapter(this, ownedStocksList);
            ownedStocksListView.setAdapter(ownershipAdapter);
        } else {
            ownershipAdapter.clear();
            ownershipAdapter.addAll(ownedStocksList);
            ownershipAdapter.notifyDataSetChanged();
        }

        totalAssetsTextView.setText(String.format("Total Assets: $%.2f", totalAssets));

        Integer quantity = stocksOwned.get(stock.getName());
        if (quantity == null) {
            quantity = 0;
        }
        driver.printOwnedStockQuantity(quantity);

        // Assuming 'isSell' is a valid boolean variable that indicates if the last
        // transaction was a sell
        driver.printTransactionMode(isSell);
        driver.printAccount(totalAssets, balance);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister receiver to avoid memory leaks
        unregisterReceiver(priceUpdateReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        keyCheckThread.setDaemon(true);
        keyCheckThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        keyCheckThread.interrupt();
    }
}
