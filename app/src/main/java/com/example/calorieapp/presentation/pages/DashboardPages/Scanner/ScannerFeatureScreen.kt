package com.example.calorieapp.presentation.pages.DashboardPages.Scanner

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.calorieapp.presentation.components.BarcodeScannerView
import com.example.calorieapp.presentation.components.CameraPermissionHandler
import com.example.calorieapp.presentation.components.PremiumConnectivityStatus
import com.example.calorieapp.presentation.components.PremiumRateLimitStatus
import com.example.calorieapp.presentation.pages.DashboardPages.MealLoggedScreen
import com.example.calorieapp.presentation.viewModel.ScanViewModel

@Composable
fun ScannerFeatureScreen(
    onClose: () -> Unit,
    scanViewModel: ScanViewModel = hiltViewModel()
) {
    val scanState by scanViewModel.state.collectAsState()
    val context = LocalContext.current
    
    var hasCameraPermission by remember { mutableStateOf(false) }

    CameraPermissionHandler(
        onPermissionGranted = { hasCameraPermission = true },
        onClosed = { onClose() }
    )

    LaunchedEffect(Unit) {
        scanViewModel.startScanning()
    }

    LaunchedEffect(scanState.error) {
        if (scanState.error != null) {
            kotlinx.coroutines.delay(4000)
            scanViewModel.onDismissError()
        }
    }

    if (scanState.scannedProduct != null) {
        MealLoggedScreen(
            product = scanState.scannedProduct!!,
            isAddedToMeal = scanState.isAddedToMeal,
            onAddToMeal = { scanViewModel.addToMeal() },
            onBackToDashboard = {
                scanViewModel.clearProduct()
                onClose()
            },
            onLogAnotherMeal = {
                scanViewModel.clearProduct()
                scanViewModel.startScanning()
            },
            onBackClick = {
                scanViewModel.clearProduct()
            }
        )
    } else {
        if (hasCameraPermission) {
            Box(modifier = Modifier.fillMaxSize()) {
                BarcodeScannerView(
                    onBarcodeDetected = { barcode ->
                        scanViewModel.onBarcodeDetected(barcode)
                    },
                    onBackClick = {
                        scanViewModel.stopScanning()
                        onClose()
                    }
                )
                PremiumConnectivityStatus(isOffline = scanState.isOffline)
                PremiumRateLimitStatus(message = scanState.error)
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
}
