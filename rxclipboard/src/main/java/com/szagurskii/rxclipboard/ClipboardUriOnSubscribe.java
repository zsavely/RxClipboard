package com.szagurskii.rxclipboard;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.net.Uri;
import android.support.annotation.NonNull;

import rx.Observable;
import rx.Subscriber;

/**
 * @author Savelii Zagurskii
 */
final class ClipboardUriOnSubscribe implements Observable.OnSubscribe<Uri> {
  @NonNull final ClipboardManager clipboard;

  public ClipboardUriOnSubscribe(@NonNull ClipboardManager clipboard) {
    this.clipboard = clipboard;
  }

  @Override public void call(final Subscriber<? super Uri> subscriber) {
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

  void propagate(@NonNull Subscriber<? super Uri> subscriber) {
    if (clipboard.hasPrimaryClip()) {
      ClipData cd = clipboard.getPrimaryClip();
      if (cd.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_URILIST)) {
        subscriber.onNext(cd.getItemAt(0).getUri());
      }
    }
  }
}
