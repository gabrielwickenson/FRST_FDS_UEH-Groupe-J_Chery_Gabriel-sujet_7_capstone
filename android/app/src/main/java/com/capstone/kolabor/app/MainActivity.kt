package com.kolabor.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.capstone.kolabor.app.ui.auth.LoginScreen
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
    // On utilise .value pour éviter les problèmes de délégation
    val showOnboarding = remember { mutableStateOf(true) }
    val showLogin = remember { mutableStateOf(false) }

    KolaborTheme {
        if (showOnboarding.value) {
            OnboardingScreen(
                onGetStarted = {
                    showOnboarding.value = false
                    showLogin.value = true
                }
            )
        } else if (showLogin.value) {
            LoginScreen(
                onLoginSuccess = {
                    // Navigation vers le dashboard (plus tard)
                }
            )
        } else {
            // Sécurité : afficher l'onboarding par défaut
            OnboardingScreen(onGetStarted = {})
        }
    }
}