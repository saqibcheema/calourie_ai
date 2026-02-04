package com.example.calorieapp.data.repository

import com.example.calorieapp.data.DataSource.local.UserDao
import com.example.calorieapp.data.Models.toUserEntity
import com.example.calorieapp.data.Models.toUserProfile
import com.example.calorieapp.domain.entities.UserProfile
import com.example.calorieapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImplementation(
    private val dao: UserDao
) : UserRepository {
    override suspend fun saveUser(user: UserProfile) {
       val entity = user.toUserEntity()
        dao.saveUser(entity)
    }

    override fun getUser(): Flow<UserProfile?> {
        return  dao.getUser().map {entity->
            entity?.toUserProfile()
        }
    }
}