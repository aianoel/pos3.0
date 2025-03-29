package com.example.possystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.possystem.inventory.InventoryActivity;
import com.example.possystem.sales.SalesActivity;
import com.example.possystem.reports.ReportsActivity;
import com.example.possystem.users.UsersActivity;

public class MainActivity extends AppCompatActivity {

    private CardView cardSales, cardInventory, cardReports, cardUsers;
    private TextView tvWelcome;
    private int userId;
    private String userName;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("POS System");
        }

        // Get user data from intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        userName = getIntent().getStringExtra("USER_NAME");
        userRole = getIntent().getStringExtra("USER_ROLE");

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome);
        cardSales = findViewById(R.id.cardSales);
        cardInventory = findViewById(R.id.cardInventory);
        cardReports = findViewById(R.id.cardReports);
        cardUsers = findViewById(R.id.cardUsers);

        // Set welcome message
        tvWelcome.setText("Welcome, " + userName + "!");

        // Show/hide features based on user role
        if (!userRole.equals("admin")) {
            cardUsers.setVisibility(View.GONE);
        }

        // Set click listeners
        cardSales.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SalesActivity.class);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("USER_ROLE", userRole);
            startActivity(intent);
        });

        cardInventory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
            intent.putExtra("USER_ROLE", userRole);
            startActivity(intent);
        });

        cardReports.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReportsActivity.class);
            startActivity(intent);
        });

        cardUsers.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            // Logout and return to login screen
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 