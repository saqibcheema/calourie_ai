package com.example.calorieapp.domain.useCases

import com.example.calorieapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckUserSessionUseCase @Inject constructor(
    private val repository : UserRepository
) {
    operator fun invoke() : Flow<Boolean>{
        return repository.checkUserSession()
    }
}