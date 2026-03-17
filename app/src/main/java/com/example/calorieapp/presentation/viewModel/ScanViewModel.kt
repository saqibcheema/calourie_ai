package com.example.calorieapp.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorieapp.domain.entities.Product
import com.example.calorieapp.domain.useCases.ScanProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanProductUseCase: ScanProductUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ScanState())
    val state: StateFlow<ScanState> = _state


    fun startScanning() {
        _state.update { it.copy(isScanning = true, error = null) }
    }

    fun stopScanning() {
        _state.update { it.copy(isScanning = false) }
    }

    fun onBarcodeDetected(barcode: String) {
        if (_state.value.isLoading || _state.value.scannedProduct != null) return
        
        viewModelScope.launch {
            _state.update {
                it.copy(isScanning = false, isLoading = true)
            }

            scanProductUseCase(barcode)
                .onSuccess { product ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            scannedProduct = product,
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

    fun clearProduct() {
        _state.update { it.copy(scannedProduct = null) }
    }
}

data class ScanState(
    val isScanning: Boolean = false,
    val isLoading: Boolean = false,
    val scannedProduct: Product? = null,
    val error: String? = null
)