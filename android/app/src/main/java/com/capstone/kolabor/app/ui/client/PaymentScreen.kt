package com.capstone.kolabor.app.ui.client

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.capstone.kolabor.app.data.model.Reservation
import com.capstone.kolabor.app.data.repository.ReservationRepository
import com.capstone.serviceplatform.app.ui.theme.*
import com.kolabor.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    reservation: Reservation,
    clientId: Long,
    onPaymentSuccess: (Reservation, String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val reservationRepo = remember { ReservationRepository(context) }

    var selectedMode by remember { mutableStateOf("card") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvc by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }

    val montant = reservation.montant?.toDouble() ?: 0.0
    val isPayable = reservation.statut == "TERMINEE"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Paiement",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = NavyPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Gray50
    ) { paddingValues ->
        if (!isPayable) {
            PaymentNotAvailableState(
                reservation = reservation,
                paddingValues = paddingValues,
                onBack = onBack
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = space24, vertical = space16)
        ) {
            // ─── Carte : Mode de paiement ───
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(space16)
            ) {
                Text(
                    text = "Mode de paiement",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Vos informations sont chiffrées et sécurisées.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500
                )
                Spacer(modifier = Modifier.height(space16))

                PaymentMethodOption(
                    badgeText = "CB",
                    badgeColor = NavyPrimary,
                    title = "Carte bancaire",
                    subtitle = "Visa, Mastercard",
                    selected = selectedMode == "card",
                    onClick = { selectedMode = "card" }
                )
                Spacer(modifier = Modifier.height(space12))
                PaymentMethodOption(
                    badgeText = "MON",
                    badgeColor = Color(0xFFE4002B),
                    title = "MonCash",
                    subtitle = "Paiement mobile Digicel",
                    selected = selectedMode == "mobile_money",
                    onClick = { selectedMode = "mobile_money" }
                )

                AnimatedVisibility(
                    visible = selectedMode == "card",
                    enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                    exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
                ) {
                    Column(modifier = Modifier.padding(top = space16)) {
                        PaymentFieldLabel("Numéro de carte")
                        Spacer(modifier = Modifier.height(6.dp))
                        PaymentOutlinedField(
                            value = cardNumber,
                            onValueChange = { cardNumber = it },
                            placeholder = "4242 4242 4242 4242",
                            keyboardType = KeyboardType.Number
                        )
                        Spacer(modifier = Modifier.height(space16))

                        Row(horizontalArrangement = Arrangement.spacedBy(space16)) {
                            Column(modifier = Modifier.weight(1f)) {
                                PaymentFieldLabel("Expiration")
                                Spacer(modifier = Modifier.height(6.dp))
                                PaymentOutlinedField(
                                    value = cardExpiry,
                                    onValueChange = { cardExpiry = it },
                                    placeholder = "12 / 27",
                                    keyboardType = KeyboardType.Number
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                PaymentFieldLabel("CVC")
                                Spacer(modifier = Modifier.height(6.dp))
                                PaymentOutlinedField(
                                    value = cardCvc,
                                    onValueChange = { cardCvc = it },
                                    placeholder = "•••",
                                    keyboardType = KeyboardType.NumberPassword
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(space16))

                        PaymentFieldLabel("Nom sur la carte")
                        Spacer(modifier = Modifier.height(6.dp))
                        PaymentOutlinedField(
                            value = cardName,
                            onValueChange = { cardName = it },
                            placeholder = "Peter Joseph",
                            keyboardType = KeyboardType.Text
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(space16))

            // ─── Carte : Récapitulatif ───
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(space16)
            ) {
                Text(
                    text = "Récapitulatif paiement",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Spacer(modifier = Modifier.height(space16))

                SummaryRow(label = reservation.service?.nom ?: "Service", value = "${montant.toInt()} Gdes")
                SummaryRow(label = "Prestataire", value = reservation.prestataire?.nom ?: "Non spécifié")
                SummaryRow(label = "Date", value = formatDate(reservation.dateHeure))

                HorizontalDivider(color = Gray200, thickness = 1.dp, modifier = Modifier.padding(vertical = space12))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Text(
                        text = "${montant.toInt()} Gdes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                }

                Spacer(modifier = Modifier.height(space16))

                Button(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        coroutineScope.launch {
                            val success = reservationRepo.simulatePayment(
                                reservationId = reservation.id,
                                modePaiement = selectedMode,
                                clientId = clientId
                            )
                            if (success) {
                                onPaymentSuccess(reservation, selectedMode)
                            } else {
                                errorMessage = "Erreur lors du paiement. Veuillez réessayer."
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                            }
                            isLoading = false
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary,
                        disabledContainerColor = GreenPrimary.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = if (isLoading) "Traitement en cours..." else "Payer ${montant.toInt()} Gdes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                AnimatedVisibility(visible = errorMessage != null) {
                    Text(
                        text = errorMessage.orEmpty(),
                        color = ErrorColor,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = space8)
                    )
                }

                Spacer(modifier = Modifier.height(space12))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Gray400, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Paiement sécurisé · SSL",
                        style = MaterialTheme.typography.labelSmall,
                        color = Gray400
                    )
                }
            }

            Spacer(modifier = Modifier.height(space24))
        }
    }
}

/**
 * État affiché à la place du formulaire de paiement quand la réservation
 * n'a pas encore le statut TERMINEE.
 */
@Composable
private fun PaymentNotAvailableState(
    reservation: Reservation,
    paddingValues: PaddingValues,
    onBack: () -> Unit
) {
    val statusPair: Pair<String, Color> = when (reservation.statut) {
        "EN_ATTENTE" -> "En attente de confirmation" to Gray500
        "ACCEPTEE" -> "Acceptée, pas encore commencée" to NavyPrimary
        "EN_COURS" -> "Intervention en cours" to NavyPrimary
        "ANNULEE" -> "Réservation annulée" to ErrorColor
        else -> (reservation.statut?.replace("_", " ") ?: "Statut inconnu") to Gray500
    }
    val statusLabel = statusPair.first
    val statusColor = statusPair.second

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = space24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Gray100),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.HourglassEmpty,
                contentDescription = null,
                tint = Gray500,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(space16))

        Text(
            text = "Paiement pas encore disponible",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = NavyPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(space8))
        Text(
            text = "Le paiement ne sera possible qu'une fois la prestation marquée comme terminée par le prestataire.",
            style = MaterialTheme.typography.bodyMedium,
            color = Gray500,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(space24))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .padding(space16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reservation.service?.nom ?: "Service",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyPrimary
                )
                Text(
                    text = "avec ${reservation.prestataire?.nom ?: "prestataire"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500
                )
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = statusColor.copy(alpha = 0.12f)
            ) {
                Text(
                    text = statusLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(space24))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Gray300),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Gray700)
        ) {
            Text("Retour", fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun PaymentMethodOption(
    badgeText: String,
    badgeColor: Color,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) GreenLightest else Color.White)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) GreenPrimary else Gray200,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(space16),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary, unselectedColor = Gray300)
        )
        Spacer(modifier = Modifier.width(space8))
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
        Spacer(modifier = Modifier.width(space12))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = NavyPrimary)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Gray500)
        }
    }
}

@Composable
private fun PaymentFieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = Gray900
    )
}

@Composable
private fun PaymentOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Gray400) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = GreenPrimary,
            unfocusedIndicatorColor = Gray300,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Gray600)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = Gray900, fontWeight = FontWeight.Medium)
    }
}