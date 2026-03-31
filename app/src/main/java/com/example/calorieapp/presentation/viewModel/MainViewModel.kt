package com.example.calorieapp.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorieapp.Core.Dest
import com.example.calorieapp.domain.useCases.CheckUserSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val checkUserSessionUseCase: CheckUserSessionUseCase
) : ViewModel() {

    private val _startDestination = MutableStateFlow<Any?>(null)
    val startDestination: StateFlow<Any?> = _startDestination

    init {
        viewModelScope.launch {
            checkUserSessionUseCase().collect { exists ->
                if (exists) {
                    _startDestination.value = Dest.MainScreen
                } else {
                    _startDestination.value = Dest.OnBoarding
                }
            }
        }
    }
}