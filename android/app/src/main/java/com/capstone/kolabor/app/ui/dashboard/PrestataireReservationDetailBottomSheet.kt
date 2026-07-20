package com.capstone.kolabor.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.kolabor.app.data.model.Reservation
import com.capstone.kolabor.app.ui.client.formatDate
import com.capstone.serviceplatform.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestataireReservationDetailBottomSheet(
    reservation: Reservation,
    onDismiss: () -> Unit
) {
    val statutColor = when (reservation.statut) {
        "EN_ATTENTE" -> Color(0xFFFFB800)
        "ACCEPTEE" -> NavyPrimary
        "EN_COURS" -> NavyPrimary
        "TERMINEE" -> GreenPrimary
        "ANNULEE" -> ErrorColor
        else -> Gray500
    }

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
                    text = "Détail de la réservation",
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

            // Informations
            DetailRow(label = "Service", value = reservation.service?.nom ?: "Inconnu")
            DetailRow(label = "Client", value = reservation.client?.nom ?: "Inconnu")
            DetailRow(label = "Date", value = formatDate(reservation.dateHeure))
            DetailRow(label = "Adresse", value = reservation.adresse ?: "Non spécifiée")
            DetailRow(label = "Montant", value = "${reservation.montant?.toString() ?: "0"} Gdes")
            DetailRow(
                label = "Statut",
                value = reservation.statut?.replace("_", " ") ?: "Inconnu",
                valueColor = statutColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Bouton de fermeture
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Fermer", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, valueColor: Color = NavyPrimary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Gray500
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = FontWeight.Medium
        )
    }
}