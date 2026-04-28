package com.smartshop.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database for SmartShop offline-first inventory management.
 * Uses indexed barcode for fast <50ms lookups.
 */
@Database(
    entities = [Product::class, CartItem::class, SaleLog::class],
    version = 1,
    exportSchema = false
)
abstract class SmartShopDatabase : RoomDatabase() {
    
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun saleLogDao(): SaleLogDao
    
    companion object {
        private const val DATABASE_NAME = "smartshop_db"
        
        @Volatile
        private var INSTANCE: SmartShopDatabase? = null
        
        fun getInstance(context: Context): SmartShopDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmartShopDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}