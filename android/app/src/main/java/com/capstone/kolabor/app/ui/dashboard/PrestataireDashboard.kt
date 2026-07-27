package com.capstone.kolabor.app.ui.dashboard

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.capstone.kolabor.app.data.model.DailyRevenue
import com.capstone.kolabor.app.data.model.Disponibilite
import com.capstone.kolabor.app.data.model.Reservation
import com.capstone.kolabor.app.data.model.Service
import com.capstone.kolabor.app.data.repository.PrestataireRepository
import com.capstone.kolabor.app.data.repository.ReservationRepository
import com.capstone.kolabor.app.ui.client.ServiceDetailScreen
import com.capstone.kolabor.app.ui.client.ServiceListScreen
import com.capstone.kolabor.app.ui.client.formatDate
import com.capstone.kolabor.app.ui.components.RevenueChart
import com.capstone.kolabor.app.ui.help.HelpScreen
import com.capstone.kolabor.app.utils.LocalNotificationManager
import com.capstone.kolabor.app.utils.TokenManager
import com.capstone.kolabor.app.utils.normalizePhotoUrl
import com.capstone.serviceplatform.app.ui.theme.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.capstone.kolabor.app.ui.privacy.PrivacyScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestataireDashboard(
    onLogout: () -> Unit,
    userId: Long,
    userName: String = "Prestataire",
    userPhoto: String? = null
) {
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
    val notificationManager = LocalNotificationManager
    val notifications by notificationManager.notifications.collectAsState()
    var showNotificationsSheet by remember { mutableStateOf(false) }
    var showServiceList by remember { mutableStateOf(false) }
    var selectedServiceDetail by remember { mutableStateOf<Service?>(null) }
    var filterForExplorer by remember { mutableStateOf<String?>(null) }
    var bookingPromptService by remember { mutableStateOf<Service?>(null) }
    var showHelp by remember { mutableStateOf(false) }
    var showPrivacy by remember { mutableStateOf(false) }

    // ✅ Résout l'ID prestataire : priorité au paramètre reçu, fallback sur tokenManager
    // uniquement si le paramètre n'a pas été fourni (0L par défaut / non identifié).
    suspend fun resolveUserId(): Long? {
        if (userId != 0L) return userId
        return tokenManager.getUserId()
    }

    suspend fun loadDashboardData() {
        try {
            isLoading = true
            isLoadingRevenue = true
            errorMessage = null

            val resolvedUserId = resolveUserId()
            Log.d("Prestataire", "🆔 ID résolu : $resolvedUserId (paramètre reçu = $userId)")
            if (resolvedUserId == null || resolvedUserId == 0L) {
                errorMessage = "Prestataire non identifié. Veuillez vous reconnecter."
                reservations = emptyList()
                weeklyRevenue = emptyList()
                isLoading = false
                isLoadingRevenue = false
                return
            }

            prestataireId = resolvedUserId

            // Statistiques
            try {
                val stats = prestataireRepo.getStatistiques(resolvedUserId)
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
                val reservationData = reservationRepo.getReservationsByPrestataire(resolvedUserId)
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
                weeklyRevenue = prestataireRepo.getWeeklyRevenue(resolvedUserId) ?: emptyList()
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

    LaunchedEffect(userId) {
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
                title = {
                    Text(
                        text = "Kolabor Pro",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary),
                actions = {
                    // 🔔 Cloche de notifications — connectée au notificationManager existant
                    val unreadCount = notifications.count { !it.isRead }
                    IconButton(onClick = { showNotificationsSheet = true }) {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(containerColor = ErrorColor, contentColor = Color.White) {
                                        Text(
                                            text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // 👤 Avatar avec anneau — cliquable vers l'onglet Profil
                    // 👤 Avatar avec photo ou lettre
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable {
                                showServiceList = false
                                selectedServiceDetail = null
                                selectedTab = 3
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!userPhoto.isNullOrBlank()) {
                            val fullUrl = normalizePhotoUrl(userPhoto)
                            AsyncImage(
                                model = fullUrl,
                                contentDescription = "Photo de profil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = userName.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // ⋮ Menu déroulant sectionné
                    var showMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .width(220.dp)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                                Text(
                                    text = userName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NavyPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = "Compte prestataire",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Gray500
                                )
                            }
                            HorizontalDivider(color = Gray100, thickness = 1.dp)

                            PrestataireMenuAction(
                                icon = Icons.AutoMirrored.Filled.List,
                                label = "Services",
                                onClick = {
                                    showMenu = false
                                    showServiceList = true
                                }
                            )
                            PrestataireMenuAction(
                                icon = Icons.Filled.HelpOutline,
                                label = "Aide",
                                onClick = {
                                    showMenu = false
                                    showHelp = true
                                }
                            )
                            PrestataireMenuAction(
                                icon = Icons.Filled.Lock,
                                label = "Confidentialité",
                                onClick = {
                                    showMenu = false
                                    showPrivacy = true
                                }
                            )

                            HorizontalDivider(color = Gray100, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                            PrestataireMenuAction(
                                icon = Icons.AutoMirrored.Filled.Logout,
                                label = "Déconnexion",
                                labelColor = ErrorColor,
                                iconColor = ErrorColor,
                                onClick = {
                                    showMenu = false
                                    onLogout()
                                }
                            )
                        }
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
                        onClick = {
                            showServiceList = false
                            selectedServiceDetail = null
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
            if (selectedServiceDetail != null) {
                ServiceDetailScreen(
                    service = selectedServiceDetail!!,
                    onBack = {
                        selectedServiceDetail = null
                        showServiceList = true
                    },
                    onReserveService = { service ->
                        bookingPromptService = service
                    }
                )
            } else if (showServiceList) {
                ServiceListScreen(
                    onBack = { showServiceList = false },
                    onServiceClick = { service ->
                        selectedServiceDetail = service
                        showServiceList = false
                    }
                )
            } else if (showHelp) {
                HelpScreen(
                    onBack = { showHelp = false }
                )
            } else if (showPrivacy) {
            PrivacyScreen(
                onBack = { showPrivacy = false }
            )
            } else {

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
                                        text = "Bonjour, $userName !",
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
                                        Text(
                                            errorMessage ?: "",
                                            color = ErrorColor,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }

                                item {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    "Disponibilité",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = NavyPrimary,
                                                    fontWeight = FontWeight.SemiBold
                                                )
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
                                        Box(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
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
                                        Box(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(color = NavyPrimary)
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            StatCard(
                                                label = "Total",
                                                value = totalCount.toString(),
                                                icon = Icons.Default.ListAlt,
                                                color = NavyPrimary,
                                                modifier = Modifier.weight(1f)
                                            )
                                            StatCard(
                                                label = "En attente",
                                                value = pendingCount.toString(),
                                                icon = Icons.Default.Pending,
                                                color = Color(0xFFFFB800),
                                                modifier = Modifier.weight(1f)
                                            )
                                            StatCard(
                                                label = "Terminées",
                                                value = completedCount.toString(),
                                                icon = Icons.Default.CheckCircle,
                                                color = GreenPrimary,
                                                modifier = Modifier.weight(1f)
                                            )
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
                                        TextButton(onClick = { selectedTab = 1 }) {
                                            Text("Voir tout", color = NavyPrimary)
                                        }
                                    }
                                }

                                if (reservations.isEmpty()) {
                                    item {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(
                                                    Icons.Default.Inbox,
                                                    contentDescription = null,
                                                    tint = Gray300,
                                                    modifier = Modifier.size(48.dp)
                                                )
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
                                    Text(
                                        "Mes réservations",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = NavyPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "${filteredReservations.size} réservation(s)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Gray500
                                    )
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
                                        Box(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
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

                        LaunchedEffect(prestataireId) {
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

                            when {
                                isLoadingDispo -> {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = NavyPrimary)
                                    }
                                }

                                errorMessageDispo != null -> {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                Icons.Default.Error,
                                                contentDescription = null,
                                                tint = ErrorColor,
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                errorMessageDispo!!,
                                                color = ErrorColor,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
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
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                Icons.Default.CalendarToday,
                                                contentDescription = null,
                                                tint = Gray300,
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("Aucune disponibilité", color = Gray500)
                                            Text(
                                                "Ajoutez vos créneaux pour être visible",
                                                color = Gray400,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }

                                else -> {
                                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

                        if (!isLoadingDispo && errorMessageDispo == null) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
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

                        if (showAddSheet) {
                            AddDisponibiliteBottomSheet(
                                prestataireId = prestataireId ?: 0L,
                                onDismiss = { showAddSheet = false },
                                onSuccess = {
                                    showAddSheet = false
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

                    3 -> {
                        ProfileScreen(
                            onLogout = onLogout,
                            onNavigateToReservations = {
                                selectedTab = 1   // Onglet Réservations
                            },
                            onNavigateToNotifications = {
                                showNotificationsSheet = true   // ✅ Ouvre le BottomSheet
                            },
                            onNavigateToPrivacy = {
                                showPrivacy = true   // ✅ Ouvre PrivacyScreen
                            }
                        )
                    }
                }
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

            if (showNotificationsSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showNotificationsSheet = false
                        notificationManager.markAllAsRead()
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    dragHandle = {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Gray300)
                            )
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Notifications",
                                style = MaterialTheme.typography.headlineSmall,
                                color = NavyPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            if (notifications.isNotEmpty()) {
                                TextButton(onClick = { notificationManager.clearAll() }) {
                                    Text("Tout effacer", color = ErrorColor)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (notifications.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = Gray300,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Aucune notification", color = Gray500)
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.height(400.dp)
                            ) {
                                items(notifications) { notification ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (notification.isRead) Color.White else Gray50
                                        ),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(
                                                text = notification.title,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = NavyPrimary,
                                                fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold
                                            )
                                            Text(
                                                text = notification.body,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Gray600
                                            )
                                            Text(
                                                text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                                    .format(Date(notification.timestamp)),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Gray400
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (bookingPromptService != null) {
                AlertDialog(
                    onDismissRequest = { bookingPromptService = null },
                    title = { Text("Réserver ce service") },
                    text = {
                        Column {
                            Text("Vous souhaitez réserver le service :")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = bookingPromptService!!.nom,
                                style = MaterialTheme.typography.titleLarge,
                                color = NavyPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Pour cela, vous devez d'abord trouver un prestataire qui propose ce service.")
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val service = bookingPromptService
                                bookingPromptService = null
                                if (service != null) {
                                    filterForExplorer = service.nom
                                    selectedTab = 1
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                        ) {
                            Text("Trouver un professionnel")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { bookingPromptService = null }) {
                            Text("Annuler")
                        }
                    }
                )
            }
        }
    }
}

// ─── Item de menu réutilisable pour la TopBar ───
@Composable
private fun PrestataireMenuAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    labelColor: Color = NavyPrimary,
    iconColor: Color = NavyPrimary
) {
    DropdownMenuItem(
        text = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = labelColor
            )
        },
        onClick = onClick,
        leadingIcon = {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        },
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp)
    )
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
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp).background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
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