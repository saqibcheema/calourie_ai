package com.example.calorieapp.DI

import android.content.Context
import androidx.room.Room
import com.example.calorieapp.data.DataSource.local.AppDatabase
import com.example.calorieapp.data.DataSource.local.ProductDao
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
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "calorie_app_db"
        )
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