package com.capstone.kolabor.app.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.capstone.kolabor.app.data.OnboardingItem
import com.capstone.serviceplatform.app.ui.theme.Gray300
import com.capstone.serviceplatform.app.ui.theme.Gray500
import com.capstone.serviceplatform.app.ui.theme.NavyPrimary
import com.kolabor.app.ui.theme.KolaborTheme
import com.kolabor.app.ui.theme.space16
import com.kolabor.app.ui.theme.space24
import com.kolabor.app.ui.theme.space32
import com.kolabor.app.ui.theme.space48
import com.kolabor.app.ui.theme.space8
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit
) {
    val items = listOf(
        OnboardingItem(
            title = "Trouvez le bon professionnel",
            description = "Recherchez parmi des centaines de prestataires qualifiés, près de chez vous, pour tous vos services à domicile.",
            icon = Icons.Outlined.Search
        ),
        OnboardingItem(
            title = "Réservez en toute simplicité",
            description = "Choisissez votre prestataire, fixez la date et l'heure, et validez votre réservation en quelques clics.",
            icon = Icons.Outlined.CalendarMonth
        ),
        OnboardingItem(
            title = "Une communauté de confiance",
            description = "Notation, avis, litiges : Kolabor garantit la transparence et la fiabilité entre clients et prestataires.",
            icon = Icons.Outlined.ThumbUp
        )
    )

    val pagerState = rememberPagerState(pageCount = { items.size })
    val coroutineScope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage

    KolaborTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = space24, vertical = space48),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Ligne avec "Skip" en haut à droite ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (currentPage < items.size - 1) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(items.size - 1)
                            }
                        }
                    ) {
                        Text(
                            text = "Skip",
                            color = Gray500,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(space32))

            // --- Pager avec les 3 écrans ---
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val item = items[page]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Icône
                    Image(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(180.dp)
                            .background(Gray300, CircleShape)
                            .padding(space24)
                    )

                    Spacer(modifier = Modifier.height(space32))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = NavyPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(space16))

                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Gray500,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // --- Indicateurs (dots) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = space16),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(items.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index) NavyPrimary
                                else Gray300
                            )
                    )
                    if (index < items.size - 1) {
                        Spacer(modifier = Modifier.width(space8))
                    }
                }
            }

            Spacer(modifier = Modifier.height(space16))

            // --- Bouton Next / Get Started ---
            Button(
                onClick = {
                    if (currentPage == items.size - 1) {
                        onGetStarted()
                    } else {
                        coroutineScope.launch {
                            pagerState.scrollToPage(currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyPrimary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (currentPage == items.size - 1) "Get Started" else "Next",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}