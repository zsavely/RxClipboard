RxClipboard
========
[![Build Status](https://travis-ci.org/zsavely/RxClipboard.svg?branch=master)](https://travis-ci.org/zsavely/RxClipboard)
[![Download](https://api.bintray.com/packages/zsavely/maven/rxclipboard/images/download.svg) ](https://bintray.com/zsavely/maven/rxclipboard/_latestVersion)

RxJava binding APIs for Android Clipboard.

### Dependency
```groovy
compile 'com.szagurskii:rxclipboard:1.0.0'
```

### How to use

In order to start listen to the clipboard updates you need so subscribe to a provided observable. You can observe 6 different types of clipboard changes:
* Plain text
* Html
* Plain text OR Html
* Uri
* Intent
* ClipData (raw)

```java
// Subscribing to text changes updates.
RxClipboard.textChanges(this)
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Subscriber<String>() {
      @Override public void onCompleted() {
        Log.d(TAG, "onCompleted()");
      }
      @Override public void onError(Throwable e) {
        Log.d(TAG, "onError()", e);
      }
      @Override public void onNext(String value) {
        Log.d(TAG, "onNext() " + value);
      }
    });

// Subscribing to clip changes updates.
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
      }
    });
```

Inspired by Jake Wharton's [RxBinding](https://github.com/JakeWharton/RxBinding).

### License

    The MIT License (MIT)

    Copyright (c) 2016 Savelii Zagurskii

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
