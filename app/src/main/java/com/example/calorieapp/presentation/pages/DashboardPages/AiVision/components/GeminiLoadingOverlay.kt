package com.example.calorieapp.presentation.pages.DashboardPages.AiVision.components

import android.graphics.Bitmap
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorieapp.ui.theme.AppTypography

@Composable
fun GeminiLoadingOverlay(capturedBitmap: Bitmap?) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Blurred captured photo as background
        if (capturedBitmap != null) {
            Image(
                bitmap = capturedBitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(20.dp),
                contentScale = ContentScale.Crop
            )
        }

        // Dark scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
        )

        // Content
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GeminiPulsingIcon()

            Spacer(modifier = Modifier.height(28.dp))

            GeminiAnalyzingText()
        }
    }
}

@Composable
private fun GeminiPulsingIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "geminiPulse")

    val outerScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "outerScale"
    )
    val outerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "outerAlpha"
    )
    val iconRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        ),
        label = "iconRotation"
    )

    // Gemini-themed gradient colors: blue-purple-pink
    val geminiGradient = Brush.sweepGradient(
        colors = listOf(
            Color(0xFF4285F4), // Google Blue
            Color(0xFF9C27B0), // Purple
            Color(0xFFE91E63), // Pink
            Color(0xFF4285F4)  // back to blue
        )
    )

    Box(contentAlignment = Alignment.Center) {
        // Outer glow ring
        Box(
            modifier = Modifier
                .size(130.dp)
                .scale(outerScale)
                .alpha(outerAlpha)
                .background(Color(0xFF4285F4), shape = CircleShape)
        )

        // Mid ring with gradient
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF4285F4).copy(alpha = 0.25f),
                            Color(0xFF9C27B0).copy(alpha = 0.15f)
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Core circle
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(brush = geminiGradient, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Gemini AI",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}

@Composable
private fun GeminiAnalyzingText() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val dotsCount by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dotsAnim"
    )
    val dots = ".".repeat(dotsCount.toInt() + 1)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(horizontal = 40.dp)
    ) {
        Text(
            text = "Identifying your meal$dots",
            style = AppTypography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center
        )

        // Gemini badge
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.15f),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = "✦ Powered by Gemini Flash 2.0",
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                style = AppTypography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp
                )
            )
        }
    }
}
