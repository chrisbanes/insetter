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

@file:Suppress("unused", "UNUSED_PARAMETER", "NOTHING_TO_INLINE")

package dev.chrisbanes.insetter

import android.view.View

@Deprecated(
    level = DeprecationLevel.ERROR,
    message = "Use applyInsetter instead",
    replaceWith = ReplaceWith(
        "applyInsetter { type(statusBars = top, navigationBars = bottom) { padding() } }",
        "dev.chrisbanes.insetter.applyInsetter"
    )
)
inline fun View.applySystemWindowInsetsToPadding(top: Boolean = false, bottom: Boolean = false) {
    noImpl()
}

@Deprecated(
    level = DeprecationLevel.ERROR,
    message = "Use applyInsetter instead",
    replaceWith = ReplaceWith(
        "applyInsetter { type(statusBars = top, navigationBars = bottom) { padding() }; consume(consume) }",
        "dev.chrisbanes.insetter.applyInsetter"
    )
)
inline fun View.applySystemWindowInsetsToPadding(top: Boolean = false, bottom: Boolean = false, consume: Boolean) {
    noImpl()
}

@Deprecated(
    level = DeprecationLevel.ERROR,
    message = "Use applyInsetter instead",
    replaceWith = ReplaceWith(
        "applyInsetter { type(statusBars = top, navigationBars = bottom) { margin() } }",
        "dev.chrisbanes.insetter.applyInsetter"
    )
)
inline fun View.applySystemWindowInsetsToMargin(top: Boolean = false, bottom: Boolean = false) {
    noImpl()
}

@Deprecated(
    level = DeprecationLevel.ERROR,
    message = "Use applyInsetter instead",
    replaceWith = ReplaceWith(
        "applyInsetter { type(statusBars = top, navigationBars = bottom) { margin() }; consume(consume) }",
        "dev.chrisbanes.insetter.applyInsetter"
    )
)
inline fun View.applySystemWindowInsetsToMargin(top: Boolean = false, bottom: Boolean = false, consume: Boolean) {
    noImpl()
}

@PublishedApi
internal inline fun noImpl(): Nothing =
    throw UnsupportedOperationException("Not implemented, should not be called")
