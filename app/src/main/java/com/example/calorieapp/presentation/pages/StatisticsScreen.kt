package com.example.calorieapp.presentation.pages

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.calorieapp.presentation.viewModel.StatisticsViewModel
import com.example.calorieapp.domain.entities.DailyMacrosSummary
import com.example.calorieapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val monthlyStats by viewModel.monthlyCalories.collectAsState()
    val avgMonthlySummary by viewModel.averageMonthlySummary.collectAsState()
    val goals by viewModel.dailyGoals.collectAsState()

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(GradientPink, GradientBlue, PureWhite, PureWhite)
    )

    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible.value = true }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        val screenWidth = maxWidth
        val isTablet = screenWidth > 600.dp
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(
                start = if (isTablet) 48.dp else 20.dp, 
                end = if (isTablet) 48.dp else 20.dp, 
                top = 40.dp, 
                bottom = 120.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val contentModifier = if (isTablet) Modifier.widthIn(max = 600.dp) else Modifier.fillMaxWidth()

            item {
                AnimatedVisibility(
                    visible = visible.value,
                    enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { -20 }
                ) {
                    Box(contentModifier) { HeaderSection() }
                }
            }

            // 1. Consistency Heatmap (GitHub Style)
            item {
                AnimatedVisibility(
                    visible = visible.value,
                    enter = fadeIn(tween(400, delayMillis = 100)) + slideInVertically(tween(400, delayMillis = 100)) { 20 }
                ) {
                    Box(contentModifier) {
                        ConsistencyHeatmapSection(
                            monthlyStats = monthlyStats,
                            targetCalories = goals?.calories ?: 2000,
                            screenWidth = screenWidth
                        )
                    }
                }
            }

            // 2. Calorie Balance Section (Refactored Axes)
            item {
                AnimatedVisibility(
                    visible = visible.value,
                    enter = fadeIn(tween(400, delayMillis = 200)) + slideInVertically(tween(400, delayMillis = 200)) { 20 }
                ) {
                    Box(contentModifier) {
                        CalorieBalanceSection(monthlyStats, goals?.calories ?: 2000)
                    }
                }
            }

            // 3. Goal Consistency (Renamed from Nutrition Adherence)
            item {
                AnimatedVisibility(
                    visible = visible.value,
                    enter = fadeIn(tween(400, delayMillis = 300)) + slideInVertically(tween(400, delayMillis = 300)) { 20 }
                ) {
                    Box(contentModifier) {
                        MacroConsistencySection(avgMonthlySummary, goals)
                    }
                }
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
                text = "Performance",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp,
                    letterSpacing = (-1).sp
                ),
                color = CharcoalBlack
            )
            Text(
                text = "Your journey in high-fidelity",
                style = MaterialTheme.typography.bodyMedium,
                color = SlateGrey
            )
        }

        Surface(
            shape = CircleShape,
            color = GhostWhite,
            modifier = Modifier.size(48.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Insights, contentDescription = null, tint = CharcoalBlack)
            }
        }
    }
}

@Composable
fun ConsistencyHeatmapSection(
    monthlyStats: Map<String, Double>,
    targetCalories: Int,
    screenWidth: Dp
) {
    val calendar = Calendar.getInstance()
    val currentMonthStr = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    // Calculate start offset (0 = Monday)
    val firstDayCal = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }
    val firstDayOfWeek = firstDayCal.get(Calendar.DAY_OF_WEEK)
    val startOffset = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2
    
    val totalCells = daysInMonth + startOffset
    
    val cellSize: Dp = 38.dp
    val cellGap: Dp = 6.dp
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = CalorieOrange,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Monthly Streak",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalBlack
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentMonthStr,
                fontSize = 12.sp,
                color = SlateGrey
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable row for the actual heatmap content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(cellGap)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(cellGap),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    dayLabels.forEach { label ->
                        Box(
                            modifier = Modifier.size(width = 14.dp, height = cellSize),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = label,
                                fontSize = 9.sp,
                                color = SlateGrey,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                val weeks = Math.ceil(totalCells.toDouble() / 7).toInt()
                for (week in 0 until weeks) {
                    Column(verticalArrangement = Arrangement.spacedBy(cellGap)) {
                        for (dayOfWeek in 0..6) {
                            val cellIndex = week * 7 + dayOfWeek
                            val dayNumber = cellIndex - startOffset + 1

                            if (dayNumber in 1..daysInMonth) {
                                val dateCal = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, dayNumber) }
                                val dateStr = dateFormat.format(dateCal.time)
                                val intake = monthlyStats[dateStr] ?: -1.0

                                val cellColor = when {
                                    intake < 0 -> Color.Transparent
                                    intake == 0.0 -> GhostWhite
                                    intake <= targetCalories * 0.30 -> DisabledGrey
                                    intake < targetCalories -> CharcoalBlack.copy(alpha = 0.45f)
                                    else -> CharcoalBlack
                                }

                                val hasBorder = intake < 0 || intake == 0.0

                                Box(
                                    modifier = Modifier
                                        .size(cellSize)
                                        .clip(RoundedCornerShape(8.dp))
                                        .then(
                                            if (hasBorder)
                                                Modifier.border(
                                                    width = 1.dp,
                                                    color = DisabledGrey,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                            else Modifier
                                        )
                                        .background(cellColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayNumber.toString(),
                                        fontSize = 12.sp,
                                        color = if (hasBorder) SlateGrey else PureWhite,
                                        fontWeight = if (hasBorder) FontWeight.Medium else FontWeight.Bold
                                    )
                                }
                            } else {
                                Box(modifier = Modifier.size(cellSize).background(Color.Transparent))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text("Less", fontSize = 10.sp, color = SlateGrey)
                Spacer(modifier = Modifier.width(6.dp))
                listOf(
                    GhostWhite,
                    DisabledGrey,
                    CharcoalBlack.copy(alpha = 0.45f),
                    CharcoalBlack
                ).forEach { shade ->
                    Spacer(modifier = Modifier.width(3.dp))
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .border(1.dp, DisabledGrey, RoundedCornerShape(3.dp))
                            .background(shade)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("More", fontSize = 10.sp, color = SlateGrey)
            }
        }
    }
}

@Composable
fun CalorieBalanceSection(monthlyStats: Map<String, Double>, target: Int) {
    val barBelowColor = CharcoalBlack
    val barAboveColor = ProteinRed

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Calorie Balance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CharcoalBlack
            )
            Spacer(modifier = Modifier.height(20.dp))

            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val displayFmt = SimpleDateFormat("MMM d", Locale.getDefault())

            val last7 = (0..6).map {
                val date = dateFormat.format(calendar.time)
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                date
            }.reversed()

            val ySteps = 3
            val yMax = target * 1.5

            fun yLabel(value: Double): String = when {
                value >= 1000 -> "${(value / 1000).toInt()}k"
                else -> value.toInt().toString()
            }

            val chartHeight = 160.dp

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .width(36.dp)
                        .height(chartHeight),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in ySteps downTo 0) {
                        val v = yMax * i / ySteps
                        Text(
                            text = yLabel(v),
                            fontSize = 9.sp,
                            color = SlateGrey,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(chartHeight),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    last7.forEach { date ->
                        val intake = monthlyStats[date] ?: 0.0
                        val ratio = (intake / yMax).coerceIn(0.0, 1.0).toFloat()
                        val display = try {
                            val parsed = dateFormat.parse(date)
                            if (parsed != null) displayFmt.format(parsed) else date.takeLast(5)
                        } catch (e: Exception) {
                            date.takeLast(5)
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            val animatedRatio by animateFloatAsState(
                                targetValue = ratio,
                                animationSpec = tween(600, easing = EaseOutCubic),
                                label = "barAnim"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .fillMaxHeight(animatedRatio.coerceAtLeast(0.01f))
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(if (intake > target) barAboveColor else barBelowColor)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = display,
                                fontSize = 8.sp,
                                color = SlateGrey,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MacroConsistencySection(
    summary: DailyMacrosSummary?,
    goals: com.example.calorieapp.domain.entities.DailyGoals?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TrackChanges,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Monthly Goal Consistency",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = CharcoalBlack
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            MacroLinearProgress(
                label = "Protein",
                current = summary?.totalProtein ?: 0.0,
                target = goals?.protein?.toDouble() ?: 100.0,
                color = ProteinRed
            )
            Spacer(modifier = Modifier.height(16.dp))
            MacroLinearProgress(
                label = "Carbs",
                current = summary?.totalCarbs ?: 0.0,
                target = goals?.carbs?.toDouble() ?: 200.0,
                color = CarbsOrange
            )
            Spacer(modifier = Modifier.height(16.dp))
            MacroLinearProgress(
                label = "Fats",
                current = summary?.totalFats ?: 0.0,
                target = goals?.fats?.toDouble() ?: 70.0,
                color = FatsBlue
            )
        }
    }
}

@Composable
fun MacroLinearProgress(label: String, current: Double, target: Double, color: Color) {
    val progress = (current / target).coerceIn(0.0, 1.0).toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "macroAnim"
    )
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, fontSize = 14.sp, color = SlateGrey)
            Text(
                text = "${current.toInt()}g / ${target.toInt()}g",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalBlack
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = color,
            trackColor = GhostWhite
        )
    }
}
