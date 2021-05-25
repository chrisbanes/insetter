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

import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.IntDef
import androidx.core.graphics.Insets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnAttach
import dev.chrisbanes.insetter.Insetter.Builder

/**
 * A class to make handling [android.view.WindowInsets] easier.
 *
 * It includes a [Builder] for building easy-to-use [OnApplyWindowInsetsListener]
 * instances:
 *
 * ```
 * Insetter.builder()
 *     // This will apply the navigation bar insets as padding to all sides of the view
 *     .padding(windowInsetTypesOf(navigationBars = true))
 *     // This is a shortcut for view.setOnApplyWindowInsetsListener(builder.build())
 *     .applyToView(view)
 * ```
 *
 * Each inset type available in [WindowInsetsCompat] is included, with variants for applying the
 * inset as either padding or margin on the view, on specified sides.
 *
 * You can also provide custom logic via the [Builder.setOnApplyInsetsListener] function.
 * The listener type is slightly different to [OnApplyWindowInsetsListener], in that it contains
 * a third parameter to tell you what the initial view padding/margin state is.
 *
 * By default the listener will not consume any insets which are passed to it. If you wish to
 * consume the system window insets, you can specify the desired behavior via
 * the [Builder.consume] function.
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

    private val paddingTypes: SideApply
    private val marginTypes: SideApply

    private val allTypes
        get() = paddingTypes + marginTypes

    @ConsumeOptions
    private val consume: Int

    init {
        onApplyInsetsListener = builder.onApplyInsetsListener
        paddingTypes = builder.padding
        marginTypes = builder.margin
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
         * Defaults to [Side.ALL] to apply all sides. The mask can be created via [sidesOf].
         *
         * @see [paddingLeft]
         * @see [paddingTop]
         * @see [paddingRight]
         * @see [paddingBottom]
         * @see [windowInsetTypesOf]
         * @see [sidesOf]
         * @see [Side.create]
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
         *
         * @see [windowInsetTypesOf]
         */
        fun paddingLeft(insetType: Int): Builder = padding(insetType, Side.LEFT)

        /**
         * Apply the top value of the given [WindowInsetsCompat.Type][insetType] as the
         * top padding of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as padding.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         *
         * @see [windowInsetTypesOf]
         */
        fun paddingTop(insetType: Int): Builder = padding(insetType, Side.TOP)

        /**
         * Apply the right value of the given [WindowInsetsCompat.Type][insetType] as the
         * right padding of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as padding.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         *
         * @see [windowInsetTypesOf]
         */
        fun paddingRight(insetType: Int): Builder = padding(insetType, Side.RIGHT)

        /**
         * Apply the bottom value of the given [WindowInsetsCompat.Type][insetType] as the
         * bottom padding of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as padding.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         *
         * @see [windowInsetTypesOf]
         */
        fun paddingBottom(insetType: Int): Builder = padding(insetType, Side.BOTTOM)

        /**
         * Apply the given [sides] dimension of the given [WindowInsetsCompat.Type][insetType]
         * as the corresponding margin side of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as margin.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         * @param sides Bit mask of [Side]s containing which sides to apply.
         * Defaults to [Side.ALL] to apply all sides. The mask can be created via [sidesOf].
         *
         * @see [marginLeft]
         * @see [marginTop]
         * @see [marginRight]
         * @see [marginBottom]
         * @see [windowInsetTypesOf]
         * @see [sidesOf]
         * @see [Side.create]
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
         *
         * @see [windowInsetTypesOf]
         */
        fun marginLeft(insetType: Int): Builder = margin(insetType, Side.LEFT)

        /**
         * Apply the top value of the given [WindowInsetsCompat.Type][insetType] as the
         * top margin of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as margin.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         *
         * @see [windowInsetTypesOf]
         */
        fun marginTop(insetType: Int): Builder = margin(insetType, Side.TOP)

        /**
         * Apply the right value of the given [WindowInsetsCompat.Type][insetType] as the
         * right margin of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as margin.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         *
         * @see [windowInsetTypesOf]
         */
        fun marginRight(insetType: Int): Builder = margin(insetType, Side.RIGHT)

        /**
         * Apply the bottom value of the given [WindowInsetsCompat.Type][insetType] as the
         * bottom margin of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as margin.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         *
         * @see [windowInsetTypesOf]
         */
        fun marginBottom(insetType: Int): Builder = margin(insetType, Side.BOTTOM)

        /**
         * @param consume how the window insets should be consumed.
         * @see ConsumeOptions
         */
        fun consume(@ConsumeOptions consume: Int): Builder {
            this.consume = consume
            return this
        }

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
                return@setOnApplyWindowInsetsListener when (consume) {
                    CONSUME_NONE -> insets
                    else -> WindowInsetsCompat.CONSUMED
                }
            }

            // Otherwise we applied through applyInsetsToView()
            applyInsetsToView(v, insets, initialState)

            when (consume) {
                CONSUME_ALL -> WindowInsetsCompat.CONSUMED
                CONSUME_AUTO -> {
                    WindowInsetsCompat.Builder(insets)
                        .consumeType(WindowInsetsCompat.Type.statusBars(), insets, allTypes)
                        .consumeType(WindowInsetsCompat.Type.navigationBars(), insets, allTypes)
                        .consumeType(WindowInsetsCompat.Type.ime(), insets, allTypes)
                        .consumeType(WindowInsetsCompat.Type.systemGestures(), insets, allTypes)
                        .consumeType(WindowInsetsCompat.Type.displayCutout(), insets, allTypes)
                        .build()
                }
                else -> insets
            }
        }

        // Now request an insets pass
        view.doOnEveryAttach { v ->
            ViewCompat.requestApplyInsets(v)
        }
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
        view.applyPadding(insets, paddingTypes, initialState.paddings)
        view.applyMargins(insets, marginTypes, initialState.margins)
    }

    /**
     * Internal class used to store which types to apply on each side using a given
     * application type (padding, margin, etc).
     */
    internal class SideApply {
        @Sides
        var left: Int = Side.NONE
            private set

        @Sides
        var top: Int = Side.NONE
            private set

        @Sides
        var right: Int = Side.NONE
            private set

        @Sides
        var bottom: Int = Side.NONE
            private set

        val isEmpty: Boolean
            get() = all == Side.NONE

        val all: Int
            get() = left or top or right or bottom

        fun add(insetTypes: Int, @Sides sides: Int = Side.ALL) {
            if (sides and Side.LEFT != 0) left = left or insetTypes
            if (sides and Side.TOP != 0) top = top or insetTypes
            if (sides and Side.RIGHT != 0) right = right or insetTypes
            if (sides and Side.BOTTOM != 0) bottom = bottom or insetTypes
        }

        operator fun plus(other: SideApply): SideApply {
            val lhs = this
            return SideApply().apply {
                left = lhs.left or other.left
                top = lhs.top or other.top
                right = lhs.right or other.right
                bottom = lhs.bottom or other.bottom
            }
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

        private const val TAG = "Insetter"
    }
}

/**
 * Applies the window insets types specified in [typesToApply], using the source values
 * from [insets] as padding.
 */
private fun View.applyPadding(
    insets: WindowInsetsCompat,
    typesToApply: Insetter.SideApply,
    initialPaddings: ViewDimensions,
) {
    // If there's no types to apply, nothing to do...
    if (typesToApply.isEmpty) return

    val paddingLeft = when (typesToApply.left) {
        Side.NONE -> paddingLeft
        else -> initialPaddings.left + insets.getInsets(typesToApply.left).left
    }
    val paddingTop = when (typesToApply.top) {
        Side.NONE -> paddingTop
        else -> initialPaddings.top + insets.getInsets(typesToApply.top).top
    }
    val paddingRight = when (typesToApply.right) {
        Side.NONE -> paddingRight
        else -> initialPaddings.right + insets.getInsets(typesToApply.right).right
    }
    val paddingBottom = when (typesToApply.bottom) {
        Side.NONE -> paddingBottom
        else -> initialPaddings.bottom + insets.getInsets(typesToApply.bottom).bottom
    }

    // setPadding() does it's own value change check, so no need to do our own to avoid layout
    setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
}

/**
 * Applies the window insets types specified in [typesToApply], using the source values
 * from [insets] as margin.
 *
 * @throws IllegalArgumentException if [View.getLayoutParams] do not extend [MarginLayoutParams]
 */
private fun View.applyMargins(
    insets: WindowInsetsCompat,
    typesToApply: Insetter.SideApply,
    initialMargins: ViewDimensions,
) {
    // If there's no types to apply, nothing to do...
    if (typesToApply.isEmpty) return

    val lp = layoutParams
    require(lp is MarginLayoutParams) {
        "Margin window insets handling requested but View's" +
            " LayoutParams do not extend MarginLayoutParams"
    }

    val marginLeft = when (typesToApply.left) {
        Side.NONE -> lp.leftMargin
        else -> initialMargins.left + insets.getInsets(typesToApply.left).left
    }
    val marginTop = when (typesToApply.top) {
        Side.NONE -> lp.topMargin
        else -> initialMargins.top + insets.getInsets(typesToApply.top).top
    }
    val marginRight = when (typesToApply.right) {
        Side.NONE -> lp.rightMargin
        else -> initialMargins.right + insets.getInsets(typesToApply.right).right
    }
    val marginBottom = when (typesToApply.bottom) {
        Side.NONE -> lp.bottomMargin
        else -> initialMargins.bottom + insets.getInsets(typesToApply.bottom).bottom
    }

    // Update the layoutParams margins. Will return true if any value has changed
    if (lp.updateMargins(marginLeft, marginTop, marginRight, marginBottom)) {
        // If any margin value changed, re-set it back on the view to trigger a layout
        layoutParams = lp

        if (Build.VERSION.SDK_INT < 26) {
            // See https://github.com/chrisbanes/insetter/issues/42
            parent.requestLayout()
        }
    }
}

/**
 * Performs the given action when this view is attached to a window. If the view is already
 * attached to a window the action will be performed immediately, otherwise the
 * action will first be performed after the view is next attached.
 *
 * The action will be invoked every time the view is attached to a window.
 *
 * @see doOnAttach
 */
private inline fun View.doOnEveryAttach(crossinline action: (view: View) -> Unit) {
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) = action(v)

        override fun onViewDetachedFromWindow(v: View) = Unit
    })

    if (ViewCompat.isAttachedToWindow(this)) {
        action(this)
    }
}

/**
 * Function which consumes the insets for the given [type], if it exists in the [applied] types.
 *
 * @param windowInsets The original [WindowInsetsCompat] to retrieve the original insets from.
 */
private fun WindowInsetsCompat.Builder.consumeType(
    type: Int,
    windowInsets: WindowInsetsCompat,
    applied: Insetter.SideApply,
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
