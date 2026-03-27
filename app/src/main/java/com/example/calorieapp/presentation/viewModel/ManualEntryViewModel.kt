package com.example.calorieapp.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorieapp.domain.entities.Product
import com.example.calorieapp.domain.useCases.AddMealUseCase
import com.example.calorieapp.domain.validation.ManualEntryValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class ManualEntryState(
    val whereEat: String = "Restaurant", // Restaurant, Home Cooked
    val foodName: String = "",
    val restaurantType: String = "Desi",
    val visibleExtras: Set<String> = emptySet(),
    val cookingMethod: String = "Raw",
    val addOil: Boolean = false,
    val oilAmount: String = "None",
    val extrasAdded: Set<String> = emptySet(),
    
    val portionType: String = "Plate size", // Grams, Plate size, Pieces, Slices, Volume (ml)
    val isPortionTypeExpanded: Boolean = true,
    
    val gramsValue: Int = 225,
    val plateSize: String = "Medium (~400g)",
    val piecesCount: Int = 1,
    val slicesSize: String = "Medium",
    val slicesCount: Int = 1,
    val volumeSize: String = "Regular (250ml)",
    val manualMl: String = "",
    
    val isDescriptionExpanded: Boolean = false,
    val extraDescription: String = "",
    
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class ManualEntryViewModel @Inject constructor(
    private val addMealUseCase: AddMealUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ManualEntryState())
    val state = _state.asStateFlow()

    fun onWhereEatChange(where: String) {
        _state.update { it.copy(whereEat = where) }
    }

    fun onFoodNameChange(name: String) {
        _state.update { it.copy(foodName = name) }
    }

    fun onRestaurantTypeChange(type: String) {
        _state.update { it.copy(restaurantType = type) }
    }

    fun toggleVisibleExtra(extra: String) {
        _state.update {
            val newExtras = if (it.visibleExtras.contains(extra)) {
                it.visibleExtras - extra
            } else {
                it.visibleExtras + extra
            }
            it.copy(visibleExtras = newExtras)
        }
    }

    fun onCookingMethodChange(method: String) {
        _state.update { it.copy(cookingMethod = method) }
    }

    fun onAddOilChange(add: Boolean) {
        _state.update { it.copy(addOil = add) }
    }

    fun onOilAmountChange(amount: String) {
        _state.update { it.copy(oilAmount = amount) }
    }

    fun toggleExtraAdded(extra: String) {
        _state.update {
            val newExtras = if (it.extrasAdded.contains(extra)) {
                it.extrasAdded - extra
            } else {
                it.extrasAdded + extra
            }
            it.copy(extrasAdded = newExtras)
        }
    }

    fun onPortionTypeChange(type: String) {
        _state.update { it.copy(portionType = type) }
    }

    fun togglePortionTypeExpansion() {
        _state.update { it.copy(isPortionTypeExpanded = !it.isPortionTypeExpanded) }
    }

    fun onGramsChange(grams: Int) {
        _state.update { it.copy(gramsValue = grams) }
    }

    fun onPlateSizeChange(size: String) {
        _state.update { it.copy(plateSize = size) }
    }

    fun onPiecesCountChange(diff: Int) {
        _state.update { it.copy(piecesCount = (it.piecesCount + diff).coerceAtLeast(1)) }
    }

    fun onSlicesSizeChange(size: String) {
        _state.update { it.copy(slicesSize = size) }
    }

    fun onSlicesCountChange(diff: Int) {
        _state.update { it.copy(slicesCount = (it.slicesCount + diff).coerceAtLeast(1)) }
    }

    fun onVolumeSizeChange(size: String) {
        _state.update { it.copy(volumeSize = size) }
    }

    fun onManualMlChange(ml: String) {
        _state.update { it.copy(manualMl = ml) }
    }

    fun toggleDescriptionExpansion() {
        _state.update { it.copy(isDescriptionExpanded = !it.isDescriptionExpanded) }
    }

    fun onDescriptionChange(desc: String) {
        _state.update { it.copy(extraDescription = desc) }
    }

    fun logFood() {
        val currentState = _state.value
        val quantityStr = when(currentState.portionType) {
            "Grams" -> currentState.gramsValue.toString()
            "Plate size" -> currentState.plateSize
            "Pieces" -> "${currentState.piecesCount} pcs"
            "Slices" -> "${currentState.slicesCount} slices (${currentState.slicesSize})"
            "Volume (ml)" -> currentState.volumeSize
            else -> ""
        }

        val validationResult = ManualEntryValidator.validate(
            mealName = currentState.foodName,
            quantity = quantityStr,
            isGrams = currentState.portionType == "Grams",
            description = currentState.extraDescription
        )

        when(validationResult) {
            is ManualEntryValidator.ValidationResult.Error -> {
                _state.update { it.copy(errorMessage = validationResult.message) }
            }
            is ManualEntryValidator.ValidationResult.Success -> {
                saveMeal(validationResult)
            }
        }
    }

    private fun saveMeal(validation: ManualEntryValidator.ValidationResult.Success) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val extras = if (_state.value.whereEat == "Restaurant") {
                    _state.value.visibleExtras.joinToString(", ")
                } else {
                    _state.value.extrasAdded.joinToString(", ")
                }

                val details = buildString {
                    append("Source: ${_state.value.whereEat}")
                    if (_state.value.whereEat == "Restaurant") {
                        append(", Type: ${_state.value.restaurantType}")
                    } else {
                        append(", Method: ${_state.value.cookingMethod}")
                        if (_state.value.addOil) append(", Oil: ${_state.value.oilAmount}")
                    }
                    if (extras.isNotEmpty()) append(", Extras: $extras")
                    if (validation.description.isNotEmpty()) append(", Note: ${validation.description}")
                }

                val manualProduct = Product(
                    barcode = "manual_${System.currentTimeMillis()}",
                    productName = validation.mealName,
                    brand = details,
                    imageUrl = null,
                    calories = 0.0, // AI will calculate after saving as per requirements
                    protein = 0.0,
                    carbs = 0.0,
                    fat = 0.0,
                    fiber = 0.0,
                    sugars = 0.0,
                    scannedAt = Date(),
                    quantity = 1 // Manual entries represent a whole meal/portion
                )

                addMealUseCase(manualProduct)
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Failed to log food: ${e.message}") }
            }
        }
    }
}
