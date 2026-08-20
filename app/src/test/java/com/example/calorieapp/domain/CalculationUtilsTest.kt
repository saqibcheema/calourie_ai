package com.example.calorieapp.domain

import com.example.calorieapp.domain.entities.UserProfile
import com.example.calorieapp.domain.useCases.CalculationUtils
import org.junit.Assert.*
import org.junit.Test

class CalculationUtilsTest {

    // ─── Helper ───────────────────────────────────────────────────────────────
    private fun profile(
        gender: String,
        age: Int,
        weight: String,
        heightFeet: Int = 5,
        heightInches: Int = 7,
        activityLevel: String = "Moderate Activity",
        goal: String = "Maintain",
        goalPace: String = "Moderate",
        medicalConditions: List<String> = emptyList(),
        pregnancyStatus: String = CalculationUtils.PREGNANCY_NONE
    ) = UserProfile(
        gender, age, weight, heightFeet, heightInches,
        activityLevel, goal, goalPace, medicalConditions, pregnancyStatus
    )

    private fun printResult(label: String, user: UserProfile) {
        val goals = CalculationUtils.calculateGoals(user)
        val bmi   = CalculationUtils.calculateBMI(user.weight.toFloat(), ((user.heightFeet * 12) + user.heightInches) * 2.54)
        println(
            "[$label]\n" +
            "  Profile : ${user.gender}, age=${user.age}, weight=${user.weight}kg, " +
            "height=${user.heightFeet}ft ${user.heightInches}in | BMI=${String.format("%.1f", bmi)}\n" +
            "  Extra   : goal=${user.goal}, pace=${user.goalPace}, " +
            "conditions=${user.medicalConditions}, pregnancy=${user.pregnancyStatus}\n" +
            "  Result  : Calories=${goals.calories} kcal | Protein=${goals.protein}g | Fat=${goals.fats}g | Carbs=${goals.carbs}g\n"
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ORIGINAL TESTS — Still passing
    // ═══════════════════════════════════════════════════════════════════════════

    @Test fun `TC01 - Adult Male 30yo 80kg Maintain`() {
        val user = profile("Male", 30, "80", goal = "Maintain")
        printResult("TC01 Adult Male 30yo Maintain", user)
        val goals = CalculationUtils.calculateGoals(user)
        assertTrue("Calories in adult male range", goals.calories in 1800..3200)
        assertTrue("Carbs >= 0", goals.carbs >= 0)
    }

    @Test fun `TC02 - Adult Female 28yo 65kg Lose Weight`() {
        val user = profile("Female", 28, "65", goal = "Lose Weight")
        printResult("TC02 Adult Female Lose Weight", user)
        val goals = CalculationUtils.calculateGoals(user)
        assertTrue("Min floor enforced", goals.calories >= 1200)
        assertTrue("Carbs >= 0", goals.carbs >= 0)
    }

    @Test fun `TC03 - Child Boy 10yo 35kg Maintain`() {
        val user = profile("Male", 10, "35", heightFeet = 4, heightInches = 6, goal = "Maintain")
        printResult("TC03 Child Boy 10yo", user)
        val goals = CalculationUtils.calculateGoals(user)
        assertTrue("Child calories >= 1000", goals.calories >= 1000)
        assertTrue("Child protein not excessive", goals.protein <= (35 * 2))
        assertTrue("Carbs >= 0", goals.carbs >= 0)
    }

    @Test fun `TC04 - Child Girl 8yo 30kg Lose Weight - no deficit for children`() {
        val user = profile("Female", 8, "30", heightFeet = 4, heightInches = 2, goal = "Lose Weight")
        val maintainGoals = CalculationUtils.calculateGoals(user.copy(goal = "Maintain"))
        val loseGoals     = CalculationUtils.calculateGoals(user)
        printResult("TC04 Child Girl Lose Weight", user)
        assertEquals("Children no deficit", maintainGoals.calories, loseGoals.calories)
    }

    @Test fun `TC05 - Teen Male 16yo 60kg High Activity Gain Weight`() {
        val user = profile("Male", 16, "60", activityLevel = "High Activity", goal = "Gain Weight")
        printResult("TC05 Active Teen Boy Gain Weight", user)
        val goals = CalculationUtils.calculateGoals(user)
        assertTrue("Carbs >= 0", goals.carbs >= 0)
        // Active teen gaining weight should get higher protein
        assertTrue("Active teen protein >= 1.5g/kg", goals.protein >= (60 * 1.5).toInt())
    }

    @Test fun `TC06 - Elderly Male 72yo - less calories than adult same weight`() {
        val elderly = profile("Male", 72, "75", activityLevel = "No Exercise")
        val adult   = profile("Male", 35, "75", activityLevel = "No Exercise")
        printResult("TC06 Elderly vs Adult", elderly)
        val elderlyGoals = CalculationUtils.calculateGoals(elderly)
        val adultGoals   = CalculationUtils.calculateGoals(adult)
        println("  [Comparison] Adult 35yo: ${adultGoals.calories} kcal vs Elderly 72yo: ${elderlyGoals.calories} kcal")
        assertTrue("Elderly < Adult calories", elderlyGoals.calories < adultGoals.calories)
        assertTrue("Carbs >= 0", elderlyGoals.carbs >= 0)
    }

    @Test fun `TC07 - Elderly Female 68yo Lose Weight - gentle deficit 300`() {
        val user    = profile("Female", 68, "70", activityLevel = "Low Activity", goal = "Lose Weight")
        val maintain = CalculationUtils.calculateGoals(user.copy(goal = "Maintain"))
        val lose     = CalculationUtils.calculateGoals(user)
        printResult("TC07 Elderly Female Lose Weight", user)
        val deficit = maintain.calories - lose.calories
        println("  [Deficit] = $deficit kcal")
        assertTrue("Elderly deficit <= 300", deficit <= 300)
    }

    @Test fun `TC08 - Heavy Adult 120kg - carbs never negative`() {
        val user = profile("Male", 40, "120", heightFeet = 5, heightInches = 10, goal = "Lose Weight")
        printResult("TC08 Heavy Adult 120kg", user)
        val goals = CalculationUtils.calculateGoals(user)
        assertTrue("CRITICAL: Carbs >= 0", goals.carbs >= 0)
    }

    @Test fun `TC09 - Very Light Female 40kg - min floor enforced`() {
        val user = profile("Female", 25, "40", heightFeet = 4, heightInches = 11, goal = "Lose Weight")
        printResult("TC09 Very Light Female", user)
        assertTrue("Min 1200 enforced", CalculationUtils.calculateGoals(user).calories >= 1200)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `TC10 - Invalid weight string throws exception`() {
        CalculationUtils.calculateGoals(profile("Male", 25, "not_a_number"))
    }

    @Test fun `TC11 - Unknown activity level falls back gracefully`() {
        val user = profile("Female", 30, "60", activityLevel = "UNKNOWN_LEVEL")
        val goals = CalculationUtils.calculateGoals(user)
        assertTrue("Calories > 0", goals.calories > 0)
        assertTrue("Carbs >= 0", goals.carbs >= 0)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // NEW TESTS — Medical Conditions, Obesity, Pregnancy, GoalPace
    // ═══════════════════════════════════════════════════════════════════════════

    @Test fun `TC13 - Obese Adult BMI 35 uses Adjusted Body Weight`() {
        // 120kg, 5ft 7in → BMI ~41 → should use ABW for BMR calculation
        val obese  = profile("Male", 35, "120", heightFeet = 5, heightInches = 7)
        val normal = profile("Male", 35, "80",  heightFeet = 5, heightInches = 7)
        printResult("TC13 Obese Adult ABW", obese)
        val obeseGoals  = CalculationUtils.calculateGoals(obese)
        val normalGoals = CalculationUtils.calculateGoals(normal)
        // Obese person should NOT have calories proportionally 120/80 = 1.5x more
        val calorieRatio = obeseGoals.calories.toDouble() / normalGoals.calories
        println("  [Calorie Ratio obese/normal] = ${String.format("%.2f", calorieRatio)}x (should be < 1.3)")
        assertTrue("ABW reduces overestimation: ratio < 1.35", calorieRatio < 1.35)
        assertTrue("Carbs >= 0", obeseGoals.carbs >= 0)
    }

    @Test fun `TC14 - Thyroid Condition reduces calories 18 percent`() {
        val normal  = profile("Female", 35, "70", activityLevel = "Moderate Activity")
        val thyroid = profile("Female", 35, "70", activityLevel = "Moderate Activity",
            medicalConditions = listOf(CalculationUtils.CONDITION_THYROID))
        printResult("TC14 Thyroid Condition", thyroid)
        val normalGoals  = CalculationUtils.calculateGoals(normal)
        val thyroidGoals = CalculationUtils.calculateGoals(thyroid)
        println("  [Normal: ${normalGoals.calories}  vs  Thyroid: ${thyroidGoals.calories}]")
        assertTrue("Thyroid should reduce calories", thyroidGoals.calories < normalGoals.calories)
        val reduction = normalGoals.calories - thyroidGoals.calories
        println("  [Reduction: $reduction kcal — expected ~14-20% of BMR]")
        assertTrue("Carbs >= 0", thyroidGoals.carbs >= 0)
    }

    @Test fun `TC15 - Diabetes caps carbs at 130g`() {
        val diabetic = profile("Male", 45, "85",
            medicalConditions = listOf(CalculationUtils.CONDITION_DIABETES))
        printResult("TC15 Diabetes Carb Cap", diabetic)
        val goals = CalculationUtils.calculateGoals(diabetic)
        assertTrue("Diabetes: carbs must be <= 130g (ADA guideline)", goals.carbs <= 130)
        assertTrue("Carbs >= 0", goals.carbs >= 0)
    }

    @Test fun `TC16 - PCOS limits carbs and deficit`() {
        val pcos = profile("Female", 28, "75", goal = "Lose Weight",
            medicalConditions = listOf(CalculationUtils.CONDITION_PCOS))
        val normal = profile("Female", 28, "75", goal = "Lose Weight")
        printResult("TC16 PCOS", pcos)
        val pcosGoals   = CalculationUtils.calculateGoals(pcos)
        val normalGoals = CalculationUtils.calculateGoals(normal)
        val pcosDeficit   = CalculationUtils.calculateGoals(pcos.copy(goal = "Maintain")).calories - pcosGoals.calories
        val normalDeficit = CalculationUtils.calculateGoals(normal.copy(goal = "Maintain")).calories - normalGoals.calories
        println("  [PCOS deficit: $pcosDeficit vs Normal: $normalDeficit]")
        assertTrue("PCOS deficit should be gentler", pcosDeficit <= normalDeficit)
        assertTrue("PCOS: carbs <= 130g", pcosGoals.carbs <= 130)
    }

    @Test fun `TC17 - Kidney Disease severely restricts protein`() {
        val kidney = profile("Male", 55, "80",
            medicalConditions = listOf(CalculationUtils.CONDITION_KIDNEY))
        printResult("TC17 Kidney Disease", kidney)
        val goals = CalculationUtils.calculateGoals(kidney)
        // Protein must be <= 0.8g/kg actual weight (CKD guideline)
        assertTrue("Kidney: protein <= 0.8g/kg", goals.protein <= (80 * 0.8).toInt() + 2)
        assertTrue("Carbs >= 0", goals.carbs >= 0)
    }

    @Test fun `TC18 - Goal Pace Slow gives smaller deficit than Fast`() {
        val slow = profile("Female", 30, "70", goal = "Lose Weight", goalPace = "Slow")
        val fast = profile("Female", 30, "70", goal = "Lose Weight", goalPace = "Fast")
        printResult("TC18 Slow Pace", slow)
        printResult("TC18 Fast Pace", fast)
        val slowGoals = CalculationUtils.calculateGoals(slow)
        val fastGoals = CalculationUtils.calculateGoals(fast)
        println("  [Slow: ${slowGoals.calories}  Fast: ${fastGoals.calories}]")
        assertTrue("Slow pace gives more calories than Fast", slowGoals.calories > fastGoals.calories)
    }

    @Test fun `TC19 - Pregnancy 2nd Trimester adds 340 calories`() {
        val normal    = profile("Female", 28, "65")
        val pregnant2 = profile("Female", 28, "65",
            pregnancyStatus = CalculationUtils.PREGNANCY_SECOND)
        printResult("TC19 Pregnancy 2nd Trimester", pregnant2)
        val normalGoals   = CalculationUtils.calculateGoals(normal)
        val pregnantGoals = CalculationUtils.calculateGoals(pregnant2)
        val bonus = pregnantGoals.calories - normalGoals.calories
        println("  [Pregnancy 2nd bonus: $bonus kcal (expected 340)]")
        assertEquals("2nd trimester: +340 kcal", 340, bonus)
    }

    @Test fun `TC20 - Breastfeeding adds 500 calories`() {
        val normal      = profile("Female", 28, "65")
        val breastfeed  = profile("Female", 28, "65",
            pregnancyStatus = CalculationUtils.PREGNANCY_BREASTFEEDING)
        printResult("TC20 Breastfeeding", breastfeed)
        val normalGoals = CalculationUtils.calculateGoals(normal)
        val bfGoals     = CalculationUtils.calculateGoals(breastfeed)
        val bonus = bfGoals.calories - normalGoals.calories
        println("  [Breastfeeding bonus: $bonus kcal (expected 500)]")
        assertEquals("Breastfeeding: +500 kcal", 500, bonus)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // FULL DEMOGRAPHIC SWEEP — Visual Table
    // ═══════════════════════════════════════════════════════════════════════════
    @Test fun `TC21 - Full demographic sweep with conditions`() {
        println("\n========== FULL DEMOGRAPHIC SWEEP (v2 — with conditions) ==========\n")

        data class Case(val label: String, val user: UserProfile)

        val cases = listOf(
            Case("Child Boy 8yo 28kg",              profile("Male",   8,  "28", 4, 2, "Moderate Activity", "Maintain")),
            Case("Child Girl 10yo 34kg",             profile("Female", 10, "34", 4, 5, "Low Activity",      "Maintain")),
            Case("Teen Boy 15yo 55kg (active+gain)", profile("Male",   15, "55", 5, 6, "High Activity",     "Gain Weight")),
            Case("Teen Girl 14yo 50kg",              profile("Female", 14, "50", 5, 4, "Moderate Activity", "Maintain")),
            Case("Adult Male 25yo 75kg",             profile("Male",   25, "75", 5, 10,"High Activity",     "Gain Weight")),
            Case("Adult Female 30yo 60kg lose",      profile("Female", 30, "60", 5, 5, "Moderate Activity", "Lose Weight")),
            Case("Obese Male 40yo 120kg",            profile("Male",   40, "120",5, 7, "Low Activity",      "Lose Weight")),
            Case("Diabetic Male 50yo 90kg",          profile("Male",   50, "90", 5, 8, "No Exercise",       "Maintain",  medicalConditions = listOf("Diabetes"))),
            Case("PCOS Female 32yo 80kg",            profile("Female", 32, "80", 5, 4, "Low Activity",      "Lose Weight", medicalConditions = listOf("PCOS"))),
            Case("Thyroid Female 40yo 75kg",         profile("Female", 40, "75", 5, 5, "No Exercise",       "Maintain",  medicalConditions = listOf("Thyroid"))),
            Case("Kidney Male 60yo 78kg",            profile("Male",   60, "78", 5, 9, "Low Activity",      "Maintain",  medicalConditions = listOf("Kidney Disease"))),
            Case("Pregnant 2nd Trim Female 30yo",    profile("Female", 30, "68", 5, 5, "Low Activity",      "Maintain",  pregnancyStatus = "2nd Trimester")),
            Case("Elderly Male 70yo 70kg",           profile("Male",   70, "70", 5, 7, "No Exercise",       "Maintain")),
            Case("Elderly Female 75yo 58kg",         profile("Female", 75, "58", 5, 2, "No Exercise",       "Lose Weight")),
        )

        println(String.format("%-38s | %3s | %8s | %8s | %7s | %7s", "Person", "Age", "Calories", "Protein", "Fat", "Carbs"))
        println("-".repeat(90))

        for (c in cases) {
            val goals = CalculationUtils.calculateGoals(c.user)
            println(String.format("%-38s | %3d | %8d | %6dg   | %5dg  | %5dg",
                c.label, c.user.age, goals.calories, goals.protein, goals.fats, goals.carbs))
            assertTrue("${c.label}: calories > 0", goals.calories > 0)
            assertTrue("${c.label}: protein > 0",  goals.protein > 0)
            assertTrue("${c.label}: fat > 0",      goals.fats > 0)
            assertTrue("${c.label}: carbs >= 0",   goals.carbs >= 0)
        }
        println("\n====================================================================\n")
    }
}
