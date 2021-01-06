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

@file:Suppress("NOTHING_TO_INLINE")

package dev.chrisbanes.insetter

import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsCompat

/**
 * Helper function for applying an insets listener which is aware of the [View]'s initial
 * padding and margin state.
 */
@Deprecated(
    "Use Insetter.builder() directly",
    ReplaceWith(
        "Insetter.builder().setOnApplyInsetsListener(f).applyToView(this)",
        "dev.chrisbanes.insetter.Insetter"
    )
)
fun View.doOnApplyWindowInsets(
    f: (view: View, insets: WindowInsetsCompat, initialState: ViewState) -> Unit
) = Insetter.builder().setOnApplyInsetsListener(f).applyToView(this)

/**
 * Set this view's system-ui visibility, with the flags required to be laid out 'edge-to'edge.
 *
 * @param enabled true if the view should request to be laid out 'edge-to-edge', false if not
 *
 * @see View.setSystemUiVisibility
 * @see Insetter.setEdgeToEdgeSystemUiFlags
 */
@RequiresApi(16)
@Suppress("DEPRECATION", "DeprecatedCallableAddReplaceWith")
@Deprecated("Use WindowCompat.setDecorFitsSystemWindows() instead")
inline fun View.setEdgeToEdgeSystemUiFlags(enabled: Boolean = true) {
    Insetter.setEdgeToEdgeSystemUiFlags(this, enabled)
}

/**
 * Apply the system window insets as the padding of this [View].
 *
 * @param left apply left indent if true
 * @param top apply in upper indent if true
 * @param right apply in the right indent if true
 * @param bottom apply in the bottom indent if true
 * @param consume consume the system window insets if true
 */
inline fun View.applySystemWindowInsetsToPadding(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false,
    consume: Boolean = false
) = Insetter.builder()
    .applyAsPadding(
        windowInsetTypesOf(ime = true, statusBars = true, navigationBars = true),
        Side.create(left, top, right, bottom)
    )
    .consume(if (consume) Insetter.CONSUME_ALL else Insetter.CONSUME_NONE)
    .applyToView(this)

/**
 * Apply the system window insets to the margins on this [View].
 *
 * @param left apply left indent if true
 * @param top apply in upper indent if true
 * @param right apply in the right indent if true
 * @param bottom apply in the bottom indent if true
 * @param consume consume the system window insets if true
 */
inline fun View.applySystemWindowInsetsToMargin(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false,
    consume: Boolean = false
) = Insetter.builder()
    .applyAsMargin(
        windowInsetTypesOf(ime = true, statusBars = true, navigationBars = true),
        Side.create(left, top, right, bottom)
    )
    .consume(if (consume) Insetter.CONSUME_ALL else Insetter.CONSUME_NONE)
    .applyToView(this)

/**
 * Apply system gesture insets to the padding of this [View].
 *
 * @param left apply left indent if true
 * @param top apply in upper indent if true
 * @param right apply in the right indent if true
 * @param bottom apply in the bottom indent if true
 * @param consume consume the system window insets if true
 */
inline fun View.applySystemGestureInsetsToPadding(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false,
    consume: Boolean = false
) = Insetter.builder()
    .applyAsPadding(
        windowInsetTypesOf(systemGestures = true),
        Side.create(left, top, right, bottom)
    )
    .consume(if (consume) Insetter.CONSUME_ALL else Insetter.CONSUME_NONE)
    .applyToView(this)

/**
 * Apply system gesture insets to the margins on this [View].
 *
 * @param left apply left indent if true
 * @param top apply in upper indent if true
 * @param right apply in the right indent if true
 * @param bottom apply in the bottom indent if true
 * @param consume consume the system window insets if true
 */
inline fun View.applySystemGestureInsetsToMargin(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false,
    consume: Boolean = false
) = Insetter.builder()
    .applyAsMargin(
        windowInsetTypesOf(systemGestures = true),
        Side.create(left, top, right, bottom)
    )
    .consume(if (consume) Insetter.CONSUME_ALL else Insetter.CONSUME_NONE)
    .applyToView(this)
