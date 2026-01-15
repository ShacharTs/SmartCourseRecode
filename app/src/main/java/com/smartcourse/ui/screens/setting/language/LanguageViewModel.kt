package com.smartcourse.ui.screens.setting.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.repositories.LanguageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val repository: LanguageRepository
) : ViewModel() {

    val languageCode: StateFlow<String> =
        repository.languageFlow
            .stateIn(
                scope = viewModelScope,
                // Keep state hot and stable
                started = SharingStarted.Eagerly,
                initialValue = "en"
            )

    fun setLanguage(code: String) {
        // Avoid redundant writes
        if (languageCode.value == code) return

        viewModelScope.launch {
            repository.saveLanguage(code)
        }
    }
}