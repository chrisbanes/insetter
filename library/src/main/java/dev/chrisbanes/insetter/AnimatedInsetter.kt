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

package dev.chrisbanes.insetter

import android.view.View
import android.view.ViewParent
import androidx.collection.ArraySet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_CONTINUE_ON_SUBTREE
import androidx.core.view.WindowInsetsCompat
import dev.chrisbanes.insetter.animation.ControlFocusInsetsAnimationCallback
import dev.chrisbanes.insetter.animation.TranslateDeferringInsetsAnimationCallback

/**
 * TODO
 */
class AnimatedInsetter internal constructor(
    private val views: Set<View>,
    private val parent: View,
    private val persistentInsetTypes: Int,
    private val animatingInsetTypes: Int,
    private val focusViews: Set<View>,
) {
    /**
     * TODO
     */
    class Builder {
        private var animatingViews = ArraySet<View>()
        private var focusViews = ArraySet<View>()
        private var parent: ViewParent? = null

        private var persistentInsetTypes: Int = 0
        private var animatingInsetTypes: Int = 0

        /**
         * TODO
         */
        fun animateView(view: View): Builder {
            animatingViews.add(view)
            return this
        }

        /**
         * TODO
         */
        fun focusView(view: View): Builder {
            focusViews.add(view)
            return this
        }

        /**
         * TODO
         */
        fun setParent(parent: ViewParent): Builder {
            this.parent = parent
            return this
        }

        /**
         * TODO
         */
        fun setPersistentTypes(types: Int): Builder {
            persistentInsetTypes = types
            return this
        }

        /**
         * TODO
         */
        fun setAnimatingTypes(types: Int): Builder {
            animatingInsetTypes = types
            return this
        }

        /**
         * TODO
         */
        fun build(): AnimatedInsetter {
            require(animatingViews.isNotEmpty()) {
                "No views have been provided to addView()"
            }
            requireNotNull(parent) {
                "A common non-null parent to all views must be provided"
            }
            require(persistentInsetTypes and animatingInsetTypes == 0) {
                "persistentInsetTypes and deferredInsetTypes can not contain any of " +
                    " same WindowInsetsCompat.Type values"
            }

            val p = parent
            require(p is View) {
                // TODO improve error message
                "The provided parent does not extend View"
            }

            return AnimatedInsetter(
                views = animatingViews,
                parent = p,
                persistentInsetTypes = persistentInsetTypes,
                animatingInsetTypes = animatingInsetTypes,
                focusViews = focusViews,
            )
        }
    }

    /**
     * TODO
     */
    fun set() {
        /**
         * 1) Since our Activity has declared `window.setDecorFitsSystemWindows(false)`, we need to
         * handle any [WindowInsetsCompat] as appropriate.
         *
         * Our [RootViewDeferringInsetsCallback] will update our attached view's padding to match
         * the combination of the [WindowInsetsCompat.Type.systemBars], and selectively apply the
         * [WindowInsetsCompat.Type.ime] insets, depending on any ongoing WindowInsetAnimations
         * (see that class for more information).
         */

        Insetter.builder()
            .padding(WindowInsetsCompat.Type.systemBars())
            .enableAnimations()
            .deferredPadding(WindowInsetsCompat.Type.ime())
            .applyToView(parent)

        /**
         * 2) The second step is reacting to any animations which run. This can be system driven,
         * such as the user focusing on an EditText and on-screen keyboard (IME) coming on screen,
         * or app driven (more on that in step 3).
         *
         * To react to animations, we set an [android.view.WindowInsetsAnimation.Callback] on any
         * views which we wish to react to inset animations. In this example, we want our
         * EditText holder view, and the conversation RecyclerView to react.
         *
         * We use our [TranslateDeferringInsetsAnimationCallback] class, bundled in this sample,
         * which will automatically move each view as the IME animates.
         *
         * Note about [TranslateDeferringInsetsAnimationCallback], it relies on the behavior of
         * [RootViewDeferringInsetsCallback] on the layout's root view.
         */

        views.forEach { view ->
            ViewCompat.setWindowInsetsAnimationCallback(
                view,
                TranslateDeferringInsetsAnimationCallback(
                    view = view,
                    persistentInsetTypes = persistentInsetTypes,
                    deferredInsetTypes = animatingInsetTypes,
                    dispatchMode = DISPATCH_MODE_CONTINUE_ON_SUBTREE
                )
            )
        }

        /**
         * 2.5) We also want to make sure that our EditText is focused once the IME
         * is animated in, to enable it to accept input. Similarly, if the IME is animated
         * off screen and the EditText is focused, we should clear that focus.
         *
         * The bundled [ControlFocusInsetsAnimationCallback] callback will automatically request
         * and clear focus for us.
         *
         * Since `binding.messageEdittext` is a child of `binding.messageHolder`, this
         * [WindowInsetsAnimationCompat.Callback] will only work if the ancestor view's callback uses the
         * [WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_CONTINUE_ON_SUBTREE] dispatch mode, which
         * we have done above.
         */
        focusViews.forEach { view ->
            ViewCompat.setWindowInsetsAnimationCallback(
                view,
                ControlFocusInsetsAnimationCallback(
                    view = view,
                    focusWhenVisibleType = animatingInsetTypes,
                    dispatchMode = DISPATCH_MODE_CONTINUE_ON_SUBTREE
                )
            )
        }
    }
}
