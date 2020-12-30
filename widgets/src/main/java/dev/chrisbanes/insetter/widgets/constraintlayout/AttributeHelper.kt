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

package dev.chrisbanes.insetter.widgets.constraintlayout

import android.view.Gravity
import dev.chrisbanes.insetter.Side.create

internal object AttributeHelper {
    // These values match the values in attrs.xml
    private const val LEFT = Gravity.LEFT
    private const val TOP = Gravity.TOP
    private const val RIGHT = Gravity.RIGHT
    private const val BOTTOM = Gravity.BOTTOM

    @JvmStatic
    fun flagToSides(value: Int): Int {
        // Fast path if the value is empty
        return if (value == 0) 0 else create(
            hasFlag(value, LEFT),
            hasFlag(value, TOP),
            hasFlag(value, RIGHT),
            hasFlag(value, BOTTOM)
        )
    }

    @JvmStatic
    private fun hasFlag(value: Int, flag: Int): Boolean {
        return value and flag == flag
    }
}
