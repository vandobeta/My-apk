package com.smartshop.ui

/**
 * Navigation routes for the app.
 */
sealed class Screen(val route: String) {
    object Setup : Screen("setup")
    object Main : Screen("main")
    object Scanner : Screen("scanner")
    object AddProduct : Screen("add_product")
    object Checkout : Screen("checkout")
    object Settings : Screen("settings")
    object Dashboard : Screen("dashboard")
    object PIN : Screen("pin")
}