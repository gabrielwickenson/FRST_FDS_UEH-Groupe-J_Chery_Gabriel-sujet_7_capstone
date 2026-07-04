package com.capstone.kolabor.app.ui.dashboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.capstone.kolabor.app.data.repository.UserRepository
import com.kolabor.app.ui.components.KolaborPrimaryButton
import com.capstone.serviceplatform.app.ui.theme.Gray300
import com.capstone.serviceplatform.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordBottomSheet(
    userId: Long,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { UserRepository(context) }

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Gray300)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // En-tête
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Changer le mot de passe",
                    style = MaterialTheme.typography.headlineSmall,
                    color = NavyPrimary,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .background(Gray100, RoundedCornerShape(50))
                        .size(40.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Gray600)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Ancien mot de passe
            OutlinedTextField(
                value = oldPassword,
                onValueChange = { oldPassword = it },
                label = { Text("Ancien mot de passe") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = NavyPrimary,
                    unfocusedIndicatorColor = NavyLight,
                    focusedLabelColor = NavyPrimary,
                    unfocusedLabelColor = Gray600,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nouveau mot de passe
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Nouveau mot de passe") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = NavyPrimary,
                    unfocusedIndicatorColor = NavyLight,
                    focusedLabelColor = NavyPrimary,
                    unfocusedLabelColor = Gray600,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirmation
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmer le nouveau mot de passe") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = confirmPassword.isNotBlank() && confirmPassword != newPassword,
                supportingText = {
                    if (confirmPassword.isNotBlank() && confirmPassword != newPassword) {
                        Text("Les mots de passe ne correspondent pas", color = ErrorColor)
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = NavyPrimary,
                    unfocusedIndicatorColor = NavyLight,
                    focusedLabelColor = NavyPrimary,
                    unfocusedLabelColor = Gray600,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorIndicatorColor = ErrorColor,
                    errorLabelColor = ErrorColor
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Lien "Afficher le mot de passe"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(
                        text = if (showPassword) "Masquer les mots de passe" else "Afficher les mots de passe",
                        color = NavyPrimary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = ErrorColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bouton Confirmer
            KolaborPrimaryButton(
                text = if (isLoading) "Mise à jour..." else "Confirmer",
                onClick = {
                    if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                        errorMessage = "Veuillez remplir tous les champs"
                        return@KolaborPrimaryButton
                    }
                    if (newPassword != confirmPassword) {
                        errorMessage = "Les mots de passe ne correspondent pas"
                        return@KolaborPrimaryButton
                    }
                    if (newPassword.length < 4) {
                        errorMessage = "Le nouveau mot de passe doit contenir au moins 4 caractères"
                        return@KolaborPrimaryButton
                    }
                    isLoading = true
                    errorMessage = null

                    coroutineScope.launch {
                        val success = repository.changePassword(userId, oldPassword, newPassword)
                        if (success) {
                            Toast.makeText(context, "Mot de passe mis à jour avec succès", Toast.LENGTH_LONG).show()
                            onSuccess()
                        } else {
                            errorMessage = "Échec : ancien mot de passe incorrect ou erreur réseau"
                        }
                        isLoading = false
                    }
                },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}