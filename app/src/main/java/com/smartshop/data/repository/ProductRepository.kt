package com.smartshop.data.repository

import com.smartshop.data.local.CartDao
import com.smartshop.data.local.CartItem
import com.smartshop.data.local.Product
import com.smartshop.data.local.ProductDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Product data operations.
 * Provides offline-first access with indexed barcode lookups.
 */
@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {
    
    /**
     * Get product by barcode - uses indexed lookup for <50ms response.
     */
    suspend fun getProductByBarcode(barcode: String): Product? {
        return productDao.getProductByBarcode(barcode)
    }
    
    /**
     * Observe product by barcode with Flow.
     */
    fun observeProductByBarcode(barcode: String): Flow<Product?> {
        return productDao.getProductByBarcodeFlow(barcode)
    }
    
    /**
     * Get all products.
     */
    fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts()
    }
    
    /**
     * Get products with stock available.
     */
    fun getInStockProducts(): Flow<List<Product>> {
        return productDao.getInStockProducts()
    }
    
    /**
     * Get product by ID.
     */
    suspend fun getProductById(id: Long): Product? {
        return productDao.getProductById(id)
    }
    
    /**
     * Add or update a product.
     */
    suspend fun saveProduct(product: Product): Long {
        return productDao.insertProduct(product)
    }
    
    /**
     * Delete a product.
     */
    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
    }
    
    /**
     * Decrement stock after a sale.
     * Returns true if successful, false if insufficient stock.
     */
    suspend fun decrementStock(productId: Long, amount: Int): Boolean {
        val result = productDao.decrementStock(productId, amount)
        return result > 0
    }
    
    /**
     * Get total product count.
     */
    fun getProductCount(): Flow<Int> {
        return productDao.getProductCount()
    }
    
    /**
     * Get total inventory value.
     */
    fun getTotalInventoryValue(): Flow<Double?> {
        return productDao.getTotalInventoryValue()
    }
    
    /**
     * Get total stock quantity.
     */
    fun getTotalStock(): Flow<Int?> {
        return productDao.getTotalStock()
    }
    
    /**
     * Get products with low stock (below threshold).
     */
    fun getLowStockProducts(threshold: Int = 20): Flow<List<Product>> {
        return productDao.getLowStockProducts(threshold)
    }
    
    /**
     * Add stock to a product.
     */
    suspend fun addStock(productId: Long, quantity: Int) {
        val product = productDao.getProductById(productId)
        if (product != null) {
            productDao.updateStockQuantity(productId, product.stockQuantity + quantity)
        }
    }
    
    /**
     * Remove stock from a product.
     */
    suspend fun removeStock(productId: Long, quantity: Int) {
        val product = productDao.getProductById(productId)
        if (product != null) {
            val newQty = (product.stockQuantity - quantity).coerceAtLeast(0)
            productDao.updateStockQuantity(productId, newQty)
        }
    }
}