package com.smartshop.ui.scanner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.SuggestionChip
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

/**
 * Professional Scanner screen with small scanning area.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    onNavigateToCheckout: () -> Unit,
    onNavigateToAddProduct: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.scannedProduct) {
        if (uiState.scannedProduct != null && !uiState.productDisplayed) {
            viewModel.displayProduct()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SmartShop") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    // Flashlight toggle
                    IconButton(onClick = { viewModel.toggleTorch() }) {
                        Icon(
                            if (uiState.isTorchOn) Icons.Default.FlashlightOn 
                            else Icons.Default.FlashlightOff,
                            contentDescription = "Flashlight",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    // Settings
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Main content area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when {
                    // Show product details - with animation
                    uiState.scannedProduct != null && uiState.productDisplayed -> {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                                animationSpec = tween(300),
                                initialOffsetY = { it }
                            ),
                            exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                                animationSpec = tween(300),
                                targetOffsetY = { it }
                            )
                        ) {
                            ProductDisplayCard(
                                product = uiState.scannedProduct!!,
                                scannedCount = uiState.scannedCount ?: 0,
                                onScanNext = { viewModel.scanNext() },
                                onResumeScanning = { viewModel.resumeScanning() }
                            )
                        }
                    }
                    // Show scanning area
                    uiState.isScanning -> {
                        ProfessionalScannerView(
                            onBarcodeDetected = { viewModel.onBarcodeDetected(it) },
                            isTorchOn = uiState.isTorchOn
                        )
                    }
                    // Loading state
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    // Product not found
                    uiState.productNotFound -> {
                        ProductNotFoundView(
                            barcode = uiState.lastBarcode ?: "",
                            onAddProduct = onNavigateToAddProduct,
                            onRescan = { viewModel.resumeScanning() }
                        )
                    }
                }
            }
            
            // Bottom action buttons
            ActionButtons(
                cartItemCount = uiState.cartItemCount,
                hasProduct = uiState.productDisplayed,
                onCheckout = onNavigateToCheckout,
                onRescan = { viewModel.resumeScanning() }
            )
        }
    }
}

@Composable
private fun ProfessionalScannerView(
    onBarcodeDetected: (String) -> Unit,
    isTorchOn: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Camera preview
        BarcodeScanner(
            modifier = Modifier.fillMaxSize(),
            onBarcodeDetected = onBarcodeDetected,
            isScanning = true,
            isTorchOn = isTorchOn
        )
        
        // Scanning box cutout effect - only center is visible, rest is darkened
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Semi-transparent overlay with hole in middle
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw full dark overlay
                drawRect(
                    color = Color.Black.copy(alpha = 0.6f),
                    size = size
                )
                
                // Cut out the scanning box (clear area)
                val boxWidth = 280.dp.toPx()
                val boxHeight = 180.dp.toPx()
                val centerX = size.width / 2
                val centerY = size.height / 2
                
                // Clear the center box area
                drawRect(
                    color = Color.Transparent,
                    topLeft = androidx.compose.ui.geometry.Offset(centerX - boxWidth / 2, centerY - boxHeight / 2),
                    size = androidx.compose.ui.geometry.Size(boxWidth, boxHeight)
                )
            }
        }
        
        // Scan frame with corners only (no background)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(280.dp, 180.dp)
                    .border(
                        3.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(12.dp)
                    )
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Instructions
            Surface(
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Align barcode in frame",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun ProductDisplayCard(
    product: com.smartshop.data.local.Product,
    scannedCount: Int,
    onScanNext: () -> Unit,
    onResumeScanning: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Product Image
        Card(
            modifier = Modifier.size(180.dp)
        ) {
            if (product.imagePath != null) {
                AsyncImage(
                    model = product.imagePath,
                    contentDescription = "Product Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = product.name.take(1).uppercase(),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Product Name
        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Price
        Text(
            text = "UGX ${product.priceUgx.toLong()}",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Stock remaining
        Text(
            text = "${product.stockQuantity} left",
            style = MaterialTheme.typography.titleMedium,
            color = if (product.stockQuantity < 10) MaterialTheme.colorScheme.error 
                   else MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // Category & Brand
        if (product.category.isNotBlank() || product.brand.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (product.category.isNotBlank()) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text(product.category) }
                    )
                }
                if (product.brand.isNotBlank()) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text(product.brand) }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Cart count
        if (scannedCount > 0) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "In cart: $scannedCount",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onResumeScanning,
                modifier = Modifier.weight(1f)
            ) {
                Text("Scan More")
            }
            
            Button(
                onClick = onScanNext,
                modifier = Modifier.weight(1f)
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
private fun ProductNotFoundView(
    barcode: String,
    onAddProduct: () -> Unit,
    onRescan: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Product Not Found",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Barcode: $barcode",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onAddProduct,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add New Product")
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedButton(
            onClick = onRescan,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Try Again")
        }
    }
}

@Composable
private fun ActionButtons(
    cartItemCount: Int,
    hasProduct: Boolean,
    onCheckout: () -> Unit,
    onRescan: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (cartItemCount > 0) {
                Button(
                    onClick = onCheckout,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Checkout ($cartItemCount)")
                }
            }
            
            if (hasProduct) {
                OutlinedButton(
                    onClick = onRescan,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Rescan")
                }
            }
        }
    }
}