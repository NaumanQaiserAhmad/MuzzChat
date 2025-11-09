package com.muzz.core.domain.usecase

import com.muzz.core.domain.model.DeliveryStatus
import com.muzz.core.domain.model.Message
import com.muzz.core.domain.repo.MessageRepository
import com.muzz.core.domain.util.AppResult
import java.time.Instant

class ReceiveMessage(private val repo: MessageRepository) {
    suspend operator fun invoke(text: String, fromUserId: String): AppResult<Unit> {
        // ✅ FIX 1: Add validation for blank text
        if (text.isBlank()) {
            return AppResult.Error(IllegalArgumentException("Received message text cannot be blank."))
        }

        val message = Message(
            fromUserId = fromUserId,
            text = text,
            sentAt = Instant.now(),
            // ✅ FIX 2: A newly received message should have the status SENT, not DELIVERED
            deliveryStatus = DeliveryStatus.SENT
        )
        repo.insert(message)
        return AppResult.Success(Unit)
    }
}
