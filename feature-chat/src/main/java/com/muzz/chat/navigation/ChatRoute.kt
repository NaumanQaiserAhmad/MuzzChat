package com.muzz.chat.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.feature.chat.presentation.ui.ChatScreen
import com.muzz.chat.presentation.ChatViewModel

@Composable
fun ChatRoute(
    partnerName: String,
    vm: ChatViewModel = hiltViewModel()
) {
    ChatScreen(partnerName = partnerName, vm = vm)
}
