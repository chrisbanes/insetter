/*
 * Copyright 2019 Google LLC
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

package dev.chrisbanes.insetter

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnAttach
import dev.chrisbanes.insetter.Insetter.Builder

/**
 * A helper class to make handling [android.view.WindowInsets] easier.
 *
 * It includes a [Builder] for building easy-to-use [OnApplyWindowInsetsListener]
 * instances:
 *
 * ```
 * Insetter.builder()
 *   // This will apply the system window insets as padding to left, bottom and right of the view
 *   .applySystemWindowInsetsToPadding(Side.LEFT | Side.BOTTOM | Side.RIGHT)
 *   // This is a shortcut for view.setOnApplyWindowInsetsListener(builder.build())
 *   .applyToView(view);
 * ```
 *
 * Each inset type as on Android 10 (API level 29) is included, with variants for applying the
 * inset as either padding or margin on the view.
 *
 * You can also provide custom logic via the [Builder.setOnApplyInsetsListener] function.
 * The listener type is slightly different to [OnApplyWindowInsetsListener], in that it contains
 * a third parameter to tell you what the initial view padding/margin state is.
 *
 * By default the listener will not consume any insets which are passed to it. If you wish to
 * consume the system window insets, you can use the [Builder.consume] function.
 */
class Insetter private constructor(builder: Builder) {
    @IntDef(value = [CONSUME_NONE, CONSUME_ALL, CONSUME_AUTO])
    @Retention(AnnotationRetention.SOURCE)
    @Target(
        AnnotationTarget.VALUE_PARAMETER,
        AnnotationTarget.PROPERTY,
        AnnotationTarget.LOCAL_VARIABLE,
        AnnotationTarget.FIELD,
        AnnotationTarget.FUNCTION
    )
    annotation class ConsumeOptions

    private val onApplyInsetsListener: OnApplyInsetsListener?
    internal var padding: SideApply
        private set
    internal var margin: SideApply
        private set
    @ConsumeOptions private val consume: Int

    init {
        onApplyInsetsListener = builder.onApplyInsetsListener
        padding = builder.padding
        margin = builder.margin
        consume = builder.consume
    }

    /** A builder class for creating instances of [Insetter].  */
    class Builder internal constructor() {
        internal var onApplyInsetsListener: OnApplyInsetsListener? = null

        internal var padding = SideApply()
        internal var margin = SideApply()
        internal var consume = CONSUME_NONE

        /**
         * @param onApplyInsetsListener Callback for supplying custom logic to apply insets. If set,
         * Insetter will ignore any specified side flags, and the caller is responsible for applying
         * insets.
         * @see Insetter.applyToView
         */
        fun setOnApplyInsetsListener(onApplyInsetsListener: OnApplyInsetsListener?): Builder {
            this.onApplyInsetsListener = onApplyInsetsListener
            return this
        }

        /**
         * Apply the given [sides] dimension of the given [WindowInsetsCompat.Type][insetType]
         * as the corresponding padding dimension of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as padding.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         * @param sides Bit mask of [Side]s containing which sides to apply.
         * Defaults to [Side.ALL] to apply all sides. The mask can be created via [Side.create].
         *
         * @see [paddingLeft]
         * @see [paddingTop]
         * @see [paddingRight]
         * @see [paddingBottom]
         */
        @JvmOverloads
        fun padding(insetType: Int, @Sides sides: Int = Side.ALL): Builder {
            padding.add(insetType, sides)
            return this
        }

        /**
         * Apply the left value of the given [WindowInsetsCompat.Type][insetType] as the
         * left padding of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as padding.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         */
        fun paddingLeft(insetType: Int): Builder = padding(insetType, Side.LEFT)

        /**
         * Apply the top value of the given [WindowInsetsCompat.Type][insetType] as the
         * top padding of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as padding.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         */
        fun paddingTop(insetType: Int): Builder = padding(insetType, Side.TOP)

        /**
         * Apply the right value of the given [WindowInsetsCompat.Type][insetType] as the
         * right padding of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as padding.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         */
        fun paddingRight(insetType: Int): Builder = padding(insetType, Side.RIGHT)

        /**
         * Apply the bottom value of the given [WindowInsetsCompat.Type][insetType] as the
         * bottom padding of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as padding.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         */
        fun paddingBottom(insetType: Int): Builder = padding(insetType, Side.BOTTOM)

        /**
         * Apply the given [sides] dimension of the given [WindowInsetsCompat.Type][insetType]
         * as the corresponding margin side of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as margin.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         * @param sides Bit mask of [Side]s containing which sides to apply.
         * Defaults to [Side.ALL] to apply all sides. The mask can be created via [Side.create].
         *
         * @see [marginLeft]
         * @see [marginTop]
         * @see [marginRight]
         * @see [marginBottom]
         */
        @JvmOverloads
        fun margin(insetType: Int, @Sides sides: Int = Side.ALL): Builder {
            margin.add(insetType, sides)
            return this
        }

        /**
         * Apply the left value of the given [WindowInsetsCompat.Type][insetType] as the
         * left margin of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as margin.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         */
        fun marginLeft(insetType: Int): Builder = margin(insetType, Side.LEFT)

        /**
         * Apply the top value of the given [WindowInsetsCompat.Type][insetType] as the
         * top margin of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as margin.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         */
        fun marginTop(insetType: Int): Builder = margin(insetType, Side.TOP)

        /**
         * Apply the right value of the given [WindowInsetsCompat.Type][insetType] as the
         * right margin of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as margin.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         */
        fun marginRight(insetType: Int): Builder = margin(insetType, Side.RIGHT)

        /**
         * Apply the bottom value of the given [WindowInsetsCompat.Type][insetType] as the
         * bottom margin of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as margin.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         */
        fun marginBottom(insetType: Int): Builder = margin(insetType, Side.BOTTOM)

        /**
         * @param sides specifies the sides on which the system window insets should be applied
         * to the padding. Ignored if [Insetter.onApplyInsetsListener] is set.
         * @see Insetter.applyInsetsToView
         */
        @Deprecated(
            "Replaced with applyAsPadding()",
            ReplaceWith(
                "applyAsPadding(windowInsetTypesOf(ime = true, statusBars = true, navigationBars = true), sides)",
                "dev.chrisbanes.insetter.windowInsetTypesOf"
            )
        )
        fun applySystemWindowInsetsToPadding(@Sides sides: Int): Builder {
            return padding(
                windowInsetTypesOf(ime = true, statusBars = true, navigationBars = true),
                sides
            )
        }

        /**
         * @param sides specifies the sides on which the system window insets should be applied
         * to the margin. Ignored if [Insetter.onApplyInsetsListener] is set.
         * @see Insetter.applyInsetsToView
         */
        @Deprecated(
            "Replaced with applyAsMargin()",
            ReplaceWith(
                "applyAsMargin(windowInsetTypesOf(ime = true, statusBars = true, navigationBars = true), sides)",
                "dev.chrisbanes.insetter.windowInsetTypesOf"
            )
        )
        fun applySystemWindowInsetsToMargin(@Sides sides: Int): Builder {
            return margin(
                windowInsetTypesOf(ime = true, statusBars = true, navigationBars = true),
                sides
            )
        }

        /**
         * @param sides specifies the sides on which the system gesture insets should be applied
         * to the padding. Ignored if [Insetter.onApplyInsetsListener] is set.
         * @see Insetter.applyInsetsToView
         */
        @Deprecated(
            "Replaced with applyAsPadding()",
            ReplaceWith(
                "applyAsPadding(windowInsetTypesOf(systemGestures = true), sides)",
                "dev.chrisbanes.insetter.windowInsetTypesOf"
            )
        )
        fun applySystemGestureInsetsToPadding(@Sides sides: Int): Builder {
            return padding(windowInsetTypesOf(systemGestures = true), sides)
        }

        /**
         * @param sides specifies the sides on which the system gesture insets should be applied
         * to the margin. Ignored if [Insetter.onApplyInsetsListener] is set.
         * @see Insetter.applyInsetsToView
         */
        @Deprecated(
            "Replaced with applyAsMargin()",
            ReplaceWith(
                "applyAsMargin(windowInsetTypesOf(systemGestures = true), sides)",
                "dev.chrisbanes.insetter.windowInsetTypesOf"
            )
        )
        fun applySystemGestureInsetsToMargin(@Sides sides: Int): Builder {
            return margin(windowInsetTypesOf(systemGestures = true), sides)
        }

        /**
         * @param consume how the window insets should be consumed.
         * @see ConsumeOptions
         */
        fun consume(@ConsumeOptions consume: Int): Builder {
            this.consume = consume
            return this
        }

        @Deprecated(
            "Migrate to consume()",
            ReplaceWith("consume(if (consumeSystemWindowInsets) Insetter.CONSUME_ALL else Insetter.CONSUME_NONE)")
        )
        fun consumeSystemWindowInsets(consumeSystemWindowInsets: Boolean): Builder = consume(
            if (consumeSystemWindowInsets) CONSUME_ALL else CONSUME_NONE
        )

        @Deprecated("Migrate to consume()", ReplaceWith("consume(consume)"))
        fun consumeSystemWindowInsets(@ConsumeOptions consume: Int): Builder = consume(consume)

        /**
         * Builds the [Insetter] instance and sets it as an
         * [OnApplyWindowInsetsListener][androidx.core.view.OnApplyWindowInsetsListener] on
         * the provided [View].
         *
         * @param view the [View] on which [WindowInsetsCompat] should be applied
         */
        fun applyToView(view: View): Insetter {
            val insetter = build()
            insetter.applyToView(view)
            return insetter
        }

        /**
         * Builds the [Insetter] instance.
         */
        fun build(): Insetter = Insetter(this)
    }

    /**
     * A wrapper around [ViewCompat.setOnApplyWindowInsetsListener] which stores the initial view state, and provides them whenever a
     * [android.view.WindowInsets] instance is dispatched to the listener provided.
     *
     *
     * This allows the listener to be able to append inset values to any existing view state
     * properties, rather than overwriting them.
     */
    fun applyToView(view: View) {
        val tagState = view.getTag(R.id.insetter_initial_state) as? ViewState
        val initialState: ViewState
        if (tagState != null) {
            initialState = tagState
        } else {
            initialState = ViewState(view)
            view.setTag(R.id.insetter_initial_state, initialState)
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            if (onApplyInsetsListener != null) {
                // If we have an onApplyInsetsListener, invoke it
                onApplyInsetsListener.onApplyInsets(v, insets, initialState)
                // We don't know what sides have been applied, so we assume all
                return@setOnApplyWindowInsetsListener WindowInsetsCompat.CONSUMED
            }

            // Otherwise we applied through applyInsetsToView()
            applyInsetsToView(v, insets, initialState)

            when (consume) {
                CONSUME_ALL -> WindowInsetsCompat.CONSUMED
                CONSUME_AUTO -> {
                    val applied = padding + margin
                    WindowInsetsCompat.Builder(insets)
                        .consumeType(insets, WindowInsetsCompat.Type.statusBars(), applied)
                        .consumeType(insets, WindowInsetsCompat.Type.navigationBars(), applied)
                        .consumeType(insets, WindowInsetsCompat.Type.ime(), applied)
                        .consumeType(insets, WindowInsetsCompat.Type.systemGestures(), applied)
                        .consumeType(insets, WindowInsetsCompat.Type.displayCutout(), applied)
                        .build()
                }
                else -> insets
            }
        }

        // Now request an insets pass
        view.doOnAttach { v ->
            ViewCompat.requestApplyInsets(v)
        }
    }

    private fun WindowInsetsCompat.Builder.consumeType(
        windowInsets: WindowInsetsCompat,
        type: Int,
        applied: SideApply,
    ): WindowInsetsCompat.Builder {
        // Fast path. If this type wasn't applied at all, no need to do anything
        if (applied.all and type != type) return this

        // First we get the original insets for the type
        val insets = windowInsets.getInsets(type)

        // If the insets are empty, nothing to do
        if (insets == Insets.NONE) return this

        // Now set the insets, selectively 'consuming' (zero-ing out) any consumed sides.
        setInsets(
            type,
            Insets.of(
                if (applied.left and type != 0) 0 else insets.left,
                if (applied.top and type != 0) 0 else insets.top,
                if (applied.right and type != 0) 0 else insets.right,
                if (applied.bottom and type != 0) 0 else insets.bottom
            )
        )
        return this
    }

    /**
     * A convenience function which applies insets to a view.
     *
     * How the given insets are applied depends on the options provided to the [Builder]
     * via the various parameters.
     *
     * @param view the view to apply inset handling too
     * @param insets the insets to apply
     * @param initialState the initial view state of the view
     */
    fun applyInsetsToView(
        view: View,
        insets: WindowInsetsCompat,
        initialState: ViewState
    ) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "applyInsetsToView. View: $view. Insets: $insets. State: $initialState")
        }

        val initialPaddings = initialState.paddings
        val paddingLeft = when {
            padding.left != 0 -> initialPaddings.left + insets.getInsets(padding.left).left
            else -> view.paddingLeft
        }
        val paddingTop = when {
            padding.top != 0 -> initialPaddings.top + insets.getInsets(padding.top).top
            else -> view.paddingTop
        }
        val paddingRight = when {
            padding.right != 0 -> initialPaddings.right + insets.getInsets(padding.right).right
            else -> view.paddingRight
        }
        val paddingBottom = when {
            padding.bottom != 0 -> initialPaddings.bottom + insets.getInsets(padding.bottom).bottom
            else -> view.paddingBottom
        }
        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)

        // Now we can deal with margins
        val lp = view.layoutParams
        if (lp is MarginLayoutParams) {
            val initialMargins = initialState.margins
            val marginLeft = when {
                margin.left != 0 -> initialMargins.left + insets.getInsets(margin.left).left
                else -> lp.leftMargin
            }
            val marginTop = when {
                margin.top != 0 -> initialMargins.top + insets.getInsets(margin.top).top
                else -> lp.topMargin
            }
            val marginRight = when {
                margin.right != 0 -> initialMargins.right + insets.getInsets(margin.right).right
                else -> lp.rightMargin
            }
            val marginBottom = when {
                margin.bottom != 0 -> initialMargins.bottom + insets.getInsets(margin.bottom).bottom
                else -> lp.bottomMargin
            }

            if (lp.updateMargins(marginLeft, marginTop, marginRight, marginBottom)) {
                view.layoutParams = lp

                if (Build.VERSION.SDK_INT < 26) {
                    // See https://github.com/chrisbanes/insetter/issues/42
                    view.parent.requestLayout()
                }
            }
        } else if (!margin.isEmpty) {
            error(
                "Margin window insets handling requested but View's LayoutParams " +
                    "do not extend MarginLayoutParams"
            )
        }
    }

    /**
     * Internal class used to store which types to apply on each side using a given
     * application type (padding, margin, etc).
     */
    internal class SideApply {
        var left: Int = 0
            private set
        var top: Int = 0
            private set
        var right: Int = 0
            private set
        var bottom: Int = 0
            private set

        val isEmpty: Boolean
            get() = all == 0

        val all: Int
            get() = left or top or right or bottom

        fun add(insets: Int, @Sides sides: Int = Side.ALL) {
            if (sides and Side.LEFT != 0) left = left or insets
            if (sides and Side.TOP != 0) top = top or insets
            if (sides and Side.RIGHT != 0) right = right or insets
            if (sides and Side.BOTTOM != 0) bottom = bottom or insets
        }

        operator fun plus(other: SideApply): SideApply = SideApply().apply {
            left = left or other.left
            top = top or other.top
            right = right or other.right
            bottom = bottom or other.bottom
        }
    }

    companion object {
        /** No consumption happens. This is the default value.  */
        const val CONSUME_NONE = 0

        /**
         * All sides are consumed. This is similar to [WindowInsetsCompat.consumeSystemWindowInsets].
         */
        const val CONSUME_ALL = 1

        /**
         * Any specified sides are consumed. This selectively consumes any sides which are set via
         * [Builder.applySystemWindowInsetsToPadding] or other related functions.
         */
        const val CONSUME_AUTO = 2

        /**
         * Returns a instance of [Builder] used for creating an instance of [Insetter].
         */
        @JvmStatic
        fun builder(): Builder = Builder()

        /**
         * Set this view's system-ui visibility, with the flags required to be laid out 'edge-to-edge'.
         *
         * @param enabled true if the view should request to be laid out 'edge-to-edge', false if not
         * @see View.setSystemUiVisibility
         */
        @JvmStatic
        @RequiresApi(api = 16)
        @Deprecated("Use WindowCompat.setDecorFitsSystemWindows() instead")
        fun setEdgeToEdgeSystemUiFlags(view: View, enabled: Boolean) {
            @Suppress("DEPRECATION")
            view.systemUiVisibility = view.systemUiVisibility and
                EDGE_TO_EDGE_FLAGS.inv() or
                if (enabled) EDGE_TO_EDGE_FLAGS else 0
        }

        @Suppress("DEPRECATION")
        @SuppressLint("InlinedApi")
        internal const val EDGE_TO_EDGE_FLAGS = (
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            )

        private const val TAG = "Insetter"
    }
}
