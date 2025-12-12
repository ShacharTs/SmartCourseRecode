package com.smartcourse.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun validate(
        email: String,
        password: String,
        confirmPassword: String
    ): String? {
        if (password != confirmPassword) return "Passwords do not match"
        if (!email.contains("@")) return "Invalid email"
        if (password.length < 6) return "Password too short"
        return null
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState(isLoading = true)

            try {
                authRepo.registerWithEmail(email, password)
                _uiState.value = RegisterUiState(success = true)
            } catch (e: Exception) {
                _uiState.value = RegisterUiState(
                    error = e.localizedMessage ?: "Registration failed"
                )
            }
        }
    }

    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(
            error = message,
            isLoading = false
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}