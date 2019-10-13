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
import androidx.core.view.WindowInsetsCompat

inline fun View.doOnApplyWindowInsets(
    crossinline f: (
        View,
        insets: WindowInsetsCompat,
        initialPadding: ViewDimensions,
        initialMargin: ViewDimensions
    ) -> Unit
) {
    InsetUtils.setOnApplyInsetsListener(this, object : OnApplyInsetsListener() {
        override fun onInsetsListener(
            insets: WindowInsetsCompat,
            initialPadding: ViewDimensions,
            initialMargins: ViewDimensions
        ) = f(this@doOnApplyWindowInsets, insets, initialPadding, initialMargins)
    })
}

fun View.requestApplyInsetsWhenAttached() {
    InsetUtils.requestApplyInsetsWhenAttached(this)
}