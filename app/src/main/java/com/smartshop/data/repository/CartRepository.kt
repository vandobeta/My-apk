package com.smartshop.data.repository

import com.smartshop.data.local.CartDao
import com.smartshop.data.local.CartItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Cart data operations.
 */
@Singleton
class CartRepository @Inject constructor(
    private val cartDao: CartDao
) {
    
    /**
     * Observe all cart items.
     */
    fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getAllCartItems()
    }
    
    /**
     * Get cart total in UGX.
     */
    fun getCartTotal(): Flow<Double?> {
        return cartDao.getCartTotal()
    }
    
    /**
     * Get total item count in cart.
     */
    fun getCartItemCount(): Flow<Int?> {
        return cartDao.getCartItemCount()
    }
    
    /**
     * Add item to cart or increment quantity if exists.
     */
    suspend fun addToCart(cartItem: CartItem) {
        val existing = cartDao.getCartItemByProductId(cartItem.productId)
        if (existing != null) {
            cartDao.incrementQuantity(cartItem.productId)
        } else {
            cartDao.insertCartItem(cartItem)
        }
    }
    
    /**
     * Update cart item quantity.
     */
    suspend fun updateQuantity(cartItem: CartItem) {
        cartDao.updateCartItem(cartItem)
    }
    
    /**
     * Increment quantity for a cart item.
     */
    suspend fun incrementQuantity(productId: Long) {
        cartDao.incrementQuantity(productId)
    }
    
    /**
     * Decrement quantity for a cart item (removes if goes to 0).
     */
    suspend fun decrementQuantity(productId: Long) {
        val item = cartDao.getCartItemByProductId(productId)
        if (item != null && item.quantity <= 1) {
            cartDao.removeFromCart(productId)
        } else {
            cartDao.decrementQuantity(productId)
        }
    }
    
    /**
     * Remove item from cart.
     */
    suspend fun removeFromCart(productId: Long) {
        cartDao.removeFromCart(productId)
    }
    
    /**
     * Clear entire cart.
     */
    suspend fun clearCart() {
        cartDao.clearCart()
    }
}