package com.szagurskii.rxclipboard;

import com.szagurskii.rxclipboard.internal.Preconditions;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Savelii Zagurskii
 */
public class PreconditionsTests {
  @Before public void setup() {
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowIfNull() {
    Preconditions.checkNotNull(null, "Method should throw NullPointer.");
  }

  @Test public void shouldReturnTheSame() {
    String what = Preconditions.checkNotNull("Hey", "Not-null");
    assertThat(what).isEqualTo("Hey");
  }

  @Test public void constructorShouldBePrivate()
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    Constructor<Preconditions> constructor = Preconditions.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }
}
