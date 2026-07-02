package com.capstone.kolabor.app.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.capstone.kolabor.app.data.model.Prestataire
import com.kolabor.app.R
import com.capstone.kolabor.app.data.model.Service
import com.capstone.kolabor.app.data.repository.PrestataireRepository
import com.capstone.kolabor.app.data.repository.ServiceRepository
import com.capstone.kolabor.app.ui.client.PrestataireCard
import com.capstone.kolabor.app.ui.client.ReservationsScreen
import com.capstone.kolabor.app.ui.client.SearchScreen
import com.capstone.serviceplatform.app.ui.theme.*
import com.kolabor.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDashboard(
    onLogout: () -> Unit,
    clientId: Long,
    onNavigateToBook: (Long) -> Unit,
    userName: String = "Client"   // ✅ nouveau paramètre
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val serviceRepo = remember { ServiceRepository(context) }

    var selectedTab by remember { mutableStateOf(0) }

    var services by remember { mutableStateOf<List<Service>>(emptyList()) }
    LaunchedEffect(Unit) {
        val data = serviceRepo.getServices()
        if (data != null) {
            services = data
        }
    }

    var selectedServiceFilter by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.logo_kolabor_svg),
                        contentDescription = "Logo Kolabor",
                        modifier = Modifier
                            .height(32.dp)
                            .width(120.dp),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(GreenPrimary) // ✅ Logo en vert sur fond NavyPrimary
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
                        icon = { Icon(icons[index], contentDescription = title, modifier = Modifier.size(24.dp)) },
                        label = { Text(title, style = MaterialTheme.typography.labelMedium) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (selectedTab) {
                0 -> {
                    // 🏠 ONGLET ACCUEIL – LISTE DES PRESTATAIRES
                    var searchQuery by remember { mutableStateOf("") }
                    var prestataires by remember { mutableStateOf<List<Prestataire>>(emptyList()) }
                    var isLoadingPrestataires by remember { mutableStateOf(true) }
                    val prestataireRepo = remember { PrestataireRepository(context) }

                    // Charger les prestataires
                    LaunchedEffect(Unit) {
                        isLoadingPrestataires = true
                        val data = prestataireRepo.searchPrestataires(service = null, noteMin = null, zone = null)
                        if (data != null) {
                            prestataires = data
                        }
                        isLoadingPrestataires = false
                    }

                    // Filtrer les prestataires
                    val filteredPrestataires by remember {
                        derivedStateOf {
                            var list = prestataires
                            if (selectedServiceFilter != null) {
                                list = list.filter {
                                    it.competences?.contains(selectedServiceFilter!!, ignoreCase = true) == true
                                }
                            }
                            if (searchQuery.isNotBlank()) {
                                list = list.filter {
                                    it.nom.contains(searchQuery, ignoreCase = true) ||
                                            (it.competences?.contains(searchQuery, ignoreCase = true) == true) ||
                                            (it.zoneIntervention?.contains(searchQuery, ignoreCase = true) == true)
                                }
                            }
                            list
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = space24, vertical = space24)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Message de bienvenue
                        Text(
                            text = "Bienvenue, $userName ",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NavyPrimary
                        )
                        Text(
                            text = "Trouvez et gérez vos services en un clin d'œil.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Gray500
                        )
                        Spacer(modifier = Modifier.height(space24))

                        // ✅ 1. CHAMP DE RECHERCHE (maintenant en premier)
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Rechercher un prestataire") },
                            placeholder = { Text("Nom, compétence, zone...", color = Gray500) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = NavyLight
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
                            shape = MaterialTheme.shapes.small,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                        )
                        Spacer(modifier = Modifier.height(space16))

                        // ✅ 2. CHIPS DE FILTRE RAPIDE (maintenant en second)
                        if (services.isNotEmpty()) {
                            Text(
                                "Filtres rapides",
                                style = MaterialTheme.typography.labelLarge,
                                color = Gray600,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(space8))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(space8)
                            ) {
                                services.take(5).forEach { service ->
                                    FilterChip(
                                        selected = selectedServiceFilter == service.nom,
                                        onClick = {
                                            selectedServiceFilter = if (selectedServiceFilter == service.nom) null else service.nom
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

                        // 📋 Liste des prestataires (inchangée)
                        if (isLoadingPrestataires) {
                            CircularProgressIndicator(modifier = Modifier.size(40.dp), color = NavyPrimary)
                            Spacer(modifier = Modifier.height(space16))
                        } else if (filteredPrestataires.isEmpty()) {
                            Text(
                                text = if (searchQuery.isBlank() && selectedServiceFilter == null)
                                    "Aucun prestataire disponible"
                                else
                                    "Aucun prestataire ne correspond à vos critères",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Gray500
                            )
                            Spacer(modifier = Modifier.height(space16))
                        } else {
                            Text(
                                text = "Prestataires disponibles (${filteredPrestataires.size})",
                                style = MaterialTheme.typography.labelLarge,
                                color = Gray600,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(space8))

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(350.dp),
                                verticalArrangement = Arrangement.spacedBy(space8)
                            ) {
                                items(filteredPrestataires) { prestataire ->
                                    PrestataireCard(
                                        prestataire = prestataire,
                                        onClick = {
                                            onNavigateToBook(prestataire.id)
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(space16))
                        }

                        Spacer(modifier = Modifier.height(space32))
                        Text("© 2026 Kolabor", style = MaterialTheme.typography.bodySmall, color = Gray500)
                    }
                }
                1 -> {
                    SearchScreen(
                        onBack = { selectedTab = 0 },
                        onNavigateToBook = onNavigateToBook,
                        initialService = selectedServiceFilter
                    )
                }
                2 -> {
                    ReservationsScreen(
                        onBack = { selectedTab = 0 },
                        clientId = clientId
                    )
                }
                3 -> {
                    ProfileScreen(onLogout = onLogout)
                }
            }
        }
    }
}


// ✅ Composant ServiceGridItem (défini ici)
@Composable
fun ServiceGridItem(service: Service, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = NavyPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(space4))
            Text(
                text = service.nom,
                style = MaterialTheme.typography.bodyMedium,
                color = NavyPrimary,
                maxLines = 1
            )
        }
    }
}

// 🔧 DashboardActionCard (inchangé)
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
            Icon(icon, contentDescription = label, tint = NavyPrimary, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(space8))
            Text(label, style = MaterialTheme.typography.labelLarge, color = NavyPrimary)
        }
    }
}

// 👤 ProfileScreen (inchangé)
@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(space24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Person, contentDescription = "Profil", modifier = Modifier.size(80.dp), tint = NavyPrimary)
        Spacer(modifier = Modifier.height(space16))
        Text("Espace Profil", style = MaterialTheme.typography.headlineMedium, color = NavyPrimary)
        Text("Gérez vos informations personnelles", style = MaterialTheme.typography.bodyLarge, color = Gray500)
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