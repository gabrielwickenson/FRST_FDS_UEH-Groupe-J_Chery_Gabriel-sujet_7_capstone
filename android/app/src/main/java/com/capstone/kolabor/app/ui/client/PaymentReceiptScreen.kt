package com.capstone.kolabor.app.ui.client

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.kolabor.app.R
import com.capstone.kolabor.app.data.model.Reservation
import com.capstone.serviceplatform.app.ui.theme.*
import com.kolabor.app.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentReceiptScreen(
    reservation: Reservation,
    modePaiement: String,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val transactionId = remember { UUID.randomUUID().toString().take(8).uppercase() }
    val date = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()) }

    val (badgeText, badgeColor, modeLabel) = when (modePaiement) {
        "mobile_money" -> Triple("MON", Color(0xFFE4002B), "MonCash")
        "card" -> Triple("CB", NavyPrimary, "Carte bancaire")
        "cash" -> Triple("💵", GreenPrimary, "Espèces")
        else -> Triple("?", Gray500, modePaiement)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reçu",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                },
                actions = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = NavyPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Gray50
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = space24, vertical = space16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ─── Icône de confirmation ───
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(GreenLightest),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(space16))

            Text(
                text = "Paiement réussi",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Transaction #$transactionId",
                style = MaterialTheme.typography.bodyMedium,
                color = Gray500
            )

            Spacer(modifier = Modifier.height(space24))

            // ─── Carte : Détails de la prestation ───
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(space16)
            ) {
                Text(
                    text = "Détails de la prestation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Spacer(modifier = Modifier.height(space12))

                ReceiptRow("Service", reservation.service?.nom ?: "Non spécifié")
                ReceiptRow("Prestataire", reservation.prestataire?.nom ?: "Non spécifié")
                ReceiptRow("Date", formatDate(reservation.dateHeure))
                ReceiptRow("Adresse", reservation.adresse ?: "Non spécifiée")

                Spacer(modifier = Modifier.height(space8))

                // ─── Badge mode de paiement ───
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Mode de paiement",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray600,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeColor)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(space8))
                    Text(
                        text = modeLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Gray900
                    )
                }

                HorizontalDivider(color = Gray200, thickness = 1.dp, modifier = Modifier.padding(vertical = space12))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Montant payé",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Text(
                        text = "${reservation.montant?.toString() ?: "0"} Gdes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                }

                Spacer(modifier = Modifier.height(space8))
                Text(
                    text = "Payé le $date",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray400
                )
            }

            Spacer(modifier = Modifier.height(space24))

            // ─── Boutons d'action ───
            Button(
                onClick = {
                    Toast.makeText(context, "Fonctionnalité de téléchargement à venir", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = Color.White)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(space8))
                Text("Télécharger le reçu", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(space8))

            OutlinedButton(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Gray300),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Gray700)
            ) {
                Text("Fermer", fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(space24))
        }
    }
}

/**
 * Ligne label/valeur du reçu — même esthétique que SummaryRow du PaymentScreen.
 */
@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Gray600)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Gray900,
            fontWeight = FontWeight.Medium
        )
    }
}