package com.example.final_proj;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import java.util.Random;
import android.os.Parcel;
import android.os.Parcelable;

public class StockAdapter extends ArrayAdapter<Stock> {
    private Activity context;
    private List<Stock> stocks;

    public StockAdapter(Activity context, List<Stock> stocks) {
        super(context, R.layout.list_item_stock, stocks);
        this.context = context;
        this.stocks = stocks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item_stock, null, true);
        TextView textViewStockName = (TextView) rowView.findViewById(R.id.textViewStockName);
        TextView textViewStockPrice = (TextView) rowView.findViewById(R.id.textViewStockPrice);
        TextView textViewStockChange = (TextView) rowView.findViewById(R.id.textViewStockChange);

        Stock stock = stocks.get(position);
        textViewStockName.setText(stock.getName());
        textViewStockPrice.setText("$" + String.format("%.2f", stock.getCurrentPrice()));
        textViewStockChange.setText(stock.getChange()); // 변동폭 계산 메서드 호출

        return rowView;
    }
}


class Stock implements Parcelable {
    private String name;
    private double previousPrice;
    private double currentPrice;
    private double riskLevel;

    public Stock(String name, double previousPrice, double currentPrice, double riskLevel) {
        this.name = name;
        this.previousPrice = previousPrice;
        this.currentPrice = currentPrice;
        this.riskLevel = riskLevel;
    }

    protected Stock(Parcel in) {
        name = in.readString();
        previousPrice = in.readDouble();
        currentPrice = in.readDouble();
        riskLevel = in.readDouble();
    }

    public static final Creator<Stock> CREATOR = new Creator<Stock>() {
        @Override
        public Stock createFromParcel(Parcel in) {
            return new Stock(in);
        }

        @Override
        public Stock[] newArray(int size) {
            return new Stock[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(previousPrice);
        dest.writeDouble(currentPrice);
        dest.writeDouble(riskLevel);
    }

    public void updatePrice() {
        final Random random = new Random();
        double changePercent = (random.nextDouble() * 2 * riskLevel) - riskLevel; // Random change within +/- risk level
        double changeAmount = currentPrice * changePercent;
        setCurrentPrice(currentPrice + changeAmount);
    }

    public void setCurrentPrice(double newPrice) {
        this.previousPrice = this.currentPrice;
        this.currentPrice = newPrice;
    }

    public String getName() {
        return name;
    }

    public double getPreviousPrice() {
        return previousPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public String getChange() {
        double change = (currentPrice - previousPrice) / previousPrice * 100;
        return String.format("%.2f%%", change);
    }
}
