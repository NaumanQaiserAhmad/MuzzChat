package com.example.feature.chat.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.muzz.chat.model.UiItem
import com.muzz.chat.presentation.ChatViewModel
import com.muzz.chat.presentation.components.ConversationDivider
import com.muzz.chat.presentation.components.DateChip
import com.muzz.chat.presentation.components.MessageInputBar
import com.muzz.chat.presentation.components.MessageRow
import com.muzz.chat.presentation.ui.topbar.ChatTopBar
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

@Composable
fun ChatScreen(
    partnerName: String,
    vm: ChatViewModel
) {
    val items = vm.pagedUi.collectAsLazyPagingItems()
    val state = vm.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ChatTopBar(partnerName = partnerName, onBack = {}, onMenu = {})

        // Apply weight HERE (inside ColumnScope)
        ChatList(
            modifier = Modifier.weight(1f),
            items = items,
            listState = listState,
            bottomContentPadding = 96.dp
        )

        // Bottom input card with rounded top corners + shadow
        Surface(
            tonalElevation = 0.dp,
            shadowElevation = 12.dp,
            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
        ) {

            //  separator BETWEEN the list and the input bar
            if (state.value is ChatViewModel.UiState.Error) {
                androidx.compose.material3.Text(
                    text = (state.value as ChatViewModel.UiState.Error).message,
                    color = androidx.compose.ui.graphics.Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }
            ConversationDivider()

            MessageInputBar(
                placeholder = "Type a message…",
                onSend = { vm.onSend(it) },
                sendButtonColor = Color(0xFFE64B71), // whatever you're using
                modifier = Modifier
                    .navigationBarsPadding() // keep above gestures
                    .imePadding()            // lift when keyboard shows
            )
        }

        LaunchedEffect(items.itemCount) {
            if (items.itemCount > 0) {
                scope.launch { listState.animateScrollToItem(items.itemCount - 1) }
            }
        }
    }
}

@Composable
private fun ChatList(
    modifier: Modifier = Modifier,
    items: LazyPagingItems<UiItem>,
    listState: LazyListState,
    bottomContentPadding: Dp
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        contentPadding = PaddingValues(
            start = 12.dp, end = 12.dp, top = 8.dp, bottom = bottomContentPadding
        )
    ) {
        items(
            count = items.itemCount,
            key = items.itemKey { it.stableKey },
            contentType = items.itemContentType { it.contentType }
        ) { index ->
            when (val row = items[index]) {
                is UiItem.SectionHeader -> DateChip(row.title)
                is UiItem.Bubble -> {
                    val compactBelow = computeCompactBelow(items, index)
                    MessageRow(text = row.text, isMine = row.isMine, status = row.status,
                        compactSpacingBelow = compactBelow
                    )
                }
                null -> Box(Modifier.height(28.dp))
            }
        }
    }
}

/** SAFE snapshot-based version to avoid OOB when Paging updates between reads. */
private fun computeCompactBelow(items: LazyPagingItems<UiItem>, index: Int): Boolean {
    val list = items.itemSnapshotList.items
    val curr = list.getOrNull(index) as? UiItem.Bubble ?: return false
    val next = list.getOrNull(index + 1) as? UiItem.Bubble ?: return false
    return sameSenderWithin20s(curr.isMine, curr.sentAt, next.isMine, next.sentAt)
}

private fun sameSenderWithin20s(aMine: Boolean, a: Instant, bMine: Boolean, b: Instant): Boolean {
    if (aMine != bMine) return false
    val delta = Duration.between(a, b).seconds
    return delta in 0 until 20
}
