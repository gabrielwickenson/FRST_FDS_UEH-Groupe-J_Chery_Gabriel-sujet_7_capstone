package com.capstone.kolabor.app.ui.help

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.serviceplatform.app.ui.theme.*
import com.kolabor.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    val helpItems = listOf(
        HelpItem(
            title = "Réservations",
            description = "Réserver, modifier ou annuler un service.",
            icon = Icons.Default.Bookmark,
            onClick = { Toast.makeText(context, "Ouvrir l'aide sur les réservations", Toast.LENGTH_SHORT).show() }
        ),
        HelpItem(
            title = "Paiements",
            description = "Moyens de paiement, factures et remboursements.",
            icon = Icons.Default.Payment,
            onClick = { Toast.makeText(context, "Ouvrir l'aide sur les paiements", Toast.LENGTH_SHORT).show() }
        ),
        HelpItem(
            title = "Mon compte",
            description = "Profil, sécurité et paramètres du compte.",
            icon = Icons.Default.Person,
            onClick = { Toast.makeText(context, "Ouvrir l'aide sur le compte", Toast.LENGTH_SHORT).show() }
        ),
        HelpItem(
            title = "Devenir professionnel",
            description = "Inscription, profil pro et vérification.",
            icon = Icons.Default.Work,
            onClick = { Toast.makeText(context, "Ouvrir l'aide pour les professionnels", Toast.LENGTH_SHORT).show() }
        ),
        HelpItem(
            title = "Sécurité & litiges",
            description = "Signaler un problème et résoudre un litige.",
            icon = Icons.Default.Security,
            onClick = { Toast.makeText(context, "Ouvrir l'aide sur la sécurité", Toast.LENGTH_SHORT).show() }
        ),
        HelpItem(
            title = "Nous contacter",
            description = "Besoin d'aide ? Notre équipe vous répond.",
            icon = Icons.Default.Email,
            onClick = { Toast.makeText(context, "Ouvrir le contact support", Toast.LENGTH_SHORT).show() }
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Aide",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Gray50)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Champ de recherche (décoratif)
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Rechercher dans l'aide…", color = Gray500) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = NavyPrimary
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = NavyPrimary,
                    unfocusedIndicatorColor = NavyLight,
                    focusedLabelColor = NavyPrimary,
                    unfocusedLabelColor = Gray600,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Gray900,
                    unfocusedTextColor = Gray900,
                    errorIndicatorColor = ErrorColor,
                    errorLabelColor = ErrorColor
                ),
                singleLine = true,
                readOnly = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Liste des sections d'aide
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(helpItems) { item ->
                    HelpCard(
                        item = item
                    )
                }

                // Carte "Vous ne trouvez pas votre réponse ?"
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Vous ne trouvez pas votre réponse ?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Notre équipe support est disponible 7j/7.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Gray500
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Contacter le support", Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NavyPrimary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Contacter le support",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Données ───
data class HelpItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

// ─── Carte d'aide ───
@Composable
fun HelpCard(item: HelpItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icône
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(NavyLightest, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            // Texte
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray600
                )
            }

            // Flèche
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Gray400
            )
        }
    }
}