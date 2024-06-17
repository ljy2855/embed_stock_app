package com.example.final_proj;

import java.util.HashMap;
import java.util.Map;

public class UserAccount {
    private double balance;
    private Map<String, Integer> ownedStocks; // Map to hold stock names and their quantities

    public UserAccount(double initialBalance) {
        this.balance = initialBalance;
        this.ownedStocks = new HashMap<String, Integer>();
    }

    public double getBalance() {
        return balance;
    }

    public boolean buyStock(Stock stock, int quantity) {
        double totalCost = stock.getCurrentPrice() * quantity;
        if (balance >= totalCost) {
            balance -= totalCost;
            Integer currentQuantity = ownedStocks.get(stock.getName());
            if (currentQuantity == null) {
                ownedStocks.put(stock.getName(), quantity);
            } else {
                ownedStocks.put(stock.getName(), currentQuantity + quantity);
            }
            return true;
        }
        return false;
    }

    public boolean sellStock(Stock stock, int quantity) {
        Integer currentQuantity = ownedStocks.get(stock.getName());
        if (currentQuantity != null && currentQuantity >= quantity) {
            balance += stock.getCurrentPrice() * quantity;
            if (currentQuantity == quantity) {
                ownedStocks.remove(stock.getName());
            } else {
                ownedStocks.put(stock.getName(), currentQuantity - quantity);
            }
            return true;
        }
        return false;
    }

    public Map<String, Integer> getOwnedStocks() {
        return ownedStocks;
    }
}
