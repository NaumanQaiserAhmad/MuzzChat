package com.muzz.core.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.muzz.core.domain.util.AppResult
import com.muzz.core.domain.model.DeliveryStatus
import com.muzz.core.domain.model.Message
import com.muzz.core.domain.repo.MessageRepository
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

class SeedMessages @Inject constructor(
    private val repo: MessageRepository
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(): AppResult<Unit> {
        return try {
            if (repo.count() > 0) return AppResult.Success(Unit)
            val base: Instant = Instant.now().minus(Duration.ofHours(2))
            val msgs = listOf(
                Message(
                    fromUserId = "other",
                    text = "Salaam! Tell me a bit about you?",
                    sentAt = base,
                    deliveryStatus = DeliveryStatus.DELIVERED
                ),
                Message(
                    fromUserId = "me",
                    text = "Hi! I’m an Android Engineer focused on Kotlin, Jetpack Compose, Coroutines/Flow, Room & Hilt. I keep things simple and robust.",
                    sentAt = base.plusSeconds(20),
                    deliveryStatus = DeliveryStatus.READ
                ),
                Message(
                    fromUserId = "other",
                    text = "What architecture do you use?",
                    sentAt = base.plus(Duration.ofMinutes(1)),
                    deliveryStatus = DeliveryStatus.DELIVERED
                ),
                Message(
                    fromUserId = "me",
                    text = "Modular clean architecture (domain/data/ui), explicit AppResult for errors, and a ViewModel UiState so the UI stays predictable.",
                    sentAt = base.plus(Duration.ofMinutes(1)).plusSeconds(10),
                    deliveryStatus = DeliveryStatus.READ
                ),
                Message(
                    fromUserId = "other",
                    text = "How do you approach testing?",
                    sentAt = base.plus(Duration.ofMinutes(65)),
                    deliveryStatus = DeliveryStatus.DELIVERED
                ),
                Message(
                    fromUserId = "me",
                    text = "Repo: in-memory Room + Robolectric; Domain: pure JVM; ViewModel: Turbine + test Main; UI: instrumented Compose tests for status/spacing.",
                    sentAt = base.plus(Duration.ofMinutes(65)).plusSeconds(10),
                    deliveryStatus = DeliveryStatus.READ
                ),
                Message(
                    fromUserId = "other",
                    text = "Why do you think you’re a good fit for Muzz?",
                    sentAt = base.plus(Duration.ofMinutes(65)).plusSeconds(25),
                    deliveryStatus = DeliveryStatus.DELIVERED
                ),
                Message(
                    fromUserId = "me",
                    text = "I value intentional, faith-aligned products and thoughtful UX. This app is simple, scalable and respectful—exactly the kind of work I want to contribute to at Muzz.",
                    sentAt = base.plus(Duration.ofMinutes(65)).plusSeconds(45),
                    deliveryStatus = DeliveryStatus.READ
                )
            )

            for (m in msgs) repo.insert(m)
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Error(t)
        }
    }
}
