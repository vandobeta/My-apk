package com.smartshop

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartshop.ui.Screen
import com.smartshop.ui.checkout.CheckoutScreen
import com.smartshop.ui.dashboard.DashboardScreen
import com.smartshop.ui.dashboard.InventoryOverviewScreen
import com.smartshop.ui.products.AddProductScreen
import com.smartshop.ui.scanner.ScannerScreen
import com.smartshop.ui.security.PINScreen
import com.smartshop.ui.settings.SettingsScreen
import com.smartshop.ui.setup.SetupScreen
import com.smartshop.ui.theme.SmartShopTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity - hosts the navigation and theme.
 * Handles permission requests at startup.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            SmartShopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SmartShopApp()
                }
            }
        }
    }
}

/**
 * Main app composable with navigation.
 */
@Composable
fun SmartShopApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    // Check if setup is complete
    var isSetupComplete by remember {
        val prefs = context.getSharedPreferences("smartshop_prefs", Context.MODE_PRIVATE)
        mutableStateOf(prefs.getBoolean("setup_complete", false))
    }
    
    // Start destination
    val startDestination = if (isSetupComplete) Screen.Scanner.route else Screen.Setup.route
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Setup Screen
        composable(Screen.Setup.route) {
            SetupScreen(
                onSetupComplete = {
                    isSetupComplete = true
                    navController.navigate(Screen.Scanner.route) {
                        popUpTo(Screen.Setup.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Scanner Screen (main)
        composable(Screen.Scanner.route) {
            ScannerScreen(
                onNavigateToCheckout = {
                    navController.navigate(Screen.Checkout.route)
                },
                onNavigateToAddProduct = {
                    // Navigate to PIN first for authentication
                    navController.navigate(Screen.PIN.route + "?returnTo=add_product")
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        // Checkout Screen
        composable(Screen.Checkout.route) {
            CheckoutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Add Product Screen (protected)
        composable(Screen.AddProduct.route) {
            AddProductScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Settings Screen (admin)
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route)
                }
            )
        }
        
        // Dashboard Screen (admin only)
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateBack = { navController.popBackStack() },
                onProductClick = { barcode ->
                    // Navigate to product detail or edit screen
                    navController.navigate("${Screen.AddProduct.route}?barcode=$barcode")
                }
            )
        }
        
        // PIN Screen (authentication with return destination)
        composable(Screen.PIN.route + "?returnTo={returnTo}") { backStackEntry ->
            val returnTo = backStackEntry.arguments?.getString("returnTo")
            PINScreen(
                onPinVerified = { isAdmin ->
                    navController.popBackStack()
                    when (returnTo) {
                        "add_product" -> navController.navigate(Screen.AddProduct.route)
                        "settings" -> navController.navigate(Screen.Settings.route)
                        "dashboard" -> navController.navigate(Screen.Dashboard.route)
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }
        
        // Simple PIN screen for settings access
        composable(Screen.PIN.route) {
            PINScreen(
                onPinVerified = { isAdmin ->
                    if (isAdmin) {
                        navController.navigate(Screen.Dashboard.route)
                    } else {
                        navController.popBackStack()
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}