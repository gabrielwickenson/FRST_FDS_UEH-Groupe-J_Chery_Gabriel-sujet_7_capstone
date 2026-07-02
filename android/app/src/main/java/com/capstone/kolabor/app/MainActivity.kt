package com.kolabor.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.capstone.kolabor.app.ui.auth.LoginScreen
import com.capstone.kolabor.app.ui.auth.RegisterScreen
import com.capstone.kolabor.app.ui.client.BookScreen
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
    val coroutineScope = rememberCoroutineScope()

    val showOnboarding = remember { mutableStateOf(true) }
    val showLogin = remember { mutableStateOf(false) }
    val showRegister = remember { mutableStateOf(false) }
    val showBook = remember { mutableStateOf(false) }
    val selectedPrestataireId = remember { mutableStateOf<Long?>(null) }
    val isLoggedIn = remember { mutableStateOf(false) }
    val userRole = remember { mutableStateOf<String?>(null) }
    val clientId = remember { mutableStateOf<Long?>(null) }
    val userName = remember { mutableStateOf<String?>(null) }

    // Charger la session au démarrage
    LaunchedEffect(Unit) {
        tokenManager.getUserRole().collectLatest { role ->
            val token = tokenManager.getToken()
            if (role != null && token != null) {
                userRole.value = role
                isLoggedIn.value = true
                showOnboarding.value = false
                if (role == "CLIENT") {
                    clientId.value = tokenManager.getUserId() ?: 0L
                    userName.value = tokenManager.getUserName() // ✅ récupérer le nom
                }
            }
        }
    }

    KolaborTheme {
        when {
            isLoggedIn.value && userRole.value != null -> {
                when (userRole.value) {
                    "CLIENT" -> {
                        when {
                            showBook.value && selectedPrestataireId.value != null -> {
                                BookScreen(
                                    onBack = {
                                        showBook.value = false
                                        selectedPrestataireId.value = null
                                    },
                                    onBookingSuccess = {
                                        showBook.value = false
                                        selectedPrestataireId.value = null
                                        Toast.makeText(context, "Réservation réussie !", Toast.LENGTH_LONG).show()
                                    },
                                    prestataireId = selectedPrestataireId.value!!,
                                    clientId = clientId.value ?: 0L
                                )
                            }
                            else -> {
                                ClientDashboard(
                                    onLogout = {
                                        coroutineScope.launch {
                                            tokenManager.clearSession()
                                            isLoggedIn.value = false
                                            userRole.value = null
                                            clientId.value = null
                                            showLogin.value = true
                                        }
                                    },
                                    clientId = clientId.value ?: 0L,
                                    onNavigateToBook = { prestataireId ->
                                        selectedPrestataireId.value = prestataireId
                                        showBook.value = true
                                    },
                                    userName = userName.value ?: "Client"   // ✅ fallback
                                )
                            }
                        }
                    }
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
                        coroutineScope.launch {
                            val id = tokenManager.getUserId()
                            if (id != null && role == "CLIENT") {
                                clientId.value = id
                            }
                        }
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