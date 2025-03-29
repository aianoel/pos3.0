package com.example.possystem.reports;

import java.util.Date;

public class TransactionModel {
    private int id;
    private String receiptNumber;
    private Date date;
    private String cashierName;
    private double amount;

    public TransactionModel(int id, String receiptNumber, Date date, String cashierName, double amount) {
        this.id = id;
        this.receiptNumber = receiptNumber;
        this.date = date;
        this.cashierName = cashierName;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public Date getDate() {
        return date;
    }

    public String getCashierName() {
        return cashierName;
    }

    public double getAmount() {
        return amount;
    }
} 