package xyz.junerver.compose.palette.theme

import kotlinx.coroutines.flow.Flow

/**
 * Minimal persistence abstraction for the demo app's theme selection.
 *
 * Decouples [ThemeManager] (common code) from the actual storage mechanism, which is platform
 * dependent: android/desktop use androidx DataStore backed by a file, while wasmJs keeps the value
 * in memory (the browser sandbox has no process-wide filesystem). Defining our own tiny interface
 * here — instead of leaking `DataStore<Preferences>` across the boundary — keeps the wasmJs target
 * free of any datastore dependency (androidx.datastore publishes no wasmJs variant).
 */
interface ThemeStorage {
    /** Emits the currently persisted [ThemeMode], starting with the initial value. */
    val themeMode: Flow<ThemeMode>

    /** Persists [mode] for subsequent reads. */
    suspend fun setThemeMode(mode: ThemeMode)
}

/** Platform-specific [ThemeStorage] instance. */
expect fun themeModeStorage(): ThemeStorage
