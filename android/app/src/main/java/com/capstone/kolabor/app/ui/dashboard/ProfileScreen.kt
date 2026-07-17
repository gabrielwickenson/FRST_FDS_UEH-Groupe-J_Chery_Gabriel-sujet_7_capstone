package com.capstone.kolabor.app.ui.dashboard

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.capstone.kolabor.app.data.model.User
import com.capstone.kolabor.app.data.repository.PrestataireRepository
import com.capstone.kolabor.app.data.repository.ReservationRepository
import com.capstone.kolabor.app.data.repository.UserRepository
import com.capstone.kolabor.app.utils.TokenManager
import com.capstone.serviceplatform.app.ui.theme.*
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }
    val userRepository = remember { UserRepository(context) }

    var user by remember { mutableStateOf<User?>(null) }
    var persistedPhotoUrl by remember { mutableStateOf<String?>(null) }
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

    // UCrop launchers
    val uCropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val croppedUri = UCrop.getOutput(data)
                if (croppedUri != null) {
                    val currentUserId = user?.id
                    if (currentUserId != null) {
                        photoUri = croppedUri
                        showImagePicker = false
                        coroutineScope.launch {
                            isLoadingPhoto = true
                            val uploadResult = userRepository.uploadPhoto(currentUserId, croppedUri)
                            if (uploadResult != null) {
                                val refreshedUser = userRepository.getUserById(currentUserId)
                                if (refreshedUser != null) user = refreshedUser
                                Toast.makeText(context, "Photo mise à jour", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Erreur lors de l'upload", Toast.LENGTH_SHORT).show()
                            }
                            isLoadingPhoto = false
                        }
                    }
                }
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            showImagePicker = false
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val destinationFile = File(context.cacheDir, "cropped_profile_photo.jpg")
            val destinationUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                destinationFile
            )
            val uCrop = UCrop.of(uri, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(500, 500)
                .withOptions(UCrop.Options().apply {
                    setCircleDimmedLayer(true)
                    setShowCropFrame(true)
                    setShowCropGrid(false)
                    setCompressionFormat(Bitmap.CompressFormat.JPEG)
                    setCompressionQuality(90)
                    setToolbarColor(android.graphics.Color.parseColor("#19355F"))
                    setToolbarWidgetColor(android.graphics.Color.WHITE)
                    setToolbarTitle("Recadrer la photo")
                })
            try {
                val intent = uCrop.getIntent(context)
                uCropLauncher.launch(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Erreur de recadrage : ${e.message}", Toast.LENGTH_LONG).show()
                showImagePicker = false
            }
        }
    }

    if (showImagePicker) {
        LaunchedEffect(Unit) {
            imagePickerLauncher.launch("image/*")
        }
    }

    // Chargement des données
    LaunchedEffect(Unit) {
        isLoading = true
        val userId = tokenManager.getUserId()
        if (userId != null) {
            user = userRepository.getUserById(userId)
            persistedPhotoUrl = tokenManager.getUserPhoto()
            if (persistedPhotoUrl.isNullOrBlank()) {
                persistedPhotoUrl = normalizePhotoUrl(user?.photo)
                tokenManager.saveUserPhoto(persistedPhotoUrl)
            } else {
                persistedPhotoUrl = normalizePhotoUrl(persistedPhotoUrl)
            }
            if (user == null) {
                errorMessage = "Impossible de charger le profil"
            } else {
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
                    }
                }
                isLoadingStats = false
            }
        } else {
            errorMessage = "ID utilisateur introuvable"
        }
        isLoading = false
    }

    // Gestion des écrans de modification (hors overlay)
    if (showEditScreen && user != null) {
        EditProfileScreen(
            user = user!!,
            onBack = { showEditScreen = false },
            onProfileUpdated = { updatedUser ->
                user = updatedUser
                showEditScreen = false
            }
        )
        return
    }

    // Contenu principal (sans Scaffold)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = NavyPrimary
                )
            }
            errorMessage != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = ErrorColor, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMessage!!, color = ErrorColor)
                }
            }
            user != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    // En‑tête avec photo
                    item {
                        ProfileHeader(
                            user = user!!,
                            onEditPhotoClick = { showImagePicker = true },
                            isLoadingPhoto = isLoadingPhoto,
                            photoUri = photoUri,
                            persistedPhotoUrl = persistedPhotoUrl
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // SECTION 1 : PRÉFÉRENCES
                    item {
                        SettingsSection(title = "PRÉFÉRENCES") {
                            SettingsItem(
                                icon = Icons.Default.Person,
                                label = "Modifier mon profil",
                                onClick = { showEditScreen = true }
                            )
                            SettingsItem(
                                icon = Icons.Default.Lock,
                                label = "Changer mon mot de passe",
                                onClick = { showChangePasswordSheet = true }
                            )
                            SettingsItem(
                                icon = Icons.Default.Edit,
                                label = "Modifier la photo de profil",
                                onClick = { showImagePicker = true }
                            )
                        }
                    }

                    // SECTION 2 : PRÉFÉRENCES DE CONTENUS
                    item {
                        SettingsSection(title = "PRÉFÉRENCES DE CONTENUS") {
                            SettingsItem(
                                icon = Icons.Default.Info,
                                label = "Mes informations",
                                subtitle = "Email, téléphone, rôle",
                                onClick = { /* déjà visible */ }
                            )
                            if (user!!.role == "PRESTATAIRE") {
                                SettingsItem(
                                    icon = Icons.Default.BarChart,
                                    label = "Statistiques",
                                    subtitle = "Prestations, notes, revenus",
                                    onClick = { /* déjà visible */ }
                                )
                            } else {
                                SettingsItem(
                                    icon = Icons.Default.History,
                                    label = "Historique des réservations",
                                    subtitle = "Total, en cours, terminées",
                                    onClick = { /* déjà visible */ }
                                )
                            }
                        }
                    }

                    // SECTION 3 : PLUS
                    item {
                        SettingsSection(title = "PLUS") {
                            SettingsItem(
                                icon = Icons.Default.Notifications,
                                label = "Notifications",
                                subtitle = "Gérer vos alertes",
                                onClick = { Toast.makeText(context, "Notifications à venir", Toast.LENGTH_SHORT).show() }
                            )
                            SettingsItem(
                                icon = Icons.Default.Settings,
                                label = "Confidentialité",
                                onClick = { Toast.makeText(context, "Confidentialité à venir", Toast.LENGTH_SHORT).show() }
                            )
                            SettingsItem(
                                icon = Icons.Default.Info,
                                label = "Version 1.0.0",
                                onClick = { /* rien */ }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // Bottom Sheet pour changement de mot de passe
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

// ─── Composants réutilisables ───

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = Gray500,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    label: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NavyPrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = NavyPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500
                )
            }
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Gray400,
            modifier = Modifier.size(24.dp)
        )
    }
    Divider(
        modifier = Modifier.padding(start = 56.dp),
        color = Gray100,
        thickness = 1.dp
    )
}

// ─── ProfileHeader (avec photo et badge crayon) ───

@Composable
fun ProfileHeader(
    user: User,
    onEditPhotoClick: () -> Unit,
    isLoadingPhoto: Boolean,
    photoUri: Uri?,
    persistedPhotoUrl: String?
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
                                contentDescription = "Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        !persistedPhotoUrl.isNullOrBlank() -> {
                            AsyncImage(
                                model = persistedPhotoUrl,
                                contentDescription = "Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        user.photo != null && user.photo.isNotEmpty() -> {
                            val fullUrl = normalizePhotoUrl(user.photo)
                            AsyncImage(
                                model = fullUrl,
                                contentDescription = "Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Photo",
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

// ─── Fonction utilitaire ───

private fun normalizePhotoUrl(photo: String?): String? {
    if (photo.isNullOrBlank()) return null
    return if (photo.startsWith("http://") || photo.startsWith("https://")) {
        photo
    } else {
        "http://10.0.2.2:8080$photo"
    }
}