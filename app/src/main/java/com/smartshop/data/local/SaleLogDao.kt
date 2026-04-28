package com.smartshop.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for SaleLog operations.
 */
@Dao
interface SaleLogDao {
    
    @Query("SELECT * FROM sale_logs ORDER BY timestamp DESC")
    fun getAllSales(): Flow<List<SaleLog>>
    
    @Query("SELECT * FROM sale_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSales(limit: Int): Flow<List<SaleLog>>
    
    @Query("SELECT * FROM sale_logs WHERE timestamp >= :startTime ORDER BY timestamp DESC")
    fun getSalesSince(startTime: Long): Flow<List<SaleLog>>
    
    @Query("SELECT SUM(totalAmount) FROM sale_logs")
    fun getTotalSales(): Flow<Double?>
    
    @Query("SELECT SUM(totalAmount) FROM sale_logs WHERE timestamp >= :startTime")
    fun getSalesSinceTotal(startTime: Long): Flow<Double?>
    
    @Query("SELECT COUNT(*) FROM sale_logs")
    fun getSalesCount(): Flow<Int>
    
    @Query("SELECT SUM(itemCount) FROM sale_logs")
    fun getTotalItemsSold(): Flow<Int?>
    
    @Insert
    suspend fun insertSale(saleLog: SaleLog): Long
}