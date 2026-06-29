package xyz.junerver.compose.palette.theme

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * wasmJs [ThemeStorage]: keeps the theme mode in memory only.
 *
 * The browser sandbox has no process-wide filesystem, so the file-backed androidx DataStore used on
 * android/desktop is unavailable (and androidx.datastore publishes no wasmJs variant). The value
 * therefore survives only for the lifetime of the page session — reloading the page resets it to
 * the default. This is acceptable for the web playground, which is a read-mostly preview surface;
 * the android/desktop builds keep their on-disk persistence.
 */
actual fun themeModeStorage(): ThemeStorage = InMemoryThemeStorage

private object InMemoryThemeStorage : ThemeStorage {
    private val state = MutableStateFlow(ThemeMode.SYSTEM)

    override val themeMode: Flow<ThemeMode> = state.asStateFlow()

    override suspend fun setThemeMode(mode: ThemeMode) {
        state.value = mode
    }
}
