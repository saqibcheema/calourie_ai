package com.example.calorieapp.data.DataSource.local

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.calorieapp.data.Models.UserEntity
import com.example.calorieapp.domain.entities.UserProfile
import kotlinx.coroutines.flow.Flow

interface userDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: UserEntity)

    @Query("Select * from user_table where id = 0")
    fun getUsers() : Flow<UserProfile?>
}