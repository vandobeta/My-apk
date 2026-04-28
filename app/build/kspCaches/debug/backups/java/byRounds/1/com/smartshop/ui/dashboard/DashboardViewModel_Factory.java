package com.smartshop.ui.dashboard;

import com.smartshop.data.repository.ProductRepository;
import com.smartshop.data.repository.SaleRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<SaleRepository> saleRepositoryProvider;

  private final Provider<ProductRepository> productRepositoryProvider;

  public DashboardViewModel_Factory(Provider<SaleRepository> saleRepositoryProvider,
      Provider<ProductRepository> productRepositoryProvider) {
    this.saleRepositoryProvider = saleRepositoryProvider;
    this.productRepositoryProvider = productRepositoryProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(saleRepositoryProvider.get(), productRepositoryProvider.get());
  }

  public static DashboardViewModel_Factory create(Provider<SaleRepository> saleRepositoryProvider,
      Provider<ProductRepository> productRepositoryProvider) {
    return new DashboardViewModel_Factory(saleRepositoryProvider, productRepositoryProvider);
  }

  public static DashboardViewModel newInstance(SaleRepository saleRepository,
      ProductRepository productRepository) {
    return new DashboardViewModel(saleRepository, productRepository);
  }
}
