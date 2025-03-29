package com.example.possystem.sales;

import com.example.possystem.database.Product;

public class CartItem {
    private Product product;
    private int quantity;
    private double subtotal;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        calculateSubtotal();
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }

    private void calculateSubtotal() {
        this.subtotal = product.getPrice() * quantity;
    }

    public void increaseQuantity() {
        quantity++;
        calculateSubtotal();
    }

    public boolean decreaseQuantity() {
        if (quantity > 1) {
            quantity--;
            calculateSubtotal();
            return true;
        }
        return false;
    }
} 