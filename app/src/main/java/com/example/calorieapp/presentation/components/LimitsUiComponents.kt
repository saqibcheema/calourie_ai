package com.example.calorieapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorieapp.ui.theme.AppTypography
import com.example.calorieapp.ui.theme.CharcoalBlack

@Composable
fun LimitIndicatorBadge(
    remaining: Int,
    modifier: Modifier = Modifier
) {
    // If it's 999, it means unlimited (Premium)
    if (remaining >= 999) return

    val color = if (remaining > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.3f), CircleShape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$remaining Left Today",
            style = AppTypography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

@Composable
fun PremiumRequiredDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onUpgradeClick: () -> Unit
) {
    if (!showDialog) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700), // Gold
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Upgrade to Premium",
                    style = AppTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = CharcoalBlack
                )
            }
        },
        text = {
            Text(
                text = "You have reached your daily free limit. Unlock unlimited barcode scans, manual entries, and the powerful AI Vision scanner by upgrading to Premium!",
                style = AppTypography.bodyMedium,
                color = CharcoalBlack.copy(alpha = 0.8f)
            )
        },
        confirmButton = {
            Button(
                onClick = onUpgradeClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View Plans", style = AppTypography.labelLarge, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Not Now", style = AppTypography.labelLarge, color = CharcoalBlack.copy(alpha = 0.6f))
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

@Composable
fun AiVisionLockedOverlay(
    onUpgradeClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "AI Vision is Locked",
                style = AppTypography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Identify any food instantly with your camera. This is a Premium-only feature.",
                style = AppTypography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onUpgradeClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Unlock Premium", style = AppTypography.titleMedium, color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = onBackClick) {
                Text("Go Back", style = AppTypography.labelLarge, color = Color.White.copy(alpha = 0.6f))
            }
        }
    }
}
