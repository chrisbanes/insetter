# insetter-widgets

[![javadoc.io](https://javadoc.io/badge2/dev.chrisbanes/insetter-widgets/javadoc.io.svg)](https://javadoc.io/doc/dev.chrisbanes/insetter-widgets)

An extension library which provides versions of commonly used ViewGroups with enhanced inset
handling. Currently this library is focusing on building upon 
[ConstraintLayout][cl].

There are currently two ways to use Insetter with [ConstraintLayout][cl]:
[InsetterConstraintLayout][icl] which is a subclass, or [InsetterConstraintHelper][ich] which is a constraint helper.

## InsetterConstraintLayout

[InsetterConstraintLayout][icl] is a [ConstraintLayout][cl] which adds support for a number of
attributes to define inset behavior on child views.

The attributes currently provided are:

 * `app:paddingSystemWindowInsets`: to apply the [system window insets][swi] using padding.
 * `app:layout_marginSystemWindowInsets`: to apply the [system window insets][swi] using margin.
 * `app:paddingSystemGestureInsets`:  to apply the [system gesture insets][sgi] using padding.
 * `app:layout_marginSystemGestureInsets`: to apply the [system gesture insets][sgi] using margin.

Each of the attributes takes a combination of flags, defining which dimensions the chosen
insets should be applied to. An example can be seen below:

``` xml
<dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="..."
    android:layout_height="...">

    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:paddingSystemWindowInsets="left|top|right|bottom"
        android:src="@drawable/rectangle" />

</dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintLayout>
```

Here, the `ImageView`'s padding on all dimensions will be increased by the [system window insets][swi].

---

You can also mix inset types, such as below where the view's padding will be using the left and right
[system window insets][swi] values, and the bottom [system gesture insets][sgi]:

``` xml
<dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintLayout>

    <ImageView
        app:paddingSystemWindowInsets="left|right"
        app:paddingSystemGestureInsets="bottom" />

</dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintLayout>
```

---

And similarly, you can mix application types. This time the view's padding will be using the left and right
[system window insets][swi] values, but the view's bottom margin which be using the [system gesture insets][sgi]:

``` xml
<dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintLayout>

    <ImageView
        app:paddingSystemWindowInsets="left|right"
        app:layout_marginSystemGestureInsets="bottom" />

</dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintLayout>
```

## InsetterConstraintHelper

Next up we have [InsetterConstraintHelper][ich], a constraint helper allowing you to apply inset
handling to all of the helper's referenced views.

Let's look at an example:

``` xml
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="..."
    android:layout_height="...">

    <dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintHelper
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:paddingSystemWindowInsets="left|top|right|bottom"
        app:constraint_referenced_ids="image,button" />

    <ImageView
        android:id="@+id/image" ... />

    <Button
        android:id="@+id/button" ... />

</androidx.constraintlayout.widget.ConstraintLayout>
```

Here you can see that we've defined a `InsetterConstraintHelper`, setting
`app:paddingSystemWindowInsets` on the helper itself. The defined insets will be applied to all
of the views listed in the `app:constraint_referenced_ids` attribute, in this example
`@id/image` and `@id/button`.

The attributes supported on [`InsetterConstraintHelper`][ich] are the same as [`InsetterConstraintLayout`][icl]:

 * `app:paddingSystemWindowInsets`: to apply the [system window insets][swi] using padding.
 * `app:layout_marginSystemWindowInsets`: to apply the [system window insets][swi] using margin.
 * `app:paddingSystemGestureInsets`:  to apply the [system gesture insets][sgi] using padding.
 * `app:layout_marginSystemGestureInsets`: to apply the [system gesture insets][sgi] using margin.

### Different flag combinations

If you have views which require different flag combinations, you need to define a
`InsetterConstraintHelper` per combination. Lets modify the example above, making `@id/image`
and `@id/button` use different flags:

``` xml
<androidx.constraintlayout.widget.ConstraintLayout ...>

    <dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintHelper
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:paddingSystemWindowInsets="left|right"
        app:constraint_referenced_ids="image" />

    <dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintHelper
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_marginSystemWindowInsets="bottom"
        app:constraint_referenced_ids="button" />

    <ImageView
        android:id="@+id/image" ... />

    <Button
        android:id="@+id/button" ... />

</androidx.constraintlayout.widget.ConstraintLayout>
```

_Thanks to [mzgreen](https://github.com/mzgreen) for contributing InsetterConstraintHelper._

## Compound padding/margin

For all of the widgets in this library, you can safely set any padding or margins on the view
and the values will be maintained. In the example below we're using a padding of `24dp`, and also
applying the [system window insets][swi] left and bottom using padding:

``` xml
<dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintLayout>

    <ImageView
        app:paddingSystemWindowInsets="left|bottom" />

</dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintLayout>
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

## `InsetterConstraintLayout` vs `InsetterConstraintHelper`?

We have two things here which both provide the same functionality on top of [ConstraintLayout][cl],
so how do you decide which to use? To help I've listed some of the benefits for each, allowing you
to decide which to use:

* `InsetterConstraintLayout` benefit #1. The attributes are defined directly on the children, which
is clearer when reading the source (for code-review, etc).
* `InsetterConstraintLayout` benefit #2. Each view can use a different combination of flags very easily.
To achieve the same with `InsetterConstraintHelper` requires creating a separate `InsetterConstraintHelper`
per flag combination.

* `InsetterConstraintHelper` benefit #1. The helper works with any [ConstraintLayout][cl] class,
including MotionLayout.

## When should I use this vs the dbx library?
The behavior provided by the widgets in this library are designed to be copies of that provided by
the `insetter-dbx` library, but without the requirement of using [data-binding][databinding].
If you're already using [data-binding][databinding] I recommend using the dbx library and it's
binding adapters, since they work with any view type.

However, if you do not use [data-binding][databinding] and do not wish to do so, the widgets in
this library provide very similar functionality at the cost of having to migrate to our widget types.

[databinding]: https://developer.android.com/topic/libraries/data-binding
[cl]: https://developer.android.com/reference/androidx/constraintlayout/widget/ConstraintLayout.html
[swi]: https://developer.android.com/reference/androidx/core/view/WindowInsetsCompat.html#getSystemWindowInsets()
[sgi]: https://developer.android.com/reference/androidx/core/view/WindowInsetsCompat.html#getSystemGestureInsets()
[icl]: widgets/src/main/java/dev/chrisbanes/insetter/widgets/constraintlayout/InsetterConstraintLayout.java
[ich]: widgets/src/main/java/dev/chrisbanes/insetter/widgets/constraintlayout/InsetterConstraintHelper.java