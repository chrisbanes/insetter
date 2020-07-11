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

import android.app.Activity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.sample.databinding.ActivityDataBindingBinding

class DataBindingSample : Activity() {
    private lateinit var binding: ActivityDataBindingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_data_binding, null
        )
        binding.state = DataBindingState()

        Insetter.setEdgeToEdgeSystemUiFlags(window.decorView, true)
    }
}
