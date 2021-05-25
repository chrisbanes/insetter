# Insetter main library

[![GitHub release](https://img.shields.io/maven-central/v/dev.chrisbanes.insetter/insetter)](https://search.maven.org/search?q=g:dev.chrisbanes.insetter)

The main library provides simple APIs for handling [WindowInsets][wi].

## Builder

The [`Builder`][builder] is the main entry point for Insetter. It allows you to build up how you want a view
to react to [WindowInsets][wi]:

=== "Kotlin"

    ``` kotlin
    Insetter.builder()
        // This will add the navigation bars insets as padding to all sides of the view,
        // maintaining the original padding (from the layout XML, style, etc)
        .padding(windowInsetTypesOf(navigationBars = true))

        // This will add the status bars insets as margin to all sides of the view,
        // maintaining the original margins (from the layout XML, etc)`
        .margin(windowInsetTypesOf(statusBars = true))

        // This is a shortcut for view.setOnApplyWindowInsetsListener(builder.build())
        .applyToView(view)
    ```

=== "Java"

    ``` java
    Insetter.builder()
        // This will add the navigation bars insets as padding to all sides of the view,
        // maintaining the original padding (from the layout XML, style, etc)
        .padding(WindowInsetsCompat.Type.navigationBars())

        // This will add the status bars insets as margin to all sides of the view,
        // maintaining the original margins (from the layout XML, etc)`
        .margin(WindowInsetsCompat.Type.statusBars())

        // This is a shortcut for view.setOnApplyWindowInsetsListener(builder.build())
        .applyToView(view);
    ```

## Kotlin DSL

If you're using Kotlin, we also provided a DSL via the [`View.applyInsetter()`][applyinsetter]
extension function. To achieve the same result as above:

``` kotlin
view.applyInsetter {
    // Apply the navigation bar insets...
    type(navigationBars = true) {
        // Add to padding on all sides
        padding()
    }

    // Apply the status bar insets...
    type(statusBars = true) {
        // Add to margin on all sides
        margin()
    }
}
```

[API docs](api/library/library/dev.chrisbanes.insetter/apply-insetter.html)

## Animated Insets support

=== "Info"

    <figure>
        <video width="300" controls loop autoplay>
        <source src="animated.mp4" type="video/mp4">
            Your browser does not support the video tag.
        </video>
        <figcaption>Animated insets demo</figcaption>
    </figure>

    The library now has support for [`WindowInsetsAnimations`](https://developer.android.com/reference/android/view/WindowInsetsAnimation), allowing your content is react to inset animations, such as the on screen-keyboard (IME) being animated on/off screen.

    This functionality works wherever [WindowInsetsAnimationCompat](https://developer.android.com/reference/androidx/core/view/WindowInsetsAnimationCompat) works, which at the time or writing is on devices running API 21+.

=== "Usage"

    ``` kotlin
    binding.messageHolder.applyInsetter {
        // Apply the navigation bar and ime insets...
        type(navigationBars = true, ime = true) {
            // ..as padding, enabling the animation support
            padding(animated = true)
        }

        // This is optional, but it's usually necessary to sync the resulting
        // translation to other views. You can provide multiple views here.
        syncTranslationTo(binding.conversationRecyclerview)
    }
    ```

    See the [AnimatedInsetterSample](https://github.com/chrisbanes/insetter/blob/main/sample/src/main/java/dev/chrisbanes/insetter/sample/AnimatedInsetterSample.kt) for a working example.

### IME animations

If you're using the animation insets support for IME/keyboard animations, you also need to ensure that the activity's `windowSoftInputMode` is set to `adjustResize`:

``` xml
<activity
      android:name=".MyActivity"
      android:windowSoftInputMode="adjustResize" />
```

## Download

=== "Stable"

    Latest version: ![GitHub release](https://img.shields.io/maven-central/v/dev.chrisbanes.insetter/insetter)

    ```groovy
    repositories {
        mavenCentral()
    }

    dependencies {
        implementation "dev.chrisbanes.insetter:insetter:<latest version>"
    }
    ```

=== "Snapshot"

    Snapshots of the current development version are available, which track the latest commit.

    The snapshots are deployed to
    Sonatype's [snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/dev/chrisbanes/insetter/).
    The latest release is: ![Latest SNAPSHOT release](https://img.shields.io/nexus/s/dev.chrisbanes.insetter/insetter?label=snapshot&server=https%3A%2F%2Foss.sonatype.org)

    ```groovy
    repositories {
        // Need to add the Sonatype snapshots repo
        maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
    }

    dependencies {
        // Check the latest SNAPSHOT version from the image above
        implementation "dev.chrisbanes.insetter:insetter:XXX-SNAPSHOT"
    }
    ```

[snap]: https://oss.sonatype.org/content/repositories/snapshots/
[wi]: https://developer.android.com/reference/android/view/WindowInsets
[builder]: ../api/library/library/dev.chrisbanes.insetter/-insetter/-builder/
[applyinsetter]: ../api/library/library/dev.chrisbanes.insetter/apply-insetter.html
