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

import androidx.core.view.WindowInsetsCompat

/**
 * Convenience function for building a combination of [WindowInsetsCompat.Type] values.
 */
fun windowInsetTypesOf(
    ime: Boolean = false,
    navigationBars: Boolean = false,
    statusBars: Boolean = false,
    systemGestures: Boolean = false,
    mandatorySystemGestures: Boolean = false,
    displayCutout: Boolean = false,
    captionBar: Boolean = false,
    tappableElement: Boolean = false,
): Int {
    var flag = 0
    if (ime) flag = flag or WindowInsetsCompat.Type.ime()
    if (navigationBars) flag = flag or WindowInsetsCompat.Type.navigationBars()
    if (statusBars) flag = flag or WindowInsetsCompat.Type.statusBars()
    if (systemGestures) flag = flag or WindowInsetsCompat.Type.systemGestures()
    if (displayCutout) flag = flag or WindowInsetsCompat.Type.displayCutout()
    if (captionBar) flag = flag or WindowInsetsCompat.Type.captionBar()
    if (tappableElement) flag = flag or WindowInsetsCompat.Type.tappableElement()
    if (mandatorySystemGestures) flag = flag or WindowInsetsCompat.Type.mandatorySystemGestures()
    return flag
}
