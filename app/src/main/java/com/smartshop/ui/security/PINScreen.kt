package com.smartshop.ui.security

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
 * PIN entry screen for admin verification with password masking.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PINScreen(
    onPinVerified: (Boolean) -> Unit,
    onCancel: () -> Unit,
    viewModel: PINViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.isUnlocked) {
        if (uiState.isUnlocked) {
            onPinVerified(true)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Access") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Text("Cancel")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Enter Admin PIN",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Enter your 4-digit PIN",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            var passwordVisible by remember { mutableStateOf(false) }
            
            OutlinedTextField(
                value = uiState.pin,
                onValueChange = { viewModel.setPin(it) },
                label = { Text("PIN") },
                singleLine = true,
                isError = uiState.error != null,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide PIN" else "Show PIN"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(0.6f)
            )
            
            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { viewModel.verifyPin() },
                enabled = uiState.pin.length == 4 && !uiState.isVerifying,
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text(if (uiState.isVerifying) "Verifying..." else "Verify")
            }
        }
    }
}

/**
 * UI State for PIN screen.
 */
data class PINUiState(
    val pin: String = "",
    val isVerifying: Boolean = false,
    val isUnlocked: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for PIN screen.
 */
@HiltViewModel
class PINViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PINUiState())
    val uiState: StateFlow<PINUiState> = _uiState.asStateFlow()
    
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