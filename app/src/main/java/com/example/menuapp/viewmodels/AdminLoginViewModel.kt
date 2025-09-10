package com.example.menuapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuapp.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Represents the result of an authentication attempt.
 */
sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

/**
 * Represents the UI state for the Admin Login screen.
 */
data class AdminLoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val authResult: AuthResult? = null
)

/**
 * ViewModel for the [AdminLoginScreen].
 */
class AdminLoginViewModel(
    private val repository: RestaurantRepository = RestaurantRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminLoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun login() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, authResult = null)

            val result = try {
                repository.signIn(_uiState.value.email, _uiState.value.password)
                // Assuming signIn in repo now returns nothing on success and throws on error
                AuthResult.Success
            } catch (e: Exception) {
                AuthResult.Error(e.message ?: "An unknown error occurred.")
            }

            _uiState.value = _uiState.value.copy(isLoading = false, authResult = result)
        }
    }

    fun resetAuthResult() {
        _uiState.value = _uiState.value.copy(authResult = null)
    }
}
