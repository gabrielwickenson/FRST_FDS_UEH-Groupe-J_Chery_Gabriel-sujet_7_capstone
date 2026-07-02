package com.capstone.kolabor.app.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import kotlin.collections.filter
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.outlined.Star
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.capstone.kolabor.app.ui.client.formatDate
import com.capstone.kolabor.app.data.model.Reservation
import com.capstone.kolabor.app.data.repository.ReservationRepository
import com.capstone.serviceplatform.app.ui.theme.*
import com.kolabor.app.ui.theme.*
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDashboard(
    onLogout: () -> Unit,
    clientId: Long,
    onNavigateToBook: (Long) -> Unit,
    userName: String = "Client",   // ✅ nouveau paramètre
    showPrestataireDetail: MutableState<Boolean>,
    selectedPrestataire: MutableState<Prestataire?>
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val serviceRepo = remember { ServiceRepository(context) }
    var selectedTab by remember { mutableStateOf(0) }
    var services by remember { mutableStateOf<List<Service>>(emptyList()) }
    var totalReservations by remember { mutableStateOf(0) }
    val prestataireRepo = remember { PrestataireRepository(context) }

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
                val tabs = listOf("Accueil", "Explorer", "Mes réservations", "Profil")
                val icons = listOf(
                    Icons.Filled.Home,
                    Icons.Filled.Search,
                    Icons.Filled.History,
                    Icons.Filled.Person
                )

                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = {
                            // ✅ BADGE SUR L'ICONE "RÉSERVATIONS"
                            if (index == 2 && totalReservations > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = ErrorColor,
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = if (totalReservations > 99) "99+" else totalReservations.toString(),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                ) {
                                    Icon(
                                        icons[index],
                                        contentDescription = title,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    icons[index],
                                    contentDescription = title,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (selectedTab) {
                0 -> {
                    // 🏠 ONGLET ACCUEIL – DASHBOARD CLIENT
                    var searchQuery by remember { mutableStateOf("") }
                    var prestataires by remember { mutableStateOf<List<Prestataire>>(emptyList()) }
                    var isLoadingPrestataires by remember { mutableStateOf(true) }
                    val prestataireRepo = remember { PrestataireRepository(context) }

                    // États pour la prochaine intervention
                    var nextReservation by remember { mutableStateOf<Reservation?>(null) }
                    var isLoadingNextReservation by remember { mutableStateOf(true) }
                    val reservationRepo = remember { ReservationRepository(context) }

                    // États pour les statistiques
                    var totalCount by remember { mutableStateOf(0) }
                    var activeCount by remember { mutableStateOf(0) }
                    var completedCount by remember { mutableStateOf(0) }

                    // État pour les prestataires recommandés
                    var topPrestataires by remember { mutableStateOf<List<Prestataire>>(emptyList()) }
                    var isLoadingTop by remember { mutableStateOf(true) }

                    // Chargement des données
                    LaunchedEffect(Unit) {
                        // Charger les prestataires
                        isLoadingPrestataires = true
                        val data = prestataireRepo.searchPrestataires(service = null, noteMin = null, zone = null)
                        if (data != null) {
                            prestataires = data
                        }
                        isLoadingPrestataires = false

                        // Charger les réservations (stats + prochaine)
                        isLoadingNextReservation = true
                        val reservations = reservationRepo.getReservationsByClient(clientId)
                        if (reservations != null) {
                            // Statistiques
                            totalCount = reservations.size
                            activeCount = reservations.count { it.statut == "ACCEPTEE" || it.statut == "EN_COURS" }
                            completedCount = reservations.count { it.statut == "TERMINEE" }
                            totalReservations = reservations.size // ✅ AJOUTE CETTE LIGNE

                            // Prochaine intervention
                            val active = reservations.filter {
                                it.statut == "ACCEPTEE" || it.statut == "EN_COURS"
                            }.sortedBy { it.dateHeure }
                            nextReservation = active.firstOrNull()
                        }
                        isLoadingNextReservation = false

                        // Charger les prestataires recommandés (note >= 4.0)
                        isLoadingTop = true
                        val topData = prestataireRepo.searchPrestataires(service = null, noteMin = 4.0, zone = null)
                        if (topData != null) {
                            // Trier par note décroissante et prendre les 5 premiers
                            val minNote = BigDecimal.valueOf(4.0)   // ✅ conversion en BigDecimal
                            topPrestataires = topData
                                .filter { it.moyenneNotes != null && it.moyenneNotes!! >= minNote }
                                .sortedByDescending { it.moyenneNotes }
                                .take(5)
                        }
                        isLoadingTop = false
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
                        // --- 1. Message de bienvenue ---
                        Text(
                            text = "Bonjour, $userName !",
                            style = MaterialTheme.typography.headlineSmall,
                            color = NavyPrimary
                        )
                        Text(
                            text = "Trouvez et gérez vos services en un clin d'œil.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = NavyPrimary
                        )
                        Spacer(modifier = Modifier.height(space24))

                        // --- 4. Champ de recherche ---
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

                        // --- 2. Prochaine intervention ---
                        if (isLoadingNextReservation) {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp), color = NavyPrimary)
                            Spacer(modifier = Modifier.height(space16))
                        } else if (nextReservation != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = GreenLightest),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Column(modifier = Modifier.padding(space16)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "📅 Votre prochaine intervention",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = GreenPrimary
                                        )
                                        Text(
                                            text = nextReservation!!.statut?.replace("_", " ") ?: "",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = when (nextReservation!!.statut) {
                                                "ACCEPTEE" -> GreenPrimary
                                                "EN_COURS" -> NavyPrimary
                                                else -> Gray500
                                            }
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(space8))
                                    Text(
                                        text = "${nextReservation!!.service?.nom ?: "Service"} avec ${nextReservation!!.prestataire?.nom ?: "prestataire"}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = NavyPrimary
                                    )
                                    Text(
                                        text = "📍 ${nextReservation!!.adresse ?: "Adresse non spécifiée"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Gray600
                                    )
                                    Text(
                                        text = "🕒 ${formatDate(nextReservation!!.dateHeure)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Gray600
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(space16))
                            // --- 3. Prestataires recommandés (Top notes) ---
                            if (isLoadingTop) {
                                CircularProgressIndicator(modifier = Modifier.size(32.dp), color = NavyPrimary)
                                Spacer(modifier = Modifier.height(space16))
                            } else if (topPrestataires.isNotEmpty()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "⭐ Prestataires recommandés",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Gray600,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(space8))
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(space8)
                                    ) {
                                        items(topPrestataires) { prestataire ->
                                            CompactPrestataireCard(
                                                prestataire = prestataire,
                                                onClick = {
                                                    onNavigateToBook(prestataire.id)
                                                }
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(space16))
                            }
                        } else {
                            Text(
                                text = "Aucune intervention à venir. Trouvez un prestataire !",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Gray500
                            )
                            Spacer(modifier = Modifier.height(space16))
                        }



                        // --- 5. Chips de filtres rapides ---
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

                        // --- 6. Liste des prestataires ---
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
                                            selectedPrestataire.value = prestataire   // ✅ Nouveau
                                            showPrestataireDetail.value = true        // ✅ Nouveau
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(space16))
                        }
                        Text("© 2026 Kolabor", style = MaterialTheme.typography.bodySmall, color = Gray500)
                    }
                }
                1 -> {
                    // 🔍 ONGLET EXPLORER – GRID CATÉGORIES + RÉSULTATS AVEC PHOTO
                    var selectedCategory by remember { mutableStateOf<String?>(null) }
                    var searchQueryExplorer by remember { mutableStateOf("") }
                    var filteredPrestatairesExplorer by remember { mutableStateOf<List<Prestataire>>(emptyList()) }
                    var isLoadingExplorer by remember { mutableStateOf(false) }

                    LaunchedEffect(selectedCategory, searchQueryExplorer) {
                        isLoadingExplorer = true
                        val data = prestataireRepo.searchPrestataires(
                            service = selectedCategory,
                            noteMin = null,
                            zone = null
                        )
                        if (data != null) {
                            filteredPrestatairesExplorer = if (searchQueryExplorer.isNotBlank()) {
                                data.filter {
                                    it.nom.contains(searchQueryExplorer, ignoreCase = true) ||
                                            (it.competences?.contains(searchQueryExplorer, ignoreCase = true) == true) ||
                                            (it.zoneIntervention?.contains(searchQueryExplorer, ignoreCase = true) == true)
                                }
                            } else {
                                data
                            }
                        } else {
                            filteredPrestatairesExplorer = emptyList()
                        }
                        isLoadingExplorer = false
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = space24, vertical = space24)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Explorer",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NavyPrimary
                        )
                        Spacer(modifier = Modifier.height(space8))
                        Text(
                            text = "Parcourez par catégorie",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Gray500
                        )
                        Spacer(modifier = Modifier.height(space16))

                        // Champ de recherche
                        OutlinedTextField(
                            value = searchQueryExplorer,
                            onValueChange = { searchQueryExplorer = it },
                            label = { Text("Rechercher un prestataire") },
                            placeholder = { Text("Nom, compétence...", color = Gray500) },
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

                        // Grille des catégories
                        if (services.isNotEmpty()) {
                            Text(
                                "Catégories",
                                style = MaterialTheme.typography.labelLarge,
                                color = Gray600,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(space8))
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                horizontalArrangement = Arrangement.spacedBy(space8),
                                verticalArrangement = Arrangement.spacedBy(space8),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            ) {
                                items(services) { service ->
                                    CategoryCard(
                                        service = service,
                                        isSelected = selectedCategory == service.nom,
                                        onClick = {
                                            selectedCategory = if (selectedCategory == service.nom) null else service.nom
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(space24))
                        }

                        // Résultats des prestataires avec photo, nom, note, tarif
                        if (isLoadingExplorer) {
                            CircularProgressIndicator(modifier = Modifier.size(40.dp), color = NavyPrimary)
                            Spacer(modifier = Modifier.height(space16))
                        } else if (filteredPrestatairesExplorer.isEmpty()) {
                            Text(
                                text = "Aucun prestataire trouvé",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Gray500
                            )
                            Spacer(modifier = Modifier.height(space16))
                        } else {
                            Text(
                                text = "Prestataires disponibles (${filteredPrestatairesExplorer.size})",
                                style = MaterialTheme.typography.labelLarge,
                                color = Gray600,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(space8))

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(space8),
                                verticalArrangement = Arrangement.spacedBy(space8),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(350.dp)
                            ) {
                                items(filteredPrestatairesExplorer) { prestataire ->
                                    ExplorerPrestataireCard(
                                        prestataire = prestataire,
                                        onClick = {
                                            selectedPrestataire.value = prestataire   // ✅ Nouveau
                                            showPrestataireDetail.value = true        // ✅ Nouveau
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(space16))
                        }
                    }
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

@Composable
fun StatCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier   // ✅ paramètre ajouté
) {
    Card(
        modifier = modifier,  // ✅ le modifier est appliqué ici
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = space12, horizontal = space8),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Gray500
            )
        }
    }
}

//  Composant ServiceGridItem (défini ici)
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

@Composable
fun ExplorerPrestataireCard(
    prestataire: Prestataire,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(space8),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Photo (placeholder si non disponible)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(NavyLight.copy(alpha = 0.3f))
            ) {
                if (prestataire.photo != null) {
                    // Si vous avez des photos réelles, chargez-les avec Coil/Glide
                    // Pour l'instant, on met une icône
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(48.dp)
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
            Spacer(modifier = Modifier.height(space4))
            Text(
                text = prestataire.nom,
                style = MaterialTheme.typography.titleMedium,
                color = NavyPrimary,
                maxLines = 1
            )
            // Note (étoiles)
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
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = " ${prestataire.moyenneNotes?.toString() ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray600
                )
            }
            Text(
                text = "Tarif: ${prestataire.tarifHoraire?.toString() ?: "N/A"} Gdes/h",
                style = MaterialTheme.typography.bodySmall,
                color = Gray600
            )
            Text(
                text = prestataire.competences?.take(15) ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = Gray500,
                maxLines = 1
            )
        }
    }
}

@Composable
fun CategoryCard(
    service: Service,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) NavyPrimary else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = service.nom,
                tint = if (isSelected) Color.White else NavyPrimary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = service.nom,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Color.White else Gray600,
                maxLines = 1
            )
        }
    }
}

@Composable
fun CompactPrestataireCard(
    prestataire: Prestataire,
    onClick: () -> Unit
) {
    // Extraire le premier service de la liste des compétences
    val mainService = prestataire.competences?.split(",")?.firstOrNull()?.trim() ?: "Service non spécifié"

    Card(
        modifier = Modifier
            .width(180.dp)
            .height(180.dp) // 🔼 Légèrement plus grand pour tout afficher
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(space12),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 📸 Photo (placeholder)
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(NavyLight.copy(alpha = 0.3f))
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(space8))

            // 📝 Nom
            Text(
                text = prestataire.nom,
                style = MaterialTheme.typography.titleSmall, // taille ajustée
                color = NavyPrimary,
                maxLines = 1
            )

            // 🛠 Service principal
            Text(
                text = mainService,
                style = MaterialTheme.typography.labelMedium,
                color = Gray600,
                maxLines = 1
            )

            // 📍 Zone d'intervention
            Text(
                text = prestataire.zoneIntervention ?: "Zone non spécifiée",
                style = MaterialTheme.typography.labelSmall,
                color = Gray500,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(space4))

            // ⭐ Nombre d'étoiles + Nombre d'avis
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Afficher les 5 étoiles (remplies selon la moyenne)
                val note = prestataire.moyenneNotes?.toInt() ?: 0
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < note) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        tint = if (index < note) GreenPrimary else Gray300,
                        modifier = Modifier.size(14.dp)
                    )
                }
                // Nombre d'avis
                Text(
                    text = " (${prestataire.nombreAvis})",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray500
                )
            }
        }
    }
}