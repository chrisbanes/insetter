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
    const val ktlint = "0.40.0"
}

object Libs {
    // We need to use a stable version of AGP since that controls which data-binding version
    // we use in dbx
    const val androidGradlePlugin = "com.android.tools.build:gradle:4.0.1"

    const val gradleMavenPublishPlugin = "com.vanniktech:gradle-maven-publish-plugin:0.13.0"

    const val junit = "junit:junit:4.12"

    object Kotlin {
        private const val version = "1.4.10"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"

        const val binaryCompatibility = "org.jetbrains.kotlinx:binary-compatibility-validator:0.2.3"
    }

    const val dokka = "org.jetbrains.dokka:dokka-gradle-plugin:1.4.10"

    object AndroidX {
        object Test {
            private const val version = "1.2.0"
            const val core = "androidx.test:core:$version"
            const val runner = "androidx.test:runner:$version"
            const val rules = "androidx.test:rules:$version"

            const val ext = "androidx.test.ext:junit:1.1.1"

            const val espressoCore = "androidx.test.espresso:espresso-core:3.2.0"
        }

        const val core = "androidx.core:core:1.5.0-alpha05"
        const val coreKtx = "androidx.core:core-ktx:1.5.0-alpha05"
    }

    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0"
}
