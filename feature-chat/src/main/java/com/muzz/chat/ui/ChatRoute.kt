package com.muzz.chat.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.muzz.chat.presentation.ChatViewModel
import com.muzz.chat.presentation.ui.ChatScreen

@Composable
fun ChatRoute(
    partnerName: String,
    vm: ChatViewModel = hiltViewModel()
) {
    ChatScreen(partnerName = partnerName, vm = vm)
}
