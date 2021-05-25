# Insetter

[![GitHub release](https://img.shields.io/maven-central/v/dev.chrisbanes.insetter/insetter)](https://search.maven.org/search?q=g:dev.chrisbanes.insetter)

Insetter is a library to help apps handle
[WindowInsets](https://developer.android.com/reference/android/view/WindowInsets) more easily.
The library contains implementations of many of the concepts described in our
[_"Listeners to Layouts"_](https://medium.com/androiddevelopers/windowinsets-listeners-to-layouts-8f9ccc8fa4d1)
blog post.

There are a number of libraries available:

## [Main library](library/)

The main library provides simple APIs for handling [WindowInsets](https://developer.android.com/reference/android/view/WindowInsets):

=== "Kotlin"

    ``` kotlin
    view.applyInsetter {
        // Apply the navigation bar insets...
        type(navigationBars = true) {
            // Add to padding on all sides
            padding()
        }
    }
    ```

=== "Java"

    ``` java
    Insetter.builder()
        // This will add the navigation bars insets as padding to all sides of the view,
        // maintaining the original padding (from the layout XML, style, etc)
        .padding(WindowInsetsCompat.Type.navigationBars())
        // This is a shortcut for view.setOnApplyWindowInsetsListener(builder.build())
        .applyToView(view);
    ```

📖 You can read more information [here](library/).

## [Data-binding extensions (DBX)](dbx/)

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

## Removed libraries 

### Widgets

The old `insetter-widgets` library has now removed. View binding + improvements to the main library mean that it now simple to use the main library only.

### insetter-ktx

The old `insetter-ktx` library has now removed, as all of the KTX functions have been moved to the main [library](library/). You can safely remove any references to the `insetter-ktx` dependency, and replace it with the core library.

## ⚠️ Attention 🚧

The library is being written to production quality, but it is not adhering to semantic versioning,
mean we may change the API if needed, though we'll try not to. We're using this repository to
allow quick and easy prototyping. The contents of this library may eventually be moved into
[Android Jetpack](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/)
at a later date.

## Contributions

Please contribute! We will gladly review any pull requests.
Make sure to read the [Contributing](contributing) page first though.

## License

```
Copyright 2021 Google LLC.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

[databinding]: https://developer.android.com/topic/libraries/data-binding
[snap]: https://oss.sonatype.org/content/repositories/snapshots/
