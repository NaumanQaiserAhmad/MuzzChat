package com.muzz.core.domain.model

import com.muzz.core.domain.model.DeliveryStatus
import java.time.Instant

data class Message(
    val id: Long = 0L,
    val fromUserId: String,
    val text: String,
    val sentAt: Instant,
    val deliveryStatus: DeliveryStatus = DeliveryStatus.SENT
)
