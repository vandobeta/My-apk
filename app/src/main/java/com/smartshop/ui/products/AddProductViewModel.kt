package com.smartshop.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartshop.data.local.Product
import com.smartshop.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Add Product screen.
 */
data class AddProductUiState(
    val barcode: String = "",
    val barcodeScanned: Boolean = false,
    val name: String = "",
    val price: String = "",
    val stockQuantity: String = "",
    val category: String = "",        // e.g., "Drinks", "Electronics"
    val brand: String = "",          // e.g., "Riham", "Coca Cola"
    val imageUri: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
) {
    val canSave: Boolean
        get() = name.isNotBlank() && price.toDoubleOrNull() != null && 
               stockQuantity.toIntOrNull() != null && !isSaving
}

/**
 * ViewModel for Add Product screen.
 */
@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddProductUiState())
    val uiState: StateFlow<AddProductUiState> = _uiState.asStateFlow()
    
    fun setBarcode(barcode: String) {
        _uiState.update { it.copy(barcode = barcode) }
    }
    
    fun confirmBarcode() {
        viewModelScope.launch {
            // Check if product already exists
            val existing = productRepository.getProductByBarcode(_uiState.value.barcode)
            if (existing != null) {
                _uiState.update { it.copy(error = "Product with this barcode already exists") }
                return@launch
            }
            _uiState.update { it.copy(barcodeScanned = true) }
        }
    }
    
    fun setName(name: String) {
        _uiState.update { it.copy(name = name) }
    }
    
    fun setPrice(price: String) {
        _uiState.update { it.copy(price = price) }
    }
    
    fun setStockQuantity(qty: String) {
        _uiState.update { it.copy(stockQuantity = qty) }
    }
    
    fun setCategory(category: String) {
        _uiState.update { it.copy(category = category) }
    }
    
    fun setBrand(brand: String) {
        _uiState.update { it.copy(brand = brand) }
    }
    
    fun setImageUri(uri: String?) {
        _uiState.update { it.copy(imageUri = uri) }
    }
    
    fun saveProduct() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            
            try {
                val state = _uiState.value
                
                // Check for duplicate barcode
                val existing = productRepository.getProductByBarcode(state.barcode)
                if (existing != null) {
                    _uiState.update { 
                        it.copy(
                            isSaving = false,
                            error = "Product with this barcode already exists"
                        )
                    }
                    return@launch
                }
                
                val product = Product(
                    barcode = state.barcode,
                    name = state.name,
                    priceUgx = state.price.toDouble(),
                    stockQuantity = state.stockQuantity.toInt(),
                    category = state.category,
                    brand = state.brand,
                    imagePath = state.imageUri
                )
                
                productRepository.saveProduct(product)
                
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false,
                        error = "Failed to save: ${e.message}"
                    )
                }
            }
        }
    }
}