package com.smartshop.ui.setup;

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
public final class SetupViewModel_Factory implements Factory<SetupViewModel> {
  private final Provider<Context> contextProvider;

  public SetupViewModel_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SetupViewModel get() {
    return newInstance(contextProvider.get());
  }

  public static SetupViewModel_Factory create(Provider<Context> contextProvider) {
    return new SetupViewModel_Factory(contextProvider);
  }

  public static SetupViewModel newInstance(Context context) {
    return new SetupViewModel(context);
  }
}
