package com.smartshop.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Cart item representing a product added to the shopping cart.
 * Stores product reference and quantity.
 */
@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val productBarcode: String,
    val productName: String,
    val productPrice: Double,
    val productImagePath: String? = null,
    val quantity: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
) {
    val totalPrice: Double
        get() = productPrice * quantity
}