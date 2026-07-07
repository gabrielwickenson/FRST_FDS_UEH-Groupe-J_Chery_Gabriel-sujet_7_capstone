package com.capstone.kolabor.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.serviceplatform.app.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun RevenueChart(
    data: Map<String, Double>, // Jour -> Revenu
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Gray100, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aucune donnée disponible",
                style = MaterialTheme.typography.bodyMedium,
                color = Gray500
            )
        }
        return
    }

    val totalRevenue = data.values.sum()
    val maxValue = data.values.maxOrNull() ?: 1.0
    val days = data.keys.toList()
    val values = data.values.toList()

    // Animation des hauteurs
    val animatedHeights = remember { values.map { Animatable(0f) } }
    LaunchedEffect(Unit) {
        animatedHeights.forEachIndexed { index, anim ->
            anim.animateTo(
                targetValue = (values[index] / maxValue).toFloat(),
                animationSpec = tween(durationMillis = 600, delayMillis = index * 60)
            )
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // --- En-tête ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Revenus des 7 derniers jours",
                        style = MaterialTheme.typography.titleMedium,
                        color = NavyPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Total : ${totalRevenue.roundToInt()} Gdes",
                        style = MaterialTheme.typography.headlineSmall,
                        color = NavyPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                // Badge tendance (simulé)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GreenPrimary.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "▲ +12%",
                            style = MaterialTheme.typography.labelMedium,
                            color = GreenPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Graphique ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Gray50, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    // Barres fines : largeur = 30% de l'espace dispo
                    val barWidth = (canvasWidth / days.size) * 0.30f
                    val spacing = (canvasWidth / days.size) * 0.70f

                    // Dessiner chaque barre
                    days.forEachIndexed { index, day ->
                        val x = index * (barWidth + spacing) + spacing / 2
                        val height = animatedHeights[index].value * canvasHeight * 0.85f
                        val y = canvasHeight - height

                        // Couleur dégradée selon la valeur
                        val isMax = values[index] == maxValue
                        val barColor = if (isMax) GreenPrimary else NavyPrimary.copy(alpha = 0.7f)

                        // Barre
                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(x, y),
                            size = androidx.compose.ui.geometry.Size(barWidth, height),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                                x = 4.dp.toPx(),
                                y = 4.dp.toPx()
                            )
                        )

                        // Valeur au-dessus (uniquement si > 0)
                        if (values[index] > 0) {
                            drawContext.canvas.nativeCanvas.apply {
                                val paint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.parseColor("#6B7280")
                                    textSize = 9.dp.toPx()
                                    textAlign = android.graphics.Paint.Align.CENTER
                                    isAntiAlias = true
                                }
                                drawText(
                                    "${values[index].roundToInt()}",
                                    x + barWidth / 2,
                                    y - 4.dp.toPx(),
                                    paint
                                )
                            }
                        }

                        // Libellé du jour (plus petit, plus fin)
                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint().apply {
                                color = android.graphics.Color.parseColor("#9CA3AF")
                                textSize = 9.dp.toPx()
                                textAlign = android.graphics.Paint.Align.CENTER
                                isAntiAlias = true
                            }
                            drawText(
                                day,
                                x + barWidth / 2,
                                canvasHeight + 14.dp.toPx(),
                                paint
                            )
                        }
                    }

                    // Ligne de base (fine, pointillée)
                    val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                    drawLine(
                        color = Gray200,
                        start = Offset(0f, canvasHeight),
                        end = Offset(canvasWidth, canvasHeight),
                        strokeWidth = 1f,
                        pathEffect = dashPathEffect
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- Légende ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(GreenPrimary, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Pic d'activité",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray500
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(NavyPrimary.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Revenus",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray500
                )
            }
        }
    }
}