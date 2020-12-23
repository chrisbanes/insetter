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

package dev.chrisbanes.insetter.dbx

import android.app.Activity
import android.graphics.Rect
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.test.ext.junit.rules.ActivityScenarioRule
import dev.chrisbanes.insetter.test.dbx.databinding.InsetterDbxBinding
import dev.chrisbanes.insetter.test.dbx.test.R
import dev.chrisbanes.insetter.testutils.assertLayoutMargin
import dev.chrisbanes.insetter.testutils.assertPadding
import dev.chrisbanes.insetter.testutils.dispatchInsets
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DbxInsetsTestCase {
    @get:Rule
    val rule = ActivityScenarioRule(Activity::class.java)

    private lateinit var binding: InsetterDbxBinding

    @Before
    fun setup() {
        rule.scenario.onActivity { activity ->
            binding = DataBindingUtil.setContentView(activity, R.layout.insetter_dbx)
        }
    }

    @Test
    fun systemWindowInsets_singlePass() {
        // Dispatch some initial insets
        val insets = binding.root.dispatchInsets(systemWindowInsets = Rect(5, 7, 9, 13))
        // ...and assert that the child view state changes
        assertChildViewState(insets)
    }

    @Test
    fun systemWindowInsets_multiPass() {
        // Dispatch some initial insets
        val insets = binding.root.dispatchInsets(systemWindowInsets = Rect(10, 20, 30, 40))
        // ...and assert that the child view state changes
        assertChildViewState(insets)

        // Now dispatch different insets
        val insets2 = binding.root.dispatchInsets(systemWindowInsets = Rect(11, 22, 33, 44))
        // ...and assert that the child view state changes
        assertChildViewState(insets2)
    }

    @Test
    fun systemGestureInsets_singlePass() {
        // Dispatch some initial insets
        val insets = binding.root.dispatchInsets(systemGestureInsets = Rect(5, 7, 9, 13))
        // ...and assert that the child view state changes
        assertChildViewState(insets)
    }

    @Test
    fun systemGestureInsets_multiPass() {
        // Dispatch some initial insets
        val insets = binding.root.dispatchInsets(systemGestureInsets = Rect(10, 20, 30, 40))
        // ...and assert that the child view state changes
        assertChildViewState(insets)

        // Now dispatch different insets
        val insets2 = binding.root.dispatchInsets(systemGestureInsets = Rect(11, 22, 33, 44))
        // ...and assert that the child view state changes
        assertChildViewState(insets2)
    }

    private fun assertChildViewState(insets: WindowInsetsCompat) {
        @Suppress("DEPRECATION")
        val systemWindowInsets = insets.systemWindowInsets
        @Suppress("DEPRECATION")
        val systemGestureInsets = insets.systemGestureInsets

        // -------------------------------------------
        // Assert the system window padding views
        // -------------------------------------------

        with(binding.systemWindowPadding.left) {
            assertPadding(left = systemWindowInsets.left)
        }
        with(binding.systemWindowPadding.top) {
            assertPadding(top = systemWindowInsets.top)
        }
        with(binding.systemWindowPadding.right) {
            assertPadding(right = systemWindowInsets.right)
        }
        with(binding.systemWindowPadding.bottom) {
            assertPadding(bottom = systemWindowInsets.bottom)
        }
        with(binding.systemWindowPadding.all) {
            assertPadding(
                systemWindowInsets.left,
                systemWindowInsets.top,
                systemWindowInsets.right,
                systemWindowInsets.bottom
            )
        }
        with(binding.systemWindowPadding.allWithpadding) {
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

        with(binding.systemWindowMargin.left) {
            assertLayoutMargin(left = systemWindowInsets.left)
        }
        with(binding.systemWindowMargin.top) {
            assertLayoutMargin(top = systemWindowInsets.top)
        }
        with(binding.systemWindowMargin.right) {
            assertLayoutMargin(right = systemWindowInsets.right)
        }
        with(binding.systemWindowMargin.bottom) {
            assertLayoutMargin(bottom = systemWindowInsets.bottom)
        }
        with(binding.systemWindowMargin.all) {
            assertLayoutMargin(
                systemWindowInsets.left,
                systemWindowInsets.top,
                systemWindowInsets.right,
                systemWindowInsets.bottom
            )
        }
        with(binding.systemWindowMargin.allWithmargin) {
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

        with(binding.systemGesturePadding.left) {
            assertPadding(left = systemGestureInsets.left)
        }
        with(binding.systemGesturePadding.top) {
            assertPadding(top = systemGestureInsets.top)
        }
        with(binding.systemGesturePadding.right) {
            assertPadding(right = systemGestureInsets.right)
        }
        with(binding.systemGesturePadding.bottom) {
            assertPadding(bottom = systemGestureInsets.bottom)
        }
        with(binding.systemGesturePadding.all) {
            assertPadding(
                systemGestureInsets.left,
                systemGestureInsets.top,
                systemGestureInsets.right,
                systemGestureInsets.bottom
            )
        }
        with(binding.systemGesturePadding.allWithpadding) {
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

        with(binding.systemGestureMargin.left) {
            assertLayoutMargin(left = systemGestureInsets.left)
        }
        with(binding.systemGestureMargin.top) {
            assertLayoutMargin(top = systemGestureInsets.top)
        }
        with(binding.systemGestureMargin.right) {
            assertLayoutMargin(right = systemGestureInsets.right)
        }
        with(binding.systemGestureMargin.bottom) {
            assertLayoutMargin(bottom = systemGestureInsets.bottom)
        }
        with(binding.systemGestureMargin.all) {
            assertLayoutMargin(
                systemGestureInsets.left,
                systemGestureInsets.top,
                systemGestureInsets.right,
                systemGestureInsets.bottom
            )
        }
        with(binding.systemGestureMargin.allWithmargin) {
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

        with(binding.mixedinsets.paddingSyswinLeftGestRight) {
            assertPadding(left = systemWindowInsets.left, right = systemGestureInsets.right)
        }
        with(binding.mixedinsets.paddingSyswinTopGestBottom) {
            assertPadding(top = systemWindowInsets.top, bottom = systemGestureInsets.bottom)
        }
        with(binding.mixedinsets.paddingSyswinPadVerticalGestMarginHoriz) {
            assertPadding(top = systemWindowInsets.top, bottom = systemGestureInsets.bottom)
            assertLayoutMargin(left = systemGestureInsets.left, right = systemGestureInsets.right)
        }
        // Assert that a view with paddingSystemWindowInsets="top" and
        // paddingSystemGestureInsets="top", the gesture insets win
        with(binding.mixedinsets.paddingSyswinTopGestTop) {
            assertPadding(top = systemGestureInsets.top)
        }
    }
}
