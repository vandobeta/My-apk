package com.smartshop.ui.checkout;

import com.smartshop.data.repository.CartRepository;
import com.smartshop.data.repository.ProductRepository;
import com.smartshop.data.repository.SaleRepository;
import com.smartshop.util.FeedbackManager;
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
public final class CheckoutViewModel_Factory implements Factory<CheckoutViewModel> {
  private final Provider<CartRepository> cartRepositoryProvider;

  private final Provider<ProductRepository> productRepositoryProvider;

  private final Provider<SaleRepository> saleRepositoryProvider;

  private final Provider<FeedbackManager> feedbackManagerProvider;

  public CheckoutViewModel_Factory(Provider<CartRepository> cartRepositoryProvider,
      Provider<ProductRepository> productRepositoryProvider,
      Provider<SaleRepository> saleRepositoryProvider,
      Provider<FeedbackManager> feedbackManagerProvider) {
    this.cartRepositoryProvider = cartRepositoryProvider;
    this.productRepositoryProvider = productRepositoryProvider;
    this.saleRepositoryProvider = saleRepositoryProvider;
    this.feedbackManagerProvider = feedbackManagerProvider;
  }

  @Override
  public CheckoutViewModel get() {
    return newInstance(cartRepositoryProvider.get(), productRepositoryProvider.get(), saleRepositoryProvider.get(), feedbackManagerProvider.get());
  }

  public static CheckoutViewModel_Factory create(Provider<CartRepository> cartRepositoryProvider,
      Provider<ProductRepository> productRepositoryProvider,
      Provider<SaleRepository> saleRepositoryProvider,
      Provider<FeedbackManager> feedbackManagerProvider) {
    return new CheckoutViewModel_Factory(cartRepositoryProvider, productRepositoryProvider, saleRepositoryProvider, feedbackManagerProvider);
  }

  public static CheckoutViewModel newInstance(CartRepository cartRepository,
      ProductRepository productRepository, SaleRepository saleRepository,
      FeedbackManager feedbackManager) {
    return new CheckoutViewModel(cartRepository, productRepository, saleRepository, feedbackManager);
  }
}
