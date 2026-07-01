package com.capstone.kolabor.app.ui.client

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.kolabor.app.data.model.Prestataire
import com.capstone.kolabor.app.data.repository.PrestataireRepository
import com.kolabor.app.ui.components.KolaborPrimaryButton
import com.capstone.serviceplatform.app.ui.theme.*
import com.kolabor.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(onBack: () -> Unit,
                 onNavigateToBook: (Long) -> Unit,// ✅ nouveau callback
                 initialService: String? = null
){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { PrestataireRepository(context) }

    var service by remember { mutableStateOf(initialService ?: "") }   // ✅ initialisation
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
        Spacer(modifier = Modifier.height(space8))
        TextButton(onClick = onBack) {
            Text("← Retour ", color = NavyPrimary, style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(space16))

        Text(
            text = "RECHERCHE",
            style = MaterialTheme.typography.headlineSmall,
            color = NavyPrimary,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(space24))

        // 🔍 Champ service
        OutlinedTextField(
            value = service,
            onValueChange = { service = it },
            label = { Text("Quel service cherchez-vous ?", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("Plombier, électricien...", style = MaterialTheme.typography.bodyMedium, color = Gray500) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = NavyLight
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = NavyPrimary,
                unfocusedIndicatorColor = NavyLight,
                focusedLabelColor = NavyPrimary,
                unfocusedLabelColor = Gray600,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Gray900,
                unfocusedTextColor = Gray900,
                errorIndicatorColor = ErrorColor,
                errorLabelColor = ErrorColor
            ),
            shape = MaterialTheme.shapes.small,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(space16))

        // Champ zone
        OutlinedTextField(
            value = zone,
            onValueChange = { zone = it },
            label = { Text("Ville ", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("Port-au-Prince, Pétion-Ville...", style = MaterialTheme.typography.bodyMedium, color = Gray500) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = NavyLight
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = NavyPrimary,
                unfocusedIndicatorColor = NavyLight,
                focusedLabelColor = NavyPrimary,
                unfocusedLabelColor = Gray600,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Gray900,
                unfocusedTextColor = Gray900,
                errorIndicatorColor = ErrorColor,
                errorLabelColor = ErrorColor
            ),
            shape = MaterialTheme.shapes.small,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(space16))

        //  Champ note minimale
        OutlinedTextField(
            value = noteMin,
            onValueChange = { noteMin = it },
            label = { Text("Note minimale", style = MaterialTheme.typography.bodyMedium) },
            placeholder = { Text("4", style = MaterialTheme.typography.bodyMedium, color = Gray500) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = NavyLight
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = NavyPrimary,
                unfocusedIndicatorColor = NavyLight,
                focusedLabelColor = NavyPrimary,
                unfocusedLabelColor = Gray600,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Gray900,
                unfocusedTextColor = Gray900,
                errorIndicatorColor = ErrorColor,
                errorLabelColor = ErrorColor
            ),
            shape = MaterialTheme.shapes.small,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(space24))

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
                        //  Conversion explicite en Double
                        val note = noteMin.toDoubleOrNull()
                        val data = repository.searchPrestataires(
                            service = service.ifBlank { null },
                            noteMin = note,  // Double? (accepté par le repository)
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

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(space8)
        ) {
            items(results) { prestataire ->
                PrestataireCard(
                    prestataire = prestataire,
                    onClick = { onNavigateToBook(prestataire.id) }   // ✅ appel du callback
                )
            }
        }
    }
}

@Composable
fun PrestataireCard(prestataire: Prestataire,
                    onClick: () -> Unit   // ✅ nouveau paramètre
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },   // ✅ rendre la carte cliquable,
        colors = CardDefaults.cardColors(containerColor = Gray50),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.small
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
                style = MaterialTheme.typography.bodyMedium,
                color = Gray700
            )
            Text(
                text = "Tarif: ${prestataire.tarifHoraire?.toString() ?: "Non spécifié"} Gdes/h",
                style = MaterialTheme.typography.bodyMedium,
                color = Gray700
            )
            Text(
                text = "Zone: ${prestataire.zoneIntervention ?: "Non spécifiée"}",
                style = MaterialTheme.typography.bodyMedium,
                color = Gray700
            )
            Text(
                text = "Note: ${prestataire.moyenneNotes?.toString() ?: "Pas encore d'avis"}",
                style = MaterialTheme.typography.bodyMedium,
                color = GreenPrimary
            )
        }
    }
}