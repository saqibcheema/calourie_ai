package com.example.calorieapp.data.Models

import com.example.calorieapp.domain.entities.UserProfile

fun UserProfile.toUserEntity(): UserEntity {
    return UserEntity(
        gender = this.gender,
        age = this.age,
        weight = this.weight.toInt(),
        heightFeet = this.heightFeet,
        heightInches = this.heightInches,
        activityLevel = this.activityLevel,
        goal = this.goal
    )
}

fun UserEntity.toUserProfile(): UserProfile{
    return UserProfile(
        gender = this.gender,
        age = this.age,
        weight = this.weight.toString(),
        heightFeet = this.heightFeet,
        heightInches = this.heightInches,
        activityLevel = this.activityLevel,
        goal = this.goal
    )
}