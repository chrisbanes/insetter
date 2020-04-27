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

package dev.chrisbanes.insetter.buildsrc

object Versions {
    const val ktlint = "0.36.0"
}

object Libs {
    // We need to use an old version of AGP since that controls which data-binding version
    // we use in dbx. Keeping one stable AGP version behind means we're not forcing people to
    // unnecessarily
    const val androidGradlePlugin = "com.android.tools.build:gradle:3.5.0"

    const val gradleMavenPublishPlugin = "com.vanniktech:gradle-maven-publish-plugin:0.8.0"

    const val junit = "junit:junit:4.12"

    object Kotlin {
        private const val version = "1.3.10"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
    }

    object AndroidX {
        object Test {
            private const val version = "1.2.0"
            const val core = "androidx.test:core:$version"
            const val runner = "androidx.test:runner:$version"
            const val rules = "androidx.test:rules:$version"

            const val ext = "androidx.test.ext:junit:1.1.1"

            const val espressoCore = "androidx.test.espresso:espresso-core:3.2.0"
        }

        const val core = "androidx.core:core:1.2.0"
        const val coreKtx = "androidx.core:core-ktx:1.2.0"
    }

    const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
}
