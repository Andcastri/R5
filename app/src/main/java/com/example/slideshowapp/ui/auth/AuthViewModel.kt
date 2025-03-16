package com.example.slideshowapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.slideshowapp.data.model.User
import com.example.slideshowapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = userRepository.getUserByUsername(username)
            
            if (user != null && user.password == password) {
                _authState.value = AuthState.Success(user)
            } else {
                _authState.value = AuthState.Error("Credenciales inválidas")
            }
        }
    }

    fun createInitialAdminIfNeeded() {
        viewModelScope.launch {
            val adminUsers = userRepository.getAdminUsers()
            adminUsers.collect { users ->
                if (users.isEmpty()) {
                    userRepository.createUser(
                        User(
                            username = "admin",
                            password = "admin123",
                            isAdmin = true
                        )
                    )
                }
            }
        }
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
} 