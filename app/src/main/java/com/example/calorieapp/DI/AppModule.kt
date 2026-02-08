package com.example.calorieapp.DI

import android.content.Context
import androidx.room.Room
import com.example.calorieapp.data.DataSource.local.AppDatabase
import com.example.calorieapp.data.DataSource.local.UserDao
import com.example.calorieapp.data.repository.UserRepositoryImplementation
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
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(db: AppDatabase): UserDao {
        return db.userDao()
    }

    @Provides
    @Singleton
    fun provideUserRepository(dao: UserDao) : UserRepository{
        return UserRepositoryImplementation(dao)
    }
}