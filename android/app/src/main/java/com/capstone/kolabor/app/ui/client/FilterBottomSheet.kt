package com.capstone.kolabor.app.ui.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.capstone.kolabor.app.data.model.FilterOptions
import com.capstone.serviceplatform.app.ui.theme.*
import com.kolabor.app.ui.theme.*
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilters: FilterOptions,
    onApplyFilters: (FilterOptions) -> Unit,
    onDismiss: () -> Unit
) {
    var noteMin by remember { mutableStateOf(currentFilters.noteMin) }
    var priceMin by remember { mutableStateOf(currentFilters.priceMin) }
    var priceMax by remember { mutableStateOf(currentFilters.priceMax) }
    var availability by remember { mutableStateOf(currentFilters.availability) }

    val availabilityOptions = listOf("Tous", "Aujourd'hui", "Cette semaine")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
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
                        .clip(MaterialTheme.shapes.small)
                        .background(Gray300)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = space24, vertical = space16)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Filtres avancés",
                style = MaterialTheme.typography.headlineSmall,
                color = NavyPrimary
            )
            Spacer(modifier = Modifier.height(space24))

            // 1. Note minimale
            Text("Note minimale : ${noteMin.toInt()} ⭐", style = MaterialTheme.typography.bodyMedium, color = Gray600)
            Slider(
                value = noteMin,
                onValueChange = { noteMin = round(it * 2) / 2 }, // incréments de 0.5
                valueRange = 0f..5f,
                steps = 10,
                colors = SliderDefaults.colors(
                    thumbColor = NavyPrimary,
                    activeTrackColor = NavyPrimary,
                    inactiveTrackColor = Gray300
                )
            )
            Spacer(modifier = Modifier.height(space16))

            // 2. Tranche de prix
            // 2. Tranche de prix
            Text("Tarif horaire (Gdes) : ${priceMin.toInt()} - ${priceMax.toInt()}", style = MaterialTheme.typography.bodyMedium, color = Gray600)
            // ✅ RangeSlider corrigé
            RangeSlider(
                value = priceMin.toFloat()..priceMax.toFloat(),
                onValueChange = { range ->
                    priceMin = range.start.toDouble()
                    priceMax = range.endInclusive.toDouble()
                },
                valueRange = 0f..3000f,
                steps = 30,
                colors = SliderDefaults.colors(
                    thumbColor = NavyPrimary,
                    activeTrackColor = NavyPrimary,
                    inactiveTrackColor = Gray300
                )
            )
            // 3. Disponibilité
            Text("Disponibilité", style = MaterialTheme.typography.bodyMedium, color = Gray600)
            Spacer(modifier = Modifier.height(space8))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space8)
            ) {
                availabilityOptions.forEach { option ->
                    FilterChip(
                        selected = availability == option,
                        onClick = { availability = option },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyPrimary,
                            selectedLabelColor = Color.White,
                            disabledSelectedContainerColor = NavyPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(space32))

            // Boutons Appliquer / Réinitialiser
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space16)
            ) {
                OutlinedButton(
                    onClick = {
                        onApplyFilters(
                            FilterOptions(
                                noteMin = noteMin,
                                priceMin = priceMin,
                                priceMax = priceMax,
                                availability = availability
                            )
                        )
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = NavyPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Appliquer")
                }
                TextButton(
                    onClick = {
                        // Réinitialiser
                        noteMin = 0f
                        priceMin = 0.0
                        priceMax = 3000.0
                        availability = "Tous"
                    },
                    modifier = Modifier.weight(1f).height(56.dp)
                ) {
                    Text("Réinitialiser", color = Gray500)
                }
            }
            Spacer(modifier = Modifier.height(space32))
        }
    }
}