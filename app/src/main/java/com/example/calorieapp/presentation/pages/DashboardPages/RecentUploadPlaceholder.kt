package com.example.calorieapp.presentation.pages.DashboardPages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorieapp.ui.theme.GhostWhite
import com.example.calorieapp.ui.theme.SuccessGreen
import com.example.calorieapp.ui.theme.SuccessLightGreen

@Composable
fun RecentUploadPlaceholder() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Placeholder for Salad Image
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(SuccessLightGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = "Meal",
                        tint = SuccessGreen
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Box(modifier = Modifier.width(120.dp).height(8.dp).background(GhostWhite, CircleShape))
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.width(80.dp).height(8.dp).background(GhostWhite, CircleShape))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = GhostWhite)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tap + to add your first meal of the day",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}