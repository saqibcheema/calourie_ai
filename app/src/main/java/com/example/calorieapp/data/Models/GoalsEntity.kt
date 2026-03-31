package com.example.calorieapp.data.Models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "goals_table",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
    )
data class GoalsEntity (
    @PrimaryKey(autoGenerate = true)
    val goalId: Int = 0,
    val userId: Int,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fats: Int,
    val createdAt: Long = System.currentTimeMillis()
)