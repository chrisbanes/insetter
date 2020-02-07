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
fun View.setEdgeToEdgeSystemUiFlags(enabled: Boolean) = Insetter.setEdgeToEdgeSystemUiFlags(this, enabled)

/**
 * Applies padding or margin to the bottom of the view when the keyboard is displayed
 * @param marginBottom whether to adjust the view's bottom margin
 * @param paddingBottom whether to adjust the view's bottom padding
 */
fun View.applyInsetsWhenKeyboardDisplayed(marginBottom: Boolean = false,
                                          paddingBottom: Boolean = false) {
    Insetter.applyInsetsWhenKeyboardDisplayed(this, marginBottom, paddingBottom)
}