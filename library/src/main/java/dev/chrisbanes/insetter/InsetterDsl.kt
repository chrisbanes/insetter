/*
 * Copyright 2021 Google LLC
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

@file:Suppress("unused")

package dev.chrisbanes.insetter

import android.view.View
import androidx.core.view.WindowInsetsCompat

/**
 * Allows easy building and applying of [WindowInsetsCompat] to this [View].
 *
 * As an example, to apply the [navigation bar][WindowInsetsCompat.Type.navigationBars] insets
 * as padding to this view, you would call:
 *
 * ```
 * view.applyInsetter {
 *     type(navigationBars = true) {
 *         padding()
 *     }
 * }
 * ```
 *
 * ### Applying different types
 *
 * If we want to apply different insets type, we can do that too:
 *
 * ```
 * view.applyInsetter {
 *     // Apply the navigation bar insets as padding
 *     type(navigationBars = true) {
 *         padding()
 *     }
 *     // Apply the status bar insets as margin
 *     type(statusBars = true) {
 *         margin()
 *     }
 * }
 * ```
 *
 * Alternatively, if we want to apply multiple types as padding/margin, you can do the following:
 *
 * ```
 * view.applyInsetter {
 *     // Apply the navigation bar and status bars insets as padding
 *     type(navigationBars = true, statusBars = true) {
 *         padding()
 *     }
 * }
 * ```
 *
 * ### Window insets animations
 *
 * You can also react to window inset animations by passing true for the
 * `animated` parameter on [InsetterApplyTypeDsl.padding] and [InsetterApplyTypeDsl.margin]:
 *
 * ```
 * view.applyInsetter {
 *     type(navigationBars = true, ime = true) {
 *         padding(animated = true)
 *     }
 * }
 * ```
 *
 * You may also wish to sync the resulting translation to other views, via
 * [InsetterDsl.syncTranslationTo].
 *
 * ### Different dimensions
 *
 * Some times you need to apply sides differently, here we're applying left + right sides to margin,
 * and then the bottom as padding:
 *
 * ```
 * view.applyInsetter {
 *     // Apply the navigation bar insets...
 *     type(navigationBars = true) {
 *         // Add the left/right to the margin
 *         margin(left = true, right = true)
 *         // Add the bottom to padding
 *         padding(bottom = true)
 *     }
 * }
 * ```
 */
fun View.applyInsetter(build: InsetterDsl.() -> Unit): Insetter {
    return InsetterDsl().apply(build).builder.applyToView(this)
}

@DslMarker
annotation class InsetterDslMarker

/**
 * Class used in [View.applyInsetter].
 */
@InsetterDslMarker
class InsetterDsl internal constructor() {
    internal var builder = Insetter.builder()

    /**
     * Set how the given [WindowInsetsCompat.Type]s are applied to the view.
     *
     * All of the common types are provided as boolean parameters. Setting multiple types
     * to `true` will result in the combination of those insets to be applied.
     *
     * @throws IllegalArgumentException if all types are set to `false`.
     *
     * @param ime True to apply the [WindowInsetsCompat.Type.ime] insets.
     * @param navigationBars True to apply the [WindowInsetsCompat.Type.navigationBars] insets.
     * @param statusBars True to apply the [WindowInsetsCompat.Type.statusBars] insets.
     * @param systemGestures True to apply the [WindowInsetsCompat.Type.systemGestures] insets.
     * @param mandatorySystemGestures True to apply the [WindowInsetsCompat.Type.mandatorySystemGestures] insets.
     * @param displayCutout True to apply the [WindowInsetsCompat.Type.displayCutout] insets.
     * @param captionBar True to apply the [WindowInsetsCompat.Type.captionBar] insets.
     * @param tappableElement True to apply the [WindowInsetsCompat.Type.tappableElement] insets.
     */
    fun type(
        ime: Boolean = false,
        navigationBars: Boolean = false,
        statusBars: Boolean = false,
        systemGestures: Boolean = false,
        mandatorySystemGestures: Boolean = false,
        displayCutout: Boolean = false,
        captionBar: Boolean = false,
        tappableElement: Boolean = false,
        systemBars: Boolean = false,
        f: InsetterApplyTypeDsl.() -> Unit,
    ) {
        val type = windowInsetTypesOf(
            ime,
            navigationBars,
            statusBars,
            systemGestures,
            mandatorySystemGestures,
            displayCutout,
            captionBar,
            tappableElement,
            systemBars,
        )
        type(type, f)
    }

    /**
     * Set how the given bitmask of [WindowInsetsCompat.Type]s in [type] are applied to the view.
     *
     * @throws IllegalArgumentException if [type] is empty, by passing in `0`.
     *
     * @param type Bit mask of [WindowInsetsCompat.Type]s apply.
     */
    fun type(type: Int, f: InsetterApplyTypeDsl.() -> Unit,) {
        require(type != 0) { "A type is required" }
        builder = InsetterApplyTypeDsl(type, builder).apply(f).builder
    }

    /**
     * @param consume whether the window insets should be consumed.
     * @see Insetter.Builder.consume
     */
    fun consume(consume: Boolean) {
        builder = builder.consume(if (consume) Insetter.CONSUME_ALL else Insetter.CONSUME_NONE)
    }

    /**
     * @param consume how the window insets should be consumed.
     * @see Insetter.Builder.consume
     * @see Insetter.ConsumeOptions
     */
    fun consume(@Insetter.ConsumeOptions consume: Int) {
        builder = builder.consume(consume)
    }

    /**
     * @param ignoreVisibility true to return the insets regardless of whether the given type is
     * currently visible or not.
     * @see Insetter.Builder.ignoreVisibility
     */
    fun ignoreVisibility(ignoreVisibility: Boolean) {
        builder = builder.ignoreVisibility(ignoreVisibility)
    }

    /**
     * When reacting to window insets animations it is often useful to apply the same
     * animated translation X and Y to other views. The views provided to this function
     * will have their [View.getTranslationX] & [View.getTranslationY] set to the same values
     * which are set to whatever view this [Insetter] is applied to.
     */
    fun syncTranslationTo(vararg views: View) {
        builder = builder.syncTranslationTo(*views)
    }
}

/**
 * Class used in [View.applyInsetter].
 */
@InsetterDslMarker
class InsetterApplyTypeDsl internal constructor(
    private val type: Int,
    internal var builder: Insetter.Builder,
) {
    /**
     * Add the [WindowInsetsCompat.Type] to all padding dimensions.
     *
     * @param animated Whether we should animate the padding whilst a window insets animation
     * with the type is ongoing.
     */
    fun padding(
        animated: Boolean = false
    ) {
        padding(
            horizontal = true,
            vertical = true,
            animated = animated,
        )
    }

    /**
     * Add the [WindowInsetsCompat.Type] to the given padding dimensions.
     *
     * @param left Add the left value of the insets to the left padding.
     * @param top Add the left value of the insets to the top padding.
     * @param right Add the left value of the insets to the right padding.
     * @param bottom Add the left value of the insets to the bottom padding.
     * @param horizontal Add both the left and right values.
     * @param vertical Add both the top and bottom values.
     * @param animated Whether we should animate the padding whilst a window insets animation
     * with the type is ongoing.
     */
    fun padding(
        left: Boolean = false,
        top: Boolean = false,
        right: Boolean = false,
        bottom: Boolean = false,
        horizontal: Boolean = false,
        vertical: Boolean = false,
        animated: Boolean = false,
    ) {
        builder = builder.padding(
            insetType = type,
            sides = sidesOf(left, top, right, bottom, horizontal, vertical),
            animated = animated,
        )
    }

    /**
     * Add the [WindowInsetsCompat.Type] to all margin dimensions.
     *
     * @param animated Whether we should animate the margin whilst a window insets animation
     * with the type is ongoing.
     */
    fun margin(
        animated: Boolean = false
    ) {
        margin(
            horizontal = true,
            vertical = true,
            animated = animated,
        )
    }

    /**
     * Add the [WindowInsetsCompat.Type] to the given margin dimensions.
     *
     * @param left Add the left value of the insets to the left padding.
     * @param top Add the left value of the insets to the top padding.
     * @param right Add the left value of the insets to the right padding.
     * @param bottom Add the left value of the insets to the bottom padding.
     * @param horizontal Add both the left and right values.
     * @param vertical Add both the top and bottom values.
     * @param animated Whether we should animate the margin whilst a window insets animation
     * with the type is ongoing.
     */
    fun margin(
        left: Boolean = false,
        top: Boolean = false,
        right: Boolean = false,
        bottom: Boolean = false,
        horizontal: Boolean = false,
        vertical: Boolean = false,
        animated: Boolean = false,
    ) {
        builder = builder.margin(
            insetType = type,
            sides = sidesOf(left, top, right, bottom, horizontal, vertical),
            animated = animated,
        )
    }
}
