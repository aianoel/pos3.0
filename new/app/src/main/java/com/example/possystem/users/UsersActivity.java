package com.example.possystem.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.possystem.R;
import com.example.possystem.database.User;

public class UsersActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {

    private RecyclerView rvUsers;
    private Button btnAddUser;
    private UserAdapter adapter;
    private UsersViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("User Management");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        rvUsers = findViewById(R.id.rvUsers);
        btnAddUser = findViewById(R.id.btnAddUser);

        // Setup RecyclerView
        adapter = new UserAdapter(this, this);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(adapter);

        // Setup ViewModel
        viewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        viewModel.getAllUsers().observe(this, users -> {
            adapter.setUsers(users);
        });

        // Add user button
        btnAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(UsersActivity.this, UserEditActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(UsersActivity.this, UserEditActivity.class);
        intent.putExtra("USER_ID", user.getId());
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