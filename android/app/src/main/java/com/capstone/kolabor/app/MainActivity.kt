package com.kolabor.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.capstone.kolabor.app.ui.auth.LoginScreen
import com.capstone.kolabor.app.ui.auth.RegisterScreen
import com.capstone.kolabor.app.ui.onboarding.OnboardingScreen
import com.kolabor.app.ui.theme.KolaborTheme

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
    val showOnboarding = remember { mutableStateOf(true) }
    val showLogin = remember { mutableStateOf(false) }
    val showRegister = remember { mutableStateOf(false) }

    KolaborTheme {
        when {
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
                    onLoginSuccess = {
                        // Navigation vers le dashboard (plus tard)
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
                        // Inscription réussie → on retourne au login
                        showRegister.value = false
                        showLogin.value = true
                    }
                )
            }
            else -> {
                // Par sécurité, on affiche l'onboarding
                OnboardingScreen(onGetStarted = {})
            }
        }
    }
}