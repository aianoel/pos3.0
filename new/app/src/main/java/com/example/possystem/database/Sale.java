package com.example.possystem.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "sales")
public class Sale {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private long timestamp;
    private double totalAmount;
    private String paymentMethod;
    private int cashierId;
    private String receiptNumber;

    public Sale(long timestamp, double totalAmount, String paymentMethod, 
                int cashierId, String receiptNumber) {
        this.timestamp = timestamp;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.cashierId = cashierId;
        this.receiptNumber = receiptNumber;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getCashierId() {
        return cashierId;
    }

    public void setCashierId(int cashierId) {
        this.cashierId = cashierId;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }
} 