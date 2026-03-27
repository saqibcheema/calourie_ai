package com.example.calorieapp.presentation.pages.DashboardPages.ManualEntry.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.example.calorieapp.presentation.viewModel.ManualEntryState
import com.example.calorieapp.presentation.viewModel.ManualEntryViewModel
import com.example.calorieapp.ui.theme.AppTypography

@Composable
fun DescriptionSection(
    state: ManualEntryState,
    viewModel: ManualEntryViewModel
) {
    val rotation by animateFloatAsState(if (state.isDescriptionExpanded) 180f else 0f, label = "rotate")
    
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.toggleDescriptionExpansion() }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Anything extra to add?", modifier = Modifier.weight(1f), style = AppTypography.titleMedium)
            Icon(
                Icons.Default.ExpandMore, 
                contentDescription = null, 
                modifier = Modifier.rotate(rotation)
            )
        }
        
        AnimatedVisibility(visible = state.isDescriptionExpanded) {
            OutlinedTextField(
                value = state.extraDescription,
                onValueChange = { viewModel.onDescriptionChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                placeholder = { Text("e.g. very oily, had extra gravy, shared with someone...") },
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )
        }
    }
}
