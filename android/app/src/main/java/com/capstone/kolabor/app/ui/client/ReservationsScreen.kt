package com.capstone.kolabor.app.ui.client

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.kolabor.app.data.model.Reservation
import com.capstone.kolabor.app.data.repository.ReservationRepository
import com.capstone.serviceplatform.app.ui.theme.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationsScreen(
    onBack: () -> Unit,
    clientId: Long,
    onReservationClick: (Reservation) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { ReservationRepository(context) }

    var reservations by remember { mutableStateOf<List<Reservation>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }

    var selectedFilter by remember { mutableStateOf("Toutes") }
    val filterOptions = listOf("Toutes", "EN_ATTENTE", "ACCEPTEE", "EN_COURS", "TERMINEE", "ANNULEE")

    // Fonction de chargement réutilisable
    suspend fun loadReservations() {
        val data = repository.getReservationsByClient(clientId)
        if (data != null) {
            reservations = data
            errorMessage = if (data.isEmpty()) "Aucune réservation trouvée" else null
        } else {
            errorMessage = "Erreur de chargement. Vérifiez votre connexion."
        }
    }

    // Chargement initial
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            loadReservations()
        } catch (e: Exception) {
            Log.e("ReservationsScreen", "Erreur lors du chargement", e)
            errorMessage = "Erreur de chargement. Vérifiez votre connexion."
        } finally {
            isLoading = false
        }
    }

    val filteredReservations = if (selectedFilter == "Toutes") {
        reservations
    } else {
        reservations.filter { it.statut == selectedFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // En-tête
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = NavyPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Mes réservations",
                style = MaterialTheme.typography.headlineMedium,
                color = NavyPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Compteur
        Text(
            text = "${filteredReservations.size} réservation(s)",
            style = MaterialTheme.typography.bodyMedium,
            color = Gray500
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filtres (chips)
        if (reservations.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { statut ->
                    FilterChip(
                        selected = selectedFilter == statut,
                        onClick = { selectedFilter = statut },
                        label = {
                            Text(
                                text = when (statut) {
                                    "Toutes" -> "Toutes"
                                    "EN_ATTENTE" -> "En attente"
                                    "ACCEPTEE" -> "Acceptées"
                                    "EN_COURS" -> "En cours"
                                    "TERMINEE" -> "Terminées"
                                    else -> "Annulées"
                                },
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
        }

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
                    Icon(Icons.Default.Error, contentDescription = null, tint = ErrorColor, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage!!, color = ErrorColor, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        isLoading = true
                        errorMessage = null
                        coroutineScope.launch {
                            loadReservations()
                            isLoading = false
                        }
                    }) {
                        Text("Réessayer")
                    }
                }
            }
            filteredReservations.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Aucune réservation avec ce statut",
                        color = Gray500,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            else -> {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing),
                    onRefresh = {
                        isRefreshing = true
                        coroutineScope.launch {
                            loadReservations()
                            isRefreshing = false
                        }
                    }
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredReservations) { reservation ->
                            ReservationCard(
                                reservation = reservation,
                                onClick = { onReservationClick(reservation) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReservationCard(reservation: Reservation, onClick: () -> Unit) {
    val statutColor = when (reservation.statut) {
        "TERMINEE" -> GreenPrimary
        "ANNULEE" -> ErrorColor
        "ACCEPTEE", "EN_COURS" -> NavyPrimary
        else -> Gray500
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Ligne supérieure : service + statut
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reservation.service?.nom ?: "Service inconnu",
                    style = MaterialTheme.typography.titleMedium,
                    color = NavyPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statutColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = reservation.statut?.replace("_", " ") ?: "Inconnu",
                        style = MaterialTheme.typography.labelSmall,
                        color = statutColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Prestataire
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Gray500,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = reservation.prestataire?.nom ?: "Prestataire inconnu",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray700
                )
            }

            // Date et adresse
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Gray500,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formatDate(reservation.dateHeure),
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray600
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Gray500,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = reservation.adresse?.take(20) ?: "N/A",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray600
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Montant
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${reservation.montant?.toString() ?: "0"} Gdes",
                    style = MaterialTheme.typography.titleMedium,
                    color = NavyPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Fonction utilitaire pour formater la date (déjà existante, mais conservée)
fun formatDate(dateString: String?): String {
    if (dateString == null) return "Date non spécifiée"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}