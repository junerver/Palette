package xyz.junerver.compose.palette.theme

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath
import java.io.File

/**
 * Desktop (JVM) [ThemeStorage]: persists the theme mode via androidx DataStore backed by a file
 * under `~/.palette`. Keeps the on-disk format identical to the android build.
 */
actual fun themeModeStorage(): ThemeStorage = DataStoreThemeStorage

private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

private object DataStoreThemeStorage : ThemeStorage {
    private val dataStore: DataStore<Preferences> by lazy {
        val userHome = System.getProperty("user.home")
        val appDir = File(userHome, ".palette")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        val dataStorePath = File(appDir, "theme_preferences.preferences_pb").absolutePath
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { dataStorePath.toPath() },
        )
    }

    override val themeMode: Flow<ThemeMode>
        get() = dataStore.data.map { preferences ->
            val modeString = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
            ThemeMode.valueOf(modeString)
        }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }
}
