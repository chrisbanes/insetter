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

/**
 * Listener for applying window insets on a view in a custom way.
 *
 * This class is similar to to [androidx.core.view.OnApplyWindowInsetsListener] but
 * contains a third parameter on [onApplyInsets]. It is
 * designed to be used in conjunction with [Insetter.Builder.setOnApplyInsetsListener].
 */
fun interface OnApplyInsetsListener {
    /**
     * When [set][Insetter.Builder.setOnApplyInsetsListener] on a View,
     * this listener method will be called instead of the view's own `onApplyWindowInsets`
     * method.
     *
     * @param view The view applying window insets
     * @param insets The insets to apply
     * @param initialState A snapshot of the view's padding/margin state when this listener was set.
     */
    fun onApplyInsets(
        view: View,
        insets: WindowInsetsCompat,
        initialState: ViewState
    )
}
