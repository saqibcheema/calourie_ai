package com.example.calorieapp.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorieapp.domain.entities.Product
import com.example.calorieapp.domain.useCases.AddMealUseCase
import com.example.calorieapp.domain.useCases.CheckDailyLimitUseCase
import com.example.calorieapp.domain.useCases.GetRemainingLimitsUseCase
import com.example.calorieapp.domain.useCases.IncrementDailyLimitUseCase
import com.example.calorieapp.domain.useCases.RemainingLimits
import com.example.calorieapp.domain.useCases.ScanProductUseCase
import com.example.calorieapp.util.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanProductUseCase: ScanProductUseCase,
    private val addMealUseCase: AddMealUseCase,
    private val connectivityObserver: ConnectivityObserver,
    private val checkDailyLimitUseCase: CheckDailyLimitUseCase,
    private val incrementDailyLimitUseCase: IncrementDailyLimitUseCase,
    private val getRemainingLimitsUseCase: GetRemainingLimitsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ScanState())
    val state: StateFlow<ScanState> = _state

    init {
        observeConnectivity()
        observeLimits()
    }

    private fun observeLimits() {
        getRemainingLimitsUseCase().onEach { limits ->
            _state.update { it.copy(remainingLimits = limits) }
        }.launchIn(viewModelScope)
    }

    private fun observeConnectivity() {
        connectivityObserver.observe()
            .onEach { status ->
                _state.update { 
                    it.copy(isOffline = status != ConnectivityObserver.Status.Available)
                }
            }
            .launchIn(viewModelScope)
    }


    fun startScanning() {
        _state.update { it.copy(isScanning = true, error = null) }
    }

    fun stopScanning() {
        _state.update { it.copy(isScanning = false) }
    }

    fun onBarcodeDetected(barcode: String) {
        if (_state.value.isLoading || _state.value.scannedProduct != null) return
        
        if (_state.value.isOffline) {
            _state.update { it.copy(error = "No internet connection. Please try again later.") }
            return
        }

        viewModelScope.launch {
            val canScan = checkDailyLimitUseCase(CheckDailyLimitUseCase.FeatureType.PRODUCT_SCAN)
            if (!canScan) {
                _state.update { it.copy(isLimitReached = true, isScanning = false, error = "Daily scan limit reached. Please upgrade to Premium.") }
                return@launch
            }

            _state.update {
                it.copy(isScanning = false, isLoading = true)
            }

            scanProductUseCase(barcode)
                .onSuccess { product ->
                    incrementDailyLimitUseCase(CheckDailyLimitUseCase.FeatureType.PRODUCT_SCAN)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            scannedProduct = product,
                            isAddedToMeal = false,
                            error = null
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Unknown error"
                        )
                    }
                }
        }
    }

    fun addToMeal() {
        val product = _state.value.scannedProduct ?: return
        
        if (_state.value.isOffline) {
            _state.update { it.copy(error = "No internet connection. Failed to add meal.") }
            return
        }

        viewModelScope.launch {
            try {
                addMealUseCase(product)
                _state.update { it.copy(isAddedToMeal = true) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to add meal") }
            }
        }
    }

    fun clearProduct() {
        _state.update { it.copy(scannedProduct = null, isAddedToMeal = false) }
    }

    fun onDismissError() {
        _state.update { it.copy(error = null) }
    }
    
    fun dismissLimitDialog() {
        _state.update { it.copy(isLimitReached = false) }
    }
}

data class ScanState(
    val isScanning: Boolean = false,
    val isLoading: Boolean = false,
    val scannedProduct: Product? = null,
    val isAddedToMeal: Boolean = false,
    val error: String? = null,
    val isOffline: Boolean = false,
    val isLimitReached: Boolean = false,
    val remainingLimits: RemainingLimits? = null
)