# insetter-dbx

[![javadoc.io](https://javadoc.io/badge2/dev.chrisbanes/insetter-dbx/javadoc.io.svg)](https://javadoc.io/doc/dev.chrisbanes/insetter-dbx)

A [Data Binding][databinding] extension library, providing [Data Binding][databinding]-specific functionality.
This primarily contains binding adapters, which allow access to the helper functions from your layouts.
Currently the attributes provided are:

## Applying insets attributes

### System Window Insets

 * `app:paddingLeftSystemWindowInsets`: to apply the left [system window insets][swi] using padding.
 * `app:paddingTopSystemWindowInsets`: to apply the top [system window insets][swi] using padding.
 * `app:paddingRightSystemWindowInsets`: to apply the right [system window insets][swi] using padding.
 * `app:paddingBottomSystemWindowInsets`: to apply the bottom [system window insets][swi] using padding.
 * `app:layout_marginLeftSystemWindowInsets`: to apply the left [system window insets][swi] using margin.
 * `app:layout_marginTopSystemWindowInsets`: to apply the top [system window insets][swi] using margin.
 * `app:layout_marginRightSystemWindowInsets`: to apply the right [system window insets][swi] using margin.
 * `app:layout_marginBottomSystemWindowInsets`: to apply the bottom [system window insets][swi] using margin.

### System Gesture Insets

 * `app:paddingLeftSystemGestureInsets`: to apply the left [system gesture insets][sgi] using padding.
 * `app:paddingTopSystemGestureInsets`: to apply the top [system gesture insets][sgi] using padding.
 * `app:paddingRightSystemGestureInsets`: to apply the right [system gesture insets][sgi] using padding.
 * `app:paddingBottomSystemGestureInsets`: to apply the bottom [system gesture insets][sgi] using padding.
 * `app:layout_marginLeftSystemGestureInsets`: to apply the left [system gesture insets][sgi] using margin.
 * `app:layout_marginTopSystemGestureInsets`: to apply the top [system gesture insets][sgi] using margin.
 * `app:layout_marginRightSystemGestureInsets`: to apply the right [system gesture insets][sgi] using margin.
 * `app:layout_marginBottomSystemGestureInsets`: to apply the bottom [system gesture insets][sgi] using margin.
 
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

## When should I use this vs the widgets library?

The behavior enabled through the [widgets](../widgets) library is similar to that provided by 
the this library, but without the requirement of using [data-binding][databinding].
If you're already using [data-binding][databinding] I recommend using the dbx library and it's
binding adapters, since they work with any view type.

However, if you do not use [data-binding][databinding] and do not wish to do so, the widgets library
provides very similar functionality at the cost of having to migrate to the insetter widget types.

 [databinding]: https://developer.android.com/topic/libraries/data-binding
 [cl]: https://developer.android.com/reference/androidx/constraintlayout/widget/ConstraintLayout.html
 [swi]: https://developer.android.com/reference/androidx/core/view/WindowInsetsCompat.html#getSystemWindowInsets()
 [sgi]: https://developer.android.com/reference/androidx/core/view/WindowInsetsCompat.html#getSystemGestureInsets()