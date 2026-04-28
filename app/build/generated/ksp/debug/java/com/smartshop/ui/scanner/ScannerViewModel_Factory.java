package com.smartshop.ui.scanner;

import com.smartshop.data.repository.CartRepository;
import com.smartshop.data.repository.ProductRepository;
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
public final class ScannerViewModel_Factory implements Factory<ScannerViewModel> {
  private final Provider<ProductRepository> productRepositoryProvider;

  private final Provider<CartRepository> cartRepositoryProvider;

  private final Provider<FeedbackManager> feedbackManagerProvider;

  public ScannerViewModel_Factory(Provider<ProductRepository> productRepositoryProvider,
      Provider<CartRepository> cartRepositoryProvider,
      Provider<FeedbackManager> feedbackManagerProvider) {
    this.productRepositoryProvider = productRepositoryProvider;
    this.cartRepositoryProvider = cartRepositoryProvider;
    this.feedbackManagerProvider = feedbackManagerProvider;
  }

  @Override
  public ScannerViewModel get() {
    return newInstance(productRepositoryProvider.get(), cartRepositoryProvider.get(), feedbackManagerProvider.get());
  }

  public static ScannerViewModel_Factory create(
      Provider<ProductRepository> productRepositoryProvider,
      Provider<CartRepository> cartRepositoryProvider,
      Provider<FeedbackManager> feedbackManagerProvider) {
    return new ScannerViewModel_Factory(productRepositoryProvider, cartRepositoryProvider, feedbackManagerProvider);
  }

  public static ScannerViewModel newInstance(ProductRepository productRepository,
      CartRepository cartRepository, FeedbackManager feedbackManager) {
    return new ScannerViewModel(productRepository, cartRepository, feedbackManager);
  }
}
