package com.example.possystem.sales;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.possystem.R;
import com.example.possystem.database.POSDatabase;
import com.example.possystem.database.Product;
import com.example.possystem.database.Sale;
import com.example.possystem.database.SaleItem;
import com.example.possystem.database.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReceiptActivity extends AppCompatActivity {

    private TextView tvReceiptNumber, tvDate, tvCashier, tvPaymentMethod;
    private TextView tvSubtotal, tvTax, tvTotal;
    private RecyclerView rvReceiptItems;
    private Button btnPrint, btnDone;
    private ReceiptItemAdapter adapter;
    
    private int saleId;
    private String receiptNumber;
    private final double TAX_RATE = 0.10; // 10% tax

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Receipt");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get data from intent
        saleId = getIntent().getIntExtra("SALE_ID", -1);
        receiptNumber = getIntent().getStringExtra("RECEIPT_NUMBER");

        // Initialize views
        tvReceiptNumber = findViewById(R.id.tvReceiptNumber);
        tvDate = findViewById(R.id.tvDate);
        tvCashier = findViewById(R.id.tvCashier);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        rvReceiptItems = findViewById(R.id.rvReceiptItems);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTax = findViewById(R.id.tvTax);
        tvTotal = findViewById(R.id.tvTotal);
        btnPrint = findViewById(R.id.btnPrint);
        btnDone = findViewById(R.id.btnDone);

        // Setup RecyclerView
        adapter = new ReceiptItemAdapter();
        rvReceiptItems.setLayoutManager(new LinearLayoutManager(this));
        rvReceiptItems.setAdapter(adapter);

        // Load receipt data
        loadReceiptData();

        // Button click listeners
        btnPrint.setOnClickListener(v -> {
            // In a real app, this would connect to a printer
            Toast.makeText(this, "Printing receipt...", Toast.LENGTH_SHORT).show();
        });

        btnDone.setOnClickListener(v -> finish());
    }

    private void loadReceiptData() {
        POSDatabase.databaseWriteExecutor.execute(() -> {
            // Get sale from database
            POSDatabase db = POSDatabase.getDatabase(getApplicationContext());
            Sale sale = db.saleDao().getSaleById(saleId);
            
            if (sale != null) {
                // Get sale items
                List<SaleItem> saleItems = db.saleDao().getSaleItemsBySaleId(saleId);
                List<ReceiptItemModel> receiptItems = new ArrayList<>();
                
                // Get cashier name
                User cashier = db.userDao().getUserById(sale.getCashierId());
                String cashierName = (cashier != null) ? cashier.getFullName() : "Unknown";
                
                // Format date
                String dateStr = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                        .format(new Date(sale.getTimestamp()));
                
                // Calculate totals
                double subtotal = 0;
                for (SaleItem item : saleItems) {
                    // Get product details
                    Product product = db.productDao().getProductById(item.getProductId());
                    if (product != null) {
                        receiptItems.add(new ReceiptItemModel(
                                product.getName(),
                                item.getUnitPrice(),
                                item.getQuantity(),
                                item.getSubtotal()
                        ));
                        subtotal += item.getSubtotal();
                    }
                }
                
                double tax = subtotal * TAX_RATE;
                double total = subtotal + tax;
                
                // Update UI on main thread
                runOnUiThread(() -> {
                    tvReceiptNumber.setText(sale.getReceiptNumber());
                    tvDate.setText(dateStr);
                    tvCashier.setText(cashierName);
                    tvPaymentMethod.setText(sale.getPaymentMethod());
                    
                    adapter.setReceiptItems(receiptItems);
                    
                    tvSubtotal.setText(String.format("$%.2f", subtotal));
                    tvTax.setText(String.format("$%.2f", tax));
                    tvTotal.setText(String.format("$%.2f", total));
                });
            }
        });
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