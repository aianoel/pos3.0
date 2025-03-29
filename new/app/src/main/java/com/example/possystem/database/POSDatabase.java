package com.example.possystem.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Product.class, Sale.class, SaleItem.class}, version = 1, exportSchema = false)
public abstract class POSDatabase extends RoomDatabase {
    
    public abstract UserDao userDao();
    public abstract ProductDao productDao();
    public abstract SaleDao saleDao();
    
    private static volatile POSDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    
    public static POSDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (POSDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            POSDatabase.class, "pos_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            
            databaseWriteExecutor.execute(() -> {
                // Populate the database with initial data
                UserDao userDao = INSTANCE.userDao();
                ProductDao productDao = INSTANCE.productDao();
                
                // Add admin user
                User admin = new User("admin", "admin123", "Administrator", "admin");
                userDao.insert(admin);
                
                // Add some sample products
                Product product1 = new Product("Cola", "Refreshing soda", 1.99, 100, "1234567890", "Beverages");
                Product product2 = new Product("Chips", "Potato chips", 2.49, 50, "2345678901", "Snacks");
                Product product3 = new Product("Bread", "White bread", 3.99, 30, "3456789012", "Bakery");
                
                productDao.insert(product1);
                productDao.insert(product2);
                productDao.insert(product3);
            });
        }
    };
} 