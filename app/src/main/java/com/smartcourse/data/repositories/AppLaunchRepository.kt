package com.smartcourse.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppLaunchRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val TERMS_ACCEPTED_KEY = booleanPreferencesKey("terms_accepted")

    val termsAccepted: Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[TERMS_ACCEPTED_KEY] ?: false
        }

    suspend fun setTermsAccepted() {
        dataStore.edit { prefs ->
            prefs[TERMS_ACCEPTED_KEY] = true
        }
    }
}
