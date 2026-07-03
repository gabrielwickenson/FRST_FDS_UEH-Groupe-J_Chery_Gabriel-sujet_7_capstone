package com.kolabor.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.capstone.kolabor.app.ui.client.BookScreen
import com.capstone.kolabor.app.ui.client.PrestataireDetailScreen
import com.capstone.kolabor.app.ui.dashboard.ClientDashboard
import com.capstone.kolabor.app.ui.dashboard.PrestataireDashboard
import com.capstone.kolabor.app.ui.onboarding.OnboardingScreen
import com.kolabor.app.ui.theme.KolaborTheme
import com.capstone.kolabor.app.utils.TokenManager
import com.capstone.kolabor.app.data.model.Prestataire
import com.capstone.serviceplatform.app.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    val clientId = remember { mutableStateOf<Long?>(null) }

    // États pour le détail du prestataire
    val showPrestataireDetail = remember { mutableStateOf(false) }
    val selectedPrestataire = remember { mutableStateOf<Prestataire?>(null) }

    // États pour le Bottom Sheet de réservation
    val showBookingBottomSheet = remember { mutableStateOf(false) }
    val selectedPrestataireForBooking = remember { mutableStateOf<Prestataire?>(null) }

    // Ajoutez cette ligne avec les autres états
    val clientTab = remember { mutableStateOf(0) }

    // Charger la session au démarrage
    LaunchedEffect(Unit) {
        tokenManager.getUserRole().collectLatest { role ->
            val token = tokenManager.getToken()
            if (role != null && token != null) {
                userRole.value = role
                isLoggedIn.value = true
                showOnboarding.value = false
                if (role == "CLIENT") {
                    clientId.value = tokenManager.getUserId() ?: 0L
                }
            }
        }
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    KolaborTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            // ─── CONTENU PRINCIPAL ───
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
                                                clientId.value = null
                                                showLogin.value = true
                                            }
                                        },
                                        clientId = clientId.value ?: 0L,
                                        onNavigateToBook = { prestataireId ->
                                            // Ce callback est déclenché depuis l'accueil ou l'explorer
                                            // On va chercher le prestataire dans la liste (on va le passer via un état partagé)
                                            // Ici on va simplement ouvrir le détail avec un prestataire factice (à améliorer)
                                        },
                                        showPrestataireDetail = showPrestataireDetail,
                                        selectedPrestataire = selectedPrestataire,

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
                                        showLogin.value = true
                                    }
                                }
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
                        onLoginSuccess = { role ->
                            userRole.value = role
                            isLoggedIn.value = true
                            showLogin.value = false
                            coroutineScope.launch {
                                val id = tokenManager.getUserId()
                                if (id != null && role == "CLIENT") {
                                    clientId.value = id
                                }
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

            // ─── BOTTOM SHEET DE RÉSERVATION (3/4 de l'écran) ───
            if (showBookingBottomSheet.value && selectedPrestataireForBooking.value != null) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBookingBottomSheet.value = false
                    },
                    sheetState = rememberModalBottomSheetState(),
                    containerColor = Color.White,
                    dragHandle = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 0.75f) // 75% de l'écran
                    ) {
                        BookScreen(
                            onBack = {
                                showBookingBottomSheet.value = false
                            },
                            onBookingSuccess = {
                                showBookingBottomSheet.value = false
                                Toast.makeText(context, "Réservation réussie !", Toast.LENGTH_LONG).show()
                            },
                            prestataire = selectedPrestataireForBooking.value!!,
                            clientId = clientId.value ?: 0L
                        )
                    }
                }
            }
        }
    }
}