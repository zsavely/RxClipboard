package com.szagurskii.rxclipboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.support.annotation.NonNull;

import rx.Observable;
import rx.Subscriber;

/**
 * @author Savelii Zagurskii
 */
final class ClipboardClipOnSubscribe implements Observable.OnSubscribe<ClipData> {
  @NonNull final ClipboardManager clipboard;

  public ClipboardClipOnSubscribe(@NonNull ClipboardManager clipboard) {
    this.clipboard = clipboard;
  }

  @Override public void call(final Subscriber<? super ClipData> subscriber) {
    final OnPrimaryClipChangedListener listener = new OnPrimaryClipChangedListener() {
      @Override public void onPrimaryClipChanged() {
        if (!subscriber.isUnsubscribed()) {
          propagate(subscriber);
        }
      }
    };
    clipboard.addPrimaryClipChangedListener(listener);

    subscriber.add(new BaseUnsubscription() {
      @Override protected void onUnsubscribe() {
        clipboard.removePrimaryClipChangedListener(listener);
      }
    });

    propagate(subscriber);
  }

  void propagate(@NonNull Subscriber<? super ClipData> subscriber) {
    if (clipboard.hasPrimaryClip()) {
      subscriber.onNext(clipboard.getPrimaryClip());
    }
  }
}
