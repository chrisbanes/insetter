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

package dev.chrisbanes.insetter.widgets

import android.app.Activity
import android.graphics.Rect
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowInsetsCompat
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.SdkSuppress
import dev.chrisbanes.insetter.testutils.assertLayoutMargin
import dev.chrisbanes.insetter.testutils.assertPadding
import dev.chrisbanes.insetter.testutils.dispatchInsets
import dev.chrisbanes.insetter.widgets.test.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@SdkSuppress(minSdkVersion = 20)
class InsetsConstraintLayoutTestCase {
    @get:Rule
    val rule = ActivityScenarioRule(Activity::class.java)

    private lateinit var container: ConstraintLayout

    @Before
    fun setup() {
        rule.scenario.onActivity { activity ->
            activity.setContentView(R.layout.insetter_cl)
            container = activity.findViewById(R.id.root)
        }
    }

    @Test
    @UiThreadTest
    fun systemWindowInsets_singlePass() {
        // Dispatch some initial insets
        val insets = container.dispatchInsets(systemWindowInsets = Rect(5, 7, 9, 13))
        // ...and assert that the child view state changes
        assertChildViewState(insets)
    }

    @Test
    @UiThreadTest
    fun systemWindowInsets_multiPass() {
        // Dispatch some initial insets
        val insets = container.dispatchInsets(systemWindowInsets = Rect(10, 20, 30, 40))
        // ...and assert that the child view state changes
        assertChildViewState(insets)

        // Now dispatch different insets
        val insets2 = container.dispatchInsets(systemWindowInsets = Rect(11, 22, 33, 44))
        // ...and assert that the child view state changes
        assertChildViewState(insets2)
    }

    @Test
    @UiThreadTest
    fun systemGestureInsets_singlePass() {
        // Dispatch some initial insets
        val insets = container.dispatchInsets(systemGestureInsets = Rect(5, 7, 9, 13))
        // ...and assert that the child view state changes
        assertChildViewState(insets)
    }

    @Test
    @UiThreadTest
    fun systemGestureInsets_multiPass() {
        // Dispatch some initial insets
        val insets = container.dispatchInsets(systemGestureInsets = Rect(10, 20, 30, 40))
        // ...and assert that the child view state changes
        assertChildViewState(insets)

        // Now dispatch different insets
        val insets2 = container.dispatchInsets(systemGestureInsets = Rect(11, 22, 33, 44))
        // ...and assert that the child view state changes
        assertChildViewState(insets2)
    }

    private fun assertChildViewState(insets: WindowInsetsCompat) {
        val systemWindowInsets = insets.systemWindowInsets
        val systemGestureInsets = insets.systemGestureInsets

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
            assertPadding(
                systemWindowInsets.left,
                systemWindowInsets.top,
                systemWindowInsets.right,
                systemWindowInsets.bottom
            )
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
            assertLayoutMargin(
                systemWindowInsets.left,
                systemWindowInsets.top,
                systemWindowInsets.right,
                systemWindowInsets.bottom
            )
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

        // -------------------------------------------
        // Assert the system gesture padding views
        // -------------------------------------------

        with(container.findViewById<View>(R.id.padding_system_gesture_left)) {
            assertPadding(left = systemGestureInsets.left)
        }
        with(container.findViewById<View>(R.id.padding_system_gesture_top)) {
            assertPadding(top = systemGestureInsets.top)
        }
        with(container.findViewById<View>(R.id.padding_system_gesture_right)) {
            assertPadding(right = systemGestureInsets.right)
        }
        with(container.findViewById<View>(R.id.padding_system_gesture_bottom)) {
            assertPadding(bottom = systemGestureInsets.bottom)
        }
        with(container.findViewById<View>(R.id.padding_system_gesture_all)) {
            assertPadding(
                systemGestureInsets.left,
                systemGestureInsets.top,
                systemGestureInsets.right,
                systemGestureInsets.bottom
            )
        }
        with(container.findViewById<View>(R.id.padding_system_gesture_all_withpadding)) {
            val layoutPadding = resources.getDimensionPixelSize(R.dimen.padding)
            assertPadding(
                systemGestureInsets.left + layoutPadding,
                systemGestureInsets.top + layoutPadding,
                systemGestureInsets.right + layoutPadding,
                systemGestureInsets.bottom + layoutPadding
            )
        }

        // -------------------------------------------
        // Assert the system gesture margin views
        // -------------------------------------------

        with(container.findViewById<View>(R.id.margin_system_gesture_left)) {
            assertLayoutMargin(left = systemGestureInsets.left)
        }
        with(container.findViewById<View>(R.id.margin_system_gesture_top)) {
            assertLayoutMargin(top = systemGestureInsets.top)
        }
        with(container.findViewById<View>(R.id.margin_system_gesture_right)) {
            assertLayoutMargin(right = systemGestureInsets.right)
        }
        with(container.findViewById<View>(R.id.margin_system_gesture_bottom)) {
            assertLayoutMargin(bottom = systemGestureInsets.bottom)
        }
        with(container.findViewById<View>(R.id.margin_system_gesture_all)) {
            assertLayoutMargin(
                systemGestureInsets.left,
                systemGestureInsets.top,
                systemGestureInsets.right,
                systemGestureInsets.bottom
            )
        }
        with(container.findViewById<View>(R.id.margin_system_gesture_all_withmargin)) {
            val layoutMargin = resources.getDimensionPixelSize(R.dimen.margin)
            assertLayoutMargin(
                systemGestureInsets.left + layoutMargin,
                systemGestureInsets.top + layoutMargin,
                systemGestureInsets.right + layoutMargin,
                systemGestureInsets.bottom + layoutMargin
            )
        }

        // -------------------------------------------
        // Assert the mixed inset views
        // -------------------------------------------

        with(container.findViewById<View>(R.id.padding_syswin_left_gest_right)) {
            assertPadding(left = systemWindowInsets.left, right = systemGestureInsets.right)
        }
        with(container.findViewById<View>(R.id.padding_syswin_top_gest_bottom)) {
            assertPadding(top = systemWindowInsets.top, bottom = systemGestureInsets.bottom)
        }
        with(container.findViewById<View>(R.id.padding_syswin_pad_vertical_gest_margin_horiz)) {
            assertPadding(top = systemWindowInsets.top, bottom = systemWindowInsets.bottom)
            assertLayoutMargin(left = systemGestureInsets.left, right = systemGestureInsets.right)
        }
        // Assert that a view with paddingSystemWindowInsets="top" and
        // paddingSystemGestureInsets="top", the gesture insets win
        with(container.findViewById<View>(R.id.padding_syswin_top_gest_top)) {
            assertPadding(top = systemGestureInsets.top)
        }
    }
}