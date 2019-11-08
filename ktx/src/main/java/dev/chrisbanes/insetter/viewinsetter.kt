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
import java.util.EnumSet

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

/**
 * TODO
 *
 * @param insets
 * @param initialState
 * @param paddingSystemWindowInsets
 * @param marginSystemWindowInsets
 * @param paddingSystemGestureInsets
 * @param marginSystemGestureInsets
 */
inline fun View.applyInsets(
    insets: WindowInsetsCompat,
    initialState: ViewState = ViewState.EMPTY,
    paddingSystemWindowInsets: EnumSet<InsetDimension>? = null,
    marginSystemWindowInsets: EnumSet<InsetDimension>? = null,
    paddingSystemGestureInsets: EnumSet<InsetDimension>? = null,
    marginSystemGestureInsets: EnumSet<InsetDimension>? = null
) {
    Insetter.applyInsetsToView(
            this,
            insets,
            initialState,
            paddingSystemWindowInsets,
            marginSystemWindowInsets,
            paddingSystemGestureInsets,
            marginSystemGestureInsets
        )
}

inline fun View.requestApplyInsetsWhenAttached() = Insetter.requestApplyInsetsWhenAttached(this)

/**
 * Set this view's system-ui visibility, with the flags required to be laid out 'edge-to'edge.
 *
 * @param enabled true if the view should request to be laid out 'edge-to-edge', false if not
 *
 * @see View.setSystemUiVisibility
 * @see Insetter.setEdgeToEdgeSystemUiFlags
 */
@RequiresApi(16)
inline fun View.setEdgeToEdgeSystemUiFlags(enabled: Boolean) = Insetter.setEdgeToEdgeSystemUiFlags(this, enabled)
