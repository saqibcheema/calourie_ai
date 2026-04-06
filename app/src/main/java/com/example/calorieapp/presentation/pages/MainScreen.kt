package com.example.calorieapp.presentation.pages

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.calorieapp.presentation.pages.DashboardPages.AiVision.AiVisionScreen
import com.example.calorieapp.presentation.pages.DashboardPages.NutritionSheetContent
import com.example.calorieapp.presentation.pages.DashboardPages.Scanner.ScannerFeatureScreen

private val FabDark = Color(0xFF1C1C22)
private val NavPillBg = Color(0xFFFFFFFF)
private val NavActiveHighlight = Color(0xFFF0F0F5)
private val NavInactiveIcon = Color(0xFFAAAAAA)
private val NavActiveIcon = Color(0xFF1C1C22)

data class NavItem(val route: String, val icon: ImageVector, val label: String)

private val navItems = listOf(
    NavItem("dashboard_route", Icons.Default.Home, "Home"),
    NavItem("statistics_route", Icons.AutoMirrored.Filled.TrendingUp, "Stats"),
    NavItem("profile_route", Icons.Default.Person, "Profile")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToManualEntry: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var showNutritionSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main content
        NavHost(
            navController = navController,
            startDestination = "dashboard_route",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("dashboard_route") {
                DashboardScreen(onNavigateToManualEntry = onNavigateToManualEntry)
            }
            composable("statistics_route") {
                StatisticsScreen()
            }
            composable("profile_route") {
                ProfileScreen()
            }
            composable("scanner_route") {
                ScannerFeatureScreen(onClose = { navController.popBackStack() })
            }
            composable("aivision_route") {
                AiVisionScreen(onClose = { navController.popBackStack() })
            }
        }

        val hideBottomBarRoutes = listOf("scanner_route", "aivision_route")
        val showBottomBar = currentDestination?.route in listOf("dashboard_route", "profile_route", "statistics_route") && currentDestination?.route !in hideBottomBarRoutes

        // Floating bottom dock overlay
        AnimatedVisibility(
            visible = showBottomBar,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            FloatingBottomDockWithFab(
                currentDestination = currentDestination,
                onNavigate = { dest ->
                    navController.navigate(dest) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onFabClick = { showNutritionSheet = true }
            )
        }

        if (showNutritionSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showNutritionSheet = false },
                sheetState = sheetState,
                containerColor = Color.White,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                NutritionSheetContent(
                    onScanClick = {
                        showNutritionSheet = false
                        navController.navigate("scanner_route")
                    },
                    onManualEntryClick = {
                        showNutritionSheet = false
                        onNavigateToManualEntry()
                    },
                    onAiVisionClick = {
                        showNutritionSheet = false
                        navController.navigate("aivision_route")
                    }
                )
            }
        }
    }
}

@Composable
fun FloatingBottomDockWithFab(
    currentDestination: androidx.navigation.NavDestination?,
    onNavigate: (String) -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = modifier
            .padding(start = 24.dp, end = 24.dp, bottom = 32.dp)
            .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier
                .height(64.dp)
                .weight(1f)
                .shadow(16.dp, RoundedCornerShape(32.dp), spotColor = Color(0x33000000)),
            shape = RoundedCornerShape(32.dp),
            color = NavPillBg
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                navItems.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    PillNavItem(
                        icon = item.icon,
                        label = item.label,
                        isSelected = isSelected,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigate(item.route)
                        }
                    )
                }
            }
        }

        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val fabScale by animateFloatAsState(
            targetValue = if (isPressed) 0.88f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
            label = "fab_scale"
        )

        Box(
            modifier = Modifier
                .size(64.dp)
                .graphicsLayer { 
                    scaleX = fabScale
                    scaleY = fabScale 
                }
                .shadow(16.dp, CircleShape, spotColor = Color(0x55000000))
                .clip(CircleShape)
                .background(FabDark)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onFabClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Meal",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun PillNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) NavActiveHighlight else Color.Transparent,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing), label = "nav_bg"
    )
    val iconTint by animateColorAsState(
        targetValue = if (isSelected) NavActiveIcon else NavInactiveIcon,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing), label = "nav_tint"
    )
    val horizontalPadding by animateDpAsState(
        targetValue = if (isSelected) 16.dp else 12.dp,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing), label = "padding_h"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = horizontalPadding, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn(tween(350)) + expandHorizontally(
                animationSpec = tween(350, easing = FastOutSlowInEasing),
                expandFrom = Alignment.Start
            ),
            exit = fadeOut(tween(250)) + shrinkHorizontally(
                animationSpec = tween(250, easing = FastOutSlowInEasing),
                shrinkTowards = Alignment.Start
            )
        ) {
            Text(
                text = label,
                color = NavActiveIcon,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 1
            )
        }
    }
}
