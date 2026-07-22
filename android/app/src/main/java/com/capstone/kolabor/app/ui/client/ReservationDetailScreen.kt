package com.capstone.kolabor.app.ui.client

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.capstone.kolabor.app.data.model.Reservation
import com.capstone.kolabor.app.utils.normalizePhotoUrl
import com.capstone.serviceplatform.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailScreen(
    reservation: Reservation,
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onReview: () -> Unit,
    clientId: Long,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var showReviewSheet by remember { mutableStateOf(false) }

    val isCancellable = reservation.statut == "EN_ATTENTE" || reservation.statut == "ACCEPTEE"
    val isReviewable = reservation.statut == "TERMINEE"
    val statutColor = when (reservation.statut) {
        "TERMINEE" -> GreenPrimary
        "ANNULEE" -> ErrorColor
        "ACCEPTEE", "EN_COURS" -> NavyPrimary
        "EN_ATTENTE" -> Color(0xFFFFB800) // Jaune/ambre
        else -> Gray500
    }
    val statutLabel = when (reservation.statut) {
        "EN_ATTENTE" -> "En attente"
        "ACCEPTEE" -> "Acceptée"
        "EN_COURS" -> "En cours"
        "TERMINEE" -> "Terminée"
        "ANNULEE" -> "Annulée"
        else -> reservation.statut ?: "Inconnu"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Détail de la réservation",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyPrimary
                ),
                actions = {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = statutColor.copy(alpha = 0.2f),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(
                            text = statutLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = statutColor,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Carte prestataire
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dans ReservationDetailScreen.kt (section "Carte prestataire")
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(NavyLight.copy(alpha = 0.2f))
                    ) {
                        val photo = reservation.prestataire?.photo
                        if (photo != null && photo.isNotEmpty()) {
                            val fullUrl = normalizePhotoUrl(photo)
                            AsyncImage(
                                model = fullUrl,
                                contentDescription = "Photo du prestataire",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = NavyPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = reservation.prestataire?.nom ?: "Prestataire",
                            style = MaterialTheme.typography.titleMedium,
                            color = NavyPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = reservation.service?.nom ?: "Service",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gray600
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${reservation.prestataire?.moyenneNotes ?: "0.0"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Gray600
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Détails
            DetailCard(
                icon = Icons.Default.CalendarToday,
                label = "Date et heure",
                value = formatDate(reservation.dateHeure)
            )
            DetailCard(
                icon = Icons.Default.LocationOn,
                label = "Adresse",
                value = reservation.adresse ?: "Non spécifiée"
            )
            DetailCard(
                icon = Icons.Default.AttachMoney,
                label = "Montant",
                value = "${reservation.montant?.toString() ?: "0"} Gdes"
            )
            DetailCard(
                icon = Icons.Default.Info,
                label = "Référence",
                value = "#${reservation.id}"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Actions
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isCancellable) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true
                                delay(500)
                                onCancel()
                                isLoading = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ErrorColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Icon(Icons.Default.Cancel, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Annuler la réservation")
                        }
                    }
                }

                if (isReviewable) {
                    Button(
                        onClick = {
                            showReviewSheet = true  // ✅ Ouvre le sheet
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.RateReview, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Laisser un avis")
                    }
                }

                OutlinedButton(
                    onClick = { /* À implémenter */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = NavyPrimary
                    )
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Contacter le prestataire")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
        // Bottom Sheet pour l'avis
        if (showReviewSheet) {
            ReviewBottomSheet(
                reservationId = reservation.id,
                clientId = clientId, // Il faut passer clientId en paramètre à ReservationDetailScreen
                prestataireNom = reservation.prestataire?.nom ?: "prestataire",
                onDismiss = {
                    showReviewSheet = false
                },
                onReviewSuccess = {
                    showReviewSheet = false
                    Toast.makeText(context, "Merci pour votre avis !", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}

@Composable
fun DetailCard(icon: ImageVector, label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Gray50),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NavyPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = Gray500
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = NavyPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}