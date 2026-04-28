package com.smartshop.data.repository

import com.smartshop.data.local.SaleLog
import com.smartshop.data.local.SaleLogDao
import com.smartshop.ui.dashboard.TopSellingItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for SaleLog data operations.
 */
@Singleton
class SaleRepository @Inject constructor(
    private val saleLogDao: SaleLogDao
) {
    
    /**
     * Get all sales ordered by most recent.
     */
    fun getAllSales(): Flow<List<SaleLog>> {
        return saleLogDao.getAllSales()
    }
    
    /**
     * Get recent sales with limit.
     */
    fun getRecentSales(limit: Int = 100): Flow<List<SaleLog>> {
        return saleLogDao.getRecentSales(limit)
    }
    
    /**
     * Get sales since a timestamp.
     */
    fun getSalesSince(startTime: Long): Flow<List<SaleLog>> {
        return saleLogDao.getSalesSince(startTime)
    }
    
    /**
     * Get sales from today.
     */
    fun getTodaySales(): Flow<List<SaleLog>> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return saleLogDao.getSalesSince(calendar.timeInMillis)
    }
    
    /**
     * Get total sales amount.
     */
    fun getTotalSales(): Flow<Double?> {
        return saleLogDao.getTotalSales()
    }
    
    /**
     * Get today's sales total.
     */
    fun getTodaySalesTotal(): Flow<Double?> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return saleLogDao.getSalesSinceTotal(calendar.timeInMillis)
    }
    
    /**
     * Get total sales count.
     */
    fun getSalesCount(): Flow<Int> {
        return saleLogDao.getSalesCount()
    }
    
    /**
     * Get total items sold.
     */
    fun getTotalItemsSold(): Flow<Int?> {
        return saleLogDao.getTotalItemsSold()
    }
    
    /**
     * Log a new sale.
     */
    suspend fun logSale(itemsJson: String, totalAmount: Double, itemCount: Int): Long {
        val saleLog = SaleLog(
            itemsJson = itemsJson,
            totalAmount = totalAmount,
            itemCount = itemCount
        )
        return saleLogDao.insertSale(saleLog)
    }
    
    /**
     * Get top selling items from sale logs.
     */
    fun getTopSellingItems(limit: Int = 5): Flow<List<TopSellingItem>> = flow {
        val sales = mutableMapOf<String, Pair<String, Int>>() // barcode -> (name, count)
        var totalItems = 0
        var maxRevenue = 0.0
        
        saleLogDao.getRecentSales(500).collect { logs ->
            for (log in logs) {
                try {
                    val items = parseSaleItems(log.itemsJson)
                    totalItems += items.size
                    for (item in items) {
                        val existing = sales[item.barcode]
                        val newCount = (existing?.second ?: 0) + item.quantity
                        sales[item.barcode] = Pair(item.name, newCount)
                    }
                } catch (e: Exception) {
                    // Skip invalid JSON
                }
            }
        }
        
        val total = sales.values.sumOf { it.second }
        val topItems = sales.entries
            .sortedByDescending { it.value.second }
            .take(limit)
            .map { (barcode, data) ->
                TopSellingItem(
                    productName = data.first,
                    productBarcode = barcode,
                    imagePath = null,
                    unitsSold = data.second,
                    totalRevenue = data.second * 100.0, // Placeholder
                    percentage = if (total > 0) data.second.toFloat() / total * 100 else 0f
                )
            }
        emit(topItems)
    }
    
    private data class ParsedItem(val barcode: String, val name: String, val quantity: Int, val price: Double)
    
    private fun parseSaleItems(json: String): List<ParsedItem> {
        val items = mutableListOf<ParsedItem>()
        try {
            // Simple JSON parsing for cart items
            val cleanJson = json.replace("\"", "").replace("{", "").replace("}", "")
            val entries = cleanJson.split("},").filter { it.isNotBlank() }
            for (entry in entries) {
                val parts = entry.split(",")
                var barcode = ""
                var name = ""
                var quantity = 1
                var price = 0.0
                for (part in parts) {
                    val kv = part.split(":")
                    if (kv.size == 2) {
                        when (kv[0].trim()) {
                            "barcode", "productBarcode" -> barcode = kv[1].trim()
                            "name", "productName" -> name = kv[1].trim()
                            "quantity" -> quantity = kv[1].trim().toIntOrNull() ?: 1
                            "price", "productPrice" -> price = kv[1].trim().toDoubleOrNull() ?: 0.0
                        }
                    }
                }
                if (barcode.isNotBlank()) {
                    items.add(ParsedItem(barcode, name.ifBlank { barcode }, quantity, price))
                }
            }
        } catch (e: Exception) {
            // Return empty on parse error
        }
        return items
    }
}