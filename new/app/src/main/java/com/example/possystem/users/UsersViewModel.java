package com.example.possystem.users;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.possystem.database.POSDatabase;
import com.example.possystem.database.User;
import com.example.possystem.database.UserDao;

import java.util.List;

public class UsersViewModel extends AndroidViewModel {

    private final UserDao userDao;
    private final LiveData<List<User>> allUsers;

    public UsersViewModel(@NonNull Application application) {
        super(application);
        POSDatabase db = POSDatabase.getDatabase(application);
        userDao = db.userDao();
        allUsers = userDao.getAllUsers();
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public void insert(User user) {
        POSDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insert(user);
        });
    }

    public void update(User user) {
        POSDatabase.databaseWriteExecutor.execute(() -> {
            userDao.update(user);
        });
    }

    public void delete(User user) {
        POSDatabase.databaseWriteExecutor.execute(() -> {
            userDao.delete(user);
        });
    }
} 