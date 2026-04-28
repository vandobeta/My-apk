package com.smartshop.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartshop.data.local.Product
import com.smartshop.data.repository.ProductRepository
import com.smartshop.data.repository.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Dashboard with comprehensive analytics.
 */
data class DashboardUiState(
    val totalSales: Double = 0.0,
    val todaySales: Double = 0.0,
    val salesCount: Int = 0,
    val productCount: Int = 0,
    val inventoryValue: Double = 0.0, // Total inventory worth
    val salesPercentage: Float = 0f, // Today sales / inventory value * 100
    val itemsSold: Int = 0,
    val topSellingItems: List<TopSellingItem> = emptyList(),
    val lowStockProducts: List<ProductStockInfo> = emptyList(),
    val salesByCategory: List<CategorySale> = emptyList(),
    val categories: List<String> = emptyList(),    // e.g., ["Drinks", "Electronics"]
    val categoryBreakdown: Map<String, Double> = emptyMap(),  // category -> value
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Top selling item data.
 */
data class TopSellingItem(
    val productName: String,
    val productBarcode: String,
    val imagePath: String?,
    val unitsSold: Int,
    val totalRevenue: Double,
    val percentage: Float
)

/**
 * Product stock info for progress bars.
 */
data class ProductStockInfo(
    val product: Product,
    val percentage: Float,
    val maxStock: Int = 100
)

/**
 * Category sale for charts.
 */
data class CategorySale(
    val name: String,
    val amount: Double,
    val percentage: Float,
    val color: Long
)

/**
 * ViewModel for Dashboard with proper error handling.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val saleRepository: SaleRepository,
    private val productRepository: ProductRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    init {
        loadDashboardData()
    }
    
    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                // Load all stats with error handling
                launch {
                    saleRepository.getTotalSales().collect { total ->
                        _uiState.update { it.copy(totalSales = total ?: 0.0) }
                    }
                }
                
                launch {
                    saleRepository.getTodaySalesTotal().collect { today ->
                        _uiState.update { it.copy(todaySales = today ?: 0.0) }
                    }
                }
                
                launch {
                    saleRepository.getSalesCount().collect { count ->
                        _uiState.update { it.copy(salesCount = count) }
                    }
                }
                
                launch {
                    productRepository.getProductCount().collect { count ->
                        _uiState.update { it.copy(productCount = count) }
                    }
                }
                
                // Calculate inventory value and sales percentage
                launch {
                    productRepository.getAllProducts().collect { products ->
                        val inventoryValue = products.sumOf { it.stockQuantity * it.priceUgx }
                        val today = _uiState.value.todaySales
                        val percentage = if (inventoryValue > 0) ((today / inventoryValue) * 100).toFloat() else 0f
                        
                        // Group by category
                        val categoryList = products.map { it.category }.filter { it.isNotBlank() }.distinct()
                        val categoryBreakdown = products
                            .filter { it.category.isNotBlank() }
                            .groupBy { it.category }
                            .mapValues { (_, prods) -> prods.sumOf { it.stockQuantity * it.priceUgx } }
                        
                        val catBreakdownMap = categoryBreakdown.mapValues { it.value.toDouble() }
                        
                        _uiState.update { it.copy(
                            inventoryValue = inventoryValue,
                            salesPercentage = percentage.coerceIn(0f, 100f),
                            categories = categoryList,
                            categoryBreakdown = catBreakdownMap
                        ) }
                        
                        // Check for low stock notifications
                        products.filter { it.stockQuantity < 5 }.forEach { product ->
                            // Notification would be triggered here
                            android.util.Log.w("LowStock", "Low stock for: ${product.name}")
                        }
                    }
                }
                
                launch {
                    saleRepository.getTotalItemsSold().collect { items ->
                        _uiState.update { it.copy(itemsSold = items ?: 0) }
                    }
                }
                
                // Load top selling items
                launch {
                    saleRepository.getTopSellingItems(5).collect { items ->
                        _uiState.update { it.copy(topSellingItems = items) }
                    }
                }
                
                // Load low stock products with error handling
                launch {
                    try {
                        productRepository.getLowStockProducts(20).collect { products ->
                            val stockInfo = products.map { product ->
                                ProductStockInfo(
                                    product = product,
                                    percentage = (product.stockQuantity.toFloat() / 100f).coerceAtMost(1f),
                                    maxStock = 100
                                )
                            }
                            _uiState.update { it.copy(lowStockProducts = stockInfo) }
                        }
                    } catch (e: Exception) {
                        _uiState.update { it.copy(errorMessage = "Failed to load stock") }
                    }
                }
                
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.message}") }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    fun addStock(productId: Long, quantityToAdd: Int) {
        viewModelScope.launch {
            try {
                productRepository.addStock(productId, quantityToAdd)
                loadDashboardData()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to add stock") }
            }
        }
    }
    
    fun removeStock(productId: Long, quantityToRemove: Int) {
        viewModelScope.launch {
            try {
                productRepository.removeStock(productId, quantityToRemove)
                loadDashboardData()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to remove stock") }
            }
        }
    }
}