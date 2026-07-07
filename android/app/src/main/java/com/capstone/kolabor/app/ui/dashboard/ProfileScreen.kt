package com.capstone.kolabor.app.ui.dashboard

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.kolabor.app.data.model.User
import com.capstone.kolabor.app.data.repository.PrestataireRepository
import com.capstone.kolabor.app.data.repository.ReservationRepository
import com.capstone.kolabor.app.data.repository.UserRepository
import com.capstone.kolabor.app.utils.TokenManager
import com.capstone.serviceplatform.app.ui.theme.ErrorColor
import com.capstone.serviceplatform.app.ui.theme.*
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }
    val userRepository = remember { UserRepository(context) }

    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showEditScreen by remember { mutableStateOf(false) }
    var showChangePasswordSheet by remember { mutableStateOf(false) }
    // Statistiques
    var totalReservations by remember { mutableStateOf(0) }
    var completedReservations by remember { mutableStateOf(0) }
    var ongoingReservations by remember { mutableStateOf(0) }
    var averageRating by remember { mutableStateOf<Double?>(null) }
    var totalRevenue by remember { mutableStateOf<Double?>(null) }
    var totalReviews by remember { mutableStateOf(0) }
    var isLoadingStats by remember { mutableStateOf(true) }
    // Image
    var showImagePicker by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var isLoadingPhoto by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val currentUserId = user?.id
            if (currentUserId == null) {
                Toast.makeText(context, "Utilisateur non chargé", Toast.LENGTH_SHORT).show()
                showImagePicker = false
                return@rememberLauncherForActivityResult
            }
            photoUri = uri
            showImagePicker = false
            // Uploader automatiquement
            coroutineScope.launch {
                isLoadingPhoto = true
                val result = userRepository.uploadPhoto(currentUserId, uri)
                if (result != null) {
                    user = user?.copy(photo = result)
                    Toast.makeText(context, "Photo mise à jour", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Erreur lors de l'upload", Toast.LENGTH_SHORT).show()
                }
                isLoadingPhoto = false
            }
        } else {
            showImagePicker = false
        }
    }

    // Déclencher le sélecteur d'image
    if (showImagePicker) {
        LaunchedEffect(Unit) {
            imagePickerLauncher.launch("image/*")
        }
    }

    // Charger les données
    LaunchedEffect(Unit) {
        isLoading = true
        val userId = tokenManager.getUserId()
        if (userId != null) {
            user = userRepository.getUserById(userId)
            if (user == null) {
                errorMessage = "Impossible de charger le profil"
            } else {
                // ✅ L'utilisateur est chargé, on charge les statistiques
                isLoadingStats = true
                val reservationRepo = ReservationRepository(context)
                val prestataireRepo = PrestataireRepository(context)

                if (user!!.role == "CLIENT") {
                    val reservations = reservationRepo.getReservationsByClient(userId)
                    if (reservations != null) {
                        totalReservations = reservations.size
                        completedReservations = reservations.count { it.statut == "TERMINEE" }
                        ongoingReservations = reservations.count { it.statut == "ACCEPTEE" || it.statut == "EN_COURS" }
                    }
                } else if (user!!.role == "PRESTATAIRE") {
                    val stats = prestataireRepo.getStatistiques(userId)
                    if (stats != null) {
                        totalReservations = (stats["nombrePrestations"] as? Int) ?: 0
                        averageRating = (stats["noteMoyenne"] as? Double)
                        totalRevenue = (stats["totalRevenus"] as? Double)
                        // totalReviews peut être ajouté plus tard
                    }
                }
                isLoadingStats = false
            }
        } else {
            errorMessage = "ID utilisateur introuvable"
        }
        isLoading = false
    }

    if (showEditScreen && user != null) {
        EditProfileScreen(
            user = user!!,
            onBack = { showEditScreen = false },
            onProfileUpdated = { updatedUser ->
                user = updatedUser
                showEditScreen = false
            }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Mon profil",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = NavyPrimary
                    ),
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.Logout, contentDescription = "Déconnexion", tint = Color.White)
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = NavyPrimary
                    )
                } else if (errorMessage != null) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = ErrorColor, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(errorMessage!!, color = ErrorColor)
                    }
                } else if (user != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // En-tête du profil
                        ProfileHeader(
                            user = user!!,
                            onEditPhotoClick = { showImagePicker = true },
                            isLoadingPhoto = isLoadingPhoto,
                            photoUri = photoUri
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Cartes d'informations
                        InfoCard(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = user!!.email
                        )
                        InfoCard(
                            icon = Icons.Default.Phone,
                            label = "Téléphone",
                            value = user!!.telephone ?: "Non renseigné"
                        )
                        InfoCard(
                            icon = Icons.Default.Badge,
                            label = "Rôle",
                            value = user!!.role,
                            isRole = true
                        )

                        // --- SECTION STATISTIQUES (design senior) ---
                        if (!isLoadingStats && user != null) {
                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "📊 Activité",
                                style = MaterialTheme.typography.titleMedium,
                                color = NavyPrimary,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            if (user!!.role == "CLIENT") {
                                // Ligne 1
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    StatCard(
                                        label = "Total réservations",
                                        value = totalReservations.toString(),
                                        icon = Icons.Default.ListAlt,
                                        color = NavyPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatCard(
                                        label = "Terminées",
                                        value = completedReservations.toString(),
                                        icon = Icons.Default.CheckCircle,
                                        color = GreenPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                // Ligne 2
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    StatCard(
                                        label = "En cours",
                                        value = ongoingReservations.toString(),
                                        icon = Icons.Default.Pending,
                                        color = Color(0xFFFFB800), // Warning
                                        modifier = Modifier.weight(1f)
                                    )
                                    // Carte "fantôme" pour équilibrer (cachée)
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            } else if (user!!.role == "PRESTATAIRE") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    StatCard(
                                        label = "Prestations",
                                        value = totalReservations.toString(),
                                        icon = Icons.Default.Work,
                                        color = NavyPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatCard(
                                        label = "Note moyenne",
                                        value = String.format("%.1f", averageRating ?: 0.0),
                                        icon = Icons.Default.Star,
                                        color = GreenPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    StatCard(
                                        label = "Revenus totaux",
                                        value = String.format("%.0f Gdes", totalRevenue ?: 0.0),
                                        icon = Icons.Default.AttachMoney,
                                        color = NavyPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatCard(
                                        label = "Avis reçus",
                                        value = totalReviews.toString(),
                                        icon = Icons.Default.RateReview,
                                        color = NavyPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        // Boutons d'action
                        ActionButton(
                            icon = Icons.Default.Edit,
                            text = "Modifier mon profil",
                            onClick = { showEditScreen = true },
                            containerColor = NavyPrimary,
                            contentColor = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ActionButton(
                            icon = Icons.Default.Edit,
                            text = "Modifier la photo de profil",
                            onClick = { showImagePicker = true },
                            containerColor = Color.White,
                            contentColor = NavyPrimary,
                            isOutlined = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ActionButton(
                            icon = Icons.Default.Lock,
                            text = "Changer mon mot de passe",
                            onClick = { showChangePasswordSheet = true },
                            containerColor = Color.White,
                            contentColor = NavyPrimary,
                            isOutlined = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ActionButton(
                            icon = Icons.Default.NotificationImportant,
                            text = "Gérer les notifications",
                            onClick = { /* À venir */ },
                            containerColor = Color.White,
                            contentColor = NavyPrimary,
                            isOutlined = true
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Version
                        Text(
                            text = "Version 1.0.0",
                            style = MaterialTheme.typography.bodySmall,
                            color = Gray400,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
        if (showChangePasswordSheet && user != null) {
            ChangePasswordBottomSheet(
                userId = user!!.id,
                onDismiss = { showChangePasswordSheet = false },
                onSuccess = {
                    showChangePasswordSheet = false
                    Toast.makeText(context, "Mot de passe mis à jour", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}

// --- Composants réutilisables ---

@Composable
fun ProfileHeader(
    user: User,
    onEditPhotoClick: () -> Unit,
    isLoadingPhoto: Boolean,
    photoUri: Uri?
) {
    val roleColor = when (user.role) {
        "CLIENT" -> GreenPrimary
        "PRESTATAIRE" -> NavyPrimary
        else -> Gray600
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Gray50),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Le conteneur externe n'est pas clipé, donc le badge reste visible.
            Box(modifier = Modifier.size(92.dp)) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(NavyLight.copy(alpha = 0.2f))
                        .border(2.dp, NavyPrimary, CircleShape)
                ) {
                    when {
                        photoUri != null -> {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = "Photo de profil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        user.photo != null && user.photo.isNotEmpty() -> {
                            val fullUrl = "http://10.0.2.2:8080${user.photo}"
                            AsyncImage(
                                model = fullUrl,
                                contentDescription = "Photo de profil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Photo de profil",
                                tint = NavyPrimary,
                                modifier = Modifier
                                    .size(48.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                    if (isLoadingPhoto) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.25f))
                        )
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.Center),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(34.dp)
                        .border(2.dp, Color.White, CircleShape)
                        .clickable(onClick = onEditPhotoClick),
                    shape = CircleShape,
                    color = NavyPrimary,
                    tonalElevation = 3.dp,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modifier la photo",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = user.nom,
                    style = MaterialTheme.typography.headlineSmall,
                    color = NavyPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = roleColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = user.role,
                        style = MaterialTheme.typography.labelMedium,
                        color = roleColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Appuyez sur le crayon pour changer la photo",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
@Composable
fun InfoCard(icon: ImageVector, label: String, value: String, isRole: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Gray50),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NavyPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray500
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isRole) NavyPrimary else NavyPrimary,
                    fontWeight = if (isRole) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    isOutlined: Boolean = false
) {
    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = containerColor,
                contentColor = contentColor
            )
        ) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor
            )
        ) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    hidden: Boolean = false,
    modifier: Modifier = Modifier   // ✅ Ajout du modifier
) {
    if (hidden) {
        Spacer(modifier = modifier)  // ✅ On applique le modifier même en caché
    } else {
        Card(
            modifier = modifier
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Gray50),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = MaterialTheme.shapes.small
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
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