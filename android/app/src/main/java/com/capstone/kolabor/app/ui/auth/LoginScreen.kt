package com.capstone.kolabor.app.ui.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kolabor.app.R
import com.kolabor.app.ui.components.KolaborPrimaryButton
import com.kolabor.app.ui.components.KolaborTextField
import com.capstone.kolabor.app.viewmodel.AuthViewModel
import com.capstone.kolabor.app.viewmodel.LoginState
import com.capstone.serviceplatform.app.ui.theme.*
import com.kolabor.app.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: (String, Long) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(context) as T
            }
        }
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val loginState by authViewModel.loginState.collectAsState()

    // Gérer les résultats de connexion
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginState.Success -> {
                Log.d("LoginScreen", "✅ Connexion réussie : ${state.response.role}, ID: ${state.response.id}")
                onLoginSuccess(state.response.role, state.response.id)
                authViewModel.resetState()
            }
            is LoginState.Error -> {
                Log.e("LoginScreen", "❌ Erreur : ${state.message}")
            }
            else -> {}
        }
    }

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
            isError = loginState is LoginState.Error,
            supportingText = {
                if (loginState is LoginState.Error) {
                    Text(text = (loginState as LoginState.Error).message, color = ErrorColor)
                }
            }
        )

        Spacer(modifier = Modifier.height(space32))

        KolaborPrimaryButton(
            text = if (loginState is LoginState.Loading) "Connexion en cours..." else "Se connecter",
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    // Afficher un message d'erreur via un état local si besoin
                    return@KolaborPrimaryButton
                }
                authViewModel.login(email, password)
            },
            enabled = loginState !is LoginState.Loading
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