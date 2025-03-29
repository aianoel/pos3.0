package com.example.possystem.sales;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.possystem.R;
import com.example.possystem.database.POSDatabase;
import com.example.possystem.database.Product;
import com.example.possystem.database.Sale;
import com.example.possystem.database.SaleItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class SalesActivity extends AppCompatActivity implements CartAdapter.CartItemListener {

    private EditText etBarcode;
    private Button btnAdd, btnSearch, btnCheckout, btnClearCart;
    private RecyclerView rvCartItems;
    private TextView tvSubtotal, tvTax, tvTotal;
    private CartAdapter adapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private int userId;
    private double subtotal = 0.0;
    private double tax = 0.0;
    private double total = 0.0;
    private final double TAX_RATE = 0.10; // 10% tax rate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("New Sale");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get user ID from intent
        userId = getIntent().getIntExtra("USER_ID", -1);

        // Initialize views
        etBarcode = findViewById(R.id.etBarcode);
        btnAdd = findViewById(R.id.btnAdd);
        btnSearch = findViewById(R.id.btnSearch);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnClearCart = findViewById(R.id.btnClearCart);
        rvCartItems = findViewById(R.id.rvCartItems);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTax = findViewById(R.id.tvTax);
        tvTotal = findViewById(R.id.tvTotal);

        // Setup RecyclerView
        adapter = new CartAdapter(this, this);
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(adapter);

        // Set button click listeners
        btnAdd.setOnClickListener(v -> addProductByBarcode());
        btnSearch.setOnClickListener(v -> searchProducts());
        btnCheckout.setOnClickListener(v -> processSale());
        btnClearCart.setOnClickListener(v -> clearCart());
    }

    private void addProductByBarcode() {
        String barcode = etBarcode.getText().toString().trim();
        if (barcode.isEmpty()) {
            Toast.makeText(this, "Please enter a barcode", Toast.LENGTH_SHORT).show();
            return;
        }

        POSDatabase.databaseWriteExecutor.execute(() -> {
            Product product = POSDatabase.getDatabase(getApplicationContext())
                    .productDao()
                    .getProductByBarcode(barcode);

            runOnUiThread(() -> {
                if (product != null) {
                    addProductToCart(product);
                    etBarcode.setText("");
                } else {
                    Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void searchProducts() {
        // In a real app, this would open a product search activity
        // For simplicity, we'll just show a message
        Toast.makeText(this, "Product search feature would open here", Toast.LENGTH_SHORT).show();
    }

    private void addProductToCart(Product product) {
        // Check if product is already in cart
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                item.increaseQuantity();
                adapter.notifyDataSetChanged();
                updateTotals();
                return;
            }
        }

        // Add new product to cart
        CartItem newItem = new CartItem(product, 1);
        cartItems.add(newItem);
        adapter.setCartItems(cartItems);
        updateTotals();
    }

    @Override
    public void onQuantityChanged(int position, boolean isIncreased) {
        CartItem item = cartItems.get(position);
        if (isIncreased) {
            item.increaseQuantity();
        } else {
            if (!item.decreaseQuantity()) {
                // If quantity becomes 0, remove item
                cartItems.remove(position);
                adapter.notifyItemRemoved(position);
            }
        }
        adapter.notifyItemChanged(position);
        updateTotals();
    }

    private void updateTotals() {
        subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getSubtotal();
        }
        tax = subtotal * TAX_RATE;
        total = subtotal + tax;

        tvSubtotal.setText(String.format("$%.2f", subtotal));
        tvTax.setText(String.format("$%.2f", tax));
        tvTotal.setText(String.format("$%.2f", total));
    }

    private void processSale() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show payment method dialog
        showPaymentMethodDialog();
    }

    private void showPaymentMethodDialog() {
        String[] paymentMethods = {"Cash", "Credit Card", "Debit Card", "Mobile Payment"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Payment Method")
                .setItems(paymentMethods, (dialog, which) -> {
                    String selectedMethod = paymentMethods[which];
                    finalizeTransaction(selectedMethod);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void finalizeTransaction(String paymentMethod) {
        // Generate receipt number
        String receiptNumber = generateReceiptNumber();
        
        // Create sale record
        Sale sale = new Sale(
                Calendar.getInstance().getTimeInMillis(),
                total,
                paymentMethod,
                userId,
                receiptNumber
        );
        
        POSDatabase.databaseWriteExecutor.execute(() -> {
            // Insert sale and get sale ID
            long saleId = POSDatabase.getDatabase(getApplicationContext())
                    .saleDao()
                    .insertSale(sale);
            
            // Insert sale items and update inventory
            for (CartItem item : cartItems) {
                SaleItem saleItem = new SaleItem(
                        (int) saleId,
                        item.getProduct().getId(),
                        item.getQuantity(),
                        item.getProduct().getPrice(),
                        item.getSubtotal()
                );
                
                POSDatabase.getDatabase(getApplicationContext())
                        .saleDao()
                        .insertSaleItem(saleItem);
                
                // Update product stock
                POSDatabase.getDatabase(getApplicationContext())
                        .productDao()
                        .decreaseStock(item.getProduct().getId(), item.getQuantity());
            }
            
            runOnUiThread(() -> {
                // Show receipt
                Intent intent = new Intent(SalesActivity.this, ReceiptActivity.class);
                intent.putExtra("SALE_ID", (int) saleId);
                intent.putExtra("RECEIPT_NUMBER", receiptNumber);
                startActivity(intent);
                
                // Clear cart after successful transaction
                clearCart();
            });
        });
    }

    private String generateReceiptNumber() {
        // Format: YYYYMMDD-XXXX (Year-Month-Day-Random4Digits)
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        
        // Generate random 4-digit number
        int random = (int) (Math.random() * 10000);
        
        return String.format("%04d%02d%02d-%04d", year, month, day, random);
    }

    private void clearCart() {
        cartItems.clear();
        adapter.setCartItems(cartItems);
        updateTotals();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 