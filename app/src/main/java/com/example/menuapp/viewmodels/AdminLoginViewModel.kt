package com.example.menuapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuapp.data.repository.LocalRestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

data class AdminLoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val authResult: AuthResult? = null
)

class AdminLoginViewModel(
    private val repository: LocalRestaurantRepository = LocalRestaurantRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminLoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) { _uiState.value = _uiState.value.copy(email = email) }
    fun onPasswordChange(password: String) { _uiState.value = _uiState.value.copy(password = password) }

    fun login() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, authResult = null)
            val result = try {
                repository.signIn(_uiState.value.email, _uiState.value.password)
                AuthResult.Success
            } catch (e: Exception) {
                AuthResult.Error(e.message ?: "An unknown error occurred.")
            }
            _uiState.value = _uiState.value.copy(isLoading = false, authResult = result)
        }
    }

    fun resetAuthResult() { _uiState.value = _uiState.value.copy(authResult = null) }
}
