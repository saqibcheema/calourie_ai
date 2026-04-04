package com.example.calorieapp.data.DataSource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.calorieapp.data.Models.GoalsEntity
import com.example.calorieapp.data.Models.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    /**
     * Upsert the user: if the user row already exists it will be UPDATED in-place,
     * NOT deleted-then-inserted. This prevents the CASCADE foreign key from wiping
     * all historical goal rows every time the profile is edited.
     */
    @Upsert
    suspend fun upsertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalsEntity)

    @Transaction
    suspend fun saveUserAndGoal(user: UserEntity, goal: GoalsEntity) {
        upsertUser(user)   // Safe update – no cascade delete triggered
        insertGoal(goal)   // Appends a NEW goal row preserving all history
    }

    @Query("Select * from user_table where id = 0")
    fun getUser(): Flow<UserEntity?>

    @Query("SELECT * FROM goals_table WHERE userId = 0 ORDER BY createdAt DESC LIMIT 1")
    fun getLatestGoals(): Flow<GoalsEntity?>

    @Query("SELECT * FROM goals_table WHERE userId = 0 AND createdAt <= :timestamp ORDER BY createdAt DESC LIMIT 1")
    fun getGoalForDate(timestamp: Long): Flow<GoalsEntity?>
}