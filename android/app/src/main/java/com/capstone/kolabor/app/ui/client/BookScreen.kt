package com.capstone.kolabor.app.ui.client

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.kolabor.app.data.model.ReservationRequest
import com.capstone.kolabor.app.data.model.Service
import com.capstone.kolabor.app.data.model.Prestataire
import com.capstone.kolabor.app.data.repository.ReservationRepository
import com.capstone.kolabor.app.data.repository.ServiceRepository
import com.kolabor.app.ui.components.KolaborPrimaryButton
import com.kolabor.app.ui.components.KolaborTextField
import com.capstone.serviceplatform.app.ui.theme.*
import com.kolabor.app.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(
    onBack: () -> Unit,
    onBookingSuccess: () -> Unit,
    prestataire: Prestataire,
    clientId: Long
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val reservationRepo = remember { ReservationRepository(context) }
    val serviceRepo = remember { ServiceRepository(context) }

    var services by remember { mutableStateOf<List<Service>>(emptyList()) }
    var selectedServiceId by remember { mutableStateOf<Long?>(null) }
    var selectedServiceName by remember { mutableStateOf<String?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var adresse by remember { mutableStateOf("") }
    var montant by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Charger les services
    LaunchedEffect(Unit) {
        val data = serviceRepo.getServices()
        if (data != null) {
            services = data
            if (services.isNotEmpty()) {
                selectedServiceId = services.first().id
                selectedServiceName = services.first().nom
            }
        } else {
            errorMessage = "Impossible de charger les services"
        }
    }

    // Pré-remplir le montant
    LaunchedEffect(prestataire.tarifHoraire) {
        prestataire.tarifHoraire?.let {
            montant = it.toString()
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = space24, vertical = space16)
            .verticalScroll(scrollState)
    ) {
        // Bouton retour
        TextButton(onClick = onBack) {
            Text("← Retour", color = NavyPrimary, style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(space16))

        // En-tête
        Text(
            text = "Nouvelle réservation",
            style = MaterialTheme.typography.headlineSmall,
            color = NavyPrimary,
            letterSpacing = 2.sp
        )
        Text(
            text = "avec ${prestataire.nom}",
            style = MaterialTheme.typography.bodyLarge,
            color = Gray600
        )
        Spacer(modifier = Modifier.height(space24))

        // ✅ Sélection du service (Dropdown au lieu des chips)
        if (services.isNotEmpty()) {
            Text("Service", style = MaterialTheme.typography.labelLarge, color = Gray600)
            Spacer(modifier = Modifier.height(space8))
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedServiceName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Choisir un service") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = NavyPrimary,
                        unfocusedIndicatorColor = NavyLight,
                        focusedLabelColor = NavyPrimary,
                        unfocusedLabelColor = Gray600,
                        focusedContainerColor = Color(0xFFFFFFFF),
                        unfocusedContainerColor = Color(0xFFFFFFFF)
                    ),
                    shape = MaterialTheme.shapes.small
                )
                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    services.forEach { service ->
                        DropdownMenuItem(
                            text = { Text(service.nom) },
                            onClick = {
                                selectedServiceId = service.id
                                selectedServiceName = service.nom
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(space16))
        } else {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = NavyPrimary)
            Spacer(modifier = Modifier.height(space16))
        }

        // Date
        OutlinedTextField(
            value = date,
            onValueChange = {},
            label = { Text("Date") },
            placeholder = { Text("Sélectionner une date", color = Gray500) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Choisir la date")
                }
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = NavyPrimary,
                unfocusedIndicatorColor = NavyLight,
                focusedLabelColor = NavyPrimary,
                unfocusedLabelColor = Gray600,
                focusedContainerColor = Color(0xFFFFFFFF),
                unfocusedContainerColor = Color(0xFFFFFFFF)
            ),
            shape = MaterialTheme.shapes.small
        )
        Spacer(modifier = Modifier.height(space16))

        // Heure
        OutlinedTextField(
            value = time,
            onValueChange = {},
            label = { Text("Heure") },
            placeholder = { Text("Sélectionner une heure", color = Gray500) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(Icons.Default.AccessTime, contentDescription = "Choisir l'heure")
                }
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = NavyPrimary,
                unfocusedIndicatorColor = NavyLight,
                focusedLabelColor = NavyPrimary,
                unfocusedLabelColor = Gray600,
                focusedContainerColor = Color(0xFFFFFFFF),
                unfocusedContainerColor = Color(0xFFFFFFFF)
            ),
            shape = MaterialTheme.shapes.small
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
                if (selectedServiceId == null || date.isBlank() || time.isBlank() || adresse.isBlank() || montant.isBlank()) {
                    Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                    return@KolaborPrimaryButton
                }

                isLoading = true
                errorMessage = null

                val dateHeure = try {
                    val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val parsed = inputFormat.parse("$date $time")
                    val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    outputFormat.format(parsed ?: Date())
                } catch (e: Exception) {
                    Toast.makeText(context, "Format date/heure invalide", Toast.LENGTH_SHORT).show()
                    isLoading = false
                    return@KolaborPrimaryButton
                }

                val request = ReservationRequest(
                    clientId = clientId,
                    prestataireId = prestataire.id,
                    serviceId = selectedServiceId!!,
                    dateHeure = dateHeure,
                    adresse = adresse,
                    montant = montant.toDoubleOrNull() ?: 0.0
                )
                Log.d("BookScreen", "📤 dateHeure envoyée : $dateHeure")
                Log.d("BookScreen", "📤 clientId=$clientId, prestataireId=${prestataire.id}, serviceId=$selectedServiceId, dateHeure=$dateHeure, adresse=$adresse, montant=$montant")
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

        Spacer(modifier = Modifier.height(space24))
    }

    // DatePicker Dialog
    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                date = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year)
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnDismissListener { showDatePicker = false }
            show()
        }
    }

    // TimePicker Dialog
    if (showTimePicker) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).apply {
            setOnDismissListener { showTimePicker = false }
            show()
        }
    }
}