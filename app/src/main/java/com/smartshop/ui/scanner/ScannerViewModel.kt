package com.smartshop.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartshop.data.local.CartItem
import com.smartshop.data.local.Product
import com.smartshop.data.repository.CartRepository
import com.smartshop.data.repository.ProductRepository
import com.smartshop.util.FeedbackManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Scanner screen.
 */
data class ScannerUiState(
    val isScanning: Boolean = true,
    val isLoading: Boolean = false,
    val scannedProduct: Product? = null,
    val productNotFound: Boolean = false,
    val productDisplayed: Boolean = false,
    val lastBarcode: String? = null,
    val scannedCount: Int? = null,
    val cartItemCount: Int = 0,
    val isTorchOn: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for Scanner screen.
 * Handles barcode detection, product lookup, and cart operations.
 */
@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val feedbackManager: FeedbackManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()
    
    private var displayJob: Job? = null
    private var lastProcessedBarcode: String? = null
    
    init {
        // Observe cart item count
        viewModelScope.launch {
            cartRepository.getCartItemCount().collect { count ->
                _uiState.update { it.copy(cartItemCount = count ?: 0) }
            }
        }
        
        // Initialize feedback
        feedbackManager.initialize()
    }
    
    /**
     * Handle barcode detection from scanner.
     */
    fun onBarcodeDetected(barcode: String) {
        // Debounce: Skip if same barcode within 1.5 seconds
        if (barcode == lastProcessedBarcode) {
            return
        }
        
        viewModelScope.launch {
            lastProcessedBarcode = barcode
            
            _uiState.update { 
                it.copy(
                    isLoading = true,
                    isScanning = false,
                    lastBarcode = barcode,
                    productNotFound = false
                )
            }
            
            try {
                // Lookup product by barcode (indexed for <50ms)
                val product = productRepository.getProductByBarcode(barcode)
                
                if (product != null) {
                    _uiState.update { 
                        it.copy(
                            scannedProduct = product,
                            isLoading = false
                        )
                    }
                    
                    // Play beep and vibrate
                    feedbackManager.beepAndVibrate()
                    
                    // Add to cart
                    addToCart(product)
                } else {
                    _uiState.update { 
                        it.copy(
                            productNotFound = true,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Error: ${e.message}",
                        isLoading = false
                    )
                }
            }
            
            // Clear debounce after 1.5 seconds
            delay(1500)
            if (barcode == lastProcessedBarcode) {
                lastProcessedBarcode = null
            }
        }
    }
    
    /**
     * Add product to cart and get current quantity.
     */
    private fun addToCart(product: Product) {
        viewModelScope.launch {
            val cartItem = CartItem(
                productId = product.id,
                productBarcode = product.barcode,
                productName = product.name,
                productPrice = product.priceUgx,
                productImagePath = product.imagePath,
                quantity = 1
            )
            cartRepository.addToCart(cartItem)
            
            // Get current quantity in cart
            // This is handled by CartRepository - it increments if exists
        }
    }
    
    /**
     * Start displaying the product (4 second timeout).
     */
    fun displayProduct() {
        displayJob?.cancel()
        displayJob = viewModelScope.launch {
            _uiState.update { it.copy(productDisplayed = true) }
            
            // Wait 4 seconds or until Scan Next is tapped
            delay(4000)
            
            if (_uiState.value.productDisplayed) {
                resumeScanning()
            }
        }
    }
    
    /**
     * Skip to next scan (Scan Next button).
     */
    fun scanNext() {
        displayJob?.cancel()
        resumeScanning()
    }
    
    /**
     * Resume scanning.
     */
    fun resumeScanning() {
        displayJob?.cancel()
        _uiState.update { 
            it.copy(
                isScanning = true,
                scannedProduct = null,
                productDisplayed = false,
                productNotFound = false,
                lastBarcode = null
            )
        }
    }
    
    /**
     * Toggle flashlight/torch.
     */
    fun toggleTorch() {
        _uiState.update { it.copy(isTorchOn = !it.isTorchOn) }
    }
    
    override fun onCleared() {
        super.onCleared()
        displayJob?.cancel()
    }
}