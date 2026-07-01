package com.capstone.kolabor.app.ui.client

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.kolabor.app.data.model.ReservationRequest
import com.capstone.kolabor.app.data.model.Service
import com.capstone.kolabor.app.data.repository.ReservationRepository
import com.capstone.kolabor.app.data.repository.ServiceRepository
import com.kolabor.app.ui.components.KolaborPrimaryButton
import com.kolabor.app.ui.components.KolaborTextField
import com.capstone.serviceplatform.app.ui.theme.*
import com.kolabor.app.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun BookScreen(
    onBack: () -> Unit,
    onBookingSuccess: () -> Unit,
    prestataireId: Long,
    clientId: Long
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val reservationRepo = remember { ReservationRepository(context) }
    val serviceRepo = remember { ServiceRepository(context) }

    var services by remember { mutableStateOf<List<Service>>(emptyList()) }
    var selectedServiceId by remember { mutableStateOf<Long?>(null) }
    var date by remember { mutableStateOf("") }
    var heure by remember { mutableStateOf("") }
    var adresse by remember { mutableStateOf("") }
    var montant by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Charger les services au premier affichage
    LaunchedEffect(Unit) {
        val data = serviceRepo.getServices()
        if (data != null) {
            services = data
            if (services.isNotEmpty()) {
                selectedServiceId = services.first().id
            }
        } else {
            errorMessage = "Impossible de charger les services"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = space24, vertical = space32)
    ) {
        // Retour
        TextButton(onClick = onBack) {
            Text("← Retour", color = NavyPrimary, style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(space16))

        Text(
            text = "NOUVELLE RÉSERVATION",
            style = MaterialTheme.typography.headlineSmall,
            color = NavyPrimary,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(space24))

        // Sélecteur de service
        if (services.isNotEmpty()) {
            Text("Service", style = MaterialTheme.typography.bodyMedium, color = Gray600)
            Spacer(modifier = Modifier.height(space8))
            Row {
                services.forEach { service ->
                    FilterChip(
                        selected = selectedServiceId == service.id,
                        onClick = { selectedServiceId = service.id },
                        label = { Text(service.nom) },
                        modifier = Modifier.padding(end = space8)
                    )
                }
            }
            Spacer(modifier = Modifier.height(space16))
        } else {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = NavyPrimary)
            Spacer(modifier = Modifier.height(space16))
        }

        // Date (format "yyyy-MM-dd")
        KolaborTextField(
            value = date,
            onValueChange = { date = it },
            label = "Date (AAAA-MM-JJ)",
            placeholder = "2025-01-15",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(space16))

        // Heure (format "HH:mm")
        KolaborTextField(
            value = heure,
            onValueChange = { heure = it },
            label = "Heure (HH:MM)",
            placeholder = "14:00",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(space16))

        // Adresse
        KolaborTextField(
            value = adresse,
            onValueChange = { adresse = it },
            label = "Adresse",
            placeholder = "123, Rue ..."
        )
        Spacer(modifier = Modifier.height(space16))

        // Montant
        KolaborTextField(
            value = montant,
            onValueChange = { montant = it },
            label = "Montant (Gdes)",
            placeholder = "1500",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(space24))

        // Bouton Confirmer
        KolaborPrimaryButton(
            text = if (isLoading) "Réservation en cours..." else "Confirmer la réservation",
            onClick = {
                if (selectedServiceId == null || date.isBlank() || heure.isBlank() || adresse.isBlank() || montant.isBlank()) {
                    Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                    return@KolaborPrimaryButton
                }

                val dateHeure = try {
                    LocalDateTime.parse("$date T $heure", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                } catch (e: Exception) {
                    Toast.makeText(context, "Format date/heure invalide", Toast.LENGTH_SHORT).show()
                    return@KolaborPrimaryButton
                }

                isLoading = true
                errorMessage = null

                val request = ReservationRequest(
                    clientId = clientId,
                    prestataireId = prestataireId,
                    serviceId = selectedServiceId!!,
                    dateHeure = dateHeure,
                    adresse = adresse,
                    montant = montant.toDoubleOrNull() ?: 0.0
                )

                coroutineScope.launch {
                    try {
                        val result = reservationRepo.createReservation(request)
                        if (result != null) {
                            Toast.makeText(context, "Réservation créée avec succès", Toast.LENGTH_LONG).show()
                            onBookingSuccess()
                        } else {
                            errorMessage = "Échec de la réservation. Vérifiez les informations."
                        }
                    } catch (e: Exception) {
                        errorMessage = "Erreur : ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading && selectedServiceId != null
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(space16))
            Text(text = errorMessage!!, color = ErrorColor, style = MaterialTheme.typography.bodyMedium)
        }
    }
}