package com.smartshop.di;

import com.smartshop.data.local.CartDao;
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
public final class DatabaseModule_ProvideCartDaoFactory implements Factory<CartDao> {
  private final Provider<SmartShopDatabase> databaseProvider;

  public DatabaseModule_ProvideCartDaoFactory(Provider<SmartShopDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public CartDao get() {
    return provideCartDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideCartDaoFactory create(
      Provider<SmartShopDatabase> databaseProvider) {
    return new DatabaseModule_ProvideCartDaoFactory(databaseProvider);
  }

  public static CartDao provideCartDao(SmartShopDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCartDao(database));
  }
}
