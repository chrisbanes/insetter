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

package dev.chrisbanes.insetter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.WindowInsetsCompat
import androidx.test.ext.junit.rules.ActivityScenarioRule
import dev.chrisbanes.insetter.Insetter.EDGE_TO_EDGE_FLAGS
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(minSdk = 23, maxSdk = 28)
class InsetsUtilsTestCase {
    @get:Rule
    val rule = ActivityScenarioRule(Activity::class.java)

    private lateinit var container: FrameLayout
    private lateinit var view: ImageView

    @Before
    fun setup() {
        rule.scenario.onActivity { activity ->
            container = FrameLayout(activity)
            view = ImageView(activity)
            activity.setContentView(container, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        }
    }

    @Test
    fun test_ApplyInsetsListener_paddingValues() {
        view.setPadding(11, 12, 13, 14)
        addViewToContainer()

        Insetter.setOnApplyInsetsListener(view) { _, _, initialState ->
            assertEquals(11, initialState.paddings.left)
            assertEquals(12, initialState.paddings.top)
            assertEquals(13, initialState.paddings.right)
            assertEquals(14, initialState.paddings.bottom)
        }
    }

    @Test
    fun test_ApplyInsetsListener_marginValues() {
        val marginLp = FrameLayout.LayoutParams(10, 10).apply {
            setMargins(11, 12, 13, 14)
        }
        addViewToContainer(marginLp)

        Insetter.setOnApplyInsetsListener(view) { _, _, initialState ->
            assertEquals(11, initialState.margins.left)
            assertEquals(12, initialState.margins.top)
            assertEquals(13, initialState.margins.right)
            assertEquals(14, initialState.margins.bottom)
        }
    }

    @Test
    fun test_requestApplyInsetsWhenAttached_dispatchesWhenAttached() {
        var resultInsets: WindowInsetsCompat? = null

        Insetter.setOnApplyInsetsListener(view) { _, insets, _ ->
            resultInsets = insets
        }

        // We shouldn't have insets now since the view isn't attached
        assertNull(resultInsets)

        // Add the view to the container, which triggers an inset pass on the container
        addViewToContainer()

        // Assert we now have insets
        assertNotNull(resultInsets)
    }

    @Test
    fun test_setEdgeToEdgeSystemUiFlags() {
        addViewToContainer()

        Insetter.setEdgeToEdgeSystemUiFlags(view, true)
        assertEquals(EDGE_TO_EDGE_FLAGS, view.systemUiVisibility and EDGE_TO_EDGE_FLAGS)

        Insetter.setEdgeToEdgeSystemUiFlags(view, false)
        assertEquals(0, view.systemUiVisibility and EDGE_TO_EDGE_FLAGS)
    }

    @Test
    fun test_setEdgeToEdgeSystemUiFlags_doesntOverwrite() {
        addViewToContainer()

        // Set some other system-ui flags not related to layout
        val otherFlags = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        view.systemUiVisibility = otherFlags

        Insetter.setEdgeToEdgeSystemUiFlags(view, true)

        assertEquals(EDGE_TO_EDGE_FLAGS, view.systemUiVisibility and EDGE_TO_EDGE_FLAGS)
        assertEquals(otherFlags, view.systemUiVisibility and otherFlags)
    }

    private fun addViewToContainer(lp: ViewGroup.LayoutParams? = null) {
        if (lp != null) container.addView(view, lp) else container.addView(view)

        // Dispatch some insets from the container, which is similar to how the system
        // would dispatch them
        container.dispatchInsets()
    }
}