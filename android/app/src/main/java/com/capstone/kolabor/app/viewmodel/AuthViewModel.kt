package com.capstone.kolabor.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.kolabor.app.data.model.LoginResponse
import com.capstone.kolabor.app.data.repository.AuthRepository
import com.capstone.kolabor.app.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val context: Context) : ViewModel() {

    private val authRepository = AuthRepository(context)
    private val tokenManager = TokenManager(context)

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = authRepository.login(email, password)
                if (response != null) {
                    // Sauvegarder les données
                    tokenManager.saveToken(response.token)
                    tokenManager.saveUserRole(response.role)
                    tokenManager.saveUserId(response.id)
                    tokenManager.saveUserName(response.nom)
                    _loginState.value = LoginState.Success(response)
                } else {
                    _loginState.value = LoginState.Error("Email ou mot de passe incorrect")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Erreur réseau")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val response: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}