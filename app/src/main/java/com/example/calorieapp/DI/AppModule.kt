package com.example.calorieapp.DI

import android.content.Context
import androidx.room.Room
import com.example.calorieapp.data.DataSource.local.AppDatabase
import com.example.calorieapp.data.DataSource.local.MealDao
import com.example.calorieapp.data.DataSource.local.UserDao
import com.example.calorieapp.data.repository.MealRepositoryImpl
import com.example.calorieapp.data.repository.UserRepositoryImplementation
import com.example.calorieapp.domain.repository.MealRepository
import com.example.calorieapp.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
            .fallbackToDestructiveMigrationFrom()
            .fallbackToDestructiveMigrationOnDowngrade()
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
    fun provideMealDao(db: AppDatabase): MealDao {
        return db.MealDao()
    }

    @Provides
    @Singleton
    fun provideMealRepository(repoImpl: MealRepositoryImpl) : MealRepository{
        return repoImpl
    }
}