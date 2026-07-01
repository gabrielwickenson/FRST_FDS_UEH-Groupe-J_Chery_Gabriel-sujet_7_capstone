package com.capstone.kolabor.app.ui.client

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.capstone.kolabor.app.data.model.Prestataire
import com.capstone.kolabor.app.data.repository.PrestataireRepository
import com.kolabor.app.ui.components.KolaborPrimaryButton
import com.kolabor.app.ui.components.KolaborTextField
import com.capstone.serviceplatform.app.ui.theme.ErrorColor
import com.capstone.serviceplatform.app.ui.theme.Gray50
import com.capstone.serviceplatform.app.ui.theme.GreenPrimary
import com.capstone.serviceplatform.app.ui.theme.NavyPrimary
import com.kolabor.app.ui.theme.space16
import com.kolabor.app.ui.theme.space24
import com.kolabor.app.ui.theme.space32
import com.kolabor.app.ui.theme.space8
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { PrestataireRepository(context) }

    var service by remember { mutableStateOf("") }
    var zone by remember { mutableStateOf("") }
    var noteMin by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<Prestataire>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = space24, vertical = space32)
    ) {
        // Bouton retour
        TextButton(onClick = onBack) {
            Text("← Retour", color = NavyPrimary)
        }
        Spacer(modifier = Modifier.height(space16))

        Text(
            text = "Rechercher un prestataire",
            style = MaterialTheme.typography.headlineSmall,
            color = NavyPrimary
        )
        Spacer(modifier = Modifier.height(space16))

        // Champ service
        KolaborTextField(
            value = service,
            onValueChange = { service = it },
            label = "Service recherché",
            placeholder = "Plomberie, Électricité..."
        )
        Spacer(modifier = Modifier.height(space16))

        // Champ zone
        KolaborTextField(
            value = zone,
            onValueChange = { zone = it },
            label = "Zone",
            placeholder = "Port-au-Prince, Pétion-Ville..."
        )
        Spacer(modifier = Modifier.height(space16))

        // Champ note minimale
        KolaborTextField(
            value = noteMin,
            onValueChange = { noteMin = it },
            label = "Note minimale",
            placeholder = "4",
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(space16))

        // Bouton Rechercher
        KolaborPrimaryButton(
            text = if (isLoading) "Recherche en cours..." else "Rechercher",
            onClick = {
                if (service.isBlank() && zone.isBlank() && noteMin.isBlank()) {
                    Toast.makeText(context, "Veuillez saisir au moins un critère", Toast.LENGTH_SHORT).show()
                    return@KolaborPrimaryButton
                }
                isLoading = true
                errorMessage = null
                coroutineScope.launch {
                    try {
                        val note = noteMin.toDoubleOrNull()
                        val data = repository.searchPrestataires(
                            service = service.ifBlank { null },
                            noteMin = note,
                            zone = zone.ifBlank { null }
                        )
                        if (data != null) {
                            results = data
                            if (data.isEmpty()) {
                                errorMessage = "Aucun prestataire trouvé"
                            }
                        } else {
                            errorMessage = "Erreur réseau ou serveur"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Erreur : ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(space16))
            Text(text = errorMessage!!, color = ErrorColor, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(space16))

        // Liste des résultats
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(space8)
        ) {
            items(results) { prestataire ->
                PrestataireCard(prestataire = prestataire)
            }
        }
    }
}

@Composable
fun PrestataireCard(prestataire: Prestataire) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Gray50),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(space16)
        ) {
            Text(
                text = prestataire.nom,
                style = MaterialTheme.typography.titleMedium,
                color = NavyPrimary
            )
            Text(
                text = "Compétences: ${prestataire.competences ?: "Non spécifié"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Tarif: ${prestataire.tarifHoraire ?: "Non spécifié"} Gdes/h",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Zone: ${prestataire.zoneIntervention ?: "Non spécifiée"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Note: ${prestataire.moyenneNotes ?: "Pas encore d'avis"}",
                style = MaterialTheme.typography.bodyMedium,
                color = GreenPrimary
            )
        }
    }
}