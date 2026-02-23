package com.example.calorieapp.data.repository

import com.example.calorieapp.data.DataSource.local.UserDao
import com.example.calorieapp.data.Models.toDailyGoals
import com.example.calorieapp.data.Models.toGoalsEntity
import com.example.calorieapp.data.Models.toUserEntity
import com.example.calorieapp.data.Models.toUserProfile
import com.example.calorieapp.domain.entities.DailyGoals
import com.example.calorieapp.domain.entities.UserProfile
import com.example.calorieapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImplementation @Inject constructor(
    private val dao: UserDao
) : UserRepository {
    override suspend fun saveUser(user: UserProfile, goals: DailyGoals) {
       val entity = user.toUserEntity()
        val goal = goals.toGoalsEntity(entity.id)
        dao.saveUserAndGoal(entity,goal)
    }

    override fun getUser(): Flow<UserProfile?> {
        return  dao.getUser().map {entity->
            entity?.toUserProfile()
        }
    }

    override fun getGoals() : Flow<DailyGoals?>{
        return dao.getGoals().map{entity->
            entity?.toDailyGoals()
        }
    }
}