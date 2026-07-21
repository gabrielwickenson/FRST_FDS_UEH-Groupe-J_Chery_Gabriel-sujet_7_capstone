package com.capstone.kolabor.app.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.serviceplatform.app.ui.theme.NavyPrimary
import com.kolabor.app.R
import kotlinx.coroutines.launch

private val NavyPrimary = Color(0xFF19355F)
private val Slate900 = Color(0xFF0F172A)
private val Slate500 = Color(0xFF64748B)
private val Slate300 = Color(0xFFCBD5E1)

data class OnboardingItem(
    val title: String,
    val description: String,
    val illustrationRes: Int
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit
) {
    val items = remember {
        listOf(
            OnboardingItem(
                title = "Trouvez le bon professionnel",
                description = "Recherchez parmi des prestataires qualifiés, près de chez vous, pour tous vos services à domicile.",
                illustrationRes = R.drawable.onboarding_1
            ),
            OnboardingItem(
                title = "Réservez en toute simplicité",
                description = "Choisissez votre prestataire, fixez la date et l'heure, et validez votre réservation en quelques clics.",
                illustrationRes = R.drawable.onboarding_2
            ),
            OnboardingItem(
                title = "Une communauté de confiance",
                description = "Notation, avis, litiges : Kolabor garantit transparence et fiabilité entre clients et prestataires.",
                illustrationRes = R.drawable.onboarding_3
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { items.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate900)
    ) {
        // --- ZONE SUPÉRIEURE : LE PAGER FLUIDE ---
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.68f)
        ) { page ->
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = items[page].illustrationRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // CORRECTION : Utilisation de Color.Transparent au lieu de Transient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.2f)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Slate900.copy(alpha = 0.4f))
                            )
                        )
                )
            }
        }

        // --- BOUTON PASSER (En haut à droite) ---
        if (pagerState.currentPage < items.size - 1) {
            TextButton(
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(items.size - 1) }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Passer",
                    color = NavyPrimary,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    ),
                    modifier = Modifier.shadow(2.dp, CircleShape, clip = false)
                )
            }
        }

        // --- ZONE INFÉRIEURE : FEUILLE BLANCHE ARRONDIE ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.38f)
                .align(Alignment.BottomCenter)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Barre de préhension (Handlebar)
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 4.dp)
                        .clip(CircleShape)
                        .background(Slate300.copy(alpha = 0.6f))
                )

                // Groupe Textes
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = items[pagerState.currentPage].title,
                        style = TextStyle(
                            fontSize = 24.sp,
                            lineHeight = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Slate900,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = items[pagerState.currentPage].description,
                        style = TextStyle(
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            color = Slate500,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Pied de page : Points + Bouton
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Les points animés de pagination
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items.forEachIndexed { index, _ ->
                            val isActive = index == pagerState.currentPage

                            // CORRECTION TECHNIQUE : Syntaxe robuste sans le délégué "by" pour éviter les conflits d'import
                            val widthAnimation = animateDpAsState(
                                targetValue = if (isActive) 22.dp else 7.dp,
                                animationSpec = tween(durationMillis = 300),
                                label = "dotWidth"
                            )
                            val colorAnimation = animateColorAsState(
                                targetValue = if (isActive) NavyPrimary else Slate300,
                                animationSpec = tween(durationMillis = 300),
                                label = "dotColor"
                            )

                            Box(
                                modifier = Modifier
                                    .size(width = widthAnimation.value, height = 7.dp)
                                    .clip(CircleShape)
                                    .background(colorAnimation.value)
                            )
                        }
                    }

                    // CORRECTION : Bouton principal entièrement refermé
                    Button(
                        onClick = {
                            if (pagerState.currentPage == items.size - 1) {
                                onGetStarted()
                            } else {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .widthIn(min = 140.dp),
                    ) {
                        Text(
                            text = if (pagerState.currentPage == items.size - 1) "Commencer" else "Suivant",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}