package com.example.calorieapp.domain.useCases

import com.example.calorieapp.domain.entities.DailyGoals
import com.example.calorieapp.domain.entities.UserProfile
import kotlin.math.roundToInt

object CalculationUtils {
    fun calculateGoals(user: UserProfile) : DailyGoals{

        val height = ((user.heightFeet * 12) + user.heightInches) *2.54
        val weight = user.weight.toFloat()
        val age = user.age

        var bmr = (weight * 10) + (height * 6.25) - (age * 5) + 5

        bmr += if(user.gender.equals("Male", ignoreCase = true)){
            5.0
        }else{
            -161.0
        }

        val activityMultiplier = when (user.activityLevel){
            "No Exercise" -> 1.2
            "Low Activity" -> 1.375
            "Moderate Activity" -> 1.55
            "High Activity" -> 1.725
            else -> 1.2
        }

        val tdee = bmr * activityMultiplier

        val targetCalories = when(user.goal){
            "Maintain" -> tdee
            "Lose Weight" -> tdee - 500
            "Gain Weight" -> tdee + 500
            else -> tdee
        }.roundToInt()

        val proteinGram = (weight * 2).roundToInt()
        val proteinCals = proteinGram * 4

        val fatGram = (weight * 0.9).roundToInt()
        val fatCals = fatGram * 9

        val carbs = targetCalories - (proteinCals + fatCals)

        return DailyGoals(
            calories = targetCalories,
            carbs = carbs,
            protein = proteinGram,
            fat = fatGram
        )
    }
}