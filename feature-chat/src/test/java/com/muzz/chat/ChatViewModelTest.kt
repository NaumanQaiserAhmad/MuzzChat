package com.muzz.chat

import app.cash.turbine.awaitItem
import app.cash.turbine.test
import com.muzz.chat.presentation.ChatViewModel
import com.muzz.core.domain.model.DeliveryStatus
import com.muzz.core.domain.model.Message
import com.muzz.core.domain.repo.MessageRepository
import com.muzz.core.domain.usecase.GetMessages
import com.muzz.core.domain.usecase.ReceiveMessage
import com.muzz.core.domain.usecase.SeedMessages
import com.muzz.core.domain.usecase.SendMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private class FakeRepo : MessageRepository {
    private val state = MutableStateFlow(listOf<Message>())
    private var id = 1L
    override fun observeAll(): Flow<List<Message>> = state
    override suspend fun insert(message: Message): Long {
        val m = message.copy(id = id++)
        state.value = state.value + m
        return m.id
    }
    override suspend fun count(): Long = state.value.size.toLong()
    override suspend fun updateStatus(id: Long, status: DeliveryStatus) {
        state.value = state.value.map { if (it.id == id) it.copy(deliveryStatus = status) else it }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()   // your existing rule

    @Test
    fun `uiState transitions from Loading to Ready on init`() = runTest {
        val repo = FakeRepo()
        val vm = ChatViewModel(
            getMessages = GetMessages(repo),
            sendMessage = SendMessage(repo),
            receiveMessage = ReceiveMessage(repo),
            seedMessages = SeedMessages(repo)
        )

        // ✅ IMPORTANT: drive any onStart{} / shareIn(…, Lazily) work
        val drivePaging = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.pagedUi.collect { /* no-op */ }
        }

        vm.uiState.test {
            // first emission
            assertTrue(awaitItem() is ChatViewModel.UiState.Loading)

            // let init/seeding coroutines run
            advanceUntilIdle()

            // consume until we see Ready (in case there are duplicate Loading emissions)
            var next = awaitItem()
            while (next is ChatViewModel.UiState.Loading) {
                next = awaitItem()
            }
            assertTrue(next is ChatViewModel.UiState.Ready)

            cancelAndIgnoreRemainingEvents()
        }

        drivePaging.cancel()
    }
}
