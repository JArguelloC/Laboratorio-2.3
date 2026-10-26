package com.example.myapplication.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ReviewPreferences
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val stateHandle: SavedStateHandle,
    private val reviewPrefs: ReviewPreferences
) : ViewModel() {

    // 1. Puntaje: Persiste en memoria (sobrevive a rotación)
    var rating by mutableFloatStateOf(1f)
        private set

    fun updateRating(newRating: Float) {
        rating = newRating
    }

    // 2. Comentario: Persiste ante Muerte de Proceso (SavedStateHandle)
    var comment by mutableStateOf(stateHandle.get<String>("review_comment") ?: "")
        private set

    fun updateComment(newComment: String) {
        comment = newComment
        stateHandle["review_comment"] = newComment
    }

    // 3. Borrador: Persistencia en disco (DataStore)
    val savedDraft = reviewPrefs.draftContent.asLiveData()

    fun saveDraftToDisk() {
        viewModelScope.launch {
            reviewPrefs.saveDraft(comment)
        }
    }
}