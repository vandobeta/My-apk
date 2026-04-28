package com.smartshop.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartshop.data.local.CartItem
import com.smartshop.data.repository.CartRepository
import com.smartshop.data.repository.ProductRepository
import com.smartshop.data.repository.SaleRepository
import com.smartshop.util.FeedbackManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import javax.inject.Inject

/**
 * UI State for Checkout screen.
 */
data class CheckoutUiState(
    val cartItems: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val itemCount: Int = 0,
    val isProcessing: Boolean = false,
    val isComplete: Boolean = false,
    val showReceipt: Boolean = false, // Show E-receipt
    val cashReceived: Double = 0.0,
    val change: Double = 0.0,
    val error: String? = null
)

/**
 * ViewModel for Checkout screen.
 * Handles Paid (stock decrement + sale log) and Cancel (clear cart).
 */
@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val saleRepository: SaleRepository,
    private val feedbackManager: FeedbackManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()
    
    init {
        // Observe cart items
        viewModelScope.launch {
            cartRepository.getCartItems().collect { items ->
                val total = items.sumOf { it.totalPrice }
                _uiState.update { 
                    it.copy(
                        cartItems = items,
                        total = total,
                        itemCount = items.sumOf { it.quantity }
                    )
                }
            }
        }
    }
    
    /**
     * Remove item from cart.
     */
    fun removeItem(productId: Long) {
        viewModelScope.launch {
            cartRepository.removeFromCart(productId)
        }
    }
    
    /**
     * Increment item quantity.
     */
    fun incrementQuantity(productId: Long) {
        viewModelScope.launch {
            cartRepository.incrementQuantity(productId)
        }
    }
    
    /**
     * Decrement item quantity (removes if 1).
     */
    fun decrementQuantity(productId: Long) {
        viewModelScope.launch {
            cartRepository.decrementQuantity(productId)
        }
    }
    
    /**
     * Handle Cancel - clear cart without stock changes.
     */
    fun cancel() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            
            try {
                cartRepository.clearCart()
                _uiState.update { it.copy(isProcessing = false, isComplete = true) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isProcessing = false,
                        error = "Error: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Handle Paid - deduct stock and log sale.
     */
    fun paid() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }
            
            try {
                val items = _uiState.value.cartItems
                
                // First, validate stock availability
                for (item in items) {
                    val product = productRepository.getProductById(item.productId)
                    if (product == null || product.stockQuantity < item.quantity) {
                        _uiState.update { 
                            it.copy(
                                isProcessing = false,
                                error = "Insufficient stock for ${item.productName}"
                            )
                        }
                        return@launch
                    }
                }
                
                // Deduct stock for each item
                for (item in items) {
                    productRepository.decrementStock(item.productId, item.quantity)
                }
                
                // Log the sale
                val itemsJson = JSONArray().apply {
                    items.forEach { item ->
                        put(org.json.JSONObject().apply {
                            put("barcode", item.productBarcode)
                            put("name", item.productName)
                            put("price", item.productPrice)
                            put("quantity", item.quantity)
                            put("total", item.totalPrice)
                        })
                    }
                }.toString()
                
                saleRepository.logSale(
                    itemsJson = itemsJson,
                    totalAmount = _uiState.value.total,
                    itemCount = _uiState.value.itemCount
                )
                
                // Clear cart
                cartRepository.clearCart()
                
                // Play beep for success
                feedbackManager.playBeep()
                
                // Show receipt (user will enter cash amount in real scenario)
                _uiState.update { it.copy(
                    isProcessing = false, 
                    isComplete = true,
                    showReceipt = true,
                    cashReceived = _uiState.value.total, // In real app, this comes from payment input
                    change = 0.0
                ) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isProcessing = false,
                        error = "Error: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun setPaymentInfo(cashReceived: Double, change: Double) {
        _uiState.update { it.copy(cashReceived = cashReceived, change = change) }
    }
    
    fun hideReceipt() {
        _uiState.update { it.copy(showReceipt = false) }
    }
}