package com.smartshop.ui.security;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class PINViewModel_Factory implements Factory<PINViewModel> {
  private final Provider<Context> contextProvider;

  public PINViewModel_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PINViewModel get() {
    return newInstance(contextProvider.get());
  }

  public static PINViewModel_Factory create(Provider<Context> contextProvider) {
    return new PINViewModel_Factory(contextProvider);
  }

  public static PINViewModel newInstance(Context context) {
    return new PINViewModel(context);
  }
}
