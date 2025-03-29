package com.example.possystem.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    Product getProductById(int id);
    
    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    Product getProductByBarcode(String barcode);

    @Query("SELECT * FROM products ORDER BY name ASC")
    LiveData<List<Product>> getAllProducts();
    
    @Query("SELECT * FROM products WHERE stockQuantity <= 5 ORDER BY stockQuantity ASC")
    LiveData<List<Product>> getLowStockProducts();
    
    @Query("UPDATE products SET stockQuantity = stockQuantity - :quantity WHERE id = :productId")
    void decreaseStock(int productId, int quantity);
} 