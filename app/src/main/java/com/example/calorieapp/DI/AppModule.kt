package com.example.calorieapp.DI

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.calorieapp.data.DataSource.local.AppDatabase
import com.example.calorieapp.data.DataSource.local.ProductDao
import com.example.calorieapp.data.DataSource.local.ScannedProductDao
import com.example.calorieapp.data.DataSource.local.UserDao
import com.example.calorieapp.data.DataSource.remote.BarcodeApiService
import com.example.calorieapp.data.DataSource.remote.GroqApiService
import com.example.calorieapp.data.repository.BarcodeRepositoryImpl
import com.example.calorieapp.data.repository.GroqNutritionRepositoryImpl
import com.example.calorieapp.data.repository.UserRepositoryImplementation
import com.example.calorieapp.domain.repository.BarcodeRepository
import com.example.calorieapp.domain.repository.GroqNutritionRepository
import com.example.calorieapp.domain.repository.UserRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) : AppDatabase {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE scanned_products ADD COLUMN quantity INTEGER NOT NULL DEFAULT 1")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Clear the cache so previously scanned items can fetch the newly scaled nutritional values
                db.execSQL("DELETE FROM scanned_products_cache")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `scanned_products_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `barcode` TEXT NOT NULL, `productName` TEXT, `brand` TEXT, `imageUrl` TEXT, `calories` REAL NOT NULL, `protein` REAL NOT NULL, `carbs` REAL NOT NULL, `fat` REAL NOT NULL, `fiber` REAL, `sugars` REAL, `scannedAt` INTEGER NOT NULL, `quantity` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL)
                """)
                db.execSQL("""
                    INSERT INTO `scanned_products_new` (`barcode`, `productName`, `brand`, `imageUrl`, `calories`, `protein`, `carbs`, `fat`, `fiber`, `sugars`, `scannedAt`, `quantity`, `isDeleted`)
                    SELECT `barcode`, `productName`, `brand`, `imageUrl`, `calories`, `protein`, `carbs`, `fat`, `fiber`, `sugars`, `scannedAt`, `quantity`, `isDeleted` FROM `scanned_products`
                """)
                db.execSQL("DROP TABLE `scanned_products`")
                db.execSQL("ALTER TABLE `scanned_products_new` RENAME TO `scanned_products`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_scanned_products_barcode` ON `scanned_products` (`barcode`)")
            }
        }

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "calorie_app_db"
        )
            .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(db: AppDatabase): UserDao {
        return db.userDao()
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userImpl : UserRepositoryImplementation
    ) : UserRepository{
        return userImpl
    }

    @Provides
    @Singleton
    fun provideProductDao(db: AppDatabase)  : ProductDao{
        return db.ProductDao()
    }

    @Provides
    @Singleton
    fun provideScannedProductDao(db: AppDatabase): ScannedProductDao {
        return db.scannedProductDao()
    }

    @Provides
    @Singleton
    fun provideBarcodeRepository(
        barcodeImpl : BarcodeRepositoryImpl
    ) : BarcodeRepository {
        return barcodeImpl
    }

    @Provides
    @Singleton
    fun provideBarcodeApi(@ApplicationContext context: Context): BarcodeApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(com.example.calorieapp.data.network.interceptors.NetworkConnectionInterceptor(context))
            .addInterceptor(com.example.calorieapp.data.network.interceptors.RateLimitInterceptor())
            .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .build()
            
        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BarcodeApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideConnectivityObserver(@ApplicationContext context: Context): com.example.calorieapp.util.ConnectivityObserver {
        return com.example.calorieapp.util.NetworkConnectivityObserver(context)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideGroqApi(@ApplicationContext context: Context): GroqApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(com.example.calorieapp.data.network.interceptors.NetworkConnectionInterceptor(context))
            .addInterceptor(com.example.calorieapp.data.network.interceptors.RateLimitInterceptor())
            .addInterceptor(com.example.calorieapp.data.network.interceptors.GroqAuthInterceptor(com.example.calorieapp.BuildConfig.GROQ_API_KEY))
            .addInterceptor(logging)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build()
            
        return Retrofit.Builder()
            .baseUrl("https://api.groq.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GroqApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGroqNutritionRepository(
        impl: GroqNutritionRepositoryImpl
    ): GroqNutritionRepository = impl
}