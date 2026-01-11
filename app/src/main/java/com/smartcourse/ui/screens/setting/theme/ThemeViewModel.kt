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
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ThemeMode.SYSTEM
            )

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch {
            repository.saveTheme(mode)
        }
    }
}

