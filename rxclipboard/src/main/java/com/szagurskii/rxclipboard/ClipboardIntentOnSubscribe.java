package com.szagurskii.rxclipboard;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Intent;
import android.support.annotation.NonNull;

import rx.Observable;
import rx.Subscriber;

/**
 * @author Savelii Zagurskii
 */
final class ClipboardIntentOnSubscribe implements Observable.OnSubscribe<Intent> {
  @NonNull final ClipboardManager clipboard;

  public ClipboardIntentOnSubscribe(@NonNull ClipboardManager clipboard) {
    this.clipboard = clipboard;
  }

  @Override public void call(final Subscriber<? super Intent> subscriber) {
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

  void propagate(@NonNull Subscriber<? super Intent> subscriber) {
    if (clipboard.hasPrimaryClip()) {
      ClipData cd = clipboard.getPrimaryClip();
      if (cd.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_INTENT)) {
        subscriber.onNext(cd.getItemAt(0).getIntent());
      }
    }
  }
}
