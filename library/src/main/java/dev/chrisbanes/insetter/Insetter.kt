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

@file:Suppress("MemberVisibilityCanBePrivate")

package dev.chrisbanes.insetter

import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.IntDef
import androidx.core.graphics.Insets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
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
 * inset as either padding or margin on the view, on specified sides, and whether to react to
 * [WindowInsetsAnimationCompat]s.
 *
 * You can also provide custom logic via the [Builder.setOnApplyInsetsListener] function.
 * The listener type is slightly different to [OnApplyWindowInsetsListener], in that it contains
 * a third parameter to tell you what the initial view padding/margin state is.
 *
 * By default the listener will not consume any insets which are passed to it. If you wish to
 * consume the system window insets, you can specify the desired behavior via
 * the [Builder.consume] function.
 */
class Insetter private constructor(
    private val paddingTypes: SideApply,
    private val marginTypes: SideApply,
    private val onApplyInsetsListener: OnApplyInsetsListener?,
    @ConsumeOptions private val consume: Int,
    private val animatingTypes: Int,
    private val animateSyncViews: List<View>,
    private val ignoreVisibility: Boolean,
) {
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

    private val persistentTypes: SideApply
        get() = paddingTypes + marginTypes

    private var currentlyDeferredTypes: Int = 0
    private var lastWindowInsets: WindowInsetsCompat? = null

    /** A builder class for creating instances of [Insetter].  */
    class Builder internal constructor() {
        private var onApplyInsetsListener: OnApplyInsetsListener? = null

        private var padding = SideApply()
        private var margin = SideApply()

        private var consume = CONSUME_NONE

        private var ignoreVisibility = false

        private var animatingTypes = 0
        private var animateSyncViews = ArrayList<View>()

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
         * When reacting to window insets animations it is often useful to apply the same
         * animated translation X and Y to other views. The views provided to this function
         * will have their [View.getTranslationX] & [View.getTranslationY] set to the same values
         * which are set to whatever view this [Insetter] is applied to.
         */
        fun syncTranslationTo(vararg views: View): Builder {
            animateSyncViews.addAll(views)
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
         * @param animated Whether we should animate the padding whilst a window insets animation
         * with the type is ongoing.
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
        fun padding(
            insetType: Int,
            @Sides sides: Int = Side.ALL,
            animated: Boolean = false,
        ): Builder {
            padding.plus(insetType, sides)
            if (animated) {
                animatingTypes = animatingTypes or insetType
            }
            return this
        }

        /**
         * Apply the left value of the given [WindowInsetsCompat.Type][insetType] as the
         * left padding of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as padding.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         * @param animated Whether we should animate the padding whilst a window insets animation
         * with the type is ongoing.
         *
         * @see [windowInsetTypesOf]
         */
        fun paddingLeft(
            insetType: Int,
            animated: Boolean = false,
        ): Builder = padding(insetType, Side.LEFT, animated)

        /**
         * Apply the top value of the given [WindowInsetsCompat.Type][insetType] as the
         * top padding of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as padding.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         * @param animated Whether we should animate the padding whilst a window insets animation
         * with the type is ongoing.
         *
         * @see [windowInsetTypesOf]
         */
        fun paddingTop(
            insetType: Int,
            animated: Boolean = false,
        ): Builder = padding(insetType, Side.TOP, animated)

        /**
         * Apply the right value of the given [WindowInsetsCompat.Type][insetType] as the
         * right padding of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as padding.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         * @param animated Whether we should animate the padding whilst a window insets animation
         * with the type is ongoing.
         *
         * @see [windowInsetTypesOf]
         */
        fun paddingRight(
            insetType: Int,
            animated: Boolean = false,
        ): Builder = padding(insetType, Side.RIGHT, animated)

        /**
         * Apply the bottom value of the given [WindowInsetsCompat.Type][insetType] as the
         * bottom padding of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as padding.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         * @param animated Whether we should animate the padding whilst a window insets animation
         * with the type is ongoing.
         *
         * @see [windowInsetTypesOf]
         */
        fun paddingBottom(
            insetType: Int,
            animated: Boolean = false,
        ): Builder = padding(insetType, Side.BOTTOM, animated)

        /**
         * Apply the given [sides] dimension of the given [WindowInsetsCompat.Type][insetType]
         * as the corresponding margin side of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as margin.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         * @param sides Bit mask of [Side]s containing which sides to apply.
         * Defaults to [Side.ALL] to apply all sides. The mask can be created via [sidesOf].
         * @param animated Whether we should animate the margin whilst a window insets animation
         * with the type is ongoing.
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
        fun margin(
            insetType: Int,
            @Sides sides: Int = Side.ALL,
            animated: Boolean = false,
        ): Builder {
            margin.plus(insetType, sides)
            if (animated) {
                animatingTypes = animatingTypes or insetType
            }
            return this
        }

        /**
         * Apply the left value of the given [WindowInsetsCompat.Type][insetType] as the
         * left margin of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as margin.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         * @param animated Whether we should animate the margin whilst a window insets animation
         * with the type is ongoing.
         *
         * @see [windowInsetTypesOf]
         */
        fun marginLeft(
            insetType: Int,
            animated: Boolean = false,
        ): Builder = margin(insetType, Side.LEFT, animated)

        /**
         * Apply the top value of the given [WindowInsetsCompat.Type][insetType] as the
         * top margin of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as margin.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         * @param animated Whether we should animate the margin whilst a window insets animation
         * with the type is ongoing.
         *
         * @see [windowInsetTypesOf]
         */
        fun marginTop(
            insetType: Int,
            animated: Boolean = false,
        ): Builder = margin(insetType, Side.TOP, animated)

        /**
         * Apply the right value of the given [WindowInsetsCompat.Type][insetType] as the
         * right margin of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as margin.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         * @param animated Whether we should animate the margin whilst a window insets animation
         * with the type is ongoing.
         *
         * @see [windowInsetTypesOf]
         */
        fun marginRight(
            insetType: Int,
            animated: Boolean = false,
        ): Builder = margin(insetType, Side.RIGHT, animated)

        /**
         * Apply the bottom value of the given [WindowInsetsCompat.Type][insetType] as the
         * bottom margin of the view.
         *
         * @param insetType Bit mask of [WindowInsetsCompat.Type]s to apply as margin.
         * The [windowInsetTypesOf] function is useful for creating the bit mask.
         * @param animated Whether we should animate the margin whilst a window insets animation
         * with the type is ongoing.
         *
         * @see [windowInsetTypesOf]
         */
        fun marginBottom(
            insetType: Int,
            animated: Boolean = false,
        ): Builder = margin(insetType, Side.BOTTOM, animated)

        /**
         * @param consume how the window insets should be consumed.
         * @see ConsumeOptions
         */
        fun consume(@ConsumeOptions consume: Int): Builder {
            this.consume = consume
            return this
        }

        /**
         * @param ignoreVisibility true to return the insets regardless of whether the given type is
         * currently visible or not.
         */
        fun ignoreVisibility(ignoreVisibility: Boolean): Builder {
            this.ignoreVisibility = ignoreVisibility
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
        fun build(): Insetter = Insetter(
            paddingTypes = padding,
            marginTypes = margin,
            onApplyInsetsListener = onApplyInsetsListener,
            animatingTypes = animatingTypes,
            animateSyncViews = animateSyncViews,
            consume = consume,
            ignoreVisibility = ignoreVisibility,
        )
    }

    /**
     * A wrapper around [ViewCompat.setOnApplyWindowInsetsListener] which stores the
     * initial view state, and provides them whenever a
     * [android.view.WindowInsets] instance is dispatched to the listener provided.
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
            lastWindowInsets = WindowInsetsCompat(insets)

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
                        .consumeType(Type.statusBars(), insets, persistentTypes, ignoreVisibility)
                        .consumeType(Type.navigationBars(), insets, persistentTypes, ignoreVisibility)
                        .consumeType(Type.ime(), insets, persistentTypes, ignoreVisibility)
                        .consumeType(Type.systemGestures(), insets, persistentTypes, ignoreVisibility)
                        .consumeType(Type.displayCutout(), insets, persistentTypes, ignoreVisibility)
                        .build()
                }
                else -> insets
            }
        }

        if (animatingTypes != 0) {
            ViewCompat.setWindowInsetsAnimationCallback(
                view,
                object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {
                    override fun onPrepare(animation: WindowInsetsAnimationCompat) {
                        currentlyDeferredTypes =
                            currentlyDeferredTypes or (animation.typeMask and animatingTypes)
                    }

                    override fun onProgress(
                        insets: WindowInsetsCompat,
                        runningAnimations: List<WindowInsetsAnimationCompat>
                    ): WindowInsetsCompat {
                        val runningAnimatingTypes = runningAnimations.fold(0) { acc, anim ->
                            acc or anim.typeMask
                        } and animatingTypes

                        if (runningAnimatingTypes == 0) {
                            // If we have no animating types which are running, return now.
                            return insets
                        }

                        // onProgress() is called when any of the running animations progress...

                        // First we get the insets which are potentially deferred
                        val animatedInsets = insets.getInsets(runningAnimatingTypes)
                        // Then we get the persistent inset types which are applied as
                        // padding during layout
                        val persistentInset = insets.getInsets(
                            persistentTypes.all and runningAnimatingTypes.inv()
                        )

                        // Now that we subtract the two insets, to calculate the difference.
                        // We also coerce the insets to be >= 0, to ensure that we don't use
                        // negative insets.
                        val diff = Insets.subtract(animatedInsets, persistentInset).let {
                            Insets.max(it, Insets.NONE)
                        }

                        // The resulting `diff` insets contain the values for us to apply as a translation
                        // to the view
                        val tx = (diff.left - diff.right).toFloat()
                        val ty = (diff.top - diff.bottom).toFloat()

                        view.translationX = tx
                        view.translationY = ty

                        for (v in animateSyncViews) {
                            v.translationX = tx
                            v.translationY = ty
                        }

                        return insets
                    }

                    override fun onEnd(animation: WindowInsetsAnimationCompat) {
                        if (currentlyDeferredTypes and animation.typeMask != 0) {
                            currentlyDeferredTypes =
                                currentlyDeferredTypes and animation.typeMask.inv()

                            // And finally dispatch the deferred insets to the view now.
                            // Ideally we would just call view.requestApplyInsets() and let
                            // the normal dispatch cycle happen, but this happens too late
                            // resulting in a visual flicker.
                            // Instead we manually re-dispatch the most recent WindowInsets
                            // to the view.
                            if (lastWindowInsets != null) {
                                ViewCompat.dispatchApplyWindowInsets(view, lastWindowInsets!!)
                            }
                        }

                        // Once the animation has ended, reset the translation values
                        view.translationX = 0f
                        view.translationY = 0f

                        for (v in animateSyncViews) {
                            v.translationX = 0f
                            v.translationY = 0f
                        }
                    }
                }
            )
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

        view.applyPadding(
            insets = insets,
            typesToApply = paddingTypes - currentlyDeferredTypes,
            initialPaddings = initialState.paddings,
            ignoreVisibility = ignoreVisibility
        )
        view.applyMargins(
            insets = insets,
            typesToApply = marginTypes - currentlyDeferredTypes,
            initialMargins = initialState.margins,
            ignoreVisibility = ignoreVisibility
        )
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
         * [Builder.padding], [Builder.margin] or other related functions.
         */
        const val CONSUME_AUTO = 2

        /**
         * Returns a instance of [Builder] used for creating an instance of [Insetter].
         */
        @JvmStatic
        fun builder(): Builder = Builder()

        internal const val TAG = "Insetter"

        @Suppress("NOTHING_TO_INLINE", "DeprecatedCallableAddReplaceWith", "unused", "UNUSED_PARAMETER")
        @Deprecated(
            level = DeprecationLevel.ERROR,
            message = "Use WindowCompat.setDecorFitsSystemWindows(window, false) to enable edge to edge mode",
        )
        inline fun setEdgeToEdgeSystemUiFlags(view: View, enabled: Boolean = true) {
            noImpl()
        }
    }
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

    fun plus(insetTypes: Int, @Sides sides: Int = Side.ALL) {
        if (sides and Side.LEFT != 0) left = left or insetTypes
        if (sides and Side.TOP != 0) top = top or insetTypes
        if (sides and Side.RIGHT != 0) right = right or insetTypes
        if (sides and Side.BOTTOM != 0) bottom = bottom or insetTypes
    }

    operator fun plus(other: SideApply): SideApply {
        if (other.isEmpty) return this

        val lhs = this
        return SideApply().apply {
            left = lhs.left or other.left
            top = lhs.top or other.top
            right = lhs.right or other.right
            bottom = lhs.bottom or other.bottom
        }
    }

    operator fun minus(other: SideApply): SideApply {
        if (isEmpty) return this
        if (other.isEmpty) return this

        val lhs = this
        return SideApply().apply {
            left = lhs.left and other.left.inv()
            top = lhs.top and other.top.inv()
            right = lhs.right and other.right.inv()
            bottom = lhs.bottom and other.bottom.inv()
        }
    }

    operator fun minus(type: Int): SideApply {
        if (isEmpty) return this
        if (type == 0) return this

        val lhs = this
        return SideApply().apply {
            left = lhs.left and type.inv()
            top = lhs.top and type.inv()
            right = lhs.right and type.inv()
            bottom = lhs.bottom and type.inv()
        }
    }
}

/**
 * Applies the window insets types specified in [typesToApply], using the source values
 * from [insets] as padding.
 */
private fun View.applyPadding(
    insets: WindowInsetsCompat,
    typesToApply: SideApply,
    initialPaddings: ViewDimensions,
    ignoreVisibility: Boolean,
) {
    // If there's no types to apply, nothing to do...
    if (typesToApply.isEmpty) return

    val paddingLeft = when (typesToApply.left) {
        Side.NONE -> paddingLeft
        else -> initialPaddings.left + insets.getInsets(typesToApply.left, ignoreVisibility).left
    }
    val paddingTop = when (typesToApply.top) {
        Side.NONE -> paddingTop
        else -> initialPaddings.top + insets.getInsets(typesToApply.top, ignoreVisibility).top
    }
    val paddingRight = when (typesToApply.right) {
        Side.NONE -> paddingRight
        else -> initialPaddings.right + insets.getInsets(typesToApply.right, ignoreVisibility).right
    }
    val paddingBottom = when (typesToApply.bottom) {
        Side.NONE -> paddingBottom
        else -> initialPaddings.bottom + insets.getInsets(typesToApply.bottom, ignoreVisibility).bottom
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
    typesToApply: SideApply,
    initialMargins: ViewDimensions,
    ignoreVisibility: Boolean,
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
        else -> initialMargins.left + insets.getInsets(typesToApply.left, ignoreVisibility).left
    }
    val marginTop = when (typesToApply.top) {
        Side.NONE -> lp.topMargin
        else -> initialMargins.top + insets.getInsets(typesToApply.top, ignoreVisibility).top
    }
    val marginRight = when (typesToApply.right) {
        Side.NONE -> lp.rightMargin
        else -> initialMargins.right + insets.getInsets(typesToApply.right, ignoreVisibility).right
    }
    val marginBottom = when (typesToApply.bottom) {
        Side.NONE -> lp.bottomMargin
        else -> initialMargins.bottom + insets.getInsets(typesToApply.bottom, ignoreVisibility).bottom
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
    applied: SideApply,
    ignoreVisibility: Boolean,
): WindowInsetsCompat.Builder {
    // Fast path. If this type wasn't applied at all, no need to do anything
    if (applied.all and type != type) return this

    // First we get the original insets for the type
    val insets = windowInsets.getInsets(type, ignoreVisibility)

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
 * Returns the insets of a specific set of windows causing insets, denoted by the
 * [typeMask] bit mask of [Type]s.
 *
 * @param typeMask Bit mask of [Type]s to query the insets for.
 * @param ignoreVisibility True to return the insets regardless of whether that type is currently
 * visible or not.
 * @see WindowInsetsCompat.getInsets
 * @see WindowInsetsCompat.getInsetsIgnoringVisibility
 */
private fun WindowInsetsCompat.getInsets(
    @Type.InsetsType typeMask: Int,
    ignoreVisibility: Boolean
): Insets {
    return if (ignoreVisibility) {
        getInsetsIgnoringVisibility(typeMask)
    } else {
        getInsets(typeMask)
    }
}
