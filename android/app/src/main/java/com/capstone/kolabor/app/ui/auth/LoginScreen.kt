package com.capstone.kolabor.app.ui.auth

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
    onNavigateToRegister: () -> Unit,
    onGoogleLoginClick: () -> Unit = {},
    onFacebookLoginClick: () -> Unit = {}
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
    val isLoading = loginState is LoginState.Loading
    val scrollState = rememberScrollState()

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
            .verticalScroll(scrollState)
            .padding(horizontal = space24, vertical = space48),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(space32))

        // --- Header / Logo ---
        Image(
            painter = painterResource(id = R.drawable.logo_kolabor_svg),
            contentDescription = "Logo Kolabor",
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .height(64.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(space24))
        Text(
            text = "Bon retour parmi nous",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = NavyPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Connectez-vous pour continuer",
            style = MaterialTheme.typography.bodyMedium,
            color = Gray500
        )

        Spacer(modifier = Modifier.height(space48))

        // --- Champs directement sur le fond, sans carte ---
        KolaborTextField(
            value = email,
            onValueChange = { email = it },
            label = "Adresse email",
            placeholder = "exemple@kolabor.com",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(space16))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            placeholder = { Text("••••••••", color = Gray500) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = NavyPrimary,
                unfocusedIndicatorColor = NavyLight,
                focusedLabelColor = NavyPrimary,
                errorIndicatorColor = ErrorColor
            ),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = null, tint = Gray500)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = loginState is LoginState.Error
        )

        // --- Message d'erreur animé ---
        AnimatedVisibility(
            visible = loginState is LoginState.Error,
            enter = fadeIn(tween(200)) + expandVertically(),
            exit = fadeOut(tween(150)) + shrinkVertically()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = space8),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = (loginState as? LoginState.Error)?.message.orEmpty(),
                    color = ErrorColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(space8))

        // Lien "mot de passe oublié" — détail premium discret
        Text(
            text = "Mot de passe oublié ?",
            style = MaterialTheme.typography.bodySmall,
            color = NavyPrimary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = space4)
        )

        Spacer(modifier = Modifier.height(space32))

        KolaborPrimaryButton(
            text = if (isLoading) "Connexion en cours..." else "Se connecter",
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    return@KolaborPrimaryButton
                }
                authViewModel.login(email, password)
            },
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(space32))

        // --- Séparateur "OU" ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Gray500.copy(alpha = 0.25f))
            Text(
                text = "OU CONTINUER AVEC",
                style = MaterialTheme.typography.labelSmall,
                color = Gray500,
                modifier = Modifier.padding(horizontal = space8)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Gray500.copy(alpha = 0.25f))
        }

        Spacer(modifier = Modifier.height(space16))

        // --- Boutons réseaux sociaux ---
        Row(
            horizontalArrangement = Arrangement.spacedBy(space16),
            modifier = Modifier.fillMaxWidth()
        ) {
            SocialLoginButton(
                iconRes = R.drawable.ic_google,
                label = "Google",
                onClick = onGoogleLoginClick,
                modifier = Modifier.weight(1f)
            )
            SocialLoginButton(
                iconRes = R.drawable.ic_facebook,
                label = "Facebook",
                onClick = onFacebookLoginClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(space32))

        Text(
            text = "Pas encore de compte ? Inscrivez-vous",
            style = MaterialTheme.typography.bodyMedium,
            color = NavyPrimary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onNavigateToRegister() }
        )

        Spacer(modifier = Modifier.height(space24))
    }
}

@Composable
private fun SocialLoginButton(
    iconRes: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Gray500.copy(alpha = 0.3f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = Color.Unspecified,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(space8))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}