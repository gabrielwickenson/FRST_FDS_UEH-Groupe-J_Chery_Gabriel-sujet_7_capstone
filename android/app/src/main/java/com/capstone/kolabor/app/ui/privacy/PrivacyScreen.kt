package com.capstone.kolabor.app.ui.privacy

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.serviceplatform.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Confidentialité",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour",
                            tint = NavyPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Gray50
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ─── SECTION 1 : COMPTE ET SÉCURITÉ ───
            item { SectionHeader("Compte et sécurité") }
            item {
                PrivacyCard {
                    PrivacyItem(
                        icon = Icons.Default.Visibility,
                        label = "Visibilité du profil",
                        subtitle = "Public, privé ou sur invitation"
                    )
                    Divider(color = Gray100, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
                    PrivacyToggleItem(
                        icon = Icons.Default.Shield,
                        label = "Authentification à deux facteurs",
                        subtitle = "Sécurisez votre compte",
                        initialChecked = false
                    )
                    Divider(color = Gray100, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
                    PrivacyItem(
                        icon = Icons.Default.Lock,
                        label = "Modifier le mot de passe",
                        subtitle = "Dernière mise à jour il y a 2 mois"
                    )
                }
            }

            // ─── SECTION 2 : DONNÉES PERSONNELLES ───
            item { SectionHeader("Données personnelles") }
            item {
                PrivacyCard {
                    PrivacyItem(
                        icon = Icons.Default.Download,
                        label = "Télécharger mes données",
                        subtitle = "Obtenir une copie de vos informations"
                    )
                    Divider(color = Gray100, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
                    PrivacyItem(
                        icon = Icons.Default.Delete,
                        label = "Supprimer mon compte",
                        subtitle = "Définitif et irréversible",
                        destructive = true
                    )
                }
            }

            // ─── SECTION 3 : COOKIES ET TRACAGE ───
            item { SectionHeader("Cookies et traçage") }
            item {
                PrivacyCard {
                    PrivacyToggleItem(
                        icon = Icons.Default.Cookie,
                        label = "Cookies fonctionnels",
                        subtitle = "Nécessaires au bon fonctionnement",
                        initialChecked = true
                    )
                    Divider(color = Gray100, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
                    PrivacyToggleItem(
                        icon = Icons.Default.Analytics,
                        label = "Cookies d'analyse",
                        subtitle = "Amélioration de l'expérience utilisateur",
                        initialChecked = false
                    )
                    Divider(color = Gray100, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
                    PrivacyToggleItem(
                        icon = Icons.Default.AdsClick,
                        label = "Publicité personnalisée",
                        subtitle = "Offres adaptées à vos centres d'intérêt",
                        initialChecked = false
                    )
                }
            }

            // ─── SECTION 4 : AIDE ───
            item { SectionHeader("Aide") }
            item {
                PrivacyCard {
                    PrivacyItem(
                        icon = Icons.Default.QuestionAnswer,
                        label = "FAQ confidentialité",
                        subtitle = "Questions fréquemment posées"
                    )
                    Divider(color = Gray100, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
                    PrivacyItem(
                        icon = Icons.Default.SupportAgent,
                        label = "Contacter le support",
                        subtitle = "Une question sur vos données ?"
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Kolabor s'engage à protéger vos données conformément au RGPD.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ─── Composants réutilisables ───

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = Gray600,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun PrivacyCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(content = content)
    }
}

@Composable
private fun PrivacyItem(
    icon: ImageVector,
    label: String,
    subtitle: String? = null,
    destructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Action à définir plus tard */ }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (destructive) ErrorColor else NavyPrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (destructive) ErrorColor else NavyPrimary,
                fontWeight = if (destructive) FontWeight.SemiBold else FontWeight.Normal
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
}

@Composable
private fun PrivacyToggleItem(
    icon: ImageVector,
    label: String,
    subtitle: String? = null,
    initialChecked: Boolean
) {
    var checked by remember { mutableStateOf(initialChecked) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { checked = !checked }
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
        Column(modifier = Modifier.weight(1f)) {
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
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = NavyPrimary,
                checkedTrackColor = NavyPrimary.copy(alpha = 0.5f),
                uncheckedThumbColor = Gray400,
                uncheckedTrackColor = Gray300
            )
        )
    }
}