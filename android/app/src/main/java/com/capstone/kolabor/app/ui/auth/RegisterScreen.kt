package com.capstone.kolabor.app.ui.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.kolabor.app.R
import com.capstone.kolabor.app.data.model.RegisterRequest
import com.capstone.kolabor.app.data.repository.AuthRepository
import com.kolabor.app.ui.components.KolaborPrimaryButton
import com.kolabor.app.ui.components.KolaborTextField
import com.capstone.serviceplatform.app.ui.theme.*
import com.capstone.serviceplatform.app.ui.theme.ErrorColor
import com.capstone.serviceplatform.app.ui.theme.Gray500
import com.kolabor.app.ui.theme.space16
import com.kolabor.app.ui.theme.space24
import com.kolabor.app.ui.theme.space32
import com.kolabor.app.ui.theme.space48
import com.kolabor.app.ui.theme.space8
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    // États des champs
    var nom by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var motDePasse by remember { mutableStateOf("") }
    var confirmMotDePasse by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("CLIENT") }

    // Visibilité des mots de passe
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Champs Client
    var adresse by remember { mutableStateOf("") }

    // Champs Prestataire
    var competences by remember { mutableStateOf("") }
    var tarifHoraire by remember { mutableStateOf("") }
    var zoneIntervention by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val passwordsMatch = motDePasse == confirmMotDePasse && motDePasse.isNotBlank()

    // 🔥 NOUVELLE STRUCTURE : En‑tête fixe + contenu scrollable
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ─── PARTIE FIXE (Logo + sous‑titre) ───
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = space24, end = space24, top = space48),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo Kolabor (SVG)
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
                text = "Créez votre compte",
                style = MaterialTheme.typography.bodyLarge,
                color = Gray500
            )
            Spacer(modifier = Modifier.height(space24))
        }

        // ─── 🔥 DÉGRADÉ DE FONDU ULTRA-PARFAIT (Blanc vers Transparent pur) ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp) // Une hauteur plus grande adoucit l'effet "fondu"
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,                    // 100% Transpent en haut
                            Color.White.copy(alpha = 0.5f), // Transition douce au milieu
                            Color.Transparent.copy(alpha = 0.0f)  // 100% transparent en bas
                        )
                    )
                )
        )

        // ─── PARTIE SCROLLABLE (Champs, boutons, liens) ───
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = space24, vertical = space8),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Nom
                KolaborTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = "Nom complet",
                    placeholder = "Jean Dupont"
                )
                Spacer(modifier = Modifier.height(space16))

                // Email
                KolaborTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Adresse email",
                    placeholder = "exemple@kolabor.com",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(space16))

                // Mot de passe avec œil
                OutlinedTextField(
                    value = motDePasse,
                    onValueChange = { motDePasse = it },
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                Spacer(modifier = Modifier.height(space16))

                // Confirmation mot de passe avec œil
                OutlinedTextField(
                    value = confirmMotDePasse,
                    onValueChange = { confirmMotDePasse = it },
                    label = { Text("Confirmer le mot de passe") },
                    placeholder = { Text("••••••••", color = Gray500) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = !passwordsMatch && confirmMotDePasse.isNotBlank(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = NavyPrimary,
                        unfocusedIndicatorColor = NavyLight,
                        focusedLabelColor = NavyPrimary,
                        errorIndicatorColor = ErrorColor
                    ),
                    trailingIcon = {
                        val icon = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(imageVector = icon, contentDescription = null)
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    supportingText = {
                        if (!passwordsMatch && confirmMotDePasse.isNotBlank()) {
                            Text("Les mots de passe ne correspondent pas", color = ErrorColor)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(space16))

                // Sélecteur de rôle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = role == "CLIENT",
                        onClick = { role = "CLIENT" },
                        label = { Text("Client") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(space16))
                    FilterChip(
                        selected = role == "PRESTATAIRE",
                        onClick = { role = "PRESTATAIRE" },
                        label = { Text("Prestataire") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(space16))

                // Champs conditionnels
                if (role == "CLIENT") {
                    KolaborTextField(
                        value = adresse,
                        onValueChange = { adresse = it },
                        label = "Adresse par défaut",
                        placeholder = "Port-au-Prince"
                    )
                } else {
                    KolaborTextField(
                        value = competences,
                        onValueChange = { competences = it },
                        label = "Compétences",
                        placeholder = "Plomberie, Électricité..."
                    )
                    Spacer(modifier = Modifier.height(space16))
                    KolaborTextField(
                        value = tarifHoraire,
                        onValueChange = { tarifHoraire = it },
                        label = "Tarif horaire (Gdes)",
                        placeholder = "500",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(space16))
                    KolaborTextField(
                        value = zoneIntervention,
                        onValueChange = { zoneIntervention = it },
                        label = "Zone d'intervention",
                        placeholder = "Port-au-Prince, Pétion-Ville"
                    )
                }

                Spacer(modifier = Modifier.height(space32))

                // Bouton S'inscrire
                KolaborPrimaryButton(
                    text = if (isLoading) "Inscription en cours..." else "S'inscrire",
                    onClick = {
                        if (nom.isBlank() || email.isBlank() || motDePasse.isBlank() || confirmMotDePasse.isBlank()) {
                            errorMessage = "Veuillez remplir tous les champs obligatoires."
                            return@KolaborPrimaryButton
                        }
                        if (!passwordsMatch) {
                            errorMessage = "Les mots de passe ne correspondent pas."
                            return@KolaborPrimaryButton
                        }
                        if (role == "PRESTATAIRE" && (competences.isBlank() || tarifHoraire.isBlank() || zoneIntervention.isBlank())) {
                            errorMessage = "Veuillez remplir toutes les compétences, tarif et zone."
                            return@KolaborPrimaryButton
                        }
                        isLoading = true
                        errorMessage = null

                        val request = RegisterRequest(
                            nom = nom,
                            email = email,
                            motDePasse = motDePasse,
                            role = role,
                            adresseParDefaut = if (role == "CLIENT") adresse else null,
                            competences = if (role == "PRESTATAIRE") competences else null,
                            tarifHoraire = if (role == "PRESTATAIRE") tarifHoraire.toDoubleOrNull() else null,
                            zoneIntervention = if (role == "PRESTATAIRE") zoneIntervention else null
                        )

                        coroutineScope.launch {
                            try {
                                val success = authRepository.register(request)
                                if (success) {
                                    onRegisterSuccess()
                                } else {
                                    errorMessage = "Erreur lors de l'inscription. Email peut-être déjà utilisé."
                                }
                            } catch (e: Exception) {
                                Log.e("RegisterScreen", "Erreur", e)
                                errorMessage = "Erreur réseau. Vérifiez votre connexion."
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading && passwordsMatch && motDePasse.isNotBlank()
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(space16))
                    Text(
                        text = errorMessage!!,
                        color = ErrorColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(space16))

                // Lien vers la connexion
                Text(
                    text = "Déjà un compte ? Connectez-vous",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NavyPrimary,
                    modifier = Modifier
                        .clickable { onNavigateToLogin() }
                )

                // Petit espace en bas pour le confort de scroll
                Spacer(modifier = Modifier.height(space24))
            }
        }
    }
}