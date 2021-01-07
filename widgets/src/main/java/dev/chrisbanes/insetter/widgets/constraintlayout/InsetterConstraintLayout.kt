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
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowInsets
import androidx.annotation.RequiresApi
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
 * An extension to [ConstraintLayout] which adds enhanced support for inset handling.
 *
 * This class supports the use of `paddingSystemWindowInsets`, `paddingSystemGestureInsets`,
 * `layout_marginSystemWindowInsets` and `layout_marginSystemGestureInsets` attributes on children:
 *
 * ```
 * <dev.chrisbanes.insetter.widgets.InsetterConstraintLayout
 *   xmlns:android="http://schemas.android.com/apk/res/android"
 *   xmlns:app="http://schemas.android.com/apk/res-auto"
 *   android:layout_width="match_parent"
 *   android:layout_height="match_parent">
 *
 *   <ImageView
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     app:paddingSystemWindowInsets="left|top|right|bottom"
 *     android:src="@drawable/icon">
 *
 * </dev.chrisbanes.insetter.widgets.InsetterConstraintLayout>
 * ```
 *
 * Each of the attributes accepts a combination of flags which defines the sides on which the
 * relevant insets will be applied.
 */
open class InsetterConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    override fun onViewAdded(view: View) {
        super.onViewAdded(view)
        view.setTag(R.id.insetter_initial_state, ViewState(view))
    }

    @RequiresApi(20)
    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        val insetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets)

        for (i in 0 until childCount) {
            val view = getChildAt(i)
            val state = view.getTag(R.id.insetter_initial_state) as ViewState
            val lp = view.layoutParams as LayoutParams
            lp.insetter.applyInsetsToView(view, insetsCompat, state)
        }

        return insetsCompat.toWindowInsets()!!
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childLp = child.layoutParams as LayoutParams
            if (childLp.getAndResetRequestApplyInsetsRequired()) {
                ViewCompat.requestApplyInsets(child)
            }
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return LayoutParams(p)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    open class LayoutParams : ConstraintLayout.LayoutParams {
        private var requestApplyInsetsRequired = true

        private var insetterDirty = false
        internal var insetter: Insetter
            private set
            get() {
                if (insetterDirty) {
                    field = buildInsetter()
                    insetterDirty = false
                }
                return field
            }

        /**
         * The sides on which system window insets should be applied to the padding.
         * This value can be set using the `app:paddingSystemWindowInsets` attribute.
         *
         * @see WindowInsetsCompat.getSystemWindowInsets
         */
        @delegate:Sides
        var systemWindowInsetsPaddingSides: Int by observable(0) { _, _, _ -> invalidateInsetter() }

        /**
         * The sides on which system gesture insets should be applied to the padding.
         * This value can be set using the `app:paddingSystemGestureInsets` attribute.
         *
         * @see WindowInsetsCompat.getSystemGestureInsets
         */
        @delegate:Sides
        var systemGestureInsetsPaddingSides: Int by observable(0) { _, _, _ -> invalidateInsetter() }

        /**
         * The behavior of consuming the window insets. Can be one of
         * [Insetter.CONSUME_ALL], [Insetter.CONSUME_AUTO] or [Insetter.CONSUME_NONE].
         */
        @delegate:Insetter.ConsumeOptions
        var consumeWindowInsets: Int by observable(0) { _, _, _ -> invalidateInsetter() }

        @Deprecated("Replaced with consumeWindowInsets", ReplaceWith("consumeWindowInsets"))
        @Insetter.ConsumeOptions
        var consumeSystemWindowInsets: Int
            get() = consumeWindowInsets
            set(value) { consumeWindowInsets = value }

        /**
         * The sides on which system window insets should be applied to the margin.
         * This value can be set using the `app:layout_marginSystemWindowInsets` attribute.
         *
         * @see WindowInsetsCompat.getSystemWindowInsets
         */
        @delegate:Sides
        var systemWindowInsetsMarginSides: Int by observable(Insetter.CONSUME_NONE) { _, _, _ -> invalidateInsetter() }

        /**
         * The sides on which system gesture insets should be applied to the margin.
         * This value can be set using the `app:layout_marginSystemGestureInsets` attribute.
         *
         * @see WindowInsetsCompat.getSystemGestureInsets
         */
        @delegate:Sides
        var systemGestureInsetsMarginSides: Int by observable(0) { _, _, _ -> invalidateInsetter() }

        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: ViewGroup.LayoutParams?) : super(source)
        constructor(source: ConstraintLayout.LayoutParams?) : super(source)

        constructor(source: LayoutParams) : super(source) {
            systemWindowInsetsPaddingSides = source.systemWindowInsetsPaddingSides
            systemWindowInsetsMarginSides = source.systemWindowInsetsMarginSides
            consumeWindowInsets = source.consumeWindowInsets
            systemGestureInsetsPaddingSides = source.systemGestureInsetsPaddingSides
            systemGestureInsetsMarginSides = source.systemGestureInsetsMarginSides
        }

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            val ta = c.obtainStyledAttributes(attrs, R.styleable.InsetterConstraintLayout_Layout)

            val paddingSystemWindowInsetsFlags = ta.getInt(
                R.styleable.InsetterConstraintLayout_Layout_paddingSystemWindowInsets,
                0
            )
            systemWindowInsetsPaddingSides = flagToSides(paddingSystemWindowInsetsFlags)

            val marginSystemWindowInsetsFlags = ta.getInt(
                R.styleable.InsetterConstraintLayout_Layout_layout_marginSystemWindowInsets,
                0
            )
            systemWindowInsetsMarginSides = flagToSides(marginSystemWindowInsetsFlags)

            val paddingSystemGestureInsetsFlags = ta.getInt(
                R.styleable.InsetterConstraintLayout_Layout_paddingSystemGestureInsets,
                0
            )
            systemGestureInsetsPaddingSides = flagToSides(paddingSystemGestureInsetsFlags)

            val marginSystemGestureInsetsFlags = ta.getInt(
                R.styleable.InsetterConstraintLayout_Layout_layout_marginSystemGestureInsets,
                0
            )
            systemGestureInsetsMarginSides = flagToSides(marginSystemGestureInsetsFlags)

            consumeWindowInsets = ta.getInt(
                R.styleable.InsetterConstraintLayout_Layout_consumeWindowInsets,
                consumeWindowInsets
            )

            ta.recycle()
        }

        init {
            insetter = buildInsetter()
            // We've just built the insetter, so reset any dirty flag
            insetterDirty = false
        }

        private fun buildInsetter(): Insetter = Insetter.builder()
            .padding(
                windowInsetTypesOf(ime = true, statusBars = true, navigationBars = true),
                systemWindowInsetsPaddingSides
            )
            .margin(
                windowInsetTypesOf(ime = true, statusBars = true, navigationBars = true),
                systemWindowInsetsMarginSides
            )
            .padding(
                windowInsetTypesOf(systemGestures = true),
                systemGestureInsetsPaddingSides
            )
            .margin(
                windowInsetTypesOf(systemGestures = true),
                systemGestureInsetsMarginSides
            )
            .consume(consumeWindowInsets)
            .build()

        private fun invalidateInsetter() {
            // Mark the insetter as 'dirty'
            insetterDirty = true
            // And request some new insets
            requestApplyInsetsRequired = true
        }

        internal fun getAndResetRequestApplyInsetsRequired(): Boolean {
            try {
                return requestApplyInsetsRequired
            } finally {
                requestApplyInsetsRequired = false
            }
        }
    }
}
