package com.muzz.core.data.repo

import com.muzz.core.data.local.MessageDao
import com.muzz.core.data.local.MessageEntity
import com.muzz.core.domain.model.DeliveryStatus
import com.muzz.core.domain.model.Message
import com.muzz.core.domain.repo.MessageRepository
import kotlinx.coroutines.flow.Flow

import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.get

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val dao: MessageDao
) : MessageRepository {
    override fun observeAll(): Flow<List<Message>> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(message: Message): Long =
        dao.insert(message.toEntity())

    override suspend fun count(): Long {
        TODO("Not yet implemented")
    }

    override suspend fun updateStatus(
        id: Long,
        status: DeliveryStatus
    ) {
        TODO("Not yet implemented")
    }

    private fun MessageEntity.toDomain() = Message(
        id = id,
        fromUserId = fromUserId,
        text = text,
        sentAt = Instant.ofEpochMilli(sentAtEpochMs),
        deliveryStatus = DeliveryStatus.values()[deliveryStatus]
    )

    private fun Message.toEntity() = MessageEntity(
        id = if (id == 0L) 0 else id,
        fromUserId = fromUserId,
        text = text,
        sentAtEpochMs = sentAt.toEpochMilli(),
        deliveryStatus = deliveryStatus.ordinal
    )
}
