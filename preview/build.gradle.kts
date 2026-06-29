@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    // Pure Web/WASM module: no android/iOS/desktop targets. Being a pure wasmJs application
    // (not an androidApplication) means Compose registers the full browser run/distribution tasks
    // (wasmJsBrowserRun / wasmJsBrowserDistribution), which the app module lacks.
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "palettePreview.js"
            }
        }
        binaries.executable()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        val wasmJsMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(project(":palette"))
            }
        }
    }
}
