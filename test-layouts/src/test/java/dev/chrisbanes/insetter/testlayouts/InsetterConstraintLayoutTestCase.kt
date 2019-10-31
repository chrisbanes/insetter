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

package dev.chrisbanes.insetter.testlayouts

import android.app.Activity
import android.graphics.Rect
import android.view.View
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.core.view.marginRight
import androidx.core.view.marginBottom
import androidx.test.ext.junit.rules.ActivityScenarioRule
import dev.chrisbanes.insetter.testutils.dispatchInsets
import dev.chrisbanes.insetter.widgets.InsetterConstraintLayout
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(minSdk = 23, maxSdk = 28)
class InsetterConstraintLayoutTestCase {
    @get:Rule
    val rule = ActivityScenarioRule(Activity::class.java)

    private lateinit var container: InsetterConstraintLayout

    @Before
    fun setup() {
        rule.scenario.onActivity { activity ->
            activity.setContentView(R.layout.insetter_cl)
            container = activity.findViewById(R.id.root)
        }
    }

    @Test
    fun testSystemWindowInsets() {
        // Dispatch some initial insets
        val rect1 = Rect(5, 7, 9, 13)
        container.dispatchInsets(rect1)
        // ...and assert that the paddings change
        assertViewState(rect1)
    }

    @Test
    fun testSystemWindowInsetsWhichChange() {
        // Dispatch some initial insets
        val rect1 = Rect(10, 20, 30, 40)
        container.dispatchInsets(rect1)
        // ...and assert that the paddings change
        assertViewState(rect1)

        // Now dispatch different insets
        val rect2 = Rect(11, 22, 33, 44)
        container.dispatchInsets(rect2)
        // ...and assert that the paddings change
        assertViewState(rect2)
    }

    private fun assertViewState(systemWindowInsets: Rect) {
        assertPaddings(systemWindowInsets)
        assertMargins(systemWindowInsets)
    }

    private fun assertPaddings(systemWindowInsets: Rect) {
        with(container.findViewById<View>(R.id.padding_system_window_left)) {
            assertEquals(systemWindowInsets.left, paddingLeft)
        }
        with(container.findViewById<View>(R.id.padding_system_window_top)) {
            assertEquals(systemWindowInsets.top, paddingTop)
        }
        with(container.findViewById<View>(R.id.padding_system_window_right)) {
            assertEquals(systemWindowInsets.right, paddingRight)
        }
        with(container.findViewById<View>(R.id.padding_system_window_bottom)) {
            assertEquals(systemWindowInsets.bottom, paddingBottom)
        }
        with(container.findViewById<View>(R.id.padding_system_window_all)) {
            assertEquals(systemWindowInsets.left, paddingLeft)
            assertEquals(systemWindowInsets.top, paddingTop)
            assertEquals(systemWindowInsets.right, paddingRight)
            assertEquals(systemWindowInsets.bottom, paddingBottom)
        }
        with(container.findViewById<View>(R.id.padding_system_window_all_withpadding)) {
            val widgetPadding = resources.getDimensionPixelSize(R.dimen.padding)
            assertEquals(systemWindowInsets.left + widgetPadding, paddingLeft)
            assertEquals(systemWindowInsets.top + widgetPadding, paddingTop)
            assertEquals(systemWindowInsets.right + widgetPadding, paddingRight)
            assertEquals(systemWindowInsets.bottom + widgetPadding, paddingBottom)
        }
    }

    private fun assertMargins(systemWindowInsets: Rect) {
        with(container.findViewById<View>(R.id.margin_system_window_left)) {
            assertEquals(systemWindowInsets.left, marginLeft)
        }
        with(container.findViewById<View>(R.id.margin_system_window_top)) {
            assertEquals(systemWindowInsets.top, marginTop)
        }
        with(container.findViewById<View>(R.id.margin_system_window_right)) {
            assertEquals(systemWindowInsets.right, marginRight)
        }
        with(container.findViewById<View>(R.id.margin_system_window_bottom)) {
            assertEquals(systemWindowInsets.bottom, marginBottom)
        }
        with(container.findViewById<View>(R.id.margin_system_window_all)) {
            assertEquals(systemWindowInsets.left, marginLeft)
            assertEquals(systemWindowInsets.top, marginTop)
            assertEquals(systemWindowInsets.right, marginRight)
            assertEquals(systemWindowInsets.bottom, marginBottom)
        }
        with(container.findViewById<View>(R.id.margin_system_window_all_withmargin)) {
            val layoutMargin = resources.getDimensionPixelSize(R.dimen.margin)
            assertEquals(systemWindowInsets.left + layoutMargin, marginLeft)
            assertEquals(systemWindowInsets.top + layoutMargin, marginTop)
            assertEquals(systemWindowInsets.right + layoutMargin, marginRight)
            assertEquals(systemWindowInsets.bottom + layoutMargin, marginBottom)
        }
    }
}