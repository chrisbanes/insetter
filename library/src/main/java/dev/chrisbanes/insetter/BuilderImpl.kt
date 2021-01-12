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

/**
 * A builder class for creating instances of [Insetter]
 */
internal class BuilderImpl : Insetter.Builder, Insetter.AnimatedBuilder {
    internal var onApplyInsetsListener: OnApplyInsetsListener? = null

    internal var padding = SideApply()
    internal var margin = SideApply()

    internal var deferredPadding = SideApply()
    internal var deferredMargin = SideApply()

    internal var consume = Insetter.CONSUME_NONE

    override fun enableAnimations(): Insetter.AnimatedBuilder = this

    override fun setOnApplyInsetsListener(onApplyInsetsListener: OnApplyInsetsListener?): Insetter.Builder {
        this.onApplyInsetsListener = onApplyInsetsListener
        return this
    }

    override fun padding(
        insetType: Int,
        @Sides sides: Int
    ): Insetter.Builder {
        return padding(insetType, sides, false)
    }

    override fun padding(
        insetType: Int,
        sides: Int,
        deferredDuringAnimation: Boolean
    ): Insetter.AnimatedBuilder {
        (if (deferredDuringAnimation) deferredPadding else padding).add(insetType, sides)
        return this
    }

    override fun paddingLeft(insetType: Int): Insetter.Builder = padding(insetType, Side.LEFT)

    override fun paddingTop(insetType: Int): Insetter.Builder = padding(insetType, Side.TOP)

    override fun paddingRight(insetType: Int): Insetter.Builder = padding(insetType, Side.RIGHT)

    override fun paddingBottom(insetType: Int): Insetter.Builder = padding(insetType, Side.BOTTOM)

    override fun margin(
        insetType: Int,
        @Sides sides: Int
    ): Insetter.Builder {
        return margin(insetType, sides, false)
    }

    override fun margin(
        insetType: Int,
        sides: Int,
        deferredDuringAnimation: Boolean
    ): Insetter.AnimatedBuilder {
        (if (deferredDuringAnimation) deferredMargin else margin).add(insetType, sides)
        return this
    }

    override fun marginLeft(insetType: Int): Insetter.Builder = margin(insetType, Side.LEFT)

    override fun marginTop(insetType: Int): Insetter.Builder = margin(insetType, Side.TOP)

    override fun marginRight(insetType: Int): Insetter.Builder = margin(insetType, Side.RIGHT)

    override fun marginBottom(insetType: Int): Insetter.Builder = margin(insetType, Side.BOTTOM)

    override fun applySystemWindowInsetsToPadding(@Sides sides: Int): Insetter.Builder {
        return padding(
            windowInsetTypesOf(ime = true, statusBars = true, navigationBars = true),
            sides
        )
    }

    override fun applySystemWindowInsetsToMargin(@Sides sides: Int): Insetter.Builder {
        return margin(
            windowInsetTypesOf(ime = true, statusBars = true, navigationBars = true),
            sides
        )
    }

    override fun applySystemGestureInsetsToPadding(@Sides sides: Int): Insetter.Builder {
        return padding(windowInsetTypesOf(systemGestures = true), sides)
    }

    override fun applySystemGestureInsetsToMargin(@Sides sides: Int): Insetter.Builder {
        return margin(windowInsetTypesOf(systemGestures = true), sides)
    }

    override fun consume(@Insetter.ConsumeOptions consume: Int): Insetter.Builder {
        this.consume = consume
        return this
    }

    override fun consumeSystemWindowInsets(consumeSystemWindowInsets: Boolean): Insetter.Builder {
        return consume(if (consumeSystemWindowInsets) Insetter.CONSUME_ALL else Insetter.CONSUME_NONE)
    }

    override fun consumeSystemWindowInsets(
        @Insetter.ConsumeOptions consume: Int
    ): Insetter.Builder = consume(consume)

    override fun applyToView(view: View): Insetter {
        return build().also { it.applyToView(view) }
    }

    override fun build(): Insetter = Insetter(this)
}
