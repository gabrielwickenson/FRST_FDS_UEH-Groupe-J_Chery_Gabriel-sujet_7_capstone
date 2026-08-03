package com.capstone.kolabor.app.ui.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.HomeRepairService
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.kolabor.app.data.model.Service
import com.capstone.serviceplatform.app.ui.theme.*
import com.kolabor.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    service: Service,
    onBack: () -> Unit,
    onReserveService: (Service) -> Unit   // ✅ Seul callback nécessaire
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = service.nom,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = NavyPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Gray50,
        bottomBar = {
            // ─── Barre sticky : tarif + bouton réserver ───
            Surface(
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = space24, vertical = space16),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tarif",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gray500
                        )
                        Text(
                            text = "Sur devis",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )
                    }
                    // ✅ Bouton Réserver → appelle onReserveService
                    Button(
                        onClick = { onReserveService(service) },
                        modifier = Modifier.height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Text(
                            text = "Réserver ce service",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // ─── Fil d'Ariane ─── (inchangé)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = space24, vertical = space8),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Services", style = MaterialTheme.typography.bodySmall, color = Gray400)
                Text(text = " / ", style = MaterialTheme.typography.bodySmall, color = Gray400)
                Text(
                    text = service.categorie ?: "Général",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray400
                )
                Text(text = " / ", style = MaterialTheme.typography.bodySmall, color = Gray400)
                Text(
                    text = service.nom,
                    style = MaterialTheme.typography.bodySmall,
                    color = NavyPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }

            // ─── Image de couverture ─── (inchangé)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(horizontal = space24)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(colors = listOf(NavyPrimary, NavyLight))
                    )
            ) {
                Icon(
                    imageVector = Icons.Outlined.HomeRepairService,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center)
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(space12)
                ) {
                    Text(
                        text = service.categorie ?: "Service",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = NavyPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(space16))

            // ─── Miniatures ─── (inchangé)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = space24),
                horizontalArrangement = Arrangement.spacedBy(space8)
            ) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        NavyLighter.copy(alpha = 0.4f),
                                        GreenLighter.copy(alpha = 0.3f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.HomeRepairService,
                            contentDescription = null,
                            tint = NavyPrimary.copy(alpha = 0.4f),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(space24))

            // ─── Titre + badge ─── (inchangé)
            Column(modifier = Modifier.padding(horizontal = space24)) {
                Text(
                    text = service.nom,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Spacer(modifier = Modifier.height(space8))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GreenLightest
                ) {
                    Text(
                        text = "⚡ Intervention rapide",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = GreenPrimary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(space24))

            // ─── Description + inclus ─── (inchangé)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = space24)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Spacer(modifier = Modifier.height(space8))
                Text(
                    text = service.description ?: "Aucune description disponible pour ce service.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Gray700,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Gray200, thickness = 1.dp)
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Ce qui est inclus",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Spacer(modifier = Modifier.height(space12))

                val inclusions = listOf(
                    "Diagnostic complet avant intervention",
                    "Réparation ou remplacement des pièces",
                    "Test de bon fonctionnement après service",
                    "Garantie 30 jours"
                )
                inclusions.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(GreenLightest),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(space12))
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gray700
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(space32))
        }
    }
}