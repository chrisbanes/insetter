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
import android.view.View
import androidx.test.ext.junit.rules.ActivityScenarioRule
import dev.chrisbanes.insetter.dispatchInsets
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
    fun setOneShot() {
        // Dispatch some initial insets
        container.dispatchInsets {
            it.replaceSystemWindowInsets(10, 20, 30, 40)
        }
        // ...and assert that the paddings change
        with(container.findViewById<View>(R.id.system_window_left)) {
            assertEquals(10, paddingLeft)
        }
        with(container.findViewById<View>(R.id.system_window_top)) {
            assertEquals(20, paddingTop)
        }
        with(container.findViewById<View>(R.id.system_window_right)) {
            assertEquals(30, paddingRight)
        }
        with(container.findViewById<View>(R.id.system_window_bottom)) {
            assertEquals(40, paddingBottom)
        }
        with(container.findViewById<View>(R.id.system_window_all)) {
            assertEquals(10, paddingLeft)
            assertEquals(20, paddingTop)
            assertEquals(30, paddingRight)
            assertEquals(40, paddingBottom)
        }
        with(container.findViewById<View>(R.id.system_window_all_withpadding)) {
            val widgetPadding = resources.getDimensionPixelSize(R.dimen.padding)
            assertEquals(10 + widgetPadding, paddingLeft)
            assertEquals(20 + widgetPadding, paddingTop)
            assertEquals(30 + widgetPadding, paddingRight)
            assertEquals(40 + widgetPadding, paddingBottom)
        }

        // Now dispatch different insets
        container.dispatchInsets {
            it.replaceSystemWindowInsets(11, 22, 33, 44)
        }
        // ...and assert that the paddings change
        with(container.findViewById<View>(R.id.system_window_left)) {
            assertEquals(11, paddingLeft)
        }
        with(container.findViewById<View>(R.id.system_window_top)) {
            assertEquals(22, paddingTop)
        }
        with(container.findViewById<View>(R.id.system_window_right)) {
            assertEquals(33, paddingRight)
        }
        with(container.findViewById<View>(R.id.system_window_bottom)) {
            assertEquals(44, paddingBottom)
        }
        with(container.findViewById<View>(R.id.system_window_all)) {
            assertEquals(11, paddingLeft)
            assertEquals(22, paddingTop)
            assertEquals(33, paddingRight)
            assertEquals(44, paddingBottom)
        }
        with(container.findViewById<View>(R.id.system_window_all_withpadding)) {
            val widgetPadding = resources.getDimensionPixelSize(R.dimen.padding)
            assertEquals(11 + widgetPadding, paddingLeft)
            assertEquals(22 + widgetPadding, paddingTop)
            assertEquals(33 + widgetPadding, paddingRight)
            assertEquals(44 + widgetPadding, paddingBottom)
        }
    }
}