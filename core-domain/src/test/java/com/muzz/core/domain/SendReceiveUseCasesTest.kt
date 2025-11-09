package com.muzz.core.domain

import com.muzz.core.domain.model.DeliveryStatus
import com.muzz.core.domain.model.Message
import com.muzz.core.domain.repo.MessageRepository
import com.muzz.core.domain.usecase.ReceiveMessage
import com.muzz.core.domain.usecase.SendMessage
import com.muzz.core.domain.util.AppResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.invoke

private class FakeRepo: MessageRepository {
    private val store = mutableListOf<Message>()
    private var id = 1L
    override fun observeAll() = flow { emit(store.toList()) }
    override suspend fun insert(message: Message): Long {
        val m = message.copy(id = id++)
        store.add(m); return m.id
    }
    override suspend fun count(): Long = store.size.toLong()
    override suspend fun updateStatus(id: Long, status: DeliveryStatus) {
        store.replaceAll { if (it.id == id) it.copy(deliveryStatus = status) else it }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SendReceiveUseCasesTest {
    @Test fun sendMessageReturnsSuccess() = runTest {
        val r = SendMessage(FakeRepo())("hello", "me")
        assertTrue(r is AppResult.Success)
    }

    @Test fun receiveMessageReturnsSuccess() = runTest {
        val r = ReceiveMessage(FakeRepo())("hi", "other")
        assertTrue(r is AppResult.Success)
    }
}
