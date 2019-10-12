# Insetter

![Maven Central](https://maven-badges.herokuapp.com/maven-central/dev.chrisbanes/insetter/badge.svg) [![CircleCI](https://circleci.com/gh/chrisbanes/insetter/tree/master.svg?style=svg)](https://circleci.com/gh/chrisbanes/insetter/tree/master)

Insetter is a library to help apps handle
[WindowInsets](https://developer.android.com/reference/android/view/WindowInsets.html) more easily.
The library contains implementations of many of the concepts described in our
[_"Listeners to Layouts"_](https://medium.com/androiddevelopers/windowinsets-listeners-to-layouts-8f9ccc8fa4d1)
blog post.

There are three libraries available:

### insetter
The base library which is written in Java.

### insetter-ktx
A Kotlin extension library, providing Kotlin-specific functionality. This library contains
extension functions allowing easy access to the helper functions from the base library.

``` kotlin
bottomNav.doOnApplyWindowInsets { view, insets, initialPadding, initialMargins ->
    // padding contains the original padding values after inflation
    view.updatePadding(
        bottom = initialPadding.bottom + insets.systemWindowInsetBottom
    )
}
```

### insetter-dbx
A [Data Binding][databinding] extension library, providing [Data Binding][databinding]-specific functionality. This primarily contains binding adapters, which allow access to the helper
functions from your layouts:

``` xml
<BottomNavigationView
    android:layout_height="wrap_content"
    android:layout_width="match_parent"
    android:paddingVertical="24dp"
    app:paddingBottomSystemWindowInsets="@{true}"
    app:paddingLeftSystemWindowInsets="@{true}" />
```

## ⚠️ Attention 🚧

The library is being written to production quality, but it is not adhering to semantic versioning,
mean we may change the API if needed, though we'll try not to. We're using this repository to
allow quick and easy prototyping. The contents of this library may eventually be moved into
[Android Jetpack](https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/README.md)
at a later date.

## Download

```groovy
repositories {
  mavenCentral()
}

dependencies {
  // The base library. If you're using either the dbx and/or ktx libraries, you don't need this
  implementation "dev.chrisbanes:insetter:0.0.2"

  // If you're using data-binding use this
  implementation "dev.chrisbanes:insetter-dbx:0.0.2"

  // If you're using Kotlin use this too
  implementation "dev.chrisbanes:insetter-ktx:0.0.2"
}
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].
These are updated on every commit.

## Contributions

Please contribute! We will gladly review any pull requests.
Make sure to read the [Contributing](CONTRIBUTING.md) page first though.

## License

```
Copyright 2019 Google LLC.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements. See the NOTICE file distributed with this work for
additional information regarding copyright ownership. The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```

 [databinding]: https://developer.android.com/topic/libraries/data-binding
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/
