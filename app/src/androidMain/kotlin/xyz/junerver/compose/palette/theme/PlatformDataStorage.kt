package xyz.junerver.compose.palette.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

/**
 * Android [ThemeStorage]: persists the theme mode via androidx DataStore backed by a file on disk.
 * The store is initialized lazily from the application context — call [initializeDataStore] in
 * Application.onCreate() (or the activity) before [ThemeManager] reads it.
 */
actual fun themeModeStorage(): ThemeStorage = DataStoreThemeStorage

private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

private object DataStoreThemeStorage : ThemeStorage {
    private lateinit var dataStore: DataStore<Preferences>

    fun init(context: Context) {
        dataStore = context.dataStore
    }

    private fun requireStore(): DataStore<Preferences> {
        if (!::dataStore.isInitialized) {
            throw IllegalStateException(
                "DataStore not initialized. Call initializeDataStore(context) in Application.onCreate()",
            )
        }
        return dataStore
    }

    override val themeMode: Flow<ThemeMode>
        get() = requireStore().data.map { preferences ->
            val modeString = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
            ThemeMode.valueOf(modeString)
        }

    override suspend fun setThemeMode(mode: ThemeMode) {
        requireStore().edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }
}

fun initializeDataStore(context: Context) {
    DataStoreThemeStorage.init(context)
}
