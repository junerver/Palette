package xyz.junerver.compose.palette.theme

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import java.io.File

actual fun createDataStore(): DataStore<Preferences> = dataStoreInstance

private val dataStoreInstance: DataStore<Preferences> by lazy {
    val userHome = System.getProperty("user.home")
    val appDir = File(userHome, ".palette")
    if (!appDir.exists()) {
        appDir.mkdirs()
    }
    val dataStorePath = File(appDir, "theme_preferences.preferences_pb").absolutePath
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { dataStorePath.toPath() }
    )
}
