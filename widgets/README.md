# insetter-widgets

[![javadoc.io](https://javadoc.io/badge2/dev.chrisbanes/insetter-widgets/javadoc.io.svg)](https://javadoc.io/doc/dev.chrisbanes/insetter-widgets)

An extension library which provides versions of commonly used ViewGroups with enhanced inset
handling. Currently this library is focusing on building upon 
[ConstraintLayout][cl].

## InsetterConstraintLayout

[InsetterConstraintLayout](widgets/src/main/java/dev/chrisbanes/insetter/widgets/InsetterConstraintLayout.java)
is a [ConstraintLayout][cl] which adds support for a number of attributes to define inset behavior
on child views.

The attributes currently provided are:

 * `app:paddingSystemWindowInsets`: to apply the [system window insets][swi] using padding.
 * `app:layout_marginSystemWindowInsets`: to apply the [system window insets][swi] using margin.
 * `app:paddingSystemGestureInsets`:  to apply the [system gesture insets][sgi] using padding.
 * `app:layout_marginSystemGestureInsets`: to apply the [system gesture insets][sgi] using margin.
 
Each of the attributes takes a combination of flags, defining which dimensions the chosen
insets should be applied to. An example can be seen below:

``` xml
<dev.chrisbanes.insetter.widgets.InsetterConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ImageView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:paddingSystemWindowInsets="left|top|right|bottom"
        android:src="@drawable/rectangle" />

</dev.chrisbanes.insetter.widgets.InsetterConstraintLayout>
```

Here, the `ImageView`'s padding on all dimensions will be increased by the [system window insets][swi].

---

You can also mix inset types, such as below where the view's padding will be using the left and right
[system window insets][swi] values, and the bottom [system gesture insets][sgi]:

``` xml
<dev.chrisbanes.insetter.widgets.InsetterConstraintLayout>

    <ImageView
        app:paddingSystemWindowInsets="left|right"
        app:paddingSystemGestureInsets="bottom" />

</dev.chrisbanes.insetter.widgets.InsetterConstraintLayout>
```

---

And similarly, you can mix application types. This time the view's padding will be using the left and right
[system window insets][swi] values, but the view's bottom margin which be using the [system gesture insets][sgi]:

``` xml
<dev.chrisbanes.insetter.widgets.InsetterConstraintLayout>

    <ImageView
        app:paddingSystemWindowInsets="left|right"
        app:layout_marginSystemGestureInsets="bottom" />

</dev.chrisbanes.insetter.widgets.InsetterConstraintLayout>
```

### When should I use this vs the dbx library?
The behavior enabled through `InsetterConstraintLayout` is similar to that provided by 
the `insetter-dbx` library, but without the requirement of using [data-binding][databinding].
If you're already using [data-binding][databinding] I recommend using the dbx library and it's
binding adapters, since they work with any view type.

However, if you do not use [data-binding][databinding] and do not wish to do so, the widgets in
this library provide very similar functionality at the cost of having to migrate to our widget types.

 [databinding]: https://developer.android.com/topic/libraries/data-binding
 [cl]: https://developer.android.com/reference/androidx/constraintlayout/widget/ConstraintLayout.html
 [swi]: https://developer.android.com/reference/androidx/core/view/WindowInsetsCompat.html#getSystemWindowInsets()
 [sgi]: https://developer.android.com/reference/androidx/core/view/WindowInsetsCompat.html#getSystemGestureInsets()