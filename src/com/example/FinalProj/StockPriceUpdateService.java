package com.example.FinalProj;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StockPriceUpdateService extends Service {
    private ScheduledExecutorService scheduler;
    private ArrayList<Stock> stocks;
    public static final String ACTION_UPDATE_STOCK_PRICE = "com.example.final_proj.action.UPDATE_STOCK_PRICE";

    @Override
    public void onCreate() {
        super.onCreate();
        scheduler = Executors.newScheduledThreadPool(1);
        stocks = new ArrayList<Stock>();
        stocks.add(new Stock("Apple Inc.", 150.00, 153.25, 0.05));
        stocks.add(new Stock("Google", 1200.50, 1185.75, 0.03));
        stocks.add(new Stock("Amazon", 3100.00, 3150.00, 0.07));
        stocks.add(new Stock("Microsoft", 200.00, 204.20, 0.02));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Intent broadcastIntent = new Intent(ACTION_UPDATE_STOCK_PRICE);

        Runnable priceUpdateTask = new Runnable() {
            public void run() {
                for (Stock stock : stocks) {
                    stock.updatePrice();
                    broadcastIntent.putExtra("STOCK_NAME", stock.getName());
                    broadcastIntent.putExtra("NEW_PRICE", stock.getCurrentPrice());
                    sendBroadcast(broadcastIntent);
                }
                JniDriver driver = new JniDriver();
                driver.printPriceChangeEvent();
            }
        };

        scheduler.scheduleAtFixedRate(priceUpdateTask, 0, 3, TimeUnit.SECONDS);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not a bound service
    }
}
