package com.muzz.core.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.muzz.core.domain.util.AppResult
import com.muzz.core.domain.model.DeliveryStatus
import com.muzz.core.domain.model.Message
import com.muzz.core.domain.repo.MessageRepository
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.delay

class SendMessage @Inject constructor(
    private val repo: MessageRepository
) {
    /** Inserts a message with SENDING and simulates status progression. */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(text: String, fromUserId: String): AppResult<Long> = try {
        val id = repo.insert(
            Message(
                fromUserId = fromUserId,
                text = text,
                sentAt = Instant.now(),
                deliveryStatus = DeliveryStatus.SENDING
            )
        )
        // Simulate progression — friendly to runTest virtual time
        delay((200..500).random().toLong())
        repo.updateStatus(id, DeliveryStatus.SENT)
        delay((500..1200).random().toLong())
        repo.updateStatus(id, DeliveryStatus.DELIVERED)
        delay((800..1400).random().toLong())
        repo.updateStatus(id, DeliveryStatus.READ)
        AppResult.Success(id)
    } catch (t: Throwable) {
        AppResult.Error(t)
    }
}
