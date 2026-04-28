package com.smartshop.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Product operations.
 * Uses barcode as unique index for fast <50ms lookups.
 */
@Dao
interface ProductDao {
    
    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): Product?
    
    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    fun getProductByBarcodeFlow(barcode: String): Flow<Product?>
    
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE stockQuantity > 0 ORDER BY name ASC")
    fun getInStockProducts(): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): Product?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long
    
    @Update
    suspend fun updateProduct(product: Product)
    
    @Delete
    suspend fun deleteProduct(product: Product)
    
    @Query("UPDATE products SET stockQuantity = stockQuantity - :amount WHERE id = :productId AND stockQuantity >= :amount")
    suspend fun decrementStock(productId: Long, amount: Int): Int
    
    @Query("SELECT COUNT(*) FROM products")
    fun getProductCount(): Flow<Int>
    
    @Query("SELECT SUM(stockQuantity * priceUgx) FROM products")
    fun getTotalInventoryValue(): Flow<Double?>
    
    @Query("SELECT SUM(stockQuantity) FROM products")
    fun getTotalStock(): Flow<Int?>
    
    @Query("SELECT * FROM products WHERE stockQuantity <= :threshold ORDER BY stockQuantity ASC")
    fun getLowStockProducts(threshold: Int): Flow<List<Product>>
    
    @Query("UPDATE products SET stockQuantity = :quantity WHERE id = :productId")
    suspend fun updateStockQuantity(productId: Long, quantity: Int)
}