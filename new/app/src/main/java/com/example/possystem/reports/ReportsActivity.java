package com.example.possystem.reports;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.possystem.R;
import com.example.possystem.database.POSDatabase;
import com.example.possystem.database.Sale;
import com.example.possystem.database.User;
import com.example.possystem.inventory.InventoryActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {

    private TextInputEditText etStartDate, etEndDate;
    private Button btnGenerateReport, btnExport, btnLowStock, btnInventoryReport;
    private TextView tvTotalSales, tvTransactionCount, tvAverageSale;
    private RecyclerView rvTransactions;
    private CardView cardReportResults;
    
    private Calendar startDateCalendar = Calendar.getInstance();
    private Calendar endDateCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Reports");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        btnGenerateReport = findViewById(R.id.btnGenerateReport);
        btnExport = findViewById(R.id.btnExport);
        btnLowStock = findViewById(R.id.btnLowStock);
        btnInventoryReport = findViewById(R.id.btnInventoryReport);
        tvTotalSales = findViewById(R.id.tvTotalSales);
        tvTransactionCount = findViewById(R.id.tvTransactionCount);
        tvAverageSale = findViewById(R.id.tvAverageSale);
        rvTransactions = findViewById(R.id.rvTransactions);
        cardReportResults = findViewById(R.id.cardReportResults);

        // Setup RecyclerView
        adapter = new TransactionAdapter();
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);

        // Set default dates (current week)
        startDateCalendar.set(Calendar.DAY_OF_WEEK, startDateCalendar.getFirstDayOfWeek());
        endDateCalendar.setTime(new Date());
        updateDateFields();

        // Setup date pickers
        etStartDate.setOnClickListener(v -> showDatePickerDialog(true));
        etEndDate.setOnClickListener(v -> showDatePickerDialog(false));

        // Button click listeners
        btnGenerateReport.setOnClickListener(v -> generateReport());
        
        btnExport.setOnClickListener(v -> {
            // In a real app, this would export data to CSV or PDF
            Toast.makeText(this, "Report would be exported here", Toast.LENGTH_SHORT).show();
        });
        
        btnLowStock.setOnClickListener(v -> {
            // Open inventory with low stock filter
            Intent intent = new Intent(ReportsActivity.this, InventoryActivity.class);
            intent.putExtra("SHOW_LOW_STOCK", true);
            startActivity(intent);
        });
        
        btnInventoryReport.setOnClickListener(v -> {
            // Open inventory
            Intent intent = new Intent(ReportsActivity.this, InventoryActivity.class);
            startActivity(intent);
        });
    }

    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = isStartDate ? startDateCalendar : endDateCalendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    if (isStartDate) {
                        startDateCalendar.set(year, month, dayOfMonth);
                    } else {
                        endDateCalendar.set(year, month, dayOfMonth);
                    }
                    updateDateFields();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateFields() {
        etStartDate.setText(dateFormat.format(startDateCalendar.getTime()));
        etEndDate.setText(dateFormat.format(endDateCalendar.getTime()));
    }

    private void generateReport() {
        // Adjust end date to end of day
        Calendar endOfDay = (Calendar) endDateCalendar.clone();
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);
        
        long startTime = startDateCalendar.getTimeInMillis();
        long endTime = endOfDay.getTimeInMillis();
        
        // Validate date range
        if (startTime > endTime) {
            Toast.makeText(this, "Start date cannot be after end date", Toast.LENGTH_SHORT).show();
            return;
        }
        
        POSDatabase.databaseWriteExecutor.execute(() -> {
            POSDatabase db = POSDatabase.getDatabase(getApplicationContext());
            
            // Get sales data for the date range
            double totalSales = db.saleDao().getSalesTotal(startTime, endTime);
            int transactionCount = db.saleDao().getSalesCount(startTime, endTime);
            double averageSale = transactionCount > 0 ? totalSales / transactionCount : 0;
            
            // Get recent transactions
            List<Sale> sales = db.saleDao().getSalesByDateRange(startTime, endTime);
            List<TransactionModel> transactions = new ArrayList<>();
            
            for (Sale sale : sales) {
                // Get cashier name
                User cashier = db.userDao().getUserById(sale.getCashierId());
                String cashierName = (cashier != null) ? cashier.getFullName() : "Unknown";
                
                transactions.add(new TransactionModel(
                        sale.getId(),
                        sale.getReceiptNumber(),
                        new Date(sale.getTimestamp()),
                        cashierName,
                        sale.getTotalAmount()
                ));
            }
            
            runOnUiThread(() -> {
                // Update UI with results
                tvTotalSales.setText(String.format("$%.2f", totalSales));
                tvTransactionCount.setText(String.valueOf(transactionCount));
                tvAverageSale.setText(String.format("$%.2f", averageSale));
                
                adapter.setTransactions(transactions);
                
                // Show results card
                cardReportResults.setVisibility(View.VISIBLE);
            });
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