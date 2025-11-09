package com.muzz.chat.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.muzz.chat.presentation.components.MessageInputBar
import com.muzz.chat.model.UiItem
import com.muzz.chat.presentation.ChatViewModel
import com.muzz.chat.presentation.components.ConversationDivider
import com.muzz.chat.presentation.components.DateChip
import com.muzz.chat.presentation.components.MessageRow
import com.muzz.chat.presentation.ui.topbar.ChatTopBar
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import kotlin.math.max

@Composable
fun ChatScreen(
    partnerName: String,
    vm: ChatViewModel
) {
    val items = vm.pagedUi.collectAsLazyPagingItems()
    val uiState by vm.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ChatTopBar(
                partnerName = partnerName,
                onBack = {},
                onMenu = {}
            )

            // List takes remaining height
            ChatList(
                modifier = Modifier.weight(1f),
                items = items,
                listState = listState,
                bottomContentPadding = 96.dp // keeps messages clear of the input when IME is hidden
            )

            BottomInputContainer {
                if (uiState is ChatViewModel.UiState.Error) {
                    Text(
                        text = (uiState as ChatViewModel.UiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                // subtle separator above input
                ConversationDivider()

                MessageInputBar(
                    placeholder = "Type a message…",
                    onSend = { vm.onSend(it) },
                    sendButtonColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Auto-scroll to latest on first load / size changes
    LaunchedEffect(items.itemCount) {
        if (items.itemCount > 0) {
            scope.launch { listState.animateScrollToItem(items.itemCount - 1) }
        }
    }
}

/**
 * Bottom container that **does not** double-count insets.
 * It uses max(IME, navBar) for a single bottom padding, and
 * applies only horizontal nav-bar insets.
 */
@Composable
private fun BottomInputContainer(
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val imeBottomPx = WindowInsets.ime.getBottom(density)
    val navBottomPx = WindowInsets.navigationBars.getBottom(density)
    val bottomDp = with(density) { max(imeBottomPx, navBottomPx).toDp() }

    Surface(
        tonalElevation = 0.dp,
        shadowElevation = 12.dp,
        // square top is fine; if you want rounded:
        // shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier
            .fillMaxWidth()
            // safe horizontally for gesture/nav areas
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)
            )
            // single, controlled bottom padding
            .padding(bottom = bottomDp)
    ) {
        content()
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
                    MessageRow(
                        text = row.text,
                        isMine = row.isMine,
                        status = row.status,
                        compactSpacingBelow = compactBelow
                    )
                }
                null -> Box(Modifier.height(28.dp))
            }
        }
    }
}

/** Snapshot-safe spacing logic between bubbles from same sender within 20s. */
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
