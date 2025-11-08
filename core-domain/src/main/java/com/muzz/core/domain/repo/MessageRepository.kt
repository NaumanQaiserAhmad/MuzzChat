package com.muzz.core.domain.repo

import com.muzz.core.domain.model.DeliveryStatus
import com.muzz.core.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun observeAll(): Flow<List<Message>>
    suspend fun insert(message: Message): Long
    suspend fun count(): Long
    suspend fun updateStatus(id: Long, status: DeliveryStatus)
}
