package com.capstone.kolabor.app.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kolabor.app.R
import com.capstone.serviceplatform.app.ui.theme.ErrorColor
import com.capstone.serviceplatform.app.ui.theme.Gray50
import com.capstone.serviceplatform.app.ui.theme.Gray500
import com.capstone.serviceplatform.app.ui.theme.NavyPrimary
import com.kolabor.app.ui.theme.space16
import com.kolabor.app.ui.theme.space24
import com.kolabor.app.ui.theme.space32

@OptIn(ExperimentalMaterial3Api::class) // ✅ Supprime l'avertissement expérimental
@Composable
fun PrestataireDashboard(onLogout: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kolabor",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyPrimary
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Déconnexion",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = space24, vertical = space32)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_kolabor_svg),
                contentDescription = "Logo Kolabor",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(space16))

            // Message de bienvenue
            Text(
                text = "Bonjour, Prestataire !",
                style = MaterialTheme.typography.headlineMedium,
                color = NavyPrimary
            )
            Text(
                text = "Gérez vos disponibilités et suivez vos interventions.",
                style = MaterialTheme.typography.bodyLarge,
                color = Gray500
            )
            Spacer(modifier = Modifier.height(space32))

            // Actions principales
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Gray50),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(space16)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DashboardActionCard(
                            icon = Icons.Default.CalendarToday,
                            label = "Disponibilités",
                            onClick = { /* Naviguer vers gestion dispo */ }
                        )
                        DashboardActionCard(
                            icon = Icons.Default.Star,
                            label = "Statistiques",
                            onClick = { /* Naviguer vers stats */ }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(space32))

            // Bouton déconnexion
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorColor,
                    contentColor = Color.White
                )
            ) {
                Text("Déconnexion", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}