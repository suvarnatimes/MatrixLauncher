package com.matrixlauncher.di

import android.content.Context
import androidx.room.Room
import com.matrixlauncher.data.local.datastore.PreferencesManager
import com.matrixlauncher.data.local.db.AppCustomizationDao
import com.matrixlauncher.data.local.db.LauncherDatabase
import com.matrixlauncher.data.repository.AppDatabaseRepositoryImpl
import com.matrixlauncher.data.repository.LauncherAppsRepositoryImpl
import com.matrixlauncher.data.repository.PreferencesRepositoryImpl
import com.matrixlauncher.domain.repository.AppDatabaseRepository
import com.matrixlauncher.domain.repository.LauncherAppsRepository
import com.matrixlauncher.domain.repository.PreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideLauncherDatabase(
        @ApplicationContext context: Context
    ): LauncherDatabase {
        return Room.databaseBuilder(
            context,
            LauncherDatabase::class.java,
            LauncherDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideAppCustomizationDao(database: LauncherDatabase): AppCustomizationDao {
        return database.appCustomizationDao()
    }

    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager {
        return PreferencesManager(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLauncherAppsRepository(
        impl: LauncherAppsRepositoryImpl
    ): LauncherAppsRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        impl: PreferencesRepositoryImpl
    ): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindAppDatabaseRepository(
        impl: AppDatabaseRepositoryImpl
    ): AppDatabaseRepository
}
