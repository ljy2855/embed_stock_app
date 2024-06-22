package com.example.FinalProj;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

public class UserAccount implements Parcelable {
    private double balance;
    private static UserAccount instance;
    private Map<String, Integer> ownedStocks;

    private UserAccount(double initialBalance) {
        this.balance = initialBalance;
        this.ownedStocks = new HashMap<String, Integer>();
    }

    // 초기화와 함께 인스턴스 생성
    public static synchronized UserAccount initialize(double initialBalance) {
        if (instance == null) {
            instance = new UserAccount(initialBalance);
        }
        return instance;
    }

    // 기존 인스턴스 반환
    public static UserAccount getInstance() {
        if (instance == null) {
            throw new IllegalStateException("UserAccount is not initialized, call initialize(double) first.");
        }
        return instance;
    }

    protected UserAccount(Parcel in) {
        balance = in.readDouble();
        int size = in.readInt();
        ownedStocks = new HashMap<String, Integer>(size);
        for (int i = 0; i < size; i++) {
            String key = in.readString();
            Integer value = in.readInt();
            ownedStocks.put(key, value);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(balance);
        dest.writeInt(ownedStocks.size());
        for (Map.Entry<String, Integer> entry : ownedStocks.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeInt(entry.getValue());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserAccount> CREATOR = new Creator<UserAccount>() {
        @Override
        public UserAccount createFromParcel(Parcel in) {
            return new UserAccount(in);
        }

        @Override
        public UserAccount[] newArray(int size) {
            return new UserAccount[size];
        }
    };

    // Getter and setter methods
    public double getBalance() {
        return balance;
    }

    public Map<String, Integer> getOwnedStocks() {
        return ownedStocks;
    }

    // Additional methods
    public boolean buyStock(Stock stock, int quantity) {
        double totalCost = stock.getCurrentPrice() * quantity;
        if (balance >= totalCost) { // 잔액 확인
            balance -= totalCost; // 잔액 감소
            Integer currentQuantity = ownedStocks.get(stock.getName());
            ownedStocks.put(stock.getName(), (currentQuantity == null ? 0 : currentQuantity) + quantity); // 주식 수량 증가
            return true;
        }
        return false; // 잔액 부족 시 거래 불가
    }

    // 주식 매도
    public boolean sellStock(Stock stock, int quantity) {
        Integer currentQuantity = ownedStocks.get(stock.getName());
        if (currentQuantity != null && currentQuantity >= quantity) { // 보유 주식 수 확인
            balance += stock.getCurrentPrice() * quantity; // 잔액 증가
            int newQuantity = currentQuantity - quantity;
            if (newQuantity > 0) {
                ownedStocks.put(stock.getName(), newQuantity); // 주식 수량 감소
            } else {
                ownedStocks.remove(stock.getName()); // 주식 전부 매도 시 제거
            }
            return true;
        }
        return false; // 보유 주식 수 부족 시 거래 불가
    }
}