package com.example.possystem.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "sale_items",
        foreignKeys = {
            @ForeignKey(entity = Sale.class,
                    parentColumns = "id",
                    childColumns = "saleId",
                    onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = Product.class,
                    parentColumns = "id",
                    childColumns = "productId",
                    onDelete = ForeignKey.RESTRICT)
        })
public class SaleItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int saleId;
    private int productId;
    private int quantity;
    private double unitPrice;
    private double subtotal;

    public SaleItem(int saleId, int productId, int quantity, 
                   double unitPrice, double subtotal) {
        this.saleId = saleId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
} 