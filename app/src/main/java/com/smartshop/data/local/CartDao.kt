package com.smartshop.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Cart operations.
 */
@Dao
interface CartDao {
    
    @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
    fun getAllCartItems(): Flow<List<CartItem>>
    
    @Query("SELECT SUM(productPrice * quantity) FROM cart_items")
    fun getCartTotal(): Flow<Double?>
    
    @Query("SELECT SUM(quantity) FROM cart_items")
    fun getCartItemCount(): Flow<Int?>
    
    @Query("SELECT * FROM cart_items WHERE productId = :productId LIMIT 1")
    suspend fun getCartItemByProductId(productId: Long): CartItem?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem): Long
    
    @Update
    suspend fun updateCartItem(cartItem: CartItem)
    
    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)
    
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
    
    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun removeFromCart(productId: Long)
    
    @Query("UPDATE cart_items SET quantity = quantity + 1 WHERE productId = :productId")
    suspend fun incrementQuantity(productId: Long)
    
    @Query("UPDATE cart_items SET quantity = quantity - 1 WHERE productId = :productId AND quantity > 1")
    suspend fun decrementQuantity(productId: Long)
}