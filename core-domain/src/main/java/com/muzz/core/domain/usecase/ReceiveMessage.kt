package com.muzz.core.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.muzz.core.domain.util.AppResult
import com.muzz.core.domain.model.DeliveryStatus
import com.muzz.core.domain.model.Message
import com.muzz.core.domain.repo.MessageRepository
import java.time.Instant
import javax.inject.Inject

class ReceiveMessage @Inject constructor(
    private val repo: MessageRepository
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(text: String, fromUserId: String): AppResult<Long> = try {
        val id = repo.insert(
            Message(
                fromUserId = fromUserId,
                text = text,
                sentAt = Instant.now(),
                deliveryStatus = DeliveryStatus.DELIVERED
            )
        )
        AppResult.Success(id)
    } catch (t: Throwable) {
        AppResult.Error(t)
    }
}
