package com.smartshop.di;

import com.smartshop.data.local.SaleLogDao;
import com.smartshop.data.local.SmartShopDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class DatabaseModule_ProvideSaleLogDaoFactory implements Factory<SaleLogDao> {
  private final Provider<SmartShopDatabase> databaseProvider;

  public DatabaseModule_ProvideSaleLogDaoFactory(Provider<SmartShopDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public SaleLogDao get() {
    return provideSaleLogDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideSaleLogDaoFactory create(
      Provider<SmartShopDatabase> databaseProvider) {
    return new DatabaseModule_ProvideSaleLogDaoFactory(databaseProvider);
  }

  public static SaleLogDao provideSaleLogDao(SmartShopDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSaleLogDao(database));
  }
}
