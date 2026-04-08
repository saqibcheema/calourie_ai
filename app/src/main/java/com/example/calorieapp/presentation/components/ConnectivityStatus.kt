package com.example.calorieapp.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorieapp.ui.theme.*

@Composable
fun PremiumStatusPill(
    isVisible: Boolean,
    message: String,
    icon: ImageVector,
    dotColor: Color,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp, start = 20.dp, end = 20.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(50), spotColor = CharcoalBlack.copy(alpha = 0.2f)),
                color = GlassWhite,
                shape = RoundedCornerShape(50)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatusDot(color = dotColor)

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = CharcoalBlack.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )

                    Text(
                        text = message,
                        style = AppTypography.labelLarge,
                        color = CharcoalBlack,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumConnectivityStatus(
    isOffline: Boolean,
    modifier: Modifier = Modifier
) {
    PremiumStatusPill(
        isVisible = isOffline,
        message = "No Internet Connection",
        icon = Icons.Default.WifiOff,
        dotColor = OfflineRed,
        modifier = modifier
    )
}

@Composable
fun PremiumRateLimitStatus(
    message: String?,
    modifier: Modifier = Modifier
) {
    val isVisible = message != null
    PremiumStatusPill(
        isVisible = isVisible,
        message = message ?: "",
        icon = Icons.Default.Timer,
        dotColor = Color(0xFFE6A23C), // A cautionary orange
        modifier = modifier
    )
}

@Composable
private fun StatusDot(color: androidx.compose.ui.graphics.Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "dotPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(8.dp)
            .background(color.copy(alpha = alpha), shape = CircleShape)
            .padding(1.dp)
    )
}
