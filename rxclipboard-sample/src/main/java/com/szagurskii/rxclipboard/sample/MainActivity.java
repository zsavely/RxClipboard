package com.szagurskii.rxclipboard.sample;

import android.app.Activity;
import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.szagurskii.rxclipboard.RxClipboard;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends Activity {
  private static final String TAG = MainActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final TextView textView = (TextView) findViewById(R.id.tv_clip);

    RxClipboard.clipChanges(this)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<ClipData>() {
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
}
