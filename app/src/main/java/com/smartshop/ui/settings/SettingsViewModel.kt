package com.smartshop.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * UI State for Settings screen.
 */
data class SettingsUiState(
    val pin: String = "",
    val isVerifying: Boolean = false,
    val isUnlocked: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for Settings screen.
 * Handles PIN verification for admin access.
 * Default PIN: 0000
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    private val prefs by lazy {
        context.getSharedPreferences("smartshop_prefs", Context.MODE_PRIVATE)
    }
    
    fun setPin(pin: String) {
        _uiState.update { it.copy(pin = pin, error = null) }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun verifyPin() {
        val state = _uiState.value
        _uiState.update { it.copy(isVerifying = true, error = null) }
        
        // Default PIN is 0000
        val savedPin = prefs.getString("admin_pin", "0000") ?: "0000"
        
        if (state.pin == savedPin) {
            _uiState.update { it.copy(isVerifying = false, isUnlocked = true) }
        } else {
            _uiState.update { 
                it.copy(
                    isVerifying = false,
                    error = "Incorrect PIN"
                )
            }
        }
    }
}