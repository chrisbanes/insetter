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
import androidx.test.ext.junit.rules.ActivityScenarioRule
import dev.chrisbanes.insetter.testutils.assertLayoutMargin
import dev.chrisbanes.insetter.testutils.assertPadding
import dev.chrisbanes.insetter.testutils.dispatchInsets
import dev.chrisbanes.insetter.widgets.InsetterConstraintLayout
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
        assertSystemWindowInsetViewState(rect1)
    }

    @Test
    fun testSystemWindowInsetsWhichChange() {
        // Dispatch some initial insets
        val rect1 = Rect(10, 20, 30, 40)
        container.dispatchInsets(rect1)
        // ...and assert that the paddings change
        assertSystemWindowInsetViewState(rect1)

        // Now dispatch different insets
        val rect2 = Rect(11, 22, 33, 44)
        container.dispatchInsets(rect2)
        // ...and assert that the paddings change
        assertSystemWindowInsetViewState(rect2)
    }

    private fun assertSystemWindowInsetViewState(systemWindowInsets: Rect) {
        // -------------------------------------------
        // Assert the system window padding views
        // -------------------------------------------

        with(container.findViewById<View>(R.id.padding_system_window_left)) {
            assertPadding(left = systemWindowInsets.left)
        }
        with(container.findViewById<View>(R.id.padding_system_window_top)) {
            assertPadding(top = systemWindowInsets.top)
        }
        with(container.findViewById<View>(R.id.padding_system_window_right)) {
            assertPadding(right = systemWindowInsets.right)
        }
        with(container.findViewById<View>(R.id.padding_system_window_bottom)) {
            assertPadding(bottom = systemWindowInsets.bottom)
        }
        with(container.findViewById<View>(R.id.padding_system_window_all)) {
            assertPadding(systemWindowInsets)
        }
        with(container.findViewById<View>(R.id.padding_system_window_all_withpadding)) {
            val layoutPadding = resources.getDimensionPixelSize(R.dimen.padding)
            assertPadding(
                systemWindowInsets.left + layoutPadding,
                systemWindowInsets.top + layoutPadding,
                systemWindowInsets.right + layoutPadding,
                systemWindowInsets.bottom + layoutPadding
            )
        }

        // -------------------------------------------
        // Assert the system window margin views
        // -------------------------------------------

        with(container.findViewById<View>(R.id.margin_system_window_left)) {
            assertLayoutMargin(left = systemWindowInsets.left)
        }
        with(container.findViewById<View>(R.id.margin_system_window_top)) {
            assertLayoutMargin(top = systemWindowInsets.top)
        }
        with(container.findViewById<View>(R.id.margin_system_window_right)) {
            assertLayoutMargin(right = systemWindowInsets.right)
        }
        with(container.findViewById<View>(R.id.margin_system_window_bottom)) {
            assertLayoutMargin(bottom = systemWindowInsets.bottom)
        }
        with(container.findViewById<View>(R.id.margin_system_window_all)) {
            assertLayoutMargin(systemWindowInsets)
        }
        with(container.findViewById<View>(R.id.margin_system_window_all_withmargin)) {
            val layoutMargin = resources.getDimensionPixelSize(R.dimen.margin)
            assertLayoutMargin(
                systemWindowInsets.left + layoutMargin,
                systemWindowInsets.top + layoutMargin,
                systemWindowInsets.right + layoutMargin,
                systemWindowInsets.bottom + layoutMargin
            )
        }
    }
}