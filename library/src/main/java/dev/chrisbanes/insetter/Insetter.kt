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

@file:Suppress("DEPRECATION")

package dev.chrisbanes.insetter

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.chrisbanes.insetter.Insetter.Builder
import java.util.Locale

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
 * consume the system window insets, you can use the [Builder.consumeSystemWindowInsets] function.
 */
class Insetter private constructor(builder: Builder) {
    @IntDef(value = [CONSUME_NONE, CONSUME_ALL, CONSUME_AUTO])
    @Retention(AnnotationRetention.SOURCE)
    annotation class ConsumeOptions

    private val onApplyInsetsListener: OnApplyInsetsListener?
    private val paddingSystemWindowInsets: Int
    private val marginSystemWindowInsets: Int
    private val paddingSystemGestureInsets: Int
    private val marginSystemGestureInsets: Int
    private val consumeSystemWindowInsets: Int

    init {
        onApplyInsetsListener = builder.onApplyInsetsListener
        paddingSystemWindowInsets = builder.paddingSystemWindowInsets
        marginSystemWindowInsets = builder.marginSystemWindowInsets
        paddingSystemGestureInsets = builder.paddingSystemGestureInsets
        marginSystemGestureInsets = builder.marginSystemGestureInsets
        consumeSystemWindowInsets = builder.consumeSystemWindowInsets
    }

    /** A builder class for creating instances of [Insetter].  */
    class Builder {
        internal var onApplyInsetsListener: OnApplyInsetsListener? = null
        internal var paddingSystemWindowInsets = 0
        internal var marginSystemWindowInsets = 0
        internal var paddingSystemGestureInsets = 0
        internal var marginSystemGestureInsets = 0
        internal var consumeSystemWindowInsets = CONSUME_NONE

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
         * @param flags specifies the sides on which the system window insets should be applied to the
         * padding. Ignored if [Insetter.onApplyInsetsListener] is set.
         * @see Insetter.applyInsetsToView
         */
        fun applySystemWindowInsetsToPadding(@Sides flags: Int): Builder {
            paddingSystemWindowInsets = flags
            return this
        }

        /**
         * @param flags specifies the sides on which the system window insets should be applied to the
         * margin. Ignored if [Insetter.onApplyInsetsListener] is set.
         * @see Insetter.applyInsetsToView
         */
        fun applySystemWindowInsetsToMargin(@Sides flags: Int): Builder {
            marginSystemWindowInsets = flags
            return this
        }

        /**
         * @param flags specifies the sides on which the system gesture insets should be applied to the
         * padding. Ignored if [Insetter.onApplyInsetsListener] is set.
         * @see Insetter.applyInsetsToView
         */
        fun applySystemGestureInsetsToPadding(@Sides flags: Int): Builder {
            paddingSystemGestureInsets = flags
            return this
        }

        /**
         * @param flags specifies the sides on which the system gesture insets should be applied to the
         * margin. Ignored if [Insetter.onApplyInsetsListener] is set.
         * @see Insetter.applyInsetsToView
         */
        fun applySystemGestureInsetsToMargin(@Sides flags: Int): Builder {
            marginSystemGestureInsets = flags
            return this
        }

        /**
         * @param consumeSystemWindowInsets true if the system window insets should be consumed, false
         * if not. If unset, the default behavior is to not consume system window insets.
         * @see Insetter.applyToView
         */
        fun consumeSystemWindowInsets(consumeSystemWindowInsets: Boolean): Builder {
            return consumeSystemWindowInsets(if (consumeSystemWindowInsets) CONSUME_ALL else CONSUME_NONE)
        }

        /**
         * @param consume how the system window insets should be consumed.
         * @see Insetter.CONSUME_NONE
         *
         * @see Insetter.CONSUME_ALL
         *
         * @see Insetter.CONSUME_AUTO
         */
        fun consumeSystemWindowInsets(@ConsumeOptions consume: Int): Builder {
            consumeSystemWindowInsets = consume
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
            @Sides val sidesApplied = if (onApplyInsetsListener != null) {
                onApplyInsetsListener.onApplyInsets(v, insets, initialState)
                // We don't have what sides all have been applied, so we assume all
                Side.ALL
            } else {
                applyInsetsToView(v, insets, initialState)
                (
                    paddingSystemWindowInsets
                        or marginSystemWindowInsets
                        or paddingSystemGestureInsets
                        or marginSystemGestureInsets
                    )
            }

            when (consumeSystemWindowInsets) {
                CONSUME_ALL -> insets.consumeSystemWindowInsets()
                CONSUME_AUTO -> when {
                    sidesApplied and Side.ALL == Side.NONE -> {
                        // If we did not apply any sides, just return the insets
                        insets
                    }
                    sidesApplied and Side.ALL == Side.ALL -> {
                        // If all sides were applied, just return a consumed insets
                        insets.consumeSystemWindowInsets()
                    }
                    else -> {
                        // Otherwise we need to go through and consume each side
                        var left = insets.systemWindowInsetLeft
                        var top = insets.systemWindowInsetTop
                        var right = insets.systemWindowInsetRight
                        var bottom = insets.systemWindowInsetBottom

                        if (Side.hasSide(sidesApplied, Side.LEFT)) left = 0
                        if (Side.hasSide(sidesApplied, Side.TOP)) top = 0
                        if (Side.hasSide(sidesApplied, Side.RIGHT)) right = 0
                        if (Side.hasSide(sidesApplied, Side.BOTTOM)) bottom = 0

                        insets.replaceSystemWindowInsets(left, top, right, bottom)
                    }
                }
                else -> insets
            }
        }

        // Now request an insets pass
        requestApplyInsetsWhenAttached(view)
    }

    /**
     * A convenience function which applies insets to a view.
     *
     *
     * How the given insets are applied depends on the options provided via the various parameters.
     * Each of `paddingSystemWindowInsets`, `marginSystemWindowInsets`,
     * `paddingSystemGestureInsets` and `marginSystemGestureInsets` accept side flag values.
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
        val systemWindowInsets = insets.systemWindowInsets
        val systemGestureInsets = insets.systemGestureInsets
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "applyInsetsToView. View: $view. Insets: $insets. State: $initialState")
        }

        val initialPadding = initialState.paddings

        var paddingLeft = view.paddingLeft
        if (Side.hasSide(paddingSystemGestureInsets, Side.LEFT)) {
            paddingLeft = initialPadding.left + systemGestureInsets.left
        } else if (Side.hasSide(paddingSystemWindowInsets, Side.LEFT)) {
            paddingLeft = initialPadding.left + systemWindowInsets.left
        }

        var paddingTop = view.paddingTop
        if (Side.hasSide(paddingSystemGestureInsets, Side.TOP)) {
            paddingTop = initialPadding.top + systemGestureInsets.top
        } else if (Side.hasSide(paddingSystemWindowInsets, Side.TOP)) {
            paddingTop = initialPadding.top + systemWindowInsets.top
        }

        var paddingRight = view.paddingRight
        if (Side.hasSide(paddingSystemGestureInsets, Side.RIGHT)) {
            paddingRight = initialPadding.right + systemGestureInsets.right
        } else if (Side.hasSide(paddingSystemWindowInsets, Side.RIGHT)) {
            paddingRight = initialPadding.right + systemWindowInsets.right
        }

        var paddingBottom = view.paddingBottom
        if (Side.hasSide(paddingSystemGestureInsets, Side.BOTTOM)) {
            paddingBottom = initialPadding.bottom + systemGestureInsets.bottom
        } else if (Side.hasSide(paddingSystemWindowInsets, Side.BOTTOM)) {
            paddingBottom = initialPadding.bottom + systemWindowInsets.bottom
        }

        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(
                TAG,
                "applyInsetsToView. Applied padding to $view: left=$paddingLeft, top=$paddingTop, right=$paddingRight, bottom=$paddingBottom}"
            )
        }

        // Now we can deal with margins
        val lp = view.layoutParams
        if (lp is MarginLayoutParams) {
            val initialMargins = initialState.margins
            var marginLeft = lp.leftMargin
            if (Side.hasSide(marginSystemGestureInsets, Side.LEFT)) {
                marginLeft = initialMargins.left + systemGestureInsets.left
            } else if (Side.hasSide(marginSystemWindowInsets, Side.LEFT)) {
                marginLeft = initialMargins.left + systemWindowInsets.left
            }
            var marginTop = lp.topMargin
            if (Side.hasSide(marginSystemGestureInsets, Side.TOP)) {
                marginTop = initialMargins.top + systemGestureInsets.top
            } else if (Side.hasSide(marginSystemWindowInsets, Side.TOP)) {
                marginTop = initialMargins.top + systemWindowInsets.top
            }
            var marginRight = lp.rightMargin
            if (Side.hasSide(marginSystemGestureInsets, Side.RIGHT)) {
                marginRight = initialMargins.right + systemGestureInsets.right
            } else if (Side.hasSide(marginSystemWindowInsets, Side.RIGHT)) {
                marginRight = initialMargins.right + systemWindowInsets.right
            }
            var marginBottom = lp.bottomMargin
            if (Side.hasSide(marginSystemGestureInsets, Side.BOTTOM)) {
                marginBottom = initialMargins.bottom + systemGestureInsets.bottom
            } else if (Side.hasSide(marginSystemWindowInsets, Side.BOTTOM)) {
                marginBottom = initialMargins.bottom + systemWindowInsets.bottom
            }

            if (lp.leftMargin != marginLeft ||
                lp.topMargin != marginTop ||
                lp.rightMargin != marginRight ||
                lp.bottomMargin != marginBottom
            ) {
                lp.leftMargin = marginLeft
                lp.topMargin = marginTop
                lp.rightMargin = marginRight
                lp.bottomMargin = marginBottom
                view.layoutParams = lp

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    view.parent.requestLayout()
                }
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(
                        TAG,
                        String.format(
                            Locale.US,
                            "applyInsetsToView. Applied margin to %s: left=%d, top=%d, right=%d, bottom=%d}",
                            view,
                            marginLeft,
                            marginTop,
                            marginRight,
                            marginBottom
                        )
                    )
                }
            }
        } else {
            require(!(marginSystemGestureInsets != Side.NONE || marginSystemWindowInsets != Side.NONE)) {
                (
                    "Margin inset handling requested but view LayoutParams do not" +
                        " extend MarginLayoutParams"
                    )
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

        /**
         * A wrapper around [ViewCompat.requestApplyInsets] which ensures the request will
         * happen, regardless of whether the view is attached or not.
         */
        private fun requestApplyInsetsWhenAttached(view: View) {
            if (ViewCompat.isAttachedToWindow(view)) {
                // If the view is already attached, we can request a pass
                ViewCompat.requestApplyInsets(view)
            } else {
                // If the view is not attached, calls to requestApplyInsets() will be ignored.
                // We can just wait until the view is attached before requesting.
                view.addOnAttachStateChangeListener(
                    object : OnAttachStateChangeListener {
                        override fun onViewAttachedToWindow(v: View) {
                            v.removeOnAttachStateChangeListener(this)
                            ViewCompat.requestApplyInsets(v)
                        }

                        override fun onViewDetachedFromWindow(v: View) {
                            // no-op
                        }
                    })
            }
        }

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
            view.systemUiVisibility = view.systemUiVisibility and
                EDGE_TO_EDGE_FLAGS.inv() or
                if (enabled) EDGE_TO_EDGE_FLAGS else 0
        }

        @SuppressLint("InlinedApi")
        @VisibleForTesting
        @JvmField
        internal val EDGE_TO_EDGE_FLAGS = (
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            )

        private const val TAG = "Insetter"
    }
}
