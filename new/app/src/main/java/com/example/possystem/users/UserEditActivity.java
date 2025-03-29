package com.example.possystem.users;

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
import com.example.possystem.database.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class UserEditActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etUsername, etPassword, etConfirmPassword;
    private TextInputLayout passwordLayout, confirmPasswordLayout;
    private AutoCompleteTextView actvRole;
    private Button btnSave, btnResetPassword, btnDelete;
    private UsersViewModel viewModel;
    
    private int userId = -1;
    private User currentUser;
    private boolean isNewUser = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Initialize views
        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        actvRole = findViewById(R.id.actvRole);
        btnSave = findViewById(R.id.btnSave);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnDelete = findViewById(R.id.btnDelete);

        // Setup ViewModel
        viewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        // Get data from intent
        if (getIntent().hasExtra("USER_ID")) {
            userId = getIntent().getIntExtra("USER_ID", -1);
            isNewUser = false;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit User");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            
            // Load user data
            loadUserData();
            
            // Show additional buttons for existing users
            btnResetPassword.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            
            // Hide password fields initially when editing
            passwordLayout.setVisibility(View.GONE);
            confirmPasswordLayout.setVisibility(View.GONE);
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add New User");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        // Setup role dropdown
        setupRoleDropdown();

        // Save button click listener
        btnSave.setOnClickListener(v -> saveUser());

        // Reset password button click listener
        btnResetPassword.setOnClickListener(v -> {
            // Show password fields
            passwordLayout.setVisibility(View.VISIBLE);
            confirmPasswordLayout.setVisibility(View.VISIBLE);
            btnResetPassword.setVisibility(View.GONE);
        });

        // Delete button click listener
        btnDelete.setOnClickListener(v -> confirmDeleteUser());
    }

    private void setupRoleDropdown() {
        String[] roles = new String[]{"admin", "manager", "cashier"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, roles);
        actvRole.setAdapter(adapter);
    }

    private void loadUserData() {
        POSDatabase.databaseWriteExecutor.execute(() -> {
            currentUser = POSDatabase.getDatabase(getApplicationContext())
                    .userDao()
                    .getUserById(userId);
            
            runOnUiThread(() -> {
                if (currentUser != null) {
                    etFullName.setText(currentUser.getFullName());
                    etUsername.setText(currentUser.getUsername());
                    actvRole.setText(currentUser.getRole(), false);
                }
            });
        });
    }

    private void saveUser() {
        String fullName = etFullName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String role = actvRole.getText().toString().trim();

        // Validate inputs
        if (fullName.isEmpty() || username.isEmpty() || role.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isNewUser && (password.isEmpty() || confirmPassword.isEmpty())) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isNewUser || passwordLayout.getVisibility() == View.VISIBLE) {
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Check username availability for new users
        if (isNewUser) {
            POSDatabase.databaseWriteExecutor.execute(() -> {
                User existingUser = POSDatabase.getDatabase(getApplicationContext())
                        .userDao()
                        .getUserByUsername(username);
                
                if (existingUser != null) {
                    runOnUiThread(() -> Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show());
                    return;
                }
                
                // Create and save new user
                User newUser = new User(username, password, fullName, role);
                viewModel.insert(newUser);
                
                runOnUiThread(() -> {
                    Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        } else {
            // Update existing user
            currentUser.setFullName(fullName);
            currentUser.setUsername(username);
            currentUser.setRole(role);
            
            // Update password if reset was requested
            if (passwordLayout.getVisibility() == View.VISIBLE && !password.isEmpty()) {
                currentUser.setPassword(password);
            }
            
            viewModel.update(currentUser);
            Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void confirmDeleteUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete User");
        builder.setMessage("Are you sure you want to delete this user?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            viewModel.delete(currentUser);
            Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
            finish();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
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