package com.capstone.kolabor.app.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kolabor.app.R
import com.capstone.kolabor.app.data.model.Service
import com.capstone.kolabor.app.data.repository.ServiceRepository
import com.capstone.serviceplatform.app.ui.theme.*
import com.kolabor.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDashboard(
    onLogout: () -> Unit,
    onNavigateToSearch: (String?) -> Unit,
    onNavigateToReservations: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val serviceRepo = remember { ServiceRepository(context) }

    var services by remember { mutableStateOf<List<Service>>(emptyList()) }
    var selectedService by remember { mutableStateOf<String?>(null) }

    // Charger les services
    LaunchedEffect(Unit) {
        val data = serviceRepo.getServices()
        if (data != null) {
            services = data
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kolabor", color = Color.White, style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Person, contentDescription = "Déconnexion", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = space24, vertical = space32)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_kolabor_svg),
                contentDescription = "Logo Kolabor",
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(space16))

            Text("Bonjour, Client !", style = MaterialTheme.typography.headlineMedium, color = NavyPrimary)
            Text("Trouvez et gérez vos services en un clin d'œil.", style = MaterialTheme.typography.bodyLarge, color = Gray500)
            Spacer(modifier = Modifier.height(space32))

            // Chips filtres
            if (services.isNotEmpty()) {
                Text("Filtres par service", style = MaterialTheme.typography.labelLarge, color = Gray600)
                Spacer(modifier = Modifier.height(space8))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(space8)
                ) {
                    services.forEach { service ->
                        FilterChip(
                            selected = selectedService == service.nom,
                            onClick = {
                                selectedService = if (selectedService == service.nom) null else service.nom
                            },
                            label = { Text(service.nom) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NavyPrimary,
                                selectedLabelColor = Color.White,
                                disabledSelectedContainerColor = NavyPrimary
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(space16))
            } else {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = NavyPrimary)
                Spacer(modifier = Modifier.height(space16))
            }

            // Actions principales
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Gray50),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(space16),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DashboardActionCard(
                        icon = Icons.Default.Search,
                        label = "Rechercher",
                        onClick = {
                            // ✅ On passe le filtre sélectionné
                            onNavigateToSearch(selectedService)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    DashboardActionCard(
                        icon = Icons.Default.History,
                        label = "Mes réservations",
                        onClick = onNavigateToReservations,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(space32))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ErrorColor, contentColor = Color.White)
            ) {
                Text("Déconnexion", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

// DashboardActionCard reste inchangé
@Composable
fun DashboardActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(horizontal = space8),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(space16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = NavyPrimary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(space8))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = NavyPrimary
            )
        }
    }
}