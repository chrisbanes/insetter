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

package dev.chrisbanes.insetly.buildsrc

object Versions {
    const val ktlint = "0.33.0"
}

object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:3.5.0"

    const val junit = "junit:junit:4.12"
    const val robolectric = "org.robolectric:robolectric:4.3"
    const val mockK = "io.mockk:mockk:1.9.3"

    object Kotlin {
        private const val version = "1.3.50"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
    }

    object AndroidX {
        const val recyclerview = "androidx.recyclerview:recyclerview:1.1.0-beta04"

        object Fragment {
            private const val version = "1.2.0-alpha02"
            const val fragment = "androidx.fragment:fragment:$version"
            const val fragmentKtx = "androidx.fragment:fragment-ktx:$version"
        }

        object Test {
            private const val version = "1.2.0"
            const val core = "androidx.test:core:$version"
            const val runner = "androidx.test:runner:$version"
            const val rules = "androidx.test:rules:$version"

            const val espressoCore = "androidx.test.espresso:espresso-core:3.2.0"
        }

        const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta2"

        const val core = "androidx.core:core:1.0.0"

        object Lifecycle {
            private const val version = "2.2.0-alpha03"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:$version"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        }
    }
}
