/*
 * Copyright 2021 Google LLC
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

package dev.chrisbanes.insetter.animation

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat

/**
 * A [WindowInsetsAnimationCompat.Callback] which will request and clear focus on the given view,
 * depending on the [WindowInsetsCompat.Type.ime] visibility state when an IME
 * [WindowInsetsAnimationCompat] has finished.
 *
 * This is primarily used when animating the [WindowInsetsCompat.Type.ime], so that the
 * appropriate view is focused for accepting input from the IME.
 *
 * @param view the view to request/clear focus
 * @param dispatchMode The dispatch mode for this callback.
 *
 * @see WindowInsetsAnimationCompat.Callback.getDispatchMode
 */
internal class ControlFocusInsetsAnimationCallback(
    private val view: View,
    private val focusWhenVisibleType: Int,
    dispatchMode: Int = DISPATCH_MODE_STOP
) : WindowInsetsAnimationCompat.Callback(dispatchMode) {

    override fun onProgress(
        insets: WindowInsetsCompat,
        runningAnimations: List<WindowInsetsAnimationCompat>
    ): WindowInsetsCompat {
        // no-op and return the insets
        return insets
    }

    override fun onEnd(animation: WindowInsetsAnimationCompat) {
        if (animation.typeMask and focusWhenVisibleType != 0) {
            // The animation has now finished, so we can check the view's focus state.
            // We post the check because the rootWindowInsets has not yet been updated, but will
            // be in the next message traversal
            view.post(this::checkFocus)
        }
    }

    private fun checkFocus() {
        val visible = ViewCompat.getRootWindowInsets(view)?.isVisible(focusWhenVisibleType) == true

        if (visible && view.rootView.findFocus() == null) {
            // If the type is visible, and there is not a currently focused view in
            // the hierarchy, request focus on our view
            view.requestFocus()
        } else if (!visible && view.isFocused) {
            // If the type is not visible and our view is currently focused, clear the focus
            view.clearFocus()
        }
    }
}
