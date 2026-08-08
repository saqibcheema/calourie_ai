package com.example.calorieapp.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorieapp.Core.Dest
import com.example.calorieapp.domain.useCases.CheckUserSessionUseCase
import com.example.calorieapp.domain.useCases.InitializeUserLimitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.calorieapp.domain.useCases.GetRemainingLimitsUseCase
import com.example.calorieapp.domain.useCases.RemainingLimits
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class MainViewModel @Inject constructor(
    private val checkUserSessionUseCase: CheckUserSessionUseCase,
    private val initializeUserLimitsUseCase: InitializeUserLimitsUseCase,
    private val getRemainingLimitsUseCase: GetRemainingLimitsUseCase
) : ViewModel() {

    private val _startDestination = MutableStateFlow<Any?>(null)
    val startDestination: StateFlow<Any?> = _startDestination

    private val _remainingLimits = MutableStateFlow<RemainingLimits?>(null)
    val remainingLimits = _remainingLimits.asStateFlow()

    init {
        viewModelScope.launch {
            // Initialize free tier / daily limits
            initializeUserLimitsUseCase()

            checkUserSessionUseCase().collect { exists ->
                if (exists) {
                    _startDestination.value = Dest.MainScreen
                } else {
                    _startDestination.value = Dest.OnBoarding
                }
            }
        }
        
        observeLimits()
    }

    private fun observeLimits() {
        getRemainingLimitsUseCase().onEach { limits ->
            _remainingLimits.value = limits
        }.launchIn(viewModelScope)
    }
}