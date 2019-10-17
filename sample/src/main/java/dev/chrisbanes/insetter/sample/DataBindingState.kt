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

package dev.chrisbanes.insetter.sample

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class DataBindingState : BaseObservable() {
    @Bindable
    var paddingLeftSystemWindow = false
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.paddingLeftSystemWindow)
            }
        }

    @Bindable
    var paddingTopSystemWindow = false
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.paddingTopSystemWindow)
            }
        }

    @Bindable
    var paddingRightSystemWindow = false
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.paddingRightSystemWindow)
            }
        }

    @Bindable
    var paddingBottomSystemWindow = false
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.paddingBottomSystemWindow)
            }
        }
}