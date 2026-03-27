package com.example.calorieapp.presentation.pages.DashboardPages.ManualEntry.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorieapp.presentation.viewModel.ManualEntryState
import com.example.calorieapp.presentation.viewModel.ManualEntryViewModel
import com.example.calorieapp.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortionSizeSection(
    state: ManualEntryState,
    viewModel: ManualEntryViewModel
) {
    Column {
        AnimatedVisibility(visible = state.isPortionTypeExpanded) {
            Column {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val types = listOf("Grams", "Plate size", "Pieces", "Slices", "Volume (ml)")
                    types.forEach { type ->
                        val isSelected = state.portionType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onPortionTypeChange(type) },
                            label = { Text(type, maxLines = 1, fontSize = 12.sp) },
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
            }
        }

        // Dynamic Input UI
        when (state.portionType) {
            "Grams" -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Slider(
                        value = state.gramsValue.toFloat(),
                        onValueChange = { viewModel.onGramsChange(it.toInt()) },
                        valueRange = 50f..600f,
                        steps = 21,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${state.gramsValue}g",
                        style = AppTypography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
            "Plate size" -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val plates = listOf("Small (~250g)", "Medium (~400g)", "Large (~600g)")
                    plates.forEach { plate ->
                        val isSelected = state.plateSize == plate
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onPlateSizeChange(plate) },
                            label = { 
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(plate.split(" (")[0])
                                    Text(plate.split(" (")[1].dropLast(1), style = AppTypography.labelSmall, color = Color.Gray)
                                }
                            },
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
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            "Pieces" -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Stepper(value = state.piecesCount, onValueChange = { viewModel.onPiecesCountChange(it) })
                    Text("Each piece ~120 kcal (AI will calculate)", style = AppTypography.labelSmall, color = Color.Gray)
                }
            }
            "Slices" -> {
                Column {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Personal", "Small", "Medium", "Large", "Family").forEach { size ->
                            val isSelected = state.slicesSize == size
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.onSlicesSizeChange(size) },
                                label = { Text(size, fontSize = 12.sp) },
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
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("How many slices?", style = AppTypography.labelMedium)
                    Stepper(value = state.slicesCount, onValueChange = { viewModel.onSlicesCountChange(it) })
                }
            }
            "Volume (ml)" -> {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Small cup (150ml)", "Regular (250ml)", "Large (400ml)").forEach { vol ->
                        val isSelected = state.volumeSize == vol
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onVolumeSizeChange(vol) },
                            label = { 
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(vol.split(" (")[0], maxLines = 1, fontSize = 12.sp)
                                    Text(vol.split(" (")[1].dropLast(1), style = AppTypography.labelSmall)
                                }
                            },
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

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
            onClick = { viewModel.togglePortionTypeExpansion() },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text(
                text = if (state.isPortionTypeExpanded) "Change completed ↑" else "Not the right unit? Change portion type ↓", 
                style = AppTypography.labelMedium
            )
        }
    }
}
