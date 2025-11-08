package com.muzz.muzzmatch.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.muzz.chat.ui.ChatRoute // from :feature-chat

private object Routes {
    const val Chat = "chat"
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.Chat) {
        composable(Routes.Chat) { ChatRoute() }
    }
}
