package com.example.calorieapp.data.DataSource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.calorieapp.data.Models.SubscriptionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatusDao {
    @Query("SELECT * FROM subscription_status WHERE id = 1")
    suspend fun getUserStatus(): SubscriptionStatus?

    @Query("SELECT * FROM subscription_status WHERE id = 1")
    fun getUserStatusFlow(): Flow<SubscriptionStatus?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUserStatus(subscriptionStatus: SubscriptionStatus)
}