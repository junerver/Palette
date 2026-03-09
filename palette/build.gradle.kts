@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinxBenchmark)
    alias(libs.plugins.kover)
    id("maven-publish")
}

kotlin {
    jvmToolchain(21)

    androidTarget {
        publishLibraryVariants("release")
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
        compilations {
            val main by getting
            create("benchmark") {
                associateWith(main)
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
        it.binaries.framework {
            baseName = "palette"
            isStatic = false
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.material3)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.collections.immutable)
                implementation(compose.materialIconsExtended)
                api(libs.hooks)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val commonJvmAndroid by creating {
            dependsOn(commonMain)
        }

        val androidMain by getting {
            dependsOn(commonJvmAndroid)
            dependencies {
                implementation(libs.androidx.lifecycle.viewmodel.compose)
                implementation(libs.compose.lifecycle.runtime.compose)
            }
        }

        val desktopMain by getting {
            dependsOn(commonJvmAndroid)
        }

        val desktopBenchmark by getting {
            dependencies {
                implementation(libs.kotlinx.benchmark.runtime)
            }
        }

        val iosMain by getting

        val androidUnitTest by getting
        val desktopTest by getting {
            dependencies {
                implementation(compose.desktop.uiTestJUnit4)
                implementation(compose.desktop.currentOs)
            }
        }
        val iosTest by getting
    }
}

android {
    namespace = "xyz.junerver.compose.palette"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
}

benchmark {
    targets {
        register("desktopBenchmark")
    }
}

tasks.register("verifyCoverageBaseline") {
    group = "verification"
    description = "Verify minimum line coverage from Kover XML report."
    dependsOn("koverXmlReport")

    doLast {
        val candidates = listOf(
            layout.buildDirectory.file("reports/kover/report.xml").get().asFile,
            layout.buildDirectory.file("reports/kover/xml/report.xml").get().asFile,
            layout.buildDirectory.file("reports/kover/xmlReport.xml").get().asFile,
        )
        val reportFile = candidates.firstOrNull { it.exists() }
            ?: error("Cannot find Kover XML report. Tried: ${candidates.joinToString()}")

        val documentBuilderFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
        val document = documentBuilderFactory.newDocumentBuilder().parse(reportFile)
        val counters = document.getElementsByTagName("counter")

        var covered = 0L
        var missed = 0L
        for (index in 0 until counters.length) {
            val counter = counters.item(index) as? org.w3c.dom.Element ?: continue
            if (counter.getAttribute("type") != "LINE") continue
            covered += counter.getAttribute("covered").toLong()
            missed += counter.getAttribute("missed").toLong()
        }

        val total = covered + missed
        require(total > 0) { "Line coverage is empty in report: $reportFile" }
        val coveragePercent = covered * 100.0 / total
        val minimumPercent = 16.5
        if (coveragePercent < minimumPercent) {
            error(
                "Line coverage %.2f%% is below required %.2f%%."
                    .format(coveragePercent, minimumPercent)
            )
        }
        logger.lifecycle(
            "Line coverage %.2f%% (threshold %.2f%%)"
                .format(coveragePercent, minimumPercent)
        )
    }
}

tasks.register("runCoverageChecks") {
    group = "verification"
    description = "Run tests and validate minimum code coverage baseline."
    dependsOn("allTests", "koverXmlReport", "verifyCoverageBaseline")
}

tasks.register("runQualityChecks") {
    group = "verification"
    description = "Run scoped static checks for baseline test/quality tasks."
    dependsOn(
        "detekt",
        "ktlintCommonTestSourceSetCheck",
        "ktlintAndroidUnitTestSourceSetCheck",
        "ktlintAndroidInstrumentedTestSourceSetCheck",
    )
}

tasks.register("verifyReleaseReadiness") {
    group = "verification"
    description = "Run quality checks and all tests before release."
    dependsOn("runQualityChecks", "runCoverageChecks")
}
