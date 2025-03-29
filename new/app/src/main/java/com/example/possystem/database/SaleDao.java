package com.example.possystem.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import java.util.List;

@Dao
public interface SaleDao {
    @Insert
    long insertSale(Sale sale);

    @Insert
    void insertSaleItem(SaleItem saleItem);

    @Query("SELECT * FROM sales WHERE id = :saleId LIMIT 1")
    Sale getSaleById(int saleId);

    @Transaction
    @Query("SELECT * FROM sales ORDER BY timestamp DESC")
    LiveData<List<Sale>> getAllSales();

    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    List<SaleItem> getSaleItemsBySaleId(int saleId);
    
    @Query("SELECT SUM(totalAmount) FROM sales WHERE timestamp BETWEEN :startTime AND :endTime")
    double getSalesTotal(long startTime, long endTime);
    
    @Query("SELECT COUNT(*) FROM sales WHERE timestamp BETWEEN :startTime AND :endTime")
    int getSalesCount(long startTime, long endTime);
    
    @Query("SELECT * FROM sales WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    List<Sale> getSalesByDateRange(long startTime, long endTime);
} 