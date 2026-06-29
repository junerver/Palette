@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

// Compose Resources: bundle the CJK fallback font (Noto Sans SC) used by the wasmJs build so Skiko
// can render Chinese glyphs. Custom namespace keeps the generated `Res` accessor next to our code.
compose.resources {
    publicResClass = true
    packageOfResClass = "xyz.junerver.compose.palette"
}

kotlin {
    jvmToolchain(21)

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }

    // Web target: powers the docs-site playground iframe and the `:app:wasmJsBrowserDevelopmentRun`
    // task. `binaries.executable()` is what registers the webpack run/distribution tasks — without it
    // only wasmJsBrowserTest is available. This makes :app the single cross-platform sample
    // (android + desktop + web), so the separate :preview module is no longer needed.
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "paletteWasm.js"
            }
        }
        binaries.executable()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        commonMain.dependencies {
            implementation(libs.jb.compose.runtime)
            implementation(libs.jb.compose.foundation)
            implementation(libs.jb.compose.material3)
            implementation(libs.jb.compose.material.icons.extended)
            implementation(libs.jb.compose.ui)
            implementation(compose.components.resources)
            implementation(project(":palette"))
            // Note: androidx.datastore is intentionally NOT in commonMain — it publishes no wasmJs
            // variant. Theme persistence is abstracted behind ThemeStorage (commonMain interface)
            // with per-platform actuals; the datastore dependency lives in androidMain/desktopMain.
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.datastore.preferences)
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.androidx.datastore.preferences.core)
            }
        }
    }
}

android {
    namespace = "xyz.junerver.compose.palette"
    compileSdk = 35
    buildToolsVersion = "34.0.0"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")

    defaultConfig {
        applicationId = "xyz.junerver.compose.palette"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    buildTypes {
        create("benchmark") {
            initWith(getByName("release"))
            matchingFallbacks += listOf("release")
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = false
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

compose.desktop {
    application {
        mainClass = "xyz.junerver.compose.palette.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Palette"
            packageVersion = "1.0.0"
        }
    }
}
