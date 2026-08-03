package com.capstone.kolabor.app.ui.client

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.capstone.kolabor.app.data.model.Avis
import com.capstone.kolabor.app.data.model.Prestataire
import com.capstone.kolabor.app.data.repository.AvisRepository
import com.capstone.kolabor.app.data.repository.DisponibiliteRepository
import com.kolabor.app.ui.components.KolaborPrimaryButton
import com.capstone.serviceplatform.app.ui.theme.*
import com.kolabor.app.ui.theme.*
import com.kolabor.app.ui.theme.space16
import com.capstone.kolabor.app.data.model.Disponibilite
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.background
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.capstone.kolabor.app.utils.normalizePhotoUrl


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestataireDetailScreen(
    prestataire: Prestataire,
    onBack: () -> Unit,
    onReserver: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val dispoRepo = remember { DisponibiliteRepository(context) }
    val avisRepo = remember { AvisRepository(context) }

    var disponibilites by remember { mutableStateOf<List<Disponibilite>?>(null) }
    var avis by remember { mutableStateOf<List<Avis>?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        val dispo = dispoRepo.getDisponibilitesByPrestataire(prestataire.id)
        val avisData = avisRepo.getAvisByPrestataire(prestataire.id)
        disponibilites = dispo
        avis = avisData
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil du prestataire", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = space24, vertical = space16)
        ) {
            // Photo + Nom + Note
            item {
                // Dans PrestataireDetailScreen.kt
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(NavyLight.copy(alpha = 0.3f))
                    ) {
                        if (prestataire.photo != null && prestataire.photo.isNotEmpty()) {
                            val fullUrl = normalizePhotoUrl(prestataire.photo)
                            AsyncImage(
                                model = fullUrl,
                                contentDescription = "Photo de ${prestataire.nom}",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = NavyPrimary,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = prestataire.nom,
                            style = MaterialTheme.typography.headlineSmall,
                            color = NavyPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = if (index < (prestataire.moyenneNotes?.toInt() ?: 0))
                                        Icons.Filled.Star
                                    else
                                        Icons.Outlined.Star,
                                    contentDescription = null,
                                    tint = if (index < (prestataire.moyenneNotes?.toInt() ?: 0))
                                        GreenPrimary else Gray300,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = " ${prestataire.moyenneNotes?.toString() ?: "N/A"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Gray600
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(space16))
            }

            // Infos générales
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Gray50),
                    shape = MaterialTheme.shapes.small
                ) {
                    Column(modifier = Modifier.padding(space16)) {
                        Text("Compétences", style = MaterialTheme.typography.labelLarge, color = Gray600)
                        Text(prestataire.competences ?: "Non spécifiées", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(space8))
                        Text("Tarif horaire", style = MaterialTheme.typography.labelLarge, color = Gray600)
                        Text("${prestataire.tarifHoraire?.toString() ?: "N/A"} Gdes", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(space8))
                        Text("Zone d'intervention", style = MaterialTheme.typography.labelLarge, color = Gray600)
                        Text(prestataire.zoneIntervention ?: "Non spécifiée", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(modifier = Modifier.height(space16))
            }

            // Disponibilités
            item {
                Text("Disponibilités", style = MaterialTheme.typography.titleMedium, color = NavyPrimary)
                Spacer(modifier = Modifier.height(space8))
            }
            if (isLoading) {
                item { CircularProgressIndicator(modifier = Modifier.size(24.dp), color = NavyPrimary) }
            } else if (disponibilites.isNullOrEmpty()) {
                item { Text("Aucune disponibilité renseignée.", style = MaterialTheme.typography.bodyMedium, color = Gray500) }
            } else {
                items(disponibilites!!) { dispo ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = space4),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(dispo.jour, style = MaterialTheme.typography.bodyMedium, color = Gray700)
                        Text("${dispo.heureDebut} - ${dispo.heureFin}", style = MaterialTheme.typography.bodyMedium, color = Gray700)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(space16)) }

            // Avis
            // --- SECTION AVIS ---
            item {
                Text("Avis des clients", style = MaterialTheme.typography.titleMedium, color = NavyPrimary)
                Spacer(modifier = Modifier.height(space8))
            }
            if (isLoading) {
                item { CircularProgressIndicator(modifier = Modifier.size(24.dp), color = NavyPrimary) }
            } else if (avis.isNullOrEmpty()) {
                item { Text("Aucun avis pour le moment.", style = MaterialTheme.typography.bodyMedium, color = Gray500) }
            } else {
                items(avis!!) { avisItem ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = space4),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(space12),
                            verticalAlignment = Alignment.Top
                        ) {
                            // 📸 PHOTO DU CLIENT (placeholder)
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(NavyLight.copy(alpha = 0.3f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Photo du client",
                                    tint = NavyPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(space12))

                            // Contenu de l'avis
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = avisItem.clientNom ?: "Client",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = NavyPrimary
                                    )
                                    Row {
                                        repeat(5) { index ->
                                            Icon(
                                                imageVector = if (index < avisItem.note)
                                                    Icons.Filled.Star
                                                else
                                                    Icons.Outlined.Star,
                                                contentDescription = null,
                                                tint = if (index < avisItem.note)
                                                    GreenPrimary else Gray300,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = avisItem.commentaire ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Gray600
                                )
                                Text(
                                    text = avisItem.date?.let { formatDate(it) } ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Gray400
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(space24)) }

            // Bouton Réserver
            item {
                KolaborPrimaryButton(
                    text = "Réserver ce prestataire",
                    onClick = onReserver,  // ✅ Ce callback déclenche le Bottom Sheet
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(space16))
            }
        }
    }
}