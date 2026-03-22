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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
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
fun DateSelectorRow(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val systemToday = LocalDate.now()
    val startOfMonth = systemToday.withDayOfMonth(1)
    val lengthOfMonth = systemToday.lengthOfMonth()
    val dates = (0 until lengthOfMonth).map { startOfMonth.plusDays(it.toLong()) }

    val listState = rememberLazyListState()

    LaunchedEffect(selectedDate) {
        val index = dates.indexOf(selectedDate)
        if (index >= 0) {
            listState.animateScrollToItem(maxOf(0, index - 3))
        }
    }

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        items(dates) { date ->
            val isSelected = date == selectedDate
            val isPastOrToday = !date.isAfter(systemToday)
            val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            val dayNumber = date.dayOfMonth.toString()

            val columnModifier = Modifier.padding(horizontal = 4.dp).let {
                if (isPastOrToday) {
                    it.clickable { onDateSelected(date) }
                } else {
                    it
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = columnModifier
            ) {
                Text(
                    text = dayName,
                    color = if (isSelected) Color.Black else if (isPastOrToday) Color.Gray else Color.LightGray,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Circle styling
                val circleModifier = if (isSelected) {
                    Modifier
                        .size(40.dp)
                        .background(Color.Black, CircleShape)
                } else {
                    Modifier
                        .size(40.dp)
                        .background(Color.White, CircleShape)
                        .border(
                            width = 1.dp,
                            color = if (isPastOrToday) Color.LightGray else Color.LightGray.copy(alpha = 0.5f),
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
                        color = if (isSelected) Color.White else if (isPastOrToday) Color.Black else Color.LightGray
                    )
                }
            }
        }
    }
}