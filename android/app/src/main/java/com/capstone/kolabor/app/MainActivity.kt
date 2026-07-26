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
import com.capstone.kolabor.app.data.repository.UserRepository
import com.capstone.serviceplatform.app.ui.theme.Gray300
import com.capstone.serviceplatform.app.ui.theme.NavyPrimary
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.google.firebase.messaging.FirebaseMessaging
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope

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

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "🔥 Token : $token")
                Toast.makeText(this, "Token FCM récupéré", Toast.LENGTH_SHORT).show()

                GlobalScope.launch(Dispatchers.IO) {
                    val tokenManager = TokenManager(applicationContext)
                    val userId = tokenManager.getUserId()
                    Log.d("FCM", "📋 ID utilisateur : $userId")
                    if (userId != null && userId != 0L) {
                        val userRepo = UserRepository(applicationContext)
                        val success = userRepo.updateFcmToken(userId, token)
                        if (success) {
                            Log.d("FCM", "✅ Token envoyé au backend")
                        } else {
                            Log.e("FCM", "❌ Échec envoi token (backend)")
                        }
                    } else {
                        Log.e("FCM", "❌ ID utilisateur null ou 0, impossible d'envoyer")
                    }
                }
            } else {
                Log.w("FCM", "Échec token", task.exception)
                Toast.makeText(this, "Erreur FCM", Toast.LENGTH_SHORT).show()
            }
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
    val userId = remember { mutableStateOf<Long?>(null) }

    // États pour ClientDashboard
    val clientTab = remember { mutableStateOf(0) }
    val showPrestataireDetail = remember { mutableStateOf(false) }
    val selectedPrestataire = remember { mutableStateOf<Prestataire?>(null) }
    val showBookingBottomSheet = remember { mutableStateOf(false) }
    val selectedPrestataireForBooking = remember { mutableStateOf<Prestataire?>(null) }
    val showReservations = remember { mutableStateOf(false) }
    val showReservationDetail = remember { mutableStateOf(false) }
    val selectedReservation = remember { mutableStateOf<Reservation?>(null) }
    val userRepository = remember { UserRepository(context) }
    val userName = remember { mutableStateOf<String?>(null) }

    // ✅ Nouveaux états pour le paiement
    val showPayment = remember { mutableStateOf(false) }
    val selectedReservationForPayment = remember { mutableStateOf<Reservation?>(null) }
    val selectedPaymentMode = remember { mutableStateOf<String?>(null) }
    val userPhoto = remember { mutableStateOf<String?>(null) }

    // Charger la session au démarrage
    LaunchedEffect(Unit) {
        tokenManager.getUserRole().collectLatest { role ->
            val token = tokenManager.getToken()
            if (role != null && token != null) {
                userRole.value = role
                isLoggedIn.value = true
                showOnboarding.value = false
                userId.value = tokenManager.getUserId() ?: 0L
                userName.value = tokenManager.getUserName() ?: "Utilisateur"
                // ✅ Récupération de la photo
                userPhoto.value = tokenManager.getUserPhoto()
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
                                showPayment.value && selectedReservationForPayment.value != null -> {
                                    PaymentScreen(
                                        reservation = selectedReservationForPayment.value!!,
                                        clientId = userId.value ?: 0L,
                                        onPaymentSuccess = { reservation, mode ->
                                            // On peut afficher le reçu ou juste fermer
                                            selectedReservationForPayment.value = reservation
                                            selectedPaymentMode.value = mode
                                            // Ici vous pouvez afficher le reçu (ou un toast)
                                            Toast.makeText(context, "Paiement effectué avec $mode", Toast.LENGTH_LONG).show()
                                            showPayment.value = false
                                            selectedReservationForPayment.value = null
                                        },
                                        onBack = {
                                            showPayment.value = false
                                            selectedReservationForPayment.value = null
                                        }
                                    )
                                }
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
                                        },
                                        onNavigateToPayment = { reservation ->
                                            selectedReservationForPayment.value = reservation
                                            showPayment.value = true
                                        },
                                        userName = userName.value ?: "Client",
                                        userPhoto = userPhoto.value   // ✅ Passer la photo
                                    )
                                }
                            }
                        }
                        "PRESTATAIRE" -> {
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
                                userId = userId.value ?: 0L,
                                userName = userName.value ?: "Prestataire",
                                userPhoto = userPhoto.value
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
                    LoginScreen(
                        onLoginSuccess = { role, id ->
                            Log.d("MainActivity", "✅ Rôle : $role, ID : $id")
                            userRole.value = role
                            isLoggedIn.value = true
                            showLogin.value = false
                            userId.value = id
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

            // Overlays
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
                        },
                        onPay = { reservation ->   // ✅ Ajout du callback
                            selectedReservationForPayment.value = reservation
                            showPayment.value = true
                            showReservationDetail.value = false  // Ferme le détail
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
                            onNavigateToPayment = { reservation ->
                                // ✅ Rediriger vers l'écran de paiement
                                selectedReservationForPayment.value = reservation
                                showPayment.value = true
                                showBookingBottomSheet.value = false
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