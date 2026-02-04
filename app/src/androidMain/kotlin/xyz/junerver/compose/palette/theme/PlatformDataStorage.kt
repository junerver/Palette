package xyz.junerver.compose.palette.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

actual fun createDataStore(): DataStore<Preferences> = getDataStore()

private lateinit var dataStoreInstance: DataStore<Preferences>

fun initializeDataStore(context: Context) {
    dataStoreInstance = context.dataStore
}

fun getDataStore(): DataStore<Preferences> {
    if (!::dataStoreInstance.isInitialized) {
        throw IllegalStateException("DataStore not initialized. Call initializeDataStore(context) in Application.onCreate()")
    }
    return dataStoreInstance
}
