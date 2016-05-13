package com.szagurskii.rxclipboard.internal;

import android.os.Looper;

public final class Preconditions {
  private Preconditions() {
    throw new AssertionError("No instances.");
  }

  public static <T> T checkNotNull(T value, String message) {
    if (value == null) {
      throw new NullPointerException(message);
    }
    return value;
  }

  public static void checkLooperAttached() {
    if (Looper.myLooper() == null) {
      throw new AssertionError("Looper must be attached to the calling thread.");
    }
  }
}
