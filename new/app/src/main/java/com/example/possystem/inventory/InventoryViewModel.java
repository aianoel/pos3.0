package com.example.possystem.inventory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.possystem.database.POSDatabase;
import com.example.possystem.database.Product;
import com.example.possystem.database.ProductDao;

import java.util.List;

public class InventoryViewModel extends AndroidViewModel {

    private final ProductDao productDao;
    private final LiveData<List<Product>> allProducts;
    private final LiveData<List<Product>> lowStockProducts;

    public InventoryViewModel(@NonNull Application application) {
        super(application);
        POSDatabase db = POSDatabase.getDatabase(application);
        productDao = db.productDao();
        allProducts = productDao.getAllProducts();
        lowStockProducts = productDao.getLowStockProducts();
    }

    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }

    public LiveData<List<Product>> getLowStockProducts() {
        return lowStockProducts;
    }

    public void insert(Product product) {
        POSDatabase.databaseWriteExecutor.execute(() -> {
            productDao.insert(product);
        });
    }

    public void update(Product product) {
        POSDatabase.databaseWriteExecutor.execute(() -> {
            productDao.update(product);
        });
    }

    public void delete(Product product) {
        POSDatabase.databaseWriteExecutor.execute(() -> {
            productDao.delete(product);
        });
    }
} 