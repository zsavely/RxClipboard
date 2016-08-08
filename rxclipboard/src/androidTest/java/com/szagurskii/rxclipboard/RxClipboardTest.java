package com.szagurskii.rxclipboard;

import android.annotation.TargetApi;
import android.app.Instrumentation;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.google.common.truth.Truth.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class RxClipboardTest {
  @Rule public final ActivityTestRule<RxClipboardTestActivity> activityRule =
      new ActivityTestRule<>(RxClipboardTestActivity.class);

  final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

  ViewDirtyIdlingResource viewDirtyIdler;
  ClipboardManager clipboard;

  @Before public void setUp() {
    RxClipboardTestActivity activity = activityRule.getActivity();
    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        clipboard = (ClipboardManager) instrumentation.getContext()
            .getSystemService(Context.CLIPBOARD_SERVICE);
      }
    });
    viewDirtyIdler = new ViewDirtyIdlingResource(activity);
    Espresso.registerIdlingResources(viewDirtyIdler);
  }

  @After public void tearDown() {
    Espresso.unregisterIdlingResources(viewDirtyIdler);
  }

  @Test public void a1FirstTestWhenDeviceStarts_nullClipData() {
    RecordingObserver<String> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.textChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents();
  }

  @Test public void a2SecondTestWhenDeviceStarts_nullClipData() {
    RecordingObserver<Intent> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.intentChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents();
  }

  @Test public void a3ThirdTestWhenDeviceStarts_nullClipData() {
    RecordingObserver<Uri> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.uriChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents();
  }

  @Test public void a4FourthTestWhenDeviceStarts_nullClipData() {
    RecordingObserver<ClipData> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.clipChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents();
  }

  @Test public void a5FifthTestWhenDeviceStarts_nullClipData() {
    RecordingObserver<String> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.htmlChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents();
  }

  @Test public void a6SixthTestWhenDeviceStarts_nullClipData() {
    RecordingObserver<String> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.textAndHtmlChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents();
  }

  @Test public void textChanges() {
    setClip("Initial");
    RecordingObserver<String> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.textChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    assertThat(o.takeNext()).isEqualTo("Initial");

    setAndAssert(o, "Next");
    setAndAssert(o, "Stuff");
    setAndAssert(o, "Works?");

    subscription.unsubscribe();

    setClip("Silent");
    o.assertNoMoreEvents();
  }

  @Test public void textAndHtmlChanges() {
    setClip("Initial");
    RecordingObserver<String> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.textAndHtmlChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    assertThat(o.takeNext()).isEqualTo("Initial");

    setAndAssert(o, "Next");
    setHtmlAndAssert(o, "<p>Example</p>");
    setAndAssert(o, "Stuff");
    setHtmlAndAssert(o, "<ol><li>HTML Examples</li></ol>");

    subscription.unsubscribe();

    setClip("Silent");
    o.assertNoMoreEvents();
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN) @Test public void clipChangesWithHtml() {
    final ClipData initialClipData = ClipData.newHtmlText("Label0", "This is a Heading",
        "<!DOCTYPE html><html><head><title>Page Title</title></head><body><h1>This is a " +
            "Heading</h1><p>This is a paragraph.</p></body></html>");
    setClip(initialClipData);
    RecordingObserver<ClipData> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.clipChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    assertClipData(o, initialClipData);

    setAndAssert(o, ClipData.newHtmlText("Label1", "https://www.google.com",
        "<!DOCTYPE html><html><body>" +
            "<a href=\"https://www.google.com\">This is a link</a></body></html>"));
    setAndAssert(o, ClipData.newHtmlText("Label2", "Stuff", "<!DOCTYPE html><html><body><p>This " +
        "is a paragraph.</p><p>This is a paragraph.</p><p>This is a paragraph.</p></body></html>"));
    setAndAssert(o, ClipData.newHtmlText("Label3", "Works?", "<!DOCTYPE html><html><body><h1>This" +
        " is heading 1</h1><h2>This is heading 2</h2><h3>This is heading 3</h3><h4>This is " +
        "heading 4</h4><h5>This is heading 5</h5><h6>This is heading 6</h6></body></html>"));

    subscription.unsubscribe();

    setClip(ClipData.newHtmlText("Label4", "Silent", "<!DOCTYPE html><html><body><p><b>Hi" +
        ".</b></p></body></html>"));
    o.assertNoMoreEvents();
  }

  @Test public void uriChanges() {
    final Uri initialUri = Uri.parse("http://initial:setup@host:8080/example/file?query#fragment");
    setClip(initialUri);
    RecordingObserver<Uri> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.uriChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    assertThat(o.takeNext()).isEqualTo(initialUri);

    setAndAssert(o, Uri.parse("http://john:passlord@host:8080/example/file?query#fragment"));
    setAndAssert(o, Uri.parse("http://smith:lord@host:8080/directory/file?query#fragment"));
    setAndAssert(o, Uri.parse("http://jack:hey1@host:8080/directory/file?query#fragment"));

    subscription.unsubscribe();

    setClip(Uri.parse("http://super:silent@host:8080/directory/file?query#fragment"));
    o.assertNoMoreEvents();
  }

  @Test public void intentChanges() {
    final Intent intent = new Intent(instrumentation.getContext(), RxClipboardTestActivity.class);
    setClip(intent);
    RecordingObserver<Intent> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.intentChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    assertIntent(o, intent);

    setAndAssert(o, new Intent("Super Action"));
    setAndAssert(o, new Intent("Super Action #2",
        Uri.parse("http://jack:hey1@host:8080/directory/file?query#fragment")));
    setAndAssert(o, new Intent("Super Action #3",
        Uri.parse("http://super:action@host:8080/directory/file?query#fragment"),
        instrumentation.getContext(), RxClipboardTestActivity.class));

    subscription.unsubscribe();

    setClip(Uri.parse("http://super:silent@host:8080/directory/file?query#fragment"));
    o.assertNoMoreEvents();
  }

  @Test public void htmlChanges() {
    final String initialHtml = "<p><a href=\"https://www.google.com/\">Example Link</a></p>";
    setHtmlClip(initialHtml);
    RecordingObserver<String> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.htmlChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    assertThat(o.takeNext()).isEqualTo(initialHtml);

    setHtmlAndAssert(o, "<p><span style=\"font-size:10px;\">Small font</span>");
    setHtmlAndAssert(o, "<span style=\"font-size:18px;\">Large font</span>");
    setHtmlAndAssert(o, "<span style=\"font-size:12px;color:orange;\">Colored font</span>");

    subscription.unsubscribe();

    setHtmlClip("<span style=\"font-size:12px;font-style:italic;\">" +
        "Italic silent font</span> and more...</p>");
    o.assertNoMoreEvents();
  }

  @Test public void clipChangesWithText() {
    final ClipData initialClipData = ClipData.newPlainText("Label0", "Initial");
    setClip(initialClipData);
    RecordingObserver<ClipData> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.clipChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    assertClipData(o, initialClipData);

    setAndAssert(o, ClipData.newPlainText("Label1", "Next"));
    setAndAssert(o, ClipData.newPlainText("Label2", "Stuff"));
    setAndAssert(o, ClipData.newPlainText("Label3", "Works?"));

    subscription.unsubscribe();

    setClip("Silent");
    o.assertNoMoreEvents();
  }

  @Test public void nullTextChanges() {
    setClip("Initial");
    RecordingObserver<String> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.textChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    assertThat(o.takeNext()).isEqualTo("Initial");

    setAndAssert(o, "Next");
    setAndAssert(o, null, "");
    setAndAssert(o, null, "");
    setAndAssert(o, "Stuff");
    setAndAssert(o, null, "");
    setAndAssert(o, null, "");
    setAndAssert(o, "Works?");
    setAndAssert(o, null, "");
    setAndAssert(o, null, "");

    subscription.unsubscribe();

    setClip("", null);
    o.assertNoMoreEvents();
  }

  @Test public void nullTextAndHtmlChanges() {
    setClip("Initial");
    RecordingObserver<String> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.textAndHtmlChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    assertThat(o.takeNext()).isEqualTo("Initial");

    setAndAssert(o, "Next");
    setAndAssert(o, null, "");
    setAndAssert(o, null, "");
    setAndAssert(o, "Stuff");
    setAndAssert(o, null, "");
    setAndAssert(o, null, "");
    setAndAssert(o, "Works?");
    setAndAssert(o, null, "");
    setAndAssert(o, null, "");

    subscription.unsubscribe();

    setClip("", null);
    o.assertNoMoreEvents();
  }

  @Test public void nullHtmlChanges() {
    setHtmlClip("Initial");
    RecordingObserver<String> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.htmlChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    assertThat(o.takeNext()).isEqualTo("Initial");

    setHtmlAndAssert(o, "Next");
    setHtmlAndAssert(o, null, "");
    setHtmlAndAssert(o, null, "");
    setHtmlAndAssert(o, "Stuff");
    setHtmlAndAssert(o, null, "");
    setHtmlAndAssert(o, null, "");
    setHtmlAndAssert(o, "Works?");
    setHtmlAndAssert(o, null, "");
    setHtmlAndAssert(o, null, "");

    subscription.unsubscribe();

    setClip("", null);
    o.assertNoMoreEvents();
  }

  @Test public void nullClipChanges() {
    final ClipData initialClipData = ClipData.newPlainText("Label0", "Initial");
    setClip(initialClipData);
    RecordingObserver<ClipData> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.clipChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    assertThat(o.takeNext().getItemAt(0).getText()).isEqualTo("Initial");

    setAndAssert(o, ClipData.newPlainText("Label1", null));
    setAndAssert(o, ClipData.newPlainText("Label2", null));
    setAndAssert(o, ClipData.newPlainText("Label3", "Stuff"));
    setAndAssert(o, ClipData.newPlainText("Label4", null));
    setAndAssert(o, ClipData.newPlainText("Label5", null));
    setAndAssert(o, ClipData.newPlainText("Label6", "Works?"));
    setAndAssert(o, ClipData.newPlainText("Label7", null));
    setAndAssert(o, ClipData.newPlainText("Label9", null));
    setAndAssert(o, ClipData.newPlainText("Label10", "Works?"));
    setAndAssert(o, ClipData.newPlainText("Label11", null));

    subscription.unsubscribe();

    setClip("", null);
    o.assertNoMoreEvents();
  }

  @Test public void initialNullTextChanges() {
    setClip("Initial", null);
    RecordingObserver<String> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.textChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    assertThat(o.takeNext()).isEqualTo("");

    setAndAssert(o, "Next");
    setAndAssert(o, null, "");
    setAndAssert(o, null, "");
    setAndAssert(o, "Stuff");
    setAndAssert(o, null, "");
    setAndAssert(o, null, "");
    setAndAssert(o, "Works?");
    setAndAssert(o, null, "");
    setAndAssert(o, null, "");

    subscription.unsubscribe();

    setClip("", null);
    o.assertNoMoreEvents();
  }

  @Test public void initialNullTextAndHtmlChanges() {
    setClip("Initial", null);
    RecordingObserver<String> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.textAndHtmlChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    assertThat(o.takeNext()).isEqualTo("");

    setAndAssert(o, "Next");
    setAndAssert(o, null, "");
    setAndAssert(o, null, "");
    setAndAssert(o, "Stuff");
    setAndAssert(o, null, "");
    setAndAssert(o, null, "");
    setAndAssert(o, "Works?");
    setAndAssert(o, null, "");
    setAndAssert(o, null, "");

    subscription.unsubscribe();

    setClip("", null);
    o.assertNoMoreEvents();
  }

  @Test public void initialNullHtmlChanges() {
    setHtmlClip("Initial", null);
    RecordingObserver<String> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.htmlChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    assertThat(o.takeNext()).isEqualTo("");

    setHtmlAndAssert(o, "Next");
    setHtmlAndAssert(o, null, "");
    setHtmlAndAssert(o, null, "");
    setHtmlAndAssert(o, "Stuff");
    setHtmlAndAssert(o, null, "");
    setHtmlAndAssert(o, null, "");
    setHtmlAndAssert(o, "Works?");
    setHtmlAndAssert(o, null, "");
    setHtmlAndAssert(o, null, "");

    subscription.unsubscribe();

    setClip("", null);
    o.assertNoMoreEvents();
  }

  @Test public void initialNullClipChanges() {
    final ClipData initialClipData = ClipData.newPlainText("Label0", null);
    setClip(initialClipData);
    RecordingObserver<ClipData> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.clipChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    assertThat(o.takeNext().getItemAt(0).getText()).isEqualTo(null);

    setAndAssert(o, ClipData.newPlainText("Label1", null));
    setAndAssert(o, ClipData.newPlainText("Label2", null));
    setAndAssert(o, ClipData.newPlainText("Label3", "Stuff"));
    setAndAssert(o, ClipData.newPlainText("Label4", null));
    setAndAssert(o, ClipData.newPlainText("Label5", null));
    setAndAssert(o, ClipData.newPlainText("Label6", "Works?"));
    setAndAssert(o, ClipData.newPlainText("Label7", null));
    setAndAssert(o, ClipData.newPlainText("Label9", null));
    setAndAssert(o, ClipData.newPlainText("Label10", "Works?"));
    setAndAssert(o, ClipData.newPlainText("Label11", null));

    subscription.unsubscribe();

    setClip("", null);
    o.assertNoMoreEvents();
  }

  @Test public void shouldNotCallMethodIfUnsubscribed() {
    setClip("Initial");
    RecordingObserver<String> o = new RecordingObserver<>();
    Subscription subscription = RxClipboard.textChanges(instrumentation.getContext())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    assertThat(o.takeNext()).isEqualTo("Initial");

    setAndAssert(o, "Next");
    setAndAssert(o, "Stuff");
    setAndAssert(o, "Works?");

    subscription.unsubscribe();
    subscription.unsubscribe(); // Should not remove Clip listener twice.
    subscription.unsubscribe(); // Should not remove Clip listener.

    setClip("Silent");
    o.assertNoMoreEvents();
  }

  private void setAndAssert(RecordingObserver<String> o, final String value) {
    setClip(value);
    assertThat(o.takeNext()).isEqualTo(value);
  }

  private void setAndAssert(RecordingObserver<String> o, final String value, final String assertionValue) {
    setClip(value);
    assertThat(o.takeNext()).isEqualTo(assertionValue);
  }

  private void setAndAssert(RecordingObserver<ClipData> o, final ClipData value) {
    setClip(value);
    assertClipData(o, value);
  }

  private void setAndAssert(RecordingObserver<ClipData> o, final ClipData value, final ClipData assertionValue) {
    setClip(value);
    assertClipData(o, value);
  }

  private void setAndAssert(RecordingObserver<Uri> o, final Uri value) {
    setClip(value);
    assertThat(o.takeNext()).isEqualTo(value);
  }

  private void setAndAssert(RecordingObserver<Intent> o, final Intent value) {
    setClip(value);
    assertIntent(o, value);
  }

  private void setHtmlAndAssert(RecordingObserver<String> o, final String value) {
    setHtmlClip(value);
    assertThat(o.takeNext()).isEqualTo(value);
  }

  private void setHtmlAndAssert(RecordingObserver<String> o, final String value, final String assertionString) {
    setHtmlClip(value);
    assertThat(o.takeNext()).isEqualTo(assertionString);
  }

  private void assertClipData(RecordingObserver<ClipData> o, ClipData value) {
    assertThat(o.takeNext().toString()).isEqualTo(value.toString());
  }

  private void assertClipData(RecordingObserver<ClipData> o, ClipData value, ClipData assertionValue) {
    assertThat(o.takeNext().toString()).isEqualTo(assertionValue.toString());
  }

  private void assertIntent(RecordingObserver<Intent> o, Intent value) {
    assertThat(o.takeNext().toString()).isEqualTo(value.toString());
  }

  private void setClip(@NonNull String label, @Nullable String clip) {
    setClip(ClipData.newPlainText(label, clip));
  }

  private void setClip(@NonNull String clip) {
    setClip(ClipData.newPlainText(clip, clip));
  }

  private void setClip(@NonNull Intent clip) {
    setClip(ClipData.newIntent("Intent", clip));
  }

  private void setClip(@NonNull Uri clip) {
    setClip(ClipData.newRawUri("Uri", clip));
  }

  private void setClip(@NonNull ClipData clipData) {
    clipboard.setPrimaryClip(clipData);
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN) private void setHtmlClip(@NonNull String clip) {
    setClip(ClipData.newHtmlText("Html", "Basic", clip));
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN) private void setHtmlClip(@NonNull String label, @Nullable String clip) {
    setClip(ClipData.newHtmlText(label, clip, clip));
  }
}
