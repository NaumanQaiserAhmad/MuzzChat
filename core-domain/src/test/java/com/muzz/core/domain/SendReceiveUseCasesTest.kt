package com.muzz.core.domain

import com.muzz.core.domain.model.DeliveryStatus
import com.muzz.core.domain.model.Message
import com.muzz.core.domain.repo.MessageRepository // Make sure this is imported correctly
import com.muzz.core.domain.usecase.ReceiveMessage
import com.muzz.core.domain.usecase.SendMessage
import com.muzz.core.domain.util.AppResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

// --- This FakeRepo is well-designed for testing ---
private class FakeRepo : MessageRepository { // ✅ FIX: Corrected typo from MessageRepo_sitory
    private val messages = mutableListOf<Message>()
    private var nextId = 1L

    override fun observeAll() = flow { emit(messages.toList()) }

    override suspend fun insert(message: Message): Long {
        val messageWithId = message.copy(id = nextId++)
        messages.add(messageWithId)
        return messageWithId.id
    }

    override suspend fun count(): Long = messages.size.toLong()

    override suspend fun updateStatus(id: Long, status: DeliveryStatus) {
        val index = messages.indexOfFirst { it.id == id }
        if (index != -1) {
            messages[index] = messages[index].copy(deliveryStatus = status)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SendReceiveUseCasesTest {

    private lateinit var fakeRepo: FakeRepo
    private lateinit var sendMessage: SendMessage
    private lateinit var receiveMessage: ReceiveMessage

    @Before
    fun setup() {
        // Create fresh instances for each test to ensure isolation
        fakeRepo = FakeRepo()
        sendMessage = SendMessage(fakeRepo)
        receiveMessage = ReceiveMessage(fakeRepo)
    }

    // --- Tests for SendMessage Use Case ---

    @Test
    fun `SendMessage with valid text should insert message with 'me' as sender and return Success`() = runTest {
        // Given
        val text = "Hello, world!"
        val myId = "me"

        // When
        val result = sendMessage(text, myId)

        // Then
        assertTrue("Result should be Success", result is AppResult.Success)

        val messages = fakeRepo.observeAll().first()
        assertEquals("There should be one message in the repository", 1, messages.size)

        val insertedMessage = messages.first()
        assertEquals("Message text should match", text, insertedMessage.text)
        assertEquals("Sender ID should be 'me'", myId, insertedMessage.fromUserId)
        assertEquals("Initial status should be SENDING", DeliveryStatus.SENDING, insertedMessage.deliveryStatus)
    }

    @Test
    fun `SendMessage with blank text should return Error and not insert a message`() = runTest {
        // Given
        val blankText = "   "
        val myId = "me"

        // When
        val result = sendMessage(blankText, myId)

        // Then
        assertTrue("Result for blank text should be Error", result is AppResult.Error)
        assertEquals("Repository should remain empty", 0L, fakeRepo.count())
    }

    // --- Tests for ReceiveMessage Use Case ---

    @Test
    fun `ReceiveMessage with valid text should insert message with 'other' as sender and return Success`() = runTest {
        // Given
        val text = "Hi there!"
        val otherId = "other"

        // When
        val result = receiveMessage(text, otherId)

        // Then
        assertTrue("Result should be Success", result is AppResult.Success)

        val messages = fakeRepo.observeAll().first()
        assertEquals("There should be one message in the repository", 1, messages.size)

        val insertedMessage = messages.first()
        assertEquals("Message text should match", text, insertedMessage.text)
        assertEquals("Sender ID should be 'other'", otherId, insertedMessage.fromUserId)
        assertEquals("Initial status should be SENT", DeliveryStatus.SENT, insertedMessage.deliveryStatus)
    }

    @Test
    fun `ReceiveMessage with blank text should return Error and not insert a message`() = runTest {
        // Given
        val blankText = ""
        val otherId = "other"

        // When
        val result = receiveMessage(blankText, otherId)

        // Then
        assertTrue("Result for blank text should be Error", result is AppResult.Error)
        assertEquals("Repository should remain empty", 0L, fakeRepo.count())
    }
}
