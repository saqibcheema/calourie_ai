package com.example.calorieapp.DI

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.calorieapp.data.DataSource.local.AppDatabase
import com.example.calorieapp.data.DataSource.local.UserDao
import com.example.calorieapp.data.repository.UserRepositoryImplementation
import com.example.calorieapp.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application) : RoomDatabase {
        return Room.databaseBuilder(
            app,
            RoomDatabase::class.java,
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