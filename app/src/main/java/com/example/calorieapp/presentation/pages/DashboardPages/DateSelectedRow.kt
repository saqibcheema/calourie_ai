package com.example.calorieapp.presentation.pages.DashboardPages

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@SuppressLint("NewApi")
@Composable
fun DateSelectorRow() {
    val today = LocalDate.now()
    val dates = (-3..3).map { today.plusDays(it.toLong()) }

    LazyRow(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        items(dates) { date ->
            val isSelected = date == today
            val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            val dayNumber = date.dayOfMonth.toString()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = dayName,
                    color = if (isSelected) Color.Black else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Circle styling
                val circleModifier = if (isSelected) {
                    Modifier
                        .size(40.dp)
                        .background(Color.White, CircleShape)
                } else {
                    Modifier
                        .size(40.dp)
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = CircleShape
                        )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = circleModifier
                ) {
                    Text(
                        text = dayNumber,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.Black else Color.Gray
                    )
                }
            }
        }
    }
}