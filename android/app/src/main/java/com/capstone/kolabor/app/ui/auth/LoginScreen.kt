package com.capstone.kolabor.app.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.capstone.kolabor.app.data.repository.AuthRepository
import com.kolabor.app.ui.components.KolaborPrimaryButton
import com.kolabor.app.ui.components.KolaborTextField
import com.capstone.kolabor.app.utils.TokenManager
import com.capstone.serviceplatform.app.ui.theme.ErrorColor
import com.capstone.serviceplatform.app.ui.theme.Gray500
import com.capstone.serviceplatform.app.ui.theme.NavyLight
import com.capstone.serviceplatform.app.ui.theme.NavyPrimary
import com.kolabor.app.ui.theme.space16
import com.kolabor.app.ui.theme.space24
import com.kolabor.app.ui.theme.space32
import com.kolabor.app.ui.theme.space48
import com.kolabor.app.R
import com.kolabor.app.ui.theme.space8
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit,   // ← rôle
                onNavigateToRegister: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository(context) }
    val tokenManager = remember { TokenManager(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) } // ✅ État visibilité


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = space24, vertical = space48),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(space48))
        Image(
            painter = painterResource(id = R.drawable.logo_kolabor_svg),
            contentDescription = "Logo Kolabor",
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(space8))
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

        // Champ Mot de passe
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            placeholder = { Text("••••••••", color = Gray500) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = NavyPrimary,
                unfocusedIndicatorColor = NavyLight,
                focusedLabelColor = NavyPrimary,
                errorIndicatorColor = ErrorColor
            ),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = errorMessage != null,
            supportingText = {
                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = ErrorColor)
                }
            }
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
                        val response = authRepository.login(email, password)
                        if (response != null) {
                            // Stocker le token et le rôle
                            tokenManager.saveToken(response.token)
                            tokenManager.saveUserRole(response.role)
                            onLoginSuccess(response.role)  // MainActivity lira le rôle depuis TokenManager
                        } else {
                            errorMessage = "Email ou mot de passe incorrect"
                        }
                    } catch (e: Exception) {
                        Log.e("LoginScreen", "Erreur", e)
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
                .clickable { onNavigateToRegister() }
        )
    }
}