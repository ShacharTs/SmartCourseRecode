package com.smartcourse.auth


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.models.usermodel.DomainUser
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {

    val currentUser = authRepo.currentUser

    var authState by mutableStateOf(AuthState.LOADING)
        private set

    var domainUser by mutableStateOf<DomainUser?>(null)
        private set

    init {
        viewModelScope.launch {
            authRepo.currentUser.collect { user ->

                authState = when (user?.role) {
                    null -> AuthState.LOGGED_OUT
                    UserRole.TEMP -> AuthState.CHOOSING_ROLE
                    else -> AuthState.LOGGED_IN
                }

                domainUser = user?.let {
                    authRepo.toDomainUser(it)
                }
            }
        }

        viewModelScope.launch {
            authRepo.restoreValidSession()
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
        }
    }

    fun onUserLoaded(user: DomainUser) {
        domainUser = user
    }

}



