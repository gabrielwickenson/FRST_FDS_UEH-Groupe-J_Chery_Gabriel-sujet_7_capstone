package com.capstone.kolabor.app.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.capstone.kolabor.app.data.model.FilterOptions
import com.capstone.kolabor.app.data.model.Prestataire
import com.capstone.kolabor.app.data.model.Reservation
import com.capstone.kolabor.app.data.model.Service
import com.capstone.kolabor.app.data.repository.PrestataireRepository
import com.capstone.kolabor.app.data.repository.ReservationRepository
import com.capstone.kolabor.app.data.repository.ServiceRepository
import com.capstone.kolabor.app.ui.client.FilterBottomSheet
import com.capstone.kolabor.app.ui.client.PrestataireCard
import com.capstone.kolabor.app.ui.client.ReservationDetailScreen
import com.capstone.kolabor.app.ui.client.ReservationsScreen
import com.capstone.kolabor.app.ui.client.formatDate
import com.capstone.kolabor.app.utils.normalizePhotoUrl
import com.capstone.serviceplatform.app.ui.theme.*
import com.kolabor.app.R
import com.kolabor.app.ui.theme.*
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDashboard(
    onLogout: () -> Unit,
    clientId: Long,
    onNavigateToBook: (Long) -> Unit,
    userName: String = "Client",
    showPrestataireDetail: MutableState<Boolean>,
    selectedPrestataire: MutableState<Prestataire?>,
    currentTab: MutableState<Int>,
    onTabChanged: (Int) -> Unit,
    onNavigateToReservations: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val serviceRepo = remember { ServiceRepository(context) }
    var selectedTab by remember { mutableStateOf(currentTab.value) }
    var services by remember { mutableStateOf<List<Service>>(emptyList()) }
    var totalReservations by remember { mutableStateOf(0) }
    val prestataireRepo = remember { PrestataireRepository(context) }
    var selectedReservation by remember { mutableStateOf<Reservation?>(null) }
    var unreadNotifications by remember { mutableStateOf(0) }

    LaunchedEffect(currentTab.value) {
        selectedTab = currentTab.value
    }

    LaunchedEffect(Unit) {
        val data = serviceRepo.getServices()
        if (data != null) services = data
    }

    var selectedServiceFilter by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.logo_kolabor_svg),
                        contentDescription = "Logo Kolabor",
                        modifier = Modifier.height(30.dp).width(110.dp),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary),
                actions = {
                    IconButton(onClick = { /* TODO: navigation notifications */ }) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifications > 0) {
                                    Badge(containerColor = ErrorColor, contentColor = Color.White) {
                                        Text(
                                            text = if (unreadNotifications > 9) "9+" else unreadNotifications.toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.width(space8))
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable {
                                selectedTab = 3
                                onTabChanged(3)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    Spacer(modifier = Modifier.width(space12))
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.height(72.dp)
            ) {
                val tabs = listOf("Accueil", "Explorer", "Réservations", "Profil")
                val icons = listOf(Icons.Outlined.Home, Icons.Outlined.Search, Icons.Outlined.History, Icons.Outlined.Person)
                val filledIcons = listOf(Icons.Filled.Home, Icons.Filled.Search, Icons.Filled.History, Icons.Filled.Person)

                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = {
                            val icon = if (selectedTab == index) filledIcons[index] else icons[index]
                            if (index == 2 && totalReservations > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = ErrorColor, contentColor = Color.White) {
                                            Text(
                                                text = if (totalReservations > 99) "99+" else totalReservations.toString(),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                ) {
                                    Icon(icon, contentDescription = title, modifier = Modifier.size(24.dp))
                                }
                            } else {
                                Icon(icon, contentDescription = title, modifier = Modifier.size(24.dp))
                            }
                        },
                        label = { Text(text = title, style = MaterialTheme.typography.labelSmall) },
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            onTabChanged(index)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyPrimary,
                            selectedTextColor = NavyPrimary,
                            unselectedIconColor = Gray400,
                            unselectedTextColor = Gray400,
                            indicatorColor = GreenLightest
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (selectedTab) {
                0 -> {
                    var searchQuery by remember { mutableStateOf("") }
                    var prestataires by remember { mutableStateOf<List<Prestataire>>(emptyList()) }
                    var isLoadingPrestataires by remember { mutableStateOf(true) }

                    var nextReservation by remember { mutableStateOf<Reservation?>(null) }
                    var isLoadingNextReservation by remember { mutableStateOf(true) }
                    val reservationRepo = remember { ReservationRepository(context) }

                    var topPrestataires by remember { mutableStateOf<List<Prestataire>>(emptyList()) }
                    var isLoadingTop by remember { mutableStateOf(true) }

                    LaunchedEffect(Unit) {
                        isLoadingPrestataires = true
                        val data = prestataireRepo.searchPrestataires(service = null, noteMin = null, zone = null)
                        if (data != null) prestataires = data
                        isLoadingPrestataires = false

                        isLoadingNextReservation = true
                        val reservations = reservationRepo.getReservationsByClient(clientId)
                        if (reservations != null) {
                            totalReservations = reservations.size
                            val active = reservations.filter {
                                it.statut == "ACCEPTEE" || it.statut == "EN_COURS"
                            }.sortedBy { it.dateHeure }
                            nextReservation = active.firstOrNull()
                        }
                        isLoadingNextReservation = false

                        isLoadingTop = true
                        val topData = prestataireRepo.searchPrestataires(service = null, noteMin = 4.0, zone = null)
                        if (topData != null) {
                            val minNote = BigDecimal.valueOf(4.0)
                            topPrestataires = topData
                                .filter { it.moyenneNotes != null && it.moyenneNotes!! >= minNote }
                                .sortedByDescending { it.moyenneNotes }
                                .take(5)
                        }
                        isLoadingTop = false
                    }

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

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Rechercher un prestataire") },
                            placeholder = { Text("Nom, compétence, zone...", color = Gray500) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = NavyLight)
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

                            if (isLoadingTop) {
                                CircularProgressIndicator(modifier = Modifier.size(32.dp), color = NavyPrimary)
                                Spacer(modifier = Modifier.height(space16))
                            } else if (topPrestataires.isNotEmpty()) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "⭐ Prestataires recommandés",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Gray600,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(space8))
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(space8)) {
                                        items(topPrestataires) { prestataire ->
                                            CompactPrestataireCard(
                                                prestataire = prestataire,
                                                onClick = { onNavigateToBook(prestataire.id) }
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

                        if (services.isNotEmpty()) {
                            Text(
                                "Filtres rapides",
                                style = MaterialTheme.typography.labelLarge,
                                color = Gray600,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(space8))
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
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
                                modifier = Modifier.fillMaxWidth().height(350.dp),
                                verticalArrangement = Arrangement.spacedBy(space8)
                            ) {
                                items(filteredPrestataires) { prestataire ->
                                    PrestataireCard(
                                        prestataire = prestataire,
                                        onClick = {
                                            selectedPrestataire.value = prestataire
                                            showPrestataireDetail.value = true
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
                    var searchQueryExplorer by remember { mutableStateOf("") }
                    var filteredPrestatairesExplorer by remember { mutableStateOf<List<Prestataire>>(emptyList()) }
                    var isLoadingExplorer by remember { mutableStateOf(false) }
                    var isGridView by remember { mutableStateOf(true) }
                    var showFilters by remember { mutableStateOf(false) }
                    var filterOptions by remember { mutableStateOf(FilterOptions()) }
                    var sortOption by remember { mutableStateOf("Par défaut") }
                    val sortOptions = listOf("Par défaut", "Note (croissante)", "Note (décroissante)", "Prix (croissant)", "Prix (décroissant)")
                    var showSortDropdown by remember { mutableStateOf(false) }
                    var selectedCategory by remember { mutableStateOf<String?>(null) }
                    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
                    var showSuggestions by remember { mutableStateOf(false) }
                    var allPrestataires by remember { mutableStateOf<List<Prestataire>>(emptyList()) }

                    LaunchedEffect(selectedCategory, searchQueryExplorer, filterOptions, sortOption) {
                        isLoadingExplorer = true
                        val data = prestataireRepo.searchPrestataires(
                            service = selectedCategory,
                            noteMin = if (filterOptions.noteMin > 0) filterOptions.noteMin.toDouble() else null,
                            zone = null
                        )
                        if (data != null) {
                            allPrestataires = data
                            var filtered = data

                            if (filterOptions.priceMin > 0 || filterOptions.priceMax < 3000.0) {
                                filtered = filtered.filter {
                                    val tarif = it.tarifHoraire?.toDouble() ?: 0.0
                                    tarif >= filterOptions.priceMin && tarif <= filterOptions.priceMax
                                }
                            }

                            if (searchQueryExplorer.isNotBlank()) {
                                filtered = filtered.filter {
                                    it.nom.contains(searchQueryExplorer, ignoreCase = true) ||
                                            (it.competences?.contains(searchQueryExplorer, ignoreCase = true) == true) ||
                                            (it.zoneIntervention?.contains(searchQueryExplorer, ignoreCase = true) == true)
                                }
                            }

                            when (sortOption) {
                                "Note (croissante)" -> filtered = filtered.sortedBy { it.moyenneNotes?.toDouble() ?: 0.0 }
                                "Note (décroissante)" -> filtered = filtered.sortedByDescending { it.moyenneNotes?.toDouble() ?: 0.0 }
                                "Prix (croissant)" -> filtered = filtered.sortedBy { it.tarifHoraire?.toDouble() ?: 0.0 }
                                "Prix (décroissant)" -> filtered = filtered.sortedByDescending { it.tarifHoraire?.toDouble() ?: 0.0 }
                                else -> {}
                            }

                            filteredPrestatairesExplorer = filtered
                        } else {
                            allPrestataires = emptyList()
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
                            text = "Trouvez le prestataire idéal",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Gray500
                        )
                        Spacer(modifier = Modifier.height(space16))

                        // ─── Grille de catégories ───
                        if (services.isNotEmpty()) {
                            Text(
                                text = "Explorez par catégorie",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            Spacer(modifier = Modifier.height(space4))
                            Text(
                                text = "Des centaines de services à domicile, partout en Haïti.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Gray500
                            )
                            Spacer(modifier = Modifier.height(space16))

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                horizontalArrangement = Arrangement.spacedBy(space8),
                                verticalArrangement = Arrangement.spacedBy(space8),
                                modifier = Modifier.height(220.dp)
                            ) {
                                items(services.take(9)) { service ->
                                    CategoryTile(
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

                        OutlinedTextField(
                            value = searchQueryExplorer,
                            onValueChange = { query ->
                                searchQueryExplorer = query
                                if (query.length >= 2 && allPrestataires.isNotEmpty()) {
                                    val allNames = allPrestataires.map { it.nom }
                                    val allCompetences = allPrestataires.flatMap {
                                        it.competences?.split(",")?.map { c -> c.trim() } ?: emptyList()
                                    }
                                    val combined = (allNames + allCompetences).distinct()
                                    suggestions = combined.filter { it.contains(query, ignoreCase = true) }.take(5)
                                    showSuggestions = suggestions.isNotEmpty()
                                } else {
                                    showSuggestions = false
                                }
                            },
                            label = { Text("Rechercher un prestataire") },
                            placeholder = { Text("Nom, compétence...", color = Gray500) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = NavyLight)
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

                        if (showSuggestions) {
                            Card(
                                modifier = Modifier.fillMaxWidth().heightIn(max = 150.dp).padding(vertical = space4),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                shape = MaterialTheme.shapes.small
                            ) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(horizontal = space8)
                                ) {
                                    items(suggestions) { suggestion ->
                                        TextButton(
                                            onClick = {
                                                searchQueryExplorer = suggestion
                                                showSuggestions = false
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = suggestion,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Gray700,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        HorizontalDivider(color = Gray100)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(space8))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tri", style = MaterialTheme.typography.labelLarge, color = Gray600)
                            Box {
                                TextButton(onClick = { showSortDropdown = !showSortDropdown }) {
                                    Text(text = sortOption, color = NavyPrimary)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = NavyPrimary)
                                }
                                DropdownMenu(
                                    expanded = showSortDropdown,
                                    onDismissRequest = { showSortDropdown = false }
                                ) {
                                    sortOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option) },
                                            onClick = {
                                                sortOption = option
                                                showSortDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(space8))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { showFilters = true }) {
                                Icon(
                                    Icons.Default.FilterList,
                                    contentDescription = "Filtres",
                                    tint = NavyPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(space4))
                                Text("Filtres", color = NavyPrimary)
                            }

                            Row {
                                IconButton(onClick = { isGridView = true }) {
                                    Icon(
                                        imageVector = if (isGridView) Icons.Filled.GridView else Icons.Outlined.GridView,
                                        contentDescription = "Vue grille",
                                        tint = if (isGridView) NavyPrimary else Gray400
                                    )
                                }
                                IconButton(onClick = { isGridView = false }) {
                                    Icon(
                                        imageVector = if (isGridView) Icons.Outlined.List else Icons.Filled.List,
                                        contentDescription = "Vue liste",
                                        tint = if (isGridView) Gray400 else NavyPrimary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(space8))

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

                            if (isGridView) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    horizontalArrangement = Arrangement.spacedBy(space8),
                                    verticalArrangement = Arrangement.spacedBy(space8),
                                    modifier = Modifier.fillMaxWidth().height(350.dp)
                                ) {
                                    items(filteredPrestatairesExplorer) { prestataire ->
                                        ExplorerPrestataireCard(
                                            prestataire = prestataire,
                                            onClick = {
                                                selectedPrestataire.value = prestataire
                                                showPrestataireDetail.value = true
                                            }
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth().height(350.dp),
                                    verticalArrangement = Arrangement.spacedBy(space8)
                                ) {
                                    items(filteredPrestatairesExplorer) { prestataire ->
                                        ExplorerPrestataireListCard(
                                            prestataire = prestataire,
                                            onClick = {
                                                selectedPrestataire.value = prestataire
                                                showPrestataireDetail.value = true
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(space16))
                        }
                    }

                    if (showFilters) {
                        FilterBottomSheet(
                            currentFilters = filterOptions,
                            onApplyFilters = { newFilters ->
                                filterOptions = newFilters
                                showFilters = false
                            },
                            onDismiss = { showFilters = false }
                        )
                    }
                }

                2 -> {
                    if (selectedReservation != null) {
                        ReservationDetailScreen(
                            reservation = selectedReservation!!,
                            clientId = clientId,
                            onBack = { selectedReservation = null },
                            onCancel = { selectedReservation = null },
                            onReview = { selectedReservation = null }
                        )
                    } else {
                        ReservationsScreen(
                            onBack = { selectedTab = 0 },
                            clientId = clientId,
                            onReservationClick = { reservation -> selectedReservation = reservation }
                        )
                    }
                }

                3 -> {
                    ProfileScreen(
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// Composants réutilisables
// ─────────────────────────────────────────────────────────

@Composable
fun StatCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = space12, horizontal = space8),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.headlineSmall, color = color)
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Gray500)
        }
    }
}

@Composable
fun ServiceGridItem(service: Service, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(80.dp),
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
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(space4))
            Text(text = service.nom, style = MaterialTheme.typography.bodyMedium, color = NavyPrimary, maxLines = 1)
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
            Icon(icon, contentDescription = label, tint = NavyPrimary, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(space8))
            Text(label, style = MaterialTheme.typography.labelLarge, color = NavyPrimary)
        }
    }
}

@Composable
fun ExplorerPrestataireCard(
    prestataire: Prestataire,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(space8),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(NavyLight.copy(alpha = 0.3f))
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(space4))
            Text(text = prestataire.nom, style = MaterialTheme.typography.titleMedium, color = NavyPrimary, maxLines = 1)
            Row {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < (prestataire.moyenneNotes?.toInt() ?: 0)) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        tint = if (index < (prestataire.moyenneNotes?.toInt() ?: 0)) GreenPrimary else Gray300,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(text = " ${prestataire.moyenneNotes?.toString() ?: "N/A"}", style = MaterialTheme.typography.bodySmall, color = Gray600)
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
        modifier = Modifier.fillMaxWidth().height(72.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = if (isSelected) NavyPrimary else Color.White),
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
    val mainService = prestataire.competences?.split(",")?.firstOrNull()?.trim() ?: "Service non spécifié"

    Card(
        modifier = Modifier
            .width(180.dp)
            .height(220.dp) // augmenté pour accueillir la photo
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
            // 📸 PHOTO DE PROFIL (avec fallback)
            Box(
                modifier = Modifier
                    .size(72.dp)
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
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(space8))

            // Nom
            Text(
                text = prestataire.nom,
                style = MaterialTheme.typography.titleSmall,
                color = NavyPrimary,
                maxLines = 1
            )

            // Service principal
            Text(
                text = mainService,
                style = MaterialTheme.typography.labelMedium,
                color = Gray600,
                maxLines = 1
            )

            // Zone d'intervention
            Text(
                text = prestataire.zoneIntervention ?: "Zone non spécifiée",
                style = MaterialTheme.typography.labelSmall,
                color = Gray500,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(space4))

            // Étoiles + Nombre d'avis
            Row(verticalAlignment = Alignment.CenterVertically) {
                val note = prestataire.moyenneNotes?.toInt() ?: 0
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < note) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        tint = if (index < note) GreenPrimary else Gray300,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = " (${prestataire.nombreAvis})",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray500
                )
            }
        }
    }
}

@Composable
fun ExplorerPrestataireListCard(
    prestataire: Prestataire,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(space12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(60.dp).clip(CircleShape).background(NavyLight.copy(alpha = 0.3f))
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(space12))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = prestataire.nom, style = MaterialTheme.typography.titleMedium, color = NavyPrimary)
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < (prestataire.moyenneNotes?.toInt() ?: 0)) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (index < (prestataire.moyenneNotes?.toInt() ?: 0)) GreenPrimary else Gray300,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(text = " (${prestataire.nombreAvis})", style = MaterialTheme.typography.labelSmall, color = Gray500)
                }
                Text(
                    text = "💰 ${prestataire.tarifHoraire?.toString() ?: "N/A"} Gdes/h",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray600
                )
                Text(
                    text = prestataire.competences?.take(20) ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500,
                    maxLines = 1
                )
            }
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Gray400)
        }
    }
}

// ─── Palette de couleurs pour les tuiles de catégories ───
private val categoryPalette = listOf(
    Color(0xFFF3E8FF) to Color(0xFF9333EA), // violet
    Color(0xFFE0F2FE) to Color(0xFF0284C7), // bleu clair
    Color(0xFFDBEAFE) to Color(0xFF2563EB), // bleu
    Color(0xFFFEF3C7) to Color(0xFFD97706), // jaune
    Color(0xFFFCE7F3) to Color(0xFFDB2777), // rose
    Color(0xFFD1FAE5) to Color(0xFF059669), // vert
)

@Composable
fun CategoryTile(
    service: Service,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (bgColor, iconColor) = categoryPalette[service.nom.hashCode().mod(categoryPalette.size)]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) GreenPrimary else Gray200,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(space12),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Build,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(space8))
        Text(
            text = service.nom,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = NavyPrimary,
            maxLines = 1
        )
        if (!service.categorie.isNullOrBlank()) {
            Text(
                text = service.categorie,
                style = MaterialTheme.typography.labelSmall,
                color = Gray500,
                maxLines = 1
            )
        }
    }
}