package com.muzz.core.data.di

import android.content.Context
import androidx.room.Room
import com.muzz.core.domain.usecase.GetMessages
import com.muzz.core.domain.usecase.ReceiveMessage
import com.muzz.core.domain.usecase.SeedMessages
import com.muzz.core.data.local.AppDatabase
import com.muzz.core.data.local.MessageDao
import com.muzz.core.data.repo.MessageRepositoryImpl
import com.muzz.core.domain.repo.MessageRepository
import com.muzz.core.domain.usecase.UpdateMessageStatus
import com.muzz.core.domain.usecase.SendMessage
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

    // Provide domain use-cases wired to repo
    @Provides fun provideGetMessages(repo: MessageRepository) = GetMessages(repo)
    @Provides fun provideSendMessage(repo: MessageRepository) = SendMessage(repo)
    @Provides fun provideReceiveMessage(repo: MessageRepository) = ReceiveMessage(repo)
    @Provides fun provideSeedMessages(repo: MessageRepository) = SeedMessages(repo)
    @Provides fun provideUpdateMessageStatus(repo: MessageRepository) = UpdateMessageStatus(repo)
}
