package com.capstone.kolabor.app.ui.dashboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import com.capstone.kolabor.app.data.model.DisponibiliteRequest
import com.capstone.kolabor.app.data.repository.PrestataireRepository
import com.kolabor.app.ui.components.KolaborPrimaryButton
import com.capstone.serviceplatform.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDisponibiliteBottomSheet(
    prestataireId: Long,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { PrestataireRepository(context) }

    var selectedJour by remember { mutableStateOf("LUNDI") }
    var heureDebut by remember { mutableStateOf("08:00") }
    var heureFin by remember { mutableStateOf("18:00") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val jours = listOf("LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI", "DIMANCHE")

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
                    text = "Ajouter une disponibilité",
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

            Spacer(modifier = Modifier.height(20.dp))

            // Sélecteur du jour
            Text("Jour", style = MaterialTheme.typography.labelLarge, color = Gray600)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                jours.forEach { jour ->
                    FilterChip(
                        selected = selectedJour == jour,
                        onClick = { selectedJour = jour },
                        label = {
                            Text(
                                text = jour.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyPrimary,
                            selectedLabelColor = Color.White,
                            disabledSelectedContainerColor = NavyPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Heure début
            OutlinedTextField(
                value = heureDebut,
                onValueChange = { heureDebut = it },
                label = { Text("Heure de début") },
                placeholder = { Text("08:00", color = Gray500) },
                modifier = Modifier.fillMaxWidth(),
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

            Spacer(modifier = Modifier.height(12.dp))

            // Heure fin
            OutlinedTextField(
                value = heureFin,
                onValueChange = { heureFin = it },
                label = { Text("Heure de fin") },
                placeholder = { Text("18:00", color = Gray500) },
                modifier = Modifier.fillMaxWidth(),
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

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = ErrorColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            KolaborPrimaryButton(
                text = if (isLoading) "Ajout en cours..." else "Ajouter",
                onClick = {
                    if (heureDebut.isBlank() || heureFin.isBlank()) {
                        errorMessage = "Veuillez saisir les heures"
                        return@KolaborPrimaryButton
                    }
                    if (heureDebut >= heureFin) {
                        errorMessage = "L'heure de début doit être avant l'heure de fin"
                        return@KolaborPrimaryButton
                    }
                    isLoading = true
                    errorMessage = null

                    val request = DisponibiliteRequest(
                        jour = selectedJour,
                        heureDebut = "$heureDebut:00",
                        heureFin = "$heureFin:00"
                    )

                    coroutineScope.launch {
                        val result = repository.addDisponibilite(prestataireId, request)
                        if (result != null) {
                            Toast.makeText(context, "Disponibilité ajoutée", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        } else {
                            errorMessage = "Erreur lors de l'ajout"
                        }
                        isLoading = false
                    }
                },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}