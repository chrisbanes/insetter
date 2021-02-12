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

/**
 * Class which contains values and functions to represents a side/dimension.
 */
object Side {
    /**
     * Value which represents an empty value of no sides.
     */
    const val NONE = 0

    /**
     * Value which represents the left side.
     */
    const val LEFT = 1 shl 0

    /**
     * Value which represents the top side.
     */
    const val TOP = 1 shl 1

    /**
     * Value which represents the right side.
     */
    const val RIGHT = 1 shl 2

    /**
     * Value which represents the bottom side.
     */
    const val BOTTOM = 1 shl 3

    /**
     * Value which represents all sides.
     */
    const val ALL = LEFT or TOP or RIGHT or BOTTOM

    /**
     * Creates a bitmask of [Side] values. Kotlin users may wish to use [sidesOf] directly instead.
     *
     * @param left provide true if the left dimension should be included.
     * @param top provide true if the top dimension should be included.
     * @param right provide true if the right dimension should be included.
     * @param bottom provide true if the bottom dimension should be included.
     */
    @Sides
    @JvmStatic
    fun create(
        left: Boolean,
        top: Boolean,
        right: Boolean,
        bottom: Boolean
    ): Int = sidesOf(left, top, right, bottom)

    /**
     * Creates a bitmask of [Side] values. Kotlin users may wish to use [sidesOf] directly instead.
     *
     * @param left provide true if the left dimension should be included.
     * @param top provide true if the top dimension should be included.
     * @param right provide true if the right dimension should be included.
     * @param bottom provide true if the bottom dimension should be included.
     * @param horizontal provide true if both the left and right dimensions should be included.
     * Passing true will override the values set for [left] and [right].
     * @param vertical provide true if both the top and bottom dimensions should be included.
     * Passing true will override the values set for [top] and [bottom].
     */
    @Sides
    @JvmStatic
    fun create(
        left: Boolean,
        top: Boolean,
        right: Boolean,
        bottom: Boolean,
        horizontal: Boolean,
        vertical: Boolean,
    ): Int = sidesOf(left, top, right, bottom, horizontal, vertical)
}

/**
 * Creates a bitmask of [Side] values.
 *
 * @param left provide true if the left dimension should be included.
 * @param top provide true if the top dimension should be included.
 * @param right provide true if the right dimension should be included.
 * @param bottom provide true if the bottom dimension should be included.
 * @param horizontal provide true if both the left and right dimensions should be included.
 * Passing true will override the values set for [left] and [right].
 * @param vertical provide true if both the top and bottom dimensions should be included.
 * Passing true will override the values set for [top] and [bottom].
 */
@Sides
fun sidesOf(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false,
    horizontal: Boolean = false,
    vertical: Boolean = false,
): Int {
    return Side.NONE or
        (if (left || horizontal) Side.LEFT else 0) or
        (if (top || vertical) Side.TOP else 0) or
        (if (right || horizontal) Side.RIGHT else 0) or
        (if (bottom || vertical) Side.BOTTOM else 0)
}
