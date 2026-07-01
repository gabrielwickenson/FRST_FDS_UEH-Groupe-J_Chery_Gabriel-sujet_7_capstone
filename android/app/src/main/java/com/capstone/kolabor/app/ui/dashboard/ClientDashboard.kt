package com.capstone.kolabor.app.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import com.kolabor.app.ui.theme.space8

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDashboard(onLogout: () -> Unit,
                    onNavigateToSearch: () -> Unit   // ✅ callback ajouté
) {
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

            Text(
                text = "Bonjour, Client !",
                style = MaterialTheme.typography.headlineMedium,
                color = NavyPrimary
            )
            Text(
                text = "Trouvez et gérez vos services en un clin d'œil.",
                style = MaterialTheme.typography.bodyLarge,
                color = Gray500
            )
            Spacer(modifier = Modifier.height(space32))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Gray50),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(space16),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // ✅ Utilisation de weight avec l'import explicite
                    DashboardActionCard(
                        icon = Icons.Default.Search,
                        label = "Rechercher",
                        onClick = onNavigateToSearch,  // ✅ appel du callback
                        modifier = Modifier.weight(1f)
                    )
                    DashboardActionCard(
                        icon = Icons.Default.History,
                        label = "Mes réservations",
                        onClick = { /* Naviguer vers historique */ },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(space32))

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

@Composable
fun DashboardActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier // ✅ on passe le modificateur weight de la Row
) {
    Card(
        modifier = modifier
            .padding(horizontal = space8), // Le weight est maintenant appliqué depuis l'appel
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(space16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = NavyPrimary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(space8))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = NavyPrimary
            )
        }
    }
}