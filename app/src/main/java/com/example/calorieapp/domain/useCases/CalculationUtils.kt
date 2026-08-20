package com.example.calorieapp.domain.useCases

import com.example.calorieapp.domain.entities.DailyGoals
import com.example.calorieapp.domain.entities.UserProfile
import kotlin.math.max
import kotlin.math.roundToInt

object CalculationUtils {

    // ─── Age Category Boundaries ─────────────────────────────────────────────
    private const val CHILD_MAX_AGE   = 12
    private const val TEEN_MAX_AGE    = 17
    private const val ELDERLY_MIN_AGE = 65

    // ─── BMI Thresholds ───────────────────────────────────────────────────────
    private const val BMI_OVERWEIGHT  = 25.0
    private const val BMI_OBESE       = 30.0
    private const val BMI_OBESE_II    = 35.0

    // ─── Minimum Safe Calorie Floors ──────────────────────────────────────────
    private const val MIN_CALORIES_CHILD    = 1000
    private const val MIN_CALORIES_TEEN_F   = 1200
    private const val MIN_CALORIES_TEEN_M   = 1400
    private const val MIN_CALORIES_ADULT_F  = 1200
    private const val MIN_CALORIES_ADULT_M  = 1500
    private const val MIN_CALORIES_ELDERLY  = 1100

    // ─── Medical Condition Identifiers ────────────────────────────────────────
    const val CONDITION_DIABETES   = "Diabetes"
    const val CONDITION_THYROID    = "Thyroid"
    const val CONDITION_PCOS       = "PCOS"
    const val CONDITION_KIDNEY     = "Kidney Disease"
    const val CONDITION_HEART      = "Heart Condition"

    // ─── Pregnancy Status ────────────────────────────────────────────────────
    const val PREGNANCY_NONE           = "None"
    const val PREGNANCY_FIRST          = "1st Trimester"
    const val PREGNANCY_SECOND         = "2nd Trimester"
    const val PREGNANCY_THIRD          = "3rd Trimester"
    const val PREGNANCY_BREASTFEEDING  = "Breastfeeding"

    // ─────────────────────────────────────────────────────────────────────────
    fun calculateGoals(user: UserProfile): DailyGoals {

        // ── Safe weight parsing — crash hone se bachao ────────────────────────
        val actualWeight = user.weight.toFloatOrNull()
            ?: throw IllegalArgumentException("Invalid weight value: '${user.weight}'")

        val heightCm = ((user.heightFeet * 12) + user.heightInches) * 2.54
        val age      = user.age
        val isMale   = user.gender.trim().equals("Male", ignoreCase = true)
        val hasCondition: (String) -> Boolean = { user.medicalConditions.contains(it) }

        // ── Issue #1 Fix: BMI-based Adjusted Body Weight for obese users ──────
        val bmi = calculateBMI(actualWeight, heightCm)
        val calcWeight = when {
            bmi >= BMI_OBESE -> calculateAdjustedBodyWeight(actualWeight, heightCm, isMale)
            else             -> actualWeight
        }

        // ── Age-based BMR formula ─────────────────────────────────────────────
        val rawBmr = when {
            age <= CHILD_MAX_AGE  -> calculateChildBMR(calcWeight, age, isMale)
            age <= TEEN_MAX_AGE   -> calculateTeenBMR(calcWeight, isMale)
            age < ELDERLY_MIN_AGE -> calculateAdultBMR(calcWeight, heightCm, age, isMale)
            else                  -> calculateElderlyBMR(calcWeight, heightCm, age, isMale)
        }

        // ── Issue #4a Fix: Thyroid condition reduces metabolism 15–25% ────────
        val bmr = when {
            hasCondition(CONDITION_THYROID) -> rawBmr * 0.82  // Hypothyroidism: -18%
            else                            -> rawBmr
        }

        // ── Activity multiplier ───────────────────────────────────────────────
        val activityMultiplier = when (user.activityLevel.trim()) {
            "No Exercise"        -> 1.2
            "Low Activity"       -> 1.375
            "Moderate Activity"  -> 1.55
            "High Activity"      -> 1.725
            "Very High Activity" -> 1.9
            else                 -> 1.2
        }

        val tdee = bmr * activityMultiplier

        // ── Issue #3 Fix: Goal Pace based calorie adjustment ─────────────────
        // Base adjustment per age group, then scaled by pace
        val basePaceAdjustment = when {
            age <= CHILD_MAX_AGE  -> 0     // Children: no calorie deficit ever
            age <= TEEN_MAX_AGE   -> 250   // Teens: max 250 per day
            age < ELDERLY_MIN_AGE -> 500   // Adults: standard 500
            else                  -> 300   // Elderly: gentle 300
        }

        val paceMultiplier = when (user.goalPace.trim()) {
            "Slow"     -> 0.5   // -250 for adults (0.25 kg/week)
            "Moderate" -> 1.0   // -500 for adults (0.5 kg/week)
            "Fast"     -> 1.5   // -750 for adults (0.75 kg/week) — capped by floor anyway
            else       -> 1.0
        }

        // ── Issue #4b Fix: Medical conditions cap the deficit ─────────────────
        val effectivePaceMultiplier = when {
            hasCondition(CONDITION_PCOS)   -> minOf(paceMultiplier, 0.6)  // PCOS: gentle deficit only
            hasCondition(CONDITION_HEART)  -> minOf(paceMultiplier, 0.5)  // Heart: very gentle
            else                           -> paceMultiplier
        }

        // ── Issue #9 Fix: Pregnancy / Breastfeeding always overrides goal to Maintain ──
        // Even if UI didn't catch it — defense in depth
        val effectiveGoal = when {
            user.pregnancyStatus != PREGNANCY_NONE -> "Maintain"
            else                                   -> user.goal.trim()
        }

        val calorieAdjustment = (basePaceAdjustment * effectivePaceMultiplier).roundToInt()

        // ── Minimum calorie floor ─────────────────────────────────────────────
        val minCalories = getMinCalories(age, isMale)

        // ── Target calories calculation ───────────────────────────────────────
        var targetCalories = when (effectiveGoal) {
            "Maintain"    -> tdee
            "Lose Weight" -> if (age <= CHILD_MAX_AGE) tdee else tdee - calorieAdjustment
            "Gain Weight" -> tdee + calorieAdjustment
            else          -> tdee
        }.roundToInt()
            .let { max(it, minCalories) }

        // ── Issue #5 Fix: Pregnancy / Breastfeeding calorie bonus ─────────────
        targetCalories += when (user.pregnancyStatus.trim()) {
            PREGNANCY_FIRST         -> 0    // 1st trimester: no extra calories needed
            PREGNANCY_SECOND        -> 340  // 2nd trimester: +340 kcal/day
            PREGNANCY_THIRD         -> 450  // 3rd trimester: +450 kcal/day
            PREGNANCY_BREASTFEEDING -> 500  // Breastfeeding: +500 kcal/day
            else                    -> 0
        }

        // ── Issue #2 Fix: Age/activity/goal-sensitive protein ────────────────
        val baseProteinPerKg = when {
            age <= CHILD_MAX_AGE  -> 0.95  // WHO/RDA children
            age <= TEEN_MAX_AGE   -> when {
                // Issue #2: Active growing teen boys need more
                isMale && user.activityLevel == "High Activity"
                        && user.goal == "Gain Weight" -> 1.8
                isMale                               -> 1.5
                else                                 -> 1.3
            }
            age < ELDERLY_MIN_AGE -> 1.6   // Active adults
            else                  -> 1.1   // Elderly — kidney-safe
        }

        // ── Issue #4c Fix: Medical conditions override protein ────────────────
        val proteinPerKg = when {
            // Kidney disease: must restrict protein severely
            hasCondition(CONDITION_KIDNEY)  -> 0.75  // < 0.8g/kg is standard for CKD
            // Diabetes: slightly higher protein helps blood sugar control
            hasCondition(CONDITION_DIABETES) -> minOf(baseProteinPerKg * 1.1, 1.8)
            else -> baseProteinPerKg
        }

        // Protein uses ABW for obese — actual weight would give too much
        val proteinWeight = if (bmi >= BMI_OBESE) calcWeight else actualWeight
        val proteinGram  = (proteinWeight * proteinPerKg).roundToInt()
        val proteinCals  = proteinGram * 4

        // ── Fat — age-based percentages ───────────────────────────────────────
        val baseFatPercent = when {
            age <= CHILD_MAX_AGE  -> 0.35  // Children: brain development
            age <= TEEN_MAX_AGE   -> 0.30
            age < ELDERLY_MIN_AGE -> 0.27
            else                  -> 0.25
        }

        // Diabetes → lower fat, higher complex carbs approach
        val fatPercent = when {
            hasCondition(CONDITION_DIABETES) -> baseFatPercent * 0.85
            hasCondition(CONDITION_HEART)    -> baseFatPercent * 0.80  // Heart: lower sat fat
            else                             -> baseFatPercent
        }

        val fatCals = (targetCalories * fatPercent).roundToInt()
        val fatGram  = (fatCals / 9.0).roundToInt()

        // ── Issue #4d Fix: Diabetes → hard carb cap ───────────────────────────
        val carbsCals = max(0, targetCalories - proteinCals - fatCals)
        val rawCarbsGram = (carbsCals / 4.0).roundToInt()

        // For diabetes/PCOS: cap carbs at 130g/day (ADA recommendation)
        val carbsGram = when {
            hasCondition(CONDITION_DIABETES) || hasCondition(CONDITION_PCOS) ->
                minOf(rawCarbsGram, 130)
            else -> rawCarbsGram
        }

        return DailyGoals(
            calories = targetCalories,
            carbs    = carbsGram,
            protein  = proteinGram,
            fats     = fatGram
        )
    }

    // ─── BMI Calculation ───────────────────────────────────────────────────────
    fun calculateBMI(weightKg: Float, heightCm: Double): Double {
        val heightM = heightCm / 100.0
        return weightKg / (heightM * heightM)
    }

    // ─── Adjusted Body Weight for Obesity (Devine + ABW formula) ─────────────
    /**
     * For BMI > 30, actual weight overestimates metabolic needs.
     * IBW = Ideal Body Weight (Devine formula)
     * ABW = IBW + 0.4 × (Actual - IBW)
     * Used by clinical dietitians worldwide (ASPEN guidelines).
     */
    private fun calculateAdjustedBodyWeight(
        actualWeight: Float, heightCm: Double, isMale: Boolean
    ): Float {
        val heightInches = heightCm / 2.54
        val ibw = if (isMale) {
            50.0 + 2.3 * (heightInches - 60)
        } else {
            45.5 + 2.3 * (heightInches - 60)
        }.coerceAtLeast(30.0)  // Minimum IBW floor for very short people

        val abw = ibw + 0.4 * (actualWeight - ibw)
        return abw.toFloat().coerceAtLeast(ibw.toFloat())
    }

    // ─── BMR Calculators by Age Group ─────────────────────────────────────────

    /**
     * Schofield Equation for children (age ≤ 12).
     * WHO recommended pediatric BMR formula.
     */
    private fun calculateChildBMR(weight: Float, age: Int, isMale: Boolean): Double {
        return if (isMale) {
            when {
                age <= 3  -> (59.512 * weight) - 30.4
                age <= 10 -> (22.706 * weight) + 504.3
                else      -> (17.686 * weight) + 658.2
            }
        } else {
            when {
                age <= 3  -> (58.317 * weight) - 31.1
                age <= 10 -> (20.315 * weight) + 485.9
                else      -> (13.384 * weight) + 692.6
            }
        }
    }

    /**
     * Schofield for teens (age 13–17).
     */
    private fun calculateTeenBMR(weight: Float, isMale: Boolean): Double {
        return if (isMale) (17.686 * weight) + 658.2
        else               (13.384 * weight) + 692.6
    }

    /**
     * Mifflin-St Jeor for adults 18–64.
     * Male:   10W + 6.25H - 5A + 5
     * Female: 10W + 6.25H - 5A - 161
     */
    private fun calculateAdultBMR(
        weight: Float, heightCm: Double, age: Int, isMale: Boolean
    ): Double {
        val base = (10 * weight) + (6.25 * heightCm) - (5 * age)
        return if (isMale) base + 5.0 else base - 161.0
    }

    /**
     * Elderly BMR (65+): Mifflin-St Jeor × 0.95.
     * Accounts for reduced muscle mass (sarcopenia) in older adults.
     */
    private fun calculateElderlyBMR(
        weight: Float, heightCm: Double, age: Int, isMale: Boolean
    ): Double {
        val base = (10 * weight) + (6.25 * heightCm) - (5 * age)
        return if (isMale) (base + 5.0) * 0.95 else (base - 161.0) * 0.95
    }

    /**
     * Minimum safe calorie intake floor by age and gender.
     */
    private fun getMinCalories(age: Int, isMale: Boolean): Int {
        return when {
            age <= CHILD_MAX_AGE  -> MIN_CALORIES_CHILD
            age <= TEEN_MAX_AGE   -> if (isMale) MIN_CALORIES_TEEN_M else MIN_CALORIES_TEEN_F
            age < ELDERLY_MIN_AGE -> if (isMale) MIN_CALORIES_ADULT_M else MIN_CALORIES_ADULT_F
            else                  -> MIN_CALORIES_ELDERLY
        }
    }
}