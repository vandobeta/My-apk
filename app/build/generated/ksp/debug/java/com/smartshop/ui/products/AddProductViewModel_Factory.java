package com.smartshop.ui.products;

import com.smartshop.data.repository.ProductRepository;
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
public final class AddProductViewModel_Factory implements Factory<AddProductViewModel> {
  private final Provider<ProductRepository> productRepositoryProvider;

  public AddProductViewModel_Factory(Provider<ProductRepository> productRepositoryProvider) {
    this.productRepositoryProvider = productRepositoryProvider;
  }

  @Override
  public AddProductViewModel get() {
    return newInstance(productRepositoryProvider.get());
  }

  public static AddProductViewModel_Factory create(
      Provider<ProductRepository> productRepositoryProvider) {
    return new AddProductViewModel_Factory(productRepositoryProvider);
  }

  public static AddProductViewModel newInstance(ProductRepository productRepository) {
    return new AddProductViewModel(productRepository);
  }
}
