package com.szagurskii.rxclipboard;

import com.szagurskii.rxclipboard.internal.Preconditions;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * @author Savelii Zagurskii
 */
public class PreconditionsTests {
  @Before
  public void setup() {
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowIfNull() {
    Preconditions.checkNotNull(null, "Method should throw NullPointer.");
  }

  @Test public void shouldReturnTheSame() {
    String what = Preconditions.checkNotNull("Hey", "Not-null");
    assertThat(what).isEqualTo("Hey");
  }
}
