package com.example.calorieapp.presentation.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.calorieapp.presentation.viewModel.ProfileViewModel
import com.example.calorieapp.ui.theme.CharcoalBlack
import com.example.calorieapp.ui.theme.GhostWhite
import com.example.calorieapp.ui.theme.GradientBlue
import com.example.calorieapp.ui.theme.GradientPink
import com.example.calorieapp.ui.theme.PureWhite
import com.example.calorieapp.ui.theme.SlateGrey
import com.example.calorieapp.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val haptic = LocalHapticFeedback.current

    // Premium ambient background matching Dashboard
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
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 140.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            item {
                ProfileHeader()
            }

            item {
                PhysicalStatsSection(viewModel = viewModel)
            }

            item {
                ActivityLevelSection(
                    selectedLevel = viewModel.activityLevel,
                    onLevelSelected = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.activityLevel = it
                    }
                )
            }

            item {
                GoalSection(
                    selectedGoal = viewModel.goal,
                    onGoalSelected = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.goal = it
                    }
                )
            }

            item {
                SaveButtonSection(viewModel = viewModel, haptic = haptic)
            }
        }
    }
}

@Composable
fun SaveButtonSection(viewModel: ProfileViewModel, haptic: androidx.compose.ui.hapticfeedback.HapticFeedback) {
    var showSuccess by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "button_scale"
    )

    val buttonGradient = Brush.horizontalGradient(
        colors = if (showSuccess) listOf(SuccessGreen, SuccessGreen.copy(alpha = 0.8f))
        else listOf(CharcoalBlack, CharcoalBlack.copy(alpha = 0.8f))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Button(
            onClick = {
                if (viewModel.isSaving || showSuccess) return@Button
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.saveProfile()
                
                scope.launch {
                    delay(600) // Simulate processing time for UX weight
                    showSuccess = true
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    delay(2000)
                    showSuccess = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            interactionSource = interactionSource,
            contentPadding = PaddingValues(0.dp),
            enabled = !viewModel.isSaving
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(buttonGradient)
                    .clip(RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AnimatedVisibility(visible = showSuccess) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        }
                        Text(
                            text = if (showSuccess) "Goals Synchronized" else "Save & Update Profile",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "My Profile",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            ),
            color = CharcoalBlack
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Personalize your goals and metrics",
            style = MaterialTheme.typography.bodyLarge,
            color = SlateGrey,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PhysicalStatsSection(viewModel: ProfileViewModel) {
    SectionContainer(title = "Physical Metrics") {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EditorialInputField(
                    modifier = Modifier.weight(1f),
                    label = "Age",
                    value = viewModel.age.toString(),
                    onValueChange = { viewModel.age = it.toIntOrNull() ?: viewModel.age },
                    suffix = "yrs"
                )
                EditorialInputField(
                    modifier = Modifier.weight(1f),
                    label = "Weight",
                    value = viewModel.weight,
                    onValueChange = { viewModel.weight = it },
                    suffix = "kg"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EditorialInputField(
                    modifier = Modifier.weight(1f),
                    label = "Height (Ft)",
                    value = viewModel.feetForHeight.toString(),
                    onValueChange = { viewModel.feetForHeight = it.toIntOrNull() ?: viewModel.feetForHeight },
                    suffix = "ft"
                )
                EditorialInputField(
                    modifier = Modifier.weight(1f),
                    label = "Inches",
                    value = viewModel.inchesForHeight.toString(),
                    onValueChange = { viewModel.inchesForHeight = it.toIntOrNull() ?: viewModel.inchesForHeight },
                    suffix = "in"
                )
            }
        }
    }
}

@Composable
fun ActivityLevelSection(
    selectedLevel: String,
    onLevelSelected: (String) -> Unit
) {
    SectionContainer(title = "Activity Routine") {
        val options = listOf(
            "No Exercise" to Icons.Default.AirlineSeatReclineNormal,
            "Low Activity" to Icons.Default.DirectionsWalk,
            "Moderate Activity" to Icons.Default.DirectionsRun,
            "High Activity" to Icons.Default.FitnessCenter
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            options.forEach { (level, icon) ->
                InteractiveRow(
                    title = level,
                    icon = icon,
                    isSelected = selectedLevel == level,
                    onClick = { onLevelSelected(level) }
                )
            }
        }
    }
}

@Composable
fun GoalSection(
    selectedGoal: String,
    onGoalSelected: (String) -> Unit
) {
    SectionContainer(title = "Primary Objective") {
        val goals = listOf("Lose Weight", "Maintain", "Gain Weight")
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            goals.forEach { goal ->
                val isSelected = selectedGoal == goal
                
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.95f else 1f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                    label = "goal_scale"
                )

                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) CharcoalBlack else GhostWhite,
                    animationSpec = tween(300), label = "goal_bg"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) PureWhite else CharcoalBlack,
                    animationSpec = tween(300), label = "goal_text"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clip(RoundedCornerShape(20.dp))
                        .background(bgColor)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onGoalSelected(goal) }
                        )
                        .padding(vertical = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = goal,
                        color = textColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InteractiveRow(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "row_scale"
    )

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) GhostWhite else PureWhite,
        animationSpec = tween(200), label = "bg_color"
    )
    
    val contentColor by animateColorAsState(
        targetValue = CharcoalBlack,
        animationSpec = tween(200), label = "content_color"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(
                width = 1.dp,
                color = if (isSelected) CharcoalBlack else GhostWhite,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = CharcoalBlack,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = CharcoalBlack,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun EditorialInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = SlateGrey,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(GhostWhite)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextFieldContent(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = CharcoalBlack
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Text(
                text = suffix,
                style = MaterialTheme.typography.bodyMedium,
                color = SlateGrey,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun BasicTextFieldContent(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: androidx.compose.ui.text.TextStyle,
    keyboardOptions: KeyboardOptions
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        cursorBrush = androidx.compose.ui.graphics.SolidColor(CharcoalBlack)
    )
}

@Composable
fun SectionContainer(
    title: String,
    content: @Composable () -> Unit
) {
    // Dashboard matched card style
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = CharcoalBlack,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            content()
        }
    }
}
