package com.capstone.kolabor.app.ui.auth

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.capstone.kolabor.app.data.model.LoginResponse
import com.capstone.kolabor.app.data.repository.AuthRepository
import com.kolabor.app.ui.components.KolaborPrimaryButton
import com.kolabor.app.ui.components.KolaborTextField
import com.capstone.kolabor.app.utils.TokenManager
import com.capstone.serviceplatform.app.ui.theme.Gray500
import com.capstone.serviceplatform.app.ui.theme.NavyPrimary
import com.kolabor.app.ui.theme.space16
import com.kolabor.app.ui.theme.space24
import com.kolabor.app.ui.theme.space32
import com.kolabor.app.ui.theme.space48
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val tokenManager = remember { TokenManager(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = space24, vertical = space48),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Kolabor",
            style = MaterialTheme.typography.displaySmall,
            color = NavyPrimary
        )
        Text(
            text = "Connectez-vous à votre compte",
            style = MaterialTheme.typography.bodyLarge,
            color = Gray500
        )
        Spacer(modifier = Modifier.height(space32))

        KolaborTextField(
            value = email,
            onValueChange = { email = it },
            label = "Adresse email",
            placeholder = "exemple@kolabor.com",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(space16))

        KolaborTextField(
            value = password,
            onValueChange = { password = it },
            label = "Mot de passe",
            placeholder = "••••••••",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = errorMessage != null,
            errorMessage = errorMessage
        )
        Spacer(modifier = Modifier.height(space32))

        KolaborPrimaryButton(
            text = if (isLoading) "Connexion en cours..." else "Se connecter",
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Email et mot de passe requis"
                    return@KolaborPrimaryButton
                }
                isLoading = true
                errorMessage = null

                coroutineScope.launch {
                    try {
                        val response = authRepository.login(email, password) // LoginResponse?
                        if (response != null) {
                            tokenManager.saveToken(response.token)
                            // Sauvegarde aussi le rôle si tu le souhaites
                            onLoginSuccess()
                        } else {
                            errorMessage = "Email ou mot de passe incorrect"
                        }
                    } catch (e: Exception) {
                        Log.e("LoginScreen", "Erreur de connexion", e)
                        errorMessage = "Erreur réseau. Vérifiez votre connexion."
                    } finally {
                        isLoading = false
                    }
                    try {
                        Log.d("LoginScreen", "Appel login avec email=$email")
                        val response = authRepository.login(email, password)
                        Log.d("LoginScreen", "Réponse reçue: $response")
                        if (response != null) {
                            // succès
                        } else {
                            errorMessage = "Email ou mot de passe incorrect"
                        }
                    } catch (e: Exception) {
                        Log.e("LoginScreen", "Erreur complète", e)
                        errorMessage = "Erreur technique: ${e.message}"
                    }
                }
            },
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(space16))

        Text(
            text = "Pas encore de compte ? Inscrivez-vous",
            style = MaterialTheme.typography.bodyMedium,
            color = NavyPrimary,
            modifier = Modifier
        )
    }
}