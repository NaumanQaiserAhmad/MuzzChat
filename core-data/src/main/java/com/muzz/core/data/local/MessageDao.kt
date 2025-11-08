package com.muzz.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY sentAtEpochMs ASC")
    fun observeAllAsc(): Flow<List<MessageEntity>>

    @Insert
    suspend fun insert(entity: MessageEntity): Long

    @Query("SELECT COUNT(*) FROM messages")
    suspend fun count(): Long

    
    @Query("UPDATE messages SET deliveryStatus = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: Int)
}

