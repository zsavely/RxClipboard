package com.szagurskii.rxclipboard;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.os.Build;

import rx.Observable;
import rx.Subscriber;

/**
 * @author Savelii Zagurskii
 */
final class ClipboardHtmlOnSubscribe implements Observable.OnSubscribe<String> {
  private final ClipboardManager clipboard;

  public ClipboardHtmlOnSubscribe(ClipboardManager clipboard) {
    this.clipboard = clipboard;
  }

  @Override public void call(final Subscriber<? super String> subscriber) {
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

  private void propagate(Subscriber<? super String> subscriber) {
    if (clipboard.hasPrimaryClip()) {
      ClipData cd = clipboard.getPrimaryClip();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        if (cd.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) {
          subscriber.onNext(cd.getItemAt(0).getHtmlText());
        }
      }
    }
  }
}
