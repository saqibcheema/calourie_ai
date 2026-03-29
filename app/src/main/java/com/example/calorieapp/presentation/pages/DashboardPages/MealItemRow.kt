package com.example.calorieapp.presentation.pages.DashboardPages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.graphicsLayer
import coil.compose.AsyncImage
import com.example.calorieapp.domain.entities.Product
import com.example.calorieapp.ui.theme.GhostWhite
import com.example.calorieapp.ui.theme.ProteinRed
import com.example.calorieapp.ui.theme.SuccessGreen
import com.example.calorieapp.ui.theme.SuccessLightGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealItemRow(
    product: Product,
    onIncreaseQuantity: () -> Unit,
    onDecreaseOrDelete: () -> Unit
) {
    val haptics = LocalHapticFeedback.current

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onDecreaseOrDelete()
                true
            } else {
                false
            }
        },
        positionalThreshold = { distance -> distance * 0.5f }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val progress = dismissState.progress
            val isThresholdReached = progress > 0.5f

            // Dynamic background transparency
            val revealAlpha by animateFloatAsState(
                targetValue = if (isThresholdReached) 1f else (progress * 1.5f).coerceIn(0f, 0.8f),
                label = "RevealAlpha"
            )

            // Glassmorphic Base + Red Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.1f)) // Glass base
                    .background(ProteinRed.copy(alpha = revealAlpha)) // Dynamic red reveal
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                // Scaling Icon
                val iconScale by animateFloatAsState(
                    targetValue = if (isThresholdReached) 1.3f else (0.8f + progress).coerceIn(0.8f, 1.2f),
                    label = "IconScale"
                )

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    }
                )
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Product Image or Fallback Icon
                if (!product.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.productName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(GhostWhite)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(SuccessLightGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = "Meal",
                            tint = SuccessGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Text info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.productName ?: "Unknown Product",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val brandText = if (!product.brand.isNullOrEmpty()) "${product.brand} • " else ""
                    Text(
                        text = "$brandText${(product.calories * product.quantity).toInt()} kcal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }

                // Quantity controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (product.quantity > 1) {
                        IconButton(onClick = onDecreaseOrDelete, modifier = Modifier.size(32.dp)) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = MaterialTheme.colorScheme.primary)
                        }
                        Text(
                            text = product.quantity.toString(),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                    IconButton(onClick = onIncreaseQuantity, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
