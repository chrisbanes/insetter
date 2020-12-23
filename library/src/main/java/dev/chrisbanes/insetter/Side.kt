/*
 * Copyright 2020 Google LLC
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

object Side {
    const val NONE = 0
    const val LEFT = 1
    const val TOP = 1 shl 1
    const val RIGHT = 1 shl 2
    const val BOTTOM = 1 shl 3
    const val ALL = LEFT or TOP or RIGHT or BOTTOM

    @Sides
    @JvmStatic
    fun create(
        left: Boolean,
        top: Boolean,
        right: Boolean,
        bottom: Boolean
    ): Int = (if (left) LEFT else 0) or
        (if (top) TOP else 0) or
        (if (right) RIGHT else 0) or
        (if (bottom) BOTTOM else 0)

    @Suppress("NOTHING_TO_INLINE")
    internal inline fun hasSide(sides: Int, @Sides flag: Int): Boolean {
        return sides and flag == flag
    }
}
