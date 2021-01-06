/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.chrisbanes.insetter.widgets.constraintlayout

import android.content.Context
import android.util.AttributeSet
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintHelper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.Sides
import dev.chrisbanes.insetter.ViewState
import dev.chrisbanes.insetter.widgets.R
import dev.chrisbanes.insetter.windowInsetTypesOf
import kotlin.properties.Delegates.observable

/**
 * An [ConstraintHelper] which adds enhanced support for inset handling.
 *
 * This class supports the use of the `paddingSystemWindowInsets`,
 * `paddingSystemGestureInsets`, `layout_marginSystemWindowInsets` and
 * `layout_marginSystemGestureInsets` attributes, which are then applied to any referenced children.
 *
 * Each of the attributes accepts a combination of flags which defines the sides on which the
 * relevant insets will be applied.
 *
 * ```
 * <androidx.constraintlayout.widget.ConstraintLayout
 *   xmlns:android="http://schemas.android.com/apk/res/android"
 *   xmlns:app="http://schemas.android.com/apk/res-auto"
 *   android:layout_width="match_parent"
 *   android:layout_height="match_parent">
 *
 *   <dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintHelper
 *     android:layout_width="wrap_content"
 *     android:layout_height="wrap_content"
 *     app:paddingSystemWindowInsets="left|top|right|bottom"
 *     app:constraint_referenced_ids="image" />
 *
 *   <ImageView
 *     android:id="@+id/image"
 *     android:layout_width="wrap_content"
 *     android:layout_height="wrap_content"
 *     android:src="@drawable/icon" />
 *
 * </androidx.constraintlayout.widget.ConstraintLayout>
 * ```
 *
 * ### Apply to multiple views
 *
 * A [InsetterConstraintHelper] can be applied to multiple views, but appending the ID name to
 * the `constraint_referenced_ids` attribute on the helper, like so:
 *
 * ```
 * <dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintHelper
 *   android:layout_width="wrap_content"
 *   android:layout_height="wrap_content"
 *   app:paddingSystemWindowInsets="left|top|right|bottom"
 *   app:constraint_referenced_ids="image,toolbar" />
 * ```
 *
 * ### Multiple helpers
 *
 * Multiple [InsetterConstraintHelper]s can also safely be applied to a single view, if
 * required:
 *
 * ```
 * <dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintHelper
 *   android:layout_width="wrap_content"
 *   android:layout_height="wrap_content"
 *   app:paddingSystemWindowInsets="left|right"
 *   app:constraint_referenced_ids="image" />
 *
 * <dev.chrisbanes.insetter.widgets.constraintlayout.InsetterConstraintHelper
 *   android:layout_width="wrap_content"
 *   android:layout_height="wrap_content"
 *   app:paddingSystemGestureInsets="top|bottom"
 *   app:constraint_referenced_ids="image" />
 * ```
 */
open class InsetterConstraintHelper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintHelper(context, attrs, defStyleAttr) {

    private var insetterDirty = false
    private var insetter: Insetter

    /**
     * The sides on which system window insets should be applied to the padding.
     * This value can be set using the `app:paddingSystemWindowInsets` attribute.
     *
     * @see WindowInsetsCompat.getSystemWindowInsets
     */
    @delegate:Sides
    var systemWindowInsetsPaddingSides: Int by observable(0) { _, _, _ ->
        invalidateInsetter()
    }

    /**
     * The sides on which system gesture insets should be applied to the padding.
     * This value can be set using the `app:paddingSystemGestureInsets` attribute.
     *
     * @see WindowInsetsCompat.getSystemGestureInsets
     */
    @delegate:Sides
    var systemGestureInsetsPaddingSides: Int by observable(0) { _, _, _ ->
        invalidateInsetter()
    }

    /**
     * Whether how to consume the system window insets. Can be one of
     * [Insetter.CONSUME_ALL] or [Insetter.CONSUME_AUTO].
     */
    @delegate:Insetter.ConsumeOptions
    var consumeSystemWindowInsets: Int by observable(0) { _, _, _ ->
        invalidateInsetter()
    }

    /**
     * The sides on which system window insets should be applied to the margin.
     * This value can be set using the `app:layout_marginSystemWindowInsets` attribute.
     *
     * @see WindowInsetsCompat.getSystemWindowInsets
     */
    @delegate:Sides
    var systemWindowInsetsMarginSides: Int by observable(Insetter.CONSUME_NONE) { _, _, _ ->
        invalidateInsetter()
    }

    /**
     * The sides on which system gesture insets should be applied to the margin.
     * This value can be set using the `app:layout_marginSystemGestureInsets` attribute.
     *
     * @see WindowInsetsCompat.getSystemGestureInsets
     */
    @delegate:Sides
    var systemGestureInsetsMarginSides: Int by observable(0) { _, _, _ ->
        invalidateInsetter()
    }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.InsetterConstraintHelper)

        val paddingSystemWindowInsetsFlags = ta.getInt(
            R.styleable.InsetterConstraintHelper_paddingSystemWindowInsets,
            0
        )
        systemWindowInsetsPaddingSides = flagToSides(paddingSystemWindowInsetsFlags)

        val marginSystemWindowInsetsFlags = ta.getInt(
            R.styleable.InsetterConstraintHelper_layout_marginSystemWindowInsets,
            0
        )
        systemWindowInsetsMarginSides = flagToSides(marginSystemWindowInsetsFlags)

        val paddingSystemGestureInsetsFlags = ta.getInt(
            R.styleable.InsetterConstraintHelper_paddingSystemGestureInsets,
            0
        )
        systemGestureInsetsPaddingSides = flagToSides(paddingSystemGestureInsetsFlags)

        val marginSystemGestureInsetsFlags = ta.getInt(
            R.styleable.InsetterConstraintHelper_layout_marginSystemGestureInsets,
            0
        )
        systemGestureInsetsMarginSides = flagToSides(marginSystemGestureInsetsFlags)

        consumeSystemWindowInsets = ta.getInt(
            R.styleable.InsetterConstraintHelper_consumeSystemWindowInsets,
            consumeSystemWindowInsets
        )

        ta.recycle()

        insetter = buildInsetter()
        // We've just built the insetter, so reset any dirty flag
        insetterDirty = false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // When we're attached to the window, store the initial view state for each sibling view
        for (view in getViews(parent as ConstraintLayout)) {
            if (view != null && view.getTag(R.id.insetter_initial_state) == null) {
                view.setTag(R.id.insetter_initial_state, ViewState(view))
            }
        }
    }

    @RequiresApi(20)
    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        val insetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets)

        if (insetterDirty) {
            // If the insetter is dirty, rebuild it now
            insetter = buildInsetter()
            insetterDirty = false
        }

        for (view in getViews(parent as ConstraintLayout)) {
            val state = view.getTag(R.id.insetter_initial_state) as? ViewState
            if (state != null) {
                insetter.applyInsetsToView(view, insetsCompat, state)
            }
        }

        return insetsCompat.toWindowInsets()!!
    }

    private fun invalidateInsetter() {
        // Mark the insetter as 'dirty'
        insetterDirty = true
        // And request some new insets. The new insetter will be built in onApplyWindowInsets()
        ViewCompat.requestApplyInsets(this)
    }

    private fun buildInsetter(): Insetter = Insetter.builder()
        .applyAsPadding(
            windowInsetTypesOf(ime = true, statusBars = true, navigationBars = true),
            systemWindowInsetsPaddingSides
        )
        .applyAsMargin(
            windowInsetTypesOf(ime = true, statusBars = true, navigationBars = true),
            systemWindowInsetsMarginSides
        )
        .applyAsPadding(
            windowInsetTypesOf(systemGestures = true),
            systemGestureInsetsPaddingSides
        )
        .applyAsMargin(
            windowInsetTypesOf(systemGestures = true),
            systemGestureInsetsMarginSides
        )
        .consume(consumeSystemWindowInsets)
        .build()
}
