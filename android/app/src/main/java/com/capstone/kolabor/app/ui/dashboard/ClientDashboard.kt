package com.capstone.kolabor.app.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.capstone.kolabor.app.ui.client.ReservationsScreen
import com.capstone.kolabor.app.ui.client.SearchScreen
import com.capstone.serviceplatform.app.ui.theme.*
import com.kolabor.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDashboard(
    onLogout: () -> Unit,
    clientId: Long,
    onNavigateToBook: (Long) -> Unit  // ✅ Nouveau paramètre ajouté
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val serviceRepo = remember { ServiceRepository(context) }

    var selectedTab by remember { mutableStateOf(0) }

    // Charger les services pour les chips
    var services by remember { mutableStateOf<List<Service>>(emptyList()) }
    LaunchedEffect(Unit) {
        val data = serviceRepo.getServices()
        if (data != null) {
            services = data
        }
    }

    // ✅ Variable correcte pour le filtre (utilisée partout)
    var selectedServiceFilter by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Kolabor",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Déconnexion",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                val tabs = listOf("Accueil", "Services", "Réservations", "Profil")
                val icons = listOf(
                    Icons.Filled.Home,
                    Icons.Filled.Search,
                    Icons.Filled.History,
                    Icons.Filled.Person
                )

                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                icons[index],
                                contentDescription = title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyPrimary,
                            selectedTextColor = NavyPrimary,
                            unselectedIconColor = Gray500,
                            unselectedTextColor = Gray500
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> {
                    // 🏠 ONGLET ACCUEIL
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = space24, vertical = space24)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_kolabor_svg),
                            contentDescription = "Logo Kolabor",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(space16))

                        Text(
                            text = "Bonjour, Client !",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NavyPrimary
                        )
                        Text(
                            text = "Trouvez et gérez vos services en un clin d'œil.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Gray500
                        )
                        Spacer(modifier = Modifier.height(space32))

                        if (services.isNotEmpty()) {
                            Text(
                                "Filtres rapides",
                                style = MaterialTheme.typography.labelLarge,
                                color = Gray600
                            )
                            Spacer(modifier = Modifier.height(space8))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(space8)
                            ) {
                                services.take(4).forEach { service ->
                                    FilterChip(
                                        selected = selectedServiceFilter == service.nom, // ✅ Ici
                                        onClick = {
                                            selectedServiceFilter = if (selectedServiceFilter == service.nom) null else service.nom // ✅ Ici
                                            selectedTab = 1 // Basculer vers Services
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
                            Spacer(modifier = Modifier.height(space24))
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Gray50),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(space16)
                            ) {
                                Text(
                                    "Accès rapide",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = NavyPrimary
                                )
                                Spacer(modifier = Modifier.height(space16))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    DashboardActionCard(
                                        icon = Icons.Default.Search,
                                        label = "Rechercher",
                                        onClick = { selectedTab = 1 },
                                        modifier = Modifier.weight(1f)
                                    )
                                    DashboardActionCard(
                                        icon = Icons.Default.History,
                                        label = "Réservations",
                                        onClick = { selectedTab = 2 },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(space32))
                        Text(
                            "© 2026 Kolabor",
                            style = MaterialTheme.typography.bodySmall,
                            color = Gray500
                        )
                    }
                }
                1 -> {
                    // 🔍 ONGLET SERVICES
                    SearchScreen(
                        onBack = {
                            selectedTab = 0
                        },
                        onNavigateToBook = onNavigateToBook,  // ✅ On passe le callback
                        initialService = selectedServiceFilter
                    )
                }
                2 -> {
                    // 📋 ONGLET RÉSERVATIONS
                    ReservationsScreen(
                        onBack = {
                            selectedTab = 0
                        },
                        clientId = clientId
                    )
                }
                3 -> {
                    // 👤 ONGLET PROFIL
                    ProfileScreen(onLogout = onLogout)
                }
            }
        }
    }
}

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

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(space24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profil",
            modifier = Modifier.size(80.dp),
            tint = NavyPrimary
        )
        Spacer(modifier = Modifier.height(space16))
        Text(
            text = "Espace Profil",
            style = MaterialTheme.typography.headlineMedium,
            color = NavyPrimary
        )
        Text(
            text = "Gérez vos informations personnelles",
            style = MaterialTheme.typography.bodyLarge,
            color = Gray500
        )
        Spacer(modifier = Modifier.height(space32))
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ErrorColor,
                contentColor = Color.White
            )
        ) {
            Text("Déconnexion", style = MaterialTheme.typography.labelLarge)
        }
    }
}