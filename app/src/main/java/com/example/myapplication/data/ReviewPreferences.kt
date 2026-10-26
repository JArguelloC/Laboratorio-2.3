package com.example.myapplication.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "review_prefs")

class ReviewPreferences(private val context: Context) {
    private val DRAFT_KEY = stringPreferencesKey("draft_content")

    // Flujo de lectura del borrador persistido
    val draftContent: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DRAFT_KEY] ?: ""
    }

    // Función suspendida para almacenar el borrador en disco
    suspend fun saveDraft(content: String) {
        context.dataStore.edit { preferences ->
            preferences[DRAFT_KEY] = content
        }
    }
}