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

import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsCompat

inline fun View.doOnApplyWindowInsets(
    crossinline f: (
        view: View,
        insets: WindowInsetsCompat,
        initialState: ViewState
    ) -> Unit
) {
    Insetter.setOnApplyInsetsListener(this) { view, insets, initialState ->
        f(view, insets, initialState)
    }
}

fun View.requestApplyInsetsWhenAttached() {
    Insetter.requestApplyInsetsWhenAttached(this)
}

/**
 * Set this view's system-ui visibility, with the flags required to be laid out 'edge-to'edge.
 *
 * @param enabled true if the view should request to be laid out 'edge-to-edge', false if not
 *
 * @see View.setSystemUiVisibility
 * @see Insetter.setEdgeToEdgeSystemUiFlags
 */
@RequiresApi(16)
fun View.setEdgeToEdgeSystemUiFlags(enabled: Boolean = true) =
    Insetter.setEdgeToEdgeSystemUiFlags(this, enabled)

/**
 * Apply system window insets to padding
 *
 * @param left apply left indent if true
 * @param top apply in upper indent if true
 * @param right apply in the right indent if true
 * @param bottom apply in the bottom indent if true
 * */
fun View.applySystemWindowInsetsToPadding(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
) {

    doOnApplyWindowInsets { view, insets, initialViewState ->
        Insetter.applyInsetsToView(
            view,
            insets,
            initialViewState,
            Insetter.generateEnumSet(left, top, right, bottom),
            null,
            null,
            null
        )

    }
}

/**
 * Apply system window insets to margin
 *
 * @param left apply left indent if true
 * @param top apply in upper indent if true
 * @param right apply in the right indent if true
 * @param bottom apply in the bottom indent if true
 * */
fun View.applySystemWindowInsetsToMargin(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
) {

    doOnApplyWindowInsets { view, insets, initialViewState ->
        Insetter.applyInsetsToView(
            view,
            insets,
            initialViewState,
            null,
            Insetter.generateEnumSet(left, top, right, bottom),
            null,
            null
        )

    }
}

/**
 * Apply system gesture insets to padding
 *
 * @param left apply left indent if true
 * @param top apply in upper indent if true
 * @param right apply in the right indent if true
 * @param bottom apply in the bottom indent if true
 * */
fun View.applySystemGestureInsetsToPadding(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
) {

    doOnApplyWindowInsets { view, insets, initialViewState ->
        Insetter.applyInsetsToView(
            view,
            insets,
            initialViewState,
            null,
            null,
            Insetter.generateEnumSet(left, top, right, bottom),
            null
        )

    }
}

/**
 * Apply system gesture insets to margin
 *
 * @param left apply left indent if true
 * @param top apply in upper indent if true
 * @param right apply in the right indent if true
 * @param bottom apply in the bottom indent if true
 * */
fun View.applySystemGestureInsetsToMargin(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
) {

    doOnApplyWindowInsets { view, insets, initialViewState ->
        Insetter.applyInsetsToView(
            view,
            insets,
            initialViewState,
            null,
            null,
            null,
            Insetter.generateEnumSet(left, top, right, bottom)
        )

    }
}
