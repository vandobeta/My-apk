package com.smartshop.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Sale log entry recording a completed transaction.
 * Created when worker taps "Paid" at checkout.
 */
@Entity(tableName = "sale_logs")
data class SaleLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemsJson: String, // JSON string of sold items
    val totalAmount: Double,
    val itemCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)