package com.smartshop

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * SmartShop Application class.
 * Annotated with @HiltAndroidApp for Hilt dependency injection.
 */
@HiltAndroidApp
class SmartShopApplication : Application()