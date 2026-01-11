package com.smartcourse.ui.screens.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.repositories.AppLaunchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AppStartViewModel @Inject constructor(
    private val repository: AppLaunchRepository
) : ViewModel() {

    val termsAccepted = repository.termsAccepted
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun acceptTerms() {
        viewModelScope.launch {
            repository.setTermsAccepted()
        }
    }
}

