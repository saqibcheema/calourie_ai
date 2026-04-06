package com.example.calorieapp.presentation.pages

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorieapp.ui.theme.CharcoalBlack
import com.example.calorieapp.ui.theme.GhostWhite
import com.example.calorieapp.ui.theme.GradientBlue
import com.example.calorieapp.ui.theme.GradientPink
import com.example.calorieapp.ui.theme.PureWhite
import com.example.calorieapp.ui.theme.SlateGrey
import com.example.calorieapp.ui.theme.SuccessGreen
import com.example.calorieapp.ui.theme.ProteinRed
import com.example.calorieapp.ui.theme.CarbsOrange
import com.example.calorieapp.ui.theme.FatsBlue

@Composable
fun StatisticsScreen() {
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            GradientPink,
            GradientBlue,
            PureWhite,
            PureWhite
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                HeaderSection()
            }

            item {
                StatsSummaryCards()
            }

            item {
                WeeklyCaloriesChart()
            }

            item {
                MacroStatsCard()
            }

            item {
                Text(
                    text = "Historical Insights",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalBlack,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(getMockInsights()) { insight ->
                InsightItem(insight)
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp,
                    letterSpacing = (-1).sp
                ),
                color = CharcoalBlack
            )
            Text(
                text = "Tracking your progress beautifully",
                style = MaterialTheme.typography.bodyMedium,
                color = SlateGrey
            )
        }
        
        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = GhostWhite),
            modifier = Modifier.size(48.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Insights, contentDescription = null, tint = CharcoalBlack)
            }
        }
    }
}

@Composable
fun StatsSummaryCards() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            label = "Total Weight Lost",
            value = "3.2 kg",
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            color = SuccessGreen
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            label = "Active Streak",
            value = "12 Days",
            icon = Icons.Default.CalendarMonth,
            color = Color(0xFFFFB74D)
        )
    }
}

@Composable
fun SummaryCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = CharcoalBlack)
            Text(text = label, fontSize = 12.sp, color = SlateGrey, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun WeeklyCaloriesChart() {
    val dataPoints = listOf(1850f, 2100f, 1950f, 2400f, 2050f, 1900f, 2150f)
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    val maxVal = dataPoints.maxOrNull() ?: 1f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Weekly Calorie Intake",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CharcoalBlack
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                dataPoints.forEachIndexed { index, value ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        val barHeight = (value / maxVal)
                        var animationPlayed by remember { mutableStateOf(false) }
                        val animatedScale by animateFloatAsState(
                            targetValue = if (animationPlayed) barHeight else 0f,
                            animationSpec = tween(1000, easing = FastOutSlowInEasing),
                            label = "bar_anim"
                        )
                        LaunchedEffect(Unit) { animationPlayed = true }

                        Box(
                            modifier = Modifier
                                .fillMaxHeight(animatedScale)
                                .width(12.dp)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(CharcoalBlack, CharcoalBlack.copy(alpha = 0.7f))
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = days[index], fontSize = 11.sp, color = SlateGrey, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun MacroStatsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Nutrient Distribution",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CharcoalBlack
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .drawWithContent {
                            drawCircle(color = GhostWhite, radius = size.minDimension / 2, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 30f))
                            drawArc(
                                color = ProteinRed,
                                startAngle = -90f,
                                sweepAngle = 120f,
                                useCenter = false,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 30f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                            )
                            drawArc(
                                color = CarbsOrange,
                                startAngle = 30f,
                                sweepAngle = 150f,
                                useCenter = false,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 30f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                            )
                            drawArc(
                                color = FatsBlue,
                                startAngle = 180f,
                                sweepAngle = 90f,
                                useCenter = false,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 30f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                            )
                        }
                )
                
                Spacer(modifier = Modifier.width(32.dp))
                
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    MacroIndicator(color = ProteinRed, label = "Protein", value = "30%")
                    MacroIndicator(color = CarbsOrange, label = "Carbohydrates", value = "45%")
                    MacroIndicator(color = FatsBlue, label = "Healthy Fats", value = "25%")
                }
            }
        }
    }
}

@Composable
fun MacroIndicator(color: Color, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 13.sp, color = SlateGrey, modifier = Modifier.weight(1f))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CharcoalBlack)
    }
}

@Composable
fun InsightItem(insight: Insight) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GhostWhite.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(PureWhite, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(insight.icon, contentDescription = null, tint = CharcoalBlack, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = insight.title, fontWeight = FontWeight.Bold, color = CharcoalBlack, fontSize = 15.sp)
                Text(text = insight.description, fontSize = 13.sp, color = SlateGrey)
            }
        }
    }
}

data class Insight(val title: String, val description: String, val icon: ImageVector)

fun getMockInsights(): List<Insight> = listOf(
    Insight("Consistency Champ", "You've stayed under your calorie limit 9 times this month.", Icons.Default.Restaurant),
    Insight("Protein Power", "Your protein intake increased by 15% since last week.", Icons.Default.Insights),
    Insight("Weekend Warrior", "Sundays are your most active days with highest burned calories.", Icons.AutoMirrored.Filled.TrendingUp)
)
