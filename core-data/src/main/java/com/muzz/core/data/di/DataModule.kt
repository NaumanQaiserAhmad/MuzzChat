package com.muzz.core.data.di

import android.content.Context
import androidx.room.Room
import com.muzz.core.data.local.AppDatabase
import com.muzz.core.data.local.MessageDao
import com.muzz.core.data.repo.MessageRepositoryImpl
import com.muzz.core.domain.repo.MessageRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindModule {
    @Binds
    abstract fun bindRepo(impl: MessageRepositoryImpl): MessageRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "muzz.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideDao(db: AppDatabase): MessageDao = db.messageDao()

}
