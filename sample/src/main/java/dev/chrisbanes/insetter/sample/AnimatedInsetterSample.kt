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

package dev.chrisbanes.insetter.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import dev.chrisbanes.insetter.AnimatedInsetter
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.sample.databinding.ActivityAnimatedBinding

class AnimatedInsetterSample : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val binding = ActivityAnimatedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.conversationRecyclerview.adapter = ConversationAdapter()
        binding.toolbar.title = title

        Insetter.builder()
            .padding(WindowInsetsCompat.Type.systemBars())
            .enableAnimations()
            .padding(WindowInsetsCompat.Type.ime(), deferredDuringAnimation = true)

        AnimatedInsetter.Builder()
            .setAnimatingTypes(WindowInsetsCompat.Type.ime())
            .setPersistentTypes(WindowInsetsCompat.Type.systemBars())
            .animateView(binding.conversationRecyclerview)
            .animateView(binding.messageHolder)
            .focusView(binding.messageEdittext)
            .setParent(binding.root)
            .build()
            .set()
    }
}
