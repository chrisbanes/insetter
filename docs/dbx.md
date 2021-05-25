# Data-binding extensions (DBX)

A [Data Binding][databinding] extension library, providing [Data Binding][databinding]-specific functionality.
This primarily contains binding adapters, which allow access to the helper functions from your layouts.
Currently the attributes provided are:

!!! warning
    The future for the DBX library is currently being evaluated. It's unclear how useful it is,
    and the amount of effort needed to update it to the new WindowInsets types is great.

    View binding + improvements to the main library mean that it now simple to use the main library only.

---

## Applying [system window insets][swi]
The following attributes are useful in being able to apply the [system window insets][swi] insets,
to specific dimensions to views.

 * `app:paddingLeftSystemWindowInsets`: to apply the left dimension to the view's `paddingLeft`.
 * `app:paddingTopSystemWindowInsets`: to apply the top dimension to the view's `paddingTop`.
 * `app:paddingRightSystemWindowInsets`: to apply the right dimension to the view's `paddingRight`.
 * `app:paddingBottomSystemWindowInsets`: to apply the bottom dimension to the view's `paddingBottom`.
 * `app:layout_marginLeftSystemWindowInsets`: to apply the left dimension to the view's `layout_marginLeft`.
 * `app:layout_marginTopSystemWindowInsets`: to apply the top dimension to the view's `layout_marginTop` .
 * `app:layout_marginRightSystemWindowInsets`: to apply the right dimension to the view's `layout_marginRight`.
 * `app:layout_marginBottomSystemWindowInsets`: to apply the bottom dimension to the view's `layout_marginBottom`.
 * `consumeSystemWindowInsets`: whether to consume the system window insets. Defaults to `false`.

## Applying [system gesture insets][sgi]
The following attributes are useful in being able to apply the [system gesture insets][sgi] insets,
to specific dimensions to views.

 * `app:paddingLeftSystemGestureInsets`: to apply the left dimension to the view's `paddingLeft`.
 * `app:paddingTopSystemGestureInsets`: to apply the top dimension to the view's `paddingTop`.
 * `app:paddingRightSystemGestureInsets`: to apply the right dimension to the view's `paddingRight`.
 * `app:paddingBottomSystemGestureInsets`: to apply the bottom dimension to the view's `paddingBottom`.
 * `app:layout_marginLeftSystemGestureInsets`: to apply the left dimension to the view's `layout_marginLeft`.
 * `app:layout_marginTopSystemGestureInsets`: to apply the top dimension to the view's `layout_marginTop` .
 * `app:layout_marginRightSystemGestureInsets`: to apply the right dimension to the view's `layout_marginRight`.
 * `app:layout_marginBottomSystemGestureInsets`: to apply the bottom dimension to the view's `layout_marginBottom`.
 
### Using the applying insets attributes

Each of the attributes takes a `boolean` value of whether the attribute functionality is enabled or not,
like so:

``` xml
<ImageView
    app:paddingBottomSystemWindowInsets="@{true}"
    app:paddingLeftSystemWindowInsets="@{true}" />
```

You can use any non-exclusive combination of insets and application type, such as the following
which uses the bottom system window inset for padding, and the left gesture inset for margin:

``` xml
<ImageView
    app:paddingBottomSystemWindowInsets="@{true}"
    app:layout_marginLeftSystemGestureInsets="@{true}" />
```

### Compound padding/margin

You can safely set any padding or margins on the view, and the values will be maintained.
For example here we're using a padding of `24dp`, and also applying the
[system window insets][swi] left and bottom using padding:

``` xml
<ImageView
    app:paddingLeftSystemWindowInsets="@{true}"
    app:paddingBottomSystemWindowInsets="@{true}"
    android:padding="24dp" />
```

If the bottom [system window insets][swi] is defined as `48dp` on the device, the final
applied padding for the view will be:

| Dimension     | Layout padding | System window insets | Final applied padding |
| ------------- | -------------- | -------------------- | --------------------- |
| Left          | 24dp           | 0dp                  | 24dp                  |
| Top           | 24dp           | 0dp                  | **24dp** (0 + 24)     |
| Right         | 24dp           | 0dp                  | 24dp                  |
| Bottom        | 24dp           | 48dp                 | **72dp** (24 + 48)    |

The same behavior happens when using margin too.

## Edge-to-edge attributes
There is currently just one edge-to-edge attribute:

* `app:layout_edgeToEdge`: Set this view's system-ui visibility with the flags required to be laid out 'edge-to-edge', or not.

``` xml
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:layout_edgeToEdge="@{true}">
    
    <!-- Yadda yadda -->

</FrameLayout>
```

## Download

=== "Stable"

    Latest version: ![GitHub release](https://img.shields.io/maven-central/v/dev.chrisbanes.insetter/insetter)

    ```groovy
    repositories {
        mavenCentral()
    }

    dependencies {
        implementation "dev.chrisbanes.insetter:insetter-dbx:<latest version>"
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
        implementation "dev.chrisbanes.insetter:insetter-dbx:XXX-SNAPSHOT"
    }
    ```

 [databinding]: https://developer.android.com/topic/libraries/data-binding
 [cl]: https://developer.android.com/reference/androidx/constraintlayout/widget/ConstraintLayout.html
 [swi]: https://developer.android.com/reference/androidx/core/view/WindowInsetsCompat.html#getSystemWindowInsets()
 [sgi]: https://developer.android.com/reference/androidx/core/view/WindowInsetsCompat.html#getSystemGestureInsets()