package com.kolabor.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.capstone.kolabor.app.ui.auth.LoginScreen
import com.capstone.kolabor.app.ui.auth.RegisterScreen
import com.capstone.kolabor.app.ui.client.*
import com.capstone.kolabor.app.ui.dashboard.ClientDashboard
import com.capstone.kolabor.app.ui.dashboard.PrestataireDashboard
import com.capstone.kolabor.app.ui.onboarding.OnboardingScreen
import com.kolabor.app.ui.theme.KolaborTheme
import com.capstone.kolabor.app.utils.TokenManager
import com.capstone.kolabor.app.data.model.Prestataire
import com.capstone.kolabor.app.data.model.Reservation
import com.capstone.serviceplatform.app.ui.theme.Gray300
import com.kolabor.app.ui.theme.*
import com.capstone.serviceplatform.app.ui.theme.NavyPrimary
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("Notifications", "✅ Permission de notification accordée")
        } else {
            Log.d("Notifications", "❌ Permission de notification refusée")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            KolaborApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KolaborApp() {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val coroutineScope = rememberCoroutineScope()

    // États de navigation
    val showOnboarding = remember { mutableStateOf(true) }
    val showLogin = remember { mutableStateOf(false) }
    val showRegister = remember { mutableStateOf(false) }
    val isLoggedIn = remember { mutableStateOf(false) }
    val userRole = remember { mutableStateOf<String?>(null) }
    val userId = remember { mutableStateOf<Long?>(null) } // ✅ Renommé pour tous les rôles

    // États pour ClientDashboard
    val clientTab = remember { mutableStateOf(0) }
    val showPrestataireDetail = remember { mutableStateOf(false) }
    val selectedPrestataire = remember { mutableStateOf<Prestataire?>(null) }
    val showBookingBottomSheet = remember { mutableStateOf(false) }
    val selectedPrestataireForBooking = remember { mutableStateOf<Prestataire?>(null) }
    val showReservations = remember { mutableStateOf(false) }
    val showReservationDetail = remember { mutableStateOf(false) }
    val selectedReservation = remember { mutableStateOf<Reservation?>(null) }

    // Charger la session au démarrage
    LaunchedEffect(Unit) {
        tokenManager.getUserRole().collectLatest { role ->
            val token = tokenManager.getToken()
            if (role != null && token != null) {
                userRole.value = role
                isLoggedIn.value = true
                showOnboarding.value = false
                // ✅ Récupérer l'ID pour TOUS les rôles
                userId.value = tokenManager.getUserId() ?: 0L
            }
        }
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    KolaborTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoggedIn.value && userRole.value != null -> {
                    when (userRole.value) {
                        "CLIENT" -> {
                            when {
                                showPrestataireDetail.value && selectedPrestataire.value != null -> {
                                    PrestataireDetailScreen(
                                        prestataire = selectedPrestataire.value!!,
                                        onBack = {
                                            showPrestataireDetail.value = false
                                            selectedPrestataire.value = null
                                        },
                                        onReserver = {
                                            selectedPrestataireForBooking.value = selectedPrestataire.value
                                            showBookingBottomSheet.value = true
                                        }
                                    )
                                }
                                else -> {
                                    ClientDashboard(
                                        onLogout = {
                                            coroutineScope.launch {
                                                tokenManager.clearSession()
                                                isLoggedIn.value = false
                                                userRole.value = null
                                                userId.value = null
                                                showLogin.value = true
                                            }
                                        },
                                        clientId = userId.value ?: 0L,
                                        onNavigateToBook = { /* géré ailleurs */ },
                                        showPrestataireDetail = showPrestataireDetail,
                                        selectedPrestataire = selectedPrestataire,
                                        currentTab = clientTab,
                                        onTabChanged = { newTab -> clientTab.value = newTab },
                                        onNavigateToReservations = {
                                            coroutineScope.launch {
                                                showReservations.value = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        "PRESTATAIRE" -> {
                            // ✅ PASSER L'ID ICI
                            PrestataireDashboard(
                                onLogout = {
                                    coroutineScope.launch {
                                        tokenManager.clearSession()
                                        isLoggedIn.value = false
                                        userRole.value = null
                                        userId.value = null
                                        showLogin.value = true
                                    }
                                },
                                userId = userId.value ?: 0L   // ← ID passé !
                            )
                        }
                        else -> {
                            showLogin.value = true
                            isLoggedIn.value = false
                        }
                    }
                }
                showOnboarding.value -> {
                    OnboardingScreen(
                        onGetStarted = {
                            showOnboarding.value = false
                            showLogin.value = true
                        }
                    )
                }
                showLogin.value -> {
                    // ✅ Signature corrigée : onLoginSuccess reçoit (role, id)
                    LoginScreen(
                        onLoginSuccess = { role, id ->
                            Log.d("MainActivity", "✅ Rôle : $role, ID : $id")
                            userRole.value = role
                            isLoggedIn.value = true
                            showLogin.value = false
                            userId.value = id
                            // Sauvegarder dans TokenManager pour les autres usages
                            coroutineScope.launch {
                                tokenManager.saveUserId(id)
                                tokenManager.saveUserRole(role)
                            }
                        },
                        onNavigateToRegister = {
                            showLogin.value = false
                            showRegister.value = true
                        }
                    )
                }
                showRegister.value -> {
                    RegisterScreen(
                        onNavigateToLogin = {
                            showRegister.value = false
                            showLogin.value = true
                        },
                        onRegisterSuccess = {
                            showRegister.value = false
                            showLogin.value = true
                        }
                    )
                }
                else -> {
                    OnboardingScreen(onGetStarted = {})
                }
            }

            // Overlays (inchangés)
            if (showReservations.value) {
                Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
                    ReservationsScreen(
                        onBack = { showReservations.value = false },
                        clientId = userId.value ?: 0L,
                        onReservationClick = { reservation ->
                            selectedReservation.value = reservation
                            showReservationDetail.value = true
                            showReservations.value = false
                        }
                    )
                }
            }

            if (showReservationDetail.value && selectedReservation.value != null) {
                Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
                    ReservationDetailScreen(
                        reservation = selectedReservation.value!!,
                        clientId = userId.value ?: 0L,
                        onBack = {
                            showReservationDetail.value = false
                            selectedReservation.value = null
                        },
                        onCancel = {
                            showReservationDetail.value = false
                            selectedReservation.value = null
                            Toast.makeText(context, "Réservation annulée", Toast.LENGTH_LONG).show()
                        },
                        onReview = {
                            Toast.makeText(context, "Ouvrir le formulaire d'avis", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            if (showBookingBottomSheet.value && selectedPrestataireForBooking.value != null) {
                ModalBottomSheet(
                    onDismissRequest = { showBookingBottomSheet.value = false },
                    sheetState = rememberModalBottomSheetState(),
                    containerColor = Color.White,
                    dragHandle = {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier.width(40.dp).height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)).background(Gray300)
                            )
                        }
                    }
                ) {
                    Column(modifier = Modifier.fillMaxWidth().height(screenHeight * 0.75f)) {
                        BookScreen(
                            onBack = { showBookingBottomSheet.value = false },
                            onBookingSuccess = {
                                showBookingBottomSheet.value = false
                                Toast.makeText(context, "Réservation réussie !", Toast.LENGTH_LONG).show()
                            },
                            prestataire = selectedPrestataireForBooking.value!!,
                            clientId = userId.value ?: 0L
                        )
                    }
                }
            }
        }
    }
}