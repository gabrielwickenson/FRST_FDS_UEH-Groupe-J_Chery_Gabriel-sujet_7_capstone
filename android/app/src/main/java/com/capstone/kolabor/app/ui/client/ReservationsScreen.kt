package com.capstone.kolabor.app.ui.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.kolabor.app.data.model.Reservation
import com.capstone.kolabor.app.data.repository.ReservationRepository
import com.capstone.serviceplatform.app.ui.theme.*
import com.kolabor.app.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReservationsScreen(onBack: () -> Unit, clientId: Long) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { ReservationRepository(context) }

    var reservations by remember { mutableStateOf<List<Reservation>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Charger les réservations au premier affichage
    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null
        val data = repository.getReservationsByClient(clientId)
        if (data != null) {
            reservations = data
            if (data.isEmpty()) {
                errorMessage = "Aucune réservation trouvée"
            }
        } else {
            errorMessage = "Erreur de chargement. Vérifiez votre connexion."
        }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = space24, vertical = space32)
    ) {
        // Bouton retour
        TextButton(onClick = onBack) {
            Text("← Retour", color = NavyPrimary, style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(space16))

        // Titre
        Text(
            text = "MES RÉSERVATIONS",
            style = MaterialTheme.typography.headlineSmall,
            color = NavyPrimary,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(space16))

        // Contenu
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NavyPrimary)
                }
            }
            errorMessage != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = errorMessage!!,
                        color = ErrorColor,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        // Recharger
                        isLoading = true
                        errorMessage = null
                        coroutineScope.launch {
                            val data = repository.getReservationsByClient(clientId)
                            if (data != null) {
                                reservations = data
                                if (data.isEmpty()) {
                                    errorMessage = "Aucune réservation trouvée"
                                }
                            } else {
                                errorMessage = "Erreur de chargement. Vérifiez votre connexion."
                            }
                            isLoading = false
                        }
                    }) {
                        Text("Réessayer")
                    }
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(space8)
                ) {
                    items(reservations) { reservation ->
                        ReservationCard(reservation = reservation)
                    }
                }
            }
        }
    }
}

@Composable
fun ReservationCard(reservation: Reservation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Gray50),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.padding(space16)
        ) {
            // Service et date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = reservation.service?.nom ?: "Service inconnu",
                    style = MaterialTheme.typography.titleMedium,
                    color = NavyPrimary
                )
                Text(
                    text = formatDate(reservation.dateHeure),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray600
                )
            }
            // Prestataire
            Text(
                text = "Prestataire: ${reservation.prestataire?.nom ?: "Non spécifié"}",
                style = MaterialTheme.typography.bodyMedium,
                color = Gray700
            )
            // Adresse
            Text(
                text = "Adresse: ${reservation.adresse ?: "Non spécifiée"}",
                style = MaterialTheme.typography.bodyMedium,
                color = Gray700
            )
            // Montant
            Text(
                text = "Montant: ${reservation.montant?.toString() ?: "Non spécifié"} Gdes",
                style = MaterialTheme.typography.bodyMedium,
                color = Gray700
            )
            // Statut
            Text(
                text = "Statut: ${reservation.statut?.replace("_", " ") ?: "Inconnu"}",
                style = MaterialTheme.typography.bodyMedium,
                color = when (reservation.statut) {
                    "TERMINEE" -> GreenPrimary
                    "ANNULEE" -> ErrorColor
                    else -> NavyPrimary
                }
            )
        }
    }
}

// Fonction utilitaire pour formater la date
fun formatDate(dateString: String?): String {
    if (dateString == null) return "Date non spécifiée"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString // fallback
    }
}