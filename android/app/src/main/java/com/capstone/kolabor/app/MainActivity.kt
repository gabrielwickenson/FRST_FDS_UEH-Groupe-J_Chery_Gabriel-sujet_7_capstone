package com.kolabor.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.capstone.kolabor.app.ui.auth.LoginScreen
import com.capstone.kolabor.app.ui.auth.RegisterScreen
import com.capstone.kolabor.app.ui.dashboard.ClientDashboard
import com.capstone.kolabor.app.ui.dashboard.PrestataireDashboard
import com.capstone.kolabor.app.ui.onboarding.OnboardingScreen
import com.kolabor.app.ui.theme.KolaborTheme
import com.capstone.kolabor.app.utils.TokenManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KolaborApp()
        }
    }
}

@Composable
fun KolaborApp() {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val coroutineScope = rememberCoroutineScope() // ✅ Pour lancer des coroutines

    val showOnboarding = remember { mutableStateOf(true) }
    val showLogin = remember { mutableStateOf(false) }
    val showRegister = remember { mutableStateOf(false) }
    val isLoggedIn = remember { mutableStateOf(false) }
    val userRole = remember { mutableStateOf<String?>(null) }

    // Vérifier si un token existe déjà au démarrage
    LaunchedEffect(Unit) {
        tokenManager.getUserRole().collectLatest { role ->
            if (role != null && tokenManager.getToken() != null) {
                userRole.value = role
                isLoggedIn.value = true
                showOnboarding.value = false
            }
        }
    }

    KolaborTheme {
        when {
            isLoggedIn.value && userRole.value != null -> {
                when (userRole.value) {
                    "CLIENT" -> ClientDashboard(
                        onLogout = {
                            // ✅ Lancer la coroutine pour clearSession
                            coroutineScope.launch {
                                tokenManager.clearSession()
                                isLoggedIn.value = false
                                userRole.value = null
                                showLogin.value = true
                            }
                        }
                    )
                    "PRESTATAIRE" -> PrestataireDashboard(
                        onLogout = {
                            coroutineScope.launch {
                                tokenManager.clearSession()
                                isLoggedIn.value = false
                                userRole.value = null
                                showLogin.value = true
                            }
                        }
                    )
                    else -> {
                        showLogin.value = true
                        isLoggedIn.value = false
                    }
                }
            }
            showOnboarding.value -> {
                OnboardingScreen(
                    onGetStarted = {
                        showOnboarding.value = false
                        showLogin.value = true
                    }
                )
            }
            showLogin.value -> {
                LoginScreen(
                    onLoginSuccess = { role ->
                        userRole.value = role
                        isLoggedIn.value = true
                        showLogin.value = false
                    },
                    onNavigateToRegister = {
                        showLogin.value = false
                        showRegister.value = true
                    }
                )
            }
            showRegister.value -> {
                RegisterScreen(
                    onNavigateToLogin = {
                        showRegister.value = false
                        showLogin.value = true
                    },
                    onRegisterSuccess = {
                        showRegister.value = false
                        showLogin.value = true
                    }
                )
            }
            else -> {
                OnboardingScreen(onGetStarted = {})
            }
        }
    }
}