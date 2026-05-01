package com.flowledger.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_LOCK_ENABLED = booleanPreferencesKey("lock_enabled")
        private val KEY_DEFAULT_CURRENCY = stringPreferencesKey("default_currency")
        private val KEY_AUTO_CATEGORIZE = booleanPreferencesKey("auto_categorize")
        private val KEY_CHART_RANGE = stringPreferencesKey("chart_range")
    }

    val isLockEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_LOCK_ENABLED] ?: false
    }

    val defaultCurrency: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_DEFAULT_CURRENCY] ?: "CNY"
    }

    val isAutoCategorize: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_AUTO_CATEGORIZE] ?: true
    }

    suspend fun setLockEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LOCK_ENABLED] = enabled
        }
    }

    suspend fun setDefaultCurrency(currency: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DEFAULT_CURRENCY] = currency
        }
    }

    suspend fun setAutoCategorize(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTO_CATEGORIZE] = enabled
        }
    }

    val chartRange: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_CHART_RANGE] ?: "WEEK"
    }

    suspend fun setChartRange(range: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_CHART_RANGE] = range
        }
    }
}
