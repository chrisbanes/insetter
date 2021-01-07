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
import androidx.databinding.BindingAdapter
import dev.chrisbanes.insetter.Insetter.Companion.setEdgeToEdgeSystemUiFlags

private const val TAG = "Insetter"

@BindingAdapter(
    value = [
        "consumeSystemWindowInsets",
        "paddingLeftSystemWindowInsets",
        "paddingTopSystemWindowInsets",
        "paddingRightSystemWindowInsets",
        "paddingBottomSystemWindowInsets",
        "paddingLeftSystemGestureInsets",
        "paddingTopSystemGestureInsets",
        "paddingRightSystemGestureInsets",
        "paddingBottomSystemGestureInsets",
        "layout_marginLeftSystemWindowInsets",
        "layout_marginTopSystemWindowInsets",
        "layout_marginRightSystemWindowInsets",
        "layout_marginBottomSystemWindowInsets",
        "layout_marginLeftSystemGestureInsets",
        "layout_marginTopSystemGestureInsets",
        "layout_marginRightSystemGestureInsets",
        "layout_marginBottomSystemGestureInsets"
    ],
    requireAll = false
)
fun applyInsetsFromBooleans(
    v: View,
    consumeSystemWindowInsets: Boolean,
    padSystemWindowLeft: Boolean,
    padSystemWindowTop: Boolean,
    padSystemWindowRight: Boolean,
    padSystemWindowBottom: Boolean,
    padSystemGestureLeft: Boolean,
    padSystemGestureTop: Boolean,
    padSystemGestureRight: Boolean,
    padSystemGestureBottom: Boolean,
    marginSystemWindowLeft: Boolean,
    marginSystemWindowTop: Boolean,
    marginSystemWindowRight: Boolean,
    marginSystemWindowBottom: Boolean,
    marginSystemGestureLeft: Boolean,
    marginSystemGestureTop: Boolean,
    marginSystemGestureRight: Boolean,
    marginSystemGestureBottom: Boolean
) {
    Insetter.builder()
        .padding(
            windowInsetTypesOf(ime = true, statusBars = true, navigationBars = true),
            Side.create(
                padSystemWindowLeft,
                padSystemWindowTop,
                padSystemWindowRight,
                padSystemWindowBottom
            )
        )
        .margin(
            windowInsetTypesOf(ime = true, statusBars = true, navigationBars = true),
            Side.create(
                marginSystemWindowLeft,
                marginSystemWindowTop,
                marginSystemWindowRight,
                marginSystemWindowBottom
            )
        )
        .padding(
            windowInsetTypesOf(systemGestures = true),
            Side.create(
                padSystemGestureLeft,
                padSystemGestureTop,
                padSystemGestureRight,
                padSystemGestureBottom
            )
        )
        .margin(
            windowInsetTypesOf(systemGestures = true),
            Side.create(
                marginSystemGestureLeft,
                marginSystemGestureTop,
                marginSystemGestureRight,
                marginSystemGestureBottom
            )
        )
        .consume(if (consumeSystemWindowInsets) Insetter.CONSUME_ALL else Insetter.CONSUME_NONE)
        .applyToView(v)
}

@BindingAdapter("layout_edgeToEdge")
@Deprecated(
    """The layout_edgeToEdge attribute is deprecated.
    See Insetter.setEdgeToEdgeSystemUiFlags for more information."""
)
fun setEdgeToEdgeFlags(view: View, enabled: Boolean) {
    if (Build.VERSION.SDK_INT >= 16) {
        @Suppress("DEPRECATION")
        setEdgeToEdgeSystemUiFlags(view, enabled)
    } else {
        Log.i(TAG, "The layout_edgeToEdge attribute only works on API 16+")
    }
}
