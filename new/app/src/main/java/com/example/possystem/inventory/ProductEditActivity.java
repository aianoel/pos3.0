package com.example.possystem.inventory;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.possystem.R;
import com.example.possystem.database.POSDatabase;
import com.example.possystem.database.Product;
import com.google.android.material.textfield.TextInputEditText;

public class ProductEditActivity extends AppCompatActivity {

    private TextInputEditText etProductName, etDescription, etPrice, etStock, etBarcode;
    private AutoCompleteTextView actvCategory;
    private Button btnSave, btnDelete;
    private InventoryViewModel viewModel;
    
    private int productId = -1;
    private Product currentProduct;
    private String userRole;
    private boolean isNewProduct = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Initialize views
        etProductName = findViewById(R.id.etProductName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etStock = findViewById(R.id.etStock);
        etBarcode = findViewById(R.id.etBarcode);
        actvCategory = findViewById(R.id.actvCategory);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        // Setup ViewModel
        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        // Get data from intent
        if (getIntent().hasExtra("PRODUCT_ID")) {
            productId = getIntent().getIntExtra("PRODUCT_ID", -1);
            isNewProduct = false;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Product");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            
            // Load product data
            loadProductData();
            
            // Show delete button
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add New Product");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        
        // Get user role
        userRole = getIntent().getStringExtra("USER_ROLE");
        
        // Disable editing for non-admin/manager users
        if (userRole != null && !userRole.equals("admin") && !userRole.equals("manager")) {
            disableEditing();
        }

        // Setup category dropdown
        setupCategoryDropdown();

        // Save button click listener
        btnSave.setOnClickListener(v -> saveProduct());

        // Delete button click listener
        btnDelete.setOnClickListener(v -> confirmDeleteProduct());
    }

    private void setupCategoryDropdown() {
        String[] categories = new String[]{
                "Beverages", "Snacks", "Bakery", "Dairy", "Produce", 
                "Meat", "Seafood", "Frozen Foods", "Canned Goods", "Other"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(adapter);
    }

    private void loadProductData() {
        POSDatabase.databaseWriteExecutor.execute(() -> {
            currentProduct = POSDatabase.getDatabase(getApplicationContext())
                    .productDao()
                    .getProductById(productId);
            
            runOnUiThread(() -> {
                if (currentProduct != null) {
                    etProductName.setText(currentProduct.getName());
                    etDescription.setText(currentProduct.getDescription());
                    etPrice.setText(String.valueOf(currentProduct.getPrice()));
                    etStock.setText(String.valueOf(currentProduct.getStockQuantity()));
                    etBarcode.setText(currentProduct.getBarcode());
                    actvCategory.setText(currentProduct.getCategory(), false);
                }
            });
        });
    }

    private void saveProduct() {
        String name = etProductName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();
        String barcode = etBarcode.getText().toString().trim();
        String category = actvCategory.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || barcode.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            if (price <= 0) {
                Toast.makeText(this, "Price must be greater than zero", Toast.LENGTH_SHORT).show();
                return;
            }

            if (stock < 0) {
                Toast.makeText(this, "Stock cannot be negative", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create or update product
            if (isNewProduct) {
                Product newProduct = new Product(name, description, price, stock, barcode, category);
                viewModel.insert(newProduct);
                Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
            } else {
                currentProduct.setName(name);
                currentProduct.setDescription(description);
                currentProduct.setPrice(price);
                currentProduct.setStockQuantity(stock);
                currentProduct.setBarcode(barcode);
                currentProduct.setCategory(category);
                viewModel.update(currentProduct);
                Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
            }
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price or stock value", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeleteProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Product");
        builder.setMessage("Are you sure you want to delete this product?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            viewModel.delete(currentProduct);
            Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
            finish();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void disableEditing() {
        etProductName.setEnabled(false);
        etDescription.setEnabled(false);
        etPrice.setEnabled(false);
        etStock.setEnabled(false);
        etBarcode.setEnabled(false);
        actvCategory.setEnabled(false);
        btnSave.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);
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