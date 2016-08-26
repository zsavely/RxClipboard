package com.szagurskii.rxclipboard;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.os.Build;
import android.support.annotation.NonNull;

import rx.Observable;
import rx.Subscriber;

/**
 * @author Savelii Zagurskii
 */
final class ClipboardStringOnSubscribe implements Observable.OnSubscribe<String> {
  private static final boolean JB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;

  @NonNull final ClipboardManager clipboard;
  private final boolean propagateHtml;

  public ClipboardStringOnSubscribe(@NonNull ClipboardManager clipboard) {
    this(clipboard, false);
  }

  public ClipboardStringOnSubscribe(@NonNull ClipboardManager clipboard, boolean propagateHtml) {
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

  @SuppressLint("NewApi") void propagate(@NonNull Subscriber<? super String> subscriber) {
    if (clipboard.hasPrimaryClip()) {
      ClipData cd = clipboard.getPrimaryClip();
      if (cd.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
        CharSequence clip = cd.getItemAt(0).getText();
        subscriber.onNext(clip == null ? "" : clip.toString());
      } else if (propagateHtml && JB && cd.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) {
        String htmlText = cd.getItemAt(0).getHtmlText();
        subscriber.onNext(htmlText == null ? "" : htmlText);
      }
    }
  }
}
