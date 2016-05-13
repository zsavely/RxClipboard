package com.szagurskii.rxclipboard;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import rx.Observable;

import static com.szagurskii.rxclipboard.internal.Preconditions.checkNotNull;

/**
 * Static factory methods for creating {@linkplain Observable observables}
 * for {@link ClipboardManager}.
 *
 * @author Savelii Zagurskii
 */
public final class RxClipboard {
  /**
   * Create an observable of text changes in Clipboard.
   */
  @CheckResult @NonNull
  public static Observable<String> textChanges(@NonNull Context context) {
    checkNotNull(context, "context == null");
    return Observable.create(new ClipboardStringOnSubscribe(clipboard(context)));
  }

  /**
   * Create an observable of text and html text changes in Clipboard.
   */
  @CheckResult @NonNull
  public static Observable<String> textAndHtmlChanges(@NonNull Context context) {
    checkNotNull(context, "context == null");
    return Observable.create(new ClipboardStringOnSubscribe(clipboard(context), true));
  }

  /**
   * Create an observable of html text changes in Clipboard. Can be used from
   * {@link android.os.Build.VERSION_CODES#JELLY_BEAN}.
   */
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN) @CheckResult @NonNull
  public static Observable<String> htmlChanges(@NonNull Context context) {
    checkNotNull(context, "context == null");
    return Observable.create(new ClipboardHtmlOnSubscribe(clipboard(context)));
  }

  /**
   * Create an observable of Intent changes in Clipboard.
   */
  @CheckResult @NonNull
  public static Observable<Intent> intentChanges(@NonNull Context context) {
    checkNotNull(context, "context == null");
    return Observable.create(new ClipboardIntentOnSubscribe(clipboard(context)));
  }

  /**
   * Create an observable of URI changes in Clipboard.
   */
  @CheckResult @NonNull
  public static Observable<Uri> uriChanges(@NonNull Context context) {
    checkNotNull(context, "context == null");
    return Observable.create(new ClipboardUriOnSubscribe(clipboard(context)));
  }

  /**
   * Create an observable of clip changes in Clipboard.
   */
  @CheckResult @NonNull
  public static Observable<ClipData> clipChanges(@NonNull Context context) {
    checkNotNull(context, "context == null");
    return Observable.create(new ClipboardClipOnSubscribe(clipboard(context)));
  }

  @CheckResult @NonNull
  private static ClipboardManager clipboard(@NonNull Context context) {
    return (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
  }

  private RxClipboard() {
    throw new AssertionError("No instances.");
  }
}
