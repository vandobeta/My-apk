package com.smartshop.di;

import com.smartshop.data.local.ProductDao;
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
public final class DatabaseModule_ProvideProductDaoFactory implements Factory<ProductDao> {
  private final Provider<SmartShopDatabase> databaseProvider;

  public DatabaseModule_ProvideProductDaoFactory(Provider<SmartShopDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public ProductDao get() {
    return provideProductDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideProductDaoFactory create(
      Provider<SmartShopDatabase> databaseProvider) {
    return new DatabaseModule_ProvideProductDaoFactory(databaseProvider);
  }

  public static ProductDao provideProductDao(SmartShopDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideProductDao(database));
  }
}
