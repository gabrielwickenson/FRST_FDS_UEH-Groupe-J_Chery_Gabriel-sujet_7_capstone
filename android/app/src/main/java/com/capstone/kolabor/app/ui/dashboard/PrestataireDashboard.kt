package com.capstone.kolabor.app.ui.dashboard

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.kolabor.app.data.model.DailyRevenue
import com.capstone.kolabor.app.data.model.Disponibilite
import com.capstone.kolabor.app.data.model.Reservation
import com.capstone.kolabor.app.data.repository.PrestataireRepository
import com.capstone.kolabor.app.data.repository.ReservationRepository
import com.capstone.kolabor.app.ui.client.formatDate
import com.capstone.kolabor.app.ui.components.RevenueChart
import com.capstone.kolabor.app.utils.TokenManager
import com.capstone.serviceplatform.app.ui.theme.*
import kotlinx.coroutines.launch
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestataireDashboard(onLogout: () -> Unit,
                         userId: Long ) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }
    val reservationRepo = remember { ReservationRepository(context) }
    val prestataireRepo = remember { PrestataireRepository(context) }

    var prestataireId by remember { mutableStateOf<Long?>(null) }
    var reservations by remember { mutableStateOf<List<Reservation>>(emptyList()) }
    var weeklyRevenue by remember { mutableStateOf<List<DailyRevenue>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isLoadingRevenue by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var totalCount by remember { mutableStateOf(0) }
    var pendingCount by remember { mutableStateOf(0) }
    var completedCount by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf(0) }
    var isAvailable by remember { mutableStateOf(false) }
    var isUpdatingAvailability by remember { mutableStateOf(false) }
    var selectedReservation by remember { mutableStateOf<Reservation?>(null) }
    var showDetailSheet by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Toutes") }

    suspend fun loadDashboardData() {
        try {
            isLoading = true
            isLoadingRevenue = true
            errorMessage = null

            val userId = tokenManager.getUserId()
            Log.d("Prestataire", "🆔 ID récupéré : $userId")
            if (userId == null || userId == 0L) {
                errorMessage = "Prestataire non identifié. Veuillez vous reconnecter."
                reservations = emptyList()
                weeklyRevenue = emptyList()
                isLoading = false
                isLoadingRevenue = false
                return
            }

            prestataireId = userId

            // Statistiques
            try {
                val stats = prestataireRepo.getStatistiques(userId)
                if (stats != null) {
                    val availabilityValue = stats["disponible"] ?: stats["available"]
                    isAvailable = when (availabilityValue) {
                        is Boolean -> availabilityValue
                        is Number -> availabilityValue.toInt() != 0
                        is String -> availabilityValue.equals("true", ignoreCase = true)
                        else -> isAvailable
                    }
                }
            } catch (e: Exception) {
                Log.e("Prestataire", "Erreur statistiques", e)
            }

            // Réservations
            try {
                val reservationData = reservationRepo.getReservationsByPrestataire(userId)
                reservations = reservationData ?: emptyList()
                totalCount = reservations.size
                pendingCount = reservations.count { it.statut == "EN_ATTENTE" }
                completedCount = reservations.count { it.statut == "TERMINEE" }
            } catch (e: Exception) {
                Log.e("Prestataire", "Erreur réservations", e)
                reservations = emptyList()
            }

            // Revenus
            try {
                weeklyRevenue = prestataireRepo.getWeeklyRevenue(userId) ?: emptyList()
                Log.d("Prestataire", "📊 Revenus bruts : ${weeklyRevenue.map { it.day to it.amount }}")
            } catch (e: Exception) {
                Log.e("Prestataire", "Erreur revenus", e)
                weeklyRevenue = emptyList()
            }

        } catch (e: Exception) {
            Log.e("Prestataire", "❌ Erreur fatale dans loadDashboardData", e)
            errorMessage = "Erreur de chargement : ${e.message}"
            reservations = emptyList()
            weeklyRevenue = emptyList()
        } finally {
            isLoading = false
            isLoadingRevenue = false
        }
    }

    LaunchedEffect(Unit) {
        loadDashboardData()
    }

    val filteredReservations = if (selectedFilter == "Toutes") {
        reservations
    } else {
        reservations.filter { it.statut == selectedFilter }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kolabor Pro", style = MaterialTheme.typography.titleLarge, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Déconnexion", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                val tabs = listOf("Accueil", "Réservations", "Disponibilités", "Profil")
                val icons = listOf(Icons.Filled.Home, Icons.Filled.History, Icons.Filled.CalendarToday, Icons.Filled.Person)
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
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing),
                        onRefresh = {
                            isRefreshing = true
                            coroutineScope.launch {
                                try {
                                    loadDashboardData()
                                } finally {
                                    isRefreshing = false
                                }
                            }
                        }
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    text = "Bonjour, Prestataire !",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = NavyPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Voici un résumé de votre activité.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Gray500
                                )
                                if (!errorMessage.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(errorMessage ?: "", color = ErrorColor, style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Disponibilité", style = MaterialTheme.typography.titleMedium, color = NavyPrimary, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                text = if (isAvailable) "Vous êtes disponible" else "Vous êtes indisponible",
                                                color = if (isAvailable) GreenPrimary else ErrorColor,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        Switch(
                                            checked = isAvailable,
                                            onCheckedChange = { newStatus ->
                                                val id = prestataireId
                                                if (id == null || id == 0L) {
                                                    Toast.makeText(context, "Prestataire non identifié", Toast.LENGTH_SHORT).show()
                                                    return@Switch
                                                }
                                                isUpdatingAvailability = true
                                                coroutineScope.launch {
                                                    try {
                                                        val success = prestataireRepo.updateAvailability(id, newStatus)
                                                        if (success) {
                                                            isAvailable = newStatus
                                                            Toast.makeText(
                                                                context,
                                                                if (newStatus) "Vous êtes maintenant disponible" else "Vous êtes maintenant indisponible",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        } else {
                                                            Toast.makeText(context, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show()
                                                        }
                                                    } catch (e: Exception) {
                                                        Toast.makeText(context, "Erreur réseau", Toast.LENGTH_SHORT).show()
                                                    } finally {
                                                        isUpdatingAvailability = false
                                                    }
                                                }
                                            },
                                            enabled = !isUpdatingAvailability,
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = NavyPrimary,
                                                checkedTrackColor = NavyPrimary.copy(alpha = 0.5f),
                                                uncheckedThumbColor = Gray400,
                                                uncheckedTrackColor = Gray300
                                            )
                                        )
                                    }
                                }
                            }

                            item {
                                if (isLoadingRevenue) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = NavyPrimary)
                                    }
                                } else if (weeklyRevenue.isNotEmpty()) {
                                    RevenueChart(
                                        data = weeklyRevenue.associate { it.day to it.amount },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    Text(
                                        text = "Aucune donnée de revenus disponible",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Gray500
                                    )
                                }
                            }

                            item {
                                if (isLoading) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = NavyPrimary)
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        StatCard(label = "Total", value = totalCount.toString(), icon = Icons.Default.ListAlt, color = NavyPrimary, modifier = Modifier.weight(1f))
                                        StatCard(label = "En attente", value = pendingCount.toString(), icon = Icons.Default.Pending, color = Color(0xFFFFB800), modifier = Modifier.weight(1f))
                                        StatCard(label = "Terminées", value = completedCount.toString(), icon = Icons.Default.CheckCircle, color = GreenPrimary, modifier = Modifier.weight(1f))
                                    }
                                }
                            }

                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "📋 Dernières demandes",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = NavyPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    TextButton(onClick = { selectedTab = 1 }) { Text("Voir tout", color = NavyPrimary) }
                                }
                            }

                            if (reservations.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.Inbox, contentDescription = null, tint = Gray300, modifier = Modifier.size(48.dp))
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("Aucune réservation pour le moment", color = Gray500)
                                        }
                                    }
                                }
                            } else {
                                items(reservations.take(5)) { reservation ->
                                    PrestataireReservationCard(
                                        reservation = reservation,
                                        prestataireId = prestataireId ?: 0L,
                                        onAction = {
                                            coroutineScope.launch { loadDashboardData() }
                                        },
                                        onCardClick = {
                                            selectedReservation = reservation
                                            showDetailSheet = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing),
                        onRefresh = {
                            isRefreshing = true
                            coroutineScope.launch {
                                try {
                                    loadDashboardData()
                                } finally {
                                    isRefreshing = false
                                }
                            }
                        }
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text("Mes réservations", style = MaterialTheme.typography.headlineSmall, color = NavyPrimary, fontWeight = FontWeight.Bold)
                                Text("${filteredReservations.size} réservation(s)", style = MaterialTheme.typography.bodyMedium, color = Gray500)
                            }

                            if (reservations.isNotEmpty()) {
                                item {
                                    Row(
                                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf("Toutes", "EN_ATTENTE", "ACCEPTEE", "EN_COURS", "TERMINEE", "ANNULEE").forEach { statut ->
                                            FilterChip(
                                                selected = selectedFilter == statut,
                                                onClick = { selectedFilter = statut },
                                                label = {
                                                    Text(
                                                        text = when (statut) {
                                                            "Toutes" -> "Toutes"
                                                            "EN_ATTENTE" -> "En attente"
                                                            "ACCEPTEE" -> "Acceptées"
                                                            "EN_COURS" -> "En cours"
                                                            "TERMINEE" -> "Terminées"
                                                            else -> "Annulées"
                                                        }
                                                    )
                                                },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = NavyPrimary,
                                                    selectedLabelColor = Color.White,
                                                    disabledSelectedContainerColor = NavyPrimary
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            if (filteredReservations.isEmpty()) {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (reservations.isEmpty()) "Aucune réservation pour le moment" else "Aucune réservation avec ce statut",
                                            color = Gray500,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            } else {
                                items(filteredReservations) { reservation ->
                                    PrestataireReservationCard(
                                        reservation = reservation,
                                        prestataireId = prestataireId ?: 0L,
                                        onAction = { coroutineScope.launch { loadDashboardData() } },
                                        onCardClick = {
                                            selectedReservation = reservation
                                            showDetailSheet = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                2 -> {
                    var disponibilites by remember { mutableStateOf<List<Disponibilite>>(emptyList()) }
                    var isLoadingDispo by remember { mutableStateOf(true) }
                    var showAddSheet by remember { mutableStateOf(false) }
                    var errorMessageDispo by remember { mutableStateOf<String?>(null) }

                    // Charger les disponibilités
                    LaunchedEffect(Unit) {
                        isLoadingDispo = true
                        errorMessageDispo = null
                        val data = prestataireRepo.getDisponibilites(prestataireId ?: 0L)
                        if (data != null) {
                            disponibilites = data
                        } else {
                            errorMessageDispo = "Impossible de charger vos disponibilités"
                        }
                        isLoadingDispo = false
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        // En-tête
                        Text(
                            text = "Mes disponibilités",
                            style = MaterialTheme.typography.headlineSmall,
                            color = NavyPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${disponibilites.size} créneau(x) défini(s)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gray500
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Contenu
                        when {
                            isLoadingDispo -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = NavyPrimary)
                                }
                            }
                            errorMessageDispo != null -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Error, contentDescription = null, tint = ErrorColor, modifier = Modifier.size(48.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(errorMessageDispo!!, color = ErrorColor, style = MaterialTheme.typography.bodyMedium)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(onClick = {
                                            isLoadingDispo = true
                                            errorMessageDispo = null
                                            coroutineScope.launch {
                                                val data = prestataireRepo.getDisponibilites(prestataireId ?: 0L)
                                                if (data != null) {
                                                    disponibilites = data
                                                } else {
                                                    errorMessageDispo = "Impossible de charger vos disponibilités"
                                                }
                                                isLoadingDispo = false
                                            }
                                        }) {
                                            Text("Réessayer")
                                        }
                                    }
                                }
                            }
                            disponibilites.isEmpty() -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Gray300, modifier = Modifier.size(48.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Aucune disponibilité", color = Gray500)
                                        Text("Ajoutez vos créneaux pour être visible", color = Gray400, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                            else -> {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(disponibilites) { dispo ->
                                        DisponibiliteCard(
                                            disponibilite = dispo,
                                            onDelete = {
                                                coroutineScope.launch {
                                                    val success = prestataireRepo.deleteDisponibilite(dispo.id)
                                                    if (success) {
                                                        val data = prestataireRepo.getDisponibilites(prestataireId ?: 0L)
                                                        if (data != null) {
                                                            disponibilites = data
                                                        }
                                                        Toast.makeText(context, "Supprimée", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        Toast.makeText(context, "Erreur de suppression", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Bouton flottant
                    if (!isLoadingDispo && errorMessageDispo == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            FloatingActionButton(
                                onClick = { showAddSheet = true },
                                containerColor = NavyPrimary,
                                contentColor = Color.White,
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Ajouter")
                            }
                        }
                    }

                    // Bottom Sheet d'ajout
                    if (showAddSheet) {
                        AddDisponibiliteBottomSheet(
                            prestataireId = prestataireId ?: 0L,
                            onDismiss = { showAddSheet = false },
                            onSuccess = {
                                showAddSheet = false
                                // Recharger la liste
                                coroutineScope.launch {
                                    val data = prestataireRepo.getDisponibilites(prestataireId ?: 0L)
                                    if (data != null) {
                                        disponibilites = data
                                    }
                                }
                            }
                        )
                    }
                }

                3 -> ProfileScreen(onLogout = onLogout)
            }

            if (showDetailSheet && selectedReservation != null) {
                PrestataireReservationDetailBottomSheet(
                    reservation = selectedReservation!!,
                    onDismiss = {
                        showDetailSheet = false
                        selectedReservation = null
                    }
                )
            }
        }
    }
}

// ─── Carte de réservation ───
@Composable
fun PrestataireReservationCard(
    reservation: Reservation,
    prestataireId: Long,
    onAction: () -> Unit,
    onCardClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { ReservationRepository(context) }
    var isLoading by remember { mutableStateOf(false) }

    val statutColor = when (reservation.statut) {
        "EN_ATTENTE" -> Color(0xFFFFB800)
        "ACCEPTEE" -> NavyPrimary
        "EN_COURS" -> NavyPrimary
        "TERMINEE" -> GreenPrimary
        "ANNULEE" -> ErrorColor
        else -> Gray500
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reservation.service?.nom ?: "Service inconnu",
                    style = MaterialTheme.typography.titleSmall,
                    color = NavyPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statutColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = reservation.statut?.replace("_", " ") ?: "Inconnu",
                        style = MaterialTheme.typography.labelSmall,
                        color = statutColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Client : ${reservation.client?.nom ?: "Inconnu"}",
                style = MaterialTheme.typography.bodyMedium,
                color = Gray700
            )
            Text(
                text = formatDate(reservation.dateHeure),
                style = MaterialTheme.typography.bodySmall,
                color = Gray500
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (reservation.statut) {
                    "EN_ATTENTE" -> {
                        SmallButton(
                            text = "Accepter",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                isLoading = true
                                coroutineScope.launch {
                                    val success = repository.updateStatut(reservation.id, "ACCEPTEE", prestataireId)
                                    if (success) onAction()
                                    isLoading = false
                                }
                            },
                            isLoading = isLoading,
                            color = GreenPrimary
                        )
                        SmallButton(
                            text = "Refuser",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                isLoading = true
                                coroutineScope.launch {
                                    val success = repository.updateStatut(reservation.id, "REFUSEE", prestataireId)
                                    if (success) onAction()
                                    isLoading = false
                                }
                            },
                            isLoading = isLoading,
                            color = ErrorColor
                        )
                    }
                    "ACCEPTEE" -> {
                        SmallButton(
                            text = "Démarrer",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                isLoading = true
                                coroutineScope.launch {
                                    val success = repository.updateStatut(reservation.id, "EN_COURS", prestataireId)
                                    if (success) onAction()
                                    isLoading = false
                                }
                            },
                            isLoading = isLoading,
                            color = NavyPrimary
                        )
                    }
                    "EN_COURS" -> {
                        SmallButton(
                            text = "Terminer",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                isLoading = true
                                coroutineScope.launch {
                                    val success = repository.updateStatut(reservation.id, "TERMINEE", prestataireId)
                                    if (success) onAction()
                                    isLoading = false
                                }
                            },
                            isLoading = isLoading,
                            color = GreenPrimary
                        )
                    }
                    else -> {
                        Spacer(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

// ─── Petit bouton ───
@Composable
fun SmallButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isLoading: Boolean,
    color: Color
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White,
            disabledContainerColor = color.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
        } else {
            Text(text, style = MaterialTheme.typography.labelSmall)
        }
    }
}

// ─── StatCard ───
@Composable
fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray500
                )
            }
        }
    }
}