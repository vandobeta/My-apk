package com.smartshop.di

import android.content.Context
import com.smartshop.data.local.CartDao
import com.smartshop.data.local.ProductDao
import com.smartshop.data.local.SaleLogDao
import com.smartshop.data.local.SmartShopDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing database and DAO dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SmartShopDatabase {
        return SmartShopDatabase.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideProductDao(database: SmartShopDatabase): ProductDao {
        return database.productDao()
    }
    
    @Provides
    @Singleton
    fun provideCartDao(database: SmartShopDatabase): CartDao {
        return database.cartDao()
    }
    
    @Provides
    @Singleton
    fun provideSaleLogDao(database: SmartShopDatabase): SaleLogDao {
        return database.saleLogDao()
    }
}