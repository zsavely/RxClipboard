package com.szagurskii.rxclipboard.sample;

import android.app.Activity;
import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.szagurskii.rxclipboard.RxClipboard;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends Activity {
  static final String TAG = MainActivity.class.getSimpleName();

  private Subscription subscription;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final TextView textView = (TextView) findViewById(R.id.tv_clip);

    subscription = RxClipboard.clipChanges(this)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<ClipData>() {
          @Override public void onCompleted() {
            Log.d(TAG, "onCompleted()");
          }

          @Override public void onError(Throwable e) {
            Log.d(TAG, "onError()", e);
          }

          @Override public void onNext(ClipData clipData) {
            Log.d(TAG, "onNext() " + clipData.toString());
            textView.setText(clipData.toString());
          }
        });
  }

  @Override protected void onStop() {
    super.onStop();

    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }
}
