package com.smartcourse.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.smartcourse.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ThemeRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {


    private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

    val themeModeFlow: Flow<ThemeMode> =
        dataStore.data.map { prefs ->
            when (prefs[THEME_MODE_KEY]) {
                ThemeMode.DARK.name -> ThemeMode.DARK
                ThemeMode.LIGHT.name -> ThemeMode.LIGHT
                else -> ThemeMode.SYSTEM
            }
        }

    suspend fun saveTheme(mode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = mode.name
        }
    }
}
