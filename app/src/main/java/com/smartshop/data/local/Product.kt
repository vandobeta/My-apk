package com.smartshop.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Product entity representing an item in the inventory.
 * Barcode is used as the unique identifier for fast lookups.
 */
@Entity(
    tableName = "products",
    indices = [Index(value = ["barcode"], unique = true)]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val barcode: String,
    val name: String,
    val priceUgx: Double,
    val stockQuantity: Int,
    val category: String = "",        // e.g., "Drinks", "Electronics", "Snacks"
    val brand: String = "",          // e.g., "Riham", "Coca Cola", "Samsung"
    val imagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)