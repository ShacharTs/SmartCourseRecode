package com.smartcourse.auth


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.models.usermodel.DomainUser
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.AppLaunchRepository
import com.smartcourse.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val appLaunchRepository: AppLaunchRepository
) : ViewModel() {

    val currentUser = authRepo.currentUser

    var authState by mutableStateOf(AuthState.LOADING)
        private set

    var domainUser by mutableStateOf<DomainUser?>(null)
        private set

    init {
        viewModelScope.launch {
            // We use combine or flatMapLatest to ensure we only have ONE source of truth
            appLaunchRepository.termsAccepted.collect { accepted ->
                if (!accepted) {
                    authState = AuthState.TERMS
                    return@collect
                }

                // If terms are accepted, now we listen to the user session
                authRepo.currentUser.collect { user ->
                    // Update the domain user first
                    domainUser = user?.let { authRepo.toDomainUser(it) }

                    // Then update the state (this triggers the UI change)
                    authState = when (user?.role) {
                        null -> AuthState.LOGGED_OUT
                        UserRole.TEMP -> AuthState.CHOOSING_ROLE
                        else -> AuthState.LOGGED_IN
                    }
                }
            }
        }

        // Restore session once
        viewModelScope.launch {
            appLaunchRepository.termsAccepted.collect { accepted ->
                if (accepted) authRepo.restoreValidSession()
            }
        }
    }

    //  Called ONLY from Terms screen
    fun onTermsAccepted() {
        viewModelScope.launch {
            appLaunchRepository.setTermsAccepted()

            // Force auth resolution after terms
            val user = authRepo.restoreValidSession()

            authState = when (user?.role) {
                null -> AuthState.LOGGED_OUT
                UserRole.TEMP -> AuthState.CHOOSING_ROLE
                else -> AuthState.LOGGED_IN
            }
        }
    }


    fun onRoleChosen() {
        viewModelScope.launch {
            val user = authRepo.restoreValidSession()

            authState = when (user?.role) {
                null -> AuthState.LOGGED_OUT
                UserRole.TEMP -> AuthState.CHOOSING_ROLE
                else -> AuthState.LOGGED_IN
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepo.logout()
            domainUser = null
            authState = AuthState.LOGGED_OUT
        }
    }

    fun onUserLoaded(user: DomainUser) {
        domainUser = user
    }


    fun refreshDomainUserFromRepo() {
        val userId = domainUser?.user?.id ?: return

        viewModelScope.launch {
            val freshUser = authRepo.loadOrCreateUser(userId)

            domainUser = authRepo.toDomainUser(freshUser)
        }
    }

}




