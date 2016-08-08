package com.szagurskii.rxclipboard;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * @author Savelii Zagurskii
 */
public class BaseUnsubscriptionTests {
  @Test public void shouldNotCallMethodIfUnsubscribed() {
    final int[] i = {0};
    BaseUnsubscription baseUnsubscription = new BaseUnsubscription() {
      @Override protected void onUnsubscribe() {
        i[0]++;
      }
    };
    baseUnsubscription.unsubscribe();
    assertThat(i[0]).isEqualTo(1);

    baseUnsubscription.unsubscribe();
    assertThat(i[0]).isEqualTo(1);

    baseUnsubscription.unsubscribe();
    assertThat(i[0]).isEqualTo(1);
  }
}
