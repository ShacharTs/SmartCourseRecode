package com.smartcourse.ui.screens.setting.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.repositories.ThemeRepository
import com.smartcourse.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val repository: ThemeRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> =
        repository.themeModeFlow
            .stateIn(
                scope = viewModelScope,
                // Theme is app-wide state; keep it hot and stable.
                // WhileSubscribed can cause re-subscription loops during navigation/recomposition.
                started = SharingStarted.Eagerly,
                initialValue = ThemeMode.SYSTEM
            )

    fun setTheme(mode: ThemeMode) {
        // Avoid redundant writes + unnecessary recompositions.
        if (themeMode.value == mode) return

        viewModelScope.launch {
            repository.saveTheme(mode)
        }
    }
}
