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

package dev.chrisbanes.insetter.testutils

import android.graphics.Insets
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import org.junit.Assert.assertEquals

fun createInsets(
    systemWindowInsets: Rect? = null,
    systemGestureInsets: Rect? = null
): WindowInsetsCompat = when {
    Build.VERSION.SDK_INT >= 29 -> {
        // On API 29+, we can use the new WindowInsets.Builder
        WindowInsets.Builder().apply {
            if (systemWindowInsets != null) {
                setSystemWindowInsets(Insets.of(systemWindowInsets))
            }
            if (systemGestureInsets != null) {
                setSystemGestureInsets(Insets.of(systemGestureInsets))
            }
        }.build().toWindowInsetsCompat()
    }
    Build.VERSION.SDK_INT >= 20 -> {
        // Other we need to use reflection 🤮
        val constructor = WindowInsets::class.java.getConstructor(Rect::class.java)
        constructor.isAccessible = true
        constructor.newInstance(systemWindowInsets ?: Rect()).toWindowInsetsCompat()
    }
    else -> WindowInsetsCompat(null)
}

fun View.dispatchInsets(
    systemWindowInsets: Rect? = null,
    systemGestureInsets: Rect? = null
): WindowInsetsCompat {
    val insets = createInsets(systemWindowInsets, systemGestureInsets)
    ViewCompat.dispatchApplyWindowInsets(this, insets)
    return insets
}

fun View.dispatchInsets(f: ((WindowInsetsCompat) -> WindowInsetsCompat)): WindowInsetsCompat {
    val insets = f(createInsets())
    ViewCompat.dispatchApplyWindowInsets(this, insets)
    return insets
}

fun View.assertPadding(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    assertEquals(left, paddingLeft)
    assertEquals(top, paddingTop)
    assertEquals(right, paddingRight)
    assertEquals(bottom, paddingBottom)
}

fun View.assertPadding(rect: Rect) = assertPadding(rect.left, rect.top, rect.right, rect.bottom)

fun View.assertLayoutMargin(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    assertEquals(left, marginLeft)
    assertEquals(top, marginTop)
    assertEquals(right, marginRight)
    assertEquals(bottom, marginBottom)
}

fun View.assertLayoutMargin(rect: Rect) = assertLayoutMargin(rect.left, rect.top, rect.right, rect.bottom)

@RequiresApi(20)
fun WindowInsets.toWindowInsetsCompat() = WindowInsetsCompat.toWindowInsetsCompat(this)