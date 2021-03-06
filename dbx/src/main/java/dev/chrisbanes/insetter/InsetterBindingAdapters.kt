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
import androidx.databinding.BindingAdapter

@BindingAdapter(
    value = [
        "consumeWindowInsets",
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
    consumeWindowInsets: Boolean,
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
            sidesOf(
                padSystemWindowLeft,
                padSystemWindowTop,
                padSystemWindowRight,
                padSystemWindowBottom
            )
        )
        .margin(
            windowInsetTypesOf(ime = true, statusBars = true, navigationBars = true),
            sidesOf(
                marginSystemWindowLeft,
                marginSystemWindowTop,
                marginSystemWindowRight,
                marginSystemWindowBottom
            )
        )
        .padding(
            windowInsetTypesOf(systemGestures = true),
            sidesOf(
                padSystemGestureLeft,
                padSystemGestureTop,
                padSystemGestureRight,
                padSystemGestureBottom
            )
        )
        .margin(
            windowInsetTypesOf(systemGestures = true),
            sidesOf(
                marginSystemGestureLeft,
                marginSystemGestureTop,
                marginSystemGestureRight,
                marginSystemGestureBottom
            )
        )
        .consume(if (consumeWindowInsets) Insetter.CONSUME_ALL else Insetter.CONSUME_NONE)
        .applyToView(v)
}
