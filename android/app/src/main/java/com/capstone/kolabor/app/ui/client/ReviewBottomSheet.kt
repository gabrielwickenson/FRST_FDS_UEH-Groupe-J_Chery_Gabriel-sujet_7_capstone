package com.capstone.kolabor.app.ui.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.kolabor.app.data.repository.ReservationRepository
import com.capstone.serviceplatform.app.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewBottomSheet(
    reservationId: Long,
    clientId: Long,
    prestataireNom: String,
    onDismiss: () -> Unit,
    onReviewSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { ReservationRepository(context) }

    var selectedRating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // En-tête
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Évaluer le service",
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Notez votre expérience avec $prestataireNom",
                style = MaterialTheme.typography.bodyMedium,
                color = Gray500
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ⭐ Étoiles
            Text(
                text = "Votre note",
                style = MaterialTheme.typography.labelLarge,
                color = Gray600
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(5) { index ->
                    val starIndex = index + 1
                    Icon(
                        imageVector = if (starIndex <= selectedRating) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "$starIndex étoiles",
                        tint = if (starIndex <= selectedRating) GreenPrimary else Gray300,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { selectedRating = starIndex }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 📝 Commentaire
            Text(
                text = "Votre commentaire (optionnel)",
                style = MaterialTheme.typography.labelLarge,
                color = Gray600
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = {
                    Text(
                        "Partagez votre expérience...",
                        color = Gray400,
                        style = MaterialTheme.typography.bodyMedium
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
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Message d'erreur
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = ErrorColor,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bouton Envoyer
            Button(
                onClick = {
                    if (selectedRating == 0) {
                        errorMessage = "Veuillez sélectionner une note"
                        return@Button
                    }
                    errorMessage = null
                    isLoading = true

                    coroutineScope.launch {
                        val success = repository.submitReview(
                            reservationId = reservationId,
                            clientId = clientId,
                            note = selectedRating,
                            commentaire = comment.ifBlank { null }
                        )

                        if (success) {
                            onReviewSuccess()
                        } else {
                            errorMessage = "Erreur lors de l'envoi. Réessayez."
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRating > 0) NavyPrimary else NavyPrimary.copy(alpha = 0.5f),
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
                    Text(
                        text = "Envoyer mon avis",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}