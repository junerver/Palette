package xyz.junerver.compose.palette.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object ThemeManager {
    private val storage by lazy { themeModeStorage() }
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    init {
        // Mirror the persisted value into the observable StateFlow as it changes.
        scope.launch {
            storage.themeMode.collect { mode ->
                _themeMode.value = mode
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        scope.launch {
            storage.setThemeMode(mode)
        }
    }
}

val LocalThemeMode = compositionLocalOf { ThemeMode.SYSTEM }
val LocalSetThemeMode = compositionLocalOf<(ThemeMode) -> Unit> { {} }

@Composable
fun isDarkTheme(
    themeMode: ThemeMode,
    isSystemInDarkTheme: Boolean,
): Boolean {
    return when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
}
