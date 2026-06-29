@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kover)
}

kotlin {
    jvmToolchain(21)

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    )

    wasmJs { browser() }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = "xyz.junerver.compose.palette.mermaid"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    // KMP `commonTest/resources/` are placed on the Desktop/JVM test classpath automatically,
    // but the Android unit test source set does not inherit them. Register the directory so
    // compatibility fixture tests can load their diagram sources under testDebugUnitTest.
    sourceSets {
        getByName("test") {
            resources.srcDirs("src/commonTest/resources")
        }
    }
}
