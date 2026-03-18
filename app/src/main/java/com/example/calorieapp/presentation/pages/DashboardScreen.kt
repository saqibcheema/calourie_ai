package com.example.calorieapp.presentation.pages

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calorieapp.presentation.components.BarcodeScannerView
import com.example.calorieapp.presentation.pages.DashboardPages.*
import com.example.calorieapp.presentation.viewModel.DashboardViewModel
import com.example.calorieapp.presentation.viewModel.ScanViewModel
import com.example.calorieapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("NewApi")
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    scanViewModel: ScanViewModel = hiltViewModel(),
) {
    val goals by viewModel.dailyGoals.collectAsStateWithLifecycle()
    val summary by viewModel.dailySummary.collectAsStateWithLifecycle()
    val dailyMeals by viewModel.dailyMeals.collectAsStateWithLifecycle()
    val scanState by scanViewModel.state.collectAsState()

    var showNutritionSheet by remember { mutableStateOf(false) }
    var showScannerScreen by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )


    val targetCals = goals?.calories ?: 0
    val targetProtein = goals?.protein ?: 0
    val targetCarbs = goals?.carbs ?: 0
    val targetFats = goals?.fats ?: 0

    val consumedCals = summary?.totalCalories ?: 0
    val consumedProtein = summary?.totalProtein ?: 0
    val consumedCarbs = summary?.totalCarbs ?: 0
    val consumedFats = summary?.totalFats ?: 0

    val leftCals = maxOf(0, targetCals - consumedCals.toInt())
    val leftProtein = maxOf(0, targetProtein - consumedProtein.toInt())
    val leftCarbs = maxOf(0, targetCarbs - consumedCarbs.toInt())
    val leftFats = maxOf(0, targetFats - consumedFats.toInt())

    val calProgress = if (targetCals > 0) consumedCals.toFloat() / targetCals else 0f
    val proteinProgress = if (targetProtein > 0) consumedProtein.toFloat() / targetProtein else 0f
    val carbsProgress = if (targetCarbs > 0) consumedCarbs.toFloat() / targetCarbs else 0f
    val fatsProgress = if (targetFats > 0) consumedFats.toFloat() / targetFats else 0f

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFDF0F3),
            Color(0xFFF4F5FB),
            Color.White,
            Color.White
        )
    )

    if (scanState.scannedProduct != null) {
        MealLoggedScreen(
            product = scanState.scannedProduct!!,
            isAddedToMeal = scanState.isAddedToMeal,
            onAddToMeal = {
                scanViewModel.addToMeal()
            },
            onBackToDashboard = {
                showScannerScreen = false
                showNutritionSheet = false
                scanViewModel.clearProduct()
            },
            onLogAnotherMeal = {
                scanViewModel.clearProduct()
                showScannerScreen = true
                scanViewModel.startScanning()
            },
            onBackClick = {
                showScannerScreen = false
                scanViewModel.clearProduct()
            }
        )
    }else{
        Box(modifier = Modifier.fillMaxSize()) {

            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { showNutritionSheet = true },
                        containerColor = Color.Black,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Meal", modifier = Modifier.size(32.dp))
                    }
                },
                floatingActionButtonPosition = FabPosition.End,
                containerColor = MaterialTheme.colorScheme.background
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundBrush)
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                    TopHeader()
                    Spacer(modifier = Modifier.height(24.dp))
                    DateSelectorRow()
                    Spacer(modifier = Modifier.height(24.dp))
                    CaloriesCard(leftCals, calProgress)
                    Spacer(modifier = Modifier.height(16.dp))
                    MacrosRow(
                        leftProtein, proteinProgress,
                        leftCarbs, carbsProgress,
                        leftFats, fatsProgress
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Recently uploaded",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (dailyMeals.isEmpty()) {
                        RecentUploadPlaceholder()
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(dailyMeals) { meal ->
                                MealItemRow(product = meal)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            if (showNutritionSheet) {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ModalBottomSheet(
                    onDismissRequest = { showNutritionSheet = false },
                    sheetState = sheetState,
                    containerColor = PureWhite,
                    dragHandle = { BottomSheetDefaults.DragHandle() }
                ) {
                    NutritionSheetContent(
                        onScanClick = {
                            showScannerScreen = true
                            scanViewModel.startScanning()
                        }
                    )
                }
            }

            if (showScannerScreen) {
                showNutritionSheet = false
                if (hasCameraPermission) {
                    BarcodeScannerView(
                        onBarcodeDetected = { barcode ->
                            scanViewModel.onBarcodeDetected(barcode)
                        },
                        onBackClick = {
                            scanViewModel.stopScanning()
                            showScannerScreen = false
                        }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        launcher.launch(android.Manifest.permission.CAMERA)
                    }
                }
            }

            if (scanState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            if (scanState.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        action = {
                            TextButton(onClick = { scanViewModel.startScanning() }) {
                                Text("Dismiss", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    ) {
                        Text(text = scanState.error!!)
                    }
                }
            }
        }
    }
}
