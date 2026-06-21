package com.magics.puffer.di

import android.content.Context
import androidx.room.Room
import com.magics.puffer.data.db.DailyLogDao
import com.magics.puffer.data.db.PufferDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt dependency injection module.
 * Provides singleton instances of database and DAO.
 * DataStore and Repository are injected directly via @Inject constructor.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePufferDatabase(
        @ApplicationContext context: Context
    ): PufferDatabase {
        return Room.databaseBuilder(
            context,
            PufferDatabase::class.java,
            PufferDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideDailyLogDao(database: PufferDatabase): DailyLogDao {
        return database.dailyLogDao()
    }
}
