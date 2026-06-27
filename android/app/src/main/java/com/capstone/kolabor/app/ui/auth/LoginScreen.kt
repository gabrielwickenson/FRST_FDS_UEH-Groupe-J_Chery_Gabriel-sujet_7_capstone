package com.capstone.kolabor.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.capstone.serviceplatform.app.ui.theme.Gray500
import com.capstone.serviceplatform.app.ui.theme.NavyPrimary
import com.kolabor.app.ui.components.KolaborPrimaryButton
import com.kolabor.app.ui.components.KolaborTextField
import com.kolabor.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
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
                isLoading = true
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Email et mot de passe requis"
                    isLoading = false
                } else {
                    // Appel API simulé
                    // onLoginSuccess()
                    errorMessage = null
                    isLoading = false
                    onLoginSuccess()
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