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

package dev.chrisbanes.insetter

import android.view.ViewGroup
import androidx.annotation.Px

internal fun ViewGroup.MarginLayoutParams.updateMargins(
    @Px left: Int = leftMargin,
    @Px top: Int = topMargin,
    @Px right: Int = rightMargin,
    @Px bottom: Int = bottomMargin
): Boolean {
    if (left != leftMargin || top != topMargin || right != rightMargin || bottom != bottomMargin) {
        setMargins(left, top, right, bottom)
        return true
    }
    return false
}
