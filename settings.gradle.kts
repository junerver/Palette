pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // PREFER_SETTINGS (not FAIL_ON_PROJECT_REPOS): Kotlin/JS webpack tasks for the wasmJs browser
    // distribution add project-level ivy repos (nodejs.org, binaryen). FAIL_ON_PROJECT_REPOS blocks
    // them, breaking :app:wasmJsBrowserDistribution. We mirror those repos here so resolution
    // still prefers settings-declared repositories.
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        // Node.js runtime — required by Kotlin/JS webpack. Matches the K/JS plugin's own ivy repo.
        ivy("https://nodejs.org/dist") {
            patternLayout {
                artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]")
                artifact("v[revision]/[artifact](-v[revision]).[ext]")
            }
            metadataSources { artifact() }
        }
        // Binaryen (wasm optimizer) + yarn, used by the K/JS production pipeline.
        ivy("https://github.com/WebAssembly/binaryen/releases/download") {
            patternLayout { artifact("[revision]/[artifact]-[revision]-[classifier].[ext]") }
            metadataSources { artifact() }
        }
        ivy("https://github.com/yarnpkg/yarn/releases/download") {
            patternLayout { artifact("v[revision]/[artifact]-v[revision].[ext]") }
            metadataSources { artifact() }
        }
    }
}

rootProject.name = "Palette"
include(":app")
include(":palette")
include(":palette-code")
include(":palette-markdown")
include(":palette-mermaid")
include(":palette-latex")
include(":benchmark")
