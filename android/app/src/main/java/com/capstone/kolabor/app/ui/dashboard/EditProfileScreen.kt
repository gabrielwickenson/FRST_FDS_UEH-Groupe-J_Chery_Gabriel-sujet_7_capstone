package com.capstone.kolabor.app.ui.dashboard

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.capstone.kolabor.app.data.model.UpdateUserRequest
import com.capstone.kolabor.app.data.model.User
import com.capstone.kolabor.app.data.repository.UserRepository
import com.kolabor.app.ui.components.KolaborPrimaryButton
import com.kolabor.app.ui.components.KolaborTextField
import com.capstone.serviceplatform.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    user: User,
    onBack: () -> Unit,
    onProfileUpdated: (User) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { UserRepository(context) }

    // États des champs
    var nom by remember { mutableStateOf(user.nom) }
    var telephone by remember { mutableStateOf(user.telephone ?: "") }
    var adresse by remember { mutableStateOf("") }
    var competences by remember { mutableStateOf("") }
    var tarifHoraire by remember { mutableStateOf("") }
    var zoneIntervention by remember { mutableStateOf("") }

    // État pour savoir si on a déjà chargé les données spécifiques au rôle
    var isDataLoaded by remember { mutableStateOf(false) }

    // Charger les données spécifiques selon le rôle
    LaunchedEffect(Unit) {
        if (!isDataLoaded) {
            // Ici on pourrait récupérer les données complètes du profil (client ou prestataire)
            // Mais on suppose que user contient déjà tout (grâce à l'endpoint GET)
            // Pour l'instant, on laisse vide, on pourra améliorer plus tard
            isDataLoaded = true
        }
    }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isPrestataire = user.role == "PRESTATAIRE"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Titre + bouton retour
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← Annuler", color = NavyPrimary)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Modifier mon profil",
                style = MaterialTheme.typography.headlineSmall,
                color = NavyPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Champs modifiables
        KolaborTextField(
            value = nom,
            onValueChange = { nom = it },
            label = "Nom complet",
            placeholder = user.nom
        )
        Spacer(modifier = Modifier.height(16.dp))

        KolaborTextField(
            value = telephone,
            onValueChange = { telephone = it },
            label = "Téléphone",
            placeholder = user.telephone ?: "Non renseigné",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Champs spécifiques selon le rôle
        if (isPrestataire) {
            KolaborTextField(
                value = competences,
                onValueChange = { competences = it },
                label = "Compétences",
                placeholder = "Plomberie, Électricité..."
            )
            Spacer(modifier = Modifier.height(16.dp))

            KolaborTextField(
                value = tarifHoraire,
                onValueChange = { tarifHoraire = it },
                label = "Tarif horaire (Gdes)",
                placeholder = "500",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))

            KolaborTextField(
                value = zoneIntervention,
                onValueChange = { zoneIntervention = it },
                label = "Zone d'intervention",
                placeholder = "Port-au-Prince, Pétion-Ville..."
            )
        } else {
            // Client
            KolaborTextField(
                value = adresse,
                onValueChange = { adresse = it },
                label = "Adresse par défaut",
                placeholder = "Votre adresse"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Message d'erreur
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = ErrorColor,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Bouton Enregistrer
        KolaborPrimaryButton(
            text = if (isLoading) "Enregistrement..." else "Enregistrer les modifications",
            onClick = {
                // Validation
                if (nom.isBlank()) {
                    errorMessage = "Le nom ne peut pas être vide"
                    return@KolaborPrimaryButton
                }

                isLoading = true
                errorMessage = null

                // Construire la requête
                val request = UpdateUserRequest(
                    nom = nom,
                    telephone = telephone.ifBlank { null },
                    adresseParDefaut = if (!isPrestataire && adresse.isNotBlank()) adresse else null,
                    competences = if (isPrestataire && competences.isNotBlank()) competences else null,
                    tarifHoraire = if (isPrestataire && tarifHoraire.isNotBlank()) tarifHoraire.toDoubleOrNull() else null,
                    zoneIntervention = if (isPrestataire && zoneIntervention.isNotBlank()) zoneIntervention else null
                )

                coroutineScope.launch {
                    try {
                        val updatedUser = repository.updateUser(user.id, request)
                        if (updatedUser != null) {
                            Toast.makeText(context, "Profil mis à jour avec succès", Toast.LENGTH_LONG).show()
                            onProfileUpdated(updatedUser)
                        } else {
                            errorMessage = "Erreur lors de la mise à jour. Vérifiez vos données."
                        }
                    } catch (e: Exception) {
                        errorMessage = "Erreur réseau : ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}