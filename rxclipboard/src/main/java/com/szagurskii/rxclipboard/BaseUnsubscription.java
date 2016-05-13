package com.szagurskii.rxclipboard;

import java.util.concurrent.atomic.AtomicBoolean;

import rx.Subscription;

/**
 * @author Savelii Zagurskii
 */
public abstract class BaseUnsubscription implements Subscription {
  private final AtomicBoolean unsubscribed = new AtomicBoolean();

  @Override
  public final boolean isUnsubscribed() {
    return unsubscribed.get();
  }

  @Override
  public final void unsubscribe() {
    if (unsubscribed.compareAndSet(false, true)) {
      onUnsubscribe();
    }
  }

  protected abstract void onUnsubscribe();
}
