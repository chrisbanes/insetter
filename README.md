# Insetter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/dev.chrisbanes/insetter/badge.svg)](https://search.maven.org/search?q=g:dev.chrisbanes%20insetter) [![CircleCI](https://circleci.com/gh/chrisbanes/insetter/tree/master.svg?style=svg)](https://circleci.com/gh/chrisbanes/insetter/tree/master)

Insetter is a library to help apps handle
[WindowInsets](https://developer.android.com/reference/android/view/WindowInsets.html) more easily.
The library contains implementations of many of the concepts described in our
[_"Listeners to Layouts"_](https://medium.com/androiddevelopers/windowinsets-listeners-to-layouts-8f9ccc8fa4d1)
blog post.

There are a number of libraries available:

### insetter
[![javadoc.io](https://javadoc.io/badge2/dev.chrisbanes/insetter/javadoc.io.svg)](https://javadoc.io/doc/dev.chrisbanes/insetter)

The base library which is written in Java.

### insetter-ktx
[![javadoc.io](https://javadoc.io/badge2/dev.chrisbanes/insetter-ktx/javadoc.io.svg)](https://javadoc.io/doc/dev.chrisbanes/insetter-ktx)

A Kotlin extension library, providing Kotlin-specific functionality. This library contains
extension functions allowing easy access to the helper functions from the base library.

``` kotlin
bottomNav.doOnApplyWindowInsets { view, insets, initialState ->
    // padding contains the original padding values after inflation
    view.updatePadding(
        bottom = initialState.paddings.bottom + insets.systemWindowInsetBottom
    )
}
```

### [insetter-dbx](dbx/)

A [Data Binding][databinding] extension library, providing [Data Binding][databinding] specific functionality.
This primarily contains binding adapters, which allow access to the helper functions from your layouts:

``` xml
<BottomNavigationView
    android:layout_height="wrap_content"
    android:layout_width="match_parent"
    android:paddingVertical="24dp"
    app:paddingBottomSystemWindowInsets="@{true}"
    app:paddingLeftSystemWindowInsets="@{true}" />
```

📖 You can read more information [here](dbx/).

### [insetter-widgets](widgets/)

An extension library which provides versions of commonly used ViewGroups with enhanced inset
handling. Currently this library is focusing on building upon 
[ConstraintLayout](https://developer.android.com/reference/androidx/constraintlayout/widget/ConstraintLayout.html).

A example of a widget is [InsetterConstraintLayout](widgets/src/main/java/dev/chrisbanes/insetter/widgets/constraintlayout/InsetterConstraintLayout.java),
which enables new attributes to define inset behavior on child views.
The behavior enabled through `InsetterConstraintLayout` is similar to that provided by 
the `insetter-dbx` library, but without the requirement of using data-binding.

``` xml
<dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ImageView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:paddingSystemWindowInsets="left|top|right|bottom"
        android:src="@drawable/rectangle" />

</dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintLayout>
```

📖 You can read more information [here](widgets/).

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
    implementation "dev.chrisbanes:insetter:0.2.1"

    // If you're using data-binding use this
    implementation "dev.chrisbanes:insetter-dbx:0.2.1"

    // If you're using Kotlin use this too
    implementation "dev.chrisbanes:insetter-ktx:0.2.1"
  
    // If you would like to use the enhanced widget set, use this
    implementation "dev.chrisbanes:insetter-widgets:0.2.1"
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
