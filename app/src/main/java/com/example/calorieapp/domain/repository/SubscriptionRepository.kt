import com.example.calorieapp.data.Models.SubscriptionStatus
import com.example.calorieapp.data.Models.UserDailyRequest
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {

    fun getSubscriptionData(): Flow<SubscriptionStatus?>
    fun getDailyUsageData(): Flow<UserDailyRequest?>
    suspend fun resetLimitsIfNewDay()
    suspend fun checkCanUseAiVision(): Boolean
    suspend fun checkCanUseManualEntry(): Boolean
    suspend fun checkCanUseBarcodeScan(): Boolean
    suspend fun addOneToAiUsage()
    suspend fun addOneToManualEntryUsage()
    suspend fun addOneToBarcodeScanUsage()
    suspend fun addOneToAdsWatched()
    suspend fun saveNewSubscription(subscriptionStatus : SubscriptionStatus)
}
