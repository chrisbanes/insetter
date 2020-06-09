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
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.SdkSuppress
import dev.chrisbanes.insetter.Insetter.EDGE_TO_EDGE_FLAGS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * InsetsTestCase
 *
 * Devices running API 19 or below are suppressed, since all of the related functionality is
 * a no-op on those platform levels.
 */
@SdkSuppress(minSdkVersion = 20)
class InsetterTestCase {
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
    fun test_setOnApplyInsetsListener_paddingValues() {
        view.setPadding(11, 12, 13, 14)
        addViewToContainer()

        lateinit var viewState: ViewState

        val latch = CountDownLatch(1)
        rule.scenario.onActivity {
            Insetter.builder()
                .setOnApplyInsetsListener { _, _, initialState ->
                    viewState = initialState
                    latch.countDown()
                }
                .applyToView(view)
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS))

        assertNotNull(viewState)
        assertEquals(11, viewState.paddings.left)
        assertEquals(12, viewState.paddings.top)
        assertEquals(13, viewState.paddings.right)
        assertEquals(14, viewState.paddings.bottom)
    }

    @Test
    fun test_setOnApplyInsetsListener_marginValues() {
        val marginLp = FrameLayout.LayoutParams(10, 10).apply {
            setMargins(11, 12, 13, 14)
        }
        addViewToContainer(marginLp)

        lateinit var viewState: ViewState

        val latch = CountDownLatch(1)
        rule.scenario.onActivity {
            Insetter.builder()
                .setOnApplyInsetsListener { _, _, initialState ->
                    viewState = initialState
                    latch.countDown()
                }
                .applyToView(view)
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS))

        assertNotNull(viewState)
        assertEquals(11, viewState.margins.left)
        assertEquals(12, viewState.margins.top)
        assertEquals(13, viewState.margins.right)
        assertEquals(14, viewState.margins.bottom)
    }

    @Test
    fun test_requestApplyInsetsWhenAttached_dispatchesWhenAttached() {
        var resultInsets: WindowInsetsCompat? = null

        val latch = CountDownLatch(1)
        rule.scenario.onActivity {
            Insetter.builder()
                .setOnApplyInsetsListener { _, insets, _ ->
                    resultInsets = insets
                    latch.countDown()
                }
                .applyToView(view)
        }

        // We shouldn't have insets now since the view isn't attached
        assertNull(resultInsets)

        // Add the view to the container, which triggers an inset pass on the container
        addViewToContainer()

        assertTrue(latch.await(5, TimeUnit.SECONDS))

        // Assert we now have insets
        assertNotNull(resultInsets)
    }

    @Test
    @UiThreadTest
    fun test_setEdgeToEdgeSystemUiFlags() {
        addViewToContainer(setEdgeToEdgeFlags = false)

        Insetter.setEdgeToEdgeSystemUiFlags(view, true)
        assertEquals(EDGE_TO_EDGE_FLAGS, view.systemUiVisibility and EDGE_TO_EDGE_FLAGS)

        Insetter.setEdgeToEdgeSystemUiFlags(view, false)
        assertEquals(0, view.systemUiVisibility and EDGE_TO_EDGE_FLAGS)
    }

    @Test
    @UiThreadTest
    fun test_setEdgeToEdgeSystemUiFlags_doesntOverwrite() {
        addViewToContainer(setEdgeToEdgeFlags = false)

        // Set some other system-ui flags not related to layout
        val otherFlags = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        view.systemUiVisibility = otherFlags

        Insetter.setEdgeToEdgeSystemUiFlags(view, true)

        assertEquals(EDGE_TO_EDGE_FLAGS, view.systemUiVisibility and EDGE_TO_EDGE_FLAGS)
        assertEquals(otherFlags, view.systemUiVisibility and otherFlags)
    }

    private fun addViewToContainer(
        lp: ViewGroup.LayoutParams? = null,
        setEdgeToEdgeFlags: Boolean = true
    ) {
        rule.scenario.onActivity {
            if (lp != null) {
                container.addView(view, lp)
            } else {
                container.addView(view)
            }
            if (setEdgeToEdgeFlags) {
                view.systemUiVisibility = EDGE_TO_EDGE_FLAGS
            }
        }
    }
}
