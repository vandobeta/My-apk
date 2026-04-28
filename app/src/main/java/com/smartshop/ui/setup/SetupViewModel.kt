package com.smartshop.ui.setup

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Setup screen.
 * Handles permission requests and PIN setup.
 */
@HiltViewModel
class SetupViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()
    
    private val prefs by lazy {
        context.getSharedPreferences("smartshop_prefs", Context.MODE_PRIVATE)
    }
    
    fun requestCameraPermission() {
        // Permission request is handled in Composable via ActivityResultLauncher
        // This is a placeholder - actual implementation uses rememberLauncherForActivityResult
        _uiState.update { it.copy(hasCameraPermission = true) }
    }
    
    fun requestNotificationPermission() {
        // Permission request is handled in Composable via ActivityResultLauncher
        _uiState.update { it.copy(hasNotificationPermission = true) }
    }
    
    fun updatePin(pin: String) {
        _uiState.update { it.copy(pin = pin, pinError = null) }
    }
    
    fun updateConfirmPin(confirmPin: String) {
        _uiState.update { it.copy(confirmPin = confirmPin, pinError = null) }
    }
    
    fun completeSetup() {
        viewModelScope.launch {
            val state = _uiState.value
            
            // Use 0000 as default if no PIN is entered
            val pinToSave = if (state.pin.isEmpty()) "0000" else state.pin
            
            // Validate PIN
            if (pinToSave.length != 4) {
                _uiState.update { it.copy(pinError = "PIN must be 4 digits") }
                return@launch
            }
            
            // Validate confirmation if provided
            if (state.confirmPin.isNotEmpty() && state.confirmPin != pinToSave) {
                _uiState.update { it.copy(pinError = "PINs do not match") }
                return@launch
            }
            
            // Save PIN using SharedPreferences
            try {
                val editor = prefs.edit()
                editor.putString("admin_pin", pinToSave)
                editor.putBoolean("setup_complete", true)
                editor.apply()
                
                _uiState.update { it.copy(isComplete = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to save PIN: ${e.message}") }
            }
        }
    }
    
    fun setCameraPermissionGranted(granted: Boolean) {
        _uiState.update { it.copy(hasCameraPermission = granted) }
    }
    
    fun setNotificationPermissionGranted(granted: Boolean) {
        _uiState.update { it.copy(hasNotificationPermission = granted) }
    }
}

/**
 * UI State for Setup screen.
 */
data class SetupUiState(
    val hasCameraPermission: Boolean = false,
    val hasNotificationPermission: Boolean = false,
    val pin: String = "",
    val confirmPin: String = "",
    val pinError: String? = null,
    val isComplete: Boolean = false,
    val error: String? = null
) {
    // Allow completion with camera permission
    val canComplete: Boolean
        get() = hasCameraPermission && (pin.isEmpty() || pin.length == 4)
}