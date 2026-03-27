package com.example.calorieapp.presentation.pages.DashboardPages.ManualEntry.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.calorieapp.presentation.viewModel.ManualEntryState
import com.example.calorieapp.presentation.viewModel.ManualEntryViewModel
import com.example.calorieapp.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhereDidYouEatSection(
    state: ManualEntryState,
    viewModel: ManualEntryViewModel
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SegmentedChip(
                label = "Restaurant",
                icon = Icons.Default.Restaurant,
                isSelected = state.whereEat == "Restaurant",
                onClick = { viewModel.onWhereEatChange("Restaurant") },
                modifier = Modifier.weight(1f)
            )
            SegmentedChip(
                label = "Home Cooked",
                icon = Icons.Default.Home,
                isSelected = state.whereEat == "Home Cooked",
                onClick = { viewModel.onWhereEatChange("Home Cooked") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.foodName,
            onValueChange = { viewModel.onFoodNameChange(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Food name") },
            placeholder = { Text(if (state.whereEat == "Restaurant") "e.g. Chicken Biryani" else "e.g. Scrambled Eggs") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.whereEat == "Restaurant") {
            Text("Restaurant type", style = AppTypography.labelMedium, color = Color.Gray)
            FlowRow(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Desi", "Fast Food", "Cafe", "Bakery").forEach { type ->
                    val isSelected = state.restaurantType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onRestaurantTypeChange(type) },
                        label = { Text(type) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = Color.White,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = MaterialTheme.colorScheme.outlineVariant,
                            selectedBorderColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Visible extras", style = AppTypography.labelMedium, color = Color.Gray)
            FlowRow(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Raita", "Naan", "Sauce", "Salad", "None").forEach { extra ->
                    val isSelected = state.visibleExtras.contains(extra)
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.toggleVisibleExtra(extra) },
                        label = { Text(extra) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = Color.White,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = MaterialTheme.colorScheme.outlineVariant,
                            selectedBorderColor = Color.Transparent
                        )
                    )
                }
            }
        } else {
            // Home Cooked fields
            var expandedCookingMethod by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedCookingMethod,
                onExpandedChange = { expandedCookingMethod = !expandedCookingMethod }
            ) {
                OutlinedTextField(
                    value = state.cookingMethod,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cooking method") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCookingMethod) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = Color.Gray
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedCookingMethod,
                    onDismissRequest = { expandedCookingMethod = false }
                ) {
                    listOf("Raw", "Boiled", "Grilled", "Fried", "Baked", "Steamed").forEach { method ->
                        DropdownMenuItem(
                            text = { Text(method) },
                            onClick = {
                                viewModel.onCookingMethodChange(method)
                                expandedCookingMethod = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Did you add oil or butter?", modifier = Modifier.weight(1f), style = AppTypography.bodyMedium)
                Switch(checked = state.addOil, onCheckedChange = { viewModel.onAddOilChange(it) })
            }

            if (state.addOil) {
                FlowRow(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("None", "A little (1 tsp)", "Normal (1 tbsp)").forEach { amount ->
                        val isSelected = state.oilAmount == amount
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onOilAmountChange(amount) },
                            label = { Text(amount) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = Color.White,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MaterialTheme.colorScheme.outlineVariant,
                                selectedBorderColor = Color.Transparent
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Extras added", style = AppTypography.labelMedium, color = Color.Gray)
            FlowRow(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Sauce", "Cheese", "Butter", "Chutney", "None").forEach { extra ->
                    val isSelected = state.extrasAdded.contains(extra)
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.toggleExtraAdded(extra) },
                        label = { Text(extra) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = Color.White,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = MaterialTheme.colorScheme.outlineVariant,
                            selectedBorderColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}
