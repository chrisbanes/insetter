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

import android.view.View
import android.view.ViewGroup.MarginLayoutParams

/**
 * A class which models and stores the state of some of a view's layout.
 * It has two properties: [paddings] stores the view's padding, and [margins]
 * stores the view's margins.
 */
data class ViewState(
    /**
     * Represents a view's padding values.
     */
    val paddings: ViewDimensions = ViewDimensions.EMPTY,
    /**
     * Represents a view's margins.
     */
    val margins: ViewDimensions = ViewDimensions.EMPTY
) {
    constructor(view: View) : this(
        paddings = view.paddingDimensions,
        margins = view.marginDimensions
    )
}

private val View.paddingDimensions: ViewDimensions
    get() = ViewDimensions(paddingLeft, paddingTop, paddingRight, paddingBottom)

private val View.marginDimensions: ViewDimensions
    get() {
        val lp = layoutParams
        if (lp is MarginLayoutParams) {
            return ViewDimensions(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin)
        }
        return ViewDimensions.EMPTY
    }
