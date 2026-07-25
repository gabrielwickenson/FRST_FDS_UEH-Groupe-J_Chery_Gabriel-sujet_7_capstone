package com.capstone.kolabor.app.ui.client

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kolabor.app.R
import com.capstone.kolabor.app.data.model.Service
import com.capstone.kolabor.app.data.repository.ServiceRepository
import com.capstone.serviceplatform.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceListScreen(
    onBack: () -> Unit,
    onServiceClick: (Service) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val serviceRepository = remember { ServiceRepository(context) }

    var services by remember { mutableStateOf<List<Service>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Charger les services
    LaunchedEffect(Unit) {
        isLoading = true
        val data = serviceRepository.getServices()
        if (data != null) {
            services = data
        } else {
            errorMessage = "Impossible de charger les services"
        }
        isLoading = false
    }

    // Filtrer les services
    val filteredServices = if (searchQuery.isBlank()) {
        services
    } else {
        services.filter {
            it.nom.contains(searchQuery, ignoreCase = true) ||
                    (it.description?.contains(searchQuery, ignoreCase = true) == true) ||
                    (it.categorie?.contains(searchQuery, ignoreCase = true) == true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nos services",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Gray50, Color.White)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Champ de recherche premium
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Rechercher un service...", color = Gray500) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = NavyPrimary)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = NavyPrimary,
                    unfocusedIndicatorColor = Gray200,
                    focusedLabelColor = NavyPrimary,
                    unfocusedLabelColor = Gray500
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

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
                        Text(errorMessage!!, color = ErrorColor, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            isLoading = true
                            errorMessage = null
                            coroutineScope.launch {
                                val data = serviceRepository.getServices()
                                if (data != null) services = data else errorMessage = "Impossible de charger"
                                isLoading = false
                            }
                        }) {
                            Text("Réessayer")
                        }
                    }
                }
                filteredServices.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SearchOff, contentDescription = null, tint = Gray300, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Aucun service trouvé", color = Gray500, style = MaterialTheme.typography.headlineSmall)
                            Text("Essayez un autre mot-clé", color = Gray400, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredServices) { service ->
                            // ✅ Carte service ultra-pro
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onServiceClick(service) },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Icône/Image
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = listOf(NavyPrimary, NavyLight)
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Outlined.Build,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    // Infos
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = service.nom,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = NavyPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = service.categorie ?: "Non catégorisé",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Gray500
                                        )
                                        Text(
                                            text = service.description?.take(60) ?: "Aucune description",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Gray600,
                                            maxLines = 2
                                        )
                                    }

                                    // Bouton Voir
                                    Button(
                                        onClick = { onServiceClick(service) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = NavyPrimary,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.width(90.dp).height(36.dp)
                                    ) {
                                        Text("Voir", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}