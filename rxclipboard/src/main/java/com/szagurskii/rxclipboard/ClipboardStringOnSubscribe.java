package com.szagurskii.rxclipboard;

import android.annotation.SuppressLint;
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
final class ClipboardStringOnSubscribe implements Observable.OnSubscribe<String> {
  private static final boolean JB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;

  private final ClipboardManager clipboard;
  private final boolean propagateHtml;

  public ClipboardStringOnSubscribe(ClipboardManager clipboard) {
    this(clipboard, false);
  }

  public ClipboardStringOnSubscribe(ClipboardManager clipboard, boolean propagateHtml) {
    this.clipboard = clipboard;
    this.propagateHtml = propagateHtml;
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

  @SuppressLint("NewApi") private void propagate(Subscriber<? super String> subscriber) {
    if (clipboard.hasPrimaryClip()) {
      ClipData cd = clipboard.getPrimaryClip();
      if (cd.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
        subscriber.onNext(cd.getItemAt(0).getText().toString());
      } else if (cd.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) {
        if (propagateHtml && JB) {
          subscriber.onNext(cd.getItemAt(0).getHtmlText());
        }
      }
    }
  }
}
