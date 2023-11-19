package com.tegar.submissionaplikasistoryapp.data.local.preference

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class UserDataStore(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    suspend fun saveUserData(userId: String?, name: String?, token: String?) {
        context.dataStore.edit { preferences ->
            preferences[SESSION_USER_ID] = userId ?: ""
            preferences[SESSION_NAME] = name ?: ""
            preferences[SESSION_TOKEN] = token ?: ""
        }
    }

    fun getUserData() = context.dataStore.data.map { preferences ->
        UserDataPreferences(
            userId = preferences[SESSION_USER_ID] ?: "",
            name = preferences[SESSION_NAME] ?: "",
            token = preferences[SESSION_TOKEN] ?: ""
        )
    }

    suspend fun deleteSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(SESSION_USER_ID)
            preferences.remove(SESSION_NAME)
            preferences.remove(SESSION_TOKEN)
        }
    }

    companion object {
        val SESSION_USER_ID = stringPreferencesKey("session_user_id")
        val SESSION_NAME = stringPreferencesKey("session_name")
        val SESSION_TOKEN = stringPreferencesKey("session_token")

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: UserDataStore? = null

        fun getInstance(context: Context): UserDataStore {
            return instance ?: synchronized(this) {
                instance ?: UserDataStore(context).also { instance = it }
            }
        }
    }
}
