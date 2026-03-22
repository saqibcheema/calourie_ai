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
import com.example.calorieapp.data.repository.BarcodeRepositoryImpl
import com.example.calorieapp.data.repository.UserRepositoryImplementation
import com.example.calorieapp.domain.repository.BarcodeRepository
import com.example.calorieapp.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "calorie_app_db"
        )
            .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
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
    fun provideBarcodeApi(): BarcodeApiService {
        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BarcodeApiService::class.java)
    }
}