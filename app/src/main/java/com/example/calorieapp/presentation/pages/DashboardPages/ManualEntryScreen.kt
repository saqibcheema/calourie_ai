package com.example.calorieapp.presentation.pages.DashboardPages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calorieapp.domain.validation.ManualEntryValidator
import com.example.calorieapp.ui.theme.AppTypography
import com.example.calorieapp.ui.theme.CharcoalBlack
import com.example.calorieapp.ui.theme.PureWhite
import com.example.calorieapp.ui.theme.SlateGrey

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEntryScreen(
    onBackClick: () -> Unit
) {
    var mealName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var isGrams by remember { mutableStateOf(true) }
    var description by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manual Input", color = CharcoalBlack) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CharcoalBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        containerColor = PureWhite
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Text(
                text = "Add Meal Details",
                style = AppTypography.headlineMedium.copy(color = CharcoalBlack)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enter what you ate to analyze nutritional value and macro-nutrients automatically. Our AI will use these details to estimate calories, proteins, fats, and carbs precisely.",
                style = AppTypography.bodyMedium.copy(color = SlateGrey)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 1. Meal Name
            OutlinedTextField(
                value = mealName,
                onValueChange = { 
                    mealName = it
                    errorMessage = null 
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Meal Name") },
                placeholder = { Text("Example: Grilled Chicken Salad") },
                isError = errorMessage != null,
                colors = defaultTextFieldColors()
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Quantity & Type Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { 
                        quantity = it
                        errorMessage = null 
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text(if (isGrams) "Weight (g)" else "Portion") },
                    placeholder = { Text(if (isGrams) "100" else "1 plate") },
                    isError = errorMessage != null,
                    keyboardOptions = if (isGrams) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
                    colors = defaultTextFieldColors()
                )

                Row(
                    modifier = Modifier.height(56.dp).border(1.dp, SlateGrey, RoundedCornerShape(8.dp))
                ) {
                    TextButton(onClick = { isGrams = true; errorMessage = null }, colors = ButtonDefaults.textButtonColors(contentColor = if (isGrams) CharcoalBlack else SlateGrey)) {
                        Text("Grams", fontWeight = if (isGrams) FontWeight.Bold else FontWeight.Normal)
                    }
                    TextButton(onClick = { isGrams = false; errorMessage = null }, colors = ButtonDefaults.textButtonColors(contentColor = if (!isGrams) CharcoalBlack else SlateGrey)) {
                        Text("Portions", fontWeight = if (!isGrams) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Optional Description
            OutlinedTextField(
                value = description,
                onValueChange = { 
                    description = it
                    errorMessage = null 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                label = { Text("Description (Optional)") },
                placeholder = { Text("Example: Had a bit of ranch dressing on top.") },
                isError = errorMessage != null,
                colors = defaultTextFieldColors()
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val result = ManualEntryValidator.validate(mealName, quantity, isGrams, description)
                    when(result) {
                        is ManualEntryValidator.ValidationResult.Success -> {
                            errorMessage = null
                            isLoading = true
                            // Simulate backend response then go back
                            onBackClick() // Replace with actual backend trigger later.
                        }
                        is ManualEntryValidator.ValidationResult.Error -> {
                            errorMessage = result.message
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CharcoalBlack,
                    contentColor = PureWhite
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Analyze Meal",
                        style = AppTypography.titleMedium.copy(color = PureWhite)
                    )
                }
            }
        }
    }
}

@Composable
fun defaultTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CharcoalBlack,
    unfocusedBorderColor = SlateGrey,
    focusedTextColor = CharcoalBlack,
    unfocusedTextColor = CharcoalBlack,
    errorBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.error,
    errorLabelColor = androidx.compose.material3.MaterialTheme.colorScheme.error,
    errorTextColor = androidx.compose.material3.MaterialTheme.colorScheme.error
)
