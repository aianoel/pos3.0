package com.example.possystem.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.possystem.R;
import com.example.possystem.database.Product;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private Button btnAddProduct;
    private EditText etSearch;
    private InventoryViewModel viewModel;
    private String userRole;
    private List<Product> allProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Inventory Management");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get user role from intent
        userRole = getIntent().getStringExtra("USER_ROLE");

        // Initialize views
        rvProducts = findViewById(R.id.rvProducts);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        etSearch = findViewById(R.id.etSearch);

        // Setup RecyclerView
        adapter = new ProductAdapter(this, this);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(adapter);

        // Setup ViewModel
        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);
        viewModel.getAllProducts().observe(this, products -> {
            allProducts = products;
            adapter.setProducts(products);
        });

        // Add product button
        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, ProductEditActivity.class);
            startActivity(intent);
        });

        // Disable add button for non-admin/manager users
        if (!userRole.equals("admin") && !userRole.equals("manager")) {
            btnAddProduct.setEnabled(false);
        }

        // Setup search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterProducts(s.toString());
            }
        });
    }

    private void filterProducts(String query) {
        if (query.isEmpty()) {
            adapter.setProducts(allProducts);
            return;
        }

        List<Product> filteredList = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(query.toLowerCase()) || 
                product.getBarcode().contains(query) ||
                product.getCategory().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        adapter.setProducts(filteredList);
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(InventoryActivity.this, ProductEditActivity.class);
        intent.putExtra("PRODUCT_ID", product.getId());
        intent.putExtra("USER_ROLE", userRole);
        startActivity(intent);
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