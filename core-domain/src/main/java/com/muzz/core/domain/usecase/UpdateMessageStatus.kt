package com.muzz.core.domain.usecase

import com.muzz.core.domain.model.DeliveryStatus
import com.muzz.core.domain.repo.MessageRepository
import javax.inject.Inject

class UpdateMessageStatus @Inject constructor(
    private val repo: MessageRepository
) {
    suspend operator fun invoke(id: Long, status: DeliveryStatus) = repo.updateStatus(id, status)
}
