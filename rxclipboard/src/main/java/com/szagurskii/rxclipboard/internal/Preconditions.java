package com.szagurskii.rxclipboard.internal;

public final class Preconditions {
  private Preconditions() {
  }

  public static <T> T checkNotNull(T value, String message) {
    if (value == null) {
      throw new NullPointerException(message);
    }
    return value;
  }
}
