package com.muzz.core.data.repo
import com.muzz.core.data.local.MessageDao
import com.muzz.core.data.local.MessageEntity
import com.muzz.core.domain.model.DeliveryStatus
import com.muzz.core.domain.model.Message
import com.muzz.core.domain.repo.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val dao: MessageDao
) : MessageRepository {

    override fun observeAll(): Flow<List<Message>> =
        dao.observeAllAsc().map { list -> list.map { it.toDomain() } }

    override suspend fun insert(message: Message): Long =
        dao.insert(message.toEntity())

    override suspend fun count(): Long = dao.count()

    override suspend fun updateStatus(id: Long, status: DeliveryStatus) {
        dao.updateStatus(id, status.ordinal)
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
