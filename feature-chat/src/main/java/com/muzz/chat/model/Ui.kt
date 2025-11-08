package com.muzz.chat.model

import com.muzz.core.domain.model.DeliveryStatus
import java.time.Instant
sealed interface UiItem {
    val stableKey: Any
    val contentType: String

    data class SectionHeader(val title: String): UiItem {
        override val stableKey: Any = "header:$title"
        override val contentType: String = "header"
    }

    data class Bubble(
        val id: Long,
        val text: String,
        val isMine: Boolean,
        val sentAt: Instant,
        val status: DeliveryStatus
    ): UiItem {
        override val stableKey: Any = id
        override val contentType: String = "bubble"
    }
}
