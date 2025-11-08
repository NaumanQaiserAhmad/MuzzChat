package com.muzz.chat.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.PagingData.Companion.from
import androidx.paging.cachedIn
import com.example.core.domain.usecase.SeedMessages
import com.muzz.chat.model.UiItem
import com.muzz.core.domain.formats.DateFormats
import com.muzz.core.domain.model.Message
import com.muzz.core.domain.policy.MessagePolicies
import com.muzz.core.domain.usecase.GetMessages
import com.muzz.core.domain.usecase.ReceiveMessage
import com.muzz.core.domain.usecase.SendMessage
import com.muzz.core.domain.util.AppResult

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getMessages: GetMessages,
    private val sendMessage: SendMessage,
    private val receiveMessage: ReceiveMessage,
    private val seedMessages: SeedMessages
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        object Ready : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState


    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern(DateFormats.CHAT_HEADER_PATTERN)

    /** Stream of UI rows with headers and bubbles, exposed as PagingData for the UI. */
    @RequiresApi(Build.VERSION_CODES.O)
    val pagedUi: Flow<PagingData<UiItem>> = getMessages()
        .map { list -> list.sortedBy { it.sentAt } }
        .map { list ->
            val rows = buildUiRows(list)
            from(rows)
        }
        .onStart {
            when (val r = seedMessages()) {
                is AppResult.Success -> _uiState.value = UiState.Ready
                is AppResult.Error -> _uiState.value = UiState.Error(r.throwable.message ?: "Failed to load")
            }
        }
        .cachedIn(viewModelScope)

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSend(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            // Insert my message (status progression handled inside SendMessage)
            when (val r = sendMessage(text, "me")) {
                is AppResult.Success -> {}
                is AppResult.Error -> _uiState.value = UiState.Error(r.throwable.message ?: "Send failed")
            }

            // Simulate a reply
            delay((600..1200).random().toLong())
            val reply = AutoReplies.options.random()
            receiveMessage(reply, "other")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildUiRows(messages: List<Message>): List<UiItem> {
        val rows = mutableListOf<UiItem>()
        var last: Message? = null
        for (m in messages) {
            val needHeader = last == null ||
                Duration.between(last!!.sentAt, m.sentAt).abs() > MessagePolicies.SECTION_GAP
            if (needHeader) rows += UiItem.SectionHeader(formatHeader(m.sentAt))
            rows += m.toBubble()
            last = m
        }
        return rows
    }

    private fun Message.toBubble() = UiItem.Bubble(
        id = id,
        text = text,
        isMine = fromUserId == "me",
        sentAt = sentAt,
        status = deliveryStatus
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatHeader(instant: Instant): String =
        instant.atZone(ZoneId.systemDefault()).format(formatter)
}
