package com.example.possystem;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.possystem.database.POSDatabase;
import com.example.possystem.database.User;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName;
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private AutoCompleteTextView actvRole;
    private Button btnRegister;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        actvRole = findViewById(R.id.actvRole);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);

        // Setup role dropdown
        String[] roles = new String[]{"cashier", "manager", "admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
        actvRole.setAdapter(adapter);

        // Register button click listener
        btnRegister.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String role = actvRole.getText().toString().trim();

            // Validate input
            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create user in background thread
            POSDatabase.databaseWriteExecutor.execute(() -> {
                // Check if username already exists
                User existingUser = POSDatabase.getDatabase(getApplicationContext())
                        .userDao()
                        .getUserByUsername(username);

                if (existingUser != null) {
                    runOnUiThread(() -> Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Create new user
                User newUser = new User(username, password, fullName, role);
                POSDatabase.getDatabase(getApplicationContext())
                        .userDao()
                        .insert(newUser);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to login
                });
            });
        });

        // Back button click listener
        btnBack.setOnClickListener(v -> finish());
    }
} 