package com.example.calorieapp.data.DataSource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.calorieapp.data.Models.GoalsEntity
import com.example.calorieapp.data.Models.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalsEntity)

    @Transaction
    suspend fun saveUserAndGoal(user: UserEntity, goal: GoalsEntity){
        insertUser(user)
        insertGoal(goal)
    }

    @Query("Select * from user_table where id = 0")
    fun getUser() : Flow<UserEntity?>

    @Query("Select * from goals_table where userId = 0")
    fun getGoals() : Flow<GoalsEntity?>
}