package xyz.junerver.compose.palette.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object ThemeManager {
    private val dataStore by lazy { createDataStore() }
    private val scope = CoroutineScope(Dispatchers.Default)
    
    private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()
    
    init {
        scope.launch {
            dataStore.data.map { preferences ->
                val modeString = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
                ThemeMode.valueOf(modeString)
            }.collect { mode ->
                _themeMode.value = mode
            }
        }
    }
    
    fun setThemeMode(mode: ThemeMode) {
        scope.launch {
            dataStore.edit { preferences ->
                preferences[THEME_MODE_KEY] = mode.name
            }
        }
    }
    
    fun loadInitialThemeMode(): ThemeMode {
        return runBlocking {
            dataStore.data.map { preferences ->
                val modeString = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
                ThemeMode.valueOf(modeString)
            }.first()
        }
    }
}

val LocalThemeMode = compositionLocalOf { ThemeMode.SYSTEM }
val LocalSetThemeMode = compositionLocalOf<(ThemeMode) -> Unit> { {} }

@Composable
fun isDarkTheme(themeMode: ThemeMode, isSystemInDarkTheme: Boolean): Boolean {
    return when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
}
