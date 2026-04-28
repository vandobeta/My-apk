package com.smartshop.data.repository;

import com.smartshop.data.local.SaleLogDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class SaleRepository_Factory implements Factory<SaleRepository> {
  private final Provider<SaleLogDao> saleLogDaoProvider;

  public SaleRepository_Factory(Provider<SaleLogDao> saleLogDaoProvider) {
    this.saleLogDaoProvider = saleLogDaoProvider;
  }

  @Override
  public SaleRepository get() {
    return newInstance(saleLogDaoProvider.get());
  }

  public static SaleRepository_Factory create(Provider<SaleLogDao> saleLogDaoProvider) {
    return new SaleRepository_Factory(saleLogDaoProvider);
  }

  public static SaleRepository newInstance(SaleLogDao saleLogDao) {
    return new SaleRepository(saleLogDao);
  }
}
